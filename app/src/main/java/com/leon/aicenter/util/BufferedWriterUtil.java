package com.leon.aicenter.util;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class BufferedWriterUtil {

    private static final String TAG = "BufferedWriterUtil";

    //写入节点
    public static void write(String path, String str) {
        if (path == null || str == null) return;
        try {
            BufferedWriter bufWriter;
            bufWriter = new BufferedWriter(new FileWriter(path));
            bufWriter.write(str);
            bufWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //读取指定长度节点值
    public static String readLength(String path, int length) {
        String result = "";
        if (path == null) return result;
        if (length == 0) length = 10;//默认
        try {
            MyBufferedReader reader = new MyBufferedReader(new FileReader(path));
            result = reader.readLength(length);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    public static class MyBufferedReader {
        private final FileReader f;
        // 定义一个数组作为缓冲区
        private final char[] buf = new char[1024];
        // 定义一个指针用来操作数组中的元素，当操作到最后一个元素后指针归零
        private int pos = 0;
        // 定义一个计数器用来计算缓冲区中的数据个数，当为0时，继续取数据到缓冲区中
        private int count = 0;

        public MyBufferedReader(FileReader f) {
            this.f = f;
        }

        /**
         * 缓冲区中读
         *
         */
        public int read() throws IOException {
            // 源中取数据到缓冲区中,计数器为0才取
            if (count == 0) {
                count = f.read(buf);
                pos = 0;
            }

            if (count == -1) {
                return -1;
            }
            char ch = buf[pos];
            pos++;
            count--;

            return ch;
        }

        //读取一行
        public String readLine() throws IOException {
            //当一个缓冲区
            StringBuilder sb = new StringBuilder();

            int ch;
            while ((ch = read()) != -1) {
                if (ch == '\r') {
                    continue;
                }
                if (ch == '\n') {
                    return sb.toString();
                }
                sb.append((char) ch);
            }
            //最后一行后面没符号，也成功
            if (sb.length() != 0)
                return sb.toString();
            return null;
        }

        //读取指定长度
        public String readLength(int length) throws IOException {
            //当一个缓冲区
            StringBuilder sb = new StringBuilder();
            int ch;
            for (int i = 0; i < length; i++) {
                ch = read();
                if (ch != -1) {
                    sb.append((char) ch);
                }
            }
            return sb.toString();
        }

        public void close() throws IOException {
            f.close();
        }

    }


}
