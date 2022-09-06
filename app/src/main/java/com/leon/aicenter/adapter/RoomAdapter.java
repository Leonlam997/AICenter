package com.leon.aicenter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.leon.aicenter.MainActivity;
import com.leon.aicenter.R;
import com.leon.aicenter.bean.RoomBean;
import com.leon.aicenter.component.InputTextDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RoomAdapter extends BaseAdapter {
    private List<RoomBean> roomBeans;
    private RoomBean draggingBean;
    private final LayoutInflater inflater;

    public RoomAdapter(Context context, List<RoomBean> room) {
        this.roomBeans = new ArrayList<>(room);
        inflater = LayoutInflater.from(context);
    }

    public void updateList(List<RoomBean> room) {
        this.roomBeans = new ArrayList<>(room);
        notifyDataSetChanged();
    }

    public void startDrag(int start) {
        draggingBean = roomBeans.get(start);
        roomBeans.remove(start);
        roomBeans.add(start, null);
        notifyDataSetChanged();
    }

    public void update(int start, int down) {
        RoomBean bean = roomBeans.get(start);
        roomBeans.remove(start);
        roomBeans.add(down, bean);
        notifyDataSetChanged();
    }

    public void stopDrag() {
        for (int i = 0; i < roomBeans.size(); i++) {
            if (roomBeans.get(i) == null) {
                roomBeans.set(i, draggingBean);
                notifyDataSetChanged();
                break;
            }
        }
    }

    public List<RoomBean> getRoomBeans() {
        return roomBeans;
    }

    @Override
    public int getCount() {
        return roomBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return roomBeans.get(position);
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
            convertView = inflater.inflate(R.layout.layout_item_room_list, parent, false);
            viewHolder.name = convertView.findViewById(R.id.tv_room_name);
            viewHolder.amount = convertView.findViewById(R.id.tv_device_amount);
            viewHolder.delete = convertView.findViewById(R.id.iv_delete);
            viewHolder.move = convertView.findViewById(R.id.iv_more);
            convertView.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) convertView.getTag();

        if (MainActivity.modifying) {
            if (roomBeans.get(position) == null) {
                viewHolder.name.setVisibility(View.INVISIBLE);
                viewHolder.amount.setVisibility(View.INVISIBLE);
                viewHolder.delete.setVisibility(View.INVISIBLE);
                viewHolder.move.setVisibility(View.INVISIBLE);
            } else {
                viewHolder.amount.setVisibility(View.VISIBLE);
                viewHolder.move.setVisibility(View.VISIBLE);
                viewHolder.name.setVisibility(View.VISIBLE);
                viewHolder.delete.setVisibility(View.VISIBLE);
                viewHolder.delete.setOnClickListener(v -> {
                    roomBeans.remove(position);
                    notifyDataSetChanged();
                });
                viewHolder.move.setImageResource(R.mipmap.ic_drag);
                viewHolder.amount.setVisibility(View.INVISIBLE);
                viewHolder.name.setOnClickListener(v -> {
                    InputTextDialog inputTextDialog = new InputTextDialog(inflater.getContext());
                    inputTextDialog.setHint(R.string.text_input_room_name);
                    inputTextDialog.setText(roomBeans.get(position).getName());
                    inputTextDialog.setCancelable(false);
                    inputTextDialog.setCanceledOnTouchOutside(false);
                    inputTextDialog.setOnDismissListener(dialog -> {
                        if (inputTextDialog.getText() != null && !inputTextDialog.getText().isEmpty() && !roomBeans.get(position).getName().equals(inputTextDialog.getText())) {
                            roomBeans.get(position).setName(inputTextDialog.getText());
                            notifyDataSetChanged();
                        }
                    });
                    inputTextDialog.show();
                });
            }
        } else {
            viewHolder.delete.setVisibility(View.GONE);
            viewHolder.move.setImageResource(R.mipmap.ic_more);
            viewHolder.amount.setVisibility(View.VISIBLE);
        }
        if (roomBeans.get(position) != null) {
            viewHolder.name.setText(roomBeans.get(position).getName());
            viewHolder.amount.setText(String.format(Locale.CHINA, inflater.getContext().getResources().getString(R.string.text_device_scene_amount), roomBeans.get(position).getButtonAmount(), roomBeans.get(position).getSceneAmount()));
        }
        return convertView;
    }

    private static class ViewHolder {
        ImageView delete;
        TextView name;
        TextView amount;
        ImageView move;
    }
}
