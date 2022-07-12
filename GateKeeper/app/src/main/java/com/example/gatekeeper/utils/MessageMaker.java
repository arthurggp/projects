package com.example.gatekeeper.utils;

import android.view.View;

import androidx.appcompat.app.AlertDialog;

public class MessageMaker {

    public void MessageMaker( View view, String titulo, String mensagem){
        new AlertDialog.Builder(view.getContext()).setTitle(titulo).setMessage(mensagem).show();
    }

}
