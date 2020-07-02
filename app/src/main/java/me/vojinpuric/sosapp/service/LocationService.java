package me.vojinpuric.sosapp.service;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

import me.vojinpuric.sosapp.helpers.EmailHelper;
import me.vojinpuric.sosapp.MainActivity;
import me.vojinpuric.sosapp.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class LocationService extends Service {
    public static final String KEY_EMAILS = "service_emails_key";
    public static final String KEY_SMS = "service_sms_key";
    public static final String CHANNEL_ID = "NOTIFICATION_CHANNEL_ID";
    public static final String STOP_ACTION = "STOP_SENDING";
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private SmsManager smsManager;
    private ArrayList<String> emails;
    private ArrayList<String> phones;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals(STOP_ACTION)) {
            stopSelf();
        }
        smsManager = SmsManager.getDefault();
        phones = intent.getStringArrayListExtra(KEY_SMS);
        emails = intent.getStringArrayListExtra(KEY_EMAILS);

        buildLocationRequest();

        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onCreate() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
//                String locationString = new StringBuilder("" + location.getLatitude())
//                        .append("/")
//                        .append(location.getLongitude())
//                        .toString();
//                Toast.makeText(getBaseContext(), locationString, Toast.LENGTH_LONG).show();

                SendToServerService service = RetrofitClientInstance.getRetrofitInstance().create(SendToServerService.class);
                service.postToServer(location.getLatitude() + "," + location.getLongitude() + "," + MainActivity.getUserId()).enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        //Log.e("Retrofit", "successs response" + response.message());
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
//                        Log.e("Retrofit", "failure");
//                        t.printStackTrace();
                    }
                });
                sendSos("" + location.getLatitude(), "" + location.getLongitude(), emails, phones , MainActivity.getUserId());
            }
        };

        moveToForeground(this);
    }

    /***
     * On url update change in 3 places
     * here in SendToServerService
     * sendSos function sendSms fucntion call
     * GMail email body creation
     * res.xml.network_security_config.xml
     */
    public interface SendToServerService {
        @POST("http://192.168.0.111:8080/api")
        Call<String> postToServer(@Body String location);
    }

    //home http://192.168.0.189:8080/api
    //ProSmart http://192.168.0.111:8080/api


    private void moveToForeground(Context service) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = ("R.string.channel_name");
            String description = ("R.string.channel_description");
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        Intent stopIntent = new Intent(service,LocationService.class);
        stopIntent.setAction(STOP_ACTION);

        startForeground(1, new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Your location is being Tracked")
                .setContentText("Tap this notification sto stop tracking")
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(PendingIntent.getService(service,0,stopIntent,0))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT).build()
        );
    }

    private void buildLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("powerMenagment", false)) {
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        } else {
            locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        }
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void sendSos(String lat, String lon, ArrayList<String> emails, ArrayList<String> phones, String trackingId) {
        if (emails != null) {
            for (String email : emails) {
                new EmailHelper(getApplicationContext(), email, lat, lon, trackingId);
            }
        }
        if (phones != null) {
            for (String phone : phones) {
                sendSms(phone, String.format("My location is https://www.google.com/maps/place/%s+%s \n Link for tracking: http://192.168.0.111:8080/?id=%s", lat, lon ,trackingId));
            }
        }

    }

    public void sendSms(String phoneNo, String message) {
        smsManager.sendTextMessage(phoneNo, null, message, null, null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);

    }
}