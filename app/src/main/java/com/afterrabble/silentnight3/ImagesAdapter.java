package com.afterrabble.silentnight3;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by rendenyoder on 11/30/17.
 */

public class ImagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private String[] images;

    public ImagesAdapter(Context context, String[] images){
        this.context = context;
        this.images = images;
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
        ((Item)holder).textView.setText(images[position]);
    }

    @Override
    public int getItemCount() {
        return images.length;
    }

    public class Item extends RecyclerView.ViewHolder{
        TextView textView;

        public Item(View itemView){
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.item);
        }
    }
}
