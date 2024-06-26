package com.example.food_app.view.home.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.food_app.R;
import com.example.food_app.helper.CallBack;
import com.example.food_app.model.DrawerItemModel;

public class DrawerItemCustomAdapter extends ArrayAdapter<DrawerItemModel> {
    Context mContext;
    int layoutResourceId;
    DrawerItemModel[] data;
    CallBack.ItemClickListener itemClickListener;

    public DrawerItemCustomAdapter(Context mContext, int layoutResourceId, DrawerItemModel[] data, CallBack.ItemClickListener itemClickListener) {
        super(mContext, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.data = data;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = LayoutInflater.from(parent.getContext()).inflate(layoutResourceId, parent, false);

        ImageView imageViewIcon = listItem.findViewById(R.id.imgItemDrawer);
        TextView textViewName = listItem.findViewById(R.id.tvTitle);
        listItem.setOnClickListener(view -> itemClickListener.onClick(position));

        DrawerItemModel folder = data[position];
        imageViewIcon.setImageResource(folder.icon);
        textViewName.setText(folder.name);

        return listItem;
    }
}
