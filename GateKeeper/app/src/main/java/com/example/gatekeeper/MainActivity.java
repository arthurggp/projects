package com.example.gatekeeper;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.gatekeeper.models.ConvidadoModel;

import com.example.gatekeeper.utils.ValidationStructure;
import com.example.gatekeeper.utils.Validations;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper databaseHelper;
    TextView datalist;
    AutoCompleteTextView teste;
    TextView datalist_count;
    String FILE_NAME = "convidados.json";
    String CHARSET = "UTF-8";
    Validations validations = new Validations();
    String MESSAGE = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn_scan = findViewById(R.id.btn_scan);
        btn_scan.setOnClickListener(v-> scanCode());

        databaseHelper=new DatabaseHelper(MainActivity.this);
        Button delete=findViewById(R.id.delete_data);
        Button insert=findViewById(R.id.insert_data);
        Button update=findViewById(R.id.update_data);
        Button read=findViewById(R.id.refresh_data);
        datalist=findViewById(R.id.all_data_list);
        datalist_count=findViewById(R.id.data_list_count);

        ParseJSON();

        read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //refreshData();
            }
        });

        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowInputDialog();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUpdateIdDialog();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchByRg();
            }
        });

    }

    //TRANSFORMA JSON STRING EM OBJETO
    public void ParseJSON(){

        try {
            JSONObject jsonObject = new JSONObject(JsonDataFromAssets());
            JSONArray jsonArray= jsonObject.getJSONArray("convidados");

            for(int i=0;i<jsonArray.length();i++) {
                JSONObject data = jsonArray.getJSONObject(i);
                ConvidadoModel convidadoModel = new ConvidadoModel();
                convidadoModel.setCodigo(data.getString("CODIGO"));
                convidadoModel.setConvidado_de(data.getString("CONVIDADO_DE"));
                convidadoModel.setNome(data.getString("NOME"));
                convidadoModel.setRg(data.getString("RG"));
                convidadoModel.setCpf(data.getString("CPF"));
                convidadoModel.setStatus(data.getString("STATUS"));

                databaseHelper.AddConvidado(convidadoModel);
                Log.i("ADD CONVIDADO", "CONVIDADO: "+ convidadoModel.getNome() + " ADICIONADO; ");
            }

        } catch (JSONException e) {
            e.printStackTrace();
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

    //POPULA TABELA
    private void fillDataBase(ConvidadoModel convidadoModel){

    }

    //MOSTRA RESULTADO DA BUSCA
    private void showSearchResults(String mensagem) {
        datalist.setText("");
        datalist.append(mensagem);
            //datalist.append("CODIGO : "+convidadoModel.getCodigo()+" | NOME : "+convidadoModel.getNome()+" | CPF : "+convidadoModel.getCpf()+" | CONVIDADO_DE : "+convidadoModel.getConvidado_de()+" | RG : "+convidadoModel.getRg()+ " | STATUS : "+convidadoModel.getStatus()+" \n\n");
    }

    //ATUALIZA STATUS
    private void updateStatus(ConvidadoModel convidadoModel){
        databaseHelper.updateStatus(convidadoModel);
    }
    //REFRESH
    private void refreshData() {

        List<ConvidadoModel> convidadoModelList=databaseHelper.getAllConvidados();
        datalist.setText("");
        for(ConvidadoModel convidadoModel:convidadoModelList){
            datalist.append("CODIGO : "+convidadoModel.getCodigo()+" | NOME : "+convidadoModel.getNome()+" | CPF : "+convidadoModel.getCpf()+" | CONVIDADO_DE : "+convidadoModel.getConvidado_de()+" | RG : "+convidadoModel.getRg()+ " | STATUS : "+convidadoModel.getStatus()+" \n\n");
        }
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
            //builder.setMessage(result.getContents());
            ConvidadoModel convidadoModel = searchByScannedQrCode(result.getContents().toString());

            //verifica se o convidado existe/ja entrou
            if(validations.convidadoIsValid(convidadoModel).getExists() && !validations.convidadoIsValid(convidadoModel).getIsInside()){
                //atualiza o status
                updateStatus(convidadoModel);
            }

            builder.setMessage(MESSAGE);
            //builder.setMessage(convidadoModel.getNome() + " - " + convidadoModel.getCpf() + " - " + convidadoModel.getRg() + " - " + convidadoModel.getStatus());
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
    private ConvidadoModel searchByScannedQrCode(String code){
        ConvidadoModel convidadoModel = new ConvidadoModel();
        convidadoModel = databaseHelper.getConvidado(Integer.parseInt(code));
        return convidadoModel;
    }

    //BUSCA POR NOME

    //BUSCA POR RG
    private void searchByRg() {
        AlertDialog.Builder al=new AlertDialog.Builder(MainActivity.this);
        View view=getLayoutInflater().inflate(R.layout.search_by_rg_dialog,null);
        al.setView(view);
        final EditText id_input=view.findViewById(R.id.id_input);
        Button delete_btn=view.findViewById(R.id.delete_btn);
        final AlertDialog alertDialog=al.show();

        //MÉTODO DE CLICK DO BOTAO CONFIRMAR
        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConvidadoModel convidadoModel = databaseHelper.getConvidadoByRg(id_input.getText().toString());

                //verifica se convidado existe e se ele entrou
                if(validations.convidadoIsValid(convidadoModel).getExists() && !validations.convidadoIsValid(convidadoModel).getIsInside()){
                    //atualiza o status
                    updateStatus(convidadoModel);
                }

                showSearchResults(validations.convidadoIsValid(convidadoModel).getMessage());
                alertDialog.dismiss();
                //refreshData();

            }
        });
    }

    private void showUpdateIdDialog() {
        AlertDialog.Builder al=new AlertDialog.Builder(MainActivity.this);
        View view=getLayoutInflater().inflate(R.layout.update_id_dialog,null);
        al.setView(view);
        final EditText id_input=view.findViewById(R.id.id_input);
        Button fetch_btn=view.findViewById(R.id.update_id_btn);
        final AlertDialog alertDialog=al.show();
        fetch_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDataDialog(id_input.getText().toString());
                alertDialog.dismiss();
                //refreshData();
            }
        });

    }

    private void showDataDialog(final String codigo) {
        ConvidadoModel convidadoModel=databaseHelper.getConvidado(Integer.parseInt(codigo));
        AlertDialog.Builder al=new AlertDialog.Builder(MainActivity.this);
        View view=getLayoutInflater().inflate(R.layout.update_dialog,null);
        //final EditText id=view.findViewById(R.id.id);
        final EditText nome=view.findViewById(R.id.nome);
        final EditText rg=view.findViewById(R.id.rg);
        final EditText cpf=view.findViewById(R.id.cpf);
        final EditText status=view.findViewById(R.id.status);
        Button update_btn=view.findViewById(R.id.update_btn);
        al.setView(view);

        nome.setText(convidadoModel.getNome());
        rg.setText(convidadoModel.getRg());
        cpf.setText(convidadoModel.getCpf());
        status.setText(convidadoModel.getStatus());

        final AlertDialog alertDialog=al.show();
        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConvidadoModel convidadoModel=new ConvidadoModel();
                convidadoModel.setNome(nome.getText().toString());
                convidadoModel.setCodigo(codigo.toString());
                convidadoModel.setRg(rg.getText().toString());
                convidadoModel.setCpf(cpf.getText().toString());
                convidadoModel.setStatus(status.getText().toString());
                databaseHelper.updateConvidado(convidadoModel);
                alertDialog.dismiss();
                //refreshData();
            }
        });
    }

    private void ShowInputDialog() {
        AlertDialog.Builder al=new AlertDialog.Builder(MainActivity.this);
        View view=getLayoutInflater().inflate(R.layout.insert_dialog,null);

        final EditText id=view.findViewById(R.id.id);
        final EditText nome=view.findViewById(R.id.nome);
        final EditText cpf=view.findViewById(R.id.cpf);
        final EditText rg=view.findViewById(R.id.rg);
        final EditText convidado_de=view.findViewById(R.id.convidado_de);
        final EditText status=view.findViewById(R.id.status);
        Button insertBtn=view.findViewById(R.id.insert_btn);
        al.setView(view);

        final AlertDialog alertDialog=al.show();

        insertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConvidadoModel convidadoModel=new ConvidadoModel();
                convidadoModel.setCodigo(id.getText().toString());
                convidadoModel.setNome(nome.getText().toString());
                convidadoModel.setRg(rg.getText().toString());
                convidadoModel.setCpf(cpf.getText().toString());
                convidadoModel.setConvidado_de(convidado_de.getText().toString());
                convidadoModel.setStatus(status.getText().toString());
                    databaseHelper.AddConvidado(convidadoModel);
                alertDialog.dismiss();
                //refreshData();
            }
        });
    }

}