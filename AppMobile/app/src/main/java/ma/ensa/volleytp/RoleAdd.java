package ma.ensa.volleytp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class RoleAdd extends AppCompatActivity implements View.OnClickListener {


    private EditText nom;
    private Button bnAdd;
    RequestQueue requestQueue;
    private Button btnListeRoles;

    String insertUrl = "http://10.0.2.2:8082/api/v1/roles";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_add);

        nom = findViewById(R.id.nom);

        bnAdd = findViewById(R.id.bnAdd);

        bnAdd.setOnClickListener(this);
        btnListeRoles= findViewById(R.id.btnListeRoles);
        btnListeRoles.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        //Filiere filiere = new Filiere(code.getText().toString(), libelle.getText().toString());
        //Gson toJson()

        if (view == bnAdd) {

            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("Nom", nom.getText().toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }
            requestQueue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                    insertUrl, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("resultat", response + "");
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Erreur", error.toString());
                }
            });
            requestQueue.add(request);
        } else if (view == btnListeRoles) {
            // Lorsque le bouton pour afficher la liste des filières est cliqué,
            // lancez une nouvelle activité pour afficher la liste des filières.
            Intent intent = new Intent(RoleAdd.this, RoleGestion.class);
            startActivity(intent);
        }
    }


}