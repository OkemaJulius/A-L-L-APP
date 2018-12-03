package com.kinstalk.m4;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.kinstalk.m4.publicaicore.AICoreManager;
import com.kinstalk.m4.publicapi.activity.M4BaseActivity;

public class MainActivity extends M4BaseActivity {

    public static final String AUTHORITY = "com.kinstalk.m4.ownerprovider";

    public static final String OWNER_PATH = "owner";

    public static final String LOCATION_PATH = "location";

    public static final Uri OWNER_URI = Uri.parse("content://" + AUTHORITY + "/" + OWNER_PATH);

    public static final Uri LOCATION_URI = Uri.parse("content://" + AUTHORITY + "/" + LOCATION_PATH);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView resultTxtView = (TextView) findViewById(R.id.tv_result);

        findViewById(R.id.add_user).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(OwnerColumn.UID, 2);
                contentValues.put(OwnerColumn.USERCODE, 1);
                contentValues.put(OwnerColumn.ACCESSTOKEN, "token");
                contentValues.put(OwnerColumn.ACCESSEXPIRESIN, 1);
                contentValues.put(OwnerColumn.REFRESHTOKEN, "token");
                contentValues.put(OwnerColumn.REFRESHEXPIRESIN, 1);
                contentValues.put(OwnerColumn.DUDUAPPID, 1);
                contentValues.put(OwnerColumn.DUDUVOIPACCOUNT, 1);
                contentValues.put(OwnerColumn.DUDUVOIPPWD, 1);

                Uri insertUri = getContentResolver().insert(OWNER_URI, contentValues);
                if (insertUri != null) {
                }
            }
        });

        findViewById(R.id.find_user).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Cursor cursor = null;
                try {
                    cursor = getContentResolver().query(LOCATION_URI, null, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        resultTxtView.setText(cursor.getString(cursor.getColumnIndex(LocationColumn.LOCATIONADDRESS)));
                    }
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }
        });

        findViewById(R.id.find_location).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Cursor cursor = null;
                try {
                    cursor = getContentResolver().query(OWNER_URI, null, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        resultTxtView.setText(cursor.getString(cursor.getColumnIndex(OwnerColumn.UID)));
                    }
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }
        });

        findViewById(R.id.play_music).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AICoreManager.getInstance(MainActivity.this).textRequest("我要听音乐");

//                Intent intent = new Intent("com.kinstalk.her.help.activity.PlayActivity");
//                intent.putExtra("INTENT_AI_TITLE", "沃视频DEMO");
//                intent.putExtra("INTENT_AI_PLAYURL", "http://157.255.23.15/storage/resource/50/20180102115037_48062.mp4");
//                intent.putExtra("INTENT_AI_MEDIATYPE", 2);
////                intent.putExtra("INTENT_AI_PLAYURL", "http://157.255.23.15/storage/resource/48/20180615160755_49272.mp3");
////                intent.putExtra("INTENT_AI_MEDIATYPE", 1);
//                startActivity(intent);
            }
        });
    }
}
