package org.pedrodev.simple_bank_api.controllers;

import jakarta.validation.Valid;
import org.pedrodev.simple_bank_api.dtos.LoginDTO;
import org.pedrodev.simple_bank_api.dtos.LoginResponseDTO;
import org.pedrodev.simple_bank_api.dtos.RegisterDTO;
import org.pedrodev.simple_bank_api.services.AuthService;
import org.pedrodev.simple_bank_api.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/auth")
public class AuthenticationController {

    private final AuthService authService;

    public AuthenticationController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping(value = "/login")
    public ResponseEntity login(@Valid @RequestBody LoginDTO loginDTO){

        LoginResponseDTO tokenDTO = authService.loginUser(loginDTO);

        return ResponseEntity.ok(new LoginResponseDTO(tokenDTO.token()));
    }

    @PostMapping(value = "/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterDTO userDTO) {

        authService.registerUser(userDTO);
        return  ResponseEntity.ok("User register with success");

    }

}
