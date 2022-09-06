package com.leon.aicenter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.leon.aicenter.R;
import com.leon.aicenter.bean.RoomBean;

import java.util.ArrayList;
import java.util.List;

public class SelectRoomListAdapter extends BaseAdapter {
    private List<RoomBean> room;
    private final LayoutInflater inflater;

    public SelectRoomListAdapter(Context context, List<RoomBean> room) {
        this.room = new ArrayList<>(room);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return room.size();
    }

    @Override
    public Object getItem(int position) {
        return room.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void updateList(List<RoomBean> room) {
        this.room = new ArrayList<>(room);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.layout_select_room_list, parent, false);
            viewHolder.name = convertView.findViewById(R.id.tv_room_name);
            viewHolder.select = convertView.findViewById(R.id.iv_selected);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.name.setText(room.get(position).getName());
        viewHolder.select.setVisibility(room.get(position).isSelected() ? View.VISIBLE : View.INVISIBLE);
        return convertView;
    }

    private static class ViewHolder {
        TextView name;
        ImageView select;
    }
}
