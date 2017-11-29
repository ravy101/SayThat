package com.example.ravy.saythat;

import android.util.SparseArray;
import android.widget.TextView;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;


/**
 * Created by ravy on 25/09/2017.
 */

public class STDetectorProcessor implements Detector.Processor<TextBlock> {
   // private GraphicOverlay<OcrGraphic> graphicOverlay;
    private TextView textView;

    STDetectorProcessor(TextView display)
    {
        textView = display;
    }

    @Override
    public void release() {

    }

    @Override
    public void receiveDetections(Detector.Detections<TextBlock> detections) {
        final SparseArray<TextBlock> items = detections.getDetectedItems();
        if (items.size() != 0) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < items.size(); ++i) {
                TextBlock item = items.valueAt(i);
                stringBuilder.append(item.getValue());
                stringBuilder.append("\n");
            }
            textView.setText(stringBuilder.toString());
        }
    }

}
