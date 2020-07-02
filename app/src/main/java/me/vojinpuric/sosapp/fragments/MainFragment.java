package me.vojinpuric.sosapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import me.vojinpuric.sosapp.MainActivity;
import me.vojinpuric.sosapp.helpers.MyAdapter;
import me.vojinpuric.sosapp.R;
import me.vojinpuric.sosapp.service.LocationService;


public class MainFragment extends Fragment {

    private RecyclerView recyclerEmails;
    private RecyclerView recyclerPhones;
    private ArrayList<String> phones;
    private ArrayList<String> emails;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerEmails = view.findViewById(R.id.recycler_emails);
        recyclerPhones = view.findViewById(R.id.recycler_phones);
        emails = MainActivity.getEmails();
        phones = MainActivity.getPhones();
        createAdapter(recyclerPhones, phones);
        createAdapter(recyclerEmails, emails);
        view.findViewById(R.id.sendAll).setOnClickListener(v -> {
            Intent serviceIntent = ((MainActivity) getActivity()).getTrackingServiceIntent();
            serviceIntent.putStringArrayListExtra(LocationService.KEY_SMS, MainActivity.getPhones());
            serviceIntent.putStringArrayListExtra(LocationService.KEY_EMAILS, MainActivity.getEmails());
            getContext().startService(serviceIntent);
        });

    }

    private void createAdapter(RecyclerView recyclerView, ArrayList<String> list) {
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);

        MyAdapter myAdapter = new MyAdapter(list);
        recyclerView.setAdapter(myAdapter);

    }

    public static MainFragment newInstance() {
        return new MainFragment();
    }
}