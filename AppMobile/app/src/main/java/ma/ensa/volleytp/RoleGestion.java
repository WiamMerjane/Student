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

public class RoleGestion extends AppCompatActivity {

    private ListView ListViewRoles;
    private List<Role> rolesList; // Utilisez la classe Role comme modèle
    private ArrayAdapter<Role> adapter; // Utilisez un ArrayAdapter de Role
    private RequestQueue requestQueue;
    private String baseUrl = "http://10.0.2.2:8082/api/v1/roles";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_gestion);

        ListViewRoles = findViewById(R.id.listViewRoles);
        rolesList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, rolesList);

        ListViewRoles.setAdapter(adapter);
        requestQueue = Volley.newRequestQueue(this);

        loadRoleList();

        ListViewRoles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Role selectedRole = rolesList.get(position);
                int roleId = selectedRole.getId();
                showOptionsDialog(roleId);
            }
        });
    }

    private void loadRoleList() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, baseUrl, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        rolesList.clear();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject roleObject = response.getJSONObject(i);
                                int id = roleObject.getInt("id");
                                String name = roleObject.getString("name");
                                Role role = new Role(id, name);
                                rolesList.add(role);
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
                    deleteRole(selectedItem);
                }
            }
        });
        builder.create().show();
    }

    private void showEditDialog(final int roleId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Modifier le rôle");
        final EditText nameEditText = new EditText(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(nameEditText);
        builder.setView(layout);
        builder.setPositiveButton("Enregistrer", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String newName = nameEditText.getText().toString();
                updateRole(roleId, newName);
            }
        });
        builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    private void updateRole(int roleId, String newName) {
        String updateUrl = "http://10.0.2.2:8082/api/v1/roles/" + roleId;
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("name", newName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest updateRequest = new JsonObjectRequest(Request.Method.PUT, updateUrl, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loadRoleList();
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

    private void deleteRole(int roleId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation");
        builder.setMessage("Êtes-vous sûr de vouloir supprimer ce rôle ?");
        builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                performRoleDeletion(roleId);
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

    private void performRoleDeletion(int roleId) {
        String deleteUrl = "http://10.0.2.2:8082/api/v1/roles/" + roleId;
        StringRequest deleteRequest = new StringRequest(Request.Method.DELETE, deleteUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loadRoleList();
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
