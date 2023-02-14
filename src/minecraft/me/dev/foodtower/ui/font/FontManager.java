/*
Author:SuMuGod
Date:2022/7/10 5:31
Project:foodtower Reborn
*/
package me.dev.foodtower.ui.font;

import me.dev.foodtower.utils.normal.Helper;

import java.awt.*;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Objects;

public class FontManager {

    public static final FontManager INSTANCE = new FontManager();
    public static ChineseFontRenderer F13;
    public static ChineseFontRenderer F18;
    public static ChineseFontRenderer F16;
    public static ChineseFontRenderer F22;
    public static final EnglishFontRenderer NovICON56;
    public static final EnglishFontRenderer FluxIcon15;
    public static final EnglishFontRenderer FluxFont16;
    public static final EnglishFontRenderer FluxFont15;
    public static final EnglishFontRenderer FluxFont30;
    public static final EnglishFontRenderer FluxIcon18;
    public static final EnglishFontRenderer FluxIcon10;

    static {
        F18 = new ChineseFontRenderer(fontFromTTF("font.ttf", 18, Font.PLAIN), true, true, true, 0, 0);
        F16 = new ChineseFontRenderer(fontFromTTF("font.ttf", 16, Font.PLAIN), true, true, true, 0, 0);
        F22 = new ChineseFontRenderer(fontFromTTF("font.ttf", 22, Font.PLAIN), true, true, true, 0, 0);
        F13 = new ChineseFontRenderer(fontFromTTF("font.ttf", 13, Font.PLAIN), true, true, true, 0, 0);
        NovICON56 = new EnglishFontRenderer(fontFromTTF("NovICON.ttf", 56, Font.PLAIN), true, true);
        FluxFont16 = new EnglishFontRenderer(fontFromTTF("fluxfont.ttf", 16, Font.PLAIN), true, true);
        FluxFont30 = new EnglishFontRenderer(fontFromTTF("fluxfont.ttf", 30, Font.PLAIN), true, true);
        FluxFont15 = new EnglishFontRenderer(fontFromTTF("fluxfont.ttf", 15, Font.PLAIN), true, true);
        FluxIcon15 = new EnglishFontRenderer(fontFromTTF("Icon.ttf", 15, Font.PLAIN), true, true);
        FluxIcon18 = new EnglishFontRenderer(fontFromTTF("Icon.ttf", 18, Font.PLAIN), true, true);
        FluxIcon10 = new EnglishFontRenderer(fontFromTTF("Icon.ttf", 10, Font.PLAIN), true, true);
    }

    private FontManager() {
    }

    public static Font fontFromFile(File f, float fontSize, int fontType) {
        Font output = null;
        try {
            InputStream inputStream = Files.newInputStream(f.toPath());
            output = Font.createFont(fontType, inputStream);
            output = output.deriveFont(fontSize);
        } catch (Exception e) {
            Helper.sendMessage("Failed load custom font!");
            e.printStackTrace();
        }
        return output;
    }

    public static Font fontFromTTF(String fontLocation, float fontSize, int fontType) {
        Font output = null;
        try {
            output = Font.createFont(fontType,
                    Objects.requireNonNull(FontManager.class.getResourceAsStream("/assets/minecraft/foodtower/Font/" + fontLocation)));
            output = output.deriveFont(fontSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }
}

