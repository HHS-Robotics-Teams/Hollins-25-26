package org.firstinspires.ftc.teamcode._OpModes.Prototypes.old;

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
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_RUN;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_RUN_TWO;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCH_FAR;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCH_TICK_VELOCITY_FAR;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCH_TICK_VELOCITY_NEAR;
import static java.lang.Math.abs;
import static java.lang.Math.max;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.teamcode._Util.AprilTagMethod;
import org.firstinspires.ftc.teamcode._Util.OLD.LauncherUtil;
import org.firstinspires.ftc.teamcode._Proccedural.Components;
import org.firstinspires.ftc.teamcode._Proccedural.Input;

@TeleOp
@Disabled
@Deprecated
public class LauncherUtilTest extends OpMode {
    Input input = new Input();
    AprilTagMethod aprilTagDetector = new AprilTagMethod();
    LauncherUtil launcherUtil;

    @Override
    public void init() {
        //Initialize Components
        Components.initComponents(hardwareMap);
        aprilTagDetector = new AprilTagMethod();
        launcherUtil = new LauncherUtil("BLUE", true);

        /* ---------- Telemetry ---------- */
        telemetry.addLine("--------- Init Complete ---------");
    }

    @Override
    public void init_loop() {
        if(aprilTagDetector.isTagVisible()){
        double theta = aprilTagDetector.getTagBearing();
        double range = aprilTagDetector.getTagDistance()+2;
        double phi;
        double target;
        if(range >= 75){
            target = LAUNCH_TICK_VELOCITY_FAR;
            phi = 1.3;
        } else {
            target = LAUNCH_TICK_VELOCITY_NEAR;
            phi = -6.2;
        }
        telemetry.addData("theta: ", theta);
        telemetry.addData("range: ", range);
        telemetry.addData("phi: ", phi);
        telemetry.addData("taget: ", target);}
        // April Tag Detector
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
            telemetry.addLine("Launch Status:" + launcherUtil.runLauncher());
        } else {
            launcherUtil.cancelLaunch();
            /* ---------- Launcher Finger (Manual) ---------- */
            if (input.x_square.held()) {
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
                LauncherMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            } else {
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
            }
        }

        /* ---------- Intake ---------- */
        if (input.b_circle.down()) {
            // Reverses intake
            INTAKE_REVERSED = !INTAKE_REVERSED;
        }

        // Hold left trigger to run the main intake motor
        if (!LAUNCHER_RUN) INTAKE_RUN = input.left_trigger.held();

        // Press left bumper to TOGGLE the second level intake on/off
        if (!LAUNCHER_RUN) INTAKE_LEVEL_TWO_RUN = input.left_bumper.held();

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

        /* ---------- Drivetrain ---------- */

        if (input.start.down()) {
            imu.resetYaw();
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
        telemetry.addData("ÏMU Z", abs(imu.getRobotOrientation(AxesReference.INTRINSIC, AxesOrder.XYZ, AngleUnit.DEGREES).thirdAngle));
        telemetry.addData("Drive Slowdown?", DriveSlowdown);
        telemetry.addData("Intake running? ", INTAKE_RUN);
        telemetry.addData("Intake reversed? ", INTAKE_REVERSED);
        telemetry.addData("Launcher running? ", LAUNCHER_RUN);
        telemetry.addData("Launcher Velocity: ", LauncherMotor.getVelocity());
    }

}
