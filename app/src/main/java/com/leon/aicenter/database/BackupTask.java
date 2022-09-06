package com.leon.aicenter.database;

import android.content.Context;

import com.leon.aicenter.util.ConstantUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class BackupTask {
    public static final String COMMAND_BACKUP = "backupDatabase";
    public static final String COMMAND_RESTORE = "restoreDatabase";
    private final Context mContext;

    public BackupTask(Context context) {
        this.mContext = context;
    }

    public void doInBackground(String command) {
        File dbFile = mContext.getDatabasePath(DatabaseStatic.DATABASE_NAME);
        File backup = new File(ConstantUtils.FILE_BACKUP_DB);
        if (command.equals(COMMAND_BACKUP)) {
            try {
                if (!backup.createNewFile())
                    return;
                fileCopy(dbFile, backup);//数据库文件拷贝至备份文件
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (command.equals(COMMAND_RESTORE)) {
            try {
                fileCopy(backup, dbFile);//备份文件拷贝至数据库文件
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void fileCopy(File dbFile, File backup) throws IOException {
        try (FileChannel inChannel = new FileInputStream(dbFile).getChannel(); FileChannel outChannel = new FileOutputStream(backup).getChannel()) {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        }
    }
}
