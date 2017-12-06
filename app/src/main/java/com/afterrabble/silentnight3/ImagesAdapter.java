package com.afterrabble.silentnight3;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

/**
 * Created by rendenyoder on 11/30/17.
 */

public class ImagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private String[] images;
    private String[] imageDates;

    public ImagesAdapter(Context context, String[] images, String[] imageDates){
        this.context = context;
        this.images = images;
        this.imageDates = imageDates;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View row = inflater.inflate(R.layout.custom_row, parent, false);
        Item item = new Item(row);
        return item;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((Item)holder).textView.setText(imageDates[position]);
        ((Item)holder).imageView.setImageURI(Uri.fromFile(new File(images[position])));
    }

    @Override
    public int getItemCount() {
        return images.length;
    }

    public class Item extends RecyclerView.ViewHolder{
        TextView textView;
        ImageView imageView;

        public Item(View itemView){
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.item);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
        }
    }
}
