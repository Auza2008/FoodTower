/*
Author:SuMuGod
Date:2022/7/10 3:56
Project:foodtower Reborn
*/
package me.dev.foodtower.other;

import me.dev.foodtower.Client;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleManager;
import me.dev.foodtower.value.Mode;
import me.dev.foodtower.value.Numbers;
import me.dev.foodtower.value.Option;
import me.dev.foodtower.value.Value;

import java.io.File;
import java.util.List;

public class Config {
    String name;

    public Config(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void saveSetting() {
        String values = "";
        for (Module m : ModuleManager.getModules()) {
            for (Value v : m.getValues()) {
                values = values + String.format("%s:%s:%s%s", m.getName(), v.getName(), v.getValue(), System.lineSeparator());
            }
        }
        FileManager.save(name + "-" + "Values.txt", values, false);
        String enableds = "";
        for (Module m : ModuleManager.getModules()) {
            if (!m.isEnabled()) continue;
            enableds = enableds + String.format("%s%s", m.getName(), System.lineSeparator());
        }
        FileManager.save(name + "-" + "Enabled.txt", enableds, false);
        Client.instance.getConfigManager().init();
    }

    public void readConfig() {
        List<String> enabled = FileManager.read(name + "-" + "Enabled.txt");
        for (String v : enabled) {
            Module m = ModuleManager.getModuleByName(v);
            if (m == null) continue;
            m.enabledOnStartup = true;
        }
        List<String> vals = FileManager.read(name + "-" + "Values.txt");
        for (String v : vals) {
            String name = v.split(":")[0];
            String values = v.split(":")[1];
            Module m = ModuleManager.getModuleByName(name);
            if (m == null) continue;
            for (Value value : m.getValues()) {
                if (!value.getName().equalsIgnoreCase(values)) continue;
                if (value instanceof Option) {
                    value.setValue(Boolean.parseBoolean(v.split(":")[2]));
                    continue;
                }
                if (value instanceof Numbers) {
                    value.setValue(Double.parseDouble(v.split(":")[2]));
                    continue;
                }
                ((Mode) value).setMode(v.split(":")[2]);
            }
        }
        Client.instance.getConfigManager().init();
    }

    public void delete() {
        new File(FileManager.dir, name + "-" + "Enabled.txt").delete();
        new File(FileManager.dir, name + "-" + "Values.txt").delete();
        Client.instance.getConfigManager().init();
    }
}
