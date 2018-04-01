package com.example.jingyue.mindpalace;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.FileProvider;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.GenericJson;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.language.v1.CloudNaturalLanguage;
import com.google.api.services.language.v1.CloudNaturalLanguageRequest;
import com.google.api.services.language.v1.CloudNaturalLanguageScopes;
import com.google.api.services.language.v1.model.AnalyzeEntitiesRequest;
import com.google.api.services.language.v1.model.AnalyzeEntitiesResponse;
import com.google.api.services.language.v1.model.Document;
import com.google.api.services.language.v1.model.Entity;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.api.services.language.v1.CloudNaturalLanguageRequestInitializer;
import com.google.api.services.language.v1.model.AnalyzeSentimentRequest;
import com.google.api.services.language.v1.CloudNaturalLanguage;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.language.v1.model.AnalyzeSentimentResponse;
import com.google.api.services.language.v1.model.Document;
import com.google.gson.Gson;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;
import java.util.Locale;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import 	android.graphics.BitmapFactory;

import java.io.FileInputStream;
import java.util.Map;
import java.util.TreeMap;
import java.util.HashMap;

import com.example.jingyue.mindpalace.data.MindContract;
import com.example.jingyue.mindpalace.data.MindDbHelper;
import com.example.jingyue.mindpalace.data.MindContract.MindEntry;


//////////////////////////////////
//TODO:
//@Override
//protected void onDestroy() {
//   mDbHelper.close();
//   super.onDestroy(); //TODO: need to be added in onDestroy of MainActivity
//}
/////////////////////////////////

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity{
    private static final String CLOUD_VISION_API_KEY = "AIzaSyBD-58JXajoizqSzCVVyYkfsmEvxvfbChQ";
    //BuildConfig.API_KEY;
    public static final String FILE_NAME = "temp.jpg";
    private static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    private static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";
    private static final int MAX_LABEL_RESULTS = 10;
    private static final int MAX_DIMENSION = 1200;

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int GALLERY_PERMISSIONS_REQUEST = 0;
    private static final int GALLERY_IMAGE_REQUEST = 1;
    public static final int CAMERA_PERMISSIONS_REQUEST = 2;
    public static final int CAMERA_IMAGE_REQUEST = 3;

    private SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        json_paser_for_label("");
        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        MindDbHelper dbHelper = new MindDbHelper(this);
        mDb = dbHelper.getWritableDatabase();

        final Button button_start = (Button) findViewById(R.id.start);
        button_start.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Intent next = new Intent(getApplicationContext(), OptionScreen.class);
                // startActivity(next);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder
                        .setMessage(R.string.dialog_select_prompt)
                        .setPositiveButton(R.string.dialog_select_gallery, (dialog, which) -> startGalleryChooser())
                        .setNegativeButton(R.string.dialog_select_camera, (dialog, which) -> startCamera());
                builder.create().show();
            }
        });

        //mImageDetails = findViewById(R.id.image_details);
        //mMainImage = findViewById(R.id.main_image);
    }

    /*
        Google cloud Vision code snippet
    */
    public void startGalleryChooser () {
        if (PermissionUtils.requestPermission(this, GALLERY_PERMISSIONS_REQUEST, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select a photo"),
                    GALLERY_IMAGE_REQUEST);
        }
    }

    public void startCamera() {
        if (PermissionUtils.requestPermission(
                this,
                CAMERA_PERMISSIONS_REQUEST,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", getCameraFile());
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(intent, CAMERA_IMAGE_REQUEST);
        }
    }

    public File getCameraFile() {
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return new File(dir, FILE_NAME);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            uploadImage(data.getData());
        } else if (requestCode == CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", getCameraFile());
            uploadImage(photoUri);
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_PERMISSIONS_REQUEST:
                if (PermissionUtils.permissionGranted(requestCode, CAMERA_PERMISSIONS_REQUEST, grantResults)) {
                    startCamera();
                }
                break;
            case GALLERY_PERMISSIONS_REQUEST:
                if (PermissionUtils.permissionGranted(requestCode, GALLERY_PERMISSIONS_REQUEST, grantResults)) {
                    startGalleryChooser();
                }
                break;
        }
    }

    public void uploadImage(Uri uri) {
            /*
                ImageView mMainImage = findViewById(R.id.main_view);
                File file = new File("mnt/sdcard/Download/images.jpg");
                //Environment.getExternalStorageDirectory().getPath() + uri.getPath());
                FileInputStream fis = new FileInputStream(file);
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inSampleSize = 2;
                Bitmap res = BitmapFactory.decodeStream(fis, null, opts);
                mMainImage.setImageBitmap(res);

             */
        if (uri != null) {
            // scale the image to save on bandwidth

            db_image(uri);
        } else {
            Log.d(TAG, "Image picker gave us a null image.");
            Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
        }
    }
    private void db_image(Uri uri){
        db_image(uri, Boolean.FALSE);
    }

    private void db_image(Uri uri, Boolean query){
        //TODO: remove repeated items
        if(query == Boolean.FALSE){
            //TODO: pass repeated item
        }//Otherwise don't do it

        Bitmap bitmap = null;
        try {
            bitmap = scaleBitmapDown(
                    MediaStore.Images.Media.getBitmap(getContentResolver(), uri),
                    MAX_DIMENSION);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String s = callCloudVision(bitmap);
        //TODO: database
        Log.d("lkl", s);
        //json_paser_for_label(s);
    }

    private void db_text(String s){
        db_text(s, Boolean.FALSE);
    }

    private void db_text(String s, Boolean query){
        //TODO: remove repeated items
        if(query == Boolean.FALSE){
            //TODO: pass repeated item
        }//Other

        AsyncTask<Object, Object, Object> txt = new GGLanguage(s);
        try {
            String ret = (String) txt.execute().get();
            json_paser_for_text(ret);
            //TODO: databse
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private Vision.Images.Annotate preparelabelRequest(Bitmap bitmap) throws IOException {
        HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        VisionRequestInitializer requestInitializer =
                new VisionRequestInitializer(CLOUD_VISION_API_KEY) {
                    /**
                     * We override this so we can inject important identifying fields into the HTTP
                     * headers. This enables use of a restricted cloud platform API key.
                     */
                    @Override
                    protected void initializeVisionRequest(VisionRequest<?> visionRequest)
                            throws IOException {
                        super.initializeVisionRequest(visionRequest);

                        String packageName = getPackageName();
                        visionRequest.getRequestHeaders().set(ANDROID_PACKAGE_HEADER, packageName);

                        String sig = PackageManagerUtils.getSignature(getPackageManager(), packageName);

                        visionRequest.getRequestHeaders().set(ANDROID_CERT_HEADER, sig);
                    }
                };

        Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
        builder.setVisionRequestInitializer(requestInitializer);

        Vision vision = builder.build();

        BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                new BatchAnnotateImagesRequest();
        batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
            AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

            // Add the image
            Image base64EncodedImage = new Image();
            // Convert the bitmap to a JPEG
            // Just in case it's a format that Android understands but Cloud Vision
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();

            // Base64 encode the JPEG
            base64EncodedImage.encodeContent(imageBytes);
            annotateImageRequest.setImage(base64EncodedImage);

            // add the features we want
            annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                Feature labelDetection = new Feature();
                labelDetection.setType("LABEL_DETECTION");
                labelDetection.setMaxResults(MAX_LABEL_RESULTS);
                add(labelDetection);
            }});

            // Add the list of one thing to the request
            add(annotateImageRequest);
        }});

        Vision.Images.Annotate annotateRequest =
                vision.images().annotate(batchAnnotateImagesRequest);
        // Due to a bug: requests to Vision API containing large images fail when GZipped.
        annotateRequest.setDisableGZipContent(true);
        Log.d(TAG, "created Cloud Vision request object, sending request");

        return annotateRequest;
    }
    private Vision.Images.Annotate preparelandmarkRequest(Bitmap bitmap) throws IOException {
        HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        VisionRequestInitializer requestInitializer =
                new VisionRequestInitializer(CLOUD_VISION_API_KEY) {
                    /**
                     * We override this so we can inject important identifying fields into the HTTP
                     * headers. This enables use of a restricted cloud platform API key.
                     */
                    @Override
                    protected void initializeVisionRequest(VisionRequest<?> visionRequest)
                            throws IOException {
                        super.initializeVisionRequest(visionRequest);

                        String packageName = getPackageName();
                        visionRequest.getRequestHeaders().set(ANDROID_PACKAGE_HEADER, packageName);

                        String sig = PackageManagerUtils.getSignature(getPackageManager(), packageName);

                        visionRequest.getRequestHeaders().set(ANDROID_CERT_HEADER, sig);
                    }
                };

        Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
        builder.setVisionRequestInitializer(requestInitializer);

        Vision vision = builder.build();

        BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                new BatchAnnotateImagesRequest();
        batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
            AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

            // Add the image
            Image base64EncodedImage = new Image();
            // Convert the bitmap to a JPEG
            // Just in case it's a format that Android understands but Cloud Vision
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();

            // Base64 encode the JPEG
            base64EncodedImage.encodeContent(imageBytes);
            annotateImageRequest.setImage(base64EncodedImage);

            // add the features we want
            annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                Feature labelDetection = new Feature();
                labelDetection.setType("LANDMARK_DETECTION");
                labelDetection.setMaxResults(MAX_LABEL_RESULTS);
                add(labelDetection);
            }});

            // Add the list of one thing to the request
            add(annotateImageRequest);
        }});

        Vision.Images.Annotate annotateRequest =
                vision.images().annotate(batchAnnotateImagesRequest);
        // Due to a bug: requests to Vision API containing large images fail when GZipped.
        annotateRequest.setDisableGZipContent(true);
        Log.d(TAG, "created Cloud Vision request object, sending request");

        return annotateRequest;
    }
    private Vision.Images.Annotate preparelogoRequest(Bitmap bitmap) throws IOException {
        HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        VisionRequestInitializer requestInitializer =
                new VisionRequestInitializer(CLOUD_VISION_API_KEY) {
                    /**
                     * We override this so we can inject important identifying fields into the HTTP
                     * headers. This enables use of a restricted cloud platform API key.
                     */
                    @Override
                    protected void initializeVisionRequest(VisionRequest<?> visionRequest)
                            throws IOException {
                        super.initializeVisionRequest(visionRequest);

                        String packageName = getPackageName();
                        visionRequest.getRequestHeaders().set(ANDROID_PACKAGE_HEADER, packageName);

                        String sig = PackageManagerUtils.getSignature(getPackageManager(), packageName);

                        visionRequest.getRequestHeaders().set(ANDROID_CERT_HEADER, sig);
                    }
                };

        Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
        builder.setVisionRequestInitializer(requestInitializer);

        Vision vision = builder.build();

        BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                new BatchAnnotateImagesRequest();
        batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
            AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

            // Add the image
            Image base64EncodedImage = new Image();
            // Convert the bitmap to a JPEG
            // Just in case it's a format that Android understands but Cloud Vision
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();

            // Base64 encode the JPEG
            base64EncodedImage.encodeContent(imageBytes);
            annotateImageRequest.setImage(base64EncodedImage);

            // add the features we want
            annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                Feature labelDetection = new Feature();
                labelDetection.setType("LOGO_DETECTION");
                labelDetection.setMaxResults(MAX_LABEL_RESULTS);
                add(labelDetection);
            }});

            // Add the list of one thing to the request
            add(annotateImageRequest);
        }});

        Vision.Images.Annotate annotateRequest =
                vision.images().annotate(batchAnnotateImagesRequest);
        // Due to a bug: requests to Vision API containing large images fail when GZipped.
        annotateRequest.setDisableGZipContent(true);
        Log.d(TAG, "created Cloud Vision request object, sending request");

        return annotateRequest;
    }

    private static class DetectionTask extends AsyncTask<Object, Void, String> {
        private final WeakReference<MainActivity> mActivityWeakReference;
        private Vision.Images.Annotate mRequest;

        DetectionTask(MainActivity activity, Vision.Images.Annotate annotate) {
            mActivityWeakReference = new WeakReference<>(activity);
            mRequest = annotate;
        }

        @Override
        protected String doInBackground(Object... params) {
            try {
                Log.d(TAG, "created Cloud Vision request object, sending request");
                BatchAnnotateImagesResponse response = mRequest.execute();
                return convertResponseToString(response);

            } catch (GoogleJsonResponseException e) {
                Log.d(TAG, "failed to make API request because " + e.getContent());
            } catch (IOException e) {
                Log.d(TAG, "failed to make API request because of other IOException " +
                        e.getMessage());
            }
            return "Cloud Vision API request failed. Check logs for details.";
        }
    }

    private String callCloudVision(final Bitmap bitmap) {
        // Switch text to loading
        //mImageDetails.setText(R.string.loading_message);

        // Do the real work in an async task, because we need to use the network anyway
        try {
            AsyncTask<Object, Void, String> labelDetectionTask = new DetectionTask(this, preparelabelRequest(bitmap));
            AsyncTask<Object, Void, String> landmarkDetectionTask = new DetectionTask(this, preparelandmarkRequest(bitmap));
            AsyncTask<Object, Void, String> logoDetectionTask = new DetectionTask(this, preparelogoRequest(bitmap));
            return logoDetectionTask.execute().get();
        } catch (IOException e) {
            return "failed to make API request because of other IOException " +
                    e.getMessage();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return "";
    }

    private Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

    private static String convertResponseToString(BatchAnnotateImagesResponse response) {
        StringBuilder message = new StringBuilder("\n\n");
        Log.d("response", response.toString());
        Log.d("get response", response.getResponses().toString());
        Log.d("get response 0", response.getResponses().get(0).toString());
        Log.d("get response 0", response.getResponses().get(0).getLabelAnnotations().toString());
        return response.getResponses().get(0).getLabelAnnotations().toString();
    }

    //json_parser for text
    //input: string
    //output: list of strings
    private List<String> json_paser_for_text(String js){
        try {
            List<String> list = new ArrayList<>();
            js = "{\"entities\":[{\"mentions\":[{\"text\":{\"beginOffset\":-1,\"content\":\"Text messaging\"},\"type\":\"COMMON\"},{\"text\":{\"beginOffset\":-1,\"content\":\"act\"},\"type\":\"COMMON\"}],\"metadata\":{},\"name\":\"Text messaging\",\"salience\":0.69890994,\"type\":\"OTHER\"},{\"mentions\":[{\"text\":{\"beginOffset\":-1,\"content\":\"messages\"},\"type\":\"COMMON\"}],\"metadata\":{},\"name\":\"messages\",\"salience\":0.06526089,\"type\":\"WORK_OF_ART\"},{\"mentions\":[{\"text\":{\"beginOffset\":-1,\"content\":\"characters\"},\"type\":\"COMMON\"}],\"metadata\":{},\"name\":\"characters\",\"salience\":0.052793197,\"type\":\"OTHER\"},{\"mentions\":[{\"text\":{\"beginOffset\":-1,\"content\":\"tablets\"},\"type\":\"COMMON\"}],\"metadata\":{},\"name\":\"tablets\",\"salience\":0.03986082,\"type\":\"CONSUMER_GOOD\"},{\"mentions\":[{\"text\":{\"beginOffset\":-1,\"content\":\"laptops\"},\"type\":\"COMMON\"}],\"metadata\":{},\"name\":\"laptops\",\"salience\":0.03986082,\"type\":\"CONSUMER_GOOD\"},{\"mentions\":[{\"text\":{\"beginOffset\":-1,\"content\":\"desktops\"},\"type\":\"COMMON\"}],\"metadata\":{},\"name\":\"desktops\",\"salience\":0.03986082,\"type\":\"OTHER\"},{\"mentions\":[{\"text\":{\"beginOffset\":-1,\"content\":\"devices\"},\"type\":\"COMMON\"}],\"metadata\":{},\"name\":\"devices\",\"salience\":0.027865255,\"type\":\"CONSUMER_GOOD\"},{\"mentions\":[{\"text\":{\"beginOffset\":-1,\"content\":\"users\"},\"type\":\"COMMON\"}],\"metadata\":{},\"name\":\"users\",\"salience\":0.017794142,\"type\":\"PERSON\"},{\"mentions\":[{\"text\":{\"beginOffset\":-1,\"content\":\"mobile phones\"},\"type\":\"COMMON\"}],\"metadata\":{},\"name\":\"mobile phones\",\"salience\":0.017794142,\"type\":\"CONSUMER_GOOD\"}],\"language\":\"en\"}\n";
            JSONObject json = new JSONObject(js);
            Log.d("fuck", json.getJSONArray("entities").toString());
            JSONArray arr = json.getJSONArray("entities");
            for(int i = 0; i < arr.length(); i++){
                JSONObject obj = (JSONObject) arr.get(i);
                Log.d("fuck", (String) obj.get("name"));
                list.add((String) obj.get("name"));
            }
            return list;
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("fuck", "fuck");
            return null;
        }
    }

    private List<String> json_paser_for_label(String js){
        try {
            List<String> list = new ArrayList<>();
            js = "[{\"description\":\"map\",\"mid\":\"/m/04_tb\",\"score\":0.9879048466682434,\"topicality\":0.9879048466682434}, {\"description\":\"ecoregion\",\"mid\":\"/m/0cblv\",\"score\":0.6792283654212952,\"topicality\":0.6792283654212952}, {\"description\":\"area\",\"mid\":\"/m/0n0j\",\"score\":0.6720095872879028,\"topicality\":0.6720095872879028}, {\"description\":\"water resources\",\"mid\":\"/m/015s2f\",\"score\":0.6240043044090271,\"topicality\":0.6240043044090271}, {\"description\":\"world\",\"mid\":\"/m/09nm_\",\"score\":0.6175522804260254,\"topicality\":0.6175522804260254}]";
            JSONArray arr = new JSONArray(js);

            for(int i = 0; i < arr.length(); i++){
                JSONObject obj = (JSONObject) arr.get(i);
                Log.d("fuck", (String) obj.get("description"));
                list.add((String) obj.get("description"));
            }
            return list;
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("fuck", "fuck");
            return null;
        }
    }







    /*
    DataBase Utilities
    */


    private List<String> analyzer (String dumUri, int dumTime, String dumLocation, String[] dumFeatures){
        //also add this uri to the database
        Map<String, Integer> values = new HashMap<String, Integer>();
        for (int i = 0; i < 10; i++){
            values.put(dumFeatures[i], 9 - i);
        }

        List<String> uris = new ArrayList<String>(); //uris is a list of uri's by precedence
        Cursor cursorT = getTimedItems(dumTime);
        while(cursorT.moveToNext()){
            String item = cursorT.getString(cursorT.getColumnIndexOrThrow(MindEntry.COLUMN_URI));
            uris.add(item);
        }
        cursorT.close();

        List<Cursor> cursorFs = new LinkedList<Cursor>();
        for (String feature : dumFeatures){
            Cursor cursorF = getFeaturedItems(feature);
            if (cursorF.getCount() != 0){
                cursorFs.add(cursorF);
            }
        }

        Map<Integer, String> rating = new TreeMap<Integer, String>();
        for (Cursor cursor : cursorFs){
            while (cursor.moveToNext()){
                String uri = cursor.getString(cursor.getColumnIndexOrThrow(MindEntry.COLUMN_URI));
                List<String> features = new ArrayList<String>();
                features.add(cursor.getString(cursor.getColumnIndexOrThrow(MindEntry.COLUMN_FEATURE1)));
                features.add(cursor.getString(cursor.getColumnIndexOrThrow(MindEntry.COLUMN_FEATURE2)));
                features.add(cursor.getString(cursor.getColumnIndexOrThrow(MindEntry.COLUMN_FEATURE3)));
                features.add(cursor.getString(cursor.getColumnIndexOrThrow(MindEntry.COLUMN_FEATURE4)));
                features.add(cursor.getString(cursor.getColumnIndexOrThrow(MindEntry.COLUMN_FEATURE5)));
                features.add(cursor.getString(cursor.getColumnIndexOrThrow(MindEntry.COLUMN_FEATURE6)));
                features.add(cursor.getString(cursor.getColumnIndexOrThrow(MindEntry.COLUMN_FEATURE7)));
                features.add(cursor.getString(cursor.getColumnIndexOrThrow(MindEntry.COLUMN_FEATURE8)));
                features.add(cursor.getString(cursor.getColumnIndexOrThrow(MindEntry.COLUMN_FEATURE9)));
                features.add(cursor.getString(cursor.getColumnIndexOrThrow(MindEntry.COLUMN_FEATURE10)));
                int score = 0;
                for (int i = 0; i < 10; i++){
                    Integer value = values.get(features.get(i));
                    if(value != null){
                        score += (9 - i)*value;
                    }
                }
                rating.put(score, uri);
            }
            cursor.close();
        }

        for (Map.Entry<Integer,String> entry : rating.entrySet()){
            uris.add(entry.getValue());
        }

        addNewItem(dumUri, dumTime, dumLocation, dumFeatures);

        return uris;
    }

    private Cursor getUri (String uri){ //could be empty uri
        String[] projection = {MindEntry.COLUMN_URI};
        String selection = MindEntry.COLUMN_URI + " = " + uri;
        return mDb.query(
                MindContract.MindEntry.TABLE_NAME,
                projection,
                selection,
                null,
                null,
                null,
                null
        );
    }

    public List<String> getAllUris (String[] uris){ //
        List<String> lst = new ArrayList<String>();
        for (String uri : uris){
            Cursor cursor = getUri(uri);
            if (cursor.getCount() != 0){
                while(cursor.moveToNext()){
                    lst.add(cursor.getString(cursor.getColumnIndexOrThrow(MindEntry.COLUMN_URI)));
                }
            }
        }
        return lst;
    }


    private Cursor getTimedItems(int time) { //check reletive items based on time
        String[] projection = {MindEntry.COLUMN_URI};
        int bound = 86400; //a day in terms of unix time in seconds
        String uppBound = Integer.toString(time + bound);
        String lowBound = Integer.toString(time - bound);
        String selection = MindEntry.COLUMN_TIME + " BETWEEN " + lowBound + " AND " + uppBound;
        return mDb.query(
                MindContract.MindEntry.TABLE_NAME,
                projection,
                selection,
                null,
                null,
                null,
                MindContract.MindEntry.COLUMN_TIME
        );
    }


    private Cursor getFeaturedItems(String feature){ //checck relative items based on features/tags from Google Cloud
        String[] projection =
                {
                        MindEntry.COLUMN_TIME,
                        MindEntry.COLUMN_FEATURE1,
                        MindEntry.COLUMN_FEATURE2,
                        MindEntry.COLUMN_FEATURE3,
                        MindEntry.COLUMN_FEATURE4,
                        MindEntry.COLUMN_FEATURE5,
                        MindEntry.COLUMN_FEATURE6,
                        MindEntry.COLUMN_FEATURE7,
                        MindEntry.COLUMN_FEATURE8,
                        MindEntry.COLUMN_FEATURE9,
                        MindEntry.COLUMN_FEATURE10
                };

        String selection =
                MindEntry.COLUMN_FEATURE1 + " = " + feature + " OR " +
                        MindEntry.COLUMN_FEATURE2 + " = " + feature + " OR " +
                        MindEntry.COLUMN_FEATURE3 + " = " + feature + " OR " +
                        MindEntry.COLUMN_FEATURE4 + " = " + feature + " OR " +
                        MindEntry.COLUMN_FEATURE5 + " = " + feature + " OR " +
                        MindEntry.COLUMN_FEATURE6 + " = " + feature + " OR " +
                        MindEntry.COLUMN_FEATURE7 + " = " + feature + " OR " +
                        MindEntry.COLUMN_FEATURE8 + " = " + feature + " OR " +
                        MindEntry.COLUMN_FEATURE9 + " = " + feature + " OR " +
                        MindEntry.COLUMN_FEATURE10 + " = " + feature;

        return mDb.query(
                MindContract.MindEntry.TABLE_NAME,
                projection,
                selection,
                null,
                null,
                null,
                null
        );
    }

    private long addNewItem(String uri, int time, String location, String[] features) {
        //return a unique _id
        if (features.length != 10)
        {
            throw new IllegalArgumentException
                    ("must be 10 features");
        }
        ContentValues cv = new ContentValues();
        cv.put(MindContract.MindEntry.COLUMN_URI, uri);
        cv.put(MindContract.MindEntry.COLUMN_TIME, time);
        cv.put(MindContract.MindEntry.COLUMN_LOCATION, location);
        cv.put(MindContract.MindEntry.COLUMN_FEATURE1, features[0]);
        cv.put(MindContract.MindEntry.COLUMN_FEATURE2, features[1]);
        cv.put(MindContract.MindEntry.COLUMN_FEATURE3, features[2]);
        cv.put(MindContract.MindEntry.COLUMN_FEATURE4, features[3]);
        cv.put(MindContract.MindEntry.COLUMN_FEATURE5, features[4]);
        cv.put(MindContract.MindEntry.COLUMN_FEATURE6, features[5]);
        cv.put(MindContract.MindEntry.COLUMN_FEATURE7, features[6]);
        cv.put(MindContract.MindEntry.COLUMN_FEATURE8, features[7]);
        cv.put(MindContract.MindEntry.COLUMN_FEATURE9, features[8]);
        cv.put(MindContract.MindEntry.COLUMN_FEATURE10, features[9]);
        return mDb.insert(MindContract.MindEntry.TABLE_NAME, null, cv);
    }

}

class GGLanguage extends AsyncTask<Object, Object, Object> {
    private static final String API_KEY = "AIzaSyBD-58JXajoizqSzCVVyYkfsmEvxvfbChQ";
    private String textToBeAnalyzed = "";
    private AnalyzeEntitiesResponse response = null;
    public GGLanguage(String s) {
        textToBeAnalyzed = s;
    }

    @Override
    protected AnalyzeEntitiesResponse doInBackground(Object... params) {

        // following DDC's code
        HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        CloudNaturalLanguage.Builder builder = new CloudNaturalLanguage.Builder(httpTransport, jsonFactory, null);
        builder.setCloudNaturalLanguageRequestInitializer(new CloudNaturalLanguageRequestInitializer(API_KEY));
        CloudNaturalLanguage naturalLanguageAPI = builder.build();
        AnalyzeEntitiesRequest analyzeentityRequest = new AnalyzeEntitiesRequest();

        // the parameters that are passed in
                //SentimentScoreDatabaseHandler ssDB = (SentimentScoreDatabaseHandler) params[1];

        Document document = new Document();
        document.setType("PLAIN_TEXT");
        document.setContent(textToBeAnalyzed);
        analyzeentityRequest.setDocument(document);
        Log.d("gog", "fuck");
        Log.d("gog", analyzeentityRequest.toString());

        try {
            CloudNaturalLanguage.Documents.AnalyzeEntities sentimentRequest = naturalLanguageAPI.documents().analyzeEntities(analyzeentityRequest);

            //Log.d("gog", sentimentRequest.toString());
            //Log.d("gog", "fuck");
            response = sentimentRequest.execute();
            //Log.d("fuck", response.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }
}