package com.example.drawingapp;


import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.drawingapp.dialogs.BrushSizeChooserFragment;
import com.example.drawingapp.listeners.OnNewBrushSizeSelectedListener;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import yuku.ambilwarna.AmbilWarnaDialog;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_CAT = "MainActivity";
    private PaintView paintView;
    private int paintColor = 0;
    private int backgroundColor = Color.WHITE;
    private Toolbar mToolbar_bottom;
    private int STORAGE_PERMISSION_CODE=23;


    public void saveDrawing() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        Bitmap bitmap = paintView.getBitmap();
        if (isExternalStorageWritable()) {
            saveImage(bitmap);
        } else {
            Toast.makeText(getApplicationContext(), "File error", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveImage(Bitmap finalBitmap) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/saved_images");
        myDir.mkdirs();

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fname = "Shutta_" + timeStamp + ".jpg";

        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            Toast.makeText(this, "Image saved.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public void brushColorPicker() {
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(this, paintColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                paintColor = color;
                paintView.setCurrentColor(color);

            }
        });
        colorPicker.show();
    }

    public void backgroundColorPicker() {
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(this, backgroundColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                backgroundColor = color;
                paintView.changeBGColor(backgroundColor);

            }
        });
        colorPicker.show();
    }

    private void brushSizePicker() {

        BrushSizeChooserFragment brushDialog = BrushSizeChooserFragment.NewInstance((int) paintView.getLastBrushSize(), paintView.getCurrentColor());
        brushDialog.setOnNewBrushSizeSelectedListener(new OnNewBrushSizeSelectedListener() {
            @Override
            public void onNewBrushSizeSelected(int newBrushSize) {
                paintView.setBrushSize(newBrushSize);
                paintView.setLastBrushSize(newBrushSize);
            }
        });
        brushDialog.show(getSupportFragmentManager(), "Dialog");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar_bottom = findViewById(R.id.toolbar_bottom);
        mToolbar_bottom.inflateMenu(R.menu.drawing_main);
        mToolbar_bottom.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_undo:
                        paintView.undo();
                        return true;
                    case R.id.action_redo:
                        paintView.redo();
                        return true;
                    case R.id.action_color:
                        if (paintView.isErase()) {
                            Toast.makeText(getApplicationContext(), "Eraser is selected. Deselect eraser to change brush color.",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            brushColorPicker();
                        }
                        return true;
                    case R.id.action_brush_size:
                        brushSizePicker();
                        return true;
                    case R.id.action_erase:
                        paintView.erase();
                        return true;
                    case R.id.action_clear:
                        paintView.clear();
                        return true;
                    case R.id.action_save:
                        saveDrawing();
                        return true;
                    case R.id.background_color:
                        backgroundColorPicker();
                }
                return true;
            }
        });
        paintView = findViewById(R.id.paintView);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        paintView.init(metrics);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.background_color:
                backgroundColorPicker();
                return true;
            case R.id.action_save:
                saveDrawing();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
