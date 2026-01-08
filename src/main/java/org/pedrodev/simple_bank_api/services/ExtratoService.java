package org.pedrodev.simple_bank_api.services;

import org.pedrodev.simple_bank_api.dtos.ExtratoBancarioDTO;
import org.pedrodev.simple_bank_api.models.User;
import org.pedrodev.simple_bank_api.repositories.TransactionRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ExtratoService {

    TransactionRepository transactionRepository;

    public ExtratoService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public List<ExtratoBancarioDTO> gerarExtratoBancario(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = (User) authentication.getPrincipal();

        List<ExtratoBancarioDTO> ExtratoBancario = transactionRepository.buscarResumoExtrato(user.getId());

        return ExtratoBancario;
    }
}
