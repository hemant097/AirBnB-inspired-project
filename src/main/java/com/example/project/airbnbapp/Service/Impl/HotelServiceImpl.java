package com.example.project.airbnbapp.Service.Impl;

import com.example.project.airbnbapp.DTOs.HotelDto;
import com.example.project.airbnbapp.Entity.Hotel;
import com.example.project.airbnbapp.Exception.ResourceNotFoundException;
import com.example.project.airbnbapp.Repository.HotelRepository;
import com.example.project.airbnbapp.Service.HotelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepo;
    private final ModelMapper modelMapper;


    @Override
    public HotelDto createNewHotel(HotelDto hotelDto) {
        log.info("creating a new hotel with name: {}",hotelDto.getName());

        Hotel hotelToSave  = modelMapper.map(hotelDto,Hotel.class);
        hotelToSave.setActive(false);

        Hotel savedHotel = hotelRepo.save(hotelToSave);
        log.info("created a new hotel with id: {}",savedHotel.getId());

        return modelMapper.map(savedHotel,HotelDto.class);

    }

    @Override
    public HotelDto getHotelById(Long id) {
        log.info("getting the hotel with id: {}",id);

        Hotel hotel = hotelRepo.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Hotel not found with id:"+id));

        return modelMapper.map(hotel,HotelDto.class);
    }

    @Override
    public HotelDto updateHotelById(Long id, HotelDto hotelDto) {
        log.info("updating the hotel with id: {}",id);

        Hotel hotelToUpdate = returnHotelIfExists(id);

        modelMapper.map(hotelDto, hotelToUpdate);
        hotelToUpdate.setId(id);

        Hotel updatedHotel = hotelRepo.save(hotelToUpdate);

        return modelMapper.map(updatedHotel, HotelDto.class);



    }

    @Override
    public void deleteHotelById(Long id) {

        boolean exists = hotelRepo.existsById(id);
        if(!exists)
            throw new ResourceNotFoundException("No hotel exists with id :" +id);

        hotelRepo.deleteById(id);

        //TODO: delete the future inventories for this hotel
    }


    Hotel returnHotelIfExists(Long id){
        return hotelRepo.findById(id)
                .orElseThrow( () -> new ResourceNotFoundException("No hotel exists with id :" +id));
    }
}
