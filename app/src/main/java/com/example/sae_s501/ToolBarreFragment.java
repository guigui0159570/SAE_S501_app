package com.example.sae_s501;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.sae_s501.activity.FilActuActivity;
import com.example.sae_s501.activity.MesPublicationsActivity;
import com.example.sae_s501.activity.MyCompteActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ToolBarreFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ToolBarreFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ToolBarreFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ToolBarreFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ToolBarreFragment newInstance(String param1, String param2) {
        ToolBarreFragment fragment = new ToolBarreFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_tool_barre, container, false);
        ImageButton home = rootView.findViewById(R.id.homeBouton);
        ImageButton profil = rootView.findViewById(R.id.profilBouton);
        ImageButton myPublication = rootView.findViewById(R.id.myPublicationBouton);

        myPublication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mespub = new Intent(view.getContext(), MesPublicationsActivity.class);
                startActivity(mespub);
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent homeview = new Intent(view.getContext(), FilActuActivity.class);
                startActivity(homeview);
            }
        });

        profil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profilview = new Intent(view.getContext(), MyCompteActivity.class);
                startActivity(profilview);
            }
        });
        return rootView;
    }
}