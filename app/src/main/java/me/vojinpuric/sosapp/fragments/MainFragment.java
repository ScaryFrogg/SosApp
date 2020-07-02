package me.vojinpuric.sosapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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

    private ArrayList<String> phones;
    private ArrayList<String> emails;
    private Button sendBtn;
    private TextView tvNoContacts;

    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerEmails = view.findViewById(R.id.recycler_emails);
        RecyclerView recyclerPhones = view.findViewById(R.id.recycler_phones);
        sendBtn = view.findViewById(R.id.sendAll);
        tvNoContacts = view.findViewById(R.id.tvNoContacts);
        emails = MainActivity.getEmails();
        phones = MainActivity.getPhones();
        createAdapter(recyclerPhones, phones);
        createAdapter(recyclerEmails, emails);

        sendBtn.setOnClickListener(v -> {
            Intent serviceIntent = ((MainActivity) getActivity()).getTrackingServiceIntent();
            serviceIntent.putStringArrayListExtra(LocationService.KEY_SMS, MainActivity.getPhones());
            serviceIntent.putStringArrayListExtra(LocationService.KEY_EMAILS, MainActivity.getEmails());
            getContext().startService(serviceIntent);
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        if (phones.isEmpty() && emails.isEmpty()) {
            tvNoContacts.setVisibility(View.VISIBLE);
            sendBtn.setEnabled(false);
        } else {
            tvNoContacts.setVisibility(View.GONE);
            sendBtn.setEnabled(true);
        }
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