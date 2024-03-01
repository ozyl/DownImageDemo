package com.example.downimagedemo;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.util.FixedPreloadSizeProvider;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;
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
        ImageView preview = findViewById(R.id.ivBigPreview);
        ScrollView svPreview = findViewById(R.id.svPreview);
        svPreview.setOnClickListener(view -> {
            svPreview.setVisibility(View.INVISIBLE);
        });
        preview.setOnClickListener(view -> {
            svPreview.setVisibility(View.INVISIBLE);
        });
        FixedPreloadSizeProvider<String> sizeProvider = new FixedPreloadSizeProvider<>(650, 650);

        RecyclerViewPreloader<String> preloader = new RecyclerViewPreloader<>(Glide.with(this), new MyPreloadModelProvider(this, images),
                sizeProvider, 10);
        rv.addOnScrollListener(preloader);
        mAdapter = new DownImageAdapter(images, new DownImageAdapter.UpdateListener() {
            @Override
            public void clickImg(int position) {
                svPreview.setVisibility(View.VISIBLE);
                Glide.with(preview).load(images.get(position)).into(preview);
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
        AtomicInteger count = new AtomicInteger(0);
        for (int i = 0; i < downImages.size(); i++) {
            String downImage = downImages.get(i);
            Glide.with(this).download(downImage).listener(new RequestListener<File>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, @Nullable Object model, @NonNull Target<File> target, boolean isFirstResource) {
                    count.incrementAndGet();
                    extracted(progressDialog, count, downImages);

                    return false;
                }

                @Override
                public boolean onResourceReady(@NonNull File resource, @NonNull Object model, Target<File> target, @NonNull DataSource dataSource, boolean isFirstResource) {
                    count.incrementAndGet();
                    extracted(progressDialog, count, downImages);
                    File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/ADownload");
                    dir.mkdirs();
                    FileUtil.copyFile(resource.getAbsolutePath(), dir.getAbsolutePath() + "/" + UUID.randomUUID() + ".png");
                    return false;
                }
            }).preload();

        }
    }

    private void extracted(ProgressDialog progressDialog, AtomicInteger count, ArrayList<String> downImages) {
        progressDialog.setMessage("下载中：" + count.get() + "/" + downImages.size());
        if (count.get() == downImages.size()) {
            getWindow().getDecorView().postDelayed(progressDialog::dismiss,800);
            downloading = false;
            Toast.makeText(DownImageActivity.this, "下载完成", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        ScrollView svPreview = findViewById(R.id.svPreview);
        if (svPreview.getVisibility() == View.VISIBLE) {
            svPreview.setVisibility(View.INVISIBLE);
        } else {
            super.onBackPressed();
        }
    }
}