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
import java.util.List;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper databaseHelper;
    TextView datalist;
    TextView datalist_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn_scan = findViewById(R.id.btn_scan);
        btn_scan.setOnClickListener(v-> scanCode());

        databaseHelper=new DatabaseHelper(MainActivity.this);
        Button delete=findViewById(R.id.delete_btn);
        Button insert=findViewById(R.id.insert_data);
        Button update=findViewById(R.id.update_data);
        Button read=findViewById(R.id.refresh_data);
        datalist=findViewById(R.id.all_data_list);
        datalist_count=findViewById(R.id.data_list_count);

        read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshData();

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

        /*delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteDialog();
            }
        });*/

    }

    //REFRESH
    private void refreshData() {

        List<ConvidadoModel> convidadoModelList=databaseHelper.getAllConvidados();
        datalist.setText("");
        for(ConvidadoModel convidadoModel:convidadoModelList){
            datalist.append("ID : "+convidadoModel.getId()+" | Name : "+convidadoModel.getNome()+" | Email : "+convidadoModel.getCpf()+" | DOB : "+convidadoModel.getRg()+ " | PHONE : "+convidadoModel.getStatus()+" \n\n");
        }
    }


    //--------------------------------------- METODO SCAN DO QRCODE -----------------------------------------------
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
    //----------------------------------------------------------------------------------------------------------------

    private void showDeleteDialog() {
        AlertDialog.Builder al=new AlertDialog.Builder(MainActivity.this);
        View view=getLayoutInflater().inflate(R.layout.delete_dialog,null);
        al.setView(view);
        final EditText id_input=view.findViewById(R.id.id_input);
        Button delete_btn=view.findViewById(R.id.delete_btn);
        final AlertDialog alertDialog=al.show();

        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseHelper.deleteConvidado(id_input.getText().toString());
                alertDialog.dismiss();
                refreshData();

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
                refreshData();
            }
        });

    }

    private void showDataDialog(final String id) {
        ConvidadoModel convidadoModel=databaseHelper.getConvidado(Integer.parseInt(id));
        AlertDialog.Builder al=new AlertDialog.Builder(MainActivity.this);
        View view=getLayoutInflater().inflate(R.layout.update_dialog,null);
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
                convidadoModel.setId(id);
                convidadoModel.setRg(rg.getText().toString());
                convidadoModel.setCpf(cpf.getText().toString());
                convidadoModel.setStatus(status.getText().toString());
                databaseHelper.updateConvidado(convidadoModel);
                alertDialog.dismiss();
                refreshData();
            }
        });
    }

    private void ShowInputDialog() {
        AlertDialog.Builder al=new AlertDialog.Builder(MainActivity.this);
        View view=getLayoutInflater().inflate(R.layout.insert_dialog,null);
        final EditText nome=view.findViewById(R.id.nome);
        final EditText rg=view.findViewById(R.id.rg);
        final EditText cpf=view.findViewById(R.id.cpf);
        final EditText status=view.findViewById(R.id.status);
        Button insertBtn=view.findViewById(R.id.insert_btn);
        al.setView(view);

        final AlertDialog alertDialog=al.show();

        insertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConvidadoModel convidadoModel=new ConvidadoModel();
                convidadoModel.setNome(nome.getText().toString());
                convidadoModel.setRg(rg.getText().toString());
                convidadoModel.setCpf(cpf.getText().toString());
                convidadoModel.setStatus(status.getText().toString());
                databaseHelper.AddConvidado(convidadoModel);
                alertDialog.dismiss();
                refreshData();
            }
        });
    }
}