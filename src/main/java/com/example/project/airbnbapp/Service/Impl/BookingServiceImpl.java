package com.example.project.airbnbapp.Service.Impl;

import com.example.project.airbnbapp.DTOs.*;
import com.example.project.airbnbapp.Entity.*;
import com.example.project.airbnbapp.Entity.enums.BookingStatus;
import com.example.project.airbnbapp.Exception.ResourceNotFoundException;
import com.example.project.airbnbapp.Exception.UnauthorizedException;
import com.example.project.airbnbapp.Repository.*;
import com.example.project.airbnbapp.Service.BookingService;
import com.example.project.airbnbapp.Service.HotelService;
import com.example.project.airbnbapp.Service.RoomService;
import com.example.project.airbnbapp.Strategy.PricingService;
import com.example.project.airbnbapp.util.AppUtil;
import com.stripe.exception.StripeException;
import com.stripe.model.Refund;
import com.stripe.model.checkout.Session;
import com.stripe.param.RefundCreateParams;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.project.airbnbapp.Entity.enums.BookingStatus.*;
import static com.example.project.airbnbapp.util.AppUtil.returnCurrentUser;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final InventoryRepository inventoryRepo;
    private final BookingRepository bookingRepo;
    private final GuestRepository guestRepo;
    private final ModelMapper modelMapper;
    private final PricingService pricingService;
    private final HotelService hotelService;
    private final RoomService roomService;


    @Override
    @Transactional
    public BookingDto initializeBooking(BookingRequest bookingRequest) {
        Long hotelId = bookingRequest.getHotelId();
        Long roomId = bookingRequest.getRoomId();
        LocalDate checkInDate = bookingRequest.getCheckInDate();
        LocalDate checkOutDate = bookingRequest.getCheckOutDate();
        Integer roomsRequired = bookingRequest.getRoomsCount();

        log.info("initializing booking for hotelID: {}, room: {}, from {} to {}",
                hotelId, roomId, checkInDate, checkOutDate);

        Hotel hotel = hotelService.returnHotelIfExists(hotelId);
        Room room = roomService.returnRoomIfExists(roomId);

        List<Inventory> inventoryList = inventoryRepo.
                findAndLockAvailableInventory(roomId, checkInDate, checkOutDate, roomsRequired);

        long dateCount = ChronoUnit.DAYS.between(checkInDate, checkOutDate) +1;

        if(inventoryList.size()!=dateCount){
            throw new IllegalStateException("Room is not available");
        }

        //Reserve the room / update the bookedCount of inventories

//        for(Inventory inv : inventoryList){
//                inv.setReservedCount(inv.getReservedCount()+ roomsRequired);}
//        inventoryRepo.saveAll(inventoryList);

        //the above logic can be done using this custom modifying query in repository
            inventoryRepo.initBooking(roomId, checkInDate, checkOutDate, roomsRequired);
        log.info("inventory updated for this booking request, hotelID: {}, room: {}, from {} to {}",
                hotelId, roomId, checkInDate, checkOutDate);


        //calculate dynamic price
        BigDecimal priceForOneRoom = pricingService.calculateTotalPrice(inventoryList);
        BigDecimal totalPrice = priceForOneRoom.multiply(BigDecimal.valueOf(roomsRequired));

        Booking booking = Booking.builder()
                .bookingStatus(BookingStatus.RESERVED)
                .hotel(hotel)
                .room(room)
                .checkInDate(checkInDate)
                .checkOutDate(checkOutDate)
                .user(returnCurrentUser())
                .roomsCount(roomsRequired)
                .amount(totalPrice)
                .build();

        booking = bookingRepo.save(booking);
        BookingDto bookingDto =  modelMapper.map(booking, BookingDto.class);
        bookingDto.setUser(modelMapper.map(returnCurrentUser(), UserDto.class));

        log.info("booking has been created for: {}, room: {}, from {} to {}",
                booking.getHotel().getName(), booking.getRoom().getType(), checkInDate, checkOutDate);
        return bookingDto;
    }

    @Override
    @Transactional
    public BookingDto addGuests(Long bookingId, List<GuestDto> guestDtoList) {
        log.info("Trying to add {} guest(s) in the booking with id:{}", guestDtoList.size(), bookingId);

        Booking booking = returnBookingIfExists(bookingId);
        User currentUser = returnCurrentUser();
        checkWhetherBookingOwnerIsMatching(currentUser, booking.getUser());

        if( hasBookingExpired(booking.getCreatedAt()) )
            throw new IllegalStateException("Booking has expired, create new booking");
        if(booking.getBookingStatus() != BookingStatus.RESERVED)
            throw new IllegalStateException("Booking is not under reserved state, cannot add guests");

        log.info("Booking has not expired, adding guests now");

        BookingDto bookingDto = new BookingDto(); // as we need to return a dto
        bookingDto.setGuests(new HashSet<>());

        for(GuestDto guestDto: guestDtoList){
            Guest guest = modelMapper.map(guestDto,Guest.class);
            guest.setUser(currentUser);
            guest = guestRepo.save(guest);
            booking.getGuests().add(guest);
            bookingDto.getGuests().add(guestDto);
        }

        booking.setBookingStatus(GUESTS_ADDED);

        Booking bookingWithGuests = bookingRepo.save(booking);

        modelMapper.map(bookingWithGuests, bookingDto);

        bookingDto.setUser(modelMapper.map(currentUser, UserDto.class));
        log.info("Added {} guest in booking ID:{}", guestDtoList.size(), bookingId);
        return bookingDto;

    }

    @Override
    @Transactional
    public void cancelBookingWithId(Long bookingId) {

        Booking booking = returnBookingIfExists(bookingId);
        User currentUser = returnCurrentUser();
        checkWhetherBookingOwnerIsMatching(currentUser, booking.getUser());

        if(booking.getBookingStatus()!=CONFIRMED)
            throw new IllegalStateException("Only CONFIRMED bookings can be cancelled");

        booking.setBookingStatus(CANCELLED);
        bookingRepo.save(booking);

        Long roomId = booking.getRoom().getId();
        LocalDate checkInDate = booking.getCheckInDate();
        LocalDate checkOutDate = booking.getCheckOutDate();
        Integer roomCount = booking.getRoomsCount();

        log.info("cancelling booking in {}, room:{}, from {} to {}, {} rooms",
                booking.getHotel().getName(), booking.getRoom().getType(), checkInDate, checkOutDate, roomCount);

        inventoryRepo.lockReservedInventory(roomId, checkInDate, checkOutDate, roomCount);
        inventoryRepo.updateInventoryWhenBookingCancelled(roomId, checkInDate, checkOutDate, roomCount);

        //handle the refund
        try {
            Session session = Session.retrieve(booking.getPaymentSessionId());
            log.info("Session object retrieved successfully");
            RefundCreateParams refundParams = RefundCreateParams.builder()
                    .setPaymentIntent(session.getPaymentIntent())
                    .putAllMetadata(Map.of("booking-id",String.valueOf(bookingId),
                            "cancellation-charges", String.valueOf(500.00)))
                    .setReason(RefundCreateParams.Reason.REQUESTED_BY_CUSTOMER)
                    .setAmount(session.getAmountTotal()-(500*100))
                    .build();

            //this will call the stripe API internally and process the refund
            Refund.create(refundParams);
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
        log.info("booking cancelled for bookingID:{}", bookingId);

    }

    @Override
    public List<BookingDto> getAllBookingsOfAHotel(Long hotelId){

        Hotel hotel = hotelService.returnHotelIfExists(hotelId);
        User currentUser = AppUtil.returnCurrentUser();
        log.info("Getting all booking for the hotel with ID:{}", hotelId);
        checkWhetherHotelOwnerIsMatching(currentUser, hotel.getOwner(), hotelId);

        List<Booking> bookings = bookingRepo.findBookingByHotelId(hotelId);

        return bookings.stream()
                .map((element) -> modelMapper.map(element, BookingDto.class))
                .toList();

    }

    @Override
    public HotelReportDto getHotelReport(Long hotelId, LocalDate startDate, LocalDate endDate) {

        Hotel hotel = hotelService.returnHotelIfExists(hotelId);
        log.info("Generating report for hotel with id:{}", hotelId);
        checkWhetherHotelOwnerIsMatching(returnCurrentUser(), hotel.getOwner(), hotel.getId());

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        List<Booking> confirmedBookings = bookingRepo.findBookingsByHotelAndStatusAndCreatedAtBetween(
                hotel, startDateTime, endDateTime, CONFIRMED);

        long totalConfirmedBookings =  confirmedBookings.size();

        BigDecimal totalRevenueOfConfirmedBookings = confirmedBookings.stream()
                .map(Booking::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal averageRevenue = totalRevenueOfConfirmedBookings.divide(
                BigDecimal.valueOf(totalConfirmedBookings), RoundingMode.HALF_UP);

        log.info("Successfully generated report for hotel with id:{}", hotelId);
        return new HotelReportDto(totalConfirmedBookings, totalRevenueOfConfirmedBookings, averageRevenue);

    }

    @Override
    public String getBookingStatus(Long bookingId) {

        Booking booking = returnBookingIfExists(bookingId);
        checkWhetherBookingOwnerIsMatching(returnCurrentUser(), booking.getUser());
        return booking.getBookingStatus().name();
    }

   @Override
    public Booking returnBookingIfExists(Long bookingId){
        return bookingRepo.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("no booking found with id "+bookingId));
    }

    //checks if the booking created time plus 10 minutes is before the current time,
     boolean hasBookingExpired(LocalDateTime bct){
        return bct.plusMinutes(10).isBefore(LocalDateTime.now());
    }

    //if the current user in security context is NOT the owner of the booking,then throws an exception, checks by
    //comparing the user IDs of both users passed
    void checkWhetherBookingOwnerIsMatching(User currentUser, User bookingUser){
        if(!currentUser.equals(bookingUser))
            throw new UnauthorizedException("Booking does not belong to this user with id:"+currentUser.getId());
    }

    //if the current user in security context is NOT the owner of the hotel,then throws an exception, checks by
    //comparing the user IDs of both users passed
    void checkWhetherHotelOwnerIsMatching(User currentUser, User hotelOwner, Long hotelID){
        if(!currentUser.equals(hotelOwner))
            throw new AccessDeniedException("You are not the owner of this hotel with ID: "+hotelID);
    }
}
