package com.example.bancoproyectos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.bancoproyectos.api.ProyectosApiService;
import com.example.bancoproyectos.perfil.Perfil;
import com.google.android.material.navigation.NavigationView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivitylistado extends AppCompatActivity implements SearchView.OnQueryTextListener {

    RecyclerView recyclerView;
    ListaListadoProyectosAdapter listaListadoProyectosAdapter;
    Retrofit retrofit;
    ImageView imageView;
    private static final String TAG = "proyecto";
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;
    NavigationView navigationView;
    DetalleProyectoFragment detalleProyectoFragment;

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    private ArrayList<Listadoproyectos> listaProyectos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);
        recyclerView = findViewById(R.id.card_recycler_view);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigationView);



        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigationView);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();

        listaListadoProyectosAdapter = new ListaListadoProyectosAdapter(this);
        recyclerView.setAdapter(listaListadoProyectosAdapter);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, true);
        recyclerView.setLayoutManager(linearLayoutManager);


        retrofit = new Retrofit.Builder()
                .baseUrl("https://lexa2334.pythonanywhere.com/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();



        obtenerDatos();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.perfil:
                        Intent i = new Intent(MainActivitylistado.this, Perfil.class);
                        startActivity(i);
                        return true;

                    case R.id.mis_proyectos:
                        Intent in = new Intent(MainActivitylistado.this, MisProyectos.class);
                        startActivity(in);
                        return true;


                    default:
                        return false;
                }
            }
        });

    }

    private void remplaceFragment(Fragment fragment){
        FragmentManager frgManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = frgManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmenPadre, fragment);
        fragmentTransaction.commit();
    }

    public void enviarProyecto(Listadoproyectos listadoproyectos) {

        detalleProyectoFragment = new DetalleProyectoFragment();

        Bundle bundleEnvio = new Bundle();

        bundleEnvio.putSerializable("objeto", (Serializable) listadoproyectos);
        detalleProyectoFragment.setArguments(bundleEnvio);


        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_fragment, detalleProyectoFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }







    private void obtenerDatos() {
        ProyectosApiService service = retrofit.create(com.example.bancoproyectos.api.ProyectosApiService.class);

        Call<List<Listadoproyectos>> productsRespuestaCall = service.obtenerListaProyectos();
        productsRespuestaCall.enqueue(new Callback<List<Listadoproyectos>>() {
            @Override
            public void onResponse(Call<List<Listadoproyectos>> call, Response<List<Listadoproyectos>> response) {
                if (response.isSuccessful()) {
                    List<Listadoproyectos> proyectos = response.body();
                    listaProyectos.addAll(proyectos);
                    listaListadoProyectosAdapter.add((ArrayList<Listadoproyectos>) proyectos);
                }
            }

            @Override
            public void onFailure(Call<List<Listadoproyectos>> call, Throwable t) {
                Log.e(TAG, "Error al obtener proyectos: " + t.getMessage());
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Obtener el SearchView del menú
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        // No se utiliza en este caso, puede dejarse vacío o implementar alguna acción si lo deseas
        return false;
    }
    @Override
    public boolean onQueryTextChange(String newText) {
        // Filtrar la lista de proyectos según el texto de búsqueda
        ArrayList<Listadoproyectos> proyectosFiltrados = new ArrayList<>();
        for (Listadoproyectos proyecto : listaProyectos) {
            if (proyecto.getNombre_proyecto().toLowerCase().contains(newText.toLowerCase())) {
                proyectosFiltrados.add(proyecto);
            }
        }

        // Actualizar el adaptador con los proyectos filtrados
        listaListadoProyectosAdapter.setProyectos(proyectosFiltrados);

        // Mostrar mensaje de prueba cuando el botón del buscador está vacío
        if (newText.isEmpty()) {
            Toast.makeText(this, "Mensaje de prueba cuando el campo de búsqueda está vacío", Toast.LENGTH_SHORT).show();
        }

        return true;
    }
    public void mostrarCuadroDialogo(Listadoproyectos proyecto) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_personal, null);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();
        dialog.show();
        ImageView dialogImageView = dialogView.findViewById(R.id.dialogImageView);
        TextView dialogTitle = dialogView.findViewById(R.id.dialogTitle);
        TextView dialogDescription = dialogView.findViewById(R.id.dialogDescription);
        TextView dialogestado = dialogView.findViewById(R.id.estado);
        TextView dialogcodigo = dialogView.findViewById(R.id.codigofuente);



        dialogTitle.setText(proyecto.getNombre_proyecto());
        dialogDescription.setText(proyecto.getDescripcion());
        dialogestado.setText(proyecto.getEstado());
        dialogcodigo.setText(proyecto.getCodigo_fuente());
        Glide.with(this)
                .load(proyecto.getFoto()) // Coloca la URL de la imagen que deseas mostrar
                .into(dialogImageView);
        Button closeButton = dialogView.findViewById(R.id.close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cerrar el cuadro de diálogo
                dialog.dismiss();
            }
        });
        dialog.show();
    }

}