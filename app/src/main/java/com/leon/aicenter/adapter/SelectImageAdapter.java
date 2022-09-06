package com.leon.aicenter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.leon.aicenter.R;
import com.leon.aicenter.bean.SelectImageBean;
import com.leon.aicenter.util.ConstantUtils;

import java.util.ArrayList;
import java.util.List;

public class SelectImageAdapter extends BaseAdapter {
    private List<SelectImageBean> selectImageList;
    private final LayoutInflater inflater;

    public SelectImageAdapter(Context context, List<SelectImageBean> selectImageList) {
        this.selectImageList = new ArrayList<>(selectImageList);
        inflater = LayoutInflater.from(context);
    }

    public void updateList(List<SelectImageBean> selectImageList) {
        this.selectImageList = new ArrayList<>(selectImageList);
        notifyDataSetChanged();
    }

    public List<SelectImageBean> getSelectImageList() {
        return selectImageList;
    }

    @Override
    public int getCount() {
        return selectImageList.size();
    }

    @Override
    public Object getItem(int position) {
        return selectImageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.layout_scene, parent, false);
            viewHolder.image = convertView.findViewById(R.id.iv_background);
            viewHolder.selected = convertView.findViewById(R.id.iv_selected);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        try {
            viewHolder.image.setBackgroundResource(ConstantUtils.SCENE_PICTURE[Integer.parseInt(selectImageList.get(position).getName())]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        viewHolder.selected.setVisibility(selectImageList.get(position).isSelected() ? View.VISIBLE : View.INVISIBLE);
        viewHolder.image.setOnClickListener(v -> {
            if (!selectImageList.get(position).isSelected()) {
                for (SelectImageBean bean : selectImageList)
                    bean.setSelected(false);
                selectImageList.get(position).setSelected(true);
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    private static class ViewHolder {
        ImageView image;
        ImageView selected;
    }
}
