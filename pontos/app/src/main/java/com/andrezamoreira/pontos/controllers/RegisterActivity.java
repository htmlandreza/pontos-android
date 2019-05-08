package com.andrezamoreira.pontos.controllers;

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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    // declarando
    private FirebaseAuth mAuth;

    private EditText editUsuario;
    private EditText editAPIKey;
    private EditText editEmail;
    private EditText editSenha;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // instanciando
        mAuth = FirebaseAuth.getInstance();

        editUsuario = findViewById(R.id.cadastroUsuarioEditText);
        editAPIKey = findViewById(R.id.cadastroApiKeyEditText);
        editEmail = findViewById(R.id.cadastroEmailEditText);
        editSenha = findViewById(R.id.cadastroSenhaEditText);
    }

    // TODO: Botão salvar
    public void salvar (View view){
        // captura valores da tela
        final String usuario = editUsuario.getText().toString().trim();
        final String apiKey = editAPIKey.getText().toString().trim();
        final String email = editEmail.getText().toString().trim();
        final String senha = editSenha.getText().toString().trim();

        // TODO: Validação de campos
        // campos vazios
        if (usuario.equals("")){
            editUsuario.setError("Preencha este campo");
            editUsuario.requestFocus();
            return;
        }
        if (apiKey.equals("")){
            editAPIKey.setError("Preencha este campo");
            editAPIKey.requestFocus();
            return;
        }
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
            editEmail.setError("Domínio de e-mail inválido!");
            editEmail.requestFocus();
            return;
        }

        // TODO: Cadastrando no autenticação do Firebase
        mAuth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            // se tiver passado nos requisitos de validação de campo e validação do Firebase
                            // TODO: Salva dados do usuário no Realtime Database
                            FirebaseUser user = mAuth.getCurrentUser();
                            // TODO: descomentar depois
                            //user.sendEmailVerification();
                            Log.d("Log", "Foi enviado um e-mail de verificacao");
                            Toast.makeText(RegisterActivity.this, "Enviamos um e-mail de verificação o seu cadastro", Toast.LENGTH_SHORT).show();

                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference userRef = database.getReference("users/" + user.getUid());

                            Map<String, Object> userInfos = new HashMap<>();
                            userInfos.put("usuario", usuario);
                            userInfos.put("apiKey", apiKey);
                            userInfos.put("email", email);
                            userInfos.put("empresaId", "5ab54394b079877ff6187947");

                            Log.d("Log", "Novo usuário foi cadastrado");

                            userRef.setValue(userInfos);
                            finish();
                        } else {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException e){
                                editSenha.setError("Senha fraca!");
                                editSenha.requestFocus();
                            } catch (FirebaseAuthInvalidCredentialsException e){
                                editEmail.setError("E-mail inválido!");
                                editEmail.requestFocus();
                            } catch (FirebaseAuthUserCollisionException e){
                                editEmail.setError("E-mail já cadastrado!");
                                editEmail.requestFocus();
                            } catch (Exception e){
                                Log.e("Cadastro", e.getMessage());
                            }
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
}
