package com.example.foad.doodle;

import android.Manifest;
import android.app.WallpaperManager;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class DoodleActivity extends AppCompatActivity implements ViewTreeObserver.OnGlobalLayoutListener, View.OnTouchListener, View.OnClickListener {

    private static float BRUSH_STROKE_WIDTH = 20.0f;
    private static int ACCESS_STORAGE_PERMISSION_FOR_SAVE_REQUEST_CODE = 1234;

    ColorPicker mBrushColorPicker;
    ColorPicker mBackgroundColorPicker;
    ImageView mBitmapHolder;
    Canvas mCanvas;

    int mBitmapHolderWidth = 0;
    int mBitnapHolderHeight = 0;

    Bitmap mBitmap;

    Paint mBrushPaint;
    Paint mBackgroundPaint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doodle);

        mBitmapHolder = (ImageView)findViewById(R.id.bitmap_holder);
        mBitmapHolder.getViewTreeObserver().addOnGlobalLayoutListener(this);

        mBrushColorPicker = (ColorPicker)findViewById(R.id.brush_color_picker);
        mBackgroundColorPicker = (ColorPicker)findViewById(R.id.background_color_picker);

        mBrushPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBrushPaint.setColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(ContextCompat.getColor(getApplicationContext(), R.color.white));




    }

    @Override
    public void onGlobalLayout() {

        mBitmapHolder.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        mBitmapHolderWidth = mBitmapHolder.getWidth();
        mBitnapHolderHeight = mBitmapHolder.getHeight();

        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        mBitmap = Bitmap.createBitmap(mBitmapHolderWidth, mBitnapHolderHeight, conf);
        mBitmapHolder.setImageBitmap(mBitmap);

        mCanvas = new Canvas(mBitmap);
        mCanvas.drawRect(0, 0, mBitmapHolderWidth, mBitnapHolderHeight, mBackgroundPaint);

        mBitmapHolder.setOnTouchListener(this);
        mBrushColorPicker.setOnClickListener(this);
        mBackgroundColorPicker.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.brush_color_picker){

            int color = mBrushColorPicker.getSelectedColor();
            mBrushPaint.setColor(color);

        } else if (v.getId() == R.id.background_color_picker) {

            int color = mBackgroundColorPicker.getSelectedColor();
            mBackgroundPaint.setColor(color);
            mCanvas.drawRect(0, 0, mBitmapHolderWidth, mBitnapHolderHeight, mBackgroundPaint);
            mBitmapHolder.invalidate();

        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        mCanvas.drawCircle(x, y, BRUSH_STROKE_WIDTH, mBrushPaint);
        mBitmapHolder.invalidate();

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.doodle_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, ACCESS_STORAGE_PERMISSION_FOR_SAVE_REQUEST_CODE);
                } else {
                    saveImage();
                }
                return true;
            case R.id.action_set_wallpaper:
                setWallpaper();

            default:
                return super.onOptionsItemSelected(item);

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == ACCESS_STORAGE_PERMISSION_FOR_SAVE_REQUEST_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];

                if (permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        saveImage();
                    }
                }
            }
        }
    }


    private void saveImage() {

        String root = Environment.getExternalStorageDirectory().toString();
        File rootDir = new File(root);
        rootDir.mkdirs();
        String fname = "doodle.jpg";
        File file = new File(rootDir, fname);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setWallpaper(){

        WallpaperManager myWallpaperManager = WallpaperManager.getInstance(getApplicationContext());
        try {
            myWallpaperManager.setBitmap(mBitmap);
            Toast.makeText(this, getResources().getString(R.string.wallpaper_changed), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
