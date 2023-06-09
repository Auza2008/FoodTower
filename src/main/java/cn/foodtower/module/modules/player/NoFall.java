package cn.foodtower.module.modules.player;

import cn.foodtower.api.EventHandler;
import cn.foodtower.api.events.World.EventPacketSend;
import cn.foodtower.api.events.World.EventPreUpdate;
import cn.foodtower.api.value.Mode;
import cn.foodtower.manager.ModuleManager;
import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;
import cn.foodtower.module.modules.player.Nofalls.NofallModule;
import cn.foodtower.module.modules.player.Nofalls.impl.AAC5NoFall;
import cn.foodtower.module.modules.player.Nofalls.impl.HypixelNoFall;
import cn.foodtower.module.modules.player.Nofalls.impl.SpoofGroundNoFall;
import cn.foodtower.module.modules.player.Nofalls.impl.VulcanNoFall;
import cn.foodtower.module.modules.combat.Criticals;
import cn.foodtower.module.modules.render.Freecam;

public class NoFall extends Module {
	public Mode mode = new Mode("Mode", "Mode", NoFallMode.values(), NoFallMode.SpoofGround);


	public NoFall() {
		super("NoFall", new String[] { "Nofalldamage" }, ModuleType.Player);
		super.addValues(mode);
	}

	@Override
	public void onEnable(){
		((NoFallMode)mode.get()).get().onEnable();
	}

	@EventHandler
	private void onUpdate(EventPreUpdate e) {
		super.setSuffix(mode.get());
		if (mc.thePlayer.capabilities.isFlying || mc.thePlayer.capabilities.disableDamage
				|| mc.thePlayer.motionY >= 0.0d)
			return;
		if ( Criticals.mode.get().equals(Criticals.CritMode.NoGround) && ModuleManager.getModuleByClass(Criticals.class).isEnabled()) {
			return;
		}
		if(ModuleManager.getModByClass( Freecam.class ).isEnabled()){return;}
		((NoFallMode)mode.get()).get().onUpdate(e);

	}

	@EventHandler
	public void onPacket(EventPacketSend e) {
		((NoFallMode)mode.get()).get().onPacketSend(e);
	}

	public enum NoFallMode {
		SpoofGround(new SpoofGroundNoFall()),
		AAC5(new AAC5NoFall()),
		Vulcan(new VulcanNoFall()),
		Hypixel(new HypixelNoFall());
		final NofallModule nofallModule;
		NoFallMode(NofallModule nofallModuleIn){
			nofallModule = nofallModuleIn;
		}

		public NofallModule get() {
			return nofallModule;
		}
	}
}
