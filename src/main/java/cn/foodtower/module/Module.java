package cn.foodtower.module;

import cn.foodtower.Client;
import cn.foodtower.api.EventBus;
import cn.foodtower.api.value.Mode;
import cn.foodtower.api.value.Numbers;
import cn.foodtower.api.value.Option;
import cn.foodtower.api.value.Value;
import cn.foodtower.command.Command;
import cn.foodtower.manager.FileManager;
import cn.foodtower.manager.ModuleManager;
import cn.foodtower.ui.ClientNotification;
import cn.foodtower.util.ClientSetting;
import cn.foodtower.util.anim.AnimationUtils;
import cn.foodtower.util.misc.Helper;
import cn.foodtower.util.sound.SoundFxPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;

import java.util.*;
import java.util.stream.Collectors;

public class Module {
    public static Minecraft mc = Minecraft.getMinecraft();
    public static Random random = new Random();
    private final String[] alias;
    public String name;
    public int color;
    public boolean enabledOnStartup = false;
    public float posY;
    public float lastY;
    public float posYRend;
    public float hoverOpacity;
    public float clickAnim = 0;
    public List<Value<?>> values;
    public ModuleType type;
    private String suffix;
    private boolean enabled;
    private boolean render;
    private int key;
    private boolean removed;
    private String cusname;
    private double curX;

    public Module(String name, String[] alias, ModuleType type) {
        this.name = name;
        this.alias = alias;
        this.type = type;
        this.suffix = "";
        this.key = 0;
        this.removed = false;
        this.enabled = false;
        this.cusname = null;
        this.render = false;
        this.values = new ArrayList<>();
        this.curX = -1;
    }

    public static void sendPacket(Packet<?> packet) {
        mc.getNetHandler().addToSendQueue(packet);
    }

    public String getName() {
        return this.name;
    }

    public String getCustomName() {
        return this.cusname;
    }

    public void setCustomName(String name) {
        this.cusname = name;
    }

    public String[] getAlias() {
        return this.alias;
    }

    public ModuleType getType() {
        return this.type;
    }

    public boolean getRender() {
        return this.render;
    }

    public void setRender(boolean b) {
        this.render = b;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (enabled) {
            this.onEnable();
            setRender(true);
            if (ModuleManager.loaded && !ModuleManager.enabledNeededMod && mc.theWorld != null && !Objects.equals(this.getName(), "ClickGui")) {
                if (ClientSetting.soundFx.getValue()) {
                    new SoundFxPlayer().playSound(SoundFxPlayer.SoundType.Enable, -11);
                } else {
                    mc.thePlayer.playSound("random.click", 0.1F, this.enabled ? 0.6F : 0.5F);
                }
                Helper.sendClientMessage(this.getName() + " \u00a78Enabled", ClientNotification.Type.success);
            }
            EventBus.getInstance().register(this);
        } else {
            if (ModuleManager.loaded && !ModuleManager.enabledNeededMod && mc.theWorld != null && !Objects.equals(this.getName(), "ClickGui")) {
                if (ClientSetting.soundFx.getValue()) {
                    new SoundFxPlayer().playSound(SoundFxPlayer.SoundType.Disable, -11);
                } else {
                    mc.thePlayer.playSound("random.click", 0.1F, this.enabled ? 0.6F : 0.5F);
                }
                Client.Pitch = 0;
                Helper.sendClientMessage(this.getName() + " \u00a78Disabled", ClientNotification.Type.error);
            }
            EventBus.getInstance().unregister(this);
            this.onDisable();
        }
    }

    public boolean wasRemoved() {
        return this.removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

    public String getSuffix() {
        return this.suffix;
    }

    public void setSuffix(Object obj) {
        String suffix = obj.toString();
        if (suffix.isEmpty()) {
            this.suffix = suffix;
        } else {
            this.suffix = String.format("%s", EnumChatFormatting.GRAY + suffix);
        }

    }

    public void setValueDisplayable(Value<?> value, Mode modes, Enum<?> targetMode) {
        value.targetModes = new Enum<?>[]{targetMode};
        value.modes = modes;
    }

    public void setValueDisplayable(Value<?> value, Mode modes, Enum<?>[] targetMode) {
        value.targetModes = targetMode;
        value.modes = modes;
    }

    public void setValueDisplayable(Value<?>[] values, Mode modes, Enum<?> targetMode) {
        for (Value<?> value : values) {
            value.targetModes = new Enum<?>[]{targetMode};
            value.modes = modes;
        }
    }

    public void setValueDisplayable(Value<?>[] values, Mode modes, Enum<?>[] targetMode) {
        for (Value<?> value : values) {
            value.targetModes = targetMode;
            value.modes = modes;
        }
    }


    public void setValueDisplayable(Value<?>[] values, Option modes, Enum<?>[] targetMode) {
        for (Value<?> value : values) {
            value.targetModes = targetMode;
            value.options = modes;
        }
    }

    public void setValueDisplayable(Value<?> value, Option modes, Boolean targetMode) {
        value.targetModesB = new Boolean[]{targetMode};
        value.options = modes;
    }

    public void setValueDisplayable(Value<?> value, Option modes, Boolean[] targetMode) {
        value.targetModesB = targetMode;
        value.options = modes;
    }

    public void setValueDisplayable(Value<?>[] values, Option modes, Boolean targetMode) {
        for (Value<?> value : values) {
            value.targetModesB = new Boolean[]{targetMode};
            value.options = modes;
        }
    }

    public void setValueDisplayable(Value<?>[] values, Option modes, Boolean[] targetMode) {
        for (Value<?> value : values) {
            value.targetModesB = targetMode;
            value.options = modes;
        }
    }


    public int getColor() {
        return this.color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    protected void addValues(Value<?>... values) {
        this.values.addAll(Arrays.asList(values));
    }

    protected void removeValues(Value<?>... values) {
        this.values.removeAll(Arrays.asList(values));
    }

    public List<Value<?>> getValues() {
        return this.values.stream().filter(Value::isDisplayable).collect(Collectors.toList());
    }

    public int getKey() {
        return this.key;
    }

    public void setKey(int key) {
        this.key = key;
        String content = "";

        Module m;
        for (Iterator<Module> var4 = ModuleManager.getModules().iterator(); var4.hasNext(); content = content + String.format("%s:%s%s", m.getName(), Keyboard.getKeyName(m.getKey()), System.lineSeparator())) {
            m = var4.next();
        }

        FileManager.save("Binds.txt", content, false);
    }

    public void onRenderArray() {
        if (lastY - posY > 0) posYRend = 14;
        if (lastY - posY < 0) posYRend = -14;
        if (posYRend != 0) posYRend = (float) AnimationUtils.animate(0, posYRend, 16.0f / Minecraft.getDebugFPS());
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

    public double getX() {
        return curX;
    }

    public void setX(double X) {
        curX = X;
    }

    public void makeCommand() {
        if (this.values.size() > 0) {
            StringBuilder options = new StringBuilder();
            StringBuilder other = new StringBuilder();

            for (Value v : values) {
                if (!(v instanceof Mode)) {
                    if (options.length() == 0) {
                        options.append(v.getName());
                    } else {
                        options.append(String.format(", %s", v.getName()));
                    }
                } else {
                    Mode mode = (Mode) v;
                    for (Enum<?> e : mode.getModes()) {
                        if (other.length() == 0) {
                            other.append(e.name().toLowerCase());
                        } else {
                            other.append(String.format(", %s", e.name().toLowerCase()));
                        }
                    }
                }
            }

            Client.instance.getCommandManager().add(new ModuleCommand(this, this.name, this.alias, String.format("%s%s", (options.length() == 0) ? "" : String.format("%s,", options), (other.length() == 0) ? "" : String.format("%s", other)), "设置此模块"));

        }
    }
}

class ModuleCommand extends Command {
    final Module this$0;
    private final Module m;

    ModuleCommand(Module var1, String $anonymous0, String[] $anonymous1, String $anonymous2, String $anonymous3) {
        super($anonymous0, $anonymous1, $anonymous2, $anonymous3);
        this.this$0 = var1;
        this.m = var1;
    }

    public String execute(String[] args) {
        Option option;
        if (args.length >= 2) {
            option = null;
            Numbers numbers = null;
            Mode mode = null;


            Value<?> v;
            for (Value<?> value : this.m.values) {
                v = value;
                if (v instanceof Option && v.getName().equalsIgnoreCase(args[0])) {
                    option = (Option) v;
                }
            }

            if (option != null) {
                option.setValue(!(Boolean) option.getValue());
                Helper.sendMessage(String.format("%s 的数值已被设置为 %s", option.getName(), option.getValue()));
            } else {

                for (Value<?> item : this.m.values) {
                    v = item;
                    if (v instanceof Numbers && v.getName().equalsIgnoreCase(args[0])) {
                        numbers = (Numbers) v;
                    }
                }

                if (numbers != null) {
                    try {
                        double v1 = Double.parseDouble(args[1]);
                        numbers.setValue(v1 > (Double) numbers.getMaximum() ? numbers.getMaximum() : v1);
                        Helper.sendMessage(String.format("> %s 的数值已被设置为 %s", numbers.getName(), numbers.getValue()));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        Helper.sendMessage("> " + args[1] + " 不是一个数字");
                    }
                }

                for (Value<?> value : this.m.values) {
                    if (args[0].equalsIgnoreCase(value.getDisplayName()) && value instanceof Mode) {
                        mode = (Mode) value;
                    }
                }

                if (mode != null) {
                    if (mode.isValid(args[1])) {
                        mode.setMode(args[1]);
                        Helper.sendMessage(String.format("> %s set to %s", mode.getName(), mode.getModeAsString()));
                    } else {
                        Helper.sendMessage("> " + args[1] + " 不是一个合法的模式");
                    }
                }
            }

            if (numbers == null && option == null && mode == null) {
                this.syntaxError("错误语法! 用法:.<模块名> <参数名> <参数(如果需要)>");
            }
        } else if (args.length >= 1) {
            option = null;

            for (Value fuck1 : this.m.values) {
                if (fuck1 instanceof Option && fuck1.getName().equalsIgnoreCase(args[0])) {
                    option = (Option) fuck1;
                }
            }

            if (option != null) {
                option.setValue(!(Boolean) option.getValue());
                String fuck2 = option.getName().substring(1);
                String xd2 = option.getName().substring(0, 1).toUpperCase();
                if (option.getValue()) {
                    Helper.sendMessage(String.format("> %s 的参数已被设置为了 \u00a7a%s", xd2 + fuck2, option.getValue()));
                } else {
                    Helper.sendMessage(String.format("> %s 的参数已被设置为了 \u00a7c%s", xd2 + fuck2, option.getValue()));
                }
            } else {
                this.syntaxError("错误语法! 用法:.<模块名> <参数名> <参数(如果需要)>");
            }
        } else {
            Helper.sendMessage(String.format("%s Values: \n %s", this.getName().substring(0, 1).toUpperCase() + this.getName().substring(1).toLowerCase(), this.getSyntax()));
        }

        return null;
    }
}
