package dev.war.openfight.api.client.event.types.camera;

import dev.war.openfight.api.client.camera.EpicFightCameraAPI;
import dev.war.openfight.api.event.Event;

/// All events fired in [EpicFightCameraAPI] inherit this class
public abstract class CameraAPIEvent extends Event {
    private final EpicFightCameraAPI cameraApi;

    public CameraAPIEvent(EpicFightCameraAPI cameraApi) {
        this.cameraApi = cameraApi;
    }

    /// Developers can use [EpicFightCameraAPI#getInstance]. However, we still
    /// recommend to use this method to get CameraAPI for the maintainability
    public final EpicFightCameraAPI getCameraApi() {
        return this.cameraApi;
    }
}
