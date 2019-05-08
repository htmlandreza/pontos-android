package com.andrezamoreira.pontos.controllers;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.andrezamoreira.pontos.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class ModifyAPIKeyActivity extends AppCompatActivity {

    // declaração
    private FirebaseAuth mAuth;

    private EditText editApiKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_apikey);

        // instanciando
        mAuth = FirebaseAuth.getInstance();

        editApiKey = findViewById(R.id.novaApiKeyEditText);
    }

    // TODO: alterar APIKey do usuário
    public void novaApiKey(View view){
        final String apiKey = editApiKey.getText().toString().trim();

        // TODO: Validação de campos
        if (apiKey.equals("")){
            editApiKey.setError("Preencha este campo");
            editApiKey.requestFocus();
            return;
        }
        // APIKey deve ser = 16 caracteres (0...15 = 16)
        if (apiKey.length() < 15){
            editApiKey.setError("APIKey inválida");
            editApiKey.requestFocus();
            return;
        }

        // altera dados na base
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference userRef = database.getReference("users/" + user.getUid());

            Map<String, Object> userInfos = new HashMap<>();
            userInfos.put("apiKey", apiKey);

            userRef.updateChildren(userInfos)
                    // retorno de chamada de conclusão
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(ModifyAPIKeyActivity.this, "Sua API Key foi alterada com sucesso!", Toast.LENGTH_SHORT).show();
                        Log.d("Log", "APIKey do usuário foi alterada");
                        finish();
                    }  else {
                        try {
                            throw task.getException();
                        } catch (Exception e){
                            Toast.makeText(ModifyAPIKeyActivity.this, "Erro ao alterar API Key do seu usuário!", Toast.LENGTH_SHORT).show();
                            Log.e("Log", "Erro ao alterar APIKey do usuário");
                            finish();
                        }
                    }
                }
            });




        } else {

        }
    }
}
