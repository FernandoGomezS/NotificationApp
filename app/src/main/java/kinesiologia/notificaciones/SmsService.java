package kinesiologia.notificaciones;


import android.app.Activity;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.NotificationCompat;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Fernando on 03-06-2016.
 */
public class SmsService extends IntentService{

    String[] idMessageOk;
    int cont;
    int contEnv=0;

    public SmsService() {
        super("MyTestService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        Log.i("MyTestService1 ", "dentro");

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        Date d = new Date();
        String dayOfTheWeek = sdf.format(d);

        sdf = new SimpleDateFormat("HH");
        d = new Date();
        int hourOfDay =  Integer.parseInt(sdf.format(d));

        System.out.println("dia: "+dayOfTheWeek);
        System.out.println("HORA: "+hourOfDay);


/*
        if( compruebaConexion(this)&& hourOfDay >= 6 && hourOfDay <= 7
                && !dayOfTheWeek.equals(new String("domingo")) && !dayOfTheWeek.equals(new String("sábado")))
        {
             try {
            int restp=0;
            URL url = new URL("http://172.16.41.183/notificationautomatic");
            JSONObject dato = new JSONObject();
            dato.put("email", "admin@utalca.cl");
            dato.put("password", "utalca");
            String userpassword = dato.toString();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            byte[] authEncBytes = android.util.Base64.encode(userpassword.getBytes(), android.util.Base64.DEFAULT);

            conn.setRequestProperty("Authorization", new String(authEncBytes));
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setReadTimeout(10000 );
            conn.setConnectTimeout(15000 );
            conn.connect();

            OutputStream os = conn.getOutputStream();
            os.write(null);
            os.flush();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            restp=conn.getResponseCode();
            conn.disconnect();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.i( "Error in Post: ",e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            Log.i( "Error in Post: ",e.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }



        }
*/

        if( compruebaConexion(this)&& hourOfDay >= 9 && hourOfDay <= 17
                && !dayOfTheWeek.equals(new String("domingo")) && !dayOfTheWeek.equals(new String("sábado"))){

            Log.i("MyTestService", "Service Run");
            String env="No Enviados";
            if(getRestFul()) {
                boolean val = true;
                //SystemClock.sleep(7000);
                try {
                    while (val) {

                        if (idMessageOk.length == cont) {
                            if (contEnv > 0) {
                                postRestFul(jsonMessageOk(idMessageOk, contEnv));
                                deleteSMS();
                                env = "enviados";
                            }
                            val = false;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            NotificationCompat.Builder mBuilder =
                    (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                            .setSmallIcon(R.mipmap.utalca_logo)
                            .setTicker("New notification")
                            .setContentTitle("My notification")
                            .setContentText(env);

            mBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(1, mBuilder.build());
        }
        System.out.println("Termino!!!");

    }

    public static boolean compruebaConexion(Context contexto) {
        boolean connected = false;
        ConnectivityManager connec = (ConnectivityManager) contexto.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] redes = connec.getAllNetworkInfo();
        for (int i = 0; i < redes.length; i++) {
            if (redes[i].getState() == NetworkInfo.State.CONNECTED) {
                connected = true;
            }
        }
        return connected;

    }

    public boolean getRestFul( ) {

        try {

            URL url = new URL("http://192.168.1.105/notification");
            JSONObject dato = new JSONObject();
            dato.put("email", "admin@utalca.cl");
            dato.put("password", "utalca");
            String userpassword = dato.toString();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            byte[] authEncBytes = android.util.Base64.encode(userpassword.getBytes(), android.util.Base64.DEFAULT);

            conn.setRequestProperty("Authorization",  new String(authEncBytes));
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            conn.setConnectTimeout(15000 );
            conn.connect();

            if (conn.getResponseCode() == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                if (br != null) {
                    System.out.println("Entro!!!");
                    String output;
                    output = br.readLine();
                    JSONObject json = new JSONObject(output);
                    JSONArray array = new JSONArray(json.getString("notifications"));
                    idMessageOk = new String[array.length()];
                    cont = 0;
                    for (int i = 0; i < array.length(); i++) {

                        JSONObject row = array.getJSONObject(i);
                        String id = row.getString("ID");
                        String message = row.getString("message");
                        String number = row.getString("number");
                        sendSms(number,message,id);
                        System.out.println("id :"+id+" numero :"+number+" Mensaje :"+message);
                    }
                }
                conn.disconnect();
                return true;
            }
            conn.disconnect();
            return false;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.i( "Error in Get: ",e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            Log.i( "Error in Get: ",e.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.i( "Error in Get: ",e.toString());
        }

        return false;
    }

    public void postRestFul(JSONObject jsonMessageOk ) throws  IOException, JSONException {


        int restp=0;
        System.out.println("JSON: " + jsonMessageOk);
        try {

            URL url = new URL("http://192.168.1.105/notification");
            JSONObject dato = new JSONObject();
            dato.put("email", "admin@utalca.cl");
            dato.put("password", "utalca");
            String userpassword = dato.toString();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            byte[] authEncBytes = android.util.Base64.encode(userpassword.getBytes(), android.util.Base64.DEFAULT);

            conn.setRequestProperty("Authorization", new String(authEncBytes));
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setReadTimeout(10000 );
            conn.setConnectTimeout(15000 );
            conn.connect();

            OutputStream os = conn.getOutputStream();
            os.write(jsonMessageOk.toString().getBytes("UTF-8"));
            os.flush();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            restp=conn.getResponseCode();

            if (restp == 200) {
                String output;
                System.out.println("Output from Server .... \n");
                while ((output = br.readLine()) != null) {
                    System.out.println(output);
                }
            }
            conn.disconnect();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.i( "Error in Post: ",e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            Log.i( "Error in Post: ",e.toString());
        }



    }


    private boolean sendSms (String numero, String mensaje,String id){

        PendingIntent sentIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(id), 0);
        getApplication().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()){
                    case Activity.RESULT_OK:
                        Log.i("Mensaje enviado","Sms");
                        idMessageOk[cont] = intent.getAction();
                        cont++;
                        contEnv++;
                        getApplication().unregisterReceiver(this);
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Log.i("ERROR","Sms");
                        cont++;
                        getApplication().unregisterReceiver(this);
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Log.i("Servicio no diponible","Sms");
                        cont++;
                        getApplication().unregisterReceiver(this);
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Log.i("PDU  es NULL","Sms");
                        cont++;
                        getApplication().unregisterReceiver(this);
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Log.i("Failed turned off","Sms");
                        cont++;
                        getApplication().unregisterReceiver(this);
                        break;
                }
            }

        }, new IntentFilter(id));
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage( numero , null, mensaje , sentIntent, null);
        return true;


       /* try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(numero, null, mensaje, null, null);
            Log.i("Mensaje enviado","Sms");
            return true;
        }
        catch (Exception e) {

            e.printStackTrace();
            Log.i("Mensaje No enviado","sms");
            return false;
        }*/

    }

    public JSONObject jsonMessageOk (String id[], int numberMessage) throws JSONException {
        JSONObject obj = null;
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < numberMessage; i++) {
            obj = new JSONObject();
            try {
                obj.put("ID", id[i]);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonArray.put(obj);
        }
        JSONObject finalobject = new JSONObject();
        finalobject.put("Sent", jsonArray);
        return finalobject;
    }

    public void deleteSMS() {
        ContentResolver cr = getContentResolver();
        Uri inboxUri = Uri.parse("content://sms/inbox");
        Cursor c = getApplicationContext().getContentResolver().query(Uri.parse("content://sms/"), null, null, null,null);
        try {
            while (c.moveToNext()) {
                int id = c.getInt(0);
                getApplicationContext().getContentResolver().delete(Uri.parse("content://sms/" + id), null, null);
            }

        }catch(Exception e){
            Log.e(this.toString(),"Error deleting sms",e);
        }finally {
            c.close();
        }
    }
}

