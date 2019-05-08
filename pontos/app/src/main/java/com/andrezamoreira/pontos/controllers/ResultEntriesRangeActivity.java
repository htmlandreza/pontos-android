package com.andrezamoreira.pontos.controllers;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.andrezamoreira.pontos.R;
import com.andrezamoreira.pontos.models.Autenticacao;
import com.andrezamoreira.pontos.models.TimeEntries;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ResultEntriesRangeActivity extends AppCompatActivity {
    // declarando
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;

    public String uid;
    public String idUsuario;
    public String data_inicio;
    public String data_final;

    private static List<TimeEntries> timeEntries;

    private RecyclerView resultRecy;
    private ResultEntriesRangeAdapter adapter;
    
    private String dataInicio;
    private String dataFinal;
    private String usuarioLogadoID;

    // resultado da API
    private TextView textViewResult;
   // private TextView resultadoRecy;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_entries_range);

        // instanciando
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        textViewResult = findViewById(R.id.resultTextView);
        textViewResult.setVisibility(View.VISIBLE);
        textViewResult.setText("Carregando...");

        resultRecy = findViewById(R.id.resultRecyclerView);
        resultRecy.setHasFixedSize(true);
        LinearLayoutManager lln = new LinearLayoutManager(this);
        lln.setOrientation(LinearLayoutManager.VERTICAL);
        resultRecy.setLayoutManager(lln);
        resultRecy.setVisibility(View.INVISIBLE);



        // recebe dados da DataRangeActivity
        Intent i = getIntent();

        dataInicio = i.getStringExtra("dataInicio");
        dataFinal = i.getStringExtra("dataFinal");
        usuarioLogadoID = i.getStringExtra("usuarioLogadoID");


        Log.d("Log", "Tela ResultEntriesRange\nRecebeu as datas\n"
                + "Data Inicial: " + dataInicio
                + "\nData Final:    " + dataFinal
                + "\nID do usuário: " + usuarioLogadoID);

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

                Log.d("Log", "Foi clicado no card \npara gerar relatório por intervalo de data.");
                Log.d("Log", "Usuário logado: " + autenticacao.getUsuario());
                Log.d("Log", "Email: " + autenticacao.getEmail());
                Log.d("Log", "APIKey: " + autenticacao.getApiKey());
                Log.d("Log", "EmpresaId: " + autenticacao.getEmpresaId());

                modificDates();

                //Log.d("Log", "Busca usuario foi chamado");
                resultTimeEntries(autenticacao);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // TODO: Modifica formato das datas
    public void modificDates(){
        String dtinicio = dataInicio;
        String dtfinal = dataFinal;

        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

        Date dinicio;
        Date dfinal;

        try {
            dinicio = df.parse(dtinicio);
            dfinal = df.parse(dtfinal);
            df = new SimpleDateFormat("yyyy-MM-dd");
            data_inicio = df.format(dinicio);
            data_final = df.format(dfinal);
            Log.i("Log", "Datas transformadas\n"
                    + "Data Inicio: " + data_inicio
                    + "\nData Final: " + data_final);
        } catch (ParseException e) {
            Log.w("Log", "Erro ao transformar datas. Codigo: " + e);
        }
    }

    public void resultTimeEntries(Autenticacao autenticacao){

        // TODO: requisicão a API para validar se o usuário existe no clickify
        String valueAPIKey = autenticacao.getApiKey();
        //String valueAPIKey = "XJ4tffFcmAri1FHE";
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\r\n  \"start\": \""
                + data_inicio + "T00:00:00.000Z\",\r\n  \"end\": \""
                + data_final+ "T23:59:59.999Z\"\r\n}");

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(

                        chain -> {Request request = chain.request().newBuilder()
                                .post(body)
                                .addHeader("x-api-key", valueAPIKey)
                                .addHeader("Content-Type", "application/json")
                                .build();
                            return chain.proceed(request);
                        }).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.clockify.me/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();

        JSONPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JSONPlaceHolderApi.class);

        idUsuario = usuarioLogadoID;
        retrofit2.Call<List<TimeEntries>> call = jsonPlaceHolderApi.getTimeEntries(idUsuario);

        call.enqueue(new Callback<List<TimeEntries>>() {
            @Override
            public void onResponse(retrofit2.Call<List<TimeEntries>> call, Response<List<TimeEntries>> response) {
                if(!response.isSuccessful()){
//                    textViewResult.setVisibility(View.VISIBLE);
//                    textViewResult.setText("Code " + response.code());
//                    usuarioEspecificoCardView.setVisibility(View.INVISIBLE);
//                    mensagemApp.setVisibility(View.INVISIBLE);
                    Log.d("Log", "Code " + response.code());
                    return;
                }

                timeEntries = response.body();
                Log.d("Log", "Retornou " + timeEntries);

                if (timeEntries.size() == 0){
                    Log.d("Log", "Erro ");
                    //resultRecy.setVisibility(View.VISIBLE);
                    textViewResult.setVisibility(View.VISIBLE);
                    textViewResult.setText("Ocorreu um erro. \nTente novamente usando outras datas. :)");
                }

                // dados
                for(TimeEntries timeEntrie : timeEntries){
                    //carrega dados aqui
                    textViewResult.setVisibility(View.INVISIBLE);
                    resultRecy.setVisibility(View.VISIBLE);

                    adapter = new ResultEntriesRangeAdapter(timeEntries);
                    resultRecy.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<TimeEntries>> call, Throwable t) {
                textViewResult.setVisibility(View.VISIBLE);
                textViewResult.setText(t.getMessage());
            }
        });
    }

    public void setTimesEntriesResult(){


    }
}
