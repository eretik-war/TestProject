package asd.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

class DBHelper extends SQLiteOpenHelper
{
    // Тэг хранящий в себе название активити. Нужно для того что определить то откуда пришёл лог
    private static final String TAG = DBHelper.class.getSimpleName();

    public DBHelper(Context context)
    {
        // конструктор суперкласса
        super(context, "myDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        // 2.	Бикон: id_beacon, uuid, major, minor (таблица всех биконов в системе)
        // 3.	Пользователь-бикон: id_beacon, аватарка, сообщение (таблица биконов, зарегистрированных за пользователями)

        db.execSQL("create table user_beacon " +  // создаем таблицу
                "(" +
                    "id integer primary key autoincrement," +
                    "id_bicnon text,"  +
                    "minor text,"  +
                    "major text," +
                    "message text," +
                    "id_user text,"  +
                    "name_image text"  +
                ");"
            );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
