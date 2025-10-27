package org.pedrodev.simple_bank_api.services;


import org.pedrodev.simple_bank_api.dtos.UserRegisterDTO;
import org.pedrodev.simple_bank_api.dtos.UserRequestUpdateDTO;
import org.pedrodev.simple_bank_api.dtos.UserResponseDTO;
import org.pedrodev.simple_bank_api.models.User;
import org.pedrodev.simple_bank_api.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {

        this.userRepository = userRepository;
    }

    @Transactional
    public void registerUser(UserRegisterDTO userRegisterDTO) {

            userRepository.save(new User(userRegisterDTO.getNomeCompleto(), userRegisterDTO.getCpf(), userRegisterDTO.getEmail(), userRegisterDTO.getPassword(), userRegisterDTO.getRole()));

    }

    @Transactional(readOnly = true)
    public UserResponseDTO findUserById(Long idUser){

        User user = userRepository.findById(idUser).orElseThrow(() -> new RuntimeException("User not found!"));

        UserResponseDTO userResponseDTO = new UserResponseDTO(user.getNomeCompleto(), user.getEmail());

        return userResponseDTO;
    }

    @Transactional
    public void updateUser(Long idUser, UserRequestUpdateDTO userRequestUpdateDTO) {

        User user = userRepository.findById(idUser).orElseThrow(() -> new RuntimeException("User not Found"));

        user.setEmail(userRequestUpdateDTO.getEmail());
        user.setSenha(userRequestUpdateDTO.getPassword());

        userRepository.save(user);
    }

    public void deletarUserById(Long idUser) {

        User user = userRepository.findById(idUser).orElseThrow(()-> new RuntimeException("User not found"));

        userRepository.deleteById(user.getId());

    }
}
