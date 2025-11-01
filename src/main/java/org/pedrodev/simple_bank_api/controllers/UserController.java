package org.pedrodev.simple_bank_api.controllers;

import jakarta.validation.Valid;
import org.pedrodev.simple_bank_api.dtos.UserRegisterDTO;
import org.pedrodev.simple_bank_api.dtos.UserRequestUpdateEmailDTO;
import org.pedrodev.simple_bank_api.dtos.UserResponseDTO;
import org.pedrodev.simple_bank_api.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody UserRegisterDTO userDTO) {

        userService.registerUser(userDTO);
        return  ResponseEntity.ok("User register with success");
    }

    @PatchMapping(value = "/{idUser}")
    public ResponseEntity<String> updateEmailUser(@Valid @PathVariable Long idUser, @RequestBody UserRequestUpdateEmailDTO userUpdate) {

        userService.updateEmailUser(idUser, userUpdate);
        return ResponseEntity.ok("User update email with success");
    }

    @GetMapping(value = "/{idUser}")
    public UserResponseDTO findUserByID(@PathVariable Long idUser) {

        UserResponseDTO userResponseDTO = userService.findUserById(idUser);
        return userResponseDTO;
    }

    @DeleteMapping(value = "/{idUser}")
    public ResponseEntity deleteUserById(@PathVariable Long idUser) {

        userService.deletarUserById(idUser);
        return ResponseEntity.noContent().build();
    }


}
