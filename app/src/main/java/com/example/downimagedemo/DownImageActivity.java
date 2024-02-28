package com.example.downimagedemo;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.flyjingfish.openimagelib.OpenImage;
import com.flyjingfish.openimagelib.beans.OpenImageUrl;
import com.flyjingfish.openimagelib.enums.MediaType;
import com.flyjingfish.openimagelib.listener.SourceImageViewIdGet;
import com.hitomi.tilibrary.transfer.TransferConfig;
import com.hitomi.tilibrary.transfer.Transferee;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.vansz.glideimageloader.GlideImageLoader;

import java.io.File;
import java.nio.file.Files;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class DownImageActivity extends AppCompatActivity {
    DownImageAdapter mAdapter;
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int QUEUE_CAPACITY = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_down_image);

        ArrayList<String> images = new ArrayList<>(MainActivity.images);
        RecyclerView rv = findViewById(R.id.rv_images);
        TextView selectHint = findViewById(R.id.tvSelectHint);
        TextView tvSave = findViewById(R.id.tvSave);
        mAdapter = new DownImageAdapter(images, new DownImageAdapter.UpdateListener() {
            @Override
            public void clickImg(int position) {
                Transferee transfer = Transferee.getDefault(rv.getContext());
                transfer.apply(TransferConfig.build()
                        .setImageLoader(GlideImageLoader.with(rv.getContext()))
                        .create()
                ).show();
            }

            @Override
            public void update(int selectSize) {
                selectHint.setText("点击清除⬅️    已选择" + selectSize + "/" + images.size());

            }
        });
        selectHint.setOnClickListener(view -> {
            mAdapter.clearSelected();
        });
        rv.setAdapter(mAdapter);
        rv.setLayoutManager(new GridLayoutManager(this, 3));
        tvSave.setOnClickListener(view -> {
            XXPermissions.with(DownImageActivity.this).permission(
                    Permission.MANAGE_EXTERNAL_STORAGE
            ).request(
                    (permissions, allGranted) -> download()
            );

        });
    }

    boolean downloading = false;

    private void download() {
        if (downloading) {
            Toast.makeText(this, "下载中...", Toast.LENGTH_SHORT).show();
            return;
        }
        ArrayList<String> downImages = mAdapter.getSelectImages();
        if (downImages.size() == 0) {
            Toast.makeText(this, "请先选择图片", Toast.LENGTH_SHORT).show();
            return;
        }

        downloading = true;
        final ExecutorService executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(QUEUE_CAPACITY), new ThreadPoolExecutor.DiscardOldestPolicy());

        Semaphore semaphore = new Semaphore(10); // 每次最多下载10张图片
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("准备下载...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        final int[] downloadCount = {0};
        for (int i = 0; i < downImages.size(); i++) {
            String downImage = downImages.get(i);
            Glide.with(this).download(downImage).listener(new RequestListener<File>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, @Nullable Object model, @NonNull Target<File> target, boolean isFirstResource) {
                    downloadCount[0] +=1;
                    progressDialog.setMessage("下载中："+downloadCount[0] + "/" + downImages.size());
                    if(downloadCount[0] == downImages.size()-1){
                        progressDialog.dismiss();
                        downloading = false;
                        Toast.makeText(DownImageActivity.this, "下载完成", Toast.LENGTH_SHORT).show();
                    }

                    return false;
                }

                @Override
                public boolean onResourceReady(@NonNull File resource, @NonNull Object model, Target<File> target, @NonNull DataSource dataSource, boolean isFirstResource) {
                    downloadCount[0] +=1;
                    progressDialog.setMessage("下载中："+downloadCount[0] + "/" + downImages.size());
                    if(downloadCount[0] == downImages.size()-1){
                        progressDialog.dismiss();
                        downloading = false;
                        Toast.makeText(DownImageActivity.this, "下载完成", Toast.LENGTH_SHORT).show();
                    }

                    File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/ADownload");
                    dir.mkdirs();
                    FileUtil.copyFile(resource.getAbsolutePath(), dir.getAbsolutePath() + "/" + UUID.randomUUID() + ".png");
                    return false;
                }
            }).preload();

        }
    }
}