package org.pedrodev.simple_bank_api.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pedrodev.simple_bank_api.dtos.TransactionRequestDTO;
import org.pedrodev.simple_bank_api.exceptions.*;
import org.pedrodev.simple_bank_api.infra.gateways.AuthorizationClient;
import org.pedrodev.simple_bank_api.models.Transaction;
import org.pedrodev.simple_bank_api.models.User;
import org.pedrodev.simple_bank_api.models.Wallet;
import org.pedrodev.simple_bank_api.models.enums.UserRole;
import org.pedrodev.simple_bank_api.repositories.TransactionRepository;
import org.pedrodev.simple_bank_api.repositories.UserRepository;
import org.pedrodev.simple_bank_api.repositories.WalletRepository;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Nested
    class performTransaction {

        @InjectMocks
        private TransactionService transactionService;

        @Mock
        private TransactionRepository transactionRepository;

        @Mock
        private WalletRepository walletRepository;

        @Mock
        private UserRepository userRepository;

        @Mock
        private AuthorizationClient authorizationClient;

        @Captor
        private ArgumentCaptor<Transaction> transactionArgumentCaptor;

        @Test
        @DisplayName("Deve realizar uma transação com sucesso")
        void performTransactionSuccess() {

            // arrange
            Long idRecebedor = 2L;
            BigDecimal valorDaTransacao = BigDecimal.valueOf(500);

            TransactionRequestDTO dadosDTO = new TransactionRequestDTO(idRecebedor, valorDaTransacao);

            UserRole role = UserRole.COMUM;
            User userPagador = new User(true, 1L, "Usuario Pagador", role);

            Authentication authentication = mock(Authentication.class);

            when(authentication.getPrincipal()).thenReturn(userPagador);

            User userRecebedor = new User(true, 2L, "Usuario Recebedor Jr",role);

            when(userRepository.findById(userPagador.getId())).thenReturn(Optional.of(userPagador));
            when(userRepository.findById(userRecebedor.getId())).thenReturn(Optional.of(userRecebedor));

            Wallet walletPagador = new Wallet(BigDecimal.valueOf(2000), userPagador);
            Wallet walletRecebedor = new Wallet(BigDecimal.valueOf(1000), userRecebedor);

            when(walletRepository.findWalletForUpdateByUserId(userPagador.getId())).thenReturn(Optional.of(walletPagador));
            when(walletRepository.findWalletForUpdateByUserId(userRecebedor.getId())).thenReturn(Optional.of(walletRecebedor));


            when(transactionRepository.findValueTransactionsByPagador_id24Hours(eq(userPagador.getId()), any(ZonedDateTime.class))).thenReturn(BigDecimal.ZERO);

            when(authorizationClient.isAuthorized()).thenReturn(true);

            //act
            transactionService.performTransaction(authentication, dadosDTO);


            // assert

            verify(transactionRepository, times(1)).save(transactionArgumentCaptor.capture());

            Transaction transacaoSalvaNoBancoDeDados = transactionArgumentCaptor.getValue();

            assertEquals(userPagador.getId(), transacaoSalvaNoBancoDeDados.getPagador().getId());
            assertEquals(userRecebedor.getId(), transacaoSalvaNoBancoDeDados.getRecebedor().getId());
            assertEquals(dadosDTO.valor(), transacaoSalvaNoBancoDeDados.getValor());

        }

        @Test
        @DisplayName("Deve lançar uma exception de pagador não encontrado!")
        void shouldThrowThePayerNotFoundException() {

            // arrange
            Long idRecebedor = 2L;
            BigDecimal valorDaTransacao = BigDecimal.valueOf(500);

            TransactionRequestDTO dadosDTO = new TransactionRequestDTO(idRecebedor, valorDaTransacao);

            UserRole role = UserRole.COMUM;
            User userPagador = new User(true, 1L, "Usuario Pagador", role);

            Authentication authentication = mock(Authentication.class);

            when(authentication.getPrincipal()).thenReturn(userPagador);

            User userRecebedor = new User(true, 2L, "Usuario Recebedor Jr",role);

            when(userRepository.findById(userPagador.getId())).thenReturn(Optional.empty());

            // act e assert

            assertThrows(UserNotFoundException.class,()-> transactionService.performTransaction(authentication, dadosDTO));
            verify(transactionRepository, never()).save(any(Transaction.class));
        }

        @Test
        @DisplayName("Deve lançar uma exception de recebedor não encontrado!")
        void shouldThrowTheRecipientNotFoundException() {

            // arrange
            Long idRecebedor = 2L;
            BigDecimal valorDaTransacao = BigDecimal.valueOf(500);

            TransactionRequestDTO dadosDTO = new TransactionRequestDTO(idRecebedor, valorDaTransacao);

            UserRole role = UserRole.COMUM;
            User userPagador = new User(true, 1L, "Usuario Pagador", role);

            Authentication authentication = mock(Authentication.class);

            when(authentication.getPrincipal()).thenReturn(userPagador);

            User userRecebedor = new User(true, 2L, "Usuario Recebedor Jr",role);

            when(userRepository.findById(userPagador.getId())).thenReturn(Optional.of(userPagador));
            when(userRepository.findById(userRecebedor.getId())).thenReturn(Optional.empty());

            // act e assert

            assertThrows(UserNotFoundException.class,()-> transactionService.performTransaction(authentication, dadosDTO));
            verify(transactionRepository, never()).save(any(Transaction.class));
        }

        @Test
        @DisplayName("Deve lançar uma exception quando o pagador tentar realizar uma transação para ele mesmo")
        void AnExceptionShouldBeThrownWhenThePayerEqualsTheReceiver() {

            // arrange
            Long idPagador = 1L;
            BigDecimal valorDaTransacao = BigDecimal.valueOf(500);

            TransactionRequestDTO dadosDTO = new TransactionRequestDTO(idPagador, valorDaTransacao);

            UserRole role = UserRole.COMUM;
            User userPagador = new User(true, 1L, "Usuario Pagador", role);

            Authentication authentication = mock(Authentication.class);

            when(authentication.getPrincipal()).thenReturn(userPagador);

            when(userRepository.findById(userPagador.getId())).thenReturn(Optional.of(userPagador));



            // act e assert

            assertThrows(TransactionDeclinedException.class,()-> transactionService.performTransaction(authentication, dadosDTO));
            verify(transactionRepository, never()).save(any(Transaction.class));

        }

        @Test
        @DisplayName("Deve lançar uma exception quando a carteira do pagador não for encontrada!")
        void shouldThrowTheWalletPayerNotFoundException() {

            // arrange
            Long idRecebedor = 2L;
            BigDecimal valorDaTransacao = BigDecimal.valueOf(500);

            TransactionRequestDTO dadosDTO = new TransactionRequestDTO(idRecebedor, valorDaTransacao);

            UserRole role = UserRole.COMUM;
            User userPagador = new User(true, 1L, "Usuario Pagador", role);

            Authentication authentication = mock(Authentication.class);

            when(authentication.getPrincipal()).thenReturn(userPagador);

            User userRecebedor = new User(true, 2L, "Usuario Recebedor Jr",role);

            when(userRepository.findById(userPagador.getId())).thenReturn(Optional.of(userPagador));
            when(userRepository.findById(userRecebedor.getId())).thenReturn(Optional.of(userRecebedor));

            Wallet walletPagador = new Wallet(BigDecimal.valueOf(2000), userPagador);
            Wallet walletRecebedor = new Wallet(BigDecimal.valueOf(1000), userRecebedor);

            when(walletRepository.findWalletForUpdateByUserId(userPagador.getId())).thenReturn(Optional.empty());


            // act e assert

            assertThrows(WalletNotFoundException.class,()-> transactionService.performTransaction(authentication, dadosDTO));
            verify(transactionRepository, never()).save(any(Transaction.class));
        }

        @Test
        @DisplayName("Deve lançar uma exception quando a carteira do recebedor não encontrada!")
        void shouldThrowTheWalletRecipientNotFoundException() {

            // arrange
            Long idRecebedor = 2L;
            BigDecimal valorDaTransacao = BigDecimal.valueOf(500);

            TransactionRequestDTO dadosDTO = new TransactionRequestDTO(idRecebedor, valorDaTransacao);

            UserRole role = UserRole.COMUM;
            User userPagador = new User(true, 1L, "Usuario Pagador", role);

            Authentication authentication = mock(Authentication.class);

            when(authentication.getPrincipal()).thenReturn(userPagador);

            User userRecebedor = new User(true, 2L, "Usuario Recebedor Jr",role);

            when(userRepository.findById(userPagador.getId())).thenReturn(Optional.of(userPagador));
            when(userRepository.findById(userRecebedor.getId())).thenReturn(Optional.of(userRecebedor));

            Wallet walletPagador = new Wallet(BigDecimal.valueOf(2000), userPagador);
            Wallet walletRecebedor = new Wallet(BigDecimal.valueOf(1000), userRecebedor);

            when(walletRepository.findWalletForUpdateByUserId(userPagador.getId())).thenReturn(Optional.of(walletPagador));
            when(walletRepository.findWalletForUpdateByUserId(userRecebedor.getId())).thenReturn(Optional.empty());

            // act e assert

            assertThrows(WalletNotFoundException.class,()-> transactionService.performTransaction(authentication, dadosDTO));
            verify(transactionRepository, never()).save(any(Transaction.class));

        }

        @Test
        @DisplayName("deve lancar uma exception quando o valor da transacao for maior que o limite permitido por transacao!")
        void ItShouldThrowAnExceptionWhenTheTransactionValueExceedsTheAllowedLimit() {

            // arrange
            Long idRecebedor = 2L;
            BigDecimal valorDaTransacao = BigDecimal.valueOf(1100);

            TransactionRequestDTO dadosDTO = new TransactionRequestDTO(idRecebedor, valorDaTransacao);

            UserRole role = UserRole.COMUM;
            User userPagador = new User(true, 1L, "Usuario Pagador", role);

            Authentication authentication = mock(Authentication.class);

            when(authentication.getPrincipal()).thenReturn(userPagador);

            User userRecebedor = new User(true, 2L, "Usuario Recebedor Jr",role);

            when(userRepository.findById(userPagador.getId())).thenReturn(Optional.of(userPagador));
            when(userRepository.findById(userRecebedor.getId())).thenReturn(Optional.of(userRecebedor));

            Wallet walletPagador = new Wallet(BigDecimal.valueOf(2000), userPagador);
            Wallet walletRecebedor = new Wallet(BigDecimal.valueOf(1000), userRecebedor);

            when(walletRepository.findWalletForUpdateByUserId(userPagador.getId())).thenReturn(Optional.of(walletPagador));
            when(walletRepository.findWalletForUpdateByUserId(userRecebedor.getId())).thenReturn(Optional.of(walletRecebedor));

            when(transactionRepository.findValueTransactionsByPagador_id24Hours(eq(userPagador.getId()), any(ZonedDateTime.class))).thenReturn(BigDecimal.ZERO);


            // act e assert

            assertThrows(TransferLimitExceededException.class,()-> transactionService.performTransaction(authentication, dadosDTO));
            verify(transactionRepository, never()).save(any(Transaction.class));

        }

        @Test
        @DisplayName("deve lancar uma exception quando o limite diario ja foi atingido!")
        void ItShouldThrowAnExceptionWhenTheDayValueExceedsTheAllowedLimit() {

            // arrange
            Long idRecebedor = 2L;
            BigDecimal valorDaTransacao = BigDecimal.valueOf(800);

            TransactionRequestDTO dadosDTO = new TransactionRequestDTO(idRecebedor, valorDaTransacao);

            UserRole role = UserRole.COMUM;
            User userPagador = new User(true, 1L, "Usuario Pagador", role);

            Authentication authentication = mock(Authentication.class);

            when(authentication.getPrincipal()).thenReturn(userPagador);

            User userRecebedor = new User(true, 2L, "Usuario Recebedor Jr",role);

            when(userRepository.findById(userPagador.getId())).thenReturn(Optional.of(userPagador));
            when(userRepository.findById(userRecebedor.getId())).thenReturn(Optional.of(userRecebedor));

            Wallet walletPagador = new Wallet(BigDecimal.valueOf(2000), userPagador);
            Wallet walletRecebedor = new Wallet(BigDecimal.valueOf(1000), userRecebedor);

            when(walletRepository.findWalletForUpdateByUserId(userPagador.getId())).thenReturn(Optional.of(walletPagador));
            when(walletRepository.findWalletForUpdateByUserId(userRecebedor.getId())).thenReturn(Optional.of(walletRecebedor));

            when(transactionRepository.findValueTransactionsByPagador_id24Hours(eq(userPagador.getId()), any(ZonedDateTime.class))).thenReturn(BigDecimal.valueOf(5000));


            // act e assert

            assertThrows(DailyLimitExceededException.class,()-> transactionService.performTransaction(authentication, dadosDTO));
            verify(transactionRepository, never()).save(any(Transaction.class));

        }

        @Test
        @DisplayName("deve lancar uma exception quando o saldo disponivel para transacao for menor que o valor solicitado!")
        void ThrownWhenTheAvailableBalanceForTheTransactionIsLessThanTheTransactionAmount() {

            // arrange
            Long idRecebedor = 2L;
            BigDecimal valorDaTransacao = BigDecimal.valueOf(800);

            TransactionRequestDTO dadosDTO = new TransactionRequestDTO(idRecebedor, valorDaTransacao);

            UserRole role = UserRole.COMUM;
            User userPagador = new User(true, 1L, "Usuario Pagador", role);

            Authentication authentication = mock(Authentication.class);

            when(authentication.getPrincipal()).thenReturn(userPagador);

            User userRecebedor = new User(true, 2L, "Usuario Recebedor Jr",role);

            when(userRepository.findById(userPagador.getId())).thenReturn(Optional.of(userPagador));
            when(userRepository.findById(userRecebedor.getId())).thenReturn(Optional.of(userRecebedor));

            Wallet walletPagador = new Wallet(BigDecimal.valueOf(2000), userPagador);
            Wallet walletRecebedor = new Wallet(BigDecimal.valueOf(1000), userRecebedor);

            when(walletRepository.findWalletForUpdateByUserId(userPagador.getId())).thenReturn(Optional.of(walletPagador));
            when(walletRepository.findWalletForUpdateByUserId(userRecebedor.getId())).thenReturn(Optional.of(walletRecebedor));

            when(transactionRepository.findValueTransactionsByPagador_id24Hours(eq(userPagador.getId()), any(ZonedDateTime.class))).thenReturn(BigDecimal.valueOf(4300));


            // act e assert

            assertThrows(AvailableLimitExceededException.class,()-> transactionService.performTransaction(authentication, dadosDTO));
            verify(transactionRepository, never()).save(any(Transaction.class));

        }


        @Test
        @DisplayName("deve lancar exception quando autorização externa falhar")
        void throwExceptionWhenExternalAuthorizationFails() {

            // arrange
            Long idRecebedor = 2L;
            BigDecimal valorDaTransacao = BigDecimal.valueOf(500);

            TransactionRequestDTO dadosDTO = new TransactionRequestDTO(idRecebedor, valorDaTransacao);

            UserRole role = UserRole.COMUM;
            User userPagador = new User(true, 1L, "Usuario Pagador", role);

            Authentication authentication = mock(Authentication.class);

            when(authentication.getPrincipal()).thenReturn(userPagador);

            User userRecebedor = new User(true, 2L, "Usuario Recebedor Jr",role);

            when(userRepository.findById(userPagador.getId())).thenReturn(Optional.of(userPagador));
            when(userRepository.findById(userRecebedor.getId())).thenReturn(Optional.of(userRecebedor));

            Wallet walletPagador = new Wallet(BigDecimal.valueOf(2000), userPagador);
            Wallet walletRecebedor = new Wallet(BigDecimal.valueOf(1000), userRecebedor);

            when(walletRepository.findWalletForUpdateByUserId(userPagador.getId())).thenReturn(Optional.of(walletPagador));
            when(walletRepository.findWalletForUpdateByUserId(userRecebedor.getId())).thenReturn(Optional.of(walletRecebedor));


            when(transactionRepository.findValueTransactionsByPagador_id24Hours(eq(userPagador.getId()), any(ZonedDateTime.class))).thenReturn(BigDecimal.ZERO);

            when(authorizationClient.isAuthorized()).thenReturn(false);

            // act & assert

            assertThrows(TransactionNotAuthorizedException.class, ()-> transactionService.performTransaction(authentication, dadosDTO));
            verify(transactionRepository, never()).save(any(Transaction.class));
        }
    }
}
