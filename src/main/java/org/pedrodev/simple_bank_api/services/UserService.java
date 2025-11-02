package org.pedrodev.simple_bank_api.services;


import org.pedrodev.simple_bank_api.dtos.*;
import org.pedrodev.simple_bank_api.models.User;
import org.pedrodev.simple_bank_api.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {

        this.userRepository = userRepository;
    }

    @Transactional
    public void registerUser(UserRegisterDTO userRegisterDTO) {

            userRepository.save(new User(userRegisterDTO.nomeCompleto(), userRegisterDTO.cpf(), userRegisterDTO.email(), userRegisterDTO.password(), userRegisterDTO.role()));

    }

    @Transactional(readOnly = true)
    public UserResponseDTO findUserById(Long idUser){

        User user = userRepository.findById(idUser).orElseThrow(() -> new RuntimeException("User not found!"));

        return new UserResponseDTO(user.getNomeCompleto(), user.getEmail());
    }

    @Transactional
    public void updateEmailUser(Long idUser, UserRequestUpdateEmailDTO userEmailUpdateDTO) {

        User user = userRepository.findById(idUser).orElseThrow(() -> new RuntimeException("User not Found"));

        if(user.getSenha().equals(userEmailUpdateDTO.password())){
            user.setEmail(userEmailUpdateDTO.email());
        } else {
            throw new RuntimeException("Informe a senha atual! esta senha esta incorreta!!");
        }

        userRepository.save(user);
    }

    @Transactional
    public void updatePasswordUser(Long idUser, UserRequestUpdatePasswordDTO passwordDTO) {

        User user = userRepository.findById(idUser).orElseThrow(() -> new RuntimeException("User not Found"));

        if(!user.getSenha().equals(passwordDTO.currentPassword())){
            throw new RuntimeException("Informe a senha atual correta!! antes de inserir uma nova senha");
        }

        if (!passwordDTO.newPassword().equals(passwordDTO.confirmNewPassword())){
            throw new RuntimeException("As senhas não são iguais!!");
        }

        if(user.getSenha().equals(passwordDTO.newPassword())){
            throw new RuntimeException("A nova senha não pode ser igual a senha atual!");
        }

        user.setSenha(passwordDTO.newPassword());

        userRepository.save(user);
    }

    // Retorna a lógica do metodo de deletar user quando implementar o spring security
    /*public void deletarUserById(UserDeletionDTO passwordDTO *//*,Long idUser*//*) {

        // User user = userRepository.findById(idUser).orElseThrow(()-> new RuntimeException("User not found"));

        *//*if (!user.getPassword().equals(passwordDTO.password())) {
            throw new RuntimeException("Senha Invalida!");
        }*//*

        // userRepository.deleteById(user.getId());

    }*/
}
