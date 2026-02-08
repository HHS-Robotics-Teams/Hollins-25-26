package org.firstinspires.ftc.teamcode._Util;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;

import java.util.List;

public class AprilTagHelper {

    public VisionPortal visionPortal;
    public AprilTagProcessor aprilTag;

    public AprilTagHelper(HardwareMap hardwareMap, String cameraName) {
        // Create AprilTag processor
        aprilTag = new AprilTagProcessor.Builder().build();

        // Create VisionPortal with the given webcam
        visionPortal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, cameraName))
                .addProcessor(aprilTag)
                .build();
    }

    /** Get the latest detections */
    public List<AprilTagDetection> getDetections() {
        // It's good practice to check if aprilTag itself is null,
        // though in this specific class structure, it's initialized in the constructor.
        // However, if the VisionPortal is stopped/closed, getDetections() might behave unexpectedly
        // or throw an error if the processor is no longer active.
        // The AprilTagProcessor itself should handle returning an empty list if no detections.
        if (visionPortal == null || visionPortal.getCameraState() != VisionPortal.CameraState.STREAMING) {
            // Or if aprilTag is somehow no longer valid after portal closure.
            // Consider returning an empty list or logging a_cross warning if the portal is not active.
            // For now, relying on aprilTag.getDetections() to handle it.
            // telemetry.addLine("Warning: VisionPortal not streaming. Detections might be stale or empty.");
        }
        return aprilTag.getDetections();
    }
    /** Get the first detected tag (null if none) */
    public AprilTagDetection getFirstTag() {
        List<AprilTagDetection> detections = getDetections(); // Uses the method above
        if (detections != null && !detections.isEmpty()) { // Added null check for detections list itself
            return detections.get(0);
        }
        return null;
    }

    /** Stop the vision portal (call when done) */
    public void stop() {
        if (visionPortal != null) {
            visionPortal.close();
            visionPortal = null;
        }
    }
}
