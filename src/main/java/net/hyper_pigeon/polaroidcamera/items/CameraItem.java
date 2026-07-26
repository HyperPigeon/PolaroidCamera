package net.hyper_pigeon.polaroidcamera.items;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.hyper_pigeon.polaroidcamera.client.render.CameraScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class CameraItem extends Item {
    public CameraItem(Properties settings) {
        super(settings);
    }

    public InteractionResult use(Level world, Player user, InteractionHand hand) {


        if (world.isClientSide()){
            openCameraScreen(world,user,hand);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.SUCCESS;
    }

    @Environment(EnvType.CLIENT)
    private void openCameraScreen(Level world, Player user, InteractionHand hand) {
        Minecraft mc = Minecraft.getInstance();
        if (!(mc.gui.screen() instanceof CameraScreen)) {
            mc.gui.setScreen(new CameraScreen(mc.options.fov().get(), world));
        }
    }
}
