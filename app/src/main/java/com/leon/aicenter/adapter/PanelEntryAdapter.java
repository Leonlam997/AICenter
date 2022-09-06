package com.leon.aicenter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.leon.aicenter.R;
import com.leon.aicenter.bean.PanelEntryBean;

import java.util.List;

public class PanelEntryAdapter extends BaseAdapter {
    private final List<PanelEntryBean> panelEntryBeans;
    private final LayoutInflater inflater;

    public PanelEntryAdapter(Context context, List<PanelEntryBean> panelEntryBeans) {
        this.panelEntryBeans = panelEntryBeans;
        inflater = LayoutInflater.from(context);
    }

    public List<PanelEntryBean> getPanelEntryBeans() {
        return panelEntryBeans;
    }

    @Override
    public int getCount() {
        return panelEntryBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return panelEntryBeans.get(position);
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
            convertView = inflater.inflate(R.layout.layout_panel_entry_list, parent, false);
            viewHolder.name = convertView.findViewById(R.id.tv_entry_name);
            convertView.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) convertView.getTag();

        viewHolder.name.setText(panelEntryBeans.get(position).getName());

        return convertView;
    }

    private static class ViewHolder {
        TextView name;
    }
}
