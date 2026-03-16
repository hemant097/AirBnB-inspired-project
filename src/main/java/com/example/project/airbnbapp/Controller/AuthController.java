package com.example.project.airbnbapp.Controller;

import com.example.project.airbnbapp.DTOs.LoginDto;
import com.example.project.airbnbapp.DTOs.LoginResponseDto;
import com.example.project.airbnbapp.DTOs.SignUpRequestDto;
import com.example.project.airbnbapp.DTOs.UserDto;
import com.example.project.airbnbapp.Security.AuthService;
import com.example.project.airbnbapp.Security.JWTService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/auth")
public class AuthController {

    private final JWTService jwtService;
    private final AuthService authService;


    @PostMapping("/signup")
    public ResponseEntity<UserDto> signUp(@RequestBody SignUpRequestDto signUpRequestDto){
        return new ResponseEntity<>(authService.signUp(signUpRequestDto), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginDto loginDto, HttpServletResponse httpServletResponse){
        String[] tokens = authService.login(loginDto);

        Cookie cookie = new Cookie("refreshToken", tokens[1]);
        cookie.setHttpOnly(true);

        httpServletResponse.addCookie(cookie);
        return  ResponseEntity.ok(new LoginResponseDto(tokens[1]));
    }
}
