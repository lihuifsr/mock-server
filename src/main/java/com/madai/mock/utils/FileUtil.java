package com.madai.mock.utils;


import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class FileUtil {

    public static String readFile(String path) {
        BufferedReader reader = null;
        String laststr = "";
        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
            reader = new BufferedReader(inputStreamReader);
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                laststr += tempString;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return laststr;
    }

    public static String[] readFiles(String path) {
        File[] files = new File(path).listFiles();
        String[] datas = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            datas[i] = readFile(files[i].getPath());
        }
        return datas;
    }

    public static Map<String, String> readFilesToMap(String path) {
        File[] files = new File(path).listFiles();
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < files.length; i++) {
            map.put(files[i].getName(), readFile(files[i].getPath()));
        }
        return map;
    }

    public static void main(String[] args) {
        readFiles(Config.getConfigPath());
    }
}
