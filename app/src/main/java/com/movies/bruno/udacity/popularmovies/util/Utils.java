package com.movies.bruno.udacity.popularmovies.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by techresult on 11/01/2016.
 */
public class Utils {

    public static Bitmap drawableToBitmap(Drawable drawable){
        Bitmap bitmap;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static void createFile(Bitmap image, String fileName){
        File direct = Environment.getExternalStorageDirectory();

        if (!direct.exists()) {
            File wallpaperDirectory = new File(Environment.getExternalStorageDirectory().getPath());
            wallpaperDirectory.mkdirs();
        }

        File file = new File(new File(Environment.getExternalStorageDirectory().getPath()), fileName + ".jpg");
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap getImage(String name){
        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/" + name + ".jpg");
        return BitmapFactory.decodeFile(file.getAbsolutePath());
    }

}
