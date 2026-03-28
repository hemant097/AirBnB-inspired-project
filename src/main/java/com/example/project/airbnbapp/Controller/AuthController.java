package com.example.project.airbnbapp.Controller;

import com.example.project.airbnbapp.DTOs.LoginDto;
import com.example.project.airbnbapp.DTOs.LoginResponseDto;
import com.example.project.airbnbapp.DTOs.SignUpRequestDto;
import com.example.project.airbnbapp.DTOs.UserDto;
import com.example.project.airbnbapp.Security.AuthService;
import com.example.project.airbnbapp.Security.JWTService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/auth")
public class AuthController {

    private final JWTService jwtService;
    private final AuthService authService;


    @PostMapping("/signup")
    @Operation(summary = "Create a new account", tags = {"Auth"})
    public ResponseEntity<UserDto> signUp(@RequestBody SignUpRequestDto signUpRequestDto){
        return new ResponseEntity<>(authService.signUp(signUpRequestDto), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(summary = "Login request", tags = {"Auth"})
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginDto loginDto, HttpServletResponse httpServletResponse){
        String[] tokens = authService.login(loginDto);
//        tokens[0] is accessToken

        Cookie cookie = new Cookie("refreshToken", tokens[1]);
        cookie.setHttpOnly(true);

        httpServletResponse.addCookie(cookie);
        return  ResponseEntity.ok(new LoginResponseDto(tokens[0]));
    }

    @PostMapping("refresh")
    @Operation(summary = "Refresh the JWT with a refresh token", tags = {"Auth"})
    public ResponseEntity<LoginResponseDto> refresh(HttpServletRequest request){
        String refreshToken = Arrays.stream(request.getCookies())
                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow( () -> new AuthenticationServiceException("no refresh token found inside cookies"))
                ;

        String newAccessToken = authService.generateNewAccessToken(refreshToken);

        return  ResponseEntity.ok(new LoginResponseDto(newAccessToken));
    }
}
