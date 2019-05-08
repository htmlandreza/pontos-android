package com.andrezamoreira.pontos.controllers;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.andrezamoreira.pontos.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    // declaração
    private FirebaseAuth mAuth;

    private EditText editEmail;
    private EditText editSenha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // instanciando
        mAuth = FirebaseAuth.getInstance();

        editEmail = findViewById(R.id.emailEditText);
        editSenha = findViewById(R.id.senhaEditText);
    }

    // TODO: Verificar usuário já está logado
    @Override
    protected void onStart() {
        super.onStart();

        // usuário atual
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // interface do usuário se comportar como usuário atual
        updateUI(currentUser);
    }

    // TODO: Interface do Usuário
    private void updateUI(FirebaseUser user){
        if (user != null){
            // passa pra tela inicial
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(i);
            Log.d("Log", "Usuário pediu para logar");
        }
    }

    // TODO: Botão Login
    public void login(View view){
        // captura valores da tela
        String email = editEmail.getText().toString().trim();
        String senha = editSenha.getText().toString().trim();

        // TODO: Validação de campos
        if (email.equals("")){
            editEmail.setError("Preencha este campo");
            editEmail.requestFocus();
            return;
        }
        if (senha.equals("")){
            editSenha.setError("Preencha este campo");
            editSenha.requestFocus();
            return;
        }
        // verifica se o e-mail é corporativo
        if (!checkEmailValidity(email)) {
            editEmail.setError("E-mail inválido!");
            editEmail.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            // loga usuário
                            updateUI(mAuth.getCurrentUser());
                        } else {
                            Toast.makeText(LoginActivity.this, "Usuário ou senha incorreta!", Toast.LENGTH_SHORT).show();
                            // sem usuário logado
                            updateUI(null);
                        }
                    }
                });
    }

    // TODO: Verifica se e-mail pertence a iBlue Consulting
    private boolean checkEmailValidity(String emailFormat){

        String getEmail = emailFormat;
        boolean getEnd;

        // verifica a inicialização do usuário
        boolean getResult = !TextUtils.isEmpty(getEmail) && android.util.Patterns.EMAIL_ADDRESS.matcher(getEmail).matches();

        // verifica se a extensão de e-mail está correta
        if (getEmail.endsWith("@iblueconsulting.com.br")){
            getEnd = true;
        } else {
            getEnd = false;
        }

        // testa início e fim
        return (getResult && getEnd);
    }

    // TODO: Envia para tela de cadastro
    public void cadastro(View view){
        Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(i);
    }

    // TODO: Envia para tela de recuperar senha
    public void recuperaSenha(View view){
        Intent i = new Intent(LoginActivity.this, ModifyPasswordActivity.class);
        startActivity(i);
    }
}
