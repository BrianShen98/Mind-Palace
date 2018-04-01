package com.example.jingyue.mindpalace;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
// import android.os.Bundle;
// import android.support.v7.app.AppCompatActivity;
// import android.support.v7.widget.LinearLayoutManager;
// import android.support.v7.widget.RecyclerView;
// import android.support.v7.widget.helper.ItemTouchHelper;
// import android.util.Log;
// import android.view.View;
// import android.widget.EditText;

import java.util.TreeMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.example.jingyue.mindpalace.data.MindContract;
import com.example.jingyue.mindpalace.data.MindDbHelper;
import com.example.jingyue.mindpalace.data.MindContract.MindEntry;


public class MainActivity extends AppCompatActivity {

    private SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MindDbHelper dbHelper = new MindDbHelper(this);
        mDb = dbHelper.getWritableDatabase();
        //request to Google Vision happens here, get back list of 10 most possible features
        String dumUri;
        int dumTime;
        String dumLocation;
        String[] dumFeatures;
        //addNewItem(dumUri, dumTime, dumLocation, dumFeatures);
        
        Map<String, Integer> values = new HashMap<String, Integer>();
        for (int i = 0; i < 10; i++){
            values.put(dumFeatures[i], 9 - i);
        }

        List<String> uris = new ArrayList<String>(); //uris is a list of uri's by precedence
        Cursor cursorT = getTimedItems(dumTime);
        while(cursorT.moveToNext()){
            String item = cursor.getString(getColumnIndexOrThrow(MindEntry.COLUMN_URI));
            uris.add(item);
        }
        cursorT.close();

        List<Cursor> cursorFs = new LinkedList<Cursor>();
        for (String feature : dumFeatures){
            Cursor cursorF = getRelatedItems(feature);
            if (cursorF.getCount() != 0){
                cursorFs.add(cursorF);
            }
        }

        Map<Integer, String> rating = new TreeMap<Integer, String>();
        for (Cursor cursor : cursorFs){
            while (cursor.moveToNext()){
                String uri = cursor.getString(getColumnIndexOrThrow(MindEntry.COLUMN_URI));
                List<String> features = new ArrayList<String>();
                features.add(cursor.getString(getColumnIndexOrThrow(MindEntry.COLUMN_FEATURE1)));
                features.add(cursor.getString(getColumnIndexOrThrow(MindEntry.COLUMN_FEATURE2)));
                features.add(cursor.getString(getColumnIndexOrThrow(MindEntry.COLUMN_FEATURE3)));
                features.add(cursor.getString(getColumnIndexOrThrow(MindEntry.COLUMN_FEATURE4)));
                features.add(cursor.getString(getColumnIndexOrThrow(MindEntry.COLUMN_FEATURE5)));
                features.add(cursor.getString(getColumnIndexOrThrow(MindEntry.COLUMN_FEATURE6)));
                features.add(cursor.getString(getColumnIndexOrThrow(MindEntry.COLUMN_FEATURE7)));
                features.add(cursor.getString(getColumnIndexOrThrow(MindEntry.COLUMN_FEATURE8)));
                features.add(cursor.getString(getColumnIndexOrThrow(MindEntry.COLUMN_FEATURE9)));
                features.add(cursor.getString(getColumnIndexOrThrow(MindEntry.COLUMN_FEATURE10)));
                int score = 0;
                for (int i = 0; i < 10; i++){
                    Integer value = values.get(features[i]);
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



    }


    private Cursor getTimedItems(int time) {
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


    private Cursor getFeaturedItems(String feature){
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