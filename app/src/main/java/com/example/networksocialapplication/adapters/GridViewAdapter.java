package com.example.networksocialapplication.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.networksocialapplication.R;

import java.util.ArrayList;

public class GridViewAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Uri>  mListImage;

    public GridViewAdapter(Context context, ArrayList<Uri> listImage) {
        mContext = context;
        mListImage = listImage;
    }

    @Override
    public int getCount() {
        return mListImage.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.grid_item_image, null);
        ViewHolder holder = new ViewHolder();
        holder.mImageView = view.findViewById(R.id.img_grid);

        if (mListImage != null){
            holder.mImageView.setImageURI(mListImage.get(position));
        }
        return view;
    }

    public class ViewHolder{
        ImageView mImageView;
    }
}
