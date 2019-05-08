package com.andrezamoreira.pontos.controllers;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.andrezamoreira.pontos.R;
import com.andrezamoreira.pontos.models.Autenticacao;
import com.andrezamoreira.pontos.models.Date;
import com.andrezamoreira.pontos.models.SpecificUser;
import com.andrezamoreira.pontos.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;


public class DateRangeActivity extends AppCompatActivity {
    // declaração
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;

    // classes
    private static List<SpecificUser> specificUsers;

    public String uid;
    private String usuarioLogadoID;

    // card
    TextView nomeUsuarioLogado;
    TextView emailUsuarioLogado;

    // datas de consulta
    EditText dataInicio;
    EditText dataFinal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_range);

        // instanciando
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        // card
        nomeUsuarioLogado = findViewById(R.id.nomeTextView);
        emailUsuarioLogado = findViewById(R.id.emailTextView);

        // relatorio
        dataInicio = findViewById(R.id.startDateEditText);
        dataFinal = findViewById(R.id.endDateEditText);

        // Recebendo dados do MainActivity
        Intent i = getIntent();

        usuarioLogadoID = i.getStringExtra("usuarioLogadoID");

        // obtendo dados do database
        getUserInfo();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) finish();

    }

    // TODO: obter dados do usuário - database
    public void getUserInfo(){
        uid = mAuth.getCurrentUser().getUid();

        // referência
        DatabaseReference userRef = database.getReference("users/" + uid);
        // obter dados da referência (UID)
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // obter dados e adicionando na classe de Autenticacao

                Autenticacao autenticacao = new Autenticacao();
                autenticacao.setUsuario(dataSnapshot.child("usuario").getValue(String.class));
                autenticacao.setEmail(dataSnapshot.child("email").getValue(String.class));
                autenticacao.setApiKey(dataSnapshot.child("apiKey").getValue(String.class));
                autenticacao.setEmpresaId(dataSnapshot.child("empresaId").getValue(String.class));

                Log.d("Log", "Foi clicado no card para gerar relatório por intervalo de data.");
                Log.d("Log", "Usuário logado: " + autenticacao.getUsuario());
                Log.d("Log", "Email: " + autenticacao.getEmail());
                Log.d("Log", "APIKey: " + autenticacao.getApiKey());
                Log.d("Log", "EmpresaId: " + autenticacao.getEmpresaId());


                Log.d("Log", "Busca usuario foi chamado");


                dadosLogado(autenticacao);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void dadosLogado(Autenticacao autenticacao) {

        // TODO: insere dados do usuário logado no card

        nomeUsuarioLogado.setText(autenticacao.getUsuario());
        emailUsuarioLogado.setText(autenticacao.getEmail());
    }

    public void consultar(View view){
        Date date = new Date();
        // possíveis erros
        if (dataInicio.getText().toString().isEmpty()) {
            dataInicio.setError("Preencha este campo");
            dataInicio.requestFocus();
            return;
        }
        if (dataFinal.getText().toString().isEmpty()){
            dataFinal.setError("Preencha este campo");
            dataFinal.requestFocus();
            return;
        }
        if (dataInicio.getText().toString().length() < 9){
            dataInicio.setError("Formato incorreto");
            dataInicio.requestFocus();
            return;
        }
        if (dataFinal.getText().toString().length() < 9){
            dataFinal.setError("Formato incorreto");
            dataFinal.requestFocus();
            return;
        }

        // todos os dados estiverem válidos
        date.setDateStart(dataInicio.getText().toString());
        date.setDateEnd(dataFinal.getText().toString());
        Log.d("Log", "Usuário inseriu as datas de intervalo desejada\n"
                + "Data Inicial: " + date.getDateStart()
                + "\nData Final:    " + date.getDateEnd());

        // chama a tela do resultado
        Intent i = new Intent(DateRangeActivity.this, ResultEntriesRangeActivity.class);
        // passando valores pra outra tela
        // key e value

        i.putExtra("dataInicio", date.getDateStart());
        i.putExtra("dataFinal", date.getDateEnd());
        i.putExtra("usuarioLogadoID", usuarioLogadoID);
        startActivity(i);
    }
}
