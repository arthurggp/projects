package com.example.gatekeeper.utils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.gatekeeper.R;
import com.example.gatekeeper.models.ConvidadoModel;

import java.util.ArrayList;
import java.util.List;

public class AutoCompleteAdapter extends ArrayAdapter<String> {

    private List<String> convidadosFull;

    public AutoCompleteAdapter(@NonNull Context context, @NonNull List<String> convidados) {
        super(context, 0,convidados);

        convidadosFull = new ArrayList<String>();

        convidadosFull.addAll(convidados);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return super.getFilter();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.listview_item, parent, false
            );
        }

        TextView textView = convertView.findViewById(R.id.nome);
        String convidadoModel = getItem(position);

        String nome = convidadoModel;

        if (convidadoModel != null) {
            textView.setText(nome);
        }

        return convertView;
    }

    final private Filter convidadoFilter = new Filter() {
        @Override
        public FilterResults performFiltering(CharSequence charSequence) {

            FilterResults results = new FilterResults();
            List<String> suggestions = new ArrayList<>();

            if (charSequence == null || charSequence.length() == 0) {
                suggestions.addAll(convidadosFull);
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for (String item : convidadosFull) {
                    if (item.toLowerCase().contains(filterPattern)) {
                        suggestions.add(item);
                    }
                }
            }

            results.values = suggestions;
            results.count = suggestions.size();

            return results;
        }


        @Override
        protected void publishResults(CharSequence charSequence, FilterResults results) {
            clear();
            addAll((List) results.values);
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            Log.i("RETORNO: ", ((ConvidadoModel) resultValue).getNome());
            return ((ConvidadoModel) resultValue).getNome();
        }

    };

}