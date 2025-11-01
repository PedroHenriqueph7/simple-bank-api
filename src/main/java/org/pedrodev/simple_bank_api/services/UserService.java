package org.pedrodev.simple_bank_api.services;


import org.pedrodev.simple_bank_api.dtos.UserRegisterDTO;
import org.pedrodev.simple_bank_api.dtos.UserRequestUpdateEmailDTO;
import org.pedrodev.simple_bank_api.dtos.UserResponseDTO;
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
    public void updateEmailUser(Long idUser, UserRequestUpdateEmailDTO userRequestUpdateDTO) {

        User user = userRepository.findById(idUser).orElseThrow(() -> new RuntimeException("User not Found"));

        if(user.getSenha().equals(userRequestUpdateDTO.password())){
            user.setEmail(userRequestUpdateDTO.email());
        } else {
            throw new RuntimeException("Informe a senha atual! esta senha esta incorreta!!");
        }

        userRepository.save(user);
    }


    public void deletarUserById(Long idUser) {

        User user = userRepository.findById(idUser).orElseThrow(()-> new RuntimeException("User not found"));

        userRepository.deleteById(user.getId());

    }
}
