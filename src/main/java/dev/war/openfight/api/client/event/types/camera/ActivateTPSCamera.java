package dev.war.openfight.api.client.event.types.camera;

import dev.war.openfight.api.client.camera.EpicFightCameraAPI;
import dev.war.openfight.api.event.CancelableEvent;

public class ActivateTPSCamera extends CameraAPIEvent implements CancelableEvent {
    public ActivateTPSCamera(EpicFightCameraAPI cameraApi) {
        super(cameraApi);
    }
}
