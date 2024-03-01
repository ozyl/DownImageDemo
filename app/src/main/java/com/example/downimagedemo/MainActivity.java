package com.example.downimagedemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    static ArrayList<String> images = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainActivity _that = this;
        findViewById(R.id.clickText).setOnClickListener(v->{

            new Thread(new Runnable() {
                @Override
                public void run() {
                    images.clear();
                    for (int i = 0; i < 800; i++) {
                        images.add("https://img.pconline.com.cn/images/upload/upc/tx/itbbs/2101/25/c1/251135935_1611532823091_mthumb.jpg?i2="+i);
                        images.add("https://img.pconline.com.cn/images/upload/upc/tx/itbbs/2101/25/c1/251135935_1611532823091_mthumb.jpg?i3="+i);
                        images.add("https://img.pconline.com.cn/images/upload/upc/tx/itbbs/2101/25/c1/251135935_1611532823091_mthumb.jpg?i4="+i);
                        images.add("https://img.pconline.com.cn/images/upload/upc/tx/itbbs/2101/25/c1/251135935_1611532823091_mthumb.jpg&i5="+i);
                    }
                    Intent intent = new Intent(_that, DownImageActivity.class);
                    startActivity(intent);
                }
            }).start();
        });
        findViewById(R.id.clickText).performClick();
    }


}