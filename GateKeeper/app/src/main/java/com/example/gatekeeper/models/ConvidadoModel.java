package com.example.gatekeeper.models;

public class ConvidadoModel {
    String codigo   = "";
    String nome     = "";
    String cpf      = "";
    String rg       = "";
    String status   = "";
    String convidado_de = "";

    public ConvidadoModel(String codigo, String nome,String cpf,String rg,String status, String convidado_de){
        this.codigo = codigo;
        this.nome = nome;
        this.cpf = cpf;
        this.rg = rg;
        this.status = status;
        this.convidado_de = convidado_de;
    }

    public ConvidadoModel(){
    }

    public String getCodigo() {
        return codigo;
    }
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
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

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getConvidado_de() {
        return convidado_de;
    }
    public void setConvidado_de(String convidado_de) {
        this.convidado_de = convidado_de;
    }

    public String toString() {
        return "Convidado{" +
                "codigo='" + codigo + '\'' +
                "nome='" + nome + '\'' +
                ", rg=" + rg +'\'' +
                ", cpf=" + cpf + '\'' +
                ", status=" + status +'\'' +
                ", convidado_de=" + convidado_de +
                '}';
    }
}
