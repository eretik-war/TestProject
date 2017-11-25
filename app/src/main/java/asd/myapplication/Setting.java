package asd.myapplication;

import android.os.Bundle;
import android.view.View;

public class Setting extends ParentClass
{
    // Тэг хранящий в себе название активити. Нужно для того что определить то откуда пришёл лог
    private static final String TAG = Setting.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);
    }


    public void ClicButton(View v)
    {
        if (v.getId() == R.id.bChat)
        {
            ListUsers.bChat.setBackground(getResources().getDrawable(R.drawable.b_menu_left_turquoise));
            ListUsers.bListUsers.setBackground(getResources().getDrawable(R.drawable.b_menu_righ));

            Filter = 1;
            finish();
        } else
        if (v.getId() == R.id.bBeacons)
        {
            ListUsers.bChat.setBackground(getResources().getDrawable(R.drawable.b_menu_left));
            ListUsers.bListUsers.setBackground(getResources().getDrawable(R.drawable.b_menu_righ_turquoise));

            Filter = 0;
            finish();
        }
    }
}