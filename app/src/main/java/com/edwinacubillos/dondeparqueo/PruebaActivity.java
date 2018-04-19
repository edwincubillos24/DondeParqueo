package com.edwinacubillos.dondeparqueo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.edwinacubillos.dondeparqueo.modelo.Usuarios;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class PruebaActivity extends AppCompatActivity {

    private EditText eNombre, eEdad, eTelefono;
    private ListView listView;
    private CircleImageView iFoto;

    private ArrayAdapter listAdapter;
    private ArrayList<String> listNombres;
    private ArrayList<Usuarios> listUsuarios;

    private Bitmap bitmap;
    private String urlFoto = "No ha cargado";

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prueba);

        FirebaseDatabase.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference(); //dondeparqueo-33a53

        eNombre = findViewById(R.id.eNombre);
        eEdad = findViewById(R.id.eEdad);
        eTelefono = findViewById(R.id.eTelefono);
        listView = findViewById(R.id.listView);
        iFoto = findViewById(R.id.iFoto);


        listNombres = new ArrayList<String>();
        listUsuarios = new ArrayList<Usuarios>();

        final UsuarioAdapter usuarioAdapter = new UsuarioAdapter(this, listUsuarios);

       /* listAdapter = new ArrayAdapter(this, //listView simple
                android.R.layout.simple_list_item_1,
                listNombres);

        listView.setAdapter(listAdapter);*/

        listView.setAdapter(usuarioAdapter);

        databaseReference.child("usuarios").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listNombres.clear();
                listUsuarios.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Log.d("data: ", snapshot.toString());
                        Usuarios usuarios = snapshot.getValue(Usuarios.class);
                        listNombres.add(usuarios.getNombre());
                        listUsuarios.add(usuarios);
                    }
                    usuarioAdapter.notifyDataSetChanged();
                }
                // listAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("load", databaseError.getMessage());

            }

        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String uid = listUsuarios.get(position).getId();
                databaseReference.child("usuarios").child(uid).removeValue();
                listNombres.remove(position);
                listUsuarios.remove(position);
                return false;
            }
        });
    }

    public void fotoClicked(View view) {
        Intent fotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        fotoIntent.setType("image/*");
        startActivityForResult(fotoIntent, 1234);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1234 && resultCode == RESULT_OK) {
            if (data == null) {
                Toast.makeText(this, "ERROR CARGANDO FOTO", Toast.LENGTH_SHORT).show();
            } else {
                Uri imagen = data.getData();

                try {
                    InputStream is = getContentResolver().openInputStream(imagen);
                    BufferedInputStream bis = new BufferedInputStream(is);
                    bitmap = BitmapFactory.decodeStream(bis);

                    iFoto.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void guardarClicked(View view) {

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference();

        ByteArrayOutputStream baos = new ByteArrayOutputStream(); //comprimir
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        storageReference.child("usuariosFotos").child(databaseReference.push().getKey()).
                putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                urlFoto = taskSnapshot.getDownloadUrl().toString();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("error", e.getMessage().toString());
            }
        });


        Usuarios usuarios = new Usuarios(databaseReference.push().getKey(),
                eNombre.getText().toString(),
                eTelefono.getText().toString(),
                urlFoto,
                Integer.valueOf(eEdad.getText().toString()));

        databaseReference.child("usuarios").child(usuarios.getId()).setValue(usuarios).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("Entre1", "OK");

                } else {
                    Log.d("Entre2", "OK");
                    Log.d("Save:", task.getException().toString());
                }
            }
        });


    }

    class UsuarioAdapter extends ArrayAdapter<Usuarios> {

        public UsuarioAdapter(@NonNull Context context, ArrayList<Usuarios> data) {
            super(context, R.layout.list_item, data);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            LayoutInflater inflater = LayoutInflater.from(getContext());
            View item = inflater.inflate(R.layout.list_item, null);

            Usuarios usuario = getItem(position);

            TextView nombre = item.findViewById(R.id.tNombre); //faltaba el item.
            nombre.setText(usuario.getNombre());

            TextView telefono = item.findViewById(R.id.tTelefono);
            telefono.setText(usuario.getTelefono());

            TextView edad = item.findViewById(R.id.tEdad);
            edad.setText(String.valueOf(usuario.getEdad()));

            CircleImageView iFoto = item.findViewById(R.id.iFoto);
            Picasso.get().load(usuario.getFoto()).into(iFoto);

            return item;
        }
    }
}
