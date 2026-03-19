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
import com.example.project.airbnbapp.Service.CheckoutService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;

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
    private final CheckoutService checkoutService;

    @Value("${front-end.url}")
    private String frontEndUrl;

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


        //TODO: calculate dynamic price

        Booking booking = Booking.builder()
                .bookingStatus(BookingStatus.RESERVED)
                .hotel(hotel)
                .room(room)
                .checkInDate(checkInDate)
                .checkOutDate(checkOutDate)
                .user(returnCurrentUser())
                .roomsCount(roomsRequired)
                .amount(BigDecimal.valueOf(1800))
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

        BookingDto bookingDto = new BookingDto();
        bookingDto.setGuests(new HashSet<>());

        for(GuestDto guestDto: guestDtoList){
            Guest guest = modelMapper.map(guestDto,Guest.class);
            guest.setUser(currentUser);
            guest = guestRepo.save(guest);
            booking.getGuests().add(guest);
            bookingDto.getGuests().add(guestDto);
        }

        booking.setBookingStatus(BookingStatus.GUESTS_ADDED);

        Booking bookingWithGuests = bookingRepo.save(booking);

        modelMapper.map(bookingWithGuests, bookingDto);

        bookingDto.setUserDto(modelMapper.map(currentUser, UserDto.class));

        System.out.println(bookingDto);
        return bookingDto;

    }

    @Override
    public String initiatePayment(Long bookingId) {

        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("no booking found with id "+bookingId));

        User currentUser = returnCurrentUser();

        if(!currentUser.equals(booking.getUser()))
            throw new UnauthorizedException("Booking does not belong to this user with id:"+currentUser.getId());

        if(  hasBookingExpired(booking.getCreatedAt()) )
            throw new IllegalStateException("Booking has expired, create new booking");

        String sessionUrl = checkoutService.getCheckOutSession(booking, frontEndUrl+"payments/success",frontEndUrl+"payments/failure");

        booking.setBookingStatus(BookingStatus.PAYMENT_PENDING);
        bookingRepo.save(booking);
        return sessionUrl;
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

    //checks if the booking created time plus 10 minutes were before than the current time,
    // if yes booking is expired, and exception is thrown
     boolean hasBookingExpired(LocalDateTime bct){
        return bct.plusMinutes(10).isBefore(LocalDateTime.now());
    }

    //TODO: remove dummy user
    User returnCurrentUser(){
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
