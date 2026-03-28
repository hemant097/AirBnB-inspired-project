package com.example.project.airbnbapp.Service;

import com.example.project.airbnbapp.DTOs.UserDto;
import com.example.project.airbnbapp.DTOs.UserProfileUpdateRequestDto;
import com.example.project.airbnbapp.Entity.User;
public interface UserService {


    User getUserById(Long id);

    void updateProfile(UserProfileUpdateRequestDto dto);

    UserDto getMyProfile();
}
