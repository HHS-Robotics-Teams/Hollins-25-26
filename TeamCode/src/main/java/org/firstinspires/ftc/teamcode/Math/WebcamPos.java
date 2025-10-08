package org.firstinspires.ftc.teamcode.Math;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;

import android.annotation.SuppressLint;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagGameDatabase;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.ArrayList;
import java.util.List;

public class WebcamPos {

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


        builder.setCamera(hardwareMap.get(WebcamName.class, "Webcam"));
        builder.addProcessor(aprilTagProcessor);
        builder.enableLiveView(true);
        visionPortal = builder.build();

        allianceSide = color;
    }

    public static double calculateLauncherPower() {
        double launcherDistance;
        List<AprilTagDetection> currentDetections = aprilTagProcessor.getDetections();
        for(AprilTagDetection detection : currentDetections) {
            if(allianceSide == "BLUE" && detection.id == 20){
                calculateDistance(detection.ftcPose.x, detection.ftcPose.y, detection.ftcPose.z, detection.ftcPose.yaw);
                break;
            } else if(allianceSide == "RED" && detection.id == 24){
                calculateDistance(detection.ftcPose.x, detection.ftcPose.y, detection.ftcPose.z, detection.ftcPose.yaw);
                break;
            }
        }

        return 0;
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

    /**
     * calculates the distance for power calculations
     * @return distance (hypotenuse) of triangle
     */
    private static double calculateDistance(double camX, double camY, double camZ, double camYaw) {
        double launchX = camX + /*cam offset*/(-2);
        double launchY = camY + /*cam offset*/(-5);
        double launchZ = camZ + /*cam offset*/(-2);
        double launchElevation = Math.atan(launchZ / launchY);
        return 0;
    }
}

