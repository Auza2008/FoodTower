package cn.foodtower.module.modules.world;

import cn.foodtower.Client;
import cn.foodtower.api.EventHandler;
import cn.foodtower.api.events.Misc.EventChat;
import cn.foodtower.api.events.Render.EventRender2D;
import cn.foodtower.api.value.Mode;
import cn.foodtower.api.value.Option;
import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;
import cn.foodtower.module.modules.combat.KillAura;
import cn.foodtower.util.math.RandomUtil;
import cn.foodtower.util.time.TimeHelper;
import cn.foodtower.util.world.SpammerUtils;

import java.awt.*;
import java.util.Random;

public class AutoL extends Module {
    public static final Option sendL = new Option("SendL", false);
    public static final Option ad = new Option("AD", true);
    public static final Option randomString = new Option("RandomString", true);
    public static final Option head = new Option("Prefix", true);
    static final String[] Hypixel = {"Do you know a little client...", "I am desperate, why am I banned...", "Why am I like this...",};
    private static final Mode mode = new Mode("Mode", LMode.values(), LMode.Hypixel);
    static int Totals = 0;
    static int i = 0;
    TimeHelper delay = new TimeHelper();

    public AutoL() {
        super("AutoL", new String[]{"L"}, ModuleType.World);
        this.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)).getRGB());
        this.addValues(mode, sendL, ad, randomString, head);
    }

    public static void sayL() {
        Random r = new Random();

        switch ((LMode) mode.get()) {
            case Hypixel: {
                sendL(" " + Hypixel[r.nextInt(Hypixel.length)]);
                break;
            }
            case PenShen: {
                sendL(SpammerUtils.intcihui[r.nextInt(SpammerUtils.intcihui.length)]);
                break;
            }
            case Killcount: {
                Totals++;
                sendL(", " + "你是我杀的第" + Totals + "个人了");
                break;
            }
            case CXK: {
                sendL(SpammerUtils.CXK[random.nextInt(SpammerUtils.CXK.length)]);
                break;
            }
            case TCC: {
                if (i > SpammerUtils.TCC.length - 1) i = 0;
                sendL(SpammerUtils.TCC[i] + getTail());
                i++;
                break;
            }
            case HHAF: {
                sendL(SpammerUtils.HHAF[random.nextInt(SpammerUtils.HHAF.length)]);
                break;
            }
            case Math: {
                if (i > SpammerUtils.Maths.length - 1) i = 0;
                sendL(SpammerUtils.Maths[i]);
                i++;
                break;
            }
            case Politics: {
                if (i > SpammerUtils.Politics.length - 1) i = 0;
                sendL(SpammerUtils.Politics[i]);
                i++;
                break;
            }
        }
    }

    public static void sendL(String string) {
        mc.thePlayer.sendChatMessage(getHead() + KillAura.curTarget.getName() + (sendL.get() ? " L " : " ") + string + getTail());
    }

    //TODO 等待网站建设
    private static String getTail() {
        return (ad.get() ? " | get FoodTower -> FoodTower dot icu " : "") + (randomString.get() ? RandomUtil.randomString(5 + new Random().nextInt(5)) : "");
    }

    private static String getHead() {
        return head.get() ? "[" + Client.name + "] " : "";
    }

    public static boolean isHypixelKilled(String message) {
        return message.toLowerCase().contains("was killed by " + mc.thePlayer.getName().toLowerCase() + ".") || message.toLowerCase().contains("was thrown into the void by " + mc.thePlayer.getName().toLowerCase() + ".") || message.toLowerCase().contains("was thrown off a cliff by " + mc.thePlayer.getName().toLowerCase() + ".");
    }

    @EventHandler
    public void onRender2D(EventRender2D e) {
        this.setSuffix(mode.get());
    }

    @EventHandler
    private void handleRequest(EventChat e) {
        if (e.getMessage() != null && e.getMessage().contains(mc.thePlayer.getName()) && e.getMessage().contains(KillAura.curTarget.getName())) {
            sayL();
        }
    }

    enum LMode {
        Hypixel, PenShen, Killcount, Math, CXK, Politics, TCC, HHAF,
    }
}

