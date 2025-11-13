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

    public UserController(UserService userService) {
        this.userService = userService;
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

    /*@PostMapping(value = "/me/confirm-deletion")
    public ResponseEntity deleteUserWithPasswordConfirmation(@Valid @RequestBody UserDeletionDTO passwordDTO *//*,Autentication autentication*//*) {

        //Long idUser = autentication.getId();

        userService.deleteUserWithPasswordConfirmation(*//*idUser,*//*passwordDTO);
        return ResponseEntity.noContent().build();
    }*/

}
