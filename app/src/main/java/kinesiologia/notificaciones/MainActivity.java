package kinesiologia.notificaciones;


import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import android.util.Log;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends AppCompatActivity{

    private NotificationManager notificationManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("MyTestService", "Test");

        int interval = 1; // minutes

        Intent receiverIntent = new Intent(getBaseContext(), InitService.class);
        PendingIntent sender = PendingIntent.getBroadcast(getBaseContext(), 123456789, receiverIntent, 0);

        AlarmManager alarmManager = (AlarmManager)getBaseContext().getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) Log.i("MyTestService", "alarmManager es null");

        // tipo alarma, primer lanzamiento, intervalo lanzamientos, Intent a ejecutar
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+interval*60*1000, interval*60*1000, sender);


        finish();

    }



}

