package net.hyper_pigeon.polaroidcamera.duck;

/**
 * Duck interface implemented on {@code LivingEntityRenderState} so that the
 * arm-posing mixin can tell whether the entity is holding the camera.
 */
public interface CameraHolder {
    boolean isHoldingCamera();

    void setHoldingCamera(boolean holding);
}
