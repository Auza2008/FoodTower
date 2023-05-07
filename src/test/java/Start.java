import java.io.File;
import java.util.Arrays;

import net.minecraft.client.main.Main;

public class Start {
    public static void main(String[] args) {
        System.setProperty("org.lwjgl.librarypath", new File("../natives/").getAbsolutePath());
        Main.main(concat(new String[]{"--version", "GradleMCP", "--accessToken", "0", "--assetsDir", "assets", "--assetIndex", "1.8", "--userProperties", "{}"}, args));
    }

    public static <T> T[] concat(T[] first, T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
}
