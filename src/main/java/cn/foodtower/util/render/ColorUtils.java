package cn.foodtower.util.render;

import cn.foodtower.Client;

import java.awt.*;
import java.util.regex.Pattern;

public class ColorUtils {
    private static final Pattern COLOR_PATTERN = Pattern.compile("(?i)§[0-9A-FK-OR]");

    public static int randomColor() {
        return -16777216 | (int) (Math.random() * 1.6777215E7);
    }

    public static int transparency(int color, double alpha) {
        Color c = new Color(color);
        float r = 0.003921569f * (float) c.getRed();
        float g = 0.003921569f * (float) c.getGreen();
        float b = 0.003921569f * (float) c.getBlue();
        return new Color(r, g, b, (float) alpha).getRGB();
    }

    public static int getStaticColor(final float hueoffset, final float saturation, final float brightness) {
        return Color.HSBtoRGB(hueoffset / 54, saturation, brightness);
    }

    public static Color mixColors(final Color color1, final Color color2, final double percent) {
        final double inverse_percent = 1.0 - percent;
        final int redPart = (int) (color1.getRed() * percent + color2.getRed() * inverse_percent);
        final int greenPart = (int) (color1.getGreen() * percent + color2.getGreen() * inverse_percent);
        final int bluePart = (int) (color1.getBlue() * percent + color2.getBlue() * inverse_percent);
        return new Color(redPart, greenPart, bluePart);
    }

    public static int getTargetHudColor(float health, float maxHealth) {
        float percentage = health / maxHealth;
        if (percentage >= 0.75f) {
            return new Color(100, 200, 100).getRGB();
        }
        if (percentage < 0.75 && percentage >= 0.25) {
            return new Color(200, 200, 100).getRGB();
        }
        return new Color(200, 75, 75).getRGB();
    }

    public static Color getHanabiHealthColor(float health, float maxHealth) {
        float[] fractions = new float[]{0.0f, 0.5f, 1.0f};
        Color[] colors = new Color[]{new Color(0, 81, 179), new Color(0, 153, 255), Client.getBlueColor(255)};
        float progress = health / maxHealth;
        return ColorUtils.blendColors(fractions, colors, progress).brighter();
    }

    public static Color getHealthColor(float health, float maxHealth) {
        float[] fractions = new float[]{0.0f, 0.5f, 1.0f};
        Color[] colors = new Color[]{new Color(255, 0, 0), new Color(255, 255, 0), new Color(0, 255, 0)};
        float progress = health / maxHealth;
        return ColorUtils.blendColors(fractions, colors, progress).brighter();
    }

    public static int getColor(final float hueoffset, final float saturation, final float brightness) {
        final float speed = 4500;
        final float hue = (System.currentTimeMillis() % (int) speed) / speed;

        return Color.HSBtoRGB(hue - hueoffset / 54, saturation, brightness);
    }

    public static String getColor(int n) {
        if (n != 1) {
            if (n == 2) {
                return "\u00a7a";
            }
            if (n == 3) {
                return "\u00a73";
            }
            if (n == 4) {
                return "\u00a74";
            }
            if (n >= 5) {
                return "\u00a7e";
            }
        }
        return "\u00a7f";
    }

    public static Color getBlendColor(double current, double max) {
        long base = Math.round(max / 5.0);
        if (current >= (base * 5L)) {
            return new Color(15, 255, 15);
        }
        if (current >= (base << 2)) {
            return new Color(166, 255, 0);
        }
        if (current >= (base * 3L)) {
            return new Color(255, 191, 0);
        }
        if (current >= (base << 1)) {
            return new Color(255, 89, 0);
        }
        return new Color(255, 0, 0);
    }

    public static Color blendColors(float[] fractions, Color[] colors, float progress) {
        if (fractions.length == colors.length) {
            int[] indices = ColorUtils.getFractionIndices(fractions, progress);
            float[] range = new float[]{fractions[indices[0]], fractions[indices[1]]};
            Color[] colorRange = new Color[]{colors[indices[0]], colors[indices[1]]};
            float max = range[1] - range[0];
            float value = progress - range[0];
            float weight = value / max;
            Color color = ColorUtils.blend(colorRange[0], colorRange[1], 1.0f - weight);
            return color;
        }
        throw new IllegalArgumentException("Fractions and colours must have equal number of elements");
    }

    public static int[] getFractionIndices(float[] fractions, float progress) {
        int startPoint;
        int[] range = new int[2];
        for (startPoint = 0; startPoint < fractions.length && fractions[startPoint] <= progress; ++startPoint) {
        }
        if (startPoint >= fractions.length) {
            startPoint = fractions.length - 1;
        }
        range[0] = startPoint - 1;
        range[1] = startPoint;
        return range;
    }

    public static int transparency(Color color, double alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) alpha).getRGB();
    }

    public static Color rainbow(long offset, float fade) {
        float hue = (float) (System.nanoTime() + offset) / 1.0E10f % 1.0f;
        long color = Long.parseLong(Integer.toHexString(Color.HSBtoRGB(hue, 1.0f, 1.0f)), 16);
        Color c = new Color((int) color);
        return new Color((float) c.getRed() / 255.0f * fade, (float) c.getGreen() / 255.0f * fade, (float) c.getBlue() / 255.0f * fade, (float) c.getAlpha() / 255.0f);
    }

    public static float[] getRGBA(int color) {
        float a = (float) (color >> 24 & 255) / 255.0f;
        float r = (float) (color >> 16 & 255) / 255.0f;
        float g = (float) (color >> 8 & 255) / 255.0f;
        float b = (float) (color & 255) / 255.0f;
        return new float[]{r, g, b, a};
    }

    public static int intFromHex(String hex) {
        try {
            if (hex.equalsIgnoreCase("rainbow")) {
                return ColorUtils.rainbow(0L, 1.0f).getRGB();
            }
            return Integer.parseInt(hex, 16);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static String hexFromInt(int color) {
        return ColorUtils.hexFromInt(new Color(color));
    }

    public static String hexFromInt(Color color) {
        return Integer.toHexString(color.getRGB()).substring(2);
    }

    public static Color blend(Color color1, Color color2, double ratio) {
        float r = (float) ratio;
        float ir = 1.0f - r;
        float[] rgb1 = new float[3];
        float[] rgb2 = new float[3];
        color1.getColorComponents(rgb1);
        color2.getColorComponents(rgb2);
        Color color3 = new Color(rgb1[0] * r + rgb2[0] * ir, rgb1[1] * r + rgb2[1] * ir, rgb1[2] * r + rgb2[2] * ir);
        return color3;
    }

    public static Color blend(Color color1, Color color2) {
        return ColorUtils.blend(color1, color2, 0.5);
    }

    public static Color darker(Color color, double fraction) {
        int red = (int) Math.round((double) color.getRed() * (1.0 - fraction));
        int green = (int) Math.round((double) color.getGreen() * (1.0 - fraction));
        int blue = (int) Math.round((double) color.getBlue() * (1.0 - fraction));
        if (red < 0) {
            red = 0;
        } else if (red > 255) {
            red = 255;
        }
        if (green < 0) {
            green = 0;
        } else if (green > 255) {
            green = 255;
        }
        if (blue < 0) {
            blue = 0;
        } else if (blue > 255) {
            blue = 255;
        }
        int alpha = color.getAlpha();
        return new Color(red, green, blue, alpha);
    }

    public static Color getDarker(Color before, int dark, int alpha) {
        int rDank = Math.max(before.getRed() - dark, 0);
        int gDank = Math.max(before.getGreen() - dark, 0);
        int bDank = Math.max(before.getBlue() - dark, 0);
        return new Color(rDank, gDank, bDank, alpha);
    }

    public static Color getLighter(Color before, int light, int alpha) {
        int rDank = Math.min(before.getRed() + light, 255);
        int gDank = Math.min(before.getGreen() + light, 255);
        int bDank = Math.min(before.getBlue() + light, 255);
        return new Color(rDank, gDank, bDank, alpha);
    }

    public static String getHexName(Color color) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        String rHex = Integer.toString(r, 16);
        String gHex = Integer.toString(g, 16);
        String bHex = Integer.toString(b, 16);
        return (rHex.length() == 2 ? rHex : "0" + rHex) + (gHex.length() == 2 ? gHex : "0" + gHex) + (bHex.length() == 2 ? bHex : "0" + bHex);
    }

    public static double colorDistance(double r1, double g1, double b1, double r2, double g2, double b2) {
        double a = r2 - r1;
        double b3 = g2 - g1;
        double c = b2 - b1;
        return Math.sqrt(a * a + b3 * b3 + c * c);
    }

    public static double colorDistance(double[] color1, double[] color2) {
        return ColorUtils.colorDistance(color1[0], color1[1], color1[2], color2[0], color2[1], color2[2]);
    }

    public static double colorDistance(Color color1, Color color2) {
        float[] rgb1 = new float[3];
        float[] rgb2 = new float[3];
        color1.getColorComponents(rgb1);
        color2.getColorComponents(rgb2);
        return ColorUtils.colorDistance(rgb1[0], rgb1[1], rgb1[2], rgb2[0], rgb2[1], rgb2[2]);
    }

    public static boolean isDark(double r, double g, double b) {
        double dWhite = ColorUtils.colorDistance(r, g, b, 1.0, 1.0, 1.0);
        double dBlack = ColorUtils.colorDistance(r, g, b, 0.0, 0.0, 0.0);
        return dBlack < dWhite;
    }

    public static boolean isDark(Color color) {
        float r = (float) color.getRed() / 255.0f;
        float g = (float) color.getGreen() / 255.0f;
        float b = (float) color.getBlue() / 255.0f;
        return ColorUtils.isDark(r, g, b);
    }

    public static String stripColor(String input) {
        return COLOR_PATTERN.matcher(input).replaceAll("");
    }

    public static int reAlpha(int color, float alpha) {
        Color c = new Color(color);
        float r = 0.003921569F * (float) c.getRed();
        float g = 0.003921569F * (float) c.getGreen();
        float b = 0.003921569F * (float) c.getBlue();
        return (new Color(r, g, b, alpha)).getRGB();
    }

    public static Color skyRainbow(int var2, float st, float bright) {
        double v1 = Math.ceil(System.currentTimeMillis() + (var2 * 109L)) / 5;
        return Color.getHSBColor(((float) ((v1 %= 360.0) / 360.0)) < 0.5 ? -((float) (v1 / 360.0)) : (float) (v1 / 360.0), st, bright);
    }
}
