package kinesiologia.notificaciones;


import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;


public class MainActivity extends AppCompatActivity {

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
        Log.i("MyTestService", "End Test");

        finish();
    }

}