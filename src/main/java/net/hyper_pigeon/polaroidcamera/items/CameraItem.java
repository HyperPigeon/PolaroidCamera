package net.hyper_pigeon.polaroidcamera.items;

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

    private void openCameraScreen(World world, PlayerEntity user, Hand hand) {
        MinecraftClient mc = MinecraftClient.getInstance();

        if (!(mc.currentScreen instanceof CameraScreen)) {
            mc.openScreen(new CameraScreen(mc.options.fov, world));
        }
    }
}
