package com.example.gatekeeper.models;

public class ConvidadoModel {
    String id       = "";
    String cpf      = "";
    String rg       = "";
    String nome     = "";
    String status   = "";

    public ConvidadoModel(String id,String cpf,String rg,String nome,String status){
        this.id = id;
        this.cpf = cpf;
        this.rg = rg;
        this.nome = nome;
        this.status = status;
    }

    public ConvidadoModel(){
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getRg() {
        return rg;
    }

    public void setRg(String rg) {
        this.rg = rg;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
