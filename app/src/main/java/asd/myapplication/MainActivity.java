package asd.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity
{
    // Тэг хранящий в себе название активити. Нужно для того что определить то откуда пришёл лог
    private static final String TAG = MainActivity.class.getSimpleName();

    EditText etLogin;
    EditText etPassword;

    static String sLogin;
    static String sPassword;
    static public ParentClass parentClass = new ParentClass();

    static Context context;
    DBHelper dbHelper;

    String LinkWebsite = null; // Ссылка на сайт
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etLogin = (EditText) findViewById(R.id.etLogin);
        etPassword = (EditText) findViewById(R.id.etPassword);

        dbHelper = new DBHelper(this);
        context = this;

        ParentClass.PathProject = context.getCacheDir() + "/" + "ProjectTravelApp" + "/";

        File file  = new File(ParentClass.PathProject + "UserId");
        File file2 = new File(ParentClass.PathProject);

        if (!file2.exists())
        {
            Log.e (TAG, "Первый запуск приложения");

            file2.mkdirs();
        }
        if (file.exists())
        {
            Log.e (TAG, "Авторизация пропускается");
            ParentClass.WriteFile = true;
            LogautTrue();
        } else Log.e (TAG, "Файла нет");
    }

    static public Boolean NetvorkType ()
    {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnectedOrConnecting())
        {
            if (networkInfo.getTypeName().equalsIgnoreCase("MOBILE")) return true; else
            if (networkInfo.getTypeName().equalsIgnoreCase("WIFI")) return true;
        }

        return false;
    }

    public void ClicButton(View v)
    {
        if (v.getId() == R.id.bLogin)
        {
            if (NetvorkType())
            {
                sLogin = etLogin.getText().toString();
                sPassword = etPassword.getText().toString();

                parentClass.SendMessage("login", this);

                LogTrue();
            }
            else Toast.makeText(this, "Нет подключения к интернету", Toast.LENGTH_SHORT).show();
        } else
        if (v.getId() == R.id.bLogin)
        {
            if (LinkWebsite != null) // Актуально только до тех пор пока нету конкретной ссылки на сайт
            {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(LinkWebsite));
                startActivity(intent);
            }
        }
    }

    public void LogTrue()
    {
        if (ParentClass.MessageReturn)
        {
            if (ParentClass.ThisUser)
            {
                LogautTrue();
                Log.e(TAG, "Этот юзер норм");
            }
            else Log.e (TAG, "Этот юзер не норм");

            ParentClass.MessageReturn = false;
        }
    }

    public void LogautTrue()
    {
        ParentClass.db = dbHelper.getWritableDatabase();

        ParentClass.bRamkaFotoFone  = (BitmapDrawable) getResources().getDrawable( R.drawable.b_ramka_foto_fone);
        ParentClass.bRamkaFotoLeft  = (BitmapDrawable) getResources().getDrawable( R.drawable.b_ramka_foto);
        ParentClass.bRamkaFotoRight = (BitmapDrawable) getResources().getDrawable( R.drawable.b_ramka_foto_right);
        ParentClass.bHexagon        = (BitmapDrawable) getResources().getDrawable( R.drawable.b_hexagon);

        parentClass.context = this;
        parentClass.StartApp();
        finish();

        Intent intent = new Intent(this, ListUsers.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("name", "1");
        startActivity(intent);
    }
}