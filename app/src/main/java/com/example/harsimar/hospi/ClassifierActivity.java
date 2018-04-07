/*
 * Copyright 2016 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.harsimar.hospi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Matrix;
import android.media.Image;
import android.media.Image.Plane;
import android.media.ImageReader;
import android.media.ImageReader.OnImageAvailableListener;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Trace;
import android.support.multidex.MultiDex;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class ClassifierActivity extends CameraActivity implements OnImageAvailableListener {

    static {
        System.loadLibrary("native-lib");
    }

    public native String stringFromJNI();
    // These are the settings for the original v1 Inception model. If you want to
    // use a model that's been produced from the TensorFlow for Poets codelab,
    // you'll need to set IMAGE_SIZE = 299, IMAGE_MEAN = 128, IMAGE_STD = 128,
    // INPUT_NAME = "Mul:0", and OUTPUT_NAME = "final_result:0".
    // You'll also need to update the MODEL_FILE and LABEL_FILE paths to point to
    // the ones you produced.
    //
    // To use v3 Inception model, strip the DecodeJpeg Op from your retrained
    // model first:
    //
    // python strip_unused.py \
    // --input_graph=<retrained-pb-file> \
    // --output_graph=<your-stripped-pb-file> \
    // --input_node_names="Mul" \
    // --output_node_names="final_result" \
    // --input_binary=true
    //
    // Note: the actual number of classes for Inception is 1001, but the output layer size is 1008.

    /*
    private static final int NUM_CLASSES = 1008;
    private static final int INPUT_SIZE = 224;
    private static final int IMAGE_MEAN = 117;
    private static final float IMAGE_STD = 1;
    private static final String INPUT_NAME = "input:0";
    private static final String OUTPUT_NAME = "output:0";
    */
    private static final int NUM_CLASSES = 23;
    private static final int INPUT_SIZE = 299;
    private static final int IMAGE_MEAN = 128;
    private static final float IMAGE_STD = 128;
    private static final String INPUT_NAME = "Mul:0";
    private static final String OUTPUT_NAME = "final_result:0";
    public static final int transmitThreshold=10;
    public static Bitmap inferenceBitmap;

    private static final String MODEL_FILE = "file:///android_asset/tensorflow_inception_graph.pb";
    private static final String LABEL_FILE =
            "file:///android_asset/imagenet_comp_graph_label_strings.txt";


    private Classifier classifier;

    private int previewWidth = 0;
    private int previewHeight = 0;
    private byte[][] yuvBytes;
    private int[] rgbBytes = null;
    public static Bitmap rgbFrameBitmap = null;

    private Bitmap cropCopyBitmap;
    private Bitmap opencvBitmap;

    private boolean computing = false;


    private TextView resultsView;
    public static Button captureBtn;
    private ImageView captureImageView;

    private BorderedText borderedText;
    private float lastWeight=0,currentWeight=0;
    private boolean saveFiles=true;

    private DoInference doInference ;


    @Override
    protected int getLayoutId() {
        return R.layout.camera_connection_fragment;
    }

    @Override
    protected int getDesiredPreviewFrameSize() {
        return INPUT_SIZE;
    }


    @Override
    public void onPreviewSizeChosen(final Size size, final int rotation) {
        Log.d(TAG, stringFromJNI());

            try {
            classifier =
                    TensorFlowImageClassifier.create(
                            getAssets(),
                            MODEL_FILE,
                            LABEL_FILE,
                            NUM_CLASSES,
                            INPUT_SIZE,
                            IMAGE_MEAN,
                            IMAGE_STD,
                            INPUT_NAME,
                            OUTPUT_NAME);
        } catch (final Exception e) {
            throw new RuntimeException("Error initializing TensorFlow!", e);
        }

        resultsView = (TextView) findViewById(R.id.results);
        captureBtn = (Button) findViewById(R.id.capture_button);
        captureImageView = (ImageView) findViewById(R.id.captured_imageView);
        previewWidth = size.getWidth();
        previewHeight = size.getHeight();


        rgbBytes = new int[previewWidth * previewHeight];
        rgbFrameBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Config.ARGB_8888);

        opencvBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Config.ARGB_8888);

        yuvBytes = new byte[3][];

        captureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("harsimarSingh","Capture sequence initiated");
                doInference=new DoInference();
                doInference.execute();
            }
        });
    }
    public class DoInference extends AsyncTask<Void,Integer,List<Classifier.Recognition>>{

        @Override
        protected List<Classifier.Recognition> doInBackground(Void... params) {
            final List<Classifier.Recognition> results =
                    classifier.recognizeImage(inferenceBitmap);
            cropCopyBitmap = Bitmap.createBitmap(inferenceBitmap);
            return results;

        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            captureBtn.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onPostExecute(List<Classifier.Recognition> results) {
            captureBtn.setVisibility(View.VISIBLE);
            super.onPostExecute(results);
                captureImageView.setImageBitmap(cropCopyBitmap);

            Log.d("harsimarSingh","inference done");
            saveBitmap(saveFiles,inferenceBitmap);
            String transmitString=" * ";

            for(int i=0;i<results.size();i++) {
                Classifier.Recognition rec = results.get(i);
                if((int)(rec.getConfidence()*100)>transmitThreshold) {
                    transmitString +=getStringAfterComma(rec.getTitle())+ " * ";
                    //transmitString += rec.getTitle() + " * ";
                }
            }
            resultsView.setText(transmitString);
            writeToFile(transmitString);
          // ClassifierActivity.captureBtn.performClick();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
    }

    private String getStringAfterComma(String title) {
        return title.substring(title.lastIndexOf(",")+1);
    }

    private void saveBitmap(boolean saveFiles, Bitmap inferenceBitmap) {
        if(saveFiles==false)return;
        else {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File direct = new File(Environment.getExternalStorageDirectory() + "/hyperworks/data");
            if (!direct.exists()) {
                File wallpaperDirectory = new File("/sdcard/hyperworks/data/");
                wallpaperDirectory.mkdirs();
            }

            File file = new File(new File("/sdcard/hyperworks/data/"), timeStamp+".jpg");
            if (file.exists()) {
                file.delete();
            }
            try {
                FileOutputStream out = new FileOutputStream(file);
                inferenceBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onImageAvailable(final ImageReader reader) {
        Image image = null;

        try {
            image = reader.acquireLatestImage();
            if (image == null) {
                return;
            }

            if (computing) {
                image.close();
                return;
            }
            computing = true;

            Trace.beginSection("imageAvailable");

            final Plane[] planes = image.getPlanes();
            fillBytes(planes, yuvBytes);

            final int yRowStride = planes[0].getRowStride();
            final int uvRowStride = planes[1].getRowStride();
            final int uvPixelStride = planes[1].getPixelStride();

            ImageUtils.convertYUV420ToARGB8888(
                    yuvBytes[0],
                    yuvBytes[1],
                    yuvBytes[2],
                    rgbBytes,
                    previewWidth,
                    previewHeight,
                    yRowStride,
                    uvRowStride,
                    uvPixelStride,
                    false);
            computing = false;
            image.close();
        } catch (final Exception e) {
            if (image != null) {
                image.close();
            }
            Trace.endSection();
            return;
        }
        rgbFrameBitmap.setPixels(rgbBytes, 0, previewWidth, 0, 0, previewWidth, previewHeight);
        if(topLeftY+cropDialogHeight>320){
            cropDialogHeight=320-topLeftY;
        }
        if(topLeftX+cropDialogWidth>480){
            cropDialogHeight=480-topLeftY;
        }

        Bitmap newBitmap=Bitmap.createBitmap(rgbFrameBitmap,topLeftX,topLeftY
                ,cropDialogWidth,cropDialogHeight);
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        inferenceBitmap = Bitmap.createBitmap(newBitmap ,
                0, 0, 299, 299, matrix, true);



    }

    private String getDate(){
        DateFormat dfDate = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        String date=dfDate.format(Calendar.getInstance().getTime());
        return date ;
    }
    private void writeToFile(String saveString) {
       String timeStampedString=getDate()+"\t"+saveString+"\r\n";
        File direct = new File(Environment.getExternalStorageDirectory() + "/hyperworks/data");
        if (!direct.exists()) {
            File responseDir = new File("/sdcard/hyperworks/data/");
            responseDir.mkdirs();
        }

        File file = new File(new File("/sdcard/hyperworks/data/"), "response.txt");
        try {
            FileOutputStream out = new FileOutputStream(file,true);
            out.write(timeStampedString.getBytes());
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        direct = new File(Environment.getExternalStorageDirectory() + "/hyperworks/inferences");
        if (!direct.exists()) {
            File responseDir = new File("/sdcard/hyperworks/inferences/");
            responseDir.mkdirs();
        }

        file = new File(new File("/sdcard/hyperworks/inferences/"), "output.txt");
        try {
            FileOutputStream out = new FileOutputStream(file);
            out.write(saveString.getBytes());
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }
}