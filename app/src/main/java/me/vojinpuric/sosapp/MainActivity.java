package me.vojinpuric.sosapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private static SmsManager smsManager;
    private static final String SHARED_PREFS_FILE = "shared_preferences";
    public static final String KEY_EMAILS = "preferences_emails_key";
    public static final String KEY_SMS = "preferences_sms_key";
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefs = getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
        smsManager = SmsManager.getDefault();

        navigate(MainFragment.newInstance());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings: {
                navigate(SettingsFragment.newInstance());
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void savePreferences(String key, ArrayList<String> list) {
        StringBuilder sb = new StringBuilder();
        sb.append(list.get(0));
        int i = 1;
        while (i < list.size()) {
            sb.append("###");
            sb.append(list.get(i));
            i++;
        }

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, sb.toString());
        editor.apply();
    }

    public ArrayList<String> readPreferences(String key) {
        String listAsText = prefs.getString(key, "");
        if (listAsText.equals("")) {
            return new ArrayList<String>();
        } else
            return new ArrayList<String>(Arrays.asList(listAsText.split(  "###")));
    }

    public void sendSms(String phoneNo, String message) {
        smsManager.sendTextMessage(phoneNo, null, message, null, null);
    }

    private void navigate(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame, fragment)
                .commit();
    }
}