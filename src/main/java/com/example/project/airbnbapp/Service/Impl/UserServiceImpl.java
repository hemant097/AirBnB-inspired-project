package com.example.project.airbnbapp.Service.Impl;

import com.example.project.airbnbapp.DTOs.UserDto;
import com.example.project.airbnbapp.DTOs.UserProfileUpdateRequestDto;
import com.example.project.airbnbapp.Entity.User;
import com.example.project.airbnbapp.Exception.ResourceNotFoundException;
import com.example.project.airbnbapp.Repository.UserRepository;
import com.example.project.airbnbapp.Service.UserService;
import com.example.project.airbnbapp.util.AppUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepo;
    private final ModelMapper modelMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("searching user by email inside loadUserByUsername with userName:{}",username);
        return userRepo.findByEmail(username)
                .orElseThrow(() -> new BadCredentialsException("user with email "+username+" not found"));
    }

    @Override
    public User getUserById(Long id) {
        log.info("searching user by id inside with userId:{}",id);
        return userRepo.findById(id)
                .orElseThrow( () -> new ResourceNotFoundException("no user found with id "+id));
    }

    @Override
    public UserDto getMyProfile(){
        User user = AppUtil.returnCurrentUser();
        log.info("Getting the profile for user with id:{}", user.getId());
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public void updateProfile(UserProfileUpdateRequestDto updateRequestDto) {

        User userToUpdate = AppUtil.returnCurrentUser();
        log.info("Updating the profile of user with id:{}", userToUpdate.getId());

        if(updateRequestDto.getName()!=null) userToUpdate.setName(updateRequestDto.getName());
        if(updateRequestDto.getGender()!=null) userToUpdate.setGender(updateRequestDto.getGender());
        if(updateRequestDto.getDateOfBirth()!=null) userToUpdate.setDateOfBirth(updateRequestDto.getDateOfBirth());

        userRepo.save(userToUpdate);
    }


}
