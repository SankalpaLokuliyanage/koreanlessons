package com.example.koreanlanguage.adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.koreanlanguage.R;
import com.example.koreanlanguage.models.voc;

import java.util.ArrayList;

public class vocAdaptor extends RecyclerView.Adapter<vocAdaptor.ViewHolder> {

    Context context;
    ArrayList<voc> arrayList;
    OnItemClickListener onItemClickListener;

    public vocAdaptor(Context context, ArrayList<voc> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public vocAdaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.voc_recycler_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull vocAdaptor.ViewHolder holder, int position) {
        holder.english.setText(arrayList.get(position).getEnglish());
        holder.korean.setText(arrayList.get(position).getKorean());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onClick(arrayList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView english, korean;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            english = itemView.findViewById(R.id.english_item);
            korean = itemView.findViewById(R.id.korean_item);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onClick(voc voca);
    }
}
