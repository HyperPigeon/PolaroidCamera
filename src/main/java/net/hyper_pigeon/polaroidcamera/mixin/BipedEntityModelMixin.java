package net.hyper_pigeon.polaroidcamera.mixin;

import net.hyper_pigeon.polaroidcamera.duck.CameraHolder;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public abstract class BipedEntityModelMixin {

    @Shadow
    public ModelPart head;

    @Shadow
    public ModelPart rightArm;

    @Shadow
    public ModelPart leftArm;

    @Inject(at = @At("TAIL"), method = "poseLeftArm")
    private void cameraLeftArmPoses(HumanoidRenderState state, CallbackInfo ci){
        if(((CameraHolder) state).isHoldingCamera()) {
            leftArm.zRot = 0;
            leftArm.yRot = 0.16F + this.head.yRot + 0.4F;
            leftArm.xRot = -1.5707964F + this.head.xRot;
        }
    }

    @Inject(at = @At("TAIL"), method = "poseRightArm")
    private void cameraRightArmPoses(HumanoidRenderState state, CallbackInfo ci){
        if(((CameraHolder) state).isHoldingCamera()) {
            rightArm.zRot = 0;
            rightArm.yRot =  -0.1F + this.head.yRot;;
            rightArm.xRot = -1.5707964F + this.head.xRot;
        }
    }


}
