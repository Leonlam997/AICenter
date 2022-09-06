package com.leon.aicenter.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.leon.aicenter.util.ConstantUtils;

import java.io.File;

public class MyHelper extends SQLiteOpenHelper {

    public MyHelper(@Nullable Context context) {
        super(context, ConstantUtils.FILE_BACKUP_DB, null, DatabaseStatic.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseStatic.CREATE_TABLE_VENDOR);
        db.execSQL(DatabaseStatic.CREATE_TABLE_ROOM);
        db.execSQL(DatabaseStatic.CREATE_TABLE_DEVICE);
        db.execSQL(DatabaseStatic.CREATE_TABLE_OWNED);
        db.execSQL(DatabaseStatic.CREATE_TABLE_TASK);
        db.execSQL(DatabaseStatic.CREATE_TABLE_SCENE);

        ContentValues values = new ContentValues();
        values.put(DatabaseStatic.Device.NAME, "LORA");
        values.put(DatabaseStatic.Device.TYPE, 0x0101);
        db.insert(DatabaseStatic.TABLE_DEVICE, null, values);
        values = new ContentValues();
        values.put(DatabaseStatic.Device.NAME, "智能锁");
        values.put(DatabaseStatic.Device.TYPE, 0x0201);
        db.insert(DatabaseStatic.TABLE_DEVICE, null, values);
        values = new ContentValues();
        values.put(DatabaseStatic.Device.NAME, "智能开关单键");
        values.put(DatabaseStatic.Device.DESCRIPTION, "1位LORA单火1本0情景");
        values.put(DatabaseStatic.Device.TYPE, 0x0301);
        values.put(DatabaseStatic.Device.BUTTON_AMOUNT, 1);
        values.put(DatabaseStatic.Device.SCENE_AMOUNT, 0);
        values.put(DatabaseStatic.Device.KIND, 1);
        values.put(DatabaseStatic.Device.PICTURE, "0");
        db.insert(DatabaseStatic.TABLE_DEVICE, null, values);
        values = new ContentValues();
        values.put(DatabaseStatic.Device.NAME, "智能开关双键");
        values.put(DatabaseStatic.Device.DESCRIPTION, "2位LORA单火2本0情景");
        values.put(DatabaseStatic.Device.TYPE, 0x0302);
        values.put(DatabaseStatic.Device.BUTTON_AMOUNT, 2);
        values.put(DatabaseStatic.Device.SCENE_AMOUNT, 0);
        values.put(DatabaseStatic.Device.KIND, 1);
        values.put(DatabaseStatic.Device.PICTURE, "1");
        db.insert(DatabaseStatic.TABLE_DEVICE, null, values);
        values = new ContentValues();
        values.put(DatabaseStatic.Device.NAME, "智能开关三键");
        values.put(DatabaseStatic.Device.DESCRIPTION, "3位LORA单火3本0情景");
        values.put(DatabaseStatic.Device.BUTTON_AMOUNT, 3);
        values.put(DatabaseStatic.Device.SCENE_AMOUNT, 0);
        values.put(DatabaseStatic.Device.TYPE, 0x0303);
        values.put(DatabaseStatic.Device.KIND, 1);
        values.put(DatabaseStatic.Device.PICTURE, "2");
        db.insert(DatabaseStatic.TABLE_DEVICE, null, values);
        values = new ContentValues();
        values.put(DatabaseStatic.Device.NAME, "智能开关双键");
        values.put(DatabaseStatic.Device.DESCRIPTION, "1位LORA零火1本1情景");
        values.put(DatabaseStatic.Device.TYPE, 0x0311);
        values.put(DatabaseStatic.Device.BUTTON_AMOUNT, 1);
        values.put(DatabaseStatic.Device.SCENE_AMOUNT, 1);
        values.put(DatabaseStatic.Device.KIND, 1);
        values.put(DatabaseStatic.Device.PICTURE, "0");
        db.insert(DatabaseStatic.TABLE_DEVICE, null, values);
        values = new ContentValues();
        values.put(DatabaseStatic.Device.NAME, "智能开关四键");
        values.put(DatabaseStatic.Device.DESCRIPTION, "2位LORA零火2本2情景");
        values.put(DatabaseStatic.Device.TYPE, 0x0312);
        values.put(DatabaseStatic.Device.BUTTON_AMOUNT, 2);
        values.put(DatabaseStatic.Device.SCENE_AMOUNT, 2);
        values.put(DatabaseStatic.Device.KIND, 1);
        values.put(DatabaseStatic.Device.PICTURE, "1");
        db.insert(DatabaseStatic.TABLE_DEVICE, null, values);
        values = new ContentValues();
        values.put(DatabaseStatic.Device.NAME, "智能开关六键");
        values.put(DatabaseStatic.Device.DESCRIPTION, "3位LORA零火3本3情景");
        values.put(DatabaseStatic.Device.BUTTON_AMOUNT, 3);
        values.put(DatabaseStatic.Device.SCENE_AMOUNT, 3);
        values.put(DatabaseStatic.Device.TYPE, 0x0313);
        values.put(DatabaseStatic.Device.KIND, 1);
        values.put(DatabaseStatic.Device.PICTURE, "4");
        db.insert(DatabaseStatic.TABLE_DEVICE, null, values);
        values = new ContentValues();
        values.put(DatabaseStatic.Device.NAME, "智能开关四键");
        values.put(DatabaseStatic.Device.DESCRIPTION, "4位LORA零火4本0情景");
        values.put(DatabaseStatic.Device.TYPE, 0x0314);
        values.put(DatabaseStatic.Device.BUTTON_AMOUNT, 4);
        values.put(DatabaseStatic.Device.SCENE_AMOUNT, 0);
        values.put(DatabaseStatic.Device.KIND, 1);
        values.put(DatabaseStatic.Device.PICTURE, "3");
        db.insert(DatabaseStatic.TABLE_DEVICE, null, values);
        values = new ContentValues();
        values.put(DatabaseStatic.Device.NAME, "窗帘开关");
        values.put(DatabaseStatic.Device.DESCRIPTION, "LORA零火3本0情景");
        values.put(DatabaseStatic.Device.BUTTON_AMOUNT, 3);
        values.put(DatabaseStatic.Device.SCENE_AMOUNT, 0);
        values.put(DatabaseStatic.Device.TYPE, 0x0320);
        values.put(DatabaseStatic.Device.KIND, 2);
        values.put(DatabaseStatic.Device.PICTURE, "5");
        db.insert(DatabaseStatic.TABLE_DEVICE, null, values);
        values = new ContentValues();
        values.put(DatabaseStatic.Device.NAME, "窗帘开关");
        values.put(DatabaseStatic.Device.DESCRIPTION, "LORA零火3本3情景");
        values.put(DatabaseStatic.Device.BUTTON_AMOUNT, 3);
        values.put(DatabaseStatic.Device.SCENE_AMOUNT, 3);
        values.put(DatabaseStatic.Device.TYPE, 0x0321);
        values.put(DatabaseStatic.Device.KIND, 2);
        values.put(DatabaseStatic.Device.PICTURE, "6");
        db.insert(DatabaseStatic.TABLE_DEVICE, null, values);
        values = new ContentValues();
        values.put(DatabaseStatic.Device.NAME, "情景按键");
        values.put(DatabaseStatic.Device.DESCRIPTION, "6位LORA零火0本6情景");
        values.put(DatabaseStatic.Device.BUTTON_AMOUNT, 0);
        values.put(DatabaseStatic.Device.SCENE_AMOUNT, 6);
        values.put(DatabaseStatic.Device.TYPE, 0x0330);
        values.put(DatabaseStatic.Device.KIND, 1);
        values.put(DatabaseStatic.Device.PICTURE, "4");
        db.insert(DatabaseStatic.TABLE_DEVICE, null, values);
        values = new ContentValues();
        values.put(DatabaseStatic.Device.NAME, "本地红外情景");
        values.put(DatabaseStatic.Device.DESCRIPTION, "LCD 4\"串口屏");
        values.put(DatabaseStatic.Device.TYPE, 0x0331);
        values.put(DatabaseStatic.Device.PICTURE, "7");
        db.insert(DatabaseStatic.TABLE_DEVICE, null, values);
        values = new ContentValues();
        values.put(DatabaseStatic.Device.NAME, "网络情景");
        values.put(DatabaseStatic.Device.DESCRIPTION, "LCD 7\"安卓");
        values.put(DatabaseStatic.Device.TYPE, 0x0332);
        values.put(DatabaseStatic.Device.PICTURE, "7");
        db.insert(DatabaseStatic.TABLE_DEVICE, null, values);
        values = new ContentValues();
        values.put(DatabaseStatic.Device.NAME, "语音红外情景");
        values.put(DatabaseStatic.Device.DESCRIPTION, "LORA零火0本N情景单麦");
        values.put(DatabaseStatic.Device.TYPE, 0x0336);
        values.put(DatabaseStatic.Device.KIND, 3);
        values.put(DatabaseStatic.Device.PICTURE, "8");
        db.insert(DatabaseStatic.TABLE_DEVICE, null, values);
        values = new ContentValues();
        values.put(DatabaseStatic.Device.NAME, "语音红外情景");
        values.put(DatabaseStatic.Device.DESCRIPTION, "LORA零火0本N情景双麦");
        values.put(DatabaseStatic.Device.TYPE, 0x0337);
        values.put(DatabaseStatic.Device.KIND, 3);
        values.put(DatabaseStatic.Device.PICTURE, "8");
        db.insert(DatabaseStatic.TABLE_DEVICE, null, values);
        values = new ContentValues();
        values.put(DatabaseStatic.Device.NAME, "红外情景");
        values.put(DatabaseStatic.Device.DESCRIPTION, "LORA零火0本N情景1键无麦");
        values.put(DatabaseStatic.Device.TYPE, 0x0338);
        values.put(DatabaseStatic.Device.KIND, 3);
        values.put(DatabaseStatic.Device.PICTURE, "8");
        db.insert(DatabaseStatic.TABLE_DEVICE, null, values);
        values = new ContentValues();
        values.put(DatabaseStatic.Device.NAME, "网关");
        values.put(DatabaseStatic.Device.DESCRIPTION, "老网关");
        values.put(DatabaseStatic.Device.TYPE, 0x0401);
        db.insert(DatabaseStatic.TABLE_DEVICE, null, values);
        values = new ContentValues();
        values.put(DatabaseStatic.Device.NAME, "网关");
        values.put(DatabaseStatic.Device.DESCRIPTION, "Linux网关");
        values.put(DatabaseStatic.Device.TYPE, 0x0402);
        db.insert(DatabaseStatic.TABLE_DEVICE, null, values);
        values = new ContentValues();
        values.put(DatabaseStatic.Device.NAME, "网关");
        values.put(DatabaseStatic.Device.DESCRIPTION, "双通道网关");
        values.put(DatabaseStatic.Device.TYPE, 0x0403);
        db.insert(DatabaseStatic.TABLE_DEVICE, null, values);
        values = new ContentValues();
        values.put(DatabaseStatic.Device.NAME, "空调");
        values.put(DatabaseStatic.Device.DESCRIPTION, "温控器");
        values.put(DatabaseStatic.Device.TYPE, 0x0503);
        values.put(DatabaseStatic.Device.KIND, 4);
        values.put(DatabaseStatic.Device.PICTURE, "9");
        db.insert(DatabaseStatic.TABLE_DEVICE, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
