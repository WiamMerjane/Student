package ma.ensa.volleytp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ma.ensa.volleytp.R;
import ma.ensa.volleytp.StudentGestion;

public class StudiantAdd extends AppCompatActivity implements View.OnClickListener {

    private EditText nom;
    private EditText phone;
    private EditText email;
    private Spinner spinnerFiliere;
    private Button bnAdd;
    private Button btnListeStudents;
    private RequestQueue requestQueue;
    private ArrayAdapter<String> spinnerAdapter;
    private List<String> filiereList;

    private String insertUrl = "http://10.0.2.2:8082/api/student";
    private String baseUrl = "http://10.0.2.2:8082/api/v1/filieres";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_add);

        nom = findViewById(R.id.nom);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);

        bnAdd = findViewById(R.id.bnAdd);
        bnAdd.setOnClickListener(this);

        btnListeStudents = findViewById(R.id.btnListeStudents);
        btnListeStudents.setOnClickListener(this);

        spinnerFiliere = findViewById(R.id.spinnerFiliere);
        filiereList = new ArrayList<>();
        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, filiereList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFiliere.setAdapter(spinnerAdapter);

        loadFiliereList();
    }

    private void loadFiliereList() {
        requestQueue = Volley.newRequestQueue(getApplicationContext()); // Initialisez la file d'attente ici
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, baseUrl, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        filiereList.clear();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject filiereObject = response.getJSONObject(i);
                                String libelle = filiereObject.getString("libelle");
                                filiereList.add(libelle);
                            }
                            spinnerAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("Erreur", "Erreur lors de l'analyse JSON : " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Erreur", "Erreur de demande : " + error.toString());
                    }
                });

        requestQueue.add(jsonArrayRequest);
    }

    @Override
    public void onClick(View view) {
        if (view == bnAdd) {
            String selectedFiliere = spinnerFiliere.getSelectedItem().toString();

            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("Nom", nom.getText().toString());
                jsonBody.put("Email", email.getText().toString());
                jsonBody.put("Phone", phone.getText().toString());
                jsonBody.put("Filiere", selectedFiliere);
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
        } else if (view == btnListeStudents) {
            Intent intent = new Intent(StudiantAdd.this, StudentGestion.class);
            startActivity(intent);
        }
    }
}
