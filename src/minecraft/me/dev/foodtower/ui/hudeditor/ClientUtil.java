/*
Author:SuMuGod
Date:2022/7/10 5:28
Project:foodtower Reborn
*/
package me.dev.foodtower.ui.hudeditor;

import java.awt.*;

public class ClientUtil {
    public static int reAlpha(int color, float alpha) {
        Color c = new Color(color);
        float r = ((float) 1 / 255) * c.getRed();
        float g = ((float) 1 / 255) * c.getGreen();
        float b = ((float) 1 / 255) * c.getBlue();
        return new Color(r, g, b, alpha).getRGB();
    }
}
