package com.example.autoslideshowapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.os.Handler;

import java.util.Timer;
import java.util.TimerTask;



public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_CODE = 100;

    Timer mTimer;
    ImageView ImageView;

    //メンバ変数
    Cursor cursor;

    private boolean autoflag = false;

    Handler mHandler = new Handler();

    Button StartButton;
    Button Move_on_Button;
    Button ReturnButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContentsInfo();
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
            }
            // Android 5系以下の場合
        } else {
            getContentsInfo();
        }

        ImageView = (ImageView) findViewById(R.id.imageView);
        ReturnButton = (Button) findViewById(R.id.return_button);
        StartButton = (Button) findViewById(R.id.start_button);
        Move_on_Button = (Button) findViewById(R.id.move_on_button);

        StartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!autoflag) { //再生
                    StartButton.setText("停止");
                    Move_on_Button.setEnabled(false);
                    ReturnButton.setEnabled(false);
                    mTimer = new Timer();
                    mTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {

                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {

                                    if (cursor.moveToNext()) {


                                    } else {
                                        cursor.moveToFirst();
                                    }

                                    ImagePickup();


                                }

                            });
                        }
                    }, 2000, 2000);
                } else { //停止
                    StartButton.setText("再生");
                    Move_on_Button.setEnabled(true);
                    ReturnButton.setEnabled(true);
                    if (mTimer != null) {
                        mTimer.cancel();
                        mTimer = null;
                    }
                }
                autoflag=!autoflag;


            }
        });

        Move_on_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cursor.moveToNext()) {


                } else {
                    cursor.moveToFirst();
                }

                ImagePickup();

            }

        });

        ReturnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cursor.moveToPrevious()) {

                } else {
                    cursor.moveToLast();
                }
                ImagePickup();
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo();
                }
                break;
            default:
                break;
        }
    }



    private void ImagePickup() {
        int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
        Long id = cursor.getLong(fieldIndex);
        Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

        ImageView = (ImageView) findViewById(R.id.imageView);
        ImageView.setImageURI(imageUri);
    }

    private void getContentsInfo() {
        // 画像の情報を取得する
        ContentResolver resolver = getContentResolver();
        cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
                null, // 項目(null = 全項目)
                null, // フィルタ条件(null = フィルタなし)
                null, // フィルタ用パラメータ
                null // ソート (null ソートなし)

        );

        if (cursor.moveToFirst()) {
            ImagePickup();

        }

    }

}

