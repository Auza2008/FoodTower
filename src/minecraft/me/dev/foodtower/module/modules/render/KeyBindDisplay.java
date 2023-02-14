/*
Author:SuMuGod
Date:2022/7/10 5:06
Project:foodtower Reborn
*/
package me.dev.foodtower.module.modules.render;

import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.api.events.EventRender2D;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleManager;
import me.dev.foodtower.module.ModuleType;
import me.dev.foodtower.ui.font.FontManager;
import me.dev.foodtower.ui.hudeditor.HUDEditor;
import me.dev.foodtower.utils.normal.RenderUtil;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.ArrayList;

public class KeyBindDisplay extends Module {
    public KeyBindDisplay() {
        super("KeybindDisplay", "按键绑定显示", new String[]{"keyHud"}, ModuleType.Render);
    }

    @NMSL
    public void render(EventRender2D e) {
        if (!HUDEditor.key.extended)
            return;
        float startX = HUDEditor.key.x;
        float startY = HUDEditor.key.y + 15;
        int modules = 0;
        ArrayList<Module> bindM = new ArrayList<>();
        for (Module m : ModuleManager.getModules()) {
            if (m.getKey() != 0) {
                bindM.add(m);
                modules++;
            }
        }
        RenderUtil.drawBordered(startX, startY, 100, (FontManager.F18.getHeight() + 2) * modules + FontManager.F18.getHeight() + 8, 1, 0xAA << 24, new Color(0x80FFFFFF, true).getRGB());
        FontManager.F18.drawCenteredString("Keybinds", startX + 50, startY + 2, -1);
        int y = (int) (startY + FontManager.F18.getHeight() + 4 + 2);
        for (Module m : bindM) {
            FontManager.F18.drawString(m.getName(), startX + 2, y, -1);
            FontManager.F18.drawString(Keyboard.getKeyName(m.getKey()), startX + 100 - FontManager.F18.getStringWidth(Keyboard.getKeyName(m.getKey())) - 2, y, -1);
            y += FontManager.F18.getHeight() + 2;
        }
    }
}

