package com.example.downimagedemo;

import static androidx.recyclerview.widget.RecyclerView.*;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DownImageAdapter extends RecyclerView.Adapter<ViewHolder> {
    List<String> images;
    public DownImageAdapter(List<String> images) {
        this.images = images;
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

    }

    @Override
    public int getItemCount() {
        return images.size();
    }
}


class ImageViewHolder extends ViewHolder{

    public ImageViewHolder(@NonNull View itemView) {
        super(itemView);
    }
}