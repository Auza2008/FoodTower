/*
Author:SuMuGod
Date:2022/7/10 5:28
Project:foodtower Reborn
*/
package me.dev.foodtower.ui.hudeditor;

import me.dev.foodtower.module.Module;
import me.dev.foodtower.ui.font.FontManager;
import me.dev.foodtower.value.Mode;
import me.dev.foodtower.value.Numbers;
import me.dev.foodtower.value.Option;
import me.dev.foodtower.value.Value;
import com.google.common.collect.Lists;
import net.minecraft.client.gui.Gui;

import java.awt.*;
import java.util.ArrayList;

public class Button {
    public Module cheat;
    public me.dev.foodtower.ui.hudeditor.Window parent;
    public int x;
    public int y;
    public int index;
    public int remander;
    public double opacity = 0.0;
    public ArrayList<ValueButton> buttons = Lists.newArrayList();
    public boolean expand;

    public Button(Module cheat, int x, int y) {
        this.cheat = cheat;
        this.x = x;
        this.y = y;
        int y2 = y + 14;
        for (Value v : cheat.getValues()) {
            if (v instanceof Mode)
                this.buttons.add(new ValueButton(v, x + 5, y2));
            y2 += 15;
        }
        for (Value v : cheat.getValues()) {
            if (v instanceof Numbers)
                this.buttons.add(new ValueButton(v, x + 5, y2));
            y2 += 15;
        }
        for (Value v : cheat.getValues()) {
            if (v instanceof Option)
                this.buttons.add(new ValueButton(v, x + 5, y2));
            y2 += 15;
        }
        this.buttons.add(new KeyBindButton(cheat, x + 5, y2));
    }

    public void render(int mouseX, int mouseY) {
        if (this.index != 0) {
            Button b2 = this.parent.buttons.get(this.index - 1);
            this.y = b2.y + 15 + (b2.expand ? 15 * b2.buttons.size() : 0);
        }
        int i = 0;
        while (i < this.buttons.size()) {
            this.buttons.get(i).y = this.y + 14 + 15 * i;
            this.buttons.get(i).x = this.x + 5;
            ++i;
        }
        Gui.drawRect(this.x - 5, this.y - 5, this.x + 85, this.y + FontManager.F18.getStringHeight(this.cheat.getName()) + 2, ClientUtil.reAlpha(1, 0.5f));
        if (mouseX > this.x - 7 && mouseX < this.x + 85 && mouseY > this.y - 6
                && mouseY < this.y + FontManager.F18.getStringHeight(this.cheat.getName()) + 4) {
            Gui.drawRect(this.x - 5, this.y - 5, this.x + 85, this.y + FontManager.F18.getStringHeight(this.cheat.getName()) + 2, ClientUtil.reAlpha(1, 0.5f));

        }
        if (this.cheat.isEnabled()) {
            FontManager.F18.drawString(this.cheat.getName(), this.x + 5, this.y, new Color(0x00A6FF).getRGB());
        } else {
            FontManager.F18.drawString(this.cheat.getName(), this.x + 5, this.y, new Color(255, 255, 255).getRGB());
        }

        if (this.expand) {
            this.buttons.forEach(b -> b.render(mouseX, mouseY));
        }
    }

    public void key(char typedChar, int keyCode) {
        this.buttons.forEach(b -> b.key(typedChar, keyCode));
    }

    public void click(int mouseX, int mouseY, int button) {
        if (mouseX > this.x - 7 && mouseX < this.x + 85 && mouseY > this.y - 6
                && mouseY < this.y + FontManager.F18.getStringHeight(this.cheat.getName()) + 4) {
            if (button == 0) {
                this.cheat.setEnabled(!this.cheat.isEnabled());
            }
            if (button == 1 && !this.buttons.isEmpty()) {
                boolean bl = this.expand = !this.expand;
            }
        }
        if (this.expand) {
            this.buttons.forEach(b -> b.click(mouseX, mouseY, button));
        }
    }

    public void setParent(Window parent) {
        this.parent = parent;
        int i = 0;
        while (i < this.parent.buttons.size()) {
            if (this.parent.buttons.get(i) == this) {
                this.index = i;
                this.remander = this.parent.buttons.size() - i;
                break;
            }
            ++i;
        }
    }
}

