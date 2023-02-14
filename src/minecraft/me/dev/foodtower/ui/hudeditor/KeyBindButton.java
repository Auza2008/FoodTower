/*
Author:SuMuGod
Date:2022/7/10 5:28
Project:foodtower Reborn
*/
package me.dev.foodtower.ui.hudeditor;

import me.dev.foodtower.module.Module;
import me.dev.foodtower.ui.font.FontManager;
import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class KeyBindButton extends ValueButton {
    public Module cheat;
    public double opacity = 0.0;
    public boolean bind;

    public KeyBindButton(Module cheat, int x, int y) {
        super(null, x, y);
        this.custom = true;
        this.bind = false;
        this.cheat = cheat;
    }

    @Override
    public void render(int mouseX, int mouseY) {
        Gui.drawRect(0.0, 0.0, 0.0, 0.0, 0);
        Gui.drawRect(this.x - 10, this.y - 4, this.x + 80, this.y + 11, ClientUtil.reAlpha(1, 0.3f));
        FontManager.F18.drawString("Bind", this.x - 5, this.y + 2, new Color(184, 184, 184).getRGB());
        FontManager.F18.drawString("" + Keyboard.getKeyName(this.cheat.getKey()),
                this.x + 76 - FontManager.F18.getStringWidth(Keyboard.getKeyName(this.cheat.getKey())), this.y + 2,
                new Color(184, 184, 184).getRGB());
    }

    @Override
    public void key(char typedChar, int keyCode) {
        if (this.bind) {
            this.cheat.setKey(keyCode);
            if (keyCode == 1) {
                this.cheat.setKey(0);
            }
            HUDEditor.binding = false;
            this.bind = false;
        }
        super.key(typedChar, keyCode);
    }

    @Override
    public void click(int mouseX, int mouseY, int button) {
        if (mouseX > this.x - 7 && mouseX < this.x + 85 && mouseY > this.y - 6
                && mouseY < this.y + FontManager.F18.getStringHeight(this.cheat.getName()) + 5
                && button == 0) {
            HUDEditor.binding = this.bind = !this.bind;
        }
        super.click(mouseX, mouseY, button);
    }
}

