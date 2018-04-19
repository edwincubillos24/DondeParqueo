package com.edwinacubillos.dondeparqueo;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.edwinacubillos.dondeparqueo.Adapters.AdapterParqueaderos;
import com.edwinacubillos.dondeparqueo.modelo.Parqueaderos;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ParqueaderoFragment extends Fragment {

    private ArrayList<Parqueaderos> parqueaderosList;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapterParqueaderos;
    private RecyclerView.LayoutManager layoutManager;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    public ParqueaderoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View view = inflater.inflate(R.layout.fragment_parqueadero, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        parqueaderosList = new ArrayList<>();

        adapterParqueaderos = new AdapterParqueaderos(parqueaderosList, R.layout.cardview_parqueadero,
                getActivity());
        recyclerView.setAdapter(adapterParqueaderos);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        databaseReference.child("parqueaderos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                parqueaderosList.clear();
                if (dataSnapshot.exists()){
                    for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                        Parqueaderos parqueaderos = snapshot.getValue(Parqueaderos.class);
                        parqueaderosList.add(parqueaderos);
                    }
                    adapterParqueaderos.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

         return view;
    }

}
