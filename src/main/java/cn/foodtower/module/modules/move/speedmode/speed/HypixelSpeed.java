package cn.foodtower.module.modules.move.speedmode.speed;

import cn.foodtower.api.events.World.*;
import cn.foodtower.module.modules.move.Speed;
import cn.foodtower.module.modules.move.speedmode.SpeedModule;
import cn.foodtower.util.entity.MoveUtils;
import cn.foodtower.util.time.MSTimer;

import static cn.foodtower.util.entity.PlayerUtil.setSpeed;

public class HypixelSpeed extends SpeedModule {
    private boolean stage = false;
    private final MSTimer timer = new MSTimer();
    public boolean shoulddamageboost=false;
    public double dmgspeed;

    @Override
    public void onMotion( EventMotionUpdate e) {
        if (mc.thePlayer.isInWater() || mc.thePlayer.isInLava() || mc.thePlayer.isInWeb()) return;
        if(mc.thePlayer.isMoving()){
            if (mc.thePlayer.onGround) {

//              Speed

//              MovementUtils.strafe(0.32f);

//              Jump
                mc.thePlayer.jump();

                if(!Speed.fullstrafe.get()) {
                    setSpeed(Speed.basespeed.get().floatValue() + MoveUtils.getSpeedEffect() * 0.1);
                }

            }

            if(shoulddamageboost&&!timer.hasTimePassed( Speed.dmgboostmstimer.get().longValue())&&!mc.thePlayer.isBurning()){
                setSpeed(Math.max(Speed.dmspeed.getValue().doubleValue() + (double)MoveUtils.getSpeedEffect() * 0.1, this.getBaseMoveSpeed() ));
                mc.timer.timerSpeed = 1.0f;
            }else {
                if (Speed.fullstrafe.get()) {
                    setSpeed(Speed.basespeed.get().floatValue() + MoveUtils.getSpeedEffect() * 0.1);
                }
                shoulddamageboost = false;
            }
        }
    }
    public double getBaseMoveSpeed() {
        return this.mc.thePlayer.isSprinting() ? 0.2873 : (double)0.223f;
    }


    @Override
    public void onPacketSend( EventPacketSend e) {
    }

    @Override
    public void onStep( EventStep e) {
    }

    @Override
    public void onPre(EventPreUpdate e) {
        if(mc.thePlayer.hurtTime>3){
            shoulddamageboost=true;
            timer.reset();
        }
    }



    double y;
    @Override
    public void onMove( EventMove event) {
    }

    @Override
    public void onPost( EventPostUpdate e) {
    }

    @Override
    public void onEnabled() {
        timer.reset();
    }



    @Override
    public void onDisabled() {
        timer.reset();
        mc.timer.timerSpeed = 1F;
    }

    @Override
    public void onPacket( EventPacket e ) {



    }
}
