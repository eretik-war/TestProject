package asd.myapplication;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
// Подгрузка картинок, недоделано и не отлажено
class ImageDownloadAsyncTask extends AsyncTask<Void, Void, Void>
{
    // Тэг хранящий в себе название активити. Нужно для того что определить то откуда пришёл лог
    private static final String TAG = ImageDownloadAsyncTask.class.getSimpleName();

    private String source;
    private String NameImage;

    Bitmap bitmap;

    public ImageDownloadAsyncTask(String source, String NameImage)
    {
        this.source = source;
        this.NameImage = NameImage;
    }

    @Override
    protected Void doInBackground(Void... params)
    {
        if (!ParentClass.mDrawableCache.containsKey(source))
        {
            try
            {
                //Скачиваем картинку в наш кэш
                URL url = new URL(source);
                URLConnection connection = url.openConnection();
                InputStream is = connection.getInputStream();

                Drawable drawable = Drawable.createFromStream(is, "src");
                bitmap = Bitmap.createScaledBitmap(((BitmapDrawable) drawable).getBitmap(), 100, 100, false);

                is.close();

                ParentClass.mDrawableCache.put(source, new WeakReference<Drawable>(drawable));

            }
            catch (MalformedURLException e){}catch (Throwable t){}
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result)
    {
        try
        {
            File file4 = new File(ParentClass.PathImages, // Путь к файлу
                                              NameImage); // Имя файла

            if (!file4.exists()) Log.e (TAG, "Картинка не загружена " + NameImage); /*file4.getParentFile().mkdirs();*/ else Log.e(TAG, "Загружена картинка " + NameImage);
            OutputStream fOut = new FileOutputStream(file4);

            bitmap.compress(Bitmap.CompressFormat.PNG, 85, fOut); // сохранять картинку в PNG-формате с 85% сжатия.
            fOut.flush();
            fOut.close();

            Log.e(TAG, "В дирректории создан файл " + NameImage);

            ParentClass.RebuildImage = true;
        }
        catch (Exception e){Log.e(TAG, "ERROR " + e.getMessage() + " " + NameImage);}
    }
}