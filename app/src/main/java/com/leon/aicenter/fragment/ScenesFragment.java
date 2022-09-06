package com.leon.aicenter.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.leon.aicenter.R;
import com.leon.aicenter.adapter.SceneAdapter;
import com.leon.aicenter.bean.SceneBean;
import com.leon.aicenter.component.DragGridView;

import java.util.ArrayList;
import java.util.List;

public class ScenesFragment extends Fragment {
    private final List<SceneBean> sceneBeans;
    private final Handler handler;
    private SceneAdapter sceneAdapter;
    private final List<Integer> deletedScene = new ArrayList<>();
    private TextView tvNone;

    public ScenesFragment(List<SceneBean> sceneBeans, Handler handler) {
        this.sceneBeans = new ArrayList<>(sceneBeans);
        this.handler = handler;
    }

    public List<SceneBean> getSceneBeans() {
        return sceneBeans;
    }

    public List<SceneBean> getChangedSceneBeans() {
        return sceneAdapter == null ? null : sceneAdapter.getSceneBeans();
    }

    public void updateScene(SceneBean sceneBean) {
        if (sceneBean != null)
            for (SceneBean dev : sceneBeans)
                if (dev.getMac() != null && dev.getMac().equals(sceneBean.getMac()) && dev.getChannel() == sceneBean.getChannel()) {
                    dev.setStatus(sceneBean.getStatus());
                    break;
                }
        if (sceneAdapter != null)
            sceneAdapter.updateList(sceneBean != null ? sceneBeans : null);
    }

    public int getDeletedSize() {
        return deletedScene.size();
    }

    public void deleteScene(int id) {
        if (sceneAdapter != null) {
            sceneAdapter.deleteScene(id);
            tvNone.setVisibility(sceneAdapter.getSceneBeans().size() == 0 ? View.VISIBLE : View.INVISIBLE);
        } else
            for (SceneBean bean : sceneBeans)
                if (id == bean.getId()) {
                    deletedScene.add(id);
                    break;
                }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_devices_list, container, false);
        DragGridView gridView = view.findViewById(R.id.grid_device);
        gridView.setDevice(false);
        sceneAdapter = new SceneAdapter(getActivity(), sceneBeans, handler);
        for (Integer id : deletedScene)
            sceneAdapter.deleteScene(id);
        deletedScene.clear();
        gridView.setAdapter(sceneAdapter);
        gridView.setNumColumns(5);
        gridView.setVerticalSpacing(30);
        tvNone = view.findViewById(R.id.tv_no_device);
        tvNone.setVisibility(sceneBeans.size() == 0 ? View.VISIBLE : View.INVISIBLE);
        tvNone.setText(R.string.text_no_scene);
        tvNone.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.mipmap.pic_no_scene, 0, 0);
        gridView.setVisibility(sceneBeans.size() == 0 ? View.INVISIBLE : View.VISIBLE);
        return view;
    }
}
