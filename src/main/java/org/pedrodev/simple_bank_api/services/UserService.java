package org.pedrodev.simple_bank_api.services;


import org.pedrodev.simple_bank_api.dtos.*;
import org.pedrodev.simple_bank_api.exceptions.DeactivatedUserException;
import org.pedrodev.simple_bank_api.exceptions.InvalidPasswordException;
import org.pedrodev.simple_bank_api.exceptions.UserNotFoundException;
import org.pedrodev.simple_bank_api.models.User;
import org.pedrodev.simple_bank_api.repositories.UserRepository;
import org.pedrodev.simple_bank_api.repositories.WalletRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;
    private final WalletRepository walletRepository;

    private final WalletService walletService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, WalletRepository walletRepository, WalletService walletService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.walletRepository = walletRepository;
        this.walletService = walletService;
    }


    // info do usuario
    @Transactional(readOnly = true)
    public UserResponseDTO findUser(Authentication authentication) {

        User userLogado = (User) authentication.getPrincipal();
        User user = userRepository.findById(userLogado.getId()).orElseThrow(() -> new UserNotFoundException("User not found!"));

        if (user.isAtivo()) {
            return new UserResponseDTO(user.getNomeCompleto(), user.getEmail());
        } else {
            throw new DeactivatedUserException("deactivated user!");
        }

    }

    @Transactional
    public void updateEmailUser(Authentication authentication, UserRequestUpdateEmailDTO userEmailUpdateDTO) {

        User userLogado = (User) authentication.getPrincipal();
        User user = userRepository.findById(userLogado.getId()).orElseThrow(() -> new UserNotFoundException("User not Found"));

        if (user.isAtivo()) {

            if (passwordEncoder.matches(userEmailUpdateDTO.password(), user.getPassword())) {
                user.setEmail(userEmailUpdateDTO.email());
            } else {
                throw new InvalidPasswordException("Please enter your current password! This password is incorrect!!");
            }

            userRepository.save(user);
        } else {
            throw new DeactivatedUserException("deactivated user!");
        }

    }

    @Transactional
    public void updatePasswordUser(Authentication authentication, UserRequestUpdatePasswordDTO passwordDTO) {

        User userLogado = (User) authentication.getPrincipal();
        User user = userRepository.findById(userLogado.getId()).orElseThrow(() -> new UserNotFoundException("User not Found!"));

        if (user.isAtivo()) {

            if (!passwordEncoder.matches(passwordDTO.currentPassword(), user.getPassword())) {
                throw new InvalidPasswordException("Enter your current correct password before entering a new password!!");
            }

            if (!passwordDTO.newPassword().equals(passwordDTO.confirmNewPassword())) {
                throw new InvalidPasswordException("The passwords are not the same.!!");
            }

            if (passwordEncoder.matches(passwordDTO.newPassword(), user.getPassword())) {
                throw new InvalidPasswordException("The new password cannot be the same as the current password.!");
            }

            var encryptedPassword = new BCryptPasswordEncoder().encode(passwordDTO.newPassword());
            user.setSenha(encryptedPassword);


            userRepository.save(user);

        } else {
            throw new DeactivatedUserException("deactivated user!");
        }

    }


    public void deleteUserWithPasswordConfirmation(Authentication authentication, UserDeletionDTO passwordDTO) {


        User userResponsible = (User) authentication.getPrincipal();
        User user = userRepository.findById(userResponsible.getId()).orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.isAtivo()) {
            if (!passwordEncoder.matches(passwordDTO.password(), user.getPassword())) {
                throw new InvalidPasswordException("Invalid password!");
            }

            user.setAtivo(false);

            // Anonimizando os dados pessoais
            user.setNomeCompleto("Deactivated User");
            user.setCpf("000000000" + user.getId()); // Um valor único, mas inválido e anonimizado
            user.setEmail(user.getId() + "@deleted.com");
            user.setSenha("DELETED_PASSWORD_HASH"); // Previne logins futuros

            // Zera a carteira para tirá-la de qualquer contagem de saldo total
            walletService.updateWalletForDeactivatedUser(user);

            // D. Salva o usuário "desativado" e anonimizado
            userRepository.save(user);

            // E. As Transações e Depósitos? NÃO MEXA NELES.
            // Eles permanecem no banco, apontando para o ID do usuário anonimizado,
            // preservando 100% do histórico financeiro.
        }
    }
}
