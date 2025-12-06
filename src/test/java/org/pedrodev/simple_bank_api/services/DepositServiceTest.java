package org.pedrodev.simple_bank_api.services;

import org.assertj.core.api.ZonedDateTimeAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pedrodev.simple_bank_api.dtos.SolicitDepositDTO;
import org.pedrodev.simple_bank_api.dtos.SolicitDepositResponseDTO;
import org.pedrodev.simple_bank_api.dtos.WebHookPaymentDTO;
import org.pedrodev.simple_bank_api.models.Deposit;
import org.pedrodev.simple_bank_api.models.User;
import org.pedrodev.simple_bank_api.models.Wallet;
import org.pedrodev.simple_bank_api.models.enums.Status;
import org.pedrodev.simple_bank_api.models.enums.UserRole;
import org.pedrodev.simple_bank_api.repositories.DepositRepository;
import org.pedrodev.simple_bank_api.repositories.UserRepository;
import org.pedrodev.simple_bank_api.repositories.WalletRepository;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class DepositServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private DepositRepository depositRepository;

    @InjectMocks
    private DepositService depositService;

    @Captor
    private ArgumentCaptor<Deposit> depositArgumentCaptor;

    @Nested
    class solicitdeposit {

        @Test
        @DisplayName("deve solicitar o deposito com sucesso")
        void solicitDepositWithSuccess() {

            //arrange
            UserRole userRole = UserRole.COMUM;
            User user = new User(true, 1L, "Phtest", userRole);
            BigDecimal valorSolicitadoDeposito = BigDecimal.valueOf(500);


            Authentication authentication = mock(Authentication.class);
            SolicitDepositDTO solicitDepositDTO = new SolicitDepositDTO(valorSolicitadoDeposito);

            when(authentication.getPrincipal()).thenReturn(user);


            //act
            SolicitDepositResponseDTO outputsDepositDTO = depositService.solicitDeposit(authentication, solicitDepositDTO);

            //assert

            verify(depositRepository, times(1)).save(depositArgumentCaptor.capture());

            Deposit depositCreated = depositArgumentCaptor.getValue();

            ZonedDateTime dateNow = ZonedDateTime.now();
            ZonedDateTime dataehoraLimiteExpiracao = ZonedDateTime.now().plusMinutes(31);


            assertNotNull(depositCreated.getPixId());
            assertEquals(user, depositCreated.getUser());
            assertEquals(valorSolicitadoDeposito, depositCreated.getValor());
            assertEquals(Status.PENDENTE, depositCreated.getStatusAtual());
            assertTrue(depositCreated.getDataExpiracao().isAfter(dateNow));
            assertTrue(depositCreated.getDataExpiracao().isBefore(dataehoraLimiteExpiracao));

            assertEquals(depositCreated.getPixId(), outputsDepositDTO.pixCode());
            assertEquals(outputsDepositDTO.valor(), depositCreated.getValor());
            assertTrue(outputsDepositDTO.dataExpiracao().isAfter(dateNow));
            assertTrue(outputsDepositDTO.dataExpiracao().isBefore(dataehoraLimiteExpiracao));


        }
    }

    @Nested
    class confirmPayment {

    }

        @Test
        @DisplayName("deve confirmar o pagamento do deposito com sucesoo")
        void confirmPayment() {

            //arrange
            String pixCode = "2aiwn239xnn92n93xol21lam29l4cd";
            WebHookPaymentDTO webHookPaymentDTO = new WebHookPaymentDTO(pixCode);

            UserRole userRole = UserRole.COMUM;
            User user = new User(true, 1L, "Phtest", userRole);
            BigDecimal valorSolicitadoDeposito = BigDecimal.valueOf(500);
            ZonedDateTime dataExpiracao = ZonedDateTime.now().plusMinutes(30);

            Deposit depositSalvo = new Deposit(1L, user, valorSolicitadoDeposito, Status.PENDENTE, pixCode,dataExpiracao);

            when(depositRepository.findDepositByPixId(pixCode)).thenReturn(Optional.of(depositSalvo));

            BigDecimal saldo = BigDecimal.ZERO;
            Wallet wallet = new Wallet(saldo, user);

            when(walletRepository.findByUserId(wallet.getUser().getId())).thenReturn(Optional.of(wallet));

            //act
            depositService.confirmPayment(webHookPaymentDTO);
            // assert

            assertEquals(wallet.getSaldo(), depositSalvo.getValor());
            assertEquals(Status.CONCLUIDO, depositSalvo.getStatusAtual());

        }
}