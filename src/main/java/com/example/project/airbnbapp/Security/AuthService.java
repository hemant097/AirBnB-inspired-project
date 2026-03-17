package com.example.project.airbnbapp.Security;

import com.example.project.airbnbapp.DTOs.LoginDto;
import com.example.project.airbnbapp.DTOs.SignUpRequestDto;
import com.example.project.airbnbapp.DTOs.UserDto;
import com.example.project.airbnbapp.Entity.User;
import com.example.project.airbnbapp.Entity.enums.Role;
import com.example.project.airbnbapp.Exception.ResourceNotFoundException;
import com.example.project.airbnbapp.Repository.UserRepository;
import io.jsonwebtoken.security.Password;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepo;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;

    public UserDto signUp(SignUpRequestDto signUpRequestDto) {

        Optional<User> optUser = userRepo.findByEmail(signUpRequestDto.getEmail());

        if (optUser.isPresent()) throw new RuntimeException("user is already present with same email id");

        User newUser = User.builder()
                .roles(Set.of(Role.GUEST))
                .name(signUpRequestDto.getName())
                .email(signUpRequestDto.getEmail())
                .password(passwordEncoder.encode(signUpRequestDto.getPassword()))
                .build();
        User savedUser = userRepo.save(newUser);

        return modelMapper.map(savedUser, UserDto.class);

    }

    public String[] login(LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
        );
        User user = (User) authentication.getPrincipal();

        String[] arr = new String[2];

        arr[0] = jwtService.generateAccessToken(user);
        arr[1] = jwtService.generateRefreshToken(user);

        return arr;
    }

    public String generateNewAccessToken(String refreshToken){
        Long userId = jwtService.getUserIdFromToken(refreshToken);

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("no user found with this id"+userId));

        return jwtService.generateAccessToken(user);


    }
}

