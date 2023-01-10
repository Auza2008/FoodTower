package me.dev.foam.ui;

import me.dev.foam.ui.font.FontManager;
import me.dev.foam.utils.client.AnimationUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

import java.awt.*;

public final class SimpleButton extends GuiButton {
    private int color = 170;
    private double animation = 0.0D;

    public SimpleButton(int buttonId, int x, int y, String buttonText) {
        super(buttonId, x - (int)((double) FontManager.F22.getStringWidth(buttonText) / 2.0D), y, FontManager.F22.getStringWidth(buttonText), 10, buttonText);
    }

    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
        this.mouseDragged(mc, mouseX, mouseY);
        if (this.hovered) {
            if (this.color < 255) {
                this.color += 5;
            }

            if (this.animation < (double)this.width / 2.0D) {
                this.animation = AnimationUtils.animate((double)this.width / 2.0D, this.animation, 0.10000000149011612D);
            }
        } else {
            if (this.color > 170) {
                this.color -= 5;
            }

            if (this.animation > 0.0D) {
                this.animation = AnimationUtils.animate(0.0D, this.animation, 0.10000000149011612D);
            }
        }

        drawRect((double)this.xPosition + (double)this.width / 2.0D - this.animation, (double)(this.yPosition + this.height + 2), (double)this.xPosition + (double)this.width / 2.0D + this.animation, (double)(this.yPosition + this.height + 3), (new Color(this.color, this.color, this.color)).getRGB());
        FontManager.F22.drawCenteredString(this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, (new Color(this.color, this.color, this.color)).getRGB());
    }
}
