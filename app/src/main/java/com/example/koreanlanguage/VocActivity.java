package com.example.koreanlanguage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.koreanlanguage.adaptors.vocAdaptor;
import com.example.koreanlanguage.models.voc;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VocActivity extends AppCompatActivity {

    RecyclerView recyclerView;



    DatabaseReference databaseReference;
    ValueEventListener eventListener;


    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voc);

        FirebaseApp.initializeApp(VocActivity.this);
        FirebaseDatabase database = FirebaseDatabase.getInstance();

//        findViewById(R.id.addwordacti).setVisibility(View.GONE);

        TextView empty = findViewById(R.id.empty);

        findViewById(R.id.addwordacti).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View view1 = LayoutInflater.from(VocActivity.this).inflate(R.layout.add_voc_word, null);
                TextInputLayout englishLayout, koreanLayout;
                englishLayout = view1.findViewById(R.id.englishLayout);
                koreanLayout = view1.findViewById(R.id.koreanLayout);
                TextInputEditText englishEt, koreanEt;
                englishEt = view1.findViewById(R.id.englishEt);
                koreanEt = view1.findViewById(R.id.koreanEt);
                AlertDialog alertDialog = new AlertDialog.Builder(VocActivity.this)
                       .setTitle("Add")
                       .setView(view1)
                       .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                                if (Objects.requireNonNull(englishEt.getText().toString().isEmpty())) {
                                    englishEt.setError("This field is required");
                                } else if (Objects.requireNonNull(koreanEt.getText().toString().isEmpty())) {
                                    koreanEt.setError("This field is required");
                                } else {
                                    ProgressDialog dialog1 = new ProgressDialog(VocActivity.this);
                                    dialog1.setMessage("Storing");
                                    dialog1.show();

                                    voc voca = new voc();
                                    voca.setEnglish(englishEt.getText().toString());
                                    voca.setKorean(koreanEt.getText().toString());
                                    database.getReference().child("vocabulary").push().setValue(voca)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    dialog1.dismiss();
                                                    dialog.dismiss();
                                                    Toast.makeText(VocActivity.this, "Saved Successfully", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    dialog1.dismiss();
                                                    Toast.makeText(VocActivity.this, "Not saved", Toast.LENGTH_SHORT).show();
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


        recyclerView = findViewById(R.id.vocRecycler);

        database.getReference().child("vocabulary").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<voc> arrayList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    voc vocs = dataSnapshot.getValue(voc.class);
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

                vocAdaptor adaptor = new vocAdaptor(VocActivity.this, arrayList);
                recyclerView.setAdapter(adaptor);

                adaptor.setOnItemClickListener(new vocAdaptor.OnItemClickListener() {
                    @Override
                    public void onClick(voc voca) {
                        View view = LayoutInflater.from(VocActivity.this).inflate(R.layout.add_voc_word, null);
                        TextInputLayout englishLayout, koreanLayout;
                        TextInputEditText englishEt, koreanEt;

                        englishEt = view.findViewById(R.id.englishEt);
                        koreanEt = view.findViewById(R.id.koreanEt);
                        englishLayout = view.findViewById(R.id.englishLayout);
                        koreanLayout = view.findViewById(R.id.koreanLayout);

                        englishEt.setText(voca.getEnglish());
                        koreanEt.setText(voca.getKorean());

                        ProgressDialog progressDialog = new ProgressDialog(VocActivity.this);

                        AlertDialog alertDialog = new AlertDialog.Builder(VocActivity.this)
                                .setTitle("Edit")
                                .setView(view)
                                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (Objects.requireNonNull(englishEt.getText().toString().isEmpty())) {
                                            englishEt.setError("This field is required");
                                        } else if (Objects.requireNonNull(koreanEt.getText().toString().isEmpty())) {
                                            koreanEt.setError("This field is required");
                                        } else {

                                            progressDialog.setMessage("Saving");
                                            progressDialog.show();

                                            voc voca = new voc();
                                            voca.setEnglish(englishEt.getText().toString());
                                            voca.setKorean(koreanEt.getText().toString());
                                            database.getReference().child("vocabulary").child(voca.getKey()).setValue(voca)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            progressDialog.dismiss();
                                                            dialog.dismiss();
                                                            Toast.makeText(VocActivity.this, "Saved Successfully", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            progressDialog.dismiss();
                                                            Toast.makeText(VocActivity.this, "Not saved", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    }
                                }).setNeutralButton("Close", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        progressDialog.setTitle("Deleting...");
                                        progressDialog.show();
                                        database.getReference().child("vocabulary").child(voca.getKey()).removeValue()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(VocActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        progressDialog.dismiss();

                                                    }
                                                });
                                    }
                                }).create();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}