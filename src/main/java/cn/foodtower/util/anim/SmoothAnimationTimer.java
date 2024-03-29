package cn.foodtower.util.anim;


public class SmoothAnimationTimer {
    public float target;

    public float speed = 0.3f;
    private float value = 0;

    public SmoothAnimationTimer(float target) {
        this.target = target;
    }

    public SmoothAnimationTimer(float target, float speed) {
        this.target = target;
        this.speed = speed;
    }

    public void setTarget(float target) {
        this.target = target;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public boolean update(boolean increment) {
        this.value = AnimationUtil.getAnimationState(value, increment ? target : 0, Math.max(10, (Math.abs(this.value - (increment ? target : 0))) * 40) * speed);
        return value == target;
    }
}
