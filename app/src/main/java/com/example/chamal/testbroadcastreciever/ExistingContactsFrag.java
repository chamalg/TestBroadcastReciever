package com.example.chamal.testbroadcastreciever;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SearchViewCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chamal.testbroadcastreciever.Adapter.MyCustomAdapter;
import com.example.chamal.testbroadcastreciever.Model.Contact;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ExistingContactsFrag.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ExistingContactsFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExistingContactsFrag extends Fragment implements SearchViewCompat.OnQueryTextListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final int REQUEST_CODE = 101;

    ListView listView;
    TextView tvSelectedVideo;
    ArrayList<String> m_arrList;
    MyCustomAdapter customAdapter;
    private boolean switchStatus;
    SharedPreferences preferences;
    String selectedContact;
    Realm realm;
    View emptyView;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ExistingContactsFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ExistingContactsFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static ExistingContactsFrag newInstance(String param1, String param2) {
        ExistingContactsFrag fragment = new ExistingContactsFrag();
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
        final View v =  inflater.inflate(R.layout.fragment_existing_contacts, container, false);


        RealmConfiguration config = new RealmConfiguration.Builder(v.getContext()).build();
        Realm.setDefaultConfiguration(config);
        realm = Realm.getDefaultInstance();
        tvSelectedVideo = (TextView)v.findViewById(R.id.tvVideo);



        listView = (ListView) v.findViewById(R.id.lvContacts);
        m_arrList = new ArrayList<>();
        m_arrList = getContactList();
        customAdapter = new MyCustomAdapter(v.getContext(), m_arrList);
        emptyView = v.findViewById(R.id.textViewEmpty);
        listView.setEmptyView(emptyView);
        listView.setAdapter(customAdapter);
        listView.setTextFilterEnabled(true);

        mListener.onFragmentInteraction("fff");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                final Dialog dialog = new Dialog(v.getContext());
                dialog.setContentView(R.layout.dialog_layout);
                ListView lvUserChoices = (ListView) dialog.findViewById(R.id.lvVideoList);
                lvUserChoices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        switch (position) {
                            case 0:
                                selectedContact = m_arrList.get(position).toString();
                                Intent mediaChooser = new Intent(Intent.ACTION_GET_CONTENT);
                                mediaChooser.setType("video/*, images/*");
                                startActivityForResult(mediaChooser, REQUEST_CODE);
                                dialog.dismiss();
                                break;
                            case 1:
                                Intent i = new Intent(v.getContext(), VideoGalleryActivity.class);
                                startActivity(i);
                                dialog.dismiss();
                                break;
                        }

                    }
                });
                dialog.show();

            }
        });

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
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

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (TextUtils.isEmpty(newText)) {
            listView.clearTextFilter();
        } else {
            listView.setFilterText(newText);
        }
        return true;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(String str);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri selectedVideoLocation = data.getData();


                Contact c = new Contact();
                Object obj = realm.where(Contact.class).contains("contactName", selectedContact).findFirst();
                if (obj == null) {
                    c.setId(getNextKey());
                    c.setVideoId(selectedVideoLocation.toString());
                    c.setContactName(selectedContact);

                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(c);
                    realm.commitTransaction();

                    Toast.makeText(getActivity(), "Insert successful.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public ArrayList<String> getContactList() {

        ArrayList<String> contactList = new ArrayList<String>();
        Cursor phones = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.Contacts.DISPLAY_NAME);
        while (phones.moveToNext()) {
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            contactList.add(name + " , " + phoneNumber);

        }
        phones.close();

        return contactList;
    }

    public int getNextKey() {
        return (int) (realm.where(Contact.class).maximumInt("id") + 1);
    }

}
