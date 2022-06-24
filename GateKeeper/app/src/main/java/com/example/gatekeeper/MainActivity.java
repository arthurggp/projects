package com.example.gatekeeper;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.gatekeeper.models.ConvidadoModel;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.w3c.dom.Text;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    Button btn_scan;
    Button insert;
    DatabaseHelper databaseHelper;
    TextView datalist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_scan = findViewById(R.id.btn_scan);
        btn_scan.setOnClickListener(v-> scanCode());

        databaseHelper = new DatabaseHelper(MainActivity.this);

        // TA COM PAU AQUI-----RETORNANDO NULL
        final Button insert = findViewById(R.id.btn_insert);
        //-----

        final TextView dataList = findViewById(R.id.btn_getall);

        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowInputDialog();
            }
        });

    }

    //METODO SCAN DO QRCODE
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
            builder.setMessage(result.getContents());
            builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).show();
        }
    });

    // TELA DO INSERT
    private void ShowInputDialog() {
        AlertDialog.Builder al = new AlertDialog.Builder(MainActivity.this);
        View view = getLayoutInflater().inflate(R.layout.insert_dialog, null);
        final EditText nome = view.findViewById(R.id.nome);
        final EditText cpf = view.findViewById(R.id.cpf);
        final EditText id = view.findViewById(R.id.id);
        final EditText rg = view.findViewById(R.id.rg);
        final EditText status = view.findViewById(R.id.status);
        Button insertBtn = view.findViewById(R.id.btn_insert);
        al.setView(view);

        final AlertDialog alertDialog = al.show();

        insertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConvidadoModel convidadoModel = new ConvidadoModel("", "", "", "", "");
                convidadoModel.setNome(nome.getText().toString());
                convidadoModel.setCpf(cpf.getText().toString());
                convidadoModel.setId(id.getText().toString());
                convidadoModel.setStatus(status.getText().toString());
                convidadoModel.setId(id.getText().toString());

                databaseHelper.AddConvidado(convidadoModel);
                alertDialog.dismiss();
            }
        });
    }

}