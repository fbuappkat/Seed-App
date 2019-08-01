package com.example.kat_app.Adapters;


import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.kat_app.R;

import org.json.JSONArray;
import org.json.JSONException;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.ViewHolder> {
    private static Context context;
    private JSONArray media;


    public MediaAdapter(Context context, JSONArray media) {
        this.context = context;
        this.media = media;
    }

    @NonNull
    @Override
    public MediaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_media, parent, false);
        return new MediaAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MediaAdapter.ViewHolder holder, int position) {
        try {
            String url = media.getJSONObject(position).getString("url");
            holder.bind(url);
        } catch (JSONException e){
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        //Log.d(TAG,"item count: " + updates.size());
        return media.length();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView ivMedia;

        View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            ivMedia = itemView.findViewById(R.id.ivMedia);


        }


        //add in data for specific user's post
        public void bind(final String url) {
            MultiTransformation<Bitmap> multiTransformation = new MultiTransformation<Bitmap>(new CenterCrop(), new RoundedCornersTransformation(20, 0));
            Glide.with(context)
                    .load(url)
                    .apply(RequestOptions.bitmapTransform(multiTransformation))
                    .into(ivMedia);
        }

    }

}