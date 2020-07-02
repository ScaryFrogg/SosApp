package me.vojinpuric.sosapp.helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.Toast;


public class EmailHelper {
    private Context context;

    public EmailHelper(Context context, String email, String lat, String lon, String trackingId) {
        this.context = context;

        if (isNetworkAvailable()) {
            new RetrieveFeedback().execute(email, lat, lon, trackingId);
        } else {
            Toast.makeText(context, "Please turn on your internet", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    static class RetrieveFeedback extends AsyncTask<String, String, Void> {
        @Override
        protected Void doInBackground(String... args) {
            try {
                GMail androidEmail = new GMail(args[0], args[1], args[2], args[3]);
                androidEmail.createEmailMessage();
                androidEmail.sendEmail();
            } catch (Exception e) {
               e.printStackTrace();
            }
            return null;
        }


    }
}
