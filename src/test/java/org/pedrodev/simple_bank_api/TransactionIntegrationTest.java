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
    @DisplayName("Deve realizar a transação bem sucedida!")
    @Transactional
    void transacaoSucesso() throws Exception {

        // Cenário
        User pagador = criarUsuario(true,"Pagador Test", "982.870.980-50","pagador@gmail.com", "56b99c0ab29fd895cfd3deba9086e0ca49d6a3f2bfe65836d8d11aa4fa291184", UserRole.COMUM );

        // User antes do salvamento sem Id, depois de executado a linha abaixo ele recebe o seu id do banco de dados, O PostgreSQL retorna para o hibernate o id gerado no registro, e por meio do Reflection esse valor é injetado no Objeto pagador
        userRepository.save(pagador);

        Wallet carteiraPagador = new Wallet(new BigDecimal(150), pagador);
        walletRepository.save(carteiraPagador);

        User recebedor = criarUsuario(true,"Recebedor Test", "171.238.340-03","recebedor@gmail.com", "0906a329060ed475c0f0e9275b133b0051be7cda6d875b4120c067b224c36e1d", UserRole.COMUM );
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
        TransactionRequestDTO requisicaoSucesso = new TransactionRequestDTO(recebedor.getId(), new BigDecimal(50));

        mockMvc.perform(post("/transactions/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requisicaoSucesso)))
                // Verificação
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve lancar uma exception de retorno 404 Not Found")
    @Transactional
    void transacaoMalSucedidaRecebedorNaoEncontrado() throws Exception {

        // Cenário
        User pagador = criarUsuario(true,"Pagador Test", "982.870.980-50","pagador@gmail.com", "56b99c0ab29fd895cfd3deba9086e0ca49d6a3f2bfe65836d8d11aa4fa291184", UserRole.COMUM );
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
        User pagador = criarUsuario(true,"Pagador Test", "982.870.980-50","pagador@gmail.com", "56b99c0ab29fd895cfd3deba9086e0ca49d6a3f2bfe65836d8d11aa4fa291184", UserRole.COMUM );
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
        User pagador = criarUsuario(true,"Pagador Test", "982.870.980-50","pagador@gmail.com", "56b99c0ab29fd895cfd3deba9086e0ca49d6a3f2bfe65836d8d11aa4fa291184", UserRole.COMUM );
        userRepository.save(pagador);

        Wallet carteiraPagador = new Wallet(new BigDecimal(150), pagador);
        walletRepository.save(carteiraPagador);

        User recebedor = criarUsuario(true,"Recebedor Test", "171.238.340-03","recebedor@gmail.com", "0906a329060ed475c0f0e9275b133b0051be7cda6d875b4120c067b224c36e1d", UserRole.COMUM );
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


    @Test
    @DisplayName("Teste de concorrencia nas transacoes entre usuarios")
    void testarConcorrenciaNasTransacoes() throws Exception {

        // Cenário
        User pagador = criarUsuario(true,"Pagador Test", "982.870.980-50","pagador@gmail.com", "56b99c0ab29fd895cfd3deba9086e0ca49d6a3f2bfe65836d8d11aa4fa291184", UserRole.COMUM);
        userRepository.save(pagador);

        Wallet carteiraPagador = new Wallet(new BigDecimal(150), pagador);
        walletRepository.save(carteiraPagador);

        User recebedor = criarUsuario(true,"Recebedor Test", "171.238.340-03","recebedor@gmail.com", "0906a329060ed475c0f0e9275b133b0051be7cda6d875b4120c067b224c36e1d", UserRole.COMUM);
        userRepository.save(recebedor);

        Wallet carteiraRecebedor = new Wallet(BigDecimal.ZERO, recebedor);
        walletRepository.save(carteiraRecebedor);


        UsernamePasswordAuthenticationToken loginFake = new UsernamePasswordAuthenticationToken(
                pagador, null, pagador.getAuthorities()
        );
        // Configura o contexto na Thread MAIN (para referência)
        SecurityContext contextMain = SecurityContextHolder.createEmptyContext();
        contextMain.setAuthentication(loginFake);

        // Acao
        TransactionRequestDTO requisicao = new TransactionRequestDTO(recebedor.getId(), new BigDecimal(100));
        String jsonBody = objectMapper.writeValueAsString(requisicao);

        // THREAD 1
        CompletableFuture<Integer> requisicao1 = CompletableFuture.supplyAsync(() -> {
            try {
                // Injetar o contexto de segurança DENTRO da nova thread
                SecurityContextHolder.setContext(contextMain);

                return mockMvc.perform(post("/transactions/transaction")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody))
                        .andReturn().getResponse().getStatus();
            } catch (Exception e) {
                e.printStackTrace();
                return 500;
            }
        });

        // THREAD 2
        CompletableFuture<Integer> requisicao2 = CompletableFuture.supplyAsync(() -> {
            try {

                SecurityContextHolder.setContext(contextMain);

                return mockMvc.perform(post("/transactions/transaction")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody))
                        .andReturn().getResponse().getStatus();
            } catch (Exception e) {
                e.printStackTrace();
                return 500;
            }
        });

        CompletableFuture.allOf(requisicao1, requisicao2).join();

        int status1 = requisicao1.get();
        int status2 = requisicao2.get();

        System.out.println("Status 1: " + status1);
        System.out.println("Status 2: " + status2);

        long sucessos = java.util.stream.Stream.of(status1, status2)
                .filter(s -> s == 200)
                .count();

        // Verificação
        assertEquals(1, sucessos, "Erro de Concorrência! Deveria ter 1 sucesso, mas teve: " + sucessos);
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


