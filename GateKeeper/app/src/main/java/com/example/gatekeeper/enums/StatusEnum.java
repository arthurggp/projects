package com.example.gatekeeper.enums;

public enum StatusEnum {
    DENTRO(1, "Dentro"),
    FORA(2, "Fora");

    private final Integer codigo;
    private final String descricao;

    StatusEnum(Integer codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }
}
