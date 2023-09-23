package com.example.koreanlanguage.adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.koreanlanguage.R;
import com.example.koreanlanguage.models.verb;
import com.example.koreanlanguage.models.voc;

import java.util.ArrayList;

public class verbAdaptor extends RecyclerView.Adapter<verbAdaptor.ViewHolder>{

    Context context;
    ArrayList<verb> arrayList;
    vocAdaptor.OnItemClickListener onItemClickListener;

    public verbAdaptor(Context context, ArrayList<verb> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public verbAdaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.verbs_recycler_item, parent, false);
        return new verbAdaptor.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull verbAdaptor.ViewHolder holder, int position) {
        holder.english.setText(arrayList.get(position).getEnglishname());
        holder.dictionary.setText(arrayList.get(position).getDictionary());
        holder.present.setText(arrayList.get(position).getPresenttense());

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView english, dictionary, present;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            english = itemView.findViewById(R.id.eng_item);
            dictionary = itemView.findViewById(R.id.dic_item);
            present = itemView.findViewById(R.id.present_item);
        }
    }


}
