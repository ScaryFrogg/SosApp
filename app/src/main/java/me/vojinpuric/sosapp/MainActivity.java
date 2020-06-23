package me.vojinpuric.sosapp;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
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

//TODO regex u dialogu
//TODO permissions ne da dalje...
//TODO brisanje kontakata
//TODO UI
//TODO bolja poruka

public class MainActivity extends AppCompatActivity {
    private static SmsManager smsManager;
    private static final String SHARED_PREFS_FILE = "shared_preferences";
    public static final String KEY_EMAILS = "preferences_emails_key";
    public static final String KEY_SMS = "preferences_sms_key";
    private static ArrayList<String> phones;
    private static ArrayList<String> emails;


    private LocationService gps;
    private static SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefs = getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
        smsManager = SmsManager.getDefault();

        gps = new LocationService(this, this.checkSendOnStartUp(), location -> {
            String lat = location.getLatitude()+"";
            String lon = location.getLongitude()+"";
            Log.e("lat",lat);
            Log.e("lon",lon);

            for (String email:emails) {
                new EmailHelper(getApplicationContext(),email,lat,lon);

            }
            for (String phone:phones) {
                sendSms(phone, String.format("https://www.google.com/maps/place/%s+%s",lat,lon));
            }
        });

        emails = readPreferences(MainActivity.KEY_EMAILS);
        phones = readPreferences(MainActivity.KEY_SMS);

        navigate(MainFragment.newInstance());
        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.SEND_SMS,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ).withListener(new MultiplePermissionsListener() {
            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {/* ... */}
            @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
        }).check();

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

    public LocationService getGps() {
        return this.gps;
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

    public boolean checkSendOnStartUp() {
        return PreferenceManager.getDefaultSharedPreferences(this).getBoolean("sendOnStartUp", false);
    }

    public static void savePreferences(String key, ArrayList<String> list) {
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

    public static ArrayList<String> readPreferences(String key) {
        String listAsText = prefs.getString(key, "");
        if (listAsText.equals("")) {
            return new ArrayList<String>();
        } else
            return new ArrayList<String>(Arrays.asList(listAsText.split("###")));
    }

    public void sendSms(String phoneNo, String message) {
        smsManager.sendTextMessage(phoneNo, null, message, null, null);
    }

    private void navigate(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame, fragment)
                .addToBackStack(null)
                .commit();
    }
}