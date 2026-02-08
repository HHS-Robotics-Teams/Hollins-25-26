// Example of a_cross TeleOp OpMode using your new class

package org.firstinspires.ftc.teamcode._OpModes.Prototypes.old;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode._Util.AprilTagMethod;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

@Disabled
@Deprecated
@TeleOp(name = "AprilTag TeleOp Example")
public class AprilTagTeleOpExample extends LinearOpMode {

    // Create an instance of your new AprilTag class
    AprilTagMethod aprilTagDetector;

    @Override
    public void runOpMode() {
        // Initialize your hardware, including the AprilTag processor (tagHelper)
        // Make sure tagHelper is initialized before this point!
        // e.g., tagHelper.init(hardwareMap);

        // Instantiate your detector class
        aprilTagDetector = new AprilTagMethod();

        waitForStart();

        while (opModeIsActive()) {
            // --- AprilTag Logic ---
            // This single line will handle getting data and showing it on the Driver Station
            aprilTagDetector.updateAndShowTelemetry(telemetry);

            // --- Example of using the returned values ---
            // You can now get the latest detection data for other purposes, like robot alignment.
            AprilTagDetection latestTag = aprilTagDetector.getLatestDetection();

            if (latestTag != null) {
                // Use the tag's data for robot control
                // For example, drive towards the tag based on its X and Y position
                double drive = -latestTag.ftcPose.y; // Example: Use Y for forward movement
                double strafe = -latestTag.ftcPose.x; // Example: Use X for strafing
                double turn = latestTag.ftcPose.yaw;  // Example: Use Yaw for turning

                // Add logic here to control your robot's drivetrain with these values
                telemetry.addLine("\n--- Robot Control Values ---");
                telemetry.addData("Drive", "%.2f", drive);
                telemetry.addData("Strafe", "%.2f", strafe);
                telemetry.addData("Turn", "%.2f", turn);
            }

            // Update telemetry at the end of the loop
            telemetry.update();

            // Allow other processes to run
            sleep(20);
        }
    }
}
