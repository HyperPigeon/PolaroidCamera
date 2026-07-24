package net.hyper_pigeon.polaroidcamera.mixin;

import net.hyper_pigeon.polaroidcamera.duck.CameraHolder;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BipedEntityModel.class)
public abstract class BipedEntityModelMixin {

    @Shadow
    public ModelPart head;

    @Shadow
    public ModelPart rightArm;

    @Shadow
    public ModelPart leftArm;

    @Inject(at = @At("TAIL"), method = "positionLeftArm")
    private void cameraLeftArmPoses(BipedEntityRenderState state, CallbackInfo ci){
        if(((CameraHolder) state).isHoldingCamera()) {
            leftArm.roll = 0;
            leftArm.yaw = 0.16F + this.head.yaw + 0.4F;
            leftArm.pitch = -1.5707964F + this.head.pitch;
        }
    }

    @Inject(at = @At("TAIL"), method = "positionRightArm")
    private void cameraRightArmPoses(BipedEntityRenderState state, CallbackInfo ci){
        if(((CameraHolder) state).isHoldingCamera()) {
            rightArm.roll = 0;
            rightArm.yaw =  -0.1F + this.head.yaw;;
            rightArm.pitch = -1.5707964F + this.head.pitch;
        }
    }


}
