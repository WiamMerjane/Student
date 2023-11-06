package ma.ensa.volleytp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FiliereStudentsActivity extends AppCompatActivity {

    private ListView studentsListView;
    private List<Student> studentsList;
    private ArrayAdapter<Student> adapter;
    private Spinner spinnerFiliere;
    private List<Filiere> filiereList; // Liste de filières avec ID et nom
    private ArrayAdapter<Filiere> filiereAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filiere_students);

        studentsListView = findViewById(R.id.listViewStudentsByFiliere);
        studentsList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, studentsList);
        studentsListView.setAdapter(adapter);

        spinnerFiliere = findViewById(R.id.spinnerFiliere);
        filiereList = new ArrayList<>();
        filiereAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, filiereList);
        filiereAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFiliere.setAdapter(filiereAdapter);

        loadFiliereList();

        // Après avoir initialisé votre spinner et votre liste
        Button btnAfficherEtudiants = findViewById(R.id.btnAfficherEtudiants);

        btnAfficherEtudiants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Récupérez la filière sélectionnée depuis le Spinner
                Filiere selectedFiliere = (Filiere) spinnerFiliere.getSelectedItem();

                // Appelez la méthode pour charger les étudiants de la filière sélectionnée en passant l'ID
                loadStudentsByFiliere(selectedFiliere.getId());
            }
        });

        // Lorsqu'un élément du Spinner est sélectionné
        spinnerFiliere.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Filiere selectedFiliere = filiereList.get(position);
                String selectedFiliereName = selectedFiliere.getLibelle();
                setTitle("Étudiants de la filière " + selectedFiliereName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Code à exécuter lorsqu'aucun élément n'est sélectionné
            }
        });
    }

    private void loadFiliereList() {
        // Effectuez une requête HTTP GET pour récupérer la liste des filières depuis l'URL
        String baseUrl = "http://10.0.2.2:8082/api/v1/filieres";

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, baseUrl, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        filiereList.clear();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject filiereObject = response.getJSONObject(i);
                                int id = filiereObject.getInt("id");
                                String filiereName = filiereObject.getString("libelle");
                                Filiere filiere = new Filiere(id, filiereName);
                                filiereList.add(filiere);
                            }
                            filiereAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("Erreur", "Erreur lors de l'analyse JSON des filières : " + e.getMessage());
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

    private void loadStudentsByFiliere(int selectedFiliereId) {
        // Effectuez une requête HTTP GET pour récupérer les étudiants de la filière en utilisant l'ID
        String baseUrl = "http://10.0.2.2:8082/api/student/filiere/" + selectedFiliereId;

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, baseUrl, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        studentsList.clear();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject studentObject = response.getJSONObject(i);
                                int id = studentObject.getInt("id");
                                String name = studentObject.getString("name");
                                String email = studentObject.getString("email");
                                int phone = studentObject.getInt("phone");
                                Student student = new Student(id, name, email, phone);
                                studentsList.add(student);
                            }
                            adapter.notifyDataSetChanged();
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
                        // Affichez un message d'erreur à l'utilisateur
                        Toast.makeText(FiliereStudentsActivity.this, "Erreur lors du chargement des étudiants", Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(jsonArrayRequest);
    }
}
