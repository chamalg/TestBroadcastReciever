package com.example.chamal.testbroadcastreciever.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chamal.testbroadcastreciever.Model.Contact;
import com.example.chamal.testbroadcastreciever.Sqlite.MyDatabaseAdapter;
import com.example.chamal.testbroadcastreciever.R;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;

import static com.example.chamal.testbroadcastreciever.R.array.testEntries;

/**
 * Created by Chamal on 11/1/2016.
 */

public class MyCustomAdapter extends BaseAdapter implements Filterable {
    private Context context;
    private LayoutInflater inflater;
    ArrayList<String> listArray;
    ArrayList<String> orig;
    String[] testArray;
    String selection, contactName;
    MyDatabaseAdapter adapter;
    Realm realm;

    public MyCustomAdapter(Context context, ArrayList<String> listArray) {
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.listArray = listArray;
        testArray = context.getResources().getStringArray(testEntries);
        adapter = new MyDatabaseAdapter(context);
        RealmConfiguration config = new RealmConfiguration.Builder(context).build();
        Realm.setDefaultConfiguration(config);
        realm = Realm.getDefaultInstance();
    }

    @Override
    public int getCount() {
        return listArray.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {

        Holder holder = null;
        View rowView = convertView;
        if (rowView == null) {
            holder = new Holder();
            rowView = inflater.inflate(R.layout.row_layout_item, null);
            holder.tvName = (TextView) rowView.findViewById(R.id.tvName);
            holder.tvVideoId = (TextView) rowView.findViewById(R.id.tvVideo);
            holder.ivContact = (ImageView) rowView.findViewById(R.id.ivContact);

            rowView.setTag(holder);
        } else {
            holder = (Holder) rowView.getTag();
        }
        holder.tvName.setText(listArray.get(position));

      try {

          Contact c = realm.where(Contact.class).equalTo("contactName", listArray.get(position)).findFirst();


          String vidName = c.getVideoId();

          if(vidName.length()<3) {
              holder.tvVideoId.setText("Null");
          }else{
              holder.tvVideoId.setText(vidName);
          }

      }catch(Exception e){
          e.printStackTrace();
      }


       /* rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog_layout);
                dialog.setCancelable(true);
                Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
                Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
                ListView lvList = (ListView) dialog.findViewById(R.id.lvVideoList);

                lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                       *//* Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                        context.startActivity(intent);*//*
                        selection = testArray[position];
                    }
                });

                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        contactName = listArray.get(position).toString().replace(" ", "").replace("-", "").split("(?<=\\D)(?=\\d)")[1];

                        Contact c = new Contact();
                        c.setId(getNextKey());
                        c.setVideoId(selection);
                        c.setContactName(contactName);

                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(c);
                        realm.commitTransaction();

                        Toast.makeText(context, "Insert successful.", Toast.LENGTH_SHORT).show();

                      *//*  long id = adapter.insertData(listArray.get(position).toString().replace(" ", "").replace("-","").split("(?<=\\D)(?=\\d)")[1], selection);
                        if(id<0){
                            Toast.makeText(context, "Error inserting!", Toast.LENGTH_SHORT).show();
                            System.out.println("Error inserting!");
                        }else{
                            Toast.makeText(context, "Insert successful.", Toast.LENGTH_SHORT).show();
                            System.out.println("Insert successful:"+listArray.get(position).toString().replace(" ", "").replace("-","").split("(?<=\\D)(?=\\d)")[1]);
                        }*//*
                        dialog.dismiss();
                    }
                });

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });*/

        return rowView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final ArrayList<String> results = new ArrayList<String>();
                if (orig == null)
                    orig = listArray;
                if (constraint != null) {
                    if (orig != null && orig.size() > 0) {
                        for (int i = 0; i < orig.size(); i++) {
                            if (orig.get(i).toLowerCase()
                                    .contains(constraint.toString()))
                                results.add(orig.get(i));
                        }
                    }
                    oReturn.values = results;
                }
                return oReturn;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                listArray = (ArrayList<String>) results.values;
                notifyDataSetChanged();

            }
        };
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    private class Holder {

        TextView tvName;
        TextView tvVideoId;
        ImageView ivContact;
    }

}
