package org.firstinspires.ftc.teamcode.OpMode.TelOp;

import static org.firstinspires.ftc.teamcode.aProccedural.Constants.AutomaticPickupInterval;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.ClawInterval;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.TiltInterval;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.clawtiltdroppos;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.clawtiltmaxpos;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.clawtiltpickuppos;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.clawtiltstartpos;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.pincerleftclosed;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.pincerleftopen;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.pincerrightopen;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.tiltdroppos;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.tiltmaxpos;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.tiltminpos;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.tiltpickuppos;
import static org.firstinspires.ftc.teamcode.aProccedural.RobotComponents.arm_tilt;
import static org.firstinspires.ftc.teamcode.aProccedural.RobotComponents.claw_tilt;
import static org.firstinspires.ftc.teamcode.aProccedural.RobotComponents.colorSensor;
import static org.firstinspires.ftc.teamcode.aProccedural.RobotComponents.leftMotor;
import static org.firstinspires.ftc.teamcode.aProccedural.RobotComponents.pincer_left;
import static org.firstinspires.ftc.teamcode.aProccedural.RobotComponents.rightMotor;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.aProccedural.AprilTagHelper;
import org.firstinspires.ftc.teamcode.aProccedural.Input;
//import org.firstinspires.ftc.teamcode.excutil.coroutines.CoroutineManager;

import org.firstinspires.ftc.teamcode.aProccedural.RobotComponents;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

@TeleOp ( name = "OutreachBotDrive")
public class OutreachBotDrive extends OpMode {
    private AprilTagHelper tagHelper;
    private Input input;

    ElapsedTime clawTiltTimer = new ElapsedTime();
    public boolean isClawTilted = false;


    @Override
    public void init() {

        RobotComponents.init(hardwareMap);
        // Create AprilTag processor
        tagHelper = new AprilTagHelper(hardwareMap, "Webcam");

        pincer_left.setPosition(pincerrightopen);
        claw_tilt.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        arm_tilt.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        claw_tilt.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        arm_tilt.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        arm_tilt.setPower(1);
        claw_tilt.setPower(1);

        arm_tilt.setTargetPosition(tiltminpos);
        claw_tilt.setTargetPosition(clawtiltstartpos);

        input = new Input();

        AprilTagDetection tag = tagHelper.getFirstTag(); // Call this every loop to get current detections
        if (tag != null && tag.ftcPose != null) { // Added null check for ftcPose
            telemetry.addData("Tag ID", tag.id);
            telemetry.addData("Tag Name", tag.metadata != null ? tag.metadata.name : "N/A"); // Display name if available
            telemetry.addData("X (in)", "%.2f", tag.ftcPose.x);
            telemetry.addData("Y (in)", "%.2f", tag.ftcPose.y);
            telemetry.addData("Z (in)", "%.2f", tag.ftcPose.z);
            telemetry.addData("Yaw (deg)", "%.2f", tag.ftcPose.yaw);
            telemetry.addData("Pitch (deg)", "%.2f", tag.ftcPose.pitch);
            telemetry.addData("Roll (deg)", "%.2f", tag.ftcPose.roll);
        }

    }

    @Override
    public void loop() {
        DetectedColor.updateColor(colorSensor);
        input.pollGamepad(gamepad1);


        // Tank drive control
        double forwardPower = -gamepad1.left_stick_y; // Forward/backward movement
        double turnPower = -gamepad1.right_stick_x; // Left/right turning

        // Calculate motor powers for left and right motors
        double leftPower = forwardPower + turnPower;
        double rightPower = forwardPower - turnPower;

        // Set power to motors
        leftMotor.setPower(leftPower);
        rightMotor.setPower(rightPower);

        // Automatic claw tilt
        if ((Math.abs(arm_tilt.getCurrentPosition() - tiltpickuppos) < AutomaticPickupInterval)) {
            claw_tilt.setTargetPosition(clawtiltpickuppos);
            isClawTilted = true;
        }
        //Home Position
        if (input.start.down()) {
            arm_tilt.setPower(0);
            claw_tilt.setTargetPosition(clawtiltmaxpos);
            clawTiltTimer.reset();
            if (clawTiltTimer.seconds() > 1) {
                arm_tilt.setPower(1);
                arm_tilt.setTargetPosition(tiltminpos);
                claw_tilt.setTargetPosition(clawtiltstartpos);
                pincer_left.setPosition(pincerrightopen);
            }
        }
        //Manual arm tilt
        if (input.dpad_up.down() && (arm_tilt.getCurrentPosition() >= tiltminpos)){
            arm_tilt.setTargetPosition(arm_tilt.getCurrentPosition() + TiltInterval);
        }
        if (input.dpad_down.down() && (arm_tilt.getCurrentPosition() <= tiltmaxpos)){
            arm_tilt.setTargetPosition(arm_tilt.getCurrentPosition() - TiltInterval);
        }
        //Manual Claw Tilt
        if (input.left_bumper.down() && (claw_tilt.getCurrentPosition() >= clawtiltstartpos)){
            claw_tilt.setTargetPosition(claw_tilt.getCurrentPosition() + ClawInterval);
        }
        if (input.left_trigger.down() && (claw_tilt.getCurrentPosition() <= clawtiltdroppos)){
            claw_tilt.setTargetPosition(claw_tilt.getCurrentPosition() - ClawInterval);
        }
        // Claw
        if (input.right_trigger.down()){
            pincer_left.setPosition(pincerleftclosed);
        }
        if (input.right_bumper.down()){
            pincer_left.setPosition(pincerleftopen);
        }
        //Pickup Position
        if (input.a.down()) {
            arm_tilt.setPower(0);
            claw_tilt.setTargetPosition(clawtiltmaxpos);
            clawTiltTimer.reset();
            if (clawTiltTimer.seconds() > 1) {
                arm_tilt.setPower(1);
                arm_tilt.setTargetPosition(tiltpickuppos);
                claw_tilt.setTargetPosition(clawtiltpickuppos);
                pincer_left.setPosition(pincerrightopen);
            }
        }
        //Drop Position
        if (input.b.down()){
            arm_tilt.setTargetPosition(tiltdroppos);
            claw_tilt.setTargetPosition(clawtiltdroppos);
        }






        // --- AprilTag Detection ---
        AprilTagDetection tag = tagHelper.getFirstTag(); // Call this every loop to get current detections
        if (tag != null && tag.ftcPose != null) { // Added null check for ftcPose
            telemetry.addData("Tag ID", tag.id);
            telemetry.addData("Tag Name", tag.metadata != null ? tag.metadata.name : "N/A"); // Display name if available
            telemetry.addData("X (in)", "%.2f", tag.ftcPose.x);
            telemetry.addData("Y (in)", "%.2f", tag.ftcPose.y);
            telemetry.addData("Z (in)", "%.2f", tag.ftcPose.z);
            telemetry.addData("Yaw (deg)", "%.2f", tag.ftcPose.yaw);
            telemetry.addData("Pitch (deg)", "%.2f", tag.ftcPose.pitch);
            telemetry.addData("Roll (deg)", "%.2f", tag.ftcPose.roll);
        }

        // Display the detected color on telemetry
        telemetry.addData("Detected Color", DetectedColor.getColor());
        telemetry.addData("arm tilt position", arm_tilt.getCurrentPosition());
        telemetry.addData("claw tilt position", claw_tilt.getCurrentPosition());
        // Display motor power on telemetry
        telemetry.addData("Left Motor Power", "%.2f", leftPower);
        telemetry.addData("Right Motor Power", "%.2f", rightPower);
        telemetry.update();

        }
    @Override
    public void stop() {

        if (tagHelper != null) {
            tagHelper.stop(); // Or tagHelper.close() depending on your AprilTagHelper implementation
        }

        telemetry.addData("Status", "OpMode Stopped");
        telemetry.update();
    }
    }

