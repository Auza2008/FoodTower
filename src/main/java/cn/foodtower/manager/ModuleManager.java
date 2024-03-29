package cn.foodtower.manager;

import cn.foodtower.api.EventBus;
import cn.foodtower.api.EventHandler;
import cn.foodtower.api.events.Misc.EventKey;
import cn.foodtower.api.events.Render.EventRender2D;
import cn.foodtower.api.events.Render.EventRender3D;
import cn.foodtower.api.value.Mode;
import cn.foodtower.api.value.Numbers;
import cn.foodtower.api.value.Option;
import cn.foodtower.api.value.Value;
import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;
import cn.foodtower.module.modules.combat.*;
import cn.foodtower.module.modules.move.*;
import cn.foodtower.module.modules.player.*;
import cn.foodtower.module.modules.render.*;
import cn.foodtower.module.modules.world.*;
import cn.foodtower.util.ClientSetting;
import cn.foodtower.util.render.gl.GLUtils;
import me.guichaguri.betterfps.BetterFpsConfig;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class ModuleManager implements Manager {
    public static List<Module> modules = new ArrayList<>();
    public static boolean enabledNeededMod = true;
    public static boolean loaded = false;

    public static List<Module> getModules() {
        return modules;
    }

    public static Module getModuleByClass(Class<? extends Module> cls) {
        for (Module m : modules) {
            if (m.getClass() != cls) continue;
            return m;
        }
        Runtime.getRuntime().exit(-11);
        return new Module("nul", new String[]{"n"}, ModuleType.Combat);
    }

    public static Module getModByClass(Class<? extends Module> cls) {
        for (Module m : modules) {
            if (m.getClass() != cls) continue;
            return m;
        }
        Runtime.getRuntime().exit(-11);
        return new Module("nul", new String[]{"n"}, ModuleType.Combat);
    }

    public static Module getModuleByName(String name) {
        for (Module m : modules) {
            if (!m.getName().equalsIgnoreCase(name)) continue;
            return m;
        }
        return null;
    }

    public static List<Module> getModulesInType(ModuleType t) {
        ArrayList<Module> output = new ArrayList<>();
        for (Module m : modules) {
            if (m.getType() != t) continue;
            output.add(m);
        }
        return output;
    }

    @Override
    public void init() {
        modules.add(new LegitSpeed());
        modules.add(new ComboOneHit());
        modules.add(new AutoEat());
        modules.add(new PacketFixer());
        modules.add(new HypixelAntibot());
        modules.add(new TargetStrafe());
        modules.add(new AimAssist());
        modules.add(new AutoSoup());
        modules.add(new BowAimbot());
        modules.add(new KillAura());
        modules.add(new AutoSword());
        modules.add(new Velocity());
        modules.add(new AntiBot());
        modules.add(new AutoHead());
        modules.add(new AutoPot());
        modules.add(new Criticals());
        modules.add(new TPAura());
        modules.add(new DuelInfo());
        modules.add(new SmartDCJ());
        modules.add(new LessDamage());
        modules.add(new Reach());
        modules.add(new Regen());
        modules.add(new SuperKnockback());
        modules.add(new Animations());
        modules.add(new Breadcrumbs());
        modules.add(new HUD());
        modules.add(new Chinahat());
        modules.add(new ItemPhysic());
        modules.add(new SetScoreboard());
        modules.add(new PacketMotior());
        modules.add(new ItemEsp());
        modules.add(new OreESP());
        modules.add(new DMGParticle());
        modules.add(new EnchantEffect());
        modules.add(new MotionBlur());
        modules.add(new TabGui());
        modules.add(new TargetHUD());
        modules.add(new NameTags());
        modules.add(new ClickGui());
        modules.add(new Tracers());
        modules.add(new NoHurtCam());
        modules.add(new ViewClip());
        modules.add(new ESP());
        modules.add(new Projectiles());
        modules.add(new FullBright());
        modules.add(new Chams());
        modules.add(new Xray());
        modules.add(new ChestESP());
        modules.add(new MiniMap());
        modules.add(new NameProtect());
        modules.add(new HungerOverlay());
        modules.add(new BowJump());
        modules.add(new AirLadder());
        modules.add(new AntiFall());
        modules.add(new AutoMLG());
        modules.add(new CustomSpeed());
        modules.add(new Sprint());
        modules.add(new Speed());
        modules.add(new Step());
        modules.add(new NoSlow());
        modules.add(new InvMove());
        modules.add(new Freecam());
        modules.add(new Fly());
        modules.add(new Jesus());
        modules.add(new NoWeb());
        modules.add(new AutoArmor());
        modules.add(new AutoL());
        modules.add(new BedNuker());
        modules.add(new ClientSetting());
        modules.add(new Disabler());
        modules.add(new PingSpoof());
//		modules.add(new IRC());
        modules.add(new SpeedMine());
        modules.add(new AntiAim());
        modules.add(new AutoMine());
        modules.add(new AutoReconnect());
        modules.add(new AntiObsidian());
        modules.add(new AutoTool());
        modules.add(new AutoGG());
        modules.add(new Keyrender());
        modules.add(new Wings());
        modules.add(new MCF());
        modules.add(new FastPlace());
        modules.add(new Timer());
        modules.add(new WorldTime());
        modules.add(new ChestStealer());
        modules.add(new NoCommand());
        modules.add(new Teams());
        modules.add(new MemoryFix());
        modules.add(new Scaffold());
        modules.add(new Crosshair());
        modules.add(new LightningCheck());
        modules.add(new NoRotate());
        modules.add(new InvCleaner());
        modules.add(new AutoOnlySword());
//		modules.add(new IRC());
        modules.add(new MusicPlayer());
        modules.add(new ArrowDodge());
        modules.add(new AutoFish());
        modules.add(new AutoSay());
        modules.add(new AutoTP());
        modules.add(new Blink());
        modules.add(new Phase());
        modules.add(new NoFall());
        modules.add(new ChatBypass());
        modules.add(new ChatManager());
        modules.add(new AutoClicker());
        modules.add(new FastUse());
        modules.add(new Eagle());
        modules.add(new HitBox());
        modules.add(new NoJumpDelay());
        //modules.add(new KeepSprint());
        modules.add(new Strafe());
        modules.add(new NoClickDelay());
        modules.add(new Baned());
        modules.add(new KeepSprint());

        this.readSettings();
        for (Module m : modules) {
            m.makeCommand();
        }

        modules.sort((mod, mod1) -> {
            int char0 = mod.getName().charAt(0);
            int char1 = mod1.getName().charAt(0);
            return -Integer.compare(char1, char0);
        });

        BetterFpsConfig.getConfig().algorithm = ((ClientSetting.betterfps) ClientSetting.betterFPS.get()).get();
        EventBus.getInstance().register(this);
        loaded = true;
    }

    public Module getAlias(String name) {
        for (Module f : modules) {
            if (f.getName().equalsIgnoreCase(name)) {
                return f;
            }
            String alias = f.getName();
            if (alias.equalsIgnoreCase(name)) {
                return f;
            }
        }
        return null;
    }

    @EventHandler
    private void onKeyPress(EventKey e) {
        for (Module m : modules) {
            if (m.getKey() != e.getKey()) continue;
            m.setEnabled(!m.isEnabled());
        }
    }

    @EventHandler
    private void onGLHack(EventRender3D e) {
        GlStateManager.getFloat(2982, (FloatBuffer) GLUtils.MODELVIEW.clear());
        GlStateManager.getFloat(2983, (FloatBuffer) GLUtils.PROJECTION.clear());
        GlStateManager.glGetInteger(2978, (IntBuffer) GLUtils.VIEWPORT.clear());
    }

    @EventHandler
    private void on2DRender(EventRender2D e) {
        if (enabledNeededMod) {
            for (Module m : modules) {
                if (!m.enabledOnStartup) continue;
                m.setEnabled(true);
            }
            enabledNeededMod = false;
        }
    }

    private void readSettings() {

        List<String> binds = FileManager.read("Binds.txt");
        for (String v : binds) {
            String name = v.split(":")[0];
            String bind = v.split(":")[1];
            Module m = ModuleManager.getModuleByName(name);
            if (m == null) continue;
            m.setKey(Keyboard.getKeyIndex(bind.toUpperCase()));
        }
        if (getModuleByClass(ClickGui.class).getKey() == 0)
            getModuleByClass(ClickGui.class).setKey(Keyboard.getKeyIndex("RSHIFT"));

        List<String> enabled = FileManager.read("Enabled.txt");
        for (String v : enabled) {
            Module m = ModuleManager.getModuleByName(v);
            if (m == null) continue;
            m.enabledOnStartup = true;
        }

        List<String> vals = FileManager.read("Values.txt");
        for (String v : vals) {
            String name = v.split(":")[0];
            String values = v.split(":")[1];
            Module m = ModuleManager.getModuleByName(name);
            if (m == null) continue;

            for (Value value : m.getValues()) {
                if (!value.getName().equalsIgnoreCase(values)) continue;
                if (value instanceof Option) {
                    value.setValue(Boolean.parseBoolean(v.split(":")[2]));
                    continue;
                }
                if (value instanceof Numbers) {
                    value.setValue(Double.parseDouble(v.split(":")[2]));

                    continue;
                }
                ((Mode) value).setMode(v.split(":")[2]);
            }
        }

        List<String> names = FileManager.read("CustomName.txt");
        for (String v : names) {
            if (v.split(":").length > 1) {
                String name = v.split(":")[0];
                String cusname = v.split(":")[1];

                Module m = ModuleManager.getModuleByName(name);
                if (m == null) continue;
                m.setCustomName(cusname);
            }
        }
    }
}
