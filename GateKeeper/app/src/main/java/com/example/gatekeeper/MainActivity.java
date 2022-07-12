package com.example.gatekeeper;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.gatekeeper.models.ConvidadoModel;

import com.example.gatekeeper.utils.AutoCompleteAdapter;
import com.example.gatekeeper.utils.CustomAdapter;
import com.example.gatekeeper.utils.MessageMaker;
import com.example.gatekeeper.utils.ValidationStructure;
import com.example.gatekeeper.utils.Validations;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper databaseHelper;
    String FILE_NAME = "convidados.json";
    String CHARSET = "UTF-8";
    Validations validations = new Validations();
    String MESSAGE = "";
    MessageMaker messageMaker = new MessageMaker();
    String selected = "";
    List<ConvidadoModel> convidados;
    ListView listview;
    AutoCompleteTextView data_to_search;
    CustomAdapter adapter;
    Button btn_scan;
    Button btn_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        databaseHelper=new DatabaseHelper(MainActivity.this);

        //DECLARA BOTÕES
        btn_scan = findViewById(R.id.btn_search_by_scan);
        btn_scan.setOnClickListener(v-> scanCode());

        btn_search = findViewById(R.id.btn_search);
        btn_search.setOnClickListener(v-> search(selected,data_to_search.getText().toString()));


        //LISTA DE CONVIDADOS
        convidados = new ArrayList<>();
        convidados = databaseHelper.getAllConvidados();

        data_to_search = findViewById(R.id.data_to_search);
        adapter = new CustomAdapter(this,convidados);


        //LISTENNER DOS RADIOBUTTONS
        final RadioGroup group = (RadioGroup) findViewById(R.id.group);
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) group.findViewById(checkedId);
                (MainActivity.this).selected = radioButton.getText().toString();
                data_to_search.setHint("DIGITE O "+ selected);
            }
        });

        //PREENCHE BASE DE DADOS
        ParseJSON();

    }

    //RETONRA ARRAY NOMES
    public List<String> returnArrayWithNames(){
        List<String> array = new ArrayList<>();
        List<ConvidadoModel> convidados = databaseHelper.getAllConvidados();

        for(ConvidadoModel item: convidados){
            array.add(item.getNome());
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            convidados.forEach(item -> {
                array.add(item.getNome());
            });
        }

        return array;
    }

    //RETORNA ARRAY CPF
    public ArrayList<String> returnArrayWithCpf(){
        ArrayList<String> array = new ArrayList<>();
        List<ConvidadoModel> convidados = databaseHelper.getAllConvidadosArray();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            convidados.forEach(item -> {
                array.add(item.getCpf().toString());
            });
        }

        return array;
    }


    //RETORNA ARRAY RG
    public ArrayList<String> returnArrayWithRg(){
        ArrayList<String> array = new ArrayList<>();
        List<ConvidadoModel> convidados = databaseHelper.getAllConvidadosArray();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            convidados.forEach(item -> {
                array.add(item.getRg().toString());
            });
        }

        return array;
    }


    //TRANSFORMA JSON STRING EM OBJETO
    public void ParseJSON(){

        int quantidade = databaseHelper.getTotalCount();

        //VERIFICA SE A BASE JA FOI CARREGADA
        if(quantidade == 0) {

            try {
                JSONObject jsonObject = new JSONObject(JsonDataFromAssets());
                JSONArray jsonArray = jsonObject.getJSONArray("convidados");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject data = jsonArray.getJSONObject(i);
                    ConvidadoModel convidadoModel = new ConvidadoModel();
                    convidadoModel.setCodigo(data.getString("CODIGO"));
                    convidadoModel.setConvidado_de(data.getString("CONVIDADO_DE"));
                    convidadoModel.setNome(data.getString("NOME"));
                    convidadoModel.setRg(data.getString("RG"));
                    convidadoModel.setCpf(data.getString("CPF"));
                    convidadoModel.setStatus(data.getString("STATUS"));

                    databaseHelper.AddConvidado(convidadoModel);
                    Log.i("ADD CONVIDADO", "CONVIDADO: " + convidadoModel.getNome() + " ADICIONADO; ");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    //RETORNA JSON DO ARQUIVO
    private String JsonDataFromAssets(){
        String json = null;
        int sizeofFile = 0;
        byte [] bufferDAta;

        InputStream inputStream = null;

        try {
            inputStream = getAssets().open(FILE_NAME);
            sizeofFile = inputStream.available();
            bufferDAta = new byte[sizeofFile];
            inputStream.read(bufferDAta);
            inputStream.close();
            json = new String(bufferDAta, CHARSET);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return json;
    }


    //ATUALIZA STATUS
    private void updateStatus(ConvidadoModel convidadoModel){
        databaseHelper.updateStatus(convidadoModel);
    }


    //--------------------------------------- MÉTODO SCAN DO QRCODE -----------------------------------------------
    private void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if(result.getContents() != null){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Result");
            ConvidadoModel convidadoModel = searchByScan(result.getContents().toString());

            //verifica se o convidado existe/ja entrou
            if(validations.convidadoIsValid(convidadoModel).getExists() && !validations.convidadoIsValid(convidadoModel).getIsInside()){
                //atualiza o status
                updateStatus(convidadoModel);
            }

            builder.setMessage(MESSAGE);
            builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).show();
        }
    });
    //----------------------------------------------------------------------------------------------------------------

    //BUSCA NA BASE PELO CODIGO DO QRCODE
    private ConvidadoModel searchByScan(String code){
        ConvidadoModel convidadoModel = new ConvidadoModel();
        convidadoModel = databaseHelper.getConvidado(Integer.parseInt(code));
        return convidadoModel;
    }

    private void search(String type, String data){
        switch (type){
            case "NOME":
                searchByName(data);
                break;
            case "CPF":
                searchByCpf(data);
                break;
            case "RG":
                searchByRg(data);
                break;
        }
    }

    //BUSCA POR NOME
    private void searchByName(String name){
        ConvidadoModel convidadoModel = databaseHelper.getConvidadoByName(name);
        ShowConfirmDataDialog(convidadoModel);
    }

    //BUSCA POR CPF
    private void searchByCpf(String cpf) {
        ConvidadoModel convidadoModel = databaseHelper.getConvidadoByCpf(cpf);
        ShowConfirmDataDialog(convidadoModel);
    }

    //BUSCA POR RG
    private void searchByRg(String rg) {
        ConvidadoModel convidadoModel = databaseHelper.getConvidadoByRg(rg);
        ShowConfirmDataDialog(convidadoModel);
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_nome:
                if (checked)
                    data_to_search.setAdapter(adapter);
                break;
            case R.id.radio_cpf:
                if (checked)
                    break;
            case R.id.radio_rg:
                if (checked)
                    break;
        }
    }

    private void ShowConfirmDataDialog(ConvidadoModel convidadoModel) {

        AlertDialog.Builder al = new AlertDialog.Builder(MainActivity.this);
        ValidationStructure validationStructure = validations.convidadoIsValid(convidadoModel);


        //verifica se convidado existe e se ele não entrou
        if (validationStructure.getExists() && !validationStructure.getIsInside()) {

           //AlertDialog.Builder al = new AlertDialog.Builder(MainActivity.this);
            View view = getLayoutInflater().inflate(R.layout.confirmation_dialog, null);

            final EditText nome = view.findViewById(R.id.nome);
            nome.setText(convidadoModel.getNome());

            final EditText cpf = view.findViewById(R.id.cpf);
            cpf.setText(convidadoModel.getCpf());

            final EditText rg = view.findViewById(R.id.rg);
            rg.setText(convidadoModel.getRg());

            final EditText status = view.findViewById(R.id.status);
            status.setText(convidadoModel.getStatus());

            Button confirmBtn = view.findViewById(R.id.confirm_button);
            Button cancelBtn = view.findViewById(R.id.cancel_button);
            al.setView(view);

            final AlertDialog alertDialog = al.show();

            //botao cancela
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });

            //botao confirma
            confirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //atualiza o status
                    updateStatus(convidadoModel);
                    alertDialog.dismiss();

                    //mostra mensagem
                    messageMaker.MessageMaker(view,"Entrada Confirmada!", validationStructure.getMessage());;

                }
            });
        }else{
            al.setTitle("Erro").setMessage(validationStructure.getMessage());
            final AlertDialog alertDialog = al.show();
        }

        }

    }