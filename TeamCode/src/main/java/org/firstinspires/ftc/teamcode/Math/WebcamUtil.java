package org.firstinspires.ftc.teamcode.Math;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;

import android.annotation.SuppressLint;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagGameDatabase;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.openftc.easyopencv.OpenCvWebcam;

import java.util.List;

public class WebcamUtil {

    static AprilTagProcessor aprilTagProcessor;
    static VisionPortal visionPortal;
    static String allianceSide;

    /**
     * @param color = "BLUE" for blue, "RED" for red
     *              Inits the webcam for apriltags
     */
    public static void initWebcamFinder(String color) {
        aprilTagProcessor = new AprilTagProcessor.Builder()
                .setTagLibrary(AprilTagGameDatabase.getCurrentGameTagLibrary())
                .setOutputUnits(DistanceUnit.METER, AngleUnit.DEGREES)
                .setDrawTagID(true)
                .setDrawAxes(true)
                .build();

        VisionPortal.Builder builder = new VisionPortal.Builder();

        builder.setCamera((CameraName) hardwareMap.get(OpenCvWebcam.class, "webcam1"));

        builder.addProcessor(aprilTagProcessor);
        builder.enableLiveView(true);
        visionPortal = builder.build();

        allianceSide = color;
    }

    /**
     * Outputs telemetry for apriltags
     *
     * @param telemetry telemetry
     */
    @SuppressLint("DefaultLocale")
    public static void aprilTagTelemetry(Telemetry telemetry) {

        List<AprilTagDetection> currentDetections = aprilTagProcessor.getDetections();
        telemetry.addData("# AprilTags Detected", currentDetections.size());

        // Step through the list of detections and display info for each one.
        for (AprilTagDetection detection : currentDetections) {
            if (detection.metadata != null) {
                telemetry.addLine(String.format("\n==== (ID %d) %s", detection.id, detection.metadata.name));
                telemetry.addLine(String.format("XYZ %6.1f %6.1f %6.1f  (inch)", detection.ftcPose.x, detection.ftcPose.y, detection.ftcPose.z));
                telemetry.addLine(String.format("PRY %6.1f %6.1f %6.1f  (deg)", detection.ftcPose.pitch, detection.ftcPose.roll, detection.ftcPose.yaw));
                telemetry.addLine(String.format("RBE %6.1f %6.1f %6.1f  (inch, deg, deg)", detection.ftcPose.range, detection.ftcPose.bearing, detection.ftcPose.elevation));
            } else {
                telemetry.addLine(String.format("\n==== (ID %d) Unknown", detection.id));
                telemetry.addLine(String.format("Center %6.0f %6.0f   (pixels)", detection.center.x, detection.center.y));
            }
        }
            // Add "key" information to telemetry
            telemetry.addLine("\nkey:\nXYZ = X (Right), Y (Forward), Z (Up) dist.");
            telemetry.addLine("PRY = Pitch, Roll & Yaw (XYZ Rotation)");
            telemetry.addLine("RBE = Range, Bearing & Elevation");
        }

    public static double getTagYaw() {
        List<AprilTagDetection> currentDetections = aprilTagProcessor.getDetections();
        AprilTagDetection usableDetection = null;
        // Step through the list of detections and display info for each one.
        for (AprilTagDetection detection : currentDetections) {
            if((detection.metadata.id == 20 && allianceSide == "BLUE") || (detection.metadata.id == 24 && allianceSide == "RED")){
                usableDetection = detection;
            }
        }
        if(usableDetection != null){
            return usableDetection.ftcPose.yaw;
        } else {
            return 0;
        }
    }

}

