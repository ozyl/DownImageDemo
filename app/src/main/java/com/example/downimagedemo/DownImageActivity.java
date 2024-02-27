package com.example.downimagedemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

public class DownImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_down_image);
        ArrayList<String> images = getIntent().getStringArrayListExtra("images");
        Log.d("","当前"+images.size());
        RecyclerView rv = findViewById(R.id.rv_images);
    }
}