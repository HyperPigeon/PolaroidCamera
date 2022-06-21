package net.hyper_pigeon.polaroidcamera.items;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.hyper_pigeon.polaroidcamera.client.render.CameraScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class CameraItem extends Item {
    public CameraItem(Settings settings) {
        super(settings);
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {


        if (world.isClient()){
            openCameraScreen(world,user,hand);
            return TypedActionResult.success(user.getStackInHand(hand));
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }

    @Environment(EnvType.CLIENT)
    private void openCameraScreen(World world, PlayerEntity user, Hand hand) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (!(mc.currentScreen instanceof CameraScreen)) {
            mc.options.hudHidden = true;
            mc.setScreen(new CameraScreen(mc.options.getFov().getValue(), world));
        }
        mc.options.hudHidden = true;
    }
}
