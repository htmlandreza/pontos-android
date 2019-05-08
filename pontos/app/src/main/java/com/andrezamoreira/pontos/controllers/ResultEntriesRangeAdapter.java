package com.andrezamoreira.pontos.controllers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.andrezamoreira.pontos.R;
import com.andrezamoreira.pontos.models.TimeEntries;

import java.util.ArrayList;
import java.util.List;

public class ResultEntriesRangeAdapter extends  RecyclerView.Adapter<ResultEntriesRangeAdapter.ResultViewHolder>{

    private List<TimeEntries> timeEntries;
    ResultEntriesRangeAdapter(List<TimeEntries> lista){
        timeEntries = lista;
    }

    @Override
    public ResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_result, parent, false);
        return new ResultViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ResultViewHolder holder, int position) {
        TimeEntries t = timeEntries.get(position);
        holder.descricaoUsuario.setText(t.getDescription());
        holder.dataInicialUsuario.setText(t.getTimeInterval().getStart());
        holder.dataFinalUsuario.setText(t.getTimeInterval().getEnd());
        holder.duracaoUsuario.setText(t.getTimeInterval().getDuration());

    }

    @Override
    public int getItemCount() {
        return timeEntries.size();
    }

    static class ResultViewHolder extends RecyclerView.ViewHolder{

        TextView dataInicialUsuario;
        TextView dataFinalUsuario;
        TextView descricaoUsuario;
        TextView duracaoUsuario;



        ResultViewHolder(View v){
            super(v);

            dataInicialUsuario = v.findViewById(R.id.dataInicialTextView);
            dataFinalUsuario = v.findViewById(R.id.dataFinalTextView);
            descricaoUsuario = v.findViewById(R.id.descricaoTextView);
            duracaoUsuario = v.findViewById(R.id.duracaoTextView);



        }
//        dataInicialUsuario.setText(timeEntries.getTimeInterval().getStart());
//        dataFinalUsuario.setText(timeEntries.getTimeInterval().getEnd());
//        descricaoUsuario.setText(timeEntries.getDescription());
//        duracaoUsuario.setText(timeEntries.getTimeInterval().getDuration());

    }

}

