package com.example.chamal.testbroadcastreciever;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;

import com.example.chamal.testbroadcastreciever.Adapter.CustomGridAdapter;
import com.example.chamal.testbroadcastreciever.Model.Contact;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;


public class FavouriteContactsFrag extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Realm realm;
    CustomGridAdapter customGridAdapter;
    ArrayList<String> list;
    Context context;

    private OnFragmentInteractionListener mListener;

    public FavouriteContactsFrag() {
        // Required empty public constructor
    }

    public static FavouriteContactsFrag newInstance(String param1, String param2) {
        FavouriteContactsFrag fragment = new FavouriteContactsFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_favourite_contacts, container, false);

        context = getActivity().getApplicationContext();
        RealmConfiguration config = new RealmConfiguration.Builder(context).build();
        Realm.setDefaultConfiguration(config);
        realm = Realm.getDefaultInstance();

        RealmResults<Contact> results = realm.where(Contact.class).findAll();
        list = new ArrayList<String>(results.size());

        for (int i = 0; i < results.size(); i++) {
            list.add(results.get(i).getContactName());

            mListener.onFragmentInteraction("fff");

        }

        customGridAdapter = new CustomGridAdapter(v.getContext(), list);
        GridView gridFav = (GridView) v.findViewById(R.id.grid_fav_contacts);
        gridFav.setAdapter(customGridAdapter);
        return v;
    }

    public void onButtonPressed(String str) {
        if (mListener != null) {
            mListener.onFragmentInteraction(str);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String str);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}
