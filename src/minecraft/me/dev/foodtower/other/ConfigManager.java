/*
Author:SuMuGod
Date:2022/7/10 3:57
Project:foodtower Reborn
*/
package me.dev.foodtower.other;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ConfigManager {
    public ArrayList<Config> configs = new ArrayList<>();
    public List<String> strs = new ArrayList<>();

    private static void removeDuplicate(List<String> list) {
        HashSet<String> set = new HashSet<String>(list.size());
        List<String> result = new ArrayList<String>(list.size());
        for (String str : list) {
            if (set.add(str)) {
                result.add(str);
            }
        }
        list.clear();
        list.addAll(result);
    }

    public void init() {
        configs.clear();

        /*
        处理旧配置，防止读取旧版本的配置引起问题
        */
        if (!(new File(FileManager.dir, "Official-Enabled.txt").exists())) {
            new File(FileManager.dir, "Enabled.txt").renameTo(new File("Official-Enabled.txt"));
            new File(FileManager.dir, "Values.txt").renameTo(new File("Official-Values.txt"));
        } else {
            new File("Official-Enabled.txt").delete();
            new File("Official-Values.txt").delete();
            new File(FileManager.dir, "Enabled.txt").renameTo(new File("Official-Enabled.txt"));
            new File(FileManager.dir, "Values.txt").renameTo(new File("Official-Values.txt"));
        }

        if (FileManager.dir.exists()) {
            for (File f : FileManager.dir.listFiles()) {
                if (f.getName().contains("Values") || f.getName().contains("Enabled")) {
                    String[] spilt = f.getName().split("-");
                    strs.add(spilt[0]);
                }
            }
        }

        removeDuplicate(strs);

        for (String s : strs) {
            configs.add(new Config(s));
        }
    }
}

