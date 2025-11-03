package org.pedrodev.simple_bank_api.controllers;

import jakarta.validation.Valid;
import org.pedrodev.simple_bank_api.dtos.*;
import org.pedrodev.simple_bank_api.services.UserService;
import org.pedrodev.simple_bank_api.services.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/user")
public class UserController {

    private final UserService userService;

    private  final WalletService walletService;

    public UserController(UserService userService, WalletService walletService) {
        this.userService = userService;
        this.walletService = walletService;
    }

    @PostMapping(value = "/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody UserRegisterDTO userDTO) {

        userService.registerUser(userDTO);
        return  ResponseEntity.ok("User register with success");
    }

    @PatchMapping(value = "/email/{idUser}")
    public ResponseEntity<String> updateEmailUser(@Valid @PathVariable Long idUser, @RequestBody UserRequestUpdateEmailDTO emailUpdateDTO) {

        userService.updateEmailUser(idUser, emailUpdateDTO);
        return ResponseEntity.ok("User email update successful.");
    }

    @PatchMapping(value = "/password/{idUser}")
    public ResponseEntity<String> updatePasswordUser(@Valid @PathVariable Long idUser, @RequestBody UserRequestUpdatePasswordDTO passwordUpdateDTO) {

        userService.updatePasswordUser(idUser, passwordUpdateDTO);
        return ResponseEntity.ok("User password update successful.");
    }

    @GetMapping(value = "/{idUser}")
    public UserResponseDTO findUserByID(@PathVariable Long idUser) {

        UserResponseDTO userResponseDTO = userService.findUserById(idUser);
        return userResponseDTO;
    }

    /*@PostMapping(value = "/me/confirm-deletion")
    public ResponseEntity deleteUserWithPasswordConfirmation(@Valid @RequestBody UserDeletionDTO passwordDTO *//*,Autentication autentication*//*) {

        //Long idUser = autentication.getId();

        userService.deleteUserWithPasswordConfirmation(*//*idUser,*//*passwordDTO);
        return ResponseEntity.noContent().build();
    }*/


}
