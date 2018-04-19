package com.edwinacubillos.dondeparqueo.Adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.edwinacubillos.dondeparqueo.R;
import com.edwinacubillos.dondeparqueo.modelo.Parqueaderos;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterParqueaderos extends RecyclerView.Adapter<AdapterParqueaderos.ParqueaderoViewHolder>{

    private ArrayList<Parqueaderos> parqueaderosList;
    private int resource;
    private Activity activity;

    public AdapterParqueaderos(ArrayList<Parqueaderos> parqueaderosList) {
        this.parqueaderosList = parqueaderosList;
    }

    public AdapterParqueaderos(ArrayList<Parqueaderos> parqueaderosList, int resource, Activity activity) {
        this.parqueaderosList = parqueaderosList;
        this.resource = resource;
        this.activity = activity;
    }

    @Override
    public ParqueaderoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, "Abre actividad con detalle", Toast.LENGTH_SHORT).show();
            }
        });

        return new ParqueaderoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ParqueaderoViewHolder holder, int position) {
        Parqueaderos parqueadero = parqueaderosList.get(position);
        holder.bindParqueadero(parqueadero, activity);
    }

    @Override
    public int getItemCount() {
        return parqueaderosList.size();
    }

    public class ParqueaderoViewHolder extends RecyclerView.ViewHolder{

        private TextView tNombre, tPrecio;
        private CircleImageView iFoto;

        public ParqueaderoViewHolder(View itemView) {
            super(itemView);
            tNombre = itemView.findViewById(R.id.tNombre);
            tPrecio = itemView.findViewById(R.id.tPrecio);
            iFoto = itemView.findViewById(R.id.iFoto);
        }

        public void bindParqueadero(Parqueaderos parqueadero, Activity activity){
            tNombre.setText(parqueadero.getNombre());
            tPrecio.setText(parqueadero.getValor());
            Picasso.get().load(parqueadero.getFoto()).into(iFoto);
        }
    }

}
