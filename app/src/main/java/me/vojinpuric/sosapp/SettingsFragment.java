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
            View dialogView = LayoutInflater.from(this.getContext()).inflate(R.layout.dialog, null, false);
            builder.setView(dialogView);
            AlertDialog alertDialog = builder.create();
            ((TextView) dialogView.findViewById(R.id.addTitle)).setText("Add Email address");
            EditText editTextAdd = dialogView.findViewById(R.id.editTextAdd);
            editTextAdd.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            dialogView.findViewById(R.id.button).setOnClickListener(v -> {
                if (!editTextAdd.getText().toString().equals("")) {
                    ArrayList<String> added = MainActivity.getEmails();
                    added.add(editTextAdd.getText().toString());
                    MainActivity.savePreferences(MainActivity.KEY_EMAILS, added);
                }
                editTextAdd.setText("");
                alertDialog.dismiss();
            });
            alertDialog.show();
            return true;
        });

        findPreference("addPhone").setOnPreferenceClickListener(preference -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this.requireContext());
            View dialogView = LayoutInflater.from(this.getContext()).inflate(R.layout.dialog, null, false);
            builder.setView(dialogView);
            AlertDialog alertDialog = builder.create();
            ((TextView) dialogView.findViewById(R.id.addTitle)).setText("Add Phone number");
            EditText editTextAdd = dialogView.findViewById(R.id.editTextAdd);
            editTextAdd.setInputType(InputType.TYPE_CLASS_PHONE);
            dialogView.findViewById(R.id.button).setOnClickListener(v -> {
                if (!editTextAdd.getText().toString().equals("")) {
                    ArrayList<String> added = MainActivity.getPhones();
                    added.add(editTextAdd.getText().toString());
                    MainActivity.savePreferences(MainActivity.KEY_SMS, added);
                }
                editTextAdd.setText("");
                alertDialog.dismiss();
            });
            alertDialog.show();
            return true;
        });
    }


}