package org.pedrodev.simple_bank_api.services;

import org.pedrodev.simple_bank_api.dtos.LoginDTO;
import org.pedrodev.simple_bank_api.dtos.LoginResponseDTO;
import org.pedrodev.simple_bank_api.dtos.RegisterDTO;
import org.pedrodev.simple_bank_api.exceptions.TheUserAlreadyHasAnAccountException;
import org.pedrodev.simple_bank_api.infra.security.TokenService;
import org.pedrodev.simple_bank_api.models.User;
import org.pedrodev.simple_bank_api.repositories.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;

    private final WalletService walletService;

    private AuthenticationManager authenticationManager;

    private TokenService tokenService;

    public AuthService(UserRepository userRepository, WalletService walletService, AuthenticationManager authenticationManager, TokenService tokenService) {
        this.userRepository = userRepository;
        this.walletService = walletService;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }


    @Transactional
    public void registerUser(RegisterDTO userRegisterDTO) {

        if (this.userRepository.findByCpf(userRegisterDTO.cpf()) != null)  throw new TheUserAlreadyHasAnAccountException("The user already has an account.");

        var encryptedPassword = new BCryptPasswordEncoder().encode(userRegisterDTO.password());

        User user = new User(userRegisterDTO.nomeCompleto(), userRegisterDTO.cpf(), userRegisterDTO.email(), encryptedPassword, userRegisterDTO.role());
        userRepository.save(user);

        walletService.automaticallyAddWalletToUserRegistration(user);

    }

    public LoginResponseDTO loginUser(LoginDTO loginDTO){
        var usernamePassword = new UsernamePasswordAuthenticationToken(loginDTO.cpf(), loginDTO.password());

        var auth =  this.authenticationManager.authenticate(usernamePassword);

        String token = tokenService.generatedToken((User) auth.getPrincipal());
        return new LoginResponseDTO(token);
    }


}
