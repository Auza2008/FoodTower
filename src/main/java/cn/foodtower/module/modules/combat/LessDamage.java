package cn.foodtower.module.modules.combat;

import cn.foodtower.api.EventHandler;
import cn.foodtower.api.events.World.EventPreUpdate;
import cn.foodtower.api.value.Numbers;
import cn.foodtower.manager.ModuleManager;
import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;
import cn.foodtower.module.modules.player.Blink;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class LessDamage extends Module {
    private final Numbers<Double> range = new Numbers<>("Range", 15d, 10d, 30d, 1d);
    private final List<EntityLivingBase> targets = new ArrayList<>();
    private EntityLivingBase curTarget;

    public LessDamage() {
        super("LessDamage", new String[]{"nodmg"}, ModuleType.Combat);
        addValues(range);
    }

    @Override
    public void onEnable() {
        targets.clear();
    }

    public final EntityLivingBase getTarget() {
        if (this.targets.isEmpty()) {
            return null;
        }
        return this.targets.get(0);
    }

    private void updateTargets() {
        this.targets.clear();
        List<Entity> entities = mc.theWorld.loadedEntityList;
        for (Entity o : entities) {
            EntityLivingBase entityLivingBase;
            if (!(o instanceof EntityLivingBase) || o.isDead || ((EntityLivingBase) o).getHealth() <= 0 || !this.checkTargets(entityLivingBase = (EntityLivingBase) o))
                continue;
            this.targets.add(entityLivingBase);
        }
    }

    private boolean checkTargets(Entity entity) {
        if (entity instanceof EntityPlayerSP) {
            return false;
        }
        if (ModuleManager.getModuleByClass(Blink.class).isEnabled() && entity.getName().equals(Minecraft.getMinecraft().thePlayer.getName())) {
            return false;
        }
        if (ModuleManager.getModuleByClass(AntiBot.class).isEnabled() && AntiBot.isServerBot(entity)) {
            return false;
        }
        if (ModuleManager.getModuleByClass(HypixelAntibot.class).isEnabled() && HypixelAntibot.isServerBot(entity)) {
            return false;
        }
        return (double) Minecraft.getMinecraft().thePlayer.getDistanceToEntity(entity) <= this.range.get();
    }


    @EventHandler
    private void onUpdate(EventPreUpdate e) {
        setSuffix(range.get());
        if (!ModuleManager.getModuleByClass(KillAura.class).isEnabled()) {
//            sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            return;
        }
        updateTargets();
        curTarget = getTarget();
        if (curTarget != null && KillAura.curTarget == null) {
            sendPacket(new C08PacketPlayerBlockPlacement(BlockPos.ORIGIN, 255, null, 0.0f, 0.0f, 0.0f));
        }
    }
}
