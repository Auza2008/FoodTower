/*
Author:SuMuGod
Date:2022/7/10 5:29
Project:foodtower Reborn
*/
package me.dev.foodtower.ui.hudeditor;

import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleManager;
import me.dev.foodtower.module.ModuleType;
import me.dev.foodtower.ui.font.FontManager;
import com.google.common.collect.Lists;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.util.ArrayList;

public class Window {
    public ModuleType category;
    public ArrayList<Button> buttons = Lists.newArrayList();
    public boolean drag;
    public boolean extended = true;
    public boolean isCustom = false;
    public int x;
    public int y;
    public int expand;
    public int dragX;
    public int dragY;
    public int max;
    public int scroll;
    public int scrollTo;
    public double angel;

    String title;
    int wid;

    public Window(ModuleType category, int x, int y) {
        this.category = category;
        this.x = x;
        this.y = y;
        this.max = 120;
        int y2 = y + 20;
        for (Module c : ModuleManager.getModules()) {
            if (c.getType() != category)
                continue;

            this.buttons.add(new Button(c, x + 5, y2));

            this.buttons.sort((o1, o2) -> {
                if (!(o1 != null && o2 != null)) {
                    throw new IllegalArgumentException("sb");
                }
                return o1.cheat.getName().compareTo(o2.cheat.getName());
            });
            y2 += 15;
        }
        for (Button b2 : this.buttons) {
            b2.setParent(this);
        }
        this.buttons.get(0).y = this.y + 20 - this.scroll;
        for (Button b4 : this.buttons) {
            b4.x = this.x + 5;
        }
        title = category.name();
        wid = 92;
    }

    public Window(String title, int x, int y,int width) {
        this.x = x;
        this.y = y;
        this.max = 120;
        int y2 = y + 20;
        this.title = title;
        this.wid = width;
    }

    public void render(int mouseX, int mouseY) {
        int current = 0;
        for (Button b3 : this.buttons) {
            if (b3.expand) {
                for (ValueButton v : b3.buttons) {
                    current += 15;
                }
            }
            current += 15;
        }
        int height = 15 + current;
        if (this.extended) {
            this.expand = this.expand + 1 < height ? (this.expand += 5) : height;
            this.angel = this.angel + 20.0 < 180.0 ? (this.angel += 20.0) : 180.0;
        } else {
            this.expand = this.expand - 5 > 0 ? (this.expand -= 5) : 0;
            this.angel = this.angel - 20.0 > 0.0 ? (this.angel -= 20.0) : 0.0;
        }
        Gui.drawRect(this.x - 2, this.y, this.x + wid, this.y + 15, ClientUtil.reAlpha(new Color(0x00A6FF).getRGB(), 1.0f));
        FontManager.F18.drawString(title, this.x + 4, this.y + 5, new Color(255, 255, 255).getRGB());
        FontManager.F18.drawString(extended ? "-" : "+", this.x + wid - 10, this.y + 5, -1);
        GlStateManager.pushMatrix();
        GlStateManager.translate(this.x + 90 - 10, this.y + 5, 0.0f);
        GlStateManager.rotate((float) this.angel, 0.0f, 0.0f, 1.0f);
        GlStateManager.translate(-this.x + 90 - 10, -this.y + 5, 0.0f);
        GlStateManager.popMatrix();
        if (this.expand > 0) {
            this.buttons.forEach(b2 -> b2.render(mouseX, mouseY));
        }
        if (this.drag) {
            if (!Mouse.isButtonDown(0)) {
                this.drag = false;
            }
            this.x = mouseX - this.dragX;
            this.y = mouseY - this.dragY;
            if(!buttons.isEmpty()){
                this.buttons.get(0).y = this.y + 20 - this.scroll;
            }
            for (Button b4 : this.buttons) {
                b4.x = this.x + 5;
            }
        }
    }

    public void key(char typedChar, int keyCode) {
        this.buttons.forEach(b2 -> b2.key(typedChar, keyCode));
    }

    public void mouseScroll(int mouseX, int mouseY, int amount) {
        if (mouseX > this.x - 2 && mouseX < this.x + 92 && mouseY > this.y - 2 && mouseY < this.y + 17 + this.expand) {
            this.scrollTo = (int) ((float) this.scrollTo - (float) (amount / 120 * 28));
        }
    }

    public void click(int mouseX, int mouseY, int button) {
        if (mouseX > this.x - 2 && mouseX < this.x + wid && mouseY > this.y - 2 && mouseY < this.y + 17) {
            if (button == 1) {
                boolean bl = this.extended = !this.extended;
            }
            if (button == 0) {
                this.drag = true;
                this.dragX = mouseX - this.x;
                this.dragY = mouseY - this.y;
            }
        }
        if (this.extended) {
            this.buttons.stream().filter(b2 -> b2.y < this.y + this.expand)
                    .forEach(b2 -> b2.click(mouseX, mouseY, button));
        }
    }
}

