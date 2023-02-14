/*
Author:SuMuGod
Date:2022/7/10 5:27
Project:foodtower Reborn
*/
package me.dev.foodtower.ui.hudeditor;

import me.dev.foodtower.module.modules.render.Radar;
import me.dev.foodtower.other.FileManager;
import me.dev.foodtower.ui.Notification;
import me.dev.foodtower.ui.font.ChineseFontRenderer;
import me.dev.foodtower.ui.font.FontManager;
import me.dev.foodtower.utils.normal.MsgUtil;
import com.google.common.collect.Lists;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HUDEditor extends GuiScreen {
    public static ArrayList<Window> windows = Lists.newArrayList();
    public static boolean binding = false;
    public double opacity = 0.0;
    public int scrollVelocity;

    public static Window radar = new Window("Radar", 5, 50, Radar.SIZE + 2);
    public static Window inv = new Window("Inventory", 5, 100, (20 * 9) + 2 + 2);
    public static Window key = new Window("KeyBind", 5, 200, 100 + 2 + 2);
    public HUDEditor() {
        if (windows.isEmpty()) {
            windows.add(radar);
            windows.add(inv);
            windows.add(key);
        }
    }


    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.opacity = this.opacity + 10.0 < 200.0 ? (this.opacity += 10.0) : 200.0;
        Gui.drawRect(0.0, 0.0, Display.getWidth(), Display.getHeight(), ClientUtil.reAlpha(1, 0.3F));
        ScaledResolution res = new ScaledResolution(this.mc);
        GlStateManager.pushMatrix();
        ScaledResolution scaledRes = new ScaledResolution(this.mc);
        float scale = (float) scaledRes.getScaleFactor() / (float) Math.pow(scaledRes.getScaleFactor(), 2.0);
        windows.forEach(w -> w.render(mouseX, mouseY));
        GlStateManager.popMatrix();
        if (Mouse.hasWheel()) {
            int wheel = Mouse.getDWheel();
            this.scrollVelocity = wheel < 0 ? -120 : (wheel > 0 ? 120 : 0);
        }
        windows.forEach(w -> w.mouseScroll(mouseX, mouseY, this.scrollVelocity));
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        windows.forEach(w -> w.click(mouseX, mouseY, mouseButton));
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0: {
                JFileChooser jfchooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Font(*.ttf)", "ttf");
                jfchooser.setFileFilter(filter);
                jfchooser.setDialogTitle("Choose a ttf font file.");
                jfchooser.showDialog(null, "Choose");
                File f = jfchooser.getSelectedFile();
                if (f != null) {
                    FontManager.F13 = new ChineseFontRenderer(FontManager.fontFromFile(f, 13, Font.PLAIN), true, true, true, 0, 0);
                    FontManager.F16 = new ChineseFontRenderer(FontManager.fontFromFile(f, 16, Font.PLAIN), true, true, true, 0, 0);
                    FontManager.F18 = new ChineseFontRenderer(FontManager.fontFromFile(f, 18, Font.PLAIN), true, true, true, 0, 0);
                    FontManager.F22 = new ChineseFontRenderer(FontManager.fontFromFile(f, 22, Font.PLAIN), true, true, true, 0, 0);
                    MsgUtil.sendNotification("Custom font load succeed!", Notification.Type.SUCCESS);
                }
                break;
            }
            case 1: {
                FontManager.F18 = new ChineseFontRenderer(FontManager.fontFromTTF("font.ttf", 18, Font.PLAIN), true, true, true, 0, 0);
                FontManager.F16 = new ChineseFontRenderer(FontManager.fontFromTTF("font.ttf", 16, Font.PLAIN), true, true, true, 0, 0);
                FontManager.F22 = new ChineseFontRenderer(FontManager.fontFromTTF("font.ttf", 22, Font.PLAIN), true, true, true, 0, 0);
                FontManager.F13 = new ChineseFontRenderer(FontManager.fontFromTTF("font.ttf", 13, Font.PLAIN), true, true, true, 0, 0);
                MsgUtil.sendNotification("Font reset succeed!", Notification.Type.SUCCESS);
            }
        }
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents((boolean) true);
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, 4, 4, "Change Font"));
        this.buttonList.add(new GuiButton(1, 4, 4 + 25, "Reset Font"));
    }

    public void init() {
        List<String> winpos = FileManager.read("ClickGui.txt");
        for (String v : winpos) {
            String name = v.split(":")[0];
            Window w = null;
            for (Window win : windows) {
                if (win.title.equals(name))
                    w = win;
            }
            if (w == null) continue;
            w.x = Integer.parseInt(v.split(":")[1]);
            w.y = Integer.parseInt(v.split(":")[2]);
            w.extended = Boolean.parseBoolean(v.split(":")[3]);
        }
    }

    @Override
    public void onGuiClosed() {
        StringBuilder windowss = new StringBuilder();
        for (Window w : windows) {
            windowss.append(String.format("%s:%s:%s:%s%s", w.title, w.x, w.y, w.extended, System.lineSeparator()));
        }
        FileManager.save("ClickGui.txt", windowss.toString(), false);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1 && !binding) {
            this.mc.displayGuiScreen(null);
            return;
        }
        windows.forEach(w -> w.key(typedChar, keyCode));
    }


    public synchronized void sendToFront(Window window) {
        int panelIndex = 0;
        int i = 0;
        while (i < windows.size()) {
            if (windows.get(i) == window) {
                panelIndex = i;
                break;
            }
            ++i;
        }
        Window t = windows.get(windows.size() - 1);
        windows.set(windows.size() - 1, windows.get(panelIndex));
        windows.set(panelIndex, t);
    }
}
