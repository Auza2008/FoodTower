package cn.foodtower.api.value;

public abstract class Value<V> {
    private final String displayName;
    private final String name;
    public Mode modes;
    public Option options;
    public Enum<?>[] targetModes;
    public Boolean targetModesB;
    private V value;

    public Value(String displayName, String name) {
        this.displayName = displayName;
        this.name = name;
    }


    public String getDisplayName() {
        return this.displayName;
    }

    public String getName() {
        return this.name;
    }

//    public V getValue() {
//        return this.value;
//    }

    public void setValue(V value) {
        this.value = value;
    }

    public V get() {
        return this.value;
    }

    public boolean isDisplayable() {
        if (targetModes != null) {
            for (Enum<?> targetMode : targetModes) {
                if (targetMode.equals(modes.get())) {
                    return true;
                }
            }
            return false;
        }
        if (targetModesB != null) {
            return targetModesB.equals(options.get());
        }
        return true;
    }
}
