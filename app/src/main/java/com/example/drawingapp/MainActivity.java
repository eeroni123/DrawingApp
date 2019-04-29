package com.example.drawingapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.drawingapp.dialogs.BrushSizeChooserFragment;
import com.example.drawingapp.listeners.OnNewBrushSizeSelectedListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import yuku.ambilwarna.AmbilWarnaDialog;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_CAT = "MainActivity";
    private PaintView paintView;
    private int paintColor = 0;

    public void shareDrawing() {
        paintView.setDrawingCacheEnabled(true);
        paintView.invalidate();
        String path = Environment.getExternalStorageDirectory().toString();
        OutputStream fOut = null;
        File file = new File(path,
                "android_drawing_app.png");
        file.getParentFile().mkdirs();

        try {
            file.createNewFile();
        } catch (Exception e) {
            Log.e(LOG_CAT, e.getCause() + e.getMessage());
        }

        try {
            fOut = new FileOutputStream(file);
        } catch (Exception e) {
            Log.e(LOG_CAT, e.getCause() + e.getMessage());
        }

        if (paintView.getDrawingCache() == null) {
            Log.e(LOG_CAT,"Unable to get drawing cache ");
        }

        paintView.getDrawingCache()
                .compress(Bitmap.CompressFormat.JPEG, 85, fOut);

        try {
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            Log.e(LOG_CAT, e.getCause() + e.getMessage());
        }

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        shareIntent.setType("image/png");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "Share image"));


    }
    public void openColorPicker(){
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
    private void brushSizePicker(){

        BrushSizeChooserFragment brushDialog = BrushSizeChooserFragment.NewInstance((int) paintView.getLastBrushSize());
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
        paintView = (PaintView) findViewById(R.id.paintView);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        paintView.init(metrics);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportActionBar().setTitle(null);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.undo:
            case R.id.action_undo:
                paintView.undo();
                return true;
            case R.id.redo:
            case R.id.action_redo:
                paintView.redo();
                return true;
            case R.id.color_palette:
                openColorPicker();
                return true;
            case R.id.brush_size:
                brushSizePicker();
                return true;
            case R.id.erase:
                paintView.erase();
                return true;
            case R.id.clear:
            case R.id.action_clear:
                paintView.clear();
                return true;
            case R.id.share:
                shareDrawing();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
