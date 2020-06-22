package me.vojinpuric.sosapp;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.PreferenceFragmentCompat;


import java.util.ArrayList;

import static me.vojinpuric.sosapp.MainActivity.readPreferences;

public class SettingsFragment extends PreferenceFragmentCompat {

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        findPreference("addEmail").setOnPreferenceClickListener(preference -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this.requireContext());
            View dialogView = LayoutInflater.from(this.getContext()).inflate(R.layout.dialog,null, false);
            builder.setView(dialogView);
            ((TextView)dialogView.findViewById(R.id.addTitle)).setText("Add Email address");
            EditText editTextAdd =dialogView.findViewById(R.id.editTextAdd);
            editTextAdd.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            dialogView.findViewById(R.id.button).setOnClickListener(v->{
                if(!editTextAdd.getText().toString().equals("")) {
                    ArrayList<String> added = readPreferences(MainActivity.KEY_EMAILS);
                    added.add(editTextAdd.getText().toString());
                    MainActivity.savePreferences(MainActivity.KEY_EMAILS,added);
                }
                editTextAdd.setText("");
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            return true;
        });


        findPreference("addPhone").setOnPreferenceClickListener(preference -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this.requireContext());
            View dialogView = LayoutInflater.from(this.getContext()).inflate(R.layout.dialog,null, false);
            builder.setView(dialogView);
            ((TextView)dialogView.findViewById(R.id.addTitle)).setText("Add Phone number");
            EditText editTextAdd =dialogView.findViewById(R.id.editTextAdd);
            editTextAdd.setInputType(InputType.TYPE_CLASS_PHONE);
            dialogView.findViewById(R.id.button).setOnClickListener(v->{

                if(!editTextAdd.getText().toString().equals("")) {
                    ArrayList<String> added = readPreferences(MainActivity.KEY_SMS);
                    added.add(editTextAdd.getText().toString());
                    MainActivity.savePreferences(MainActivity.KEY_SMS,added);
                }
                editTextAdd.setText("");

            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            return true;
        });
    }


}