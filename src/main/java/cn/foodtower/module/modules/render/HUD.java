package cn.foodtower.module.modules.render;

import cn.foodtower.Client;
import cn.foodtower.api.EventHandler;
import cn.foodtower.api.events.Render.EventRender2D;
import cn.foodtower.api.events.Render.EventRender3D;
import cn.foodtower.api.value.Mode;
import cn.foodtower.api.value.Numbers;
import cn.foodtower.api.value.Option;
import cn.foodtower.api.value.Value;
import cn.foodtower.fastuni.FastUniFontRenderer;
import cn.foodtower.fastuni.FontLoader;
import cn.foodtower.manager.ModuleManager;
import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;
import cn.foodtower.module.modules.player.AutoTP;
import cn.foodtower.module.modules.world.MusicPlayer;
import cn.foodtower.ui.cloudmusic.MusicManager;
import cn.foodtower.ui.font.CFontRenderer;
import cn.foodtower.ui.font.FontLoaders;
import cn.foodtower.ui.jello.Compass;
import cn.foodtower.ui.notifications.user.Notifications;
import cn.foodtower.util.anim.AnimationUtil;
import cn.foodtower.util.anim.AnimationUtils;
import cn.foodtower.util.anim.Palette;
import cn.foodtower.util.math.MathUtil;
import cn.foodtower.util.math.Vec3;
import cn.foodtower.util.misc.Helper;
import cn.foodtower.util.render.ColorUtils;
import cn.foodtower.util.render.Colors;
import cn.foodtower.util.render.DrawUtil;
import cn.foodtower.util.render.RenderUtil;
import cn.foodtower.util.time.TimerUtil;
import com.google.common.collect.Lists;
import javafx.scene.media.MediaPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.main.Main;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;

import static net.minecraft.util.MathHelper.abs;

public class HUD extends Module {
    public static Option hideRender = new Option("HideRender", false);
    public static Numbers<Double> ArrayGap = new Numbers<>("ArraylistGap", 0.0D, -10.0D, 10.0D, 1D);
    public static Mode ArrayMode = new Mode("ArrayColorMode", ArrayModeE.values(), ArrayModeE.Sky);
    public static Mode ArrayFontMode = new Mode("ArrayFont", ArrayFont.values(), ArrayFont.Baloo16);
    public static Option customlogo = new Option("Logo", true);
    public static Option lhp = new Option("LowHPWarning", true);
    public static Option Arraylists = new Option("ArrayList", true);
    public static Option ArrayShadow = new Option("ArrayShadow", true);
    public static Numbers<Double> RainbowSpeed = new Numbers<>("RainbowSpeed", 6.0D, 1.0D, 20.0D, 1D);
    public static Option clientCape = new Option("Cape", true);
    public static Mode Widget = new Mode("Widget", widgetE.values(), widgetE.None);
    public static Numbers<Double> r = new Numbers<>("Red", 255.0, 0.0, 255.0, 1.0);
    public static Numbers<Double> g = new Numbers<>("Green", 255d, 0.0, 255.0, 1.0);
    public static Numbers<Double> b = new Numbers<>("Blue", 255.0, 0.0, 255.0, 1.0);
    public static Numbers<Double> a = new Numbers<>("Alpha", 255.0, 0.0, 255.0, 1.0);
    public static Numbers<Double> Arraybackground = new Numbers<>("ArrayAlpha", 160.0, 0.0, 255.0, 1.0);
    public static Mode logomode = new Mode("LogoMode", HUD.logomodeE.values(), logomodeE.FoodTower);
    public static Mode RectMode = new Mode("RectMode", RectModes.values(), RectModes.None);
    public static Option GuiChatBackGround = new Option("GuiChatBackGround", false);
    public static boolean firstrun = true;
    public static boolean shouldMove;
    public static float hue = 0.0F;
    public static Color RainbowColor = Color.getHSBColor(hue / 255.0F, 0.55F, 0.9F);
    public static Color RainbowColors = Color.getHSBColor(hue / 255.0F, 0.4f, 0.8f);
    public static Color RainbowColor2 = Client.getClientColor(true);
    public static int PotY;
    //    skid from awareline
    private static int count;
    private static CFontRenderer fontarry;
    private final Numbers<Double> skyDistanceValue = new Numbers<>("SkyDistance", 1d, -4d, 4d, 1d);
    private final Numbers<Double> skyBrightness = new Numbers<>("SkyBrightness", 1d, 0d, 1d, 0.1);
    private final Numbers<Double> skySaturation = new Numbers<>("SkySaturation", 0.5, 0d, 1d, 0.1);
    private final Option info = new Option("Information", true);
    private final Option CompassValue = new Option("Compass", false);
    private final long startTime = System.currentTimeMillis();
    public Compass compass = new Compass(325, 325, 1, 2, true);
    public TimerUtil timer = new TimerUtil();
    public float animation = 0;
    boolean lowhealth = false;
    boolean arryfont;
    SimpleDateFormat df = new SimpleDateFormat("HH:mm");
    float animLogoX = 2;
    float animLogoY = 3;
    boolean firstTime = true;
    int addY;
    private final int[] counter = new int[]{0};
    private int width;

    public HUD() {
        super("HUD", new String[]{"gui"}, ModuleType.Render);
        this.addValues(logomode, Widget, RainbowSpeed, Arraylists, ArrayGap, hideRender, RectMode, info, ArrayShadow, ArrayFontMode, ArrayMode, skySaturation, skyBrightness, skyDistanceValue, customlogo, CompassValue, lhp, r, g, b, a, clientCape, Arraybackground, GuiChatBackGround);
        this.setEnabled(true);
        setValueDisplayable(RainbowSpeed, ArrayMode, new Enum[]{ArrayModeE.Rainbow, ArrayModeE.BlueIceSakura, ArrayModeE.Rainbow2, ArrayModeE.Wave, ArrayModeE.NEON});
        setValueDisplayable(new Value[]{skyDistanceValue, skyBrightness, skySaturation}, ArrayMode, ArrayModeE.Sky);
    }

    private static int HUDColor() {
        return new Color(r.get().intValue(), g.get().intValue(), b.get().intValue()).getRGB();
    }

    @EventHandler
    private void Render3d(EventRender3D e) {
        if (AutoTP.path != null && ModuleManager.getModuleByClass(AutoTP.class).isEnabled()) {
            for (int i = 0; i < AutoTP.path.size(); i++) {
                try {
                    for (Vec3 pos : AutoTP.path) {
                        if (pos != null)
                            //GL11.glEnable(3042);
                            RenderUtil.drawPath(pos);
                    }
                } catch (Exception ignored) {

                }
            }
        }
    }

    @EventHandler
    private void renderHud(EventRender2D event) {
        ScaledResolution sr = new ScaledResolution(mc);

        compass = new Compass(325, 325, 1, 4, true);
        if (Main.isbeta && firstrun) {
            Helper.sendMessage("FoodTower Beta tips:");
            Helper.sendMessage("May everything be fine.");
        }
        if (firstrun) firstrun = false;

        RainbowColors = Color.getHSBColor(hue / 255.0F, 0.4f, 0.8f);
        Color rainbowcolors = Color.getHSBColor(hue / 255.0f, 0.4f, 0.8f);
        Color rainbowcolors2 = Color.getHSBColor(hue / 255.0f, 1f, 1f);
        Color color2222 = Color.getHSBColor(hue / 255.0F, 0.55F, 0.9F);
        int c2222 = color2222.getRGB();
        int colorXD;
        if (!mc.gameSettings.showDebugInfo) {
            if (ArrayMode.get().equals(ArrayModeE.NewRainbow)) {
                count = 0;
            }
        }
        if (CompassValue.get()) {
            compass.draw(sr);
        }
        if (ArrayMode.get().equals(ArrayModeE.Wave)) {
            colorXD = Palette.fade(Client.getClientColor(false), (addY + 11) / 11, 16).getRGB();
        } else if (ArrayMode.get().equals(ArrayModeE.NewRainbow)) {
            double rainbowDelay = Math.ceil((double) ((System.currentTimeMillis() + (long) ((double) (++count * -50))) / 8L) + -2.5);
            colorXD = Color.getHSBColor((double) ((float) (rainbowDelay / 360.0)) < 0.5 ? -((float) (rainbowDelay / 360.0)) : (float) ((rainbowDelay %= 360.0) / 360.0), 0.5f, 1.0f).getRGB();
        } else if (ArrayMode.get().equals(ArrayModeE.Rainbow)) {
            colorXD = c2222;
        } else if (ArrayMode.get().equals(ArrayModeE.Rainbow2)) {
            colorXD = RainbowColor2.getRGB();
        } else if (ArrayMode.get().equals(ArrayModeE.BlueIceSakura)) {
            colorXD = new Color(rainbowcolors2.getRed(), 190, 255).getRGB();
        } else if (ArrayMode.get().equals(ArrayModeE.NEON)) {
            colorXD = new Color(rainbowcolors.getRed(), rainbowcolors.getGreen(), 255).getRGB();
        } else if (ArrayMode.get().equals(ArrayModeE.Sky)) {
            colorXD = ColorUtils.skyRainbow(counter[0] * (skyDistanceValue.get().intValue() * 50), skySaturation.get().floatValue(), skyBrightness.get().floatValue()).getRGB();
            counter[0] = counter[0] - 1;
        } else {
            colorXD = Client.getClientColor();
        }

        hue += RainbowSpeed.get().floatValue() / 5.0F;
        if (hue > 255.0F) {
            hue = 0.0F;
        }
        float h = hue;
        counter[0] = 0;
        if (lhp.get()) {
            if (mc.thePlayer.getHealth() < 6 && !lowhealth) {
                Notifications.getManager().post("Warning!", "当前血量过低！", Notifications.Type.WARNING);
                lowhealth = true;
            }
            if (mc.thePlayer.getHealth() > 6 && lowhealth) {
                lowhealth = false;
            }
        }
        CFontRenderer font = FontLoaders.GoogleSans18;

        if (ModuleManager.getModByClass(MusicPlayer.class).isEnabled() && MusicManager.INSTANCE.lyric && MusicManager.INSTANCE.getMediaPlayer().getStatus() == MediaPlayer.Status.PLAYING) {
            FastUniFontRenderer lyricFont = FontLoader.msFont25;
            int addonYlyr = MusicPlayer.musicPosYlyr.get().intValue();
            //Lyric
            int borderCol = new Color(238, 171, 227).getRGB();
            int col = new Color(0xffE8DEFF).getRGB();

            lyricFont.drawCenterOutlinedString(MusicManager.INSTANCE.lrcCur.contains("_EMPTY_") ? "" : MusicManager.INSTANCE.lrcCur, ScaledResolution.getScaledWidth() / 2f - 0.5f, ScaledResolution.getScaledHeight() - 140 - 80 + addonYlyr, borderCol, col);

            lyricFont.drawCenterOutlinedString(MusicManager.INSTANCE.tlrcCur.contains("_EMPTY_") ? "" : MusicManager.INSTANCE.tlrcCur, ScaledResolution.getScaledWidth() / 2f, ScaledResolution.getScaledHeight() - 120 + 0.5f - 80 + addonYlyr, new Color(0x595959).getRGB(), col);

        }
        if ((MusicManager.showMsg)) {
            Notifications.getManager().post("正在播放", MusicManager.INSTANCE.getCurrentTrack().name + " - " + MusicManager.INSTANCE.getCurrentTrack().artists, 5000L, Notifications.Type.MUSIC);
            MusicManager.showMsg = false;
        }

//        if (ModuleManager.getModuleByClass( Scaffold.class).isEnabled() && Scaffold.renderBlockCount) {
//            Scaffold.renderBlock();
//        }


        String name;
        String fps;
        String speedc;
        String user;
        String xyz;
        if (!Widget.get().equals(widgetE.None) && !Widget.get().equals(widgetE.Astolfo)) {
            int widgetwidth = ((widgetE) Widget.get()).width;
            int widgetheight = ((widgetE) Widget.get()).height;
            int id = ((widgetE) Widget.get()).id;
            widgetwidth *= 0.25;
            widgetheight *= 0.25;
            RenderUtil.drawCustomImage(RenderUtil.width() - 100 - widgetwidth, RenderUtil.height() - widgetheight - (mc.ingameGUI.getChatGUI().getChatOpen() ? 14 : 0), widgetwidth, widgetheight, new ResourceLocation("FoodTower/widget/" + id + ".png"));
        } else if (Widget.get().equals(widgetE.Astolfo)) {
            RenderUtil.drawImage(new ResourceLocation("FoodTower/AstolfoTrifasSprite.png"), RenderUtil.width() - 160, RenderUtil.height() - 70, 256, 256);
        }
        if (customlogo.get()) {
            HUD.shouldMove = true;
            switch ((logomodeE) logomode.get()) {
                case Dark_Distance:
                case Distance:
                    CFontRenderer dfont1 = FontLoaders.calibrilite50;
                    CFontRenderer dfont2 = FontLoaders.calibrilite15s;
                    if (animLogoX != (mc.gameSettings.showDebugInfo ? ScaledResolution.getScaledWidth() / 2f : 2f) || animLogoY != (mc.gameSettings.showDebugInfo ? 80f : 3f)) {
                        animLogoX = AnimationUtil.moveUD(animLogoX, mc.gameSettings.showDebugInfo ? (ScaledResolution.getScaledWidth() / 2f) - dfont1.getStringWidth("FoodTower") / 2f : 2f, 10f / Minecraft.getDebugFPS(), 1f / Minecraft.getDebugFPS());
                        animLogoY = AnimationUtil.moveUD(animLogoY, mc.gameSettings.showDebugInfo ? 80f : 3f, 10f / Minecraft.getDebugFPS(), 1f / Minecraft.getDebugFPS());
                    }
                    GlStateManager.enableBlend();
                    RenderUtil.drawImage(new ResourceLocation("Jello/shadow.png"), -20, 0, dfont1.getStringWidth("FoodTower") + 35, dfont1.getStringHeight("FoodTower") + 1);

                    dfont1.drawString(Client.name, animLogoX, animLogoY, new Color(255, 255, 255).getRGB());
                    dfont2.drawString(Client.ClientVersion, animLogoX + 2f, animLogoY + 24, new Color(255, 255, 255).getRGB());
                    //GL11.glDisable(3042);
                    break;
                case Jigsaw: {
                    FastUniFontRenderer font1 = Client.FontLoaders.KomikaTitleBold50;
                    if (Client.getClientColor(180) == new Color(255, 255, 255, 180).getRGB()) {
                        font1.drawStringWithShadow(Client.name, 4.0f, 22, new Color(255, 255, 255).getRGB(), Client.getBBlueColor(), 2);
                    } else {
                        font1.drawStringWithShadow(Client.name, 4.0f, 22, new Color(255, 255, 255).getRGB(), Client.getClientColor(180), 2);
                    }
                    break;
                }
                case Novoline: {
                    double xDist = mc.thePlayer.posX - mc.thePlayer.lastTickPosX;
                    double zDist = mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ;
                    double moveSpeed = Math.sqrt(xDist * xDist + zDist * zDist) * 2 * mc.timer.timerSpeed;
                    double speed = MathUtil.round(moveSpeed * 10, 2);
                    name = "\247l" + Client.name.charAt(0);
                    user = "User:" + " " + EnumChatFormatting.WHITE + Client.userName;
                    fps = "FPS:" + " " + EnumChatFormatting.WHITE + Minecraft.getDebugFPS();
                    speedc = "Speed:" + " " + EnumChatFormatting.WHITE + speed;
                    xyz = "XYZ:" + " " + EnumChatFormatting.WHITE + MathHelper.floor_double(mc.thePlayer.posX) + " " + MathHelper.floor_double(mc.thePlayer.posY) + " " + MathHelper.floor_double(mc.thePlayer.posZ);
                    FontLoaders.SF18.drawStringWithShadow(user, RenderUtil.width() - FontLoaders.SF18.getStringWidth(user) - 80, RenderUtil.height() - 9, colorXD);
                    FontLoaders.SF18.drawStringWithShadow(xyz, new ScaledResolution(mc).getScaledWidth() / 250 - this.width, new ScaledResolution(mc).getScaledHeight() - 9, colorXD);
                    FontLoaders.SF18.drawStringWithShadow(speedc, new ScaledResolution(mc).getScaledWidth() / 250 - this.width, new ScaledResolution(mc).getScaledHeight() - 19, colorXD);
                    FontLoaders.SF18.drawStringWithShadow(fps, new ScaledResolution(mc).getScaledWidth() / 250 - this.width, new ScaledResolution(mc).getScaledHeight() - 29, colorXD);
                    FontLoaders.SF18.drawStringWithShadow(name, 3.0F, 3.0F, colorXD);
                    String ok = Client.name.substring(1);
                    font.drawStringWithShadow(ok + "" + " \2477(\247r" + df.format(new Date()) + "\2477)\247r", font.getStringWidth(name) + 3.5F, 3.0F, new Color(255, 255, 255).getRGB());
                    //GL11.glDisable(3042);
                    break;
                }
                case FoodTower:
                    double xDist = mc.thePlayer.posX - mc.thePlayer.lastTickPosX;
                    double zDist = mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ;
                    double moveSpeed = Math.sqrt(xDist * xDist + zDist * zDist) * 2 * mc.timer.timerSpeed;
                    double speed = MathUtil.round(moveSpeed * 10, 2);
                    name = "\247l" + Client.name.charAt(0);
                    String version = "\247l" + Client.version.charAt(0);
//                    user = "User:" + " " + EnumChatFormatting.WHITE + Client.userName;
                    fps = "FPS:" + " " + EnumChatFormatting.GRAY + Minecraft.getDebugFPS();
                    speedc = "Speed:" + " " + EnumChatFormatting.GRAY + speed;
                    xyz = "XYZ:" + " " + EnumChatFormatting.GRAY + MathHelper.floor_double(mc.thePlayer.posX) + " " + MathHelper.floor_double(mc.thePlayer.posY) + " " + MathHelper.floor_double(mc.thePlayer.posZ);
                    DrawUtil.drawRoundedRect((float) (ScaledResolution.getScaledWidth() / 250 - this.width - 1.5), 1.0F, (float) (FontLoaders.Baloo18.getStringWidth("  v" + version + " | " + Client.userName + " | " + Minecraft.getDebugFPS() + "FPS") + 63.5), 12, 8, new Color(0, 0, 0, 160).getRGB(), 2, colorXD);
//                    FontLoaders.SF18.drawStringWithShadow(user, RenderUtil.width() - FontLoaders.SF18.getStringWidth(user) - 80, RenderUtil.height() - 9, colorXD);
                    new ScaledResolution(mc);
                    FontLoaders.Baloo18.drawStringWithShadow(xyz, ScaledResolution.getScaledWidth() / 250 - this.width, ScaledResolution.getScaledHeight() - 9, -1);
                    FontLoaders.Baloo18.drawStringWithShadow(speedc, ScaledResolution.getScaledWidth() / 250 - this.width, new ScaledResolution(mc).getScaledHeight() - 19, -1);
                    FontLoaders.Baloo18.drawStringWithShadow(fps, ScaledResolution.getScaledWidth() / 250 - this.width, new ScaledResolution(mc).getScaledHeight() - 29, -1);
                    String ok = "\247l" + Client.name.substring(1);
//                    DrawUtil.drawBorderedRect(ScaledResolution.getScaledWidth() / 250 - this.width + 3.5f, 1f, 136f, 12f, 2f, new Color(50, 100, 255), new Color(0, 0, 0, 0));
                    FontLoaders.Baloo18.drawStringWithShadow(name, 3.0F, 3.0, -1);
//                    font.drawStringWithShadow(ok + "" + " \2477(\247r" + df.format(new Date()) + "\2477)\247r", font.getStringWidth(name) + 3.5F, 3.0F, new Color(255, 255, 255).getRGB());
                    FontLoaders.Baloo18.drawStringWithShadow(ok, font.getStringWidth(name) + 3.5F, 3.0, new Color(255, 255, 255).getRGB());
                    FontLoaders.Baloo18.drawStringWithShadow("  v" + version + " | " + Client.userName + " | " + Minecraft.getDebugFPS() + "FPS", font.getStringWidth(name) + font.getStringWidth(ok) + 3.5F, 3.0, new Color(255, 255, 255).getRGB());
                    //GL11.glDisable(3042);
                    break;
            }
        }
        if (!mc.gameSettings.showDebugInfo) {
            // ArrayList
            arryfont = true;
            switch ((ArrayFont) ArrayFontMode.get()) {
                case SF18:
                    fontarry = FontLoaders.SF18;
                    break;
                case SF20:
                    fontarry = FontLoaders.SF20;
                    break;
                case Jello18:
                    fontarry = FontLoaders.Jello18;
                    break;
                case GoogleSans16:
                    fontarry = FontLoaders.GoogleSans16;
                    break;
                case GoogleSans18:
                    fontarry = FontLoaders.GoogleSans18;
                    break;
                case Jello16:
                    fontarry = FontLoaders.Jello16;
                    break;
                case Baloo16:
                    fontarry = FontLoaders.Baloo16;
                    break;
                case Baloo18:
                    fontarry = FontLoaders.Baloo18;
                    break;
                case Minecraft:
                    arryfont = false;
            }
            if (fontarry == null) {
                fontarry = FontLoaders.GoogleSans16;
            }
            int[] counter = {0};
            if (Arraylists.get()) {
                ArrayList<Module> sorted = new ArrayList<>();
                for (Module m : ModuleManager.getModules()) {
                    if ((m.getType().equals(ModuleType.Render) && !m.getName().equalsIgnoreCase("PacketMotior") && hideRender.get()) || m.wasRemoved() || (!m.isEnabled() && !m.getRender()))
                        continue;
                    sorted.add(m);
                }
                if (!arryfont) {
                    sorted.sort((o1, o2) -> mc.fontRendererObj.getStringWidth(o2.getSuffix().isEmpty() ? Client.getModuleName(o2) : String.format("%s %s", Client.getModuleName(o2), o2.getSuffix())) - mc.fontRendererObj.getStringWidth(o1.getSuffix().isEmpty() ? Client.getModuleName(o1) : String.format("%s %s", Client.getModuleName(o1), o1.getSuffix())));
                } else {
                    sorted.sort((o1, o2) -> fontarry.getStringWidth(o2.getSuffix().isEmpty() ? Client.getModuleName(o2) : String.format("%s %s", Client.getModuleName(o2), o2.getSuffix())) - fontarry.getStringWidth(o1.getSuffix().isEmpty() ? Client.getModuleName(o1) : String.format("%s %s", Client.getModuleName(o1), o1.getSuffix())));
                }
                int nextY = 0;
                double lastX = 0;
                float width;
                boolean first = true;
                Color color = new Color(r.get().intValue(), g.get().intValue(), b.get().intValue(), a.get().intValue());
                for (Module m : sorted) {
                    Color rainbowcolor = Color.getHSBColor(h / 255.0f, 0.4f, 0.8f);
                    Color rainbowcolor2 = Color.getHSBColor(h / 255.0f, 0.6f, 1f);
                    color = new Color(r.get().intValue(), g.get().intValue(), b.get().intValue(), a.get().intValue());
                    if (h > 255.0F) {
                        h = 0.0F;
                    }
                    if (ArrayMode.get().equals(ArrayModeE.Wave)) {
                        color = Palette.fade(Client.getClientColor(false), (int) ((nextY - m.posYRend) / 11), 11);
                    }
                    if (ArrayMode.get().equals(ArrayModeE.NewRainbow)) {
                        double rainbowDelay = Math.ceil((double) ((System.currentTimeMillis() + (long) ((double) (++count * -50))) / 8L) + -2.5);
                        color = Color.getHSBColor((double) ((float) (rainbowDelay / 360.0)) < 0.5 ? -((float) (rainbowDelay / 360.0)) : (float) ((rainbowDelay %= 360.0) / 360.0), 0.5f, 1.0f);
                    }
                    if (ArrayMode.get().equals(ArrayModeE.Sky)) {
                        color = ColorUtils.skyRainbow(counter[0] * (skyDistanceValue.get().intValue() * 50), skySaturation.get().floatValue(), skyBrightness.get().floatValue());
                        this.counter[0] = this.counter[0] - 1;
                    }
                    if (ArrayMode.get().equals(ArrayModeE.Rainbow)) {
                        color = Color.getHSBColor(h / 255.0f, 0.5f, 0.9f);
                    }
                    if (ArrayMode.get().equals(ArrayModeE.Rainbow2)) {
                        color = RenderUtil.rainbow(counter[0] * -50, true);
                        RainbowColor2 = color;
                    }
                    if (ArrayMode.get().equals(ArrayModeE.BlueIceSakura)) {
                        color = new Color(rainbowcolor2.getRed(), 180, 255);
                    }
                    if (ArrayMode.get().equals(ArrayModeE.NEON)) {
                        color = new Color(rainbowcolor.getRed(), rainbowcolor.getGreen(), 255);
                    }
                    m.lastY = m.posY;
                    m.posY = nextY;
                    m.onRenderArray();
                    name = m.getSuffix().isEmpty() ? Client.getModuleName(m) : (String.format("%s %s", Client.getModuleName(m), m.getSuffix()));
                    double x = RenderUtil.width() - m.getX();
                    if (!RectMode.get().equals(RectModes.Right)) {
                        if (arryfont) {
                            RenderUtil.drawRect(x - 4, nextY + m.posYRend, x + fontarry.getStringWidth(name), nextY + m.posYRend + 11 + ArrayGap.get(), new Color(0, 0, 0, Arraybackground.get().intValue()).getRGB());
                        } else {
                            RenderUtil.drawRect(x - 4, nextY + m.posYRend, x + mc.fontRendererObj.getStringWidth(name), nextY + m.posYRend + 11 + ArrayGap.get(), new Color(0, 0, 0, Arraybackground.get().intValue()).getRGB());
                        }
                    } else {
                        if (arryfont) {
                            RenderUtil.drawRect(x - 4, nextY + m.posYRend, x + fontarry.getStringWidth(name) + 1, nextY + m.posYRend + 11 + ArrayGap.get(), new Color(0, 0, 0, Arraybackground.get().intValue()).getRGB());
                        } else {
                            RenderUtil.drawRect(x - 4, nextY + m.posYRend, x + mc.fontRendererObj.getStringWidth(name) + 1, nextY + m.posYRend + 11 + ArrayGap.get(), new Color(0, 0, 0, Arraybackground.get().intValue()).getRGB());
                        }
                    }

                    if (RectMode.get().equals(RectModes.Full)) {
                        double i = (lastX - 4 - 1) - (lastX - 3 - 1 - (x - 4 - 1));
                        boolean b = (lastX - 4 - 1) > i;
                        if (arryfont) {
                            if (first) {
                                RenderUtil.drawRect(x - 5, 0, x + fontarry.getStringWidth(name), 1, color.getRGB());
                            }
                        } else {
                            if (first) {
                                RenderUtil.drawRect(x - 5, 0, x + mc.fontRendererObj.getStringWidth(name), 1, color.getRGB());
                            }
                        }
                        RenderUtil.drawRect(x - 5, nextY + m.posYRend, x - 4, nextY + m.posYRend + 11 + ArrayGap.get(), color.getRGB());
                        if (!first) {
                            RenderUtil.drawRect(lastX - 4 - 1 + (b ? 1 : 0), nextY + m.posYRend - 1 + (b ? 0 : 1), i + 1, nextY + m.posYRend + (b ? 0 : 1), color.getRGB());
                        }
                    } else if (RectMode.get().equals(RectModes.Left)) {
                        RenderUtil.drawRect(x - 5, nextY + m.posYRend, x - 4, nextY + m.posYRend + 11 + ArrayGap.get(), color.getRGB());
                    } else if (RectMode.get().equals(RectModes.Right)) {
                        RenderUtil.drawRect(x + fontarry.getStringWidth(name) + 1, nextY + m.posYRend, x + fontarry.getStringWidth(name) + 2, nextY + m.posYRend + 11 + ArrayGap.get(), color.getRGB());
                    }
                    if (!arryfont) {
                        width = mc.fontRendererObj.getStringWidth(name);
                        if (ArrayShadow.get())
                            mc.fontRendererObj.drawStringWithShadow(name, (float) (x - 1), (float) (nextY + m.posYRend + (ArrayGap.get() / 2) + 1), color.getRGB());
                        else
                            mc.fontRendererObj.drawString(name, (int) (x - 1), (int) (nextY + m.posYRend + (ArrayGap.get()) + 1), color.getRGB());
                    } else {
                        width = fontarry.getStringWidth(name);
                        if (ArrayShadow.get())
                            fontarry.drawStringWithShadow(name, x - 1, nextY + m.posYRend + (ArrayGap.get() / 2) + 3, color.getRGB());
                        else
                            fontarry.drawString(name, (float) (x - 1), (float) (nextY + m.posYRend + (ArrayGap.get() / 2) + 3), color.getRGB());
                    }
                    if (RectMode.get().equals(RectModes.Right)) {
                        width += 2;
                    }
                    lastX = x;
                    first = false;
                    h += 9.0F;
                    if (!arryfont) {
                        if ((m.getX() != width) && (m.getRender() && m.isEnabled())) {
                            m.setX(AnimationUtils.animate(width, m.getX(), (14.4f / (float) Minecraft.getDebugFPS())));
                        }
                        if ((m.getX() > 0) && (m.getRender() && !m.isEnabled())) {
                            m.setX(AnimationUtils.animate(0, m.getX(), (14.4f / (float) Minecraft.getDebugFPS())));
                        }
                        if ((m.getX() <= 0)) {
                            m.setRender(false);
                        }
                        nextY += 11 + ArrayGap.get();
                    } else {
                        if ((m.getX() != width) && (m.getRender() && m.isEnabled())) {
                            m.setX(AnimationUtils.animate(width, m.getX(), (14.4f / (float) Minecraft.getDebugFPS())));
                        }
                        if ((m.getX() > -5) && (m.getRender() && !m.isEnabled())) {
                            m.setX(AnimationUtils.animate(-7, m.getX(), (14.4f / (float) Minecraft.getDebugFPS())));
                        }
                        if ((m.getX() <= -5)) {
                            m.setRender(false);
                        }
                        nextY += 11 + ArrayGap.get();
                    }
                    addY = nextY;
                    counter[0]++;
                    this.counter[0] = 0;
                    if (sorted.size() == counter[0]) {
                        if (RectMode.get().equals(RectModes.Full) || RectMode.get().equals(RectModes.Bottom)) {
                            RenderUtil.drawRect(lastX - (RectMode.get().equals(RectModes.Full) ? 4 : 3) - 1, nextY + m.posYRend, RenderUtil.width(), nextY + m.posYRend + 1, color.getRGB());
                        }
                    }
                }
            }
            int ping;
            String pings;
            double xDif = mc.thePlayer.posX - mc.thePlayer.prevPosX;
            double zDif = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
            double lastDist = Math.sqrt(xDif * xDif + zDif * zDif) * 20.0D;
            String text2 = (EnumChatFormatting.GRAY + "Bps: " + EnumChatFormatting.WHITE + String.format("%.2f", lastDist) + "b/s");
            try {
//                if( ServerUtils.isHypixelDomain(ServerUtils.getRemoteIp() )){
//                                        ping=0;
//                }else{
//                    ping = mc.getNetHandler().getPlayerInfo(mc.thePlayer.getUniqueID()).getResponseTime();
//                }
                ping = 0;

            } catch (NullPointerException e) {
                ping = 0;
            }
            if (ping == 0) pings = "N/A";
            else pings = ping + "ms";
            if (mc.thePlayer != null) {
                String text = EnumChatFormatting.GRAY + "X:" + EnumChatFormatting.WHITE + " " + MathHelper.floor_double(mc.thePlayer.posX) + " " + EnumChatFormatting.GRAY + "Y:" + EnumChatFormatting.WHITE + " " + MathHelper.floor_double(mc.thePlayer.posY) + " " + EnumChatFormatting.GRAY + "Z:" + EnumChatFormatting.WHITE + " " + MathHelper.floor_double(mc.thePlayer.posZ);
                int ychat;
                //String vertext = (Object)((Object)EnumChatFormatting.WHITE) + Client.RELTYPE + (Object)((Object)EnumChatFormatting.GRAY) + "-" + Client.RELclientVersion;
                ychat = mc.ingameGUI.getChatGUI().getChatOpen() ? 24 : 10;
                if (this.info.get()) {
                    //font2.drawStringWithShadow(vertext, new ScaledResolution(this.mc).getScaledWidth() - font2.getStringWidth(vertext) - 2, new ScaledResolution(this.mc).getScaledHeight() - font.getHeight() + y - 12 - ychat,new Color(255,255,255).getRGB());
                    if (!(logomode.get().equals(logomodeE.Novoline)) && !HUD.logomode.get().equals(logomodeE.FoodTower)) {
                        font.drawCenteredStringWithShadow(text, ScaledResolution.getScaledWidth() / 2f, font.getStringHeight(text) + 5, new Color(12, 12, 17).getRGB());
                        font.drawStringWithShadow(EnumChatFormatting.GRAY + "FPS: " + EnumChatFormatting.WHITE + Minecraft.debugFPS + EnumChatFormatting.GRAY + " Ping: " + EnumChatFormatting.WHITE + pings + " " + text2, 4.0, ScaledResolution.getScaledHeight() - ychat, Colors.WHITE.c);
                    }
                    this.drawPotionStatus(sr);
                }
            }
            if (ArrayMode.get().equals(ArrayModeE.NewRainbow) && (count += 3) > 100) {
                count = 0;
            }
        }
    }

    public Color hslRainbow(int index) {
        return Color.getHSBColor((float) ((abs(((((System.currentTimeMillis() - startTime) + index * 300L) / 1500) % 2) - 1) * (0.58 - 0.41)) + 0.41), 0.7f, 1f);
    }

    private void drawPotionStatus(ScaledResolution sr) {
        int y = 0;
        FastUniFontRenderer fonts = Client.FontLoaders.Chinese16;
        Iterator<PotionEffect> localIterator1 = mc.thePlayer.getActivePotionEffects().iterator();
        List<PotionEffect> myList = Lists.newArrayList(localIterator1);
        myList.sort((o1, o2) -> {
            String str1 = I18n.format(o1.getEffectName());
            str1 = str1 + getAmplifier(o1.getAmplifier());

            String str2 = I18n.format(o2.getEffectName());
            str2 = str2 + getAmplifier(o2.getAmplifier());

            if (o1.getDuration() < 600 && o1.getDuration() > 300) {
                str1 = str1 + "\u00a77:\u00a76 " + Potion.getDurationString(o1);
            } else if (o1.getDuration() < 300) {
                str1 = str1 + "\u00a77:\u00a7c " + Potion.getDurationString(o1);
            } else if (o1.getDuration() > 600) {
                str1 = str1 + "\u00a77:\u00a77 " + Potion.getDurationString(o1);
            }
            if (o2.getDuration() < 600 && o2.getDuration() > 300) {
                str2 = str2 + "\u00a77:\u00a76 " + Potion.getDurationString(o2);
            } else if (o2.getDuration() < 300) {
                str2 = str2 + "\u00a77:\u00a7c " + Potion.getDurationString(o2);
            } else if (o2.getDuration() > 600) {
                str2 = str2 + "\u00a77:\u00a77 " + Potion.getDurationString(o2);
            }
            return fonts.getStringWidth(str1) - fonts.getStringWidth(str2);
        });
        Collections.reverse(myList);
        for (PotionEffect effect : myList) {
            int ychat;
            Potion potion = Potion.potionTypes[effect.getPotionID()];
            String PType = I18n.format(potion.getName());
            PType = PType + getAmplifier(effect.getAmplifier());
            if (effect.getDuration() < 600 && effect.getDuration() > 300) {
                PType = PType + "\u00a77:\u00a76 " + Potion.getDurationString(effect);
            } else if (effect.getDuration() < 300) {
                PType = PType + "\u00a77:\u00a7c " + Potion.getDurationString(effect);
            } else if (effect.getDuration() > 600) {
                PType = PType + "\u00a77:\u00a77 " + Potion.getDurationString(effect);
            }
            ychat = mc.ingameGUI.getChatGUI().getChatOpen() ? 5 : -10;
            fonts.drawStringWithShadow(PType, ScaledResolution.getScaledWidth() - fonts.getStringWidth(PType) - 2, ScaledResolution.getScaledHeight() - fonts.FONT_HEIGHT + y - 10 - ychat, potion.getLiquidColor());
            y -= 10;
        }
        PotY = y;
    }

    @EventHandler
    public void onDisable() {
        PotY = 0;
    }

    public String getAmplifier(int count) {
        String Amplifier = "";
        switch (count) {
            case 0: {
                Amplifier = " I";
                break;
            }
            case 1: {
                Amplifier = " II";
                break;
            }
            case 2: {
                Amplifier = " III";
                break;
            }
            case 3: {
                Amplifier = " IV";
                break;
            }
            case 4: {
                Amplifier = " V";
                break;
            }
            case 5: {
                Amplifier = " VI";
                break;
            }
            case 6: {
                Amplifier = " VII";
                break;
            }
            case 7: {
                Amplifier = " VIII";
                break;
            }
            case 8: {
                Amplifier = " IX";
                break;
            }
            case 9: {
                Amplifier = " X";
                break;
            }
        }
        if (count > 9 || count == -1) {
            Amplifier = " X+";
        }
        return Amplifier;
    }

    public enum logomodeE {
        FoodTower, Novoline, Jigsaw, Distance, Dark_Distance

    }

    public enum ArrayFont {
        SF18, SF20, GoogleSans16, GoogleSans18, Jello18, Jello16, Baloo16, Baloo18, Minecraft
    }

    public enum ArrayModeE {
        Wave, Sky, NewRainbow, Rainbow, Rainbow2, NEON, BlueIceSakura, None
    }

    public enum RectModes {
        None, Full, Right, Left, Bottom
    }

    public enum widgetE {
        None(0, 0, 0), Astolfo(0, 0, 0), Widget_1(1, 505, 512), Widget_2(2, 494, 512), Widget_3(3, 489, 512), Widget_4(4, 464, 512), Widget_5(5, 512, 493), Widget_6(6, 505, 512), Widget_7(7, 428, 512), Widget_8(8, 460, 512), Widget_9(9, 512, 472), Widget_10(10, 486, 512), Widget_11(11, 489, 512), Widget_12(12, 446, 512), Widget_13(13, 512, 480), Widget_14(14, 493, 512), Widget_15(15, 512, 489), Widget_16(16, 512, 518), Widget_17(17, 512, 485), Widget_18(18, 512, 500), Widget_19(19, 512, 485), Widget_20(20, 512, 482), Widget_21(21, 512, 509);

        final int width;
        final int height;
        final int id;

        widgetE(int id, int width, int height) {
            this.id = id;
            this.width = width;
            this.height = height;
        }
    }

}

