package asd.myapplication;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class ParentClass extends Activity
{
    static public Boolean RebuildImage = false;
    static public Boolean WriteFile = false;

    static public Button button;
    static final Map<String, WeakReference<Drawable>> mDrawableCache = Collections.synchronizedMap(new WeakHashMap<String, WeakReference<Drawable>>());

    final static String USER_AGENT = "Android";

    private static final DefaultHttpClient httpClient = new DefaultHttpClient() ;
    public static DefaultHttpClient getInstance() { return httpClient; }
    static public Boolean MessageReturn = false;
    static public Boolean ThisUser;
    static public Context context;

    // Тэг хранящий в себе название активити. Нужно для того что определить то откуда пришёл лог
    private static final String TAG = ParentClass.class.getSimpleName();

    // Базовые цвета
    static public int MyTurquoise = Color.rgb(84, 186, 255);
    static public int MyGreen = Color.rgb(0, 255, 0);
    static public int MyYellow = Color.rgb(255, 255, 0);
    static public int MyRed = Color.rgb(255, 0, 0);
    static public int MyWrite = Color.rgb(255, 255, 255);
    static public int MyBlack = Color.rgb(0, 0, 0);

    static public int MyColor;

    static public String IdThisUser; // Id юзера

    // Информация по пользователю
    public String MyName = "";
    public String MyFamily = "";
    public String MyYear = "";
    public String MyFrends = "";
    public String MyAva = "";


    // Рамки и фон для фото
    static public BitmapDrawable bRamkaFotoFone;
    static public BitmapDrawable bRamkaFotoLeft;
    static public BitmapDrawable bRamkaFotoRight;
    static public BitmapDrawable bHexagon;

    static String PathProject;
    static String PathImages;


    static public SQLiteDatabase db;
    static public DBHelper dbHelper;


    Boolean NetworkTypeMobile;
    ConnectivityManager cm;
    NetworkInfo networkInfo;

    // Для переключения на чат в котором могут писать все кто захотят
    static public int Filter = 0; // 0 - список пользователей, 1 - список сообщений

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        PathImages = PathProject + "Images" + "/";
        dbHelper = new DBHelper(this);
        cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = cm.getActiveNetworkInfo();

        NetvorkType ();
    }

    public void NetvorkType ()
    {
        if (networkInfo != null && networkInfo.isConnectedOrConnecting())
        {
            if (networkInfo.getTypeName().equalsIgnoreCase("MOBILE")) NetworkTypeMobile = true; else
            if (networkInfo.getTypeName().equalsIgnoreCase("WIFI"  )) NetworkTypeMobile = false;
        }
        Log.e (TAG, "Mobile = " + NetworkTypeMobile);
    }

    // Выполняет необходимые вещи после логининга
    public void StartApp ()
    {
        MyColor = MyTurquoise; // В будущем буду грузить инфу из информации пользователя
        bRamkaFotoFone = ReColorBitmap(bRamkaFotoFone, MyColor, 0xFF2B23FF);

        if (WriteFile)
        {
            try
            {
                String str;

                BufferedReader br = new BufferedReader(new FileReader(PathProject + "UserId"));
                while ((str = br.readLine()) != null) IdThisUser = str;
                br.close();

                File file = new File(PathProject + "LastDateUpdate");
                if (!file.exists())  Log.e (TAG, "Файла LastDateUpdate нет так как это первый запуск приложения");

                Log.e (TAG, "Получены данные о Id " + IdThisUser);
            }
            catch (IOException e){}
        }
    }

    // Превращаем фотку в шестигранник с рамкой
    static public BitmapDrawable ReColorBitmap(BitmapDrawable bitdrawImage, int NewColor, int OldColor)
    {
        Bitmap bm = bitdrawImage.getBitmap();
        int w = bm.getWidth();
        int h = bm.getHeight();
        Bitmap result = Bitmap.createBitmap(w, h, bm.getConfig());
        for (int i = 0; i < w; i++) // Перебираем все пиксели
            for (int j = 0; j < h; j++)
            {
                int hexColor = bm.getPixel(i, j);
                if (hexColor == OldColor) hexColor = NewColor; // Если текущий пиксель равен старому цвету, то меняем его на новый
                result.setPixel(i, j, hexColor); // Заносим новые данные в текущий пиксель
            }

        return new BitmapDrawable(result);
    }

    // Меняем цвет рамки.
    static public LayerDrawable createLayerDrawable(BitmapDrawable drawable0, int ColorFrame, String Side)
    {
        BitmapDrawable frame;
        if (Side.equals("Left")) frame = ReColorBitmap(bRamkaFotoLeft, ColorFrame, MyWrite); else
        if (Side.equals("Right")) frame = ReColorBitmap(bRamkaFotoRight, ColorFrame, MyWrite);
        else frame = bHexagon;

        Drawable drawableArray[] = new Drawable[] { drawable0, bRamkaFotoFone, frame};
        LayerDrawable layerDraw = new LayerDrawable(drawableArray);
        return layerDraw;
    }

    // Все запросы по отправке сообщений приходят сюда и отсюда уже связывается с классом работающим с сервером
    public void SendMessage (String param, Context NewContext)
    {
        if (NewContext != null) context = NewContext;
        new StartTask().execute(param);
    }

    // Общение с сервером
    class StartTask extends AsyncTask<String, String, String>
    {
        ProgressDialog dialog;
        String UrlServer = "http://192.168.43.76/api.php";
        String Comand;

        @Override
        protected String doInBackground(String... params) // Отправляю
        {
            String res = null;
            List<NameValuePair> param = new ArrayList<NameValuePair>();
            Comand = params[0];

            if (params[0].equals("login")) // Логинимся
            {
                Log.e (TAG, "Авторизуюсь");

                param.add(new BasicNameValuePair("mobilka", "1"));
                param.add(new BasicNameValuePair("request", params[0]));
                param.add(new BasicNameValuePair("login", MainActivity.sLogin));
                param.add(new BasicNameValuePair("pass", MainActivity.sPassword));
            }

            res = postData(param,UrlServer);

            return params[0]+": "+res;
        }

        @Override
        protected void onPostExecute(String result) // Получаю
        {
            JSONObject json;
            String json_string = result.substring(result.indexOf("{"));


            try
            {
                json = new JSONObject(json_string);

                if (Comand.equals("login")) // Логинимся
                {
                    if (json.getJSONObject("res").getString("return").toString().equals("1"))
                    {
                        Log.e (TAG, "Логинюсь");

                        ThisUser = true;
                        MyName = json.getJSONObject("res").getString("name");
                        MyFamily = json.getJSONObject("res").getString("family");
                        MyYear = json.getJSONObject("res").getString("year");
                        MyFrends = json.getJSONObject("res").getString("Frends");
                        MyAva = json.getJSONObject("res").getString("ava");

                        File NewFile = new File(PathProject, "UserId");

                        try
                        {
                            BufferedWriter bw = new BufferedWriter(new FileWriter(NewFile)); bw.write(" "); bw.close(); // Создаю пустой файл
                            Log.e (TAG, "Файл UserId создан");
                        }
                        catch (IOException e){Log.e (TAG, "Файл не создан");}
                    } else ThisUser = false;

                    MessageReturn = true;
                }
            }
            catch (JSONException e) {
                Log.e (TAG, "Получил инфу с ошибкой" + e.toString());}

            dialog.dismiss();
            super.onPostExecute(result);
        }

        // Ожидание ответа от сервера
        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(context);
            dialog.setMessage("гружу...");
            dialog.setIndeterminate(true);
            dialog.setCancelable(true);
            dialog.show();
            super.onPreExecute();
        }
    }

    public String postData(List<NameValuePair> param,String url)
    {
        // Create a new HttpClient and Post Header
        InputStream is = null;
        StringBuilder sb = null;
        String result = "";

        CookieStore cookieStore = new BasicCookieStore();
        DefaultHttpClient httpClient = getInstance();

        httpClient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "Android-AEApp,ID=2435743");
        httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BEST_MATCH);

        httpClient.setCookieStore(cookieStore);
        HttpPost httpPost = new HttpPost(url);
        AbstractHttpEntity entity;
        HttpResponse httpResponse = null;

        try
        {
            entity = new UrlEncodedFormEntity(param);
            httpPost.setEntity(entity);
            httpPost.setHeader("User-Agent","Android-AEApp,ID=2435743");
            httpPost.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");

            if (CookieStorage.getInstance().getArrayList().isEmpty())
                CookieStorage.getInstance().getArrayList().add("PHPSESSID=lc89a2uu0rj6t2p219gc2cq4i2");

            httpPost.setHeader("Cookie", CookieStorage.getInstance().getArrayList().get(0).toString());

            Log.e (TAG, CookieStorage.getInstance().getArrayList().get(0).toString());

            httpResponse = httpClient.execute(httpPost);

            if (httpResponse.getLastHeader("Set-Cookie")!=null)
            {
                CookieStorage.getInstance().getArrayList().remove(0);
                CookieStorage.getInstance().getArrayList().add(httpResponse.getLastHeader("Set-Cookie").getValue());
            }

            HttpEntity httpEntity = httpResponse.getEntity();

            is = httpEntity.getContent();
        }
        catch (Exception e){Log.e (TAG, "ошибка " + e.toString());}

        try
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"), 8);

            sb = new StringBuilder();

            sb.append(reader.readLine() + "\n");
            String line = "0";

            while ((line = reader.readLine()) != null) sb.append(line + "\n");
            is.close();
            result = sb.toString();
        }
        catch (Exception e){Log.e("log_tag", "Error converting result " + e.toString());}
        Log.e(TAG, "Отправлен текст: "+param);
        Log.e(TAG, "Получен ответ "+result);
        return result;
    }
}
