package com.andrezamoreira.pontos.controllers;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.andrezamoreira.pontos.R;

import com.andrezamoreira.pontos.models.Autenticacao;
import com.andrezamoreira.pontos.models.SpecificUser;
import com.andrezamoreira.pontos.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    // declaração
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;

    public String uid;
    private String apiKey;
    private String status;
    private String mensagem;
    private int useradmin;
    private String usuarioLogadoID;

    // classes
    private static List<User> users;
    private static List<SpecificUser> specificUsers;

    // card view
    private TextView nomeUsuario;
    private TextView emailUsuario;
    private TextView statusUsuario;
    private TextView tipoUsuario;
    private TextView mensagemApp;

    // resultado da API
    private TextView textViewResult;
    private CardView usuarioEspecificoCardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // instanciando
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        textViewResult = findViewById(R.id.resultTextView);
        usuarioEspecificoCardView = findViewById(R.id.authUserCardView);
        usuarioEspecificoCardView.setVisibility(View.INVISIBLE);
        mensagemApp = findViewById(R.id.msgtextView);
        mensagemApp.setVisibility(View.INVISIBLE);

        nomeUsuario = findViewById(R.id.nomeTextView);
        emailUsuario = findViewById(R.id.emailTextView);
        statusUsuario = findViewById(R.id.statusTextView);
        tipoUsuario = findViewById(R.id.tipoTextView);

        // TODO: requisicão a API para validar se o usuário existe no clickify
        String valueAPIKey = "XJzUp/FcmBU8oozz"; // APIKey do Gilmar

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(
                        chain -> {
                            Request request = chain.request().newBuilder()
                                    .addHeader("x-api-key", valueAPIKey)
                                    .addHeader("Content-Type", "application/json").build();
                            return chain.proceed(request);
                        }).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.clockify.me/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();

        JSONPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JSONPlaceHolderApi.class);

        Call<List<User>> call = jsonPlaceHolderApi.getUser();

        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if(!response.isSuccessful()){
                    textViewResult.setVisibility(View.VISIBLE);
                    textViewResult.setText("Code " + response.code());
                    usuarioEspecificoCardView.setVisibility(View.INVISIBLE);
                    mensagemApp.setVisibility(View.INVISIBLE);
                    return;
                }

                users = response.body();

                textViewResult.setVisibility(View.VISIBLE);
                textViewResult.setText("Carregando...");
                usuarioEspecificoCardView.setVisibility(View.INVISIBLE);
                mensagemApp.setVisibility(View.INVISIBLE);

                // obtendo dados do database
                getUserInfo();
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                textViewResult.setVisibility(View.VISIBLE);
                textViewResult.setText(t.getMessage());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) finish();

        Log.d("Log", "Usuário logado com sucesso");
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        Log.d("Log", "Recarregando informações");
        getUserInfo();
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

                Log.d("Log", "Usuário logado: " + autenticacao.getUsuario());
                Log.d("Log", "Email: " + autenticacao.getEmail());
                Log.d("Log", "APIKey: " + autenticacao.getApiKey());
                Log.d("Log", "EmpresaId: " + autenticacao.getEmpresaId());


                Log.d("Log", "Busca usuario foi chamado");

                buscaUsuario(autenticacao);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // TODO: Busca se usuário logado existe no Clockify
    public void buscaUsuario(Autenticacao autenticacao){
        // email do usuário logado
        String emailAutenticacao = autenticacao.getEmail();
        Log.d("Log", "Recebeu no login o email: " + emailAutenticacao);

        // Dados do usuário logado
        User userTemp = new User();
        for (User user : users){
            // verifica se o usuario logado existe no clockify
            if (user.getEmail().equals(emailAutenticacao)){
                // recupera dados do usuário para tratar como específico
                // atribui seu objeto a userTemp
                userTemp = user;
                Toast.makeText(this, "Usuário reconhecido no Clockify", Toast.LENGTH_SHORT).show();
                usuarioLogadoID = userTemp.getID();
                Log.i("Log", "Dados Recebidos do Clockify\nNome do Usuário: "+ userTemp.getName() + " \nID do Usuário: " + userTemp.getID() );
            } else {
                //Toast.makeText(this, "Usuário não foi reconhecido no Clockify", Toast.LENGTH_SHORT).show();
                Log.i("Log", "Dados Recebidos do Clockify\nNome do Usuário: "+ userTemp.getName() + " \nID do Usuário: " + userTemp.getID() );
            }
        }

        // TODO: API de Usuário Específico
        String APIKeyAutenticacao = autenticacao.getApiKey();
        String idUsuario = userTemp.getID();

        // TODO: requisicão a API
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(
                        chain -> {
                            Request request = chain.request().newBuilder()
                                    .addHeader("x-api-key", APIKeyAutenticacao).build();
                            return chain.proceed(request);
                        }).build();

        String API_BASE_URL = "https://api.clockify.me/api/" ;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();

        JSONPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JSONPlaceHolderApi.class);

        Call<SpecificUser> call = jsonPlaceHolderApi.getSpecificUser(idUsuario);

        call.enqueue(new Callback<SpecificUser>() {
            @Override
            public void onResponse(Call<SpecificUser> call, Response<SpecificUser> response) {
                Log.d("Log", "Iniciando a requisição dos usuários específicos");

                if(!response.isSuccessful()){
                    usuarioEspecificoCardView.setVisibility(View.INVISIBLE);
                    mensagemApp.setVisibility(View.INVISIBLE);
                    Log.e("Log", "Code: " + response.code());
                    if (response.code() == 500) {
                        mensagem = "Ocorreu um erro. \nSua APIKey pode está incorreta ou o servidor do Clockify está indisponível. \nTente novamente mais tarde. \nCódigo do Erro: " + response.code();
                    }
                    else {
                        mensagem = "Ocorreu um erro. \nTente novamente mais tarde. \nCódigo do Erro: \n" + response.code();
                    }
                    textViewResult.setVisibility(View.VISIBLE);
                    textViewResult.setText(mensagem);
                    return;
                }

                 specificUsers = Collections.singletonList(response.body());

                for(SpecificUser specificUser : specificUsers){
                    usuarioEspecificoCardView.setVisibility(View.VISIBLE);
                    mensagemApp.setVisibility(View.VISIBLE);
                    textViewResult.setVisibility(View.INVISIBLE);
                    if (specificUser.getStatus().equals("ACTIVE")){
                        status = "Usuário Ativo";
                    } else {
                        status = specificUser.getStatus();
                    }

                    nomeUsuario.setText(specificUser.getName());
                    emailUsuario.setText(specificUser.getEmail());
                    statusUsuario.setText(status);
                    for (int i = 0; i < specificUser.getMemberships().size(); i++){
                        // TODO: Verificando se é um Administrador
                        String idAdmin = "5ab54394b079877ff6187948";
                        if (specificUser.getMemberships().get(i).getTargetID().toString().trim().contains(idAdmin)){
                            useradmin += 1;
                            Log.d("Log", "Permissão: ADM.");
                            if (useradmin >= 1){
                                tipoUsuario.setText("Administrador");
                            }
                            if (useradmin < 1) {
                                tipoUsuario.setText("Usuário Comum");
                            }
                        } else {
                            if (useradmin < 1) {
                                Log.d("Log", "Permissão: Usuário comum.");
                                tipoUsuario.setText("Usuário Comum");
                            }
                        }
                    };
                }

                // TODO: Abri tela para consultar pontos registrados por intervalo de data
                usuarioEspecificoCardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(MainActivity.this, DateRangeActivity.class);
                        i.putExtra("usuarioLogadoID", usuarioLogadoID);

                        startActivity(i);
                    }
                });

            }

            @Override
            public void onFailure(Call<SpecificUser> call, Throwable t) {
                usuarioEspecificoCardView.setVisibility(View.INVISIBLE);
                mensagemApp.setVisibility(View.INVISIBLE);
                textViewResult.setVisibility(View.VISIBLE);
                mensagem = "Ocorreu um erro. \nTente novamente mais tarde \nCódigo do Erro: " + t.getMessage();

                textViewResult.setText(mensagem);
                Log.e("Log", "Code: " + t.getMessage());
                Log.e("Log", "Erro: "  + t.getLocalizedMessage());
                Log.e("Log", "Erro: "  + t.getCause());
            }
        });

    }

    // TODO: envia dados de autenticacao do usuário para a API
    public void dadoAuth(){
        uid = mAuth.getCurrentUser().getUid();

        // referência
        DatabaseReference userRef = database.getReference("users/" + uid);
        // obter dados da referência (UID)
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // obter dados
                apiKey = dataSnapshot.child("apiKey").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // TODO: Criando menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // TODO: Ações do menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // botão sair
        if (id == R.id.sair) {
            mAuth.signOut();
            Log.d("Log", "Usuário deslogou");
            finish();
        }
        // botão alterar API Key
        if (id == R.id.alterarAPIKey) {
            alterarAPIKey();
            Log.d("Log", "Usuário solicitou alterar sua APIKey do Clockify");
        }
        return true;
    }

    // TODO: Envia para tela de alterar API Key
    public void alterarAPIKey(){
        Intent i = new Intent(MainActivity.this, ModifyAPIKeyActivity.class);
        startActivity(i);
    }



}
