package com.example.gatekeeper.enums;

public enum FiltrosPesquisaEnum {
    CODIGO("ID"),
    CPF("CPF"),
    RG("RG"),
    NOME("NOME");
    
    private final String nomeColuna;

    FiltrosPesquisaEnum(String nomeColuna) {
        this.nomeColuna = nomeColuna;
    }
}
