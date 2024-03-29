package cn.foodtower.ui.font;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.InputStream;

public abstract class FontLoaders {
    public static CFontRenderer ICON10 = new CFontRenderer(FontLoaders.getfonts(10, "ICONF", true), true, true);
    public static CFontRenderer ICON20 = new CFontRenderer(FontLoaders.getfonts(20, "ICONF", true), true, true);

    public static CFontRenderer Comfortaa10 = new CFontRenderer(FontLoaders.getComfortaa(10), true, true);
    public static CFontRenderer Comfortaa12 = new CFontRenderer(FontLoaders.getComfortaa(10), true, true);
    public static CFontRenderer Comfortaa13 = new CFontRenderer(FontLoaders.getComfortaa(13), true, true);
    public static CFontRenderer Comfortaa14 = new CFontRenderer(FontLoaders.getComfortaa(14), true, true);
    public static CFontRenderer Comfortaa18 = new CFontRenderer(FontLoaders.getComfortaa(18), true, true);
    public static CFontRenderer GoogleSans14 = new CFontRenderer(FontLoaders.getGoogleSans(14), true, true);
    public static CFontRenderer GoogleSans15 = new CFontRenderer(FontLoaders.getGoogleSans(15), true, true);
    public static CFontRenderer GoogleSans16 = new CFontRenderer(FontLoaders.getGoogleSans(16), true, true);
    public static CFontRenderer GoogleSans18 = new CFontRenderer(FontLoaders.getGoogleSans(18), true, true);
    public static CFontRenderer GoogleSans20 = new CFontRenderer(FontLoaders.getGoogleSans(20), true, true);
    public static CFontRenderer GoogleSans22 = new CFontRenderer(FontLoaders.getGoogleSans(22), true, true);
    public static CFontRenderer GoogleSans24 = new CFontRenderer(FontLoaders.getGoogleSans(24), true, true);
    public static CFontRenderer GoogleSans28 = new CFontRenderer(FontLoaders.getGoogleSans(28), true, true);
    public static CFontRenderer GoogleSans35 = new CFontRenderer(FontLoaders.getGoogleSans(35), true, true);
    public static CFontRenderer GoogleSans40 = new CFontRenderer(FontLoaders.getGoogleSans(40), true, true);
    public static CFontRenderer SF18 = new CFontRenderer(FontLoaders.getSF(18), true, true);
    public static CFontRenderer SF20 = new CFontRenderer(FontLoaders.getSF(20), true, true);
    public static CFontRenderer SF24 = new CFontRenderer(FontLoaders.getSF(24), true, true);
    public static CFontRenderer NovICON18 = new CFontRenderer(FontLoaders.getNovICON(18), true, true);
    public static CFontRenderer NovICON20 = new CFontRenderer(FontLoaders.getNovICON(20), true, true);
    public static CFontRenderer NovICON24 = new CFontRenderer(FontLoaders.getNovICON(24), true, true);
    public static CFontRenderer NovICON28 = new CFontRenderer(FontLoaders.getNovICON(28), true, true);
    public static CFontRenderer NovICON64 = new CFontRenderer(FontLoaders.getNovICON(64), true, true);


    public static CFontRenderer JelloBIG41 = new CFontRenderer(FontLoaders.getfonts(41, "jellomedium", true), true, true);
    public static CFontRenderer Jello19 = new CFontRenderer(FontLoaders.getfonts(19, "jellolight", true), true, true);
    public static CFontRenderer Jello25 = new CFontRenderer(FontLoaders.getfonts(25, "jellolight", true), true, true);
    public static CFontRenderer Jello24 = new CFontRenderer(FontLoaders.getfonts(24, "jellolight", true), true, true);
    public static CFontRenderer Jello18 = new CFontRenderer(FontLoaders.getfonts(18, "jellolight", true), true, true);
    public static CFontRenderer Jello16 = new CFontRenderer(FontLoaders.getfonts(16, "jellolight", true), true, true);

    public static CFontRenderer Baloo16 = new CFontRenderer(FontLoaders.getfonts(16, "Baloo", true), true, true);
    public static CFontRenderer Baloo18 = new CFontRenderer(FontLoaders.getfonts(18, "Baloo", true), true, true);


    public static CFontRenderer calibrilite15 = new CFontRenderer(FontLoaders.getfonts(15, "Candaral", true), true, true);
    public static CFontRenderer calibrilite15s = new CFontRenderer(FontLoaders.getfonts(15, "calibrilites", true), true, true);
    public static CFontRenderer calibrilite20 = new CFontRenderer(FontLoaders.getfonts(20, "Candaral", true), true, true);
    public static CFontRenderer calibrilite24 = new CFontRenderer(FontLoaders.getfonts(24, "Candaral", true), true, true);

    public static CFontRenderer calibrilite32 = new CFontRenderer(FontLoaders.getfonts(32, "Candaral", true), true, true);
    public static CFontRenderer calibrilite35 = new CFontRenderer(FontLoaders.getfonts(35, "Candaral", true), true, true);
    public static CFontRenderer calibrilite50 = new CFontRenderer(FontLoaders.getfonts(50, "Candaral", true), true, true);
    public static CFontRenderer calibrilite18 = new CFontRenderer(FontLoaders.getfonts(18, "Candaral", true), true, true);


    public static CFontRenderer Arial18 = new CFontRenderer(FontLoaders.getfonts(18, "Arial", true), true, true);

    public static CFontRenderer roboto18 = new CFontRenderer(FontLoaders.getfonts(18, "Roboto-Medium", true), true, true);
    public static CFontRenderer roboto16 = new CFontRenderer(FontLoaders.getfonts(16, "Roboto-Medium", true), true, true);

    private static Font getComfortaa(int size) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("FoodTower/fonts/Comfortaa.ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(0, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", 0, size);
        }
        return font;
    }

    private static Font getNovICON(int size) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("FoodTower/fonts/NovICON.ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(0, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", 0, size);
        }
        return font;
    }

    private static Font getSF(int size) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("FoodTower/fonts/SFREGULAR.ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(0, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", Font.PLAIN, size);
        }
        return font;
    }

    private static Font getRoboto(int size) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("FoodTower/fonts/Roboto-Medium.ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(0, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", Font.PLAIN, size);
        }
        return font;
    }

    private static Font getGoogleSans(int size) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("FoodTower/fonts/GoogleSans.ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(0, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", Font.PLAIN, size);
        }
        return font;
    }

    private static Font getfonts(int size, String fontname, boolean ttf) {
        Font font;
        try {
            InputStream is;
            if (ttf) {
                is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("FoodTower/fonts/" + fontname + ".ttf")).getInputStream();
            } else {
                is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("FoodTower/fonts/" + fontname + ".otf")).getInputStream();
            }
            font = Font.createFont(0, is);
            font = font.deriveFont(Font.PLAIN, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", 0, size);
        }
        return font;
    }
}
