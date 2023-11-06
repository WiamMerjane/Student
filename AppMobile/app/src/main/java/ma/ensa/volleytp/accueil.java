package ma.ensa.volleytp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class accueil extends AppCompatActivity   {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button button1 = findViewById(R.id.button1);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button button2 = findViewById(R.id.button2);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button button3 = findViewById(R.id.button3);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button button4 = findViewById(R.id.button4);


        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(accueil.this, MainActivity.class);
                startActivity(intent);
            }
        });


        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lorsqu'on clique sur Bouton 2, on passe à une troisième page
                Intent intent = new Intent(accueil.this, RoleAdd.class);
                startActivity(intent);
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(accueil.this, StudiantAdd.class);
                startActivity(intent);            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(accueil.this, FiliereStudentsActivity.class);
                startActivity(intent);            }
        });
  }
}