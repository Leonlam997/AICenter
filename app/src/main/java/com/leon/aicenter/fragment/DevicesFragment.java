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
import com.leon.aicenter.adapter.DeviceAdapter;
import com.leon.aicenter.bean.DeviceBean;
import com.leon.aicenter.component.DragGridView;

import java.util.ArrayList;
import java.util.List;

public class DevicesFragment extends Fragment {
    private final List<DeviceBean> deviceBeans;
    private final Handler handler;
    private DeviceAdapter deviceAdapter;
    private final List<Integer> deletedDevice = new ArrayList<>();
    private TextView tvNone;

    public DevicesFragment(List<DeviceBean> deviceBeans, Handler handler) {
        this.deviceBeans = new ArrayList<>(deviceBeans);
        this.handler = handler;
    }

    public List<DeviceBean> getDeviceBeans() {
        return deviceBeans;
    }

    public List<DeviceBean> getChangedDeviceBeans() {
        return deviceAdapter == null ? null : deviceAdapter.getDeviceBeans();
    }

    public void updateDevice(DeviceBean deviceBean) {
        if (null != deviceBean) {
            for (DeviceBean dev : deviceBeans)
                if (dev.getMac().equals(deviceBean.getMac()) && dev.getChannel() == deviceBean.getChannel()) {
                    dev.setStatus(deviceBean.getStatus());
                    break;
                }
        }
        if (deviceAdapter != null)
            deviceAdapter.updateList(null != deviceBean ? deviceBeans : null);
    }

    public int getDeletedSize() {
        return deletedDevice.size();
    }

    public void deleteDevice(int id) {
        if (deviceAdapter != null) {
            deviceAdapter.deleteDevice(id);
            tvNone.setVisibility(deviceAdapter.getDeviceBeans().size() == 0 ? View.VISIBLE : View.INVISIBLE);
        } else
            for (DeviceBean bean : deviceBeans)
                if (id == bean.getId()) {
                    deletedDevice.add(id);
                    break;
                }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_devices_list, container, false);
        DragGridView gridView = view.findViewById(R.id.grid_device);
        gridView.setDevice(true);
        deviceAdapter = new DeviceAdapter(getActivity(), deviceBeans, handler);
        for (Integer id : deletedDevice)
            deviceAdapter.deleteDevice(id);
        deletedDevice.clear();
        gridView.setAdapter(deviceAdapter);
        gridView.setNumColumns(4);
        gridView.setVerticalSpacing(30);
        tvNone = view.findViewById(R.id.tv_no_device);
        tvNone.setVisibility(deviceBeans.size() == 0 ? View.VISIBLE : View.INVISIBLE);
        tvNone.setText(R.string.text_no_device);
        tvNone.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.mipmap.pic_no_device, 0, 0);
        gridView.setVisibility(deviceBeans.size() == 0 ? View.INVISIBLE : View.VISIBLE);
        return view;
    }
}
