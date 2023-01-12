/*
Author:SuMuGod
Date:2022/7/10 3:11
Project:foam Reborn
*/
package me.dev.foam;

import me.dev.foam.command.CommandManager;
import me.dev.foam.module.Module;
import me.dev.foam.module.ModuleManager;
import me.dev.foam.other.Config;
import me.dev.foam.other.ConfigManager;
import me.dev.foam.other.FileManager;
import me.dev.foam.other.FriendManager;
import me.dev.foam.ui.hudeditor.HUDEditor;
import me.dev.foam.ui.login.AltManager;
import me.dev.foam.value.Value;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Random;

public class Client {
    public static boolean publicMode = false;
    public static Client instance = new Client();
    public static boolean inited = false;
    public static String user = null;
    public static String authKey = null;

    public Config configInUse = new Config("Official");
    public static ResourceLocation CLIENT_CAPE = new ResourceLocation("foam/cape.png");
    public final String name = "Foam";
    public final double version = 0.22;
    private ModuleManager modulemanager;
    private CommandManager commandmanager;
    private AltManager altmanager;
    private FriendManager friendmanager;
    private ConfigManager configManager;

    public static boolean cracked = false;
    public static boolean debug = true;


    public static int rainbowDraw(long speed, long delay) {
        long time = System.currentTimeMillis() + delay;
        return Color.getHSBColor((float) (time % (15000L / speed)) / (15000.0F / (float) speed), 1.0F, 1.0F).getRGB();
    }

    public void initiate() {
/*        String pass = JOptionPane.showInputDialog(null, "[AntiLeakLite]你正在使用测试版本，请输入访问码", "AntiLeakLite", JOptionPane.WARNING_MESSAGE);
        if (!pass.equals("1336779")) {
            for (int i = 0; i <= 100000000; i++) {
                JOptionPane.showMessageDialog(null, "Leak神cnm！", "LLL leakgod LLL", JOptionPane.ERROR_MESSAGE);
            }
            System.exit(0);
        }
 */

        if (debug)
            user = "Foam User";

        if (user == null) {
            cracked = true;
        }
        if (!cracked) {
            this.commandmanager = new CommandManager();
            this.commandmanager.init();
            this.friendmanager = new FriendManager();
            this.friendmanager.init();
            this.modulemanager = new ModuleManager();
            this.modulemanager.init();
            this.altmanager = new AltManager();
//        System.out.println("Config Manager init");

            AltManager.init();
            AltManager.setupAlts();
            FileManager.init();

            this.configManager = new ConfigManager();
            this.configManager.init();
            System.out.println("Init");
            new HUDEditor().init();
            configInUse.readConfig();
            Display.setTitle(name + " " + version);
//            if (Minecraft.getMinecraft().gameSettings.ofFastRender) {
//            JOptionPane.showMessageDialog(null, "开你妈FastRender", "L", JOptionPane.ERROR_MESSAGE);
//                Minecraft.getMinecraft().gameSettings.ofFastRender = false;
//            }
            readBind();
            inited = true;
        } else {
            int n = 0;
            Random rd = new Random();
            while (n < 114514) {
                JFrame frame = new JFrame("Hacked by Dimples#1337");
                frame.setSize(500, 20);
                frame.setLocation(rd.nextInt(1920), rd.nextInt(1080));
                frame.setVisible(true);
                n++;

            }
        }
    }

    public void readBind() {
        List<String> binds = FileManager.read("Binds.txt");
        for (String v : binds) {
            String name = v.split(":")[0];
            String bind = v.split(":")[1];
            Module m = ModuleManager.getModuleByName(name);
            if (m == null) continue;
            m.setKey(Keyboard.getKeyIndex((String) bind.toUpperCase()));
        }
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }


    public ModuleManager getModuleManager() {
        return this.modulemanager;
    }

    public CommandManager getCommandManager() {
        return this.commandmanager;
    }

    public AltManager getAltManager() {
        return this.altmanager;
    }

    public void shutDown() {
        String values = "";
        instance.getModuleManager();
        for (Module m : ModuleManager.getModules()) {
            for (Value v : m.getValues()) {
                values = values + String.format("%s:%s:%s%s", m.getName(), v.getName(), v.getValue(), System.lineSeparator());
            }
        }
        FileManager.save("Values.txt", values, false);
        String enabled = "";
        instance.getModuleManager();
        for (Module m : ModuleManager.getModules()) {
            if (!m.isEnabled()) continue;
            enabled = enabled + String.format("%s%s", m.getName(), System.lineSeparator());
        }
        FileManager.save("Enabled.txt", enabled, false);
    }
}
