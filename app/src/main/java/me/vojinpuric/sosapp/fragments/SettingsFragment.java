package me.vojinpuric.sosapp.fragments;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.preference.PreferenceFragmentCompat;

import java.util.ArrayList;
import java.util.regex.Pattern;

import me.vojinpuric.sosapp.MainActivity;
import me.vojinpuric.sosapp.R;


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
                String email = editTextAdd.getText().toString();
                if (!email.equals("")) {
                    ArrayList<String> added = MainActivity.getEmails();
                    String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
                    Pattern pattern = Pattern.compile(regex);
                    if (pattern.matcher(email).matches()) {
                        added.add(email);
                    } else {
                        Toast.makeText(getContext(), "Please enter valid email address", Toast.LENGTH_LONG).show();
                    }
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
        findPreference("removeContact").setOnPreferenceClickListener(preference -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this.requireContext());
            View dialogView = LayoutInflater.from(this.getContext()).inflate(R.layout.dialog, null, false);
            builder.setView(dialogView);
            AlertDialog alertDialog = builder.create();
            ((TextView) dialogView.findViewById(R.id.addTitle)).setText("Remove");
            EditText editTextAdd = dialogView.findViewById(R.id.editTextAdd);
            Button button = dialogView.findViewById(R.id.button);
            button.setText("Remove");
            button.setOnClickListener(v -> {
                String text =editTextAdd.getText().toString();
                if (!text.equals("")) {
                    ArrayList<String> phones = MainActivity.getPhones();
                    ArrayList<String> emails = MainActivity.getEmails();
                    ArrayList<String> phonesCopy = phones;
                    for (String phone:phones) {
                        if(phone.equals(text)){
                            phonesCopy.remove(phone);
                        }
                    }
                    ArrayList<String> emailsCopy = emails;
                    for (String email:emails) {
                        if(email.equals(text)){
                            emails.remove(email);
                        }
                    }
                    MainActivity.savePreferences(MainActivity.KEY_EMAILS, emailsCopy);
                    MainActivity.savePreferences(MainActivity.KEY_SMS, phonesCopy);
                }
                editTextAdd.setText("");
                alertDialog.dismiss();
            });
            alertDialog.show();
            return true;
        });

    }


}