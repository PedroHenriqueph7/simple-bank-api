package org.pedrodev.simple_bank_api.controllers;

import jakarta.validation.Valid;
import org.pedrodev.simple_bank_api.dtos.*;
import org.pedrodev.simple_bank_api.services.UserService;
import org.pedrodev.simple_bank_api.services.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping(value = "/user")
    public UserResponseDTO findUser(Authentication authentication){
        UserResponseDTO userInfo = userService.findUser(authentication);

        return userInfo;
    }

    @PatchMapping(value = "/me/email")
    public ResponseEntity<String> updateEmailUser(@Valid Authentication authentication, @RequestBody UserRequestUpdateEmailDTO emailUpdateDTO) {

        userService.updateEmailUser(authentication, emailUpdateDTO);
        return ResponseEntity.ok("User email update successful.");
    }

    @PatchMapping(value = "/me/password")
    public ResponseEntity<String> updatePasswordUser(@Valid Authentication authentication, @RequestBody UserRequestUpdatePasswordDTO passwordUpdateDTO) {

        userService.updatePasswordUser(authentication, passwordUpdateDTO);
        return ResponseEntity.ok("User password update successful.");
    }

    @PostMapping(value = "/me/confirm-deletion")
    public ResponseEntity deleteUserWithPasswordConfirmation(@Valid @RequestBody UserDeletionDTO passwordDTO,Authentication authentication) {

        userService.deleteUserWithPasswordConfirmation(passwordDTO,authentication);
       return ResponseEntity.noContent().build();
    }

}
