package kinesiologia.notificaciones;

import android.app.Activity;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;
import android.support.v7.app.NotificationCompat;
import android.telephony.SmsManager;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Fernando on 03-06-2016.
 */
public class SmsService extends IntentService{

    public SmsService() {
        super("MyTestService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.i("MyTestService", "Service running");

        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setTicker("New notification")
                        .setContentTitle("My notification")
                        .setContentText(getRestFul());

        mBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(1, mBuilder.build());
    }

    public String getRestFul( )
    {
        //ruta del servidor
        // final String HTTP_RESTFUL="http://androidexample.com/media/webservice/JsonReturn.php";
        final String HTTP_RESTFUL="http://rest-service.guides.spring.io/greeting";

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(HTTP_RESTFUL);
        String strResultado="NaN";
        try {
            //ejecuta
            HttpResponse response = httpclient.execute(httppost);
            //Obtiene la respuesta del servidor
            String jsonResult = inputStreamToString(response.getEntity().getContent()).toString();
            JSONObject object = new JSONObject(jsonResult);
            //obtiene el status
            strResultado="";
            //extrae los registros
            /*
             JSONArray array = new JSONArray(object.getString("Android"));
            for (int i = 0; i < array.length(); i++)
            {
                //recorre cada registro y concatena el resultado
                JSONObject row = array.getJSONObject(i);

               String name = row.getString("name");
                String number = row.getString("number");
                String date_added = row.getString("date_added");
                strResultado += name + " " + number + " " + date_added +"\n";
                strResultado += id + " " + content  +"\n";
            }
            */

             int id = object.getInt("id");

            String content= object.getString("content");
            envioSms(Integer.toString(id),content);
            strResultado +=  content  +"\n";
            return strResultado;

        } catch (ClientProtocolException e) {
            strResultado = e.getMessage();
            e.printStackTrace();
        } catch (IOException e) {
            strResultado = e.getMessage();
            e.printStackTrace();
        } catch (JSONException e) {
            strResultado = e.getMessage();
            e.printStackTrace();
        }
        return strResultado;
    }
    public String postRestFul(String name, String age) throws ClientProtocolException, IOException, JSONException
    {
        HttpClient httpclient = new DefaultHttpClient();
        String uuid = UUID.randomUUID().toString();
        //url y tipo de contenido
        HttpPost httppost = new HttpPost("ALgo");
        httppost.addHeader("Content-Type", "application/json");
        //forma el JSON y tipo de contenido
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("uuid", uuid );
        jsonObject.put("name", name );
        jsonObject.put("age", age );
        StringEntity stringEntity = new StringEntity( jsonObject.toString());
        stringEntity.setContentType( (Header) new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        httppost.setEntity(stringEntity);
        //ejecuta
        HttpResponse response = httpclient.execute(httppost);
        //obtiene la respuesta y transforma a objeto JSON
        String jsonResult = inputStreamToString(response.getEntity().getContent()).toString();
        JSONObject object = new JSONObject(jsonResult);
        Log.i("jsonResult",jsonResult);
        if( object.getString("Result").equals("200"))
        {
            return "Petición POST: Exito";
        }
        return "Petición POST: Fracaso";
    }


    /**
     * Transforma el InputStream en un String
     * @return StringBuilder
     * */
    private StringBuilder inputStreamToString(InputStream is)
    {
        String line = "";
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader rd = new BufferedReader( new InputStreamReader(is) );
        try
        {
            while( (line = rd.readLine()) != null )
            {
                stringBuilder.append(line);
            }
        }
        catch( IOException e)
        {
            e.printStackTrace();
        }

        return stringBuilder;
    }

    private void envioSms (String numero, String mensaje){

        PendingIntent sentIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("SMS_SENT"), 0);

       /* registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()){
                    case Activity.RESULT_OK:
                        Toast.makeText(getApplicationContext(), "SMS enviado", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getApplicationContext(), "No se pudo enviar SMS", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getApplicationContext(), "Servicio no diponible", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getApplicationContext(), "PDU (Protocol Data Unit) es NULL", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getApplicationContext(), "Failed because radio was explicitly turned off", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter("SMS_SENT"));
        */

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage( numero , null, mensaje , sentIntent, null);



    }
}

