package com.example.findp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.IOException;

public class RegisterActivity extends AppCompatActivity {
    private String URL = "http://10.0.2.2:8000/api/";
    private EditText editTextUsername, editTextEmail, editTextPassword;
    private Button buttonRegister, buttonCancel;
    private String username, email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = editTextUsername.getText().toString().trim();
                email = editTextEmail.getText().toString().trim();
                password = editTextPassword.getText().toString();
                if(username.isEmpty() || username.length() < 5 || username.length() > 20) {
                    Toast.makeText(RegisterActivity.this, "A felhasználónév minimum 5, maximum 20 karakterből állhat!", Toast.LENGTH_SHORT).show();
                }
                else if(email.isEmpty() || email.length()  > 255) {
                    Toast.makeText(RegisterActivity.this, "Az email nem lehet üres, maximum 255 karakterből állhat!", Toast.LENGTH_SHORT).show();
                }
                else if(password.isEmpty() || password.length() < 8) {
                    Toast.makeText(RegisterActivity.this, "A jelszónak minimum 8 karakterből kell állnia!", Toast.LENGTH_SHORT).show();
                }
                else {
                    new RequestTaskRegister().execute();
                }
            }
        });
    }

    private void init() {
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextEmail = findViewById(R.id.editTextEmail);
        buttonRegister = findViewById(R.id.registerButton);
        buttonCancel = findViewById(R.id.Backbtn);
    }

    private class RequestTaskRegister extends AsyncTask<Void, Void, Response> {

        @Override
        protected Response doInBackground(Void... voids) {

            Response response = null;
            try {
                String data = String.format("{\"username\":\"%s\",\"email\":\"%s\",\"password\":\"%s\"}", username, email, password);
                response = RequestHandler.post(URL + "users", data);
            } catch (IOException e) {
                e.printStackTrace();
            }


            return response;
        }

        @Override
        protected void onPostExecute(Response response) {
            super.onPostExecute(response);
            Gson converter = new Gson();
            if (response == null){
                Toast.makeText(RegisterActivity.this, "Hiba történt a regisztráció során", Toast.LENGTH_SHORT).show();
            }
            else if(response.getResponseCode() >= 400) {
                try {
                    ErrorMessage errorMessage = converter.fromJson(response.getContent(), ErrorMessage.class);
                    Toast.makeText(RegisterActivity.this, errorMessage.getMessage(), Toast.LENGTH_SHORT).show();
                }
                catch (Exception e) {
                    Toast.makeText(RegisterActivity.this, response.getContent(), Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(RegisterActivity.this, "Sikeres regisztráció", Toast.LENGTH_SHORT).show();
                finish();
            }

        }
    }
}