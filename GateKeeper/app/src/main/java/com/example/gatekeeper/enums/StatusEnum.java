package com.example.gatekeeper.enums;

public enum StatusEnum {
    DENTRO(1, "DENTRO"),
    FORA(2, "FORA");

    private final Integer codigo;
    private final String descricao;

    StatusEnum(Integer codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }
}
