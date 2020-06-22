package me.vojinpuric.sosapp;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.regex.Pattern;


public class MainFragment extends Fragment {

    private RecyclerView recyclerEmails;
    private RecyclerView recyclerPhones;
    private EditText enterEmail;
    private EditText enterPhone;
    private MainActivity activity;
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
//        enterEmail = view.findViewById(R.id.enter_email);
//        enterPhone = view.findViewById(R.id.enter_phone);

        activity = (MainActivity) getActivity();
        emails = activity.getEmails();
        phones = activity.getPhones();
        LocationService gps = ((MainActivity) activity).getGps();

        createAdapter(recyclerPhones, phones);
        createAdapter(recyclerEmails, emails);
//        view.findViewById(R.id.add_email).setOnClickListener(v -> addEmail());
//        view.findViewById(R.id.add_phone).setOnClickListener(v -> addPhone());
        view.findViewById(R.id.sendAll).setOnClickListener(v -> {

            gps.getLocation();

            //            for (String p : phones) {
//                (activity).sendSms(p, String.format("https://www.google.com/maps/place/%s+%s", "44.816472", "20.460111"));
//            }
        });

    }

    private void createAdapter(RecyclerView recyclerView, ArrayList<String> list) {
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);

        MyAdapter myAdapter = new MyAdapter(list);
        recyclerView.setAdapter(myAdapter);

    }

    private void addEmail() {
        String email = enterEmail.getText().toString();
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        if ((!email.equals("")) && pattern.matcher(email).matches()) {
            emails.add(email);
        }
        if (recyclerEmails.getAdapter() != null) {
            recyclerEmails.getAdapter().notifyDataSetChanged();
        }
        MainActivity.savePreferences(MainActivity.KEY_EMAILS, emails);
        enterEmail.setText("");
    }

    private void addPhone() {
        String phone = enterPhone.getText().toString();
        String regex = "[0-9]+";
        Pattern pattern = Pattern.compile(regex);
        if ((!phone.equals("")) && pattern.matcher(phone).matches()) {
            phones.add(phone);
        }
        if (recyclerPhones.getAdapter() != null) {
            recyclerPhones.getAdapter().notifyDataSetChanged();
        }
        MainActivity.savePreferences(MainActivity.KEY_SMS, phones);
        enterPhone.setText("");
    }

    public static MainFragment newInstance() {
        return new MainFragment();
    }
}
//Swipe for Delete
//    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
//        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
//            return true;
//        }
//
//        @Override
//        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
//            myAdapter.notifyDataSetChanged();
//        }
//    };
//    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
//        itemTouchHelper.attachToRecyclerView(recyclerView);
//fali update preferences, arraylista