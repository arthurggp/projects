package com.example.gatekeeper.DAO;

import com.example.gatekeeper.enums.FiltrosPesquisaEnum;
import com.example.gatekeeper.models.ConvidadoModel;

import java.util.List;

public interface ConvidadoDao<T> {

    // ADICIONAR CONVIDADO
    void adicionarConvidado(ConvidadoModel convidado);

    // ATUALIZAR CONVIDADO
    Integer atualizarConvidado(ConvidadoModel convidadoModel);

    // DELETAR CONVIDADO
    void deletarConvidado(String id);

    // BUSCA LISTA DE TODOS OS CONVIDADOS
    List<ConvidadoModel> buscarTodosConvidados();

    // CONTAGEM DE CONVIDADOS
    Integer buscarNumeroTotalConvidados();

    // BUSCA POR FILTRO
    ConvidadoModel buscarConvidadoFiltro(FiltrosPesquisaEnum colunaFiltro);
}
