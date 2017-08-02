/**
 * HorizontalListViewAdapter.java
 * YUNEEC_SDK_ANDROID
 *
 * Copyright @ 2016 Yuneec.
 * All rights reserved.
 *
 */

package com.longkai.stcarcontrol.st_exp.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.longkai.stcarcontrol.st_exp.R;
import com.longkai.stcarcontrol.st_exp.Utils.BitmapUtil;


/**
 * 用来添加水印的类
 *
 * @author zp
 * @version 1.0, 17-2-28
 * @since 1.0
 */
public class HorizontalListViewAdapter extends BaseAdapter{

    private int[] mIconIDs;
    private Context mContext;
    private LayoutInflater mInflater;
    Bitmap iconBitmap;

    private int mSelectIndex = -1;

    public HorizontalListViewAdapter(Context context, int[] ids){
        this.mContext = context;
        this.mIconIDs = ids;
        mInflater=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);//LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mIconIDs.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView==null){
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.horizontal_list_item, null);
            holder.mImage = (ImageView)convertView.findViewById(R.id.img_list_item);
            convertView.setTag(holder);
        } else {
            holder=(ViewHolder)convertView.getTag();
        }
        if(position == mSelectIndex){
            Log.i("Adapter", "position = " + position);
            convertView.setSelected(true);
            holder.mImage.setSelected(true);
            holder.mImage.setImageResource(mIconIDs[position]);
        } else {
            convertView.setSelected(false);
            holder.mImage.setSelected(false);
            holder.mImage.setImageResource(mIconIDs[position]);
        }
        return convertView;
    }

    /**
     * listview的适配器
     *
     * @author zp
     * @version 1.0, 17-2-28
     * @since 1.0
     */
    private static class ViewHolder {
        private ImageView mImage;
    }


    private Bitmap getPropThumnail(int id){

        Drawable d = mContext.getResources().getDrawable(id);
        Bitmap b = BitmapUtil.drawableToBitmap(d);
        //Bitmap bb = BitmapUtil.getRoundedCornerBitmap(b, 100);
//        int w = mContext.getResources().getDimensionPixelOffset(R.dimen.thumnail_default_width);
//        int h = mContext.getResources().getDimensionPixelSize(R.dimen.thumnail_default_height);

//        Bitmap thumBitmap = ThumbnailUtils.extractThumbnail(b, w, h);
        return b;
    }


    public void setSelectIndex(int i){
        mSelectIndex = i;

    }
}