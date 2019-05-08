package com.andrezamoreira.pontos.controllers;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.andrezamoreira.pontos.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;

public class ModifyPasswordActivity extends AppCompatActivity {

    // declarando

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private TextView emailAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_password);

        // instanciando
        mAuth = FirebaseAuth.getInstance();

        emailAddress = findViewById(R.id.novaApiKeyEditText);
    }

    // TODO: enviar um e-mail de redefinição de senha
    public void redefinirSenha(View view){
        final String email = emailAddress.getText().toString().trim();

        // TODO: Validação de campos
        if (email.equals("")){
            emailAddress.setError("Digite seu de e-mail cadastrado.");
            emailAddress.requestFocus();
            return;
        }

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ModifyPasswordActivity.this, "E-mail de redefinição de senha foi enviado para o e-mail solicitado.", Toast.LENGTH_SHORT).show();
                            Log.d("Log", "Usuário solicitou uma nova senha");
                            finish();
                        }  else {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidCredentialsException e){
                                emailAddress.setError("E-mail inválido!");
                                emailAddress.requestFocus();
                            } catch (Exception e){
                                Toast.makeText(ModifyPasswordActivity.this, "Falha na redefinição de senha por e-mail!", Toast.LENGTH_SHORT).show();
                                Log.e("Log", "Falha na redefinição de senha por e-mail!");
                            }
                        }
                    }
                });
    }


}
