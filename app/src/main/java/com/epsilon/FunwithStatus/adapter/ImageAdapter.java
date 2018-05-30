package com.epsilon.FunwithStatus.adapter;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.epsilon.FunwithStatus.R;
import com.epsilon.FunwithStatus.utills.Constants;
import com.epsilon.FunwithStatus.utills.Helper;
import com.epsilon.FunwithStatus.utills.ImageConverter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;


public class ImageAdapter extends BaseAdapter {

    Activity activity;
    private LayoutInflater inflater;
    public Resources res;

    public ImageAdapter(Activity a) {
        this.activity = a;
        inflater = (LayoutInflater) activity.getSystemService(activity.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return Constants.imageCategoryData.size();
    }

    @Override
    public Object getItem(int i) {
        return Constants.imageCategoryData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        final MyViewHolder viewHolder;
        if (view == null) {
            view = inflater.inflate(R.layout.image_item, viewGroup, false);
            viewHolder = new MyViewHolder(view);
            view.setTag(viewHolder);


        } else {
            viewHolder = (MyViewHolder) view.getTag();
        }
        Glide.with(activity).load(Constants.imageCategoryData.get(i).getImage()).placeholder(R.drawable.icon).into(viewHolder.tvimage);
        viewHolder.tvimage_name.setText(Constants.imageCategoryData.get(i).getName());
        return view;
    }


    class MyViewHolder {
        public TextView tvimage_name;
        public ImageView tvimage;


        public MyViewHolder(View item) {
            tvimage_name = (TextView) item.findViewById(R.id.tvimage_name);
            tvimage = (ImageView) item.findViewById(R.id.tvimage);

        }
    }
}

