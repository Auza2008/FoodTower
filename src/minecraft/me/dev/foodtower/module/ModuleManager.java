/*
Author:SuMuGod
Date:2022/7/10 3:45
Project:foodtower Reborn
*/
package me.dev.foodtower.module;

import me.dev.foodtower.api.EventBus;
import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.api.events.EventKey;
import me.dev.foodtower.api.events.EventRender2D;
import me.dev.foodtower.api.events.EventRender3D;
import me.dev.foodtower.module.modules.combat.*;
import me.dev.foodtower.module.modules.ghost.*;
import me.dev.foodtower.module.modules.movement.*;
import me.dev.foodtower.module.modules.player.*;
import me.dev.foodtower.module.modules.render.*;
import me.dev.foodtower.module.modules.world.*;
import me.dev.foodtower.utils.client.Manager;
import me.dev.foodtower.utils.math.gl.GLUtils;
import me.dev.foodtower.value.Value;
import net.minecraft.client.renderer.GlStateManager;

import java.lang.reflect.Field;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModuleManager
        implements Manager {
    public static List<Module> modules = new ArrayList<Module>();
    public boolean nicetry = true;
    private boolean enabledNeededMod = true;

    public static List<Module> getModules() {
        return modules;
    }

    public static Module getModuleByName(String name) {
        for (Module m : modules) {
            if (!m.name.equalsIgnoreCase(name)) continue;
            return m;
        }
        return null;
    }

    @Override
    public void init() {
        registerModule(new AutoGapple());
        registerModule(new NoClickDelay());
        registerModule(new LegitSpeed());
        registerModule(new HUD());
        registerModule(new Sprint());
        registerModule(new Killaura());
        registerModule(new Velocity());
        registerModule(new BlockOverlay());
        registerModule(new Criticals());
        registerModule(new Speed());
        registerModule(new Longjump());
        registerModule(new Teams());
        registerModule(new Flight());
        registerModule(new NoFall());
        registerModule(new EveryThingBlock());
        registerModule(new NoSlow());
        registerModule(new TargetStrafe());
        registerModule(new EnchantEffect());
        registerModule(new AutoPot());
        registerModule(new FastBow());
        registerModule(new AntiBot());
        registerModule(new Animations());
        registerModule(new Freecam());
        registerModule(new BedNuker());
        registerModule(new MCF());
        registerModule(new Timer());
        registerModule(new Nametags());
        registerModule(new Tracers());
        registerModule(new ESP());
        registerModule(new Regen());
        registerModule(new AutoTool());
        registerModule(new Damage());
        registerModule(new FastPlace());
        registerModule(new Reach());
        registerModule(new AimAssist());
        registerModule(new NoRender());
        registerModule(new FullBright());
        registerModule(new AutoClicker());
        registerModule(new ChestStealer());
        registerModule(new AutoArmor());
        registerModule(new AntiVoid());
        registerModule(new InventoryHUD());
        registerModule(new NoRotate());
        registerModule(new Scaffold());
        registerModule(new Eagle());
        registerModule(new Disabler());
        registerModule(new SafeWalk());
        registerModule(new Zoot());
        registerModule(new Jesus());
        registerModule(new Phase());
        registerModule(new Deathclip());
        registerModule(new NoStrike());
        registerModule(new ItemPhysic());
        registerModule(new SkinFlash());
        registerModule(new Hitbox());
        registerModule(new AutoAccept());
        registerModule(new ClickGui());
        registerModule(new Blink());
        registerModule(new AirWalk());
        registerModule(new ThrowL());
        registerModule(new FastUse());
        registerModule(new PingSpoof());
        registerModule(new BowAimBot());
        registerModule(new Xray());
        registerModule(new ChestESP());
        registerModule(new InvCleaner());
        registerModule(new Step());
        registerModule(new LightningCheck());
        registerModule(new Teleport());
        registerModule(new AutoSword());
        registerModule(new Boost());
        registerModule(new Bobbing());
        registerModule(new PinCracker());
        registerModule(new TPAura());
        registerModule(new KeyBindDisplay());
        registerModule(new InvMove());
        registerModule(new Radar());
        registerModule(new TargetHUD());
        registerModule(new AutoReport());
        registerModule(new AutoPlay());
        for (Module m : modules) {
            m.makeCommand();
        }
        Collections.sort(modules, (o1, o2) -> {
            int flag = o1.getName().compareTo(o2.getName());
            return flag;
        });
        EventBus.getInstance().register(this);
    }

    public void registerModule(Module module) {
        for (final Field field : module.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                final Object obj = field.get(module);
                if (obj instanceof Value) module.getValues().add((Value) obj);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        modules.add(module);
    }

    public Module getModuleByClass(Class<? extends Module> cls) {
        for (Module m : modules) {
            if (m.getClass() != cls) continue;
            return m;
        }
        return null;
    }

    public Module getAlias(String name) {
        for (Module f : modules) {
            if (f.getName().equalsIgnoreCase(name)) {
                return f;
            }
            String[] alias = f.getAlias();
            int length = alias.length;
            int i = 0;
            while (i < length) {
                String s = alias[i];
                if (s.equalsIgnoreCase(name)) {
                    return f;
                }
                ++i;
            }
        }
        return null;
    }

    public static List<Module> getModulesInType(ModuleType t) {
        ArrayList<Module> output = new ArrayList<Module>();
        for (Module m : modules) {
            if (m.getType() != t) continue;
            output.add(m);
        }
        return output;
    }

    @NMSL
    private void onKeyPress(EventKey e) {
        for (Module m : modules) {
            if (m.getKey() != e.getKey()) continue;
            m.setEnabled(!m.isEnabled());
        }
    }

    @NMSL
    private void onGLHack(EventRender3D e) {
        GlStateManager.getFloat(2982, (FloatBuffer) GLUtils.MODELVIEW.clear());
        GlStateManager.getFloat(2983, (FloatBuffer) GLUtils.PROJECTION.clear());
        GlStateManager.glGetInteger(2978, (IntBuffer) GLUtils.VIEWPORT.clear());
    }

    @NMSL
    private void on2DRender(EventRender2D e) {
        if (this.enabledNeededMod) {
            this.enabledNeededMod = false;
            for (Module m : modules) {
                if (!m.enabledOnStartup) continue;
                m.setEnabled(true);
            }
        }
    }
}

