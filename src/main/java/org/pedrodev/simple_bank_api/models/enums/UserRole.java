package org.pedrodev.simple_bank_api.models.enums;

import lombok.Getter;
import org.pedrodev.simple_bank_api.services.PoliticaTaxa;
import org.pedrodev.simple_bank_api.services.PoliticaTaxaComum;
import org.pedrodev.simple_bank_api.services.PoliticaTaxaLojista;


public enum UserRole {

    COMUM("ROLE_COMUM", new PoliticaTaxaComum()),
    LOJISTA("ROLE_LOJISTA", new PoliticaTaxaLojista());

    private final String roleName;
    private final PoliticaTaxa politicaTaxa;

    UserRole(String roleName, PoliticaTaxa politicaTaxa) {
        this.roleName = roleName;
        this.politicaTaxa = politicaTaxa;
    }

    public String getRoleName() {
        return roleName;
    }

    public PoliticaTaxa getPoliticaTaxa() {
        return politicaTaxa;
    }

}
