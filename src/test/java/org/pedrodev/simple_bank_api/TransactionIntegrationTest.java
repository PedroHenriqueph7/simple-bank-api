package org.pedrodev.simple_bank_api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.pedrodev.simple_bank_api.dtos.TransactionRequestDTO;
import org.pedrodev.simple_bank_api.models.User;
import org.pedrodev.simple_bank_api.models.Wallet;
import org.pedrodev.simple_bank_api.models.enums.UserRole;
import org.pedrodev.simple_bank_api.repositories.UserRepository;
import org.pedrodev.simple_bank_api.repositories.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TransactionIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    WalletRepository walletRepository;

    @Test
    @DisplayName("Deve lancar uma exception de retorno 404 Not Found")
    @Transactional
    void transacaoMalSucedidaRecebedorNaoEncontrado() throws Exception {

        // Cenário
        User pagador = criarUsuario(true,"Pagador Test", "866.867.480-30","pagador111@gmail.com", "56b99c0ab29fd895cfd3deba9086e0ca49d6a3f2bfe65836d8d11aa4fa291184", UserRole.COMUM );
        userRepository.save(pagador);

        Wallet carteiraPagador = new Wallet(new BigDecimal(150), pagador);
        walletRepository.save(carteiraPagador);

        // Simular o Login Manualmente com o Objeto User Real
        // Isso garante que o (User) authentication.getPrincipal() funcione no Service
        UsernamePasswordAuthenticationToken loginFake = new UsernamePasswordAuthenticationToken(
                pagador, // Aqui vai o Principal (Seu objeto User)
                null,
                pagador.getAuthorities() // Se a classe User implementa UserDetails
        );
        SecurityContextHolder.getContext().setAuthentication(loginFake);

        // Ação
        TransactionRequestDTO requisicaoFalha = new TransactionRequestDTO(30L, new BigDecimal(50));

        mockMvc.perform(post("/transactions/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requisicaoFalha)))
                // Verificação
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());;
    }

    @Test
    @DisplayName("Deve lancar exception Bad Request por recebedor nulo")
    @Transactional
    void transacaoMalSucedidaArgumentoRecebedorInvalido() throws Exception {

        // Cenário
        User pagador = criarUsuario(true,"Pagador Test", "866.867.480-30","pagador1111@gmail.com", "56b99c0ab29fd895cfd3deba9086e0ca49d6a3f2bfe65836d8d11aa4fa291184", UserRole.COMUM );
        userRepository.save(pagador);

        Wallet carteiraPagador = new Wallet(new BigDecimal(150), pagador);
        walletRepository.save(carteiraPagador);


        UsernamePasswordAuthenticationToken loginFake = new UsernamePasswordAuthenticationToken(
                pagador,
                null,
                pagador.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(loginFake);

        // Ação
        TransactionRequestDTO requisicaoFalha = new TransactionRequestDTO(null, new BigDecimal(50));

        mockMvc.perform(post("/transactions/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requisicaoFalha)))

                .andExpect(status().isBadRequest()) // $ -> Raiz do Json
                .andExpect(jsonPath("$.recebedor_id").value("O id do recebedor é obrigatório"));
    }

    @Test
    @DisplayName("Deve lancar exception de Bad Request por valor nulo")
    @Transactional
    void transacaoMalSucedidaArgumentoValorInvalido() throws Exception {

        // Cenário
        User pagador = criarUsuario(true,"Pagador Test", "866.867.480-30","pagador11@gmail.com", "56b99c0ab29fd895cfd3deba9086e0ca49d6a3f2bfe65836d8d11aa4fa291184", UserRole.COMUM );
        userRepository.save(pagador);

        Wallet carteiraPagador = new Wallet(new BigDecimal(150), pagador);
        walletRepository.save(carteiraPagador);

        User recebedor = criarUsuario(true,"Recebedor Test", "171.238.340-03","recebedor1@gmail.com", "0906a329060ed475c0f0e9275b133b0051be7cda6d875b4120c067b224c36e1d", UserRole.COMUM );
        userRepository.save(recebedor);

        Wallet carteiraRecebedor = new Wallet(BigDecimal.ZERO, recebedor);
        walletRepository.save(carteiraRecebedor);

        // Simular o Login Manualmente com o Objeto User Real
        // Isso garante que o (User) authentication.getPrincipal() funcione no Service
        UsernamePasswordAuthenticationToken loginFake = new UsernamePasswordAuthenticationToken(
                pagador, // Aqui vai o Principal (Seu objeto User)
                null,
                pagador.getAuthorities() // Se a classe User implementa UserDetails
        );
        SecurityContextHolder.getContext().setAuthentication(loginFake);

        // Ação
        TransactionRequestDTO requisicaoFalha = new TransactionRequestDTO(recebedor.getId(), null);

        mockMvc.perform(post("/transactions/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requisicaoFalha)))
                // Verificacao
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.valor").value("O valor da transação é obrigatório"));

    }

    User criarUsuario(boolean ativo, String nomeCompleto, String cpf, String email, String senha, UserRole role){

        User user = new User();
        user.setAtivo(true);
        user.setNomeCompleto(nomeCompleto);
        user.setCpf(cpf);
        user.setEmail(email);
        user.setSenha(senha);
        user.setRole(role);
        return user;
    }
}


