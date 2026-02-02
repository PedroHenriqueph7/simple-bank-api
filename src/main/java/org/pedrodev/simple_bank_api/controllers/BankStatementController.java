package org.pedrodev.simple_bank_api.controllers;

import org.pedrodev.simple_bank_api.dtos.ExtratoBancarioDTO;
import org.pedrodev.simple_bank_api.services.ExtratoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/extrato")
public class BankStatementController {

    private final ExtratoService extratoService;

    public BankStatementController(ExtratoService extratoService) {
        this.extratoService = extratoService;
    }

    @GetMapping
    public List<ExtratoBancarioDTO> exibirExtratoBancario() {

        List<ExtratoBancarioDTO> extratoBancario = extratoService.gerarExtratoBancario();

        return extratoBancario;
    }
}
