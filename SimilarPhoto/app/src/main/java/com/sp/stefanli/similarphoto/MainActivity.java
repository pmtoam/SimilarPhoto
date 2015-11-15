package com.sp.stefanli.similarphoto;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.NumberFormat;


public class MainActivity extends Activity {

    private TextView mImg1;
    private TextView mImg2;
    private Bitmap mBmp1;
    private Bitmap mBmp2;
    private TextView mTextView;
    private NumberFormat mNumberFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImg1 = (TextView) findViewById(R.id.img1);
        mImg2 = (TextView) findViewById(R.id.img2);
        mTextView = (TextView) findViewById(R.id.result);
        init();
    }

    private void init() {

        mNumberFormat = NumberFormat.getPercentInstance();
        mNumberFormat.setMaximumIntegerDigits(3);
        mNumberFormat.setMaximumFractionDigits(2);
        //double csdn = 0.20;
        //System.out.println(num.format(csdn));

        mImg1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open(0);
            }
        });

        mImg2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open(1);
            }
        });
    }

    private Handler mHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    mImg1.setText("");
                    mImg1.setBackgroundDrawable(new BitmapDrawable(mBmp1));
                    if (mBmp1 != null && mBmp2 != null) {
                        mTextView.setText("similarity ：" + mNumberFormat.format(BitmapUtil.calSimilarity(mBmp1, mBmp2)));
                    }
                    break;
                case 1:
                    mImg2.setText("");
                    mImg2.setBackgroundDrawable(new BitmapDrawable(mBmp2));
                    if (mBmp1 != null && mBmp2 != null) {
                        mTextView.setText("similarity ：" + mNumberFormat.format(BitmapUtil.calSimilarity(mBmp1, mBmp2)));
                    }
                    break;
            }
        }
    };

    private void open(int requestCode) {
        Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
        openAlbumIntent.setType("image/*");
        startActivityForResult(openAlbumIntent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 0: {
                    ContentResolver resolver = getContentResolver();
                    //照片的原始资源地址
                    Uri originalUri = data.getData();
                    try {
                        //使用ContentProvider通过URI获取原始图片
                        Bitmap photo = MediaStore.Images.Media.getBitmap(resolver, originalUri);
                        if (photo != null) {
                            // TODO 位图过大，内存溢出
                            mBmp1 = photo;
                            mHandler.sendEmptyMessage(0);
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } }
                    break;
                case 1: {
                    ContentResolver resolver = getContentResolver();
                    //照片的原始资源地址
                    Uri originalUri = data.getData();
                    try {
                        //使用ContentProvider通过URI获取原始图片
                        Bitmap photo = MediaStore.Images.Media.getBitmap(resolver, originalUri);
                        if (photo != null) {
                            // TODO 位图过大，内存溢出
                            mBmp2 = photo;
                            mHandler.sendEmptyMessage(1);
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } }
                    break;
                default:
                    break;
            }
        }
    }
}
