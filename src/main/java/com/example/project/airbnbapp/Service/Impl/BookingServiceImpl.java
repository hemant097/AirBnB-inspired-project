package com.example.project.airbnbapp.Service.Impl;

import com.example.project.airbnbapp.DTOs.BookingDto;
import com.example.project.airbnbapp.DTOs.BookingRequest;
import com.example.project.airbnbapp.DTOs.GuestDto;
import com.example.project.airbnbapp.DTOs.UserDto;
import com.example.project.airbnbapp.Entity.*;
import com.example.project.airbnbapp.Entity.enums.BookingStatus;
import com.example.project.airbnbapp.Exception.ResourceNotFoundException;
import com.example.project.airbnbapp.Exception.UnauthorizedException;
import com.example.project.airbnbapp.Repository.*;
import com.example.project.airbnbapp.Service.BookingService;
import com.example.project.airbnbapp.Strategy.PricingService;
import com.stripe.exception.StripeException;
import com.stripe.model.Refund;
import com.stripe.model.checkout.Session;
import com.stripe.param.RefundCreateParams;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static com.example.project.airbnbapp.Entity.enums.BookingStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final InventoryRepository inventoryRepo;
    private final BookingRepository bookingRepo;
    private final HotelRepository hotelRepo;
    private final RoomRepository roomRepo;
    private final GuestRepository guestRepo;
    private final ModelMapper modelMapper;
    private final PricingService pricingService;


    @Override
    @Transactional
    public BookingDto initializeBooking(BookingRequest bookingRequest) {
        Long hotelId = bookingRequest.getHotelId();
        Long roomId = bookingRequest.getRoomId();
        LocalDate checkInDate = bookingRequest.getCheckInDate();
        LocalDate checkOutDate = bookingRequest.getCheckOutDate();
        Integer roomsRequired = bookingRequest.getRoomsCount();

        log.info("initializing booking for hotel: {}, room: {}, from {} to {}", hotelId, roomId, checkInDate, checkOutDate);

        Hotel hotel = returnHotelIfExists(hotelId);
        Room room = returnRoomIfExists(roomId);

        List<Inventory> inventoryList = inventoryRepo.
                findAndLockAvailableInventory(roomId, checkInDate, checkOutDate, roomsRequired);

        long dateCount = ChronoUnit.DAYS.between(checkInDate, checkOutDate) +1;

        if(inventoryList.size()!=dateCount){
            throw new IllegalStateException("Room is not available");
        }

        //Reserve the room / update the bookedCount of inventories
        for(Inventory inv : inventoryList){
            inv.setReservedCount(inv.getReservedCount()+ roomsRequired);}

        inventoryRepo.saveAll(inventoryList);


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
        bookingDto.setUserDto(modelMapper.map(returnCurrentUser(), UserDto.class));
        return bookingDto;
    }

    @Override
    @Transactional
    public BookingDto addGuests(Long bookingId, List<GuestDto> guestDtoList) {
        log.info("Adding {} guest in the booking with id:{}", guestDtoList.size(), bookingId);

        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("no booking found with id "+bookingId));

        User currentUser = returnCurrentUser();
        if(!currentUser.equals(booking.getUser()))
            throw new UnauthorizedException("Booking does not belong to this user with id:"+currentUser.getId());

        boolean isBookingExpired = hasBookingExpired(booking.getCreatedAt());

        if(isBookingExpired)
            throw new IllegalStateException("Booking has expired, create new booking");
        if(booking.getBookingStatus() != BookingStatus.RESERVED)
            throw new IllegalStateException("Booking is not under reserved state, cannot add guests");

        log.info("Booking has not expired, and is in RESERVED status");

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

        bookingDto.setUserDto(modelMapper.map(currentUser, UserDto.class));
        log.info("Added {} guest in booking ID:{}", guestDtoList.size(), bookingId);
        return bookingDto;

    }

    @Override
    @Transactional
    public void cancelBookingWithId(Long bookingId) {

        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("no booking found with id "+bookingId));

        User currentUser = returnCurrentUser();
        if(!currentUser.equals(booking.getUser()))
            throw new UnauthorizedException("Booking does not belong to this user with id:"+currentUser.getId());
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


    }

    //Returns the hotel for an Id, else throws a ResourceNotFoundException
    Hotel returnHotelIfExists(Long id){
        return hotelRepo.findById(id)
                .orElseThrow( () -> new ResourceNotFoundException("No hotel exists with id :" +id));
    }

    //Returns the Room for an Id, else throws a ResourceNotFoundException
    Room returnRoomIfExists(Long roomId){
        return roomRepo.findById(roomId)
                .orElseThrow( () -> new ResourceNotFoundException("No room exists with id :"+roomId));
    }

    //checks if the booking created time plus 10 minutes is before the current time,
    // if yes booking is expired, and exception is thrown
     boolean hasBookingExpired(LocalDateTime bct){
        return bct.plusMinutes(10).isBefore(LocalDateTime.now());
    }

    User returnCurrentUser(){
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
