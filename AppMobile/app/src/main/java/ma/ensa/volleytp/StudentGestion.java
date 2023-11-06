package ma.ensa.volleytp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StudentGestion extends AppCompatActivity {

    private ListView ListViewStudents;
    private List<Student> studentsList; // Utilisez la classe Student comme modèle
    private ArrayAdapter<Student> adapter; // Utilisez un ArrayAdapter de Student
    private RequestQueue requestQueue;
    private String baseUrl = "http://10.0.2.2:8082/api/student";

    private List<Filiere> filiereList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_gestion);

        ListViewStudents = findViewById(R.id.listViewStudents);
        studentsList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, studentsList);

        ListViewStudents.setAdapter(adapter);

        // À l'intérieur de onCreate()
        requestQueue = Volley.newRequestQueue(this);

// Ajoutez la demande pour obtenir la liste des filières ici
        JsonArrayRequest filieresRequest = new JsonArrayRequest(Request.Method.GET, "http://10.0.2.2:8082/api/v1/filieres", null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray filieresResponse) {
                        filiereList.clear();
                        try {
                            for (int j = 0; j < filieresResponse.length(); j++) {
                                JSONObject filiereObject = filieresResponse.getJSONObject(j);
                                int filiereId = filiereObject.getInt("id");
                                String filiereCode = filiereObject.getString("code");
                                String filiereLibelle = filiereObject.getString("libelle");


                                Filiere filiere = new Filiere(filiereId, filiereCode, filiereLibelle);
                                filiereList.add(filiere);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("Erreur", "Erreur lors de l'analyse JSON des filières : " + e.getMessage());
                        }

                        // Après avoir obtenu la liste des filières, chargez la liste des étudiants
                        loadStudentList();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Erreur", "Erreur de demande de filières : " + error.toString());

                        // En cas d'erreur, vous pouvez toujours essayer de charger la liste des étudiants ici
                        loadStudentList();
                    }
                });

        requestQueue.add(filieresRequest);




        loadStudentList();

        ListViewStudents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Student selectedStudent = studentsList.get(position);
                int studentId = selectedStudent.getId();
                showOptionsDialog(studentId);
            }
        });
    }

    private void loadStudentList() {
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
                                int filiere_id = studentObject.optInt("filiere_id", 2);

                                // Effectuez une nouvelle requête pour obtenir les détails de la filière
                                retrieveFiliereDetails(id, name, email, phone, filiere_id);
                            }
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
    private void retrieveFiliereDetails(final int studentId, final String name, final String email, final int phone, final int filiereId) {
        // URL pour obtenir les détails de la filière en utilisant l'ID
        String filiereUrl = "http://10.0.2.2:8082/api/v1/filieres/" + filiereId;

        JsonObjectRequest filiereDetailsRequest = new JsonObjectRequest(Request.Method.GET, filiereUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject filiereResponse) {
                        try {
                            // Analysez les détails de la filière à partir de la réponse JSON
                            int filiereId = filiereResponse.getInt("id");
                            String filiereCode = filiereResponse.getString("code");
                            String filiereLibelle = filiereResponse.getString("libelle");

                            // Créez un objet Filiere avec les détails
                            Filiere filiere = new Filiere(filiereId, filiereCode, filiereLibelle);

                            // Créez un nouvel objet Student avec les détails de la filière
                            Student student = new Student(studentId, name, email, phone, filiere);

                            // Ajoutez le nouvel étudiant à la liste des étudiants
                            studentsList.add(student);

                            // Mettez à jour l'interface utilisateur
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("Erreur", "Erreur lors de l'analyse JSON des détails de la filière : " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Erreur", "Erreur de demande des détails de la filière : " + error.toString());
                    }
                });

        requestQueue.add(filiereDetailsRequest);
    }


    private void showOptionsDialog(final int selectedItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sélectionnez une option");
        builder.setItems(new CharSequence[]{"Modifier", "Supprimer"}, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    showEditDialog(selectedItem);
                } else if (which == 1) {
                    deleteStudent(selectedItem);
                }
            }
        });
        builder.create().show();
    }

    private void showEditDialog(final int studentId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Modifier l'étudiant");
        final EditText nameEditText = new EditText(this);
        final EditText emailEditText = new EditText(this);
        final EditText phoneEditText = new EditText(this);
        final EditText filiereEditText = new EditText(this);
        // Ajoutez d'autres champs d'édition pour l'e-mail, le téléphone, la filière, etc.
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(nameEditText);
        layout.addView(emailEditText);
        layout.addView(phoneEditText);

        layout.addView(filiereEditText);


        // Ajoutez d'autres champs d'édition ici
        builder.setView(layout);
        builder.setPositiveButton("Enregistrer", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String newName = nameEditText.getText().toString();
                String newEmail = emailEditText.getText().toString();
                String newPhone = phoneEditText.getText().toString();
                String newFiliere = filiereEditText.getText().toString();



                updateStudent(studentId, newName, newEmail,  newPhone,  newFiliere);
            }
        });
        builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    private void updateStudent(int studentId, String newName, String newEmail, String newPhone, String newFiliere) {
        String updateUrl = "http://10.0.2.2:8082/api/v1/students/" + studentId;
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("name", newName);
            jsonBody.put("name", newEmail);
            jsonBody.put("name", newPhone);
            jsonBody.put("name", newFiliere);



        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest updateRequest = new JsonObjectRequest(Request.Method.PUT, updateUrl, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loadStudentList();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Erreur", "Erreur lors de la mise à jour : " + error.toString());
                    }
                });
        requestQueue.add(updateRequest);
    }

    private void deleteStudent(int studentId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation");
        builder.setMessage("Êtes-vous sûr de vouloir supprimer cet étudiant ?");
        builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                performStudentDeletion(studentId);
            }
        });
        builder.setNegativeButton("Non", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void performStudentDeletion(int studentId) {
        String deleteUrl = "http://10.0.2.2:8082/api/v1/students/" + studentId;
        StringRequest deleteRequest = new StringRequest(Request.Method.DELETE, deleteUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loadStudentList();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Erreur", "Erreur lors de la suppression : " + error.toString());
                    }
                });
        requestQueue.add(deleteRequest);
    }
}