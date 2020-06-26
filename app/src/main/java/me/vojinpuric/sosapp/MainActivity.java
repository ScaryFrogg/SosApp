package me.vojinpuric.sosapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//TODO bolja poruka

public class MainActivity extends AppCompatActivity {
    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final String SHARED_PREFS_FILE = "shared_preferences";
    public static final String KEY_EMAILS = "preferences_emails_key";
    public static final String KEY_SERVER_ID = "preferences_id_for_server";
    public static final String KEY_SMS = "preferences_sms_key";

    private static String userId;
    private static ArrayList<String> phones;
    private static ArrayList<String> emails;
    private static SharedPreferences prefs;
    private static Intent serviceIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        serviceIntent = new Intent(this, LocationService.class);
        prefs = getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
        userId = getId();
        emails = readPreferences(MainActivity.KEY_EMAILS);
        phones = readPreferences(MainActivity.KEY_SMS);

        askForPermissions();
    }

    private void askForPermissions() {
        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.SEND_SMS,
                        //Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()) {
                    if (checkSendOnStartUp()) {
                        serviceIntent.putStringArrayListExtra(LocationService.KEY_SMS, phones);
                        serviceIntent.putStringArrayListExtra(LocationService.KEY_EMAILS, emails);
                        startService(serviceIntent);
                    }else{
                        stopService(serviceIntent);
                    }
                    navigate(MainFragment.newInstance());
                } else {
                    navigate(PermissionsFragment.newInstance());
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }

    public static String getUserId() { return userId; }

    public static Intent getTrackingServiceIntent() {
        return serviceIntent;
    }

    public static ArrayList<String> getPhones() {
        return phones;
    }

    public static ArrayList<String> getEmails() {
        return emails;
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

    @Override
    protected void onDestroy() {
        stopService(serviceIntent);
        super.onDestroy();
    }

    public boolean checkSendOnStartUp() {
        return PreferenceManager.getDefaultSharedPreferences(this).getBoolean("sendOnStartUp", false);
    }

    public static void savePreferences(String key, ArrayList<String> list) {
        StringBuilder sb = new StringBuilder("");
        if(list.size()>0){
            sb.append(list.get(0));
            int i = 1;
            while (i < list.size()) {
                sb.append("###");
                sb.append(list.get(i));
                i++;
            }
        }
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, sb.toString());
        editor.apply();
    }

    public static ArrayList<String> readPreferences(String key) {
        String listAsText = prefs.getString(key, "");
        if (listAsText.equals("")) {
            return new ArrayList<>();
        } else
            return new ArrayList<>(Arrays.asList(listAsText.split("###")));
    }
    private String getId(){
        String check = prefs.getString(KEY_SERVER_ID,"");
        if(check.equals("")){
            String generatedId = generateId();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(KEY_SERVER_ID, generatedId);
            editor.apply();
            return generatedId;
        }
        return check;
    }

    public String generateId() {
        StringBuilder builder = new StringBuilder();
        int count = 11;
        while (count-- != 0) {
            int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }
    public void navigate(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame, fragment)
                .addToBackStack(null)
                .commit();
    }
}