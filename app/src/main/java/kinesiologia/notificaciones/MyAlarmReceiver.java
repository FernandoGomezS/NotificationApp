package kinesiologia.notificaciones;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Fernando on 03-06-2016.
 */
public class MyAlarmReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("MyTestService", "onReceive on MyAlarmReceiver");

        Intent ii = new Intent(context, MainActivity.class);
        ii.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(ii);



    }
}