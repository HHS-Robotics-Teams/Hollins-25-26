package org.firstinspires.ftc.teamcode.OpModes.TeleOp;

import static org.firstinspires.ftc.teamcode.Util.IntakeV2Util.initIntake;
import static org.firstinspires.ftc.teamcode.Util.IntakeV2Util.updateIntake;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.IntakeMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherFingerServo;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LeftSideFeedRoller;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.imu;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.leftBack;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.leftFront;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.rightBack;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.rightFront;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.DriveSlowdown;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.INTAKE_LEVEL_TWO_RUN;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.INTAKE_POWER;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.INTAKE_REVERSED;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.INTAKE_RUN;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_FINGER_DOWN_POS;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_FINGER_UP_POS;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_IDLE;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_RUN;
import static java.lang.Math.abs;
import static java.lang.Math.max;

import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.teamcode.Util.AprilTagMethod;
import org.firstinspires.ftc.teamcode.Util.LauncherUtilV2;
import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode._Proccedural.Components;
import org.firstinspires.ftc.teamcode._Proccedural.Input;

@TeleOp
@Disabled
public class CompDriveV4BLUE extends OpMode {
    Input input = new Input();
    AprilTagMethod aprilTagDetector;
    LauncherUtilV2 launcherUtil;

    @Override
    public void init() {
        //Initialize Components
        Components.initComponents(hardwareMap);
        initIntake();
        aprilTagDetector = new AprilTagMethod();
        launcherUtil = new LauncherUtilV2(aprilTagDetector, "BLUE", new MecanumDrive(hardwareMap, new Pose2d(0,0,0)));
        /* ---------- Telemetry ---------- */
        telemetry.addLine("--------- Init Complete ---------");
    }

    @Override
    public void init_loop() {
        // April Tag Detector
        aprilTagDetector.updateAndShowTelemetry(telemetry);
    }

    @Override
    public void stop() {
        LauncherMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        LauncherMotor.setPower(0);
    }

    @Override
    public void loop() {
        input.pollGamepad(gamepad1);

        /* ---------- Launch ---------- */
        if (input.right_trigger.down()) {
            LAUNCHER_RUN = !LAUNCHER_RUN;
            INTAKE_REVERSED = false;
        }
        if (input.right_bumper.down()) {
            LAUNCHER_RUN = !LAUNCHER_RUN;
            INTAKE_REVERSED = false;
        }

        //while LAUNCHER_RUN flag is true, launch one
        //while LAUNCHER_RUN_THREE flag is true, launch 3
        //otherwise let motor float
        if (LAUNCHER_RUN) {
            telemetry.addData("Launcher Output:", launcherUtil.runLauncher());
            telemetry.addData("Launch State: ", launcherUtil.getLaunchState());
            telemetry.addData("Launch Location: ", launcherUtil.getLaunchLocation());
        } else {
            LauncherMotor.setPower(LAUNCHER_IDLE);
            /* ---------- Launcher Finger (Manual) ---------- */
            if (input.x.held()) {
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
            } else {
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
            }
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

        updateIntake(INTAKE_RUN, INTAKE_REVERSED);

        /* ---------- Drivetrain ---------- */

        if (input.start.down()) {
            imu.resetYaw();
            imu.initialize(new IMU.Parameters(new RevHubOrientationOnRobot(RevHubOrientationOnRobot.LogoFacingDirection.LEFT, RevHubOrientationOnRobot.UsbFacingDirection.UP)));
        }


        //Drivetrain movement values
        double forward = -gamepad1.left_stick_y * 0.8;
        double strafes = gamepad1.left_stick_x * 1;
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

        if (abs(imu.getRobotOrientation(AxesReference.INTRINSIC, AxesOrder.XYZ, AngleUnit.DEGREES).thirdAngle) <= 30) {
            strafes = -strafes;
        }

        //Setting Powers
        leftFront.setPower((forward + strafes + rotates) / denominator);
        rightFront.setPower((forward - strafes - rotates) / denominator);
        leftBack.setPower((forward - strafes + rotates) / denominator);
        rightBack.setPower((forward + strafes - rotates) / denominator);

        /* ---------- Telemetry ---------- */
        telemetry.addLine("--------- Comp Drive Running ---------");
        telemetry.addData("IMU Z", abs(imu.getRobotOrientation(AxesReference.INTRINSIC, AxesOrder.XYZ, AngleUnit.DEGREES).thirdAngle));
        telemetry.addData("Drive Slowdown?", DriveSlowdown);
        telemetry.addData("Intake running? ", INTAKE_RUN);
        telemetry.addData("Intake reversed? ", INTAKE_REVERSED);
        telemetry.addData("Launcher running? ", LAUNCHER_RUN);
        telemetry.addData("Launcher Velocity: ", LauncherMotor.getVelocity());
    }

}
