/*
Author:SuMuGod
Date:2022/7/10 6:02
Project:foodtower Reborn
*/
package me.dev.foodtower.utils.client;

import me.dev.foodtower.ui.font.FontManager;
import me.dev.foodtower.utils.normal.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

import java.awt.*;

public class MainMenuButton extends GuiButton {

    public String text;
    public MainMenuButton(final int buttonId, final int x, final int y, final int width, final int height, final String buttonText, final String buttonText2) {
        super(buttonId, x, y, width, height, buttonText);
        this.text = buttonText2;
    }

    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (visible) {

            hovered = (mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height);


            mouseDragged(mc, mouseX, mouseY);
            int stringColor = new Color(251,251,254).getRGB();

            if (hovered){
                FontManager.F22.drawString(text, xPosition - 10, yPosition - 15, -1);
                RenderUtil.drawRect(xPosition - 30, yPosition + 57, xPosition + 65f, yPosition + 57 - 4, new Color(48, 180, 255).getRGB());
            }
            FontManager.NovICON56.drawCenteredString(displayString, xPosition + width / 2, yPosition + (height - 6) / 2, stringColor);
        }
    }

    protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {}
}

