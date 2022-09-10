package com.example.universities.uniList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.universities.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for filtering list
 */
public class UniversityAdapter extends ArrayAdapter<University> implements Filterable {
    private Context context;
    private int resource;
    private List<University> listUniversities;
    private List<University> listFilteredUniversities;

    public UniversityAdapter(@NonNull Context context, int resource, @NonNull ArrayList<University> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.listUniversities = objects;
        this.listFilteredUniversities = objects;
    }

    @Override
    public int getCount() {
        return listFilteredUniversities.size();
    }

    @Override
    public University getItem(int position) {
        return listFilteredUniversities.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        convertView = layoutInflater.inflate(resource, parent, false);

        ImageView imageView = convertView.findViewById(R.id.imageListView);
        TextView txtName = convertView.findViewById(R.id.txtNameListView);
        TextView txtDes = convertView.findViewById(R.id.txtDesListView);

        loadImageFromUrl(getItem(position).getImage(), imageView);
        txtName.setText(getItem(position).getName());

        StringBuilder sb = new StringBuilder();
        sb.append("Тел. ");
        sb.append(getItem(position).getDes());
        txtDes.setText(sb.toString());


        return convertView;
    }

    private void loadImageFromUrl(String url, ImageView iv) {
        Picasso.get().load(url).resize(1024,767).error(R.drawable.no_photo3).into(iv);
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults filterResults = new FilterResults();
                if(constraint == null || constraint.length() == 0){
                    filterResults.count = listUniversities.size();
                    filterResults.values = listUniversities;

                }else{
                    List<University> resultsModel = new ArrayList<>();
                    String searchStr = constraint.toString().toLowerCase();

                    for(University itemsModel:listUniversities){
                        if(itemsModel.getName().contains(searchStr)){
                            resultsModel.add(itemsModel);

                        }
                        filterResults.count = resultsModel.size();
                        filterResults.values = resultsModel;
                    }


                }

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                listFilteredUniversities = (List<University>) results.values;
                notifyDataSetChanged();

            }
        };
        return filter;
    }
}
