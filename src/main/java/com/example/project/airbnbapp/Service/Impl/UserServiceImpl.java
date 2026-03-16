package com.example.project.airbnbapp.Service.Impl;

import com.example.project.airbnbapp.Entity.User;
import com.example.project.airbnbapp.Exception.ResourceNotFoundException;
import com.example.project.airbnbapp.Repository.UserRepository;
import com.example.project.airbnbapp.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByEmail(username)
                .orElseThrow(() -> new BadCredentialsException("user with email "+username+" not found"));
    }



    @Override
    public User getUserById(Long id) {
        return userRepo.findById(id)
                .orElseThrow( () -> new ResourceNotFoundException("no user found with id "+id));
    }


}
