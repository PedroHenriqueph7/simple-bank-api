package org.pedrodev.simple_bank_api.controllers;

import jakarta.validation.Valid;
import org.pedrodev.simple_bank_api.controllers.docs.UserControllerDocs;
import org.pedrodev.simple_bank_api.dtos.*;
import org.pedrodev.simple_bank_api.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/users")
public class UserController implements UserControllerDocs {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Override
    @GetMapping(value = "/user")
    public UserResponseDTO findUser(Authentication authentication){
        UserResponseDTO userInfo = userService.findUser(authentication);
        return userInfo;
    }

    @Override
    @PatchMapping(value = "/me/email")
    public ResponseEntity<String> updateEmailUser(Authentication authentication,@Valid @RequestBody UserRequestUpdateEmailDTO emailUpdateDTO) {

        userService.updateEmailUser(authentication, emailUpdateDTO);
        return ResponseEntity.ok("User email update successful.");
    }

    @Override
    @PatchMapping(value = "/me/password")
    public ResponseEntity<String> updatePasswordUser(Authentication authentication,@Valid @RequestBody UserRequestUpdatePasswordDTO passwordUpdateDTO) {

        userService.updatePasswordUser(authentication, passwordUpdateDTO);
        return ResponseEntity.ok("User password update successful.");
    }

    @Override
    @PostMapping(value = "/me/confirm-deletion")
    public ResponseEntity deleteUserWithPasswordConfirmation(Authentication authentication,@Valid @RequestBody UserDeletionDTO passwordDTO) {

        userService.deleteUserWithPasswordConfirmation(authentication, passwordDTO);
        return ResponseEntity.noContent().build();
    }

}
