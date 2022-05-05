package com.gproject.binge.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class login extends AppCompatActivity {

   EditText etGetAdmin;
   Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etGetAdmin = findViewById(R.id.etGetAdmin);
        btnSubmit = findViewById(R.id.btnSubmit);

        checkSharedPre();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etGetAdmin.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "No username provided", Toast.LENGTH_SHORT).show();
                }else{
                    SharedPreferences sp = getSharedPreferences("credentials", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("username", etGetAdmin.getText().toString());
                    editor.apply();
                    launcher();
                }
            }
        });
    }

    public void checkSharedPre(){
        SharedPreferences sp = getSharedPreferences("credentials", MODE_PRIVATE);
        if (sp.contains("username")){
            Toast.makeText(getApplicationContext(), "Auth success", Toast.LENGTH_SHORT).show();
            launcher();
        }
    }

    public void launcher(){
        Intent i;
        i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}