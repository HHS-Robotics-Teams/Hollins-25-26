package org.firstinspires.ftc.teamcode.OpModes.TeleOp;

import static org.firstinspires.ftc.teamcode._Proccedural.Components.IntakeMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherFingerServo;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LeftSideFeedRoller;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.Parking_Motor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.cameraTiltServo;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.imu;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.leftBack;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.leftFront;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.rightBack;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.rightFront;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.CAMERA_START_POS;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.DriveSlowdown;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.INTAKE_LEVEL_TWO_RUN;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.INTAKE_POWER;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.INTAKE_REVERSED;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.INTAKE_RUN;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_FINGER_DOWN_POS;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_FINGER_UP_POS;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_IDLE;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_RUN;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_RUN_TWO;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCH_FAR;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.park_Pos;
import static java.lang.Math.abs;
import static java.lang.Math.max;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.teamcode.Util.AprilTagMethod;
import org.firstinspires.ftc.teamcode.Util.LauncherUtil;
import org.firstinspires.ftc.teamcode._Proccedural.Components;
import org.firstinspires.ftc.teamcode._Proccedural.Input;

@TeleOp
public class CompDriveV3RED extends OpMode {
    //Instantiated new input
    Input input = new Input();
    AprilTagMethod aprilTagDetector;
    LauncherUtil launcherUtil;

    @Override
    public void init() {
        //Initialize Components
        Components.initComponents(hardwareMap);
        aprilTagDetector = new AprilTagMethod();
        launcherUtil = new LauncherUtil("RED", false);



        /* ---------- Telemetry ---------- */
        telemetry.addLine("--------- Init Complete ---------");
    }
    @Override
    public void init_loop() {
        // April Tag Detector
        aprilTagDetector = new AprilTagMethod();
        aprilTagDetector.updateAndShowTelemetry(telemetry);
    }

    @Override
    public void start() {
        //resets from other flags
        INTAKE_RUN = false;
        INTAKE_LEVEL_TWO_RUN = false;
        LAUNCHER_RUN_TWO = false;
        LAUNCHER_RUN = false;
        LAUNCH_FAR = false;
        DriveSlowdown = false;
        LauncherMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
        cameraTiltServo.setPosition(CAMERA_START_POS);
        Parking_Motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Parking_Motor.setTargetPosition(0);
        Parking_Motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        Parking_Motor.setPower(1);
    }

    @Override
    public void stop() {
        LauncherMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        LauncherMotor.setPower(0);
    }

    @Override
    public void loop() {
        input.pollGamepad(gamepad1);

        /* ---------- Drivetrain ---------- */

        if (input.start.down()) {
            imu.resetYaw();
            imu.initialize(new IMU.Parameters(new RevHubOrientationOnRobot(RevHubOrientationOnRobot.LogoFacingDirection.LEFT, RevHubOrientationOnRobot.UsbFacingDirection.UP)));
        }

        //Drivetrain movement values
        double forward = -gamepad1.left_stick_y * 0.8;
        double strafes = gamepad1.left_stick_x * 1.0;
        double rotates = gamepad1.right_stick_x * 0.6;


        if (abs(forward) <= 0.15) {
            forward = 0;
        }
        if (abs(strafes) <= 0.15) {
            strafes = 0;
        }
        if (abs(rotates) <= 0.15) {
            rotates = 0;
        }
        // slow down
        if (input.left_stick_button.down() || input.right_stick_button.down()) {
            DriveSlowdown = !DriveSlowdown;
        }
        if (DriveSlowdown) {
            rotates = rotates / 3;
            strafes = strafes / 3;
            forward = forward / 3;
        }


        //Power fixer
        double denominator = max((abs(forward) + abs(strafes) + abs(rotates)), 1);

//        if (abs(imu.getRobotOrientation(AxesReference.INTRINSIC, AxesOrder.XYZ, AngleUnit.DEGREES).thirdAngle) <= 30) {
//            strafes = -strafes;
//        }

        //Setting Powers
        leftFront.setPower((forward + strafes + rotates) / denominator);
        rightFront.setPower((forward - strafes - rotates) / denominator);
        leftBack.setPower((forward - strafes + rotates) / denominator);
        rightBack.setPower((forward + strafes - rotates) / denominator);

        /* ---------- Launch ---------- */
        if (input.right_trigger.down()) {
            LAUNCHER_RUN = !LAUNCHER_RUN;
            INTAKE_REVERSED = false;
        }
        if (input.right_bumper.down()) {
            LAUNCHER_RUN = !LAUNCHER_RUN;
            INTAKE_REVERSED = false;
        }

        /* ---------- Intake ---------- */
        if (input.b.down()) {
            // Reverses intake
            INTAKE_REVERSED = !INTAKE_REVERSED;
        }

        // Hold left trigger to run the main intake motor
        if (!LAUNCHER_RUN) INTAKE_RUN = input.left_trigger.held();

        // Press left bumper to TOGGLE the second level intake on/off
        if (!LAUNCHER_RUN) INTAKE_LEVEL_TWO_RUN = input.left_bumper.held();

        // --- Final Intake Motor Logic ---

        // Control the main intake motor
        if (INTAKE_RUN) {
            IntakeMotor.setPower(INTAKE_REVERSED ? -INTAKE_POWER : INTAKE_POWER);
        } else {
            IntakeMotor.setPower(0);
        }

        if (INTAKE_LEVEL_TWO_RUN) {
            double power = INTAKE_REVERSED ? -1 : 1;
            LeftSideFeedRoller.setPower(power);
        } else {
            LeftSideFeedRoller.setPower(0);
        }

        //while LAUNCHER_RUN flag is true launch
        if (LAUNCHER_RUN) {
            telemetry.addLine("Launch Status:" + launcherUtil.runLauncher());
        } else {
            LauncherMotor.setPower(LAUNCHER_IDLE);
            /* ---------- Launcher Finger (Manual) ---------- */
            if (input.x.held()) {
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
                LauncherMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            } else {
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
            }
        }

        if (input.dpad_down.down()){
            Parking_Motor.setTargetPosition(park_Pos);
        } if (input.dpad_up.down()) {
            Parking_Motor.setTargetPosition(0);
        }
        /* ---------- Telemetry ---------- */
        telemetry.addLine("--------- Comp Drive Running ---------");
        telemetry.addData("ÏMU Z", abs(imu.getRobotOrientation(AxesReference.INTRINSIC, AxesOrder.XYZ, AngleUnit.DEGREES).thirdAngle));
        telemetry.addData("Drive Slowdown?", DriveSlowdown);
        telemetry.addData("Intake running? ", INTAKE_RUN);
        telemetry.addData("Intake reversed? ", INTAKE_REVERSED);
        telemetry.addData("Launcher running? ", LAUNCHER_RUN);
        telemetry.addData("Launcher Velocity: ", LauncherMotor.getVelocity());
    }

}
