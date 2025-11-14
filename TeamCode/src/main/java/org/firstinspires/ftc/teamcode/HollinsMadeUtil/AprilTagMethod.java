package org.firstinspires.ftc.teamcode.HollinsMadeUtil;

import static org.firstinspires.ftc.teamcode.aProccedural.Components.tagHelper;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

public class AprilTagMethod {

    private AprilTagDetection lastDetectedTag = null;

    /**
     * Call this method every loop in your OpMode to get the latest AprilTag detection
     * and display its information via telemetry.
     *
     * @param telemetry The telemetry object from your OpMode (e.g., this.telemetry).
     */
    public void updateAndShowTelemetry(Telemetry telemetry) {
        // Get the first detected tag from your helper
        AprilTagDetection currentTag = tagHelper.getFirstTag();

        if (currentTag != null && currentTag.ftcPose != null) {
            // If a tag is found, update our stored tag
            lastDetectedTag = currentTag;

            // Display telemetry for the currently detected tag
            telemetry.addLine("--- AprilTag Detected! ---");
            telemetry.addData("Tag ID", currentTag.id);
            // Display name if available
            telemetry.addData("Tag Name", currentTag.metadata != null ? currentTag.metadata.name : "N/A");
            telemetry.addData("X (in)", "%.2f", currentTag.ftcPose.x);
            telemetry.addData("Y (in)", "%.2f", currentTag.ftcPose.y);
            telemetry.addData("Z (in)", "%.2f", currentTag.ftcPose.z);
            telemetry.addData("Yaw (deg)", "%.2f", currentTag.ftcPose.yaw);
            telemetry.addData("Pitch (deg)", "%.2f", currentTag.ftcPose.pitch);
            telemetry.addData("Roll (deg)", "%.2f", currentTag.ftcPose.roll);
        } else {
            // If no tag is currently visible
            telemetry.addLine("--- No AprilTag Detected ---");
            if (lastDetectedTag != null) {
                // Optionally, you can show the data of the last seen tag
                telemetry.addLine("(Showing last known tag)");
                telemetry.addData("Last Seen Tag ID", lastDetectedTag.id);
            }
        }
    }

    /**
     * Returns the most recently detected AprilTag.
     * This can be a tag that is currently in view or the last one seen.
     *
     * @return The last detected AprilTagDetection object, or null if none have been seen.
     */
    public AprilTagDetection getLatestDetection() {
        // Update the detection from the helper
        lastDetectedTag = tagHelper.getFirstTag();
        return lastDetectedTag;
    }

    /**
     * Checks if a tag is currently visible by the camera.
     *
     * @return true if a tag is detected, false otherwise.
     */
    public boolean isTagVisible() {
        return tagHelper.getFirstTag() != null;
    }
}
