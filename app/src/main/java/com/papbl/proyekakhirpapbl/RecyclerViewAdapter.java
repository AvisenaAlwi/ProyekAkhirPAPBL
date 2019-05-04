package com.papbl.proyekakhirpapbl;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.Holder> {

    private ArrayList<ModelProvinsi> data;

    public RecyclerViewAdapter(ArrayList<ModelProvinsi> data){
        this.data = data;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_recyclerview_item, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        String namaProvinsiSaatIni = data.get(position).getNama();
        holder.tvProvinsi.setText(namaProvinsiSaatIni);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class Holder extends RecyclerView.ViewHolder{

        TextView tvProvinsi;

        public Holder(@NonNull View itemView) {
            super(itemView);
            tvProvinsi = itemView.findViewById(R.id.tv_provinsi);
        }
    }
}
