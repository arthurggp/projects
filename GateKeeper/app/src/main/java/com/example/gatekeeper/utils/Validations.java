package com.example.gatekeeper.utils;

import com.example.gatekeeper.models.ConvidadoModel;

public class Validations {

    public ValidationStructure convidadoIsValid(ConvidadoModel convidadoModel) {
        ValidationStructure validationStructure = new ValidationStructure();

        boolean isInside = false;
        Boolean exists = false;

        String message = "";
        isInside = convidadoModel.getStatus() == "DENTRO";
        exists = convidadoModel.getNome() != "";

        //VERIFICA SE O CONVIDADO EXISTE
        if (exists) {
            validationStructure.setExists(true);
            //VERIFICA SE O CONVIDADO JA ENTROU
            if (isInside) {
                validationStructure.setisInside(true);
                validationStructure.setMessage("O CONVIDADO " + convidadoModel.getNome() + " JÁ ENTROU !! ");
            } else {
                validationStructure.setisInside(false);
                validationStructure.setMessage("BEM VINDO " + convidadoModel.getNome() + " !!  ");
            }
        } else {
            validationStructure.setExists(false);
            validationStructure.setMessage("CONVIDADO NÃO ESTÁ NA LISTA !!");
        }
        return validationStructure;
    }
}
