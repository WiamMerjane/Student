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

public class FiliereGestion extends AppCompatActivity {

    private ListView filieresListView;
    private List<Filiere> filieresList; // Utilisez la classe Filiere comme modèle
    private ArrayAdapter<Filiere> adapter; // Utilisez un ArrayAdapter de Filiere
    private RequestQueue requestQueue;
    private String baseUrl = "http://10.0.2.2:8082/api/v1/filieres";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filiere);

        filieresListView = findViewById(R.id.listViewFilieres);
        filieresList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filieresList);

        filieresListView.setAdapter(adapter);
        requestQueue = Volley.newRequestQueue(this);

        loadFiliereList();

        filieresListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Filiere selectedFiliere = filieresList.get(position);
                int filiereId = selectedFiliere.getId();
                showOptionsDialog(filiereId);
            }
        });
    }

    private void loadFiliereList() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, baseUrl, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        filieresList.clear();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject filiereObject = response.getJSONObject(i);
                                int id = filiereObject.getInt("id");
                                String code = filiereObject.getString("code");
                                String libelle = filiereObject.getString("libelle");
                                Filiere filiere = new Filiere(id, code, libelle);
                                filieresList.add(filiere);
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
                    }
                });

        requestQueue.add(jsonArrayRequest);
    }

    private void showOptionsDialog(final int selectedItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sélectionnez une option");
        builder.setItems(new CharSequence[]{"Modifier", "Supprimer"}, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    showEditDialog(selectedItem);
                } else if (which == 1) {
                    deleteFiliere(selectedItem);
                }
            }
        });
        builder.create().show();
    }

    private void showEditDialog(final int filiereId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Modifier la filière");
        final EditText codeEditText = new EditText(this);
        final EditText libelleEditText = new EditText(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(codeEditText);
        layout.addView(libelleEditText);
        builder.setView(layout);
        builder.setPositiveButton("Enregistrer", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String newCode = codeEditText.getText().toString();
                String newLibelle = libelleEditText.getText().toString();
                updateFiliere(filiereId, newCode, newLibelle);
            }
        });
        builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    private void updateFiliere(int filiereId, String newCode, String newLibelle) {
        String updateUrl = "http://10.0.2.2:8082/api/v1/filieres/" + filiereId;
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("code", newCode);
            jsonBody.put("libelle", newLibelle);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest updateRequest = new JsonObjectRequest(Request.Method.PUT, updateUrl, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loadFiliereList();
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

    private void deleteFiliere(int filiereId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation");
        builder.setMessage("Êtes-vous sûr de vouloir supprimer cette filière ?");
        builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                performFiliereDeletion(filiereId);
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

    private void performFiliereDeletion(int filiereId) {
        String deleteUrl = "http://10.0.2.2:8082/api/v1/filieres/" + filiereId;
        StringRequest deleteRequest = new StringRequest(Request.Method.DELETE, deleteUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loadFiliereList();
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
