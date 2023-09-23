package com.example.koreanlanguage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.koreanlanguage.adaptors.verbAdaptor;
import com.example.koreanlanguage.adaptors.vocAdaptor;
import com.example.koreanlanguage.models.verb;
import com.example.koreanlanguage.models.voc;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class VerbsActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verbs);

        FirebaseApp.initializeApp(VerbsActivity.this);
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        TextView empty = findViewById(R.id.emptyver);

        findViewById(R.id.addverb).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View view1 = LayoutInflater.from(VerbsActivity.this).inflate(R.layout.add_verb, null);
                TextInputLayout englishLayout, koreanLayout, presentLayout;
                englishLayout = view1.findViewById(R.id.englishLayout);
                koreanLayout = view1.findViewById(R.id.koreanLayout);
                presentLayout = view1.findViewById(R.id.presentLayout);
                TextInputEditText englishEt, koreanEt, presentEt;
                englishEt = view1.findViewById(R.id.englishEt);
                koreanEt = view1.findViewById(R.id.koreanEt);
                presentEt = view1.findViewById(R.id.presentEt);
                AlertDialog alertDialog = new AlertDialog.Builder(VerbsActivity.this)
                        .setTitle("Add")
                        .setView(view1)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (Objects.requireNonNull(englishEt.getText().toString().isEmpty())) {
                                    englishEt.setError("This field is required");
                                } else if (Objects.requireNonNull(koreanEt.getText().toString().isEmpty())) {
                                    koreanEt.setError("This field is required");
                                } else if (Objects.requireNonNull(presentEt.getText().toString().isEmpty())) {
                                    presentEt.setError("This field is required");
                                } else {
                                    ProgressDialog dialog1 = new ProgressDialog(VerbsActivity.this);
                                    dialog1.setMessage("Storing");
                                    dialog1.show();

                                    verb voca = new verb();
                                    voca.setEnglishname(englishEt.getText().toString());
                                    voca.setDictionary(koreanEt.getText().toString());
                                    voca.setPresenttense(presentEt.getText().toString());
                                    database.getReference().child("verbs").push().setValue(voca)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    dialog1.dismiss();
                                                    dialog.dismiss();
                                                    Toast.makeText(VerbsActivity.this, "Saved Successfully", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    dialog1.dismiss();
                                                    Toast.makeText(VerbsActivity.this, "Not saved", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })

                        .create();

                alertDialog.show();
            }
        });


        recyclerView = findViewById(R.id.verbRecycler);

        database.getReference().child("verbs").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<verb> arrayList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    verb vocs = dataSnapshot.getValue(verb.class);
                    Objects.requireNonNull(vocs).setKey(dataSnapshot.getKey());
                    arrayList.add(vocs);
                }

                if (arrayList.isEmpty()) {
                    empty.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    empty.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }

                verbAdaptor adaptor = new verbAdaptor(VerbsActivity.this, arrayList);
                recyclerView.setAdapter(adaptor);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}