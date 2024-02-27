package com.example.downimagedemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainActivity _that = this;
        findViewById(R.id.clickText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> images = new ArrayList<>();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (images.size() <= 1000) {
                            images.add("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fsafe-img.xhscdn.com%2Fbw1%2F2b7fed7b-e4a5-4298-8566-bd8e39783d2e%3FimageView2%2F2%2Fw%2F1080%2Fformat%2Fjpg&refer=http%3A%2F%2Fsafe-img.xhscdn.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1711651005&t=131117d85b397f6016b84afd5ef83502");
                            images.add("https://up.enterdesk.com/edpic_source/51/5a/f2/515af29c511902f8abceb4379e9efe77.jpg");
                            images.add("https://up.enterdesk.com/edpic_source/54/8b/cc/548bccaf7de12adc3e21e5be35dd5cf2.jpg");
                            images.add("https://p9.toutiaoimg.com/origin/pgc-image/6d817289d3b44d53bb6e55aa81e41bd2?from=pc");
                        }
                        Intent intent = new Intent(_that, DownImageActivity.class);
                        intent.putStringArrayListExtra("images",images);
                        startActivity(intent);
                    }
                }).start();
            }
        });
    }


}