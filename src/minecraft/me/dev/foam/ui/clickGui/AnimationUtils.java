/*
Author:SuMuGod
Date:2022/7/10 5:34
Project:foam Reborn
*/
package me.dev.foam.ui.clickGui;

public class AnimationUtils {
    public static float rotateDirection = 0;
    public static boolean animationDone = true;


    //Minecraft.java
    public static double delta;

    public static float getAnimationState(float animation, float finalState, float speed) {
        final float add = (float) (delta * (speed / 1000f));
        if (animation < finalState) {
            if (animation + add < finalState) {
                animation += add;
            } else {
                animation = finalState;
            }
        } else if (animation - add > finalState) {
            animation -= add;
        } else {
            animation = finalState;
        }
        return animation;
    }

    public static float smoothAnimation(float ani, float finalState, float speed, float scale) {
        return getAnimationState(ani, finalState, (float) (Math.max(10, (Math.abs(ani - finalState)) * speed) * scale));
    }

    public static float getRotateDirection() {// AllitemRotate->Rotate
        rotateDirection = rotateDirection + (float) delta;
        if (rotateDirection > 360)
            rotateDirection = 0;
        return rotateDirection;
    }
}
