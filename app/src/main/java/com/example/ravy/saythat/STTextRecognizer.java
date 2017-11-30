package com.example.ravy.saythat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.util.SparseArray;
import android.graphics.ImageFormat;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.internal.client.FrameMetadataParcel;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.android.gms.vision.text.internal.client.LineBoxParcel;
import com.google.android.gms.vision.text.internal.client.RecognitionOptions;
import com.google.android.gms.vision.text.internal.client.TextRecognizerOptions;
import com.google.android.gms.vision.text.internal.client.zzg;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;


public final class STTextRecognizer extends Detector<TextBlock> {
    private TextRecognizer tr;
    private Context context;
    public Bitmap latestCrop;

    private STTextRecognizer() {
        throw new IllegalStateException("Default constructor called");
    }

    public STTextRecognizer(Context c) {
        context = c;
        tr = new TextRecognizer.Builder(c).build();
    }

    public SparseArray<TextBlock> detect(Frame var1) {

        return tr.detect(CropFrame(var1, 50,50,500,100 ));
    }

    public SparseArray<TextBlock> zza(Frame var1, RecognitionOptions var2) {

        return tr.zza(CropFrame(var1, 50,50,500,50 ), var2);
    }

    private Frame CropFrame(Frame sourceFrame, int x, int y,int cropWidth, int cropHeight)
    {
        Matrix rotaMatrix = new Matrix();
        rotaMatrix.postRotate(90);

        YuvImage yuvimage=new YuvImage(sourceFrame.getGrayscaleImageData().array(),  sourceFrame.getMetadata().getFormat(),  sourceFrame.getMetadata().getWidth(), sourceFrame.getMetadata().getHeight(), null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        yuvimage.compressToJpeg(new Rect(0, 0, sourceFrame.getMetadata().getWidth(), sourceFrame.getMetadata().getHeight()), 100, baos); // Where 100 is the quality of the generated jpeg
        byte[] jpegArray = baos.toByteArray();
        Bitmap bitmap = BitmapFactory.decodeByteArray(jpegArray, 0, jpegArray.length);
        latestCrop = Bitmap.createBitmap(bitmap, x, y, cropWidth,cropHeight, rotaMatrix, false);
        return new Frame.Builder().setBitmap(latestCrop).build();
    }

    public boolean isOperational() {
        return tr.isOperational();
    }

    public void release() {
        tr.release();
    }

}
