package com.leon.aicenter.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.leon.aicenter.MainActivity;
import com.leon.aicenter.R;
import com.leon.aicenter.bean.SceneBean;
import com.leon.aicenter.component.TintImageView;
import com.leon.aicenter.util.ConstantUtils;

import java.util.ArrayList;
import java.util.List;

public class SceneAdapter extends BaseAdapter {
    private List<SceneBean> sceneBeans;
    private final LayoutInflater inflater;
    private final Handler handler;
    private SceneBean draggingBean;
    private boolean notDragging = true;
    private TintImageView ivBackground;
    private ImageView ivSelect;

    public SceneAdapter(Context context, List<SceneBean> sceneBeans, Handler handler) {
        this.sceneBeans = new ArrayList<>(sceneBeans);
        inflater = LayoutInflater.from(context);
        this.handler = handler;
    }

    public void updateList(List<SceneBean> sceneBeans) {
        if (null != sceneBeans)
            this.sceneBeans = new ArrayList<>(sceneBeans);
        notifyDataSetChanged();
    }

    public void deleteScene(int id) {
        sceneBeans.removeIf(sceneBean -> sceneBean.getId() == id);
        notifyDataSetChanged();
    }

    public void startDrag(int start) {
        notDragging = false;
        handler.sendMessage(handler.obtainMessage(MainActivity.HANDLER_DRAG, ConstantUtils.TYPE_SCENE, ConstantUtils.DRAG_START));
        draggingBean = sceneBeans.get(start);
        sceneBeans.remove(start);
        sceneBeans.add(start, null);
        notifyDataSetChanged();
    }

    public void update(int start, int down) {
        SceneBean bean = sceneBeans.get(start);
        sceneBeans.remove(start);
        sceneBeans.add(down, bean);
        notifyDataSetChanged();
    }

    public void stopDrag() {
        notDragging = true;
        handler.sendMessage(handler.obtainMessage(MainActivity.HANDLER_DRAG, ConstantUtils.TYPE_SCENE, ConstantUtils.DRAG_STOP));
        for (int i = 0; i < sceneBeans.size(); i++) {
            if (sceneBeans.get(i) == null) {
                sceneBeans.set(i, draggingBean);
                notifyDataSetChanged();
                break;
            }
        }
    }

    public void clickItem(int position) {
        if (notDragging) {
            ivBackground.setImageTintList(null);
            if (MainActivity.modifying) {
                handler.sendMessage(handler.obtainMessage(MainActivity.HANDLER_MODIFY_SCENE, sceneBeans.get(position)));
            } else if (MainActivity.selectMode) {
                sceneBeans.get(position).setSelected(!sceneBeans.get(position).isSelected());
                ivSelect.setVisibility(sceneBeans.get(position).isSelected() ? View.VISIBLE : View.INVISIBLE);
                handler.sendMessage(handler.obtainMessage(MainActivity.HANDLER_ITEM_SELECTED, sceneBeans.get(position)));
            } else {
                handler.sendMessage(handler.obtainMessage(MainActivity.HANDLER_SCENE_PRESS, MainActivity.CommandType.scene.ordinal(), 0, sceneBeans.get(position)));
            }
        }
    }

    public List<SceneBean> getSceneBeans() {
        return sceneBeans;
    }

    @Override
    public int getCount() {
        return sceneBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return sceneBeans.get(position);
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
            viewHolder.name = convertView.findViewById(R.id.tv_scene_name);
            viewHolder.background = convertView.findViewById(R.id.iv_background);
            viewHolder.tintBackground = convertView.findViewById(R.id.tiv_background);
            viewHolder.delete = convertView.findViewById(R.id.iv_delete);
            viewHolder.select = convertView.findViewById(R.id.iv_selected);
            viewHolder.room = convertView.findViewById(R.id.tv_room_name);
            viewHolder.type = convertView.findViewById(R.id.tv_scene_type);
            viewHolder.connection = convertView.findViewById(R.id.iv_connection);
            viewHolder.drag = convertView.findViewById(R.id.iv_drag);
            convertView.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) convertView.getTag();
        ivBackground = viewHolder.tintBackground;
        ivSelect = viewHolder.select;
        ivBackground.setVisibility(MainActivity.modifying ? View.GONE : View.VISIBLE);
        viewHolder.background.setVisibility(MainActivity.modifying ? View.VISIBLE : View.GONE);
        ivBackground.setImageTintList(null);
        if (sceneBeans.get(position) == null) {
            ivBackground.setImageResource(R.drawable.common_background);
            viewHolder.background.setImageResource(R.drawable.common_background);
            viewHolder.delete.setVisibility(View.INVISIBLE);
            viewHolder.room.setVisibility(View.INVISIBLE);
            viewHolder.name.setVisibility(View.INVISIBLE);
            viewHolder.type.setVisibility(View.INVISIBLE);
            viewHolder.select.setVisibility(View.INVISIBLE);
            viewHolder.connection.setVisibility(View.INVISIBLE);
            viewHolder.drag.setVisibility(View.INVISIBLE);
        } else {
            try {
                ivBackground.setImageResource(ConstantUtils.SCENE_PICTURE[Integer.parseInt(sceneBeans.get(position).getPicture())]);
                viewHolder.background.setImageResource(ConstantUtils.SCENE_PICTURE[Integer.parseInt(sceneBeans.get(position).getPicture())]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            viewHolder.drag.setVisibility(MainActivity.modifying ? View.VISIBLE : View.INVISIBLE);
            viewHolder.delete.setVisibility(MainActivity.modifying ? View.VISIBLE : View.INVISIBLE);
            viewHolder.delete.setOnClickListener(v -> {
                if (notDragging) {
                    handler.sendMessage(handler.obtainMessage(MainActivity.HANDLER_DELETE_SCENE, sceneBeans.get(position).getId(), 0));
                }
            });
            viewHolder.room.setVisibility(View.VISIBLE);
            viewHolder.name.setVisibility(View.VISIBLE);

            switch (sceneBeans.get(position).getType()) {
                case switcher:
                    viewHolder.type.setVisibility(View.VISIBLE);
                    viewHolder.type.setText(R.string.text_button);
                    break;
                case panel:
                    viewHolder.type.setVisibility(View.VISIBLE);
                    viewHolder.type.setText(R.string.text_voice);
                    break;
                default:
                    viewHolder.type.setVisibility(View.INVISIBLE);
                    break;
            }
            viewHolder.select.setVisibility(MainActivity.selectMode ? (sceneBeans.get(position).isSelected() ? View.VISIBLE : View.INVISIBLE) : View.INVISIBLE);
            viewHolder.connection.setVisibility(sceneBeans.get(position).getStatusEnum() != null && sceneBeans.get(position).getStatusEnum() == ConstantUtils.Status.offline ? View.VISIBLE : View.INVISIBLE);
            viewHolder.name.setText(sceneBeans.get(position).getName());
            viewHolder.room.setText(sceneBeans.get(position).getRoom());
            ivBackground.setOnClickListener(v -> clickItem(position));
            viewHolder.background.setOnClickListener(v -> clickItem(position));
        }
        return convertView;
    }

    private static class ViewHolder {
        TintImageView tintBackground;
        ImageView background;
        ImageView connection;
        ImageView delete;
        ImageView select;
        ImageView drag;
        TextView name;
        TextView room;
        TextView type;
    }
}
