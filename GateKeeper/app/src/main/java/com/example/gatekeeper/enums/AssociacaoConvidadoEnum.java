package com.example.gatekeeper.enums;

public enum AssociacaoConvidadoEnum {
    CARECA(1, "Careca"),
    KIARA(2, "Kiara");

    private final Integer codigo;
    private final String descricao;

    AssociacaoConvidadoEnum(Integer codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }
}
