package org.pedrodev.simple_bank_api.services;


import jakarta.validation.Valid;
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


    // info do usuario
    @Transactional(readOnly = true)
    public UserResponseDTO findUserById(Long idUser){

        User user = userRepository.findById(idUser).orElseThrow(() -> new RuntimeException("User not found!"));

        if(user.isAtivo()){
            return new UserResponseDTO(user.getNomeCompleto(), user.getEmail());
        } else {
            throw  new RuntimeException("Usuario desativado!");
        }

    }

    @Transactional
    public void updateEmailUser(Long idUser, UserRequestUpdateEmailDTO userEmailUpdateDTO) {

        User user = userRepository.findById(idUser).orElseThrow(() -> new RuntimeException("User not Found"));

        if (user.isAtivo()){

            if(user.getSenha().equals(userEmailUpdateDTO.password())){
                user.setEmail(userEmailUpdateDTO.email());
            } else {
                throw new RuntimeException("Informe a senha atual! esta senha esta incorreta!!");
            }

            userRepository.save(user);
        } else {
            throw  new RuntimeException("Usuario desativado!");
        }

    }

    @Transactional
    public void updatePasswordUser(Long idUser, UserRequestUpdatePasswordDTO passwordDTO) {

        User user = userRepository.findById(idUser).orElseThrow(() -> new RuntimeException("User not Found"));

        if(user.isAtivo()) {

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

        } else {
            throw  new RuntimeException("Usuario desativado!");
        }

    }

    // Retorna a lógica do metodo de deletar user quando implementar o spring security
    // Deletar em cascata a wallet, transaction, deposit
    /*public void deleteUserWithPasswordConfirmation(UserDeletionDTO passwordDTO *//*,Long idUser*//*) {


        // User user = userRepository.findById(idUser).orElseThrow(()-> new RuntimeException("User not found"));

        *//*if (!user.getPassword().equals(passwordDTO.password())) {
            throw new RuntimeException("Senha Invalida!");
        }*//*

        user.setAtivo(false)

        // B. (Opcional, mas recomendado para LGPD/GDPR) Anonimiza os dados pessoais
        user.setNomeCompleto("Usuário Desativado");
        user.setCpf("000000000" + user.getId()); // Um valor único, mas inválido e anonimizado
        user.setEmail(user.getId() + "@deleted.com");
        user.setSenha("DELETED_PASSWORD_HASH"); // Previne logins futuros

        // C. (Opcional) Zera a carteira para tirá-la de qualquer contagem de saldo total
        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Carteira não encontrada"));
        wallet.setSaldo(BigDecimal.ZERO);
        walletRepository.save(wallet);

        // D. Salva o usuário "desativado" e anonimizado
        userRepository.save(user);

        // E. As Transações e Depósitos? NÃO MEXA NELES.
        // Eles permanecem no banco, apontando para o ID do usuário anonimizado,
        // preservando 100% do histórico financeiro.
    }*/
}
