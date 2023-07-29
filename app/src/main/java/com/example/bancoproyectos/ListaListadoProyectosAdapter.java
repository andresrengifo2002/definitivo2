package com.example.bancoproyectos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class ListaListadoProyectosAdapter extends RecyclerView.Adapter<ListaListadoProyectosAdapter.ViewHolder> {
    private RecyclerView recyclerView;
    private  Listadoproyectos listadoproyectos;
    private ArrayList<Listadoproyectos> dataset;
    private Context context;

    public class ViewHolder extends  RecyclerView.ViewHolder {

        private TextView title;
        private ImageView image;


        private TextView verMasTextView;
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            title=itemView.findViewById(R.id.title);
            image=itemView.findViewById(R.id.image);


            verMasTextView = itemView.findViewById(R.id.verMasTextView);
        }

    }

    public ListaListadoProyectosAdapter (Context context){
        this.context = context;
        dataset = new ArrayList<>();
    }

    @Override
    public ListaListadoProyectosAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_row,parent,false);
        return new ListaListadoProyectosAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Listadoproyectos p=dataset.get(position);
        holder.title.setText(p.getNombre_proyecto());



        String url ="";

        Glide.with(context)
                .load(p.getFoto())
                .into(holder.image);
        holder.verMasTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Listadoproyectos proyecto = dataset.get(position);
                // Llamar a la función en la actividad principal para mostrar el cuadro de diálogo
                ((MainActivitylistado) context).mostrarCuadroDialogo(proyecto);
            }
        });
    }

    @Override
    public int getItemCount() { return dataset.size();}

    public void add(ArrayList<Listadoproyectos>listaproyectos) {
        dataset.addAll( listaproyectos);
        notifyDataSetChanged();
    }
    public void setProyectos(List<Listadoproyectos> proyectos) {
        dataset.clear();
        dataset.addAll(proyectos);
        notifyDataSetChanged();
    }
}

