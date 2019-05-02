package com.example.drawingapp.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.drawingapp.PaintView;
import com.example.drawingapp.R;
import com.example.drawingapp.listeners.OnNewBrushSizeSelectedListener;


public class BrushSizeChooserFragment extends DialogFragment {

    private float selectedBrushSize;
    private OnNewBrushSizeSelectedListener mListener;
    private SeekBar brushSizeSeekBar;
    private TextView minValue, maxValue, currentValue;
    private int currentBrushSize;
    private int currentBrushColor;
    private ImageView brushView;


    /**
     * @param listener an implementation of the listener
     */
    public void setOnNewBrushSizeSelectedListener(
            OnNewBrushSizeSelectedListener listener) {
        mListener = listener;
    }

    public static BrushSizeChooserFragment NewInstance(int size, int color) {
        BrushSizeChooserFragment fragment = new BrushSizeChooserFragment();
        Bundle args = new Bundle();
        if (size > 0) {
            args.putInt("current_brush_size", size);
            args.putInt("current_brush_color", color);
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null && args.containsKey("current_brush_size") && args.containsKey("current_brush_color")) {
            int brushSize = args.getInt("current_brush_size", 0);
            int brushColor = args.getInt("current_brush_color", 0);
            if (brushSize > 0) {
                currentBrushSize = brushSize;
                currentBrushColor = brushColor;
            }
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final LayoutInflater inflater = getActivity().getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.fragment_brush_size_chooser, null);
        if (dialogView != null) {
            brushView = dialogView.findViewById(R.id.circle);
            final ViewGroup.LayoutParams params = brushView.getLayoutParams();

            GradientDrawable gd = (GradientDrawable)brushView.getBackground();
            gd.setColor(currentBrushColor);
            gd.setStroke(5, Color.DKGRAY);

            minValue = (TextView) dialogView.findViewById(R.id.text_view_min_value);
            int minSize = getResources().getInteger(R.integer.min_size);
            minValue.setText(minSize + "");

            maxValue = (TextView) dialogView.findViewById(R.id.text_view_max_value);
            maxValue.setText(String.valueOf(getResources().getInteger(R.integer.max_size)));


            currentValue = (TextView) dialogView.findViewById(R.id.text_view_brush_size);
            if (currentBrushSize > 0) {
                currentValue.setText(String.valueOf(currentBrushSize));
                params.height = currentBrushSize;
                params.width = currentBrushSize;
                brushView.setLayoutParams(params);
            }

            brushSizeSeekBar = (SeekBar) dialogView.findViewById(R.id.seek_bar_brush_size);
            brushSizeSeekBar.setProgress(currentBrushSize);
            brushSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                int progressChanged = 0;

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    progressChanged = progress;
                    currentValue.setText(getResources().getString(R.string.label_brush_size) + progress);
                    params.height = progress;
                    params.width = progress;
                    brushView.setLayoutParams(params);

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    mListener.onNewBrushSizeSelected(progressChanged);
                }
            });

        }

        builder.setTitle("Choose new Brush Size")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setView(dialogView);


        return builder.create();

    }
}