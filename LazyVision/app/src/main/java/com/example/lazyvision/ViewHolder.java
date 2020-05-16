package com.example.lazyvision;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

public class ViewHolder extends RecyclerView.ViewHolder {

    View mview;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        mview=itemView;
    }

    public void setDetails(Context ctx, String image, String label)
    {
        TextView mlabel=mview.findViewById(R.id.label_text);
        ImageView mImageView = mview.findViewById(R.id.image_View);
        Log.e("IMAGE",image+".........");
        mlabel.setText(label);

        Picasso.get().load(image).into(mImageView);

    }
    public void setDetails1(Context ctx, String image, String label)
    {
        TextView mlabel=mview.findViewById(R.id.label_text);
        ImageView mImageView = mview.findViewById(R.id.image_View);
        Log.e("IMAGE",image+".........");
        mlabel.setText(label);

        Picasso.get().load(image).into(mImageView);

    }
}
