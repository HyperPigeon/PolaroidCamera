package net.hyper_pigeon.polaroidcamera.mixin;

import net.hyper_pigeon.polaroidcamera.duck.CameraHolder;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LivingEntityRenderState.class)
public class LivingEntityRenderStateMixin implements CameraHolder {

    @Unique
    private boolean holdingCamera;

    @Override
    public boolean isHoldingCamera() {
        return this.holdingCamera;
    }

    @Override
    public void setHoldingCamera(boolean holding) {
        this.holdingCamera = holding;
    }
}
