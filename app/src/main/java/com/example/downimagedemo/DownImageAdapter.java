package com.example.downimagedemo;

import static androidx.recyclerview.widget.RecyclerView.*;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class DownImageAdapter extends RecyclerView.Adapter<ViewHolder> {


    interface UpdateListener {
        void clickImg(int position);
        void update(int selectSize);
    }

    List<Integer> selectIndex = new ArrayList<>();
    List<String> images;

    UpdateListener call;

    public ArrayList<String> getSelectImages() {
        ArrayList<String> selectedList = new ArrayList<>();
        for (int i = 0; i < selectIndex.size(); i++) {
            selectedList.add(images.get(selectIndex.get(i)));
        }
        return selectedList;
    }

    public int getSelectImageSize() {
        return selectIndex.size();
    }

    public void clearSelected() {
        selectIndex.clear();
        call.update(0);
        notifyDataSetChanged();
    }

    public DownImageAdapter(List<String> images, UpdateListener listener) {
        this.images = images;
        this.call = listener;
        call.update(0);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ImageView iv = holder.itemView.findViewById(R.id.ivItem);
        TextView selectedTv = holder.itemView.findViewById(R.id.tvItemSelected);
        ImageView selectIv = holder.itemView.findViewById(R.id.ivItemSelect);
        iv.setOnClickListener(view -> {
            call.clickImg(position);
        });
        selectIv.setOnClickListener(view -> {
            selectIndex.add(position);
            call.update(selectIndex.size());
            notifyDataSetChanged();
        });
        selectedTv.setOnClickListener(view -> {
            selectIndex.remove((Integer) position);
            call.update(selectIndex.size());
            notifyDataSetChanged();
        });
        int current = selectIndex.indexOf(position);
        if (current != -1) {
            selectedTv.setText(current + 1 + "");
            selectedTv.setVisibility(View.VISIBLE);
            selectIv.setVisibility(View.INVISIBLE);
        } else {
            selectedTv.setVisibility(View.INVISIBLE);
            selectIv.setVisibility(View.VISIBLE);
        }
        Glide.with(iv).load(images.get(position)).into(iv);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }


}


class ImageViewHolder extends ViewHolder {

    public ImageViewHolder(@NonNull View itemView) {
        super(itemView);
    }
}