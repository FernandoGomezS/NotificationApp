package kinesiologia.notificaciones;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Fernando on 03-06-2016.
 */
public class InitService extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {


        Intent i = new Intent(context, SmsService.class);
        context.startService(i);

    }
}