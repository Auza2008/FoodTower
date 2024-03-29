package cn.foodtower.module.modules.render;


import cn.foodtower.api.EventHandler;
import cn.foodtower.api.events.Render.EventRender3D;
import cn.foodtower.api.value.Mode;
import cn.foodtower.api.value.Option;
import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;
import cn.foodtower.util.render.RenderUtil;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.opengl.GL11;

public class ItemEsp extends Module {
    public static Option outlinedboundingBox = new Option("OutlinedBoundingBox", "OutlinedBoundingBox", false);
    public static Option boundingBox = new Option("BoundingBox", "BoundingBox", true);
    public static Mode heigh = new Mode("Height", "Height", height.values(), height.High);

    public ItemEsp() {
        super("ItemESP", new String[]{"ItemESP"}, ModuleType.Render);
        this.addValues(outlinedboundingBox, boundingBox, heigh);
    }

    @EventHandler
    public void onRender(EventRender3D event) {
        for (Object o : mc.theWorld.loadedEntityList) {
            if (!(o instanceof EntityItem)) continue;
            EntityItem item = (EntityItem) o;
            double var10000 = item.posX;
            double x = var10000 - RenderManager.renderPosX;
            var10000 = item.posY + 0.5D;
            double y = var10000 - RenderManager.renderPosY;
            var10000 = item.posZ;
            double z = var10000 - RenderManager.renderPosZ;
            GL11.glEnable(3042);
            GL11.glLineWidth(2.0F);
            GL11.glColor4f(1, 1, 1, .75F);
            GL11.glDisable(3553);
            GL11.glDisable(2929);
            GL11.glDepthMask(false);
            if (outlinedboundingBox.get() && heigh.get() == height.High) {
                RenderUtil.drawOutlinedBoundingBox(new AxisAlignedBB(x - .2D, y - 0.05, z - .2D, x + .2D, y - 0.45d, z + .2D));
            }
            if (outlinedboundingBox.get() && heigh.get() == height.Low) {
                RenderUtil.drawOutlinedBoundingBox(new AxisAlignedBB(x - .2D, y - 0.3d, z - .2D, x + .2D, y - 0.4d, z + .2D));
            }
            GL11.glColor4f(1, 1, 1, 0.15f);
            if (boundingBox.get() && heigh.get() == height.High) {
                RenderUtil.drawBoundingBox(new AxisAlignedBB(x - .2D, y - 0.05, z - .2D, x + .2D, y - 0.45d, z + .2D));
            }
            GL11.glColor4f(1, 1, 1, 0.15f);
            if (boundingBox.get() && heigh.get() == height.Low) {
                RenderUtil.drawBoundingBox(new AxisAlignedBB(x - .2D, y - 0.3d, z - .2D, x + .2D, y - 0.4d, z + .2D));
            }
            GL11.glEnable(3553);
            GL11.glEnable(2929);
            GL11.glDepthMask(true);
            GL11.glDisable(3042);
        }
    }

    enum height {
        High,
        Low
    }
}
