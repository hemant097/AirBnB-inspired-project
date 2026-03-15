package com.example.project.airbnbapp.Service.Impl;

import com.example.project.airbnbapp.Entity.User;
import com.example.project.airbnbapp.Exception.ResourceNotFoundException;
import com.example.project.airbnbapp.Repository.UserRepository;
import com.example.project.airbnbapp.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;

    @Override
    public User getUserById(Long id) {
        return userRepo.findById(id)
                .orElseThrow( () -> new ResourceNotFoundException("no user found with id "+id));
    }
}
