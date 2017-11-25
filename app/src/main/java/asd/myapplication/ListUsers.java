package asd.myapplication;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ListUsers extends ParentClass
{
    // Тэг хранящий в себе название активити. Нужно для того что определить то откуда пришёл лог
    private static final String TAG = ListUsers.class.getSimpleName();

    static public ListView list;

    static public Button bChat;
    static public Button bListUsers;

    static public Context context;
    static public NotificationManager nm;

    static public ArrayList<View> ListMyView = new ArrayList<View>();
    static public boolean bScroll = false;

    static public Activity activity;
    static public int Index;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_users);

        context = this;
        ParentClass.context = this;

        bChat = (Button)findViewById(R.id.bChat);
        bListUsers = (Button)findViewById(R.id.bUsers);

        list = (ListView) findViewById(R.id.users_list);
        //list.setAdapter(MyAdapter);

        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        activity = this;

        UpdateFileDate ();

        RebuildColorButton ();
    }

    void RebuildColorButton ()
    {
        if (Filter == 1)
        {
            bChat.setBackground(getResources().getDrawable(R.drawable.b_menu_left_turquoise));
            bListUsers.setBackground(getResources().getDrawable(R.drawable.b_menu_righ));

        } else
        if (Filter == 0)
        {
            bChat.setBackground(getResources().getDrawable(R.drawable.b_menu_left));
            bListUsers.setBackground(getResources().getDrawable(R.drawable.b_menu_righ_turquoise));
        }
    }

    //Обновление базы данных (недоделано)
    void UpdateFileDate ()
    {
        if (MainActivity.NetvorkType())
        {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String date = sdf.format(new Date(System.currentTimeMillis()));

            Log.e (TAG, date);

            Boolean bool = false;

            File file = new File(PathProject + "LastDateUpdate");
            if (!file.exists())
            {
                bool = true;

                Log.e (TAG, "Файла LastDateUpdate нет");
                try
                {
                    BufferedWriter bw = new BufferedWriter(new FileWriter(file)); bw.write(date); bw.close(); // Создаю файл и заношу в него текущую дату
                    Log.e (TAG, "Файл LastDateUpdate создан");
                }
                catch (IOException e){Log.e (TAG, "Файл LastDateUpdate не создан");}
            }
            else
            {
                try
                {
                    String FileDate = "";

                    String [] MessageOut;
                    String [] MessageOut2;

                    BufferedReader br = new BufferedReader(new FileReader(PathProject + "LastDateUpdate"));
                    FileDate = br.readLine();
                    br.close();

                    MessageOut = FileDate.split("/");
                    MessageOut2 = date.split("/");

                    if (Integer.parseInt(MessageOut[2]) == Integer.parseInt(MessageOut2[2]))
                    {
                        if (Integer.parseInt(MessageOut[1]) == Integer.parseInt(MessageOut2[1]))
                        {
                            if (Integer.parseInt(MessageOut[0]) == Integer.parseInt(MessageOut2[0]))
                            {
                                Log.e (TAG, "Дата актуальна");
                            } else bool = true;
                        } else bool = true;
                    } else bool = true;

                    if (bool)
                    {
                        Log.e (TAG, "В файле LastDateUpdate старая дата");

                        BufferedWriter bw = new BufferedWriter(new FileWriter(PathProject + "LastDateUpdate"));
                        bw.write(date + "\n");
                        bw.close();

                        Log.e (TAG, "Файл LastDateUpdate записан");
                    }
                }
                catch (IOException e){}
            }

            //if (bool) SendMessage("/* тот должна быть команда для сервера */", context);
        } else Log.e (TAG, "Инэта нет, так что обновл базы не будет");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        menu.add(0,1,0,"Настройка");
        menu.add(0,2,0,"Выход");

        return super.onCreateOptionsMenu (menu);
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == 1) // Настройка
        {
            Intent intent = new Intent(this, Setting.class);
            startActivityForResult(intent, 1);
        } else
        if (item.getItemId() == 2) // Выход к экрану с авторизацией
        {
            Runtime runtime = Runtime.getRuntime();
            try
            {
                runtime.exec("rm -r " + this.getCacheDir() + "/" + "ProjectTravelApp" + "/" + "UserId");
                Log.e (TAG, "Файл удалён");
            }
            catch (IOException e){}

            finish();
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("reason", "Chat");
            startActivity(intent);

            ParentClass.ThisUser = null;
            ParentClass.WriteFile = false;
            ListMyView.clear();
            Filter = 0;
        }

        return super.onOptionsItemSelected(item);
    }

    public void ClicButton(View v)
    {
        if (v.getId() == R.id.bChat & Filter == 0)
        {
            Filter = 1;

            RebuildColorButton();

        } else
        if (v.getId() == R.id.bBeacons & Filter == 1)
        {
            Filter = 0;

            RebuildColorButton ();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (data == null) return;

        String name = data.getStringExtra("name");

        if (name.equals("1"))
        {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}