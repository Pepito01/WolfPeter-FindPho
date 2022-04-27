package com.example.FindPho;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.findp.ProfileActivity;
import com.google.gson.Gson;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextUsername, editTextPassword;
    private Button buttonLogin, buttonCancel;
    private String username, password;
    private SharedPreferences sharedPreferences;
    private String URL = "http://10.0.2.2:8000/api/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sharedPreferences = LoginActivity.this.getSharedPreferences("token", Context.MODE_PRIVATE);
        init();

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = editTextUsername.getText().toString().trim();
                password = editTextPassword.getText().toString();
                if(username.isEmpty() || username.length() < 5 || username.length() > 20) {
                    Toast.makeText(LoginActivity.this, "A felhasználónév minimum 5, maximum 20 karakterből állhat!", Toast.LENGTH_SHORT).show();
                }
                else if(password.isEmpty() || password.length() < 8) {
                    Toast.makeText(LoginActivity.this, "A jelszónak minimum 8 karakterből kell állnia!", Toast.LENGTH_SHORT).show();
                }
                else {
                    new RequestTaskLogin().execute();
                }
            }
        });
    }

    private void init() {
        editTextUsername = findViewById(R.id.editUsername);
        editTextPassword = findViewById(R.id.editpassword);
        buttonLogin = findViewById(R.id.buttonLogin);


    }

    private class RequestTaskLogin extends AsyncTask<Void, Void, Response> {

        @Override
        protected Response doInBackground(Void... voids) {

            Response response = null;
            try {
                String data = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password);
                response = RequestHandler.post(URL + "login", data);
            } catch (IOException e) {
                e.printStackTrace();
            }


            return response;
        }

        @Override
        protected void onPostExecute(Response response) {
            super.onPostExecute(response);
            if (response == null) {

                Toast.makeText(LoginActivity.this, "Hiba történt a bejelentkezés során", Toast.LENGTH_SHORT).show();

            }else if (response.getResponseCode() >= 400){
                Toast.makeText(LoginActivity.this, response.getContent(), Toast.LENGTH_SHORT).show();
            }

            else {
                Toast.makeText(LoginActivity.this, "Sikeres bejelentkezés", Toast.LENGTH_SHORT).show();
                Gson gson = new Gson();
                Token token = gson.fromJson(response.getContent(), Token.class);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("token", token.getToken());
                editor.apply();
                Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();

            }
        }
    }
}