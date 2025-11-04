package org.firstinspires.ftc.teamcode.OpModes.TeleOp;

import static org.firstinspires.ftc.teamcode.aProccedural.Components.IntakeMotor;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.IntakeSecondLevelServo;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.LauncherFingerServo;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.LauncherMotor;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.LeftSideFeedRoller;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.ParkingStopServo;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.RightSideFeedRoller;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.leftFront;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.leftBack;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.rightFront;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.rightBack;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.INTAKE_LEVEL_TWO_RUN;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.INTAKE_POWER;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.INTAKE_REVERSED;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.INTAKE_RUN;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCHER_FAR_TARGET;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCHER_NEAR_TARGET;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCHER_HOLDER_ENABLE;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCHER_IDLE;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCHER_RUN_THREE;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCH_FAR;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCH_THRESHOLD;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCHER_FINGER_UP_POS;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCHER_FINGER_DOWN_POS;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCHER_RUN;

import static java.lang.Math.abs;
import static java.lang.Math.max;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.Math.AprilTagHelper;
import org.firstinspires.ftc.teamcode.aProccedural.Components;
import org.firstinspires.ftc.teamcode.aProccedural.Input;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

@TeleOp
public class CompDrive extends OpMode {
    private AprilTagHelper tagHelper;
    //Instantiated new input
    Input input = new Input();
    //temp time used mainly in state machines
    static double timeAtLaunch;
    ElapsedTime launchTimer = new ElapsedTime();
    //enum for launching state machines
    enum LaunchState{
        SPIN_UP,
        UP,
        WAIT,
        DOWN,
        SPIN_UP_TWO,
        UP_TWO,
        WAIT_TWO,
        DOWN_TWO
    }
    static LaunchState state = LaunchState.SPIN_UP;

    @Override
    public void init() {
        //Initialize Components
        Components.initComponents(hardwareMap);

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
            telemetry.update();
        }
        /* ---------- Telemetry ---------- */
        telemetry.addLine("--------- Init Complete ---------");
    }

    @Override
    public void start() {
        //resets from other flags
        INTAKE_RUN = false;
        INTAKE_LEVEL_TWO_RUN = false;
        LAUNCHER_RUN_THREE = false;
        LAUNCHER_RUN = false;
        LAUNCH_FAR = false;
        state = LaunchState.SPIN_UP;
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
           state = LaunchState.SPIN_UP;
        }
        if(input.right_bumper.down()){
            LAUNCHER_RUN_THREE = !LAUNCHER_RUN_THREE;
        }

        //while LAUNCHER_RUN flag is true, launch one
        //while LAUNCHER_RUN_THREE flag is true, launch 3
        //otherwise let motor float
        if (LAUNCHER_RUN) {
            if(LAUNCH_FAR){
                runLauncherOnceFar();
            } else {
                runLauncherOnceNear(); }
        } else if(LAUNCHER_RUN_THREE) {
            if(LAUNCH_FAR){
                runLauncherThreeFar();
            } else {
                runLauncherThreeNear();
        }
        } else {
            LauncherMotor.setPower(LAUNCHER_IDLE);
            /* ---------- Launcher Finger (Manual) ---------- */
            if (input.x.held()) {
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
            } else {
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
            }
        }
        if(input.back.down()){
            LAUNCH_FAR = !LAUNCH_FAR;
        }

        /* ---------- Intake ---------- */
        if (input.b.down()) {
            //Reverses intake, side rollers, and launcher
            INTAKE_REVERSED = !INTAKE_REVERSED;
            INTAKE_RUN = !INTAKE_RUN;
            INTAKE_LEVEL_TWO_RUN = !INTAKE_LEVEL_TWO_RUN;
        }
        if (input.left_trigger.held()) {
            //toggles intake on and off
            INTAKE_RUN = true;
        } else {
            INTAKE_RUN = false;
        }
        if (input.left_bumper.held()) {
            //toggles second level on and off
            INTAKE_LEVEL_TWO_RUN = true;
        } else {
            INTAKE_LEVEL_TWO_RUN = false;
        }


        if (INTAKE_RUN) {
            if (!INTAKE_REVERSED) {
                //run intake & rollers
                IntakeMotor.setPower(INTAKE_POWER);
            } else {
                //run everything backwards
                IntakeMotor.setPower(-INTAKE_POWER);
            }
        } else {
            //no intake
            IntakeMotor.setPower(0);
        }

        if (INTAKE_LEVEL_TWO_RUN) {
            if (!INTAKE_REVERSED) {
                //run rollers
                LeftSideFeedRoller.setPower(1);
                RightSideFeedRoller.setPower(1);
                IntakeSecondLevelServo.setPower(1);
            } else {
                //run everything backwards
                LeftSideFeedRoller.setPower(-1);
                RightSideFeedRoller.setPower(-1);
                IntakeSecondLevelServo.setPower(-1);
            }
        } else {
            LeftSideFeedRoller.setPower(0);
            RightSideFeedRoller.setPower(0);
            IntakeSecondLevelServo.setPower(0);
        }

        /* ---------- Drivetrain ---------- */

        //Drivetrain movement values
        double forward = -gamepad1.left_stick_y  * 0.8;
        double strafes =  gamepad1.left_stick_x  * 1;
        double rotates = -gamepad1.right_stick_x * 0.6;

        if (abs(forward) <= 0.15) {
            forward = 0;
        }
        if (abs(strafes) <= 0.15) {
            strafes = 0;
        }
        if (abs(rotates) <= 0.15) {
            rotates = 0;
        }

        //Power fixer
        double denominator = max((abs(forward) + abs(strafes) + abs(rotates)), 1);

        //Setting Powers
        leftFront.setPower((forward + strafes + rotates) / denominator);
        rightFront.setPower((forward - strafes - rotates) / denominator);
        leftBack.setPower((forward - strafes + rotates) / denominator);
        rightBack.setPower((forward + strafes - rotates) / denominator);

        /* ---------- Telemetry ---------- */
        telemetry.addLine("--------- Comp Drive Running ---------");
        telemetry.addData("Intake running? ", INTAKE_RUN);
        telemetry.addData("Intake second level running?", INTAKE_LEVEL_TWO_RUN);
        telemetry.addData("Intake reversed? ", INTAKE_REVERSED);
        telemetry.addData("Launcher holders holding? ", LAUNCHER_HOLDER_ENABLE);
        telemetry.addData("Launcher running? ", LAUNCHER_RUN);
        telemetry.addData("Launcher running triple? ", LAUNCHER_RUN_THREE);
        telemetry.addData("Launcher Far?", LAUNCH_FAR);
        telemetry.addData("Launcher Velocity: ", LauncherMotor.getVelocity(AngleUnit.RADIANS));
        telemetry.addData("Launcher State: ", state);
        telemetry.addLine("");
    }

    public void runLauncherOnceNear() {
        switch(state) {
            case SPIN_UP:
                LauncherMotor.setVelocity(LAUNCHER_NEAR_TARGET, AngleUnit.RADIANS);
                if (abs(LauncherMotor.getVelocity(AngleUnit.RADIANS) - LAUNCHER_NEAR_TARGET) <= LAUNCH_THRESHOLD*2) {
                    timeAtLaunch = getRuntime();

                    state = LaunchState.UP;
                    INTAKE_RUN = false;
                    INTAKE_LEVEL_TWO_RUN = false;
                }
                break;
            case UP:
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
                launchTimer.reset();
                state = LaunchState.WAIT;
                timeAtLaunch = getRuntime();
                break;
            case WAIT:
                if(launchTimer.seconds() >= 1){
                    state = LaunchState.DOWN;
                }
                break;
            case DOWN:
                state = LaunchState.SPIN_UP;
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
                timeAtLaunch = getRuntime();
                LAUNCHER_RUN = false;
                break;
        }
    }
    public void runLauncherOnceFar() {
        switch(state) {
            case SPIN_UP:
                LauncherMotor.setVelocity(LAUNCHER_FAR_TARGET, AngleUnit.RADIANS);
                if (abs(LauncherMotor.getVelocity(AngleUnit.RADIANS) - LAUNCHER_FAR_TARGET) <= LAUNCH_THRESHOLD*2) {
                    timeAtLaunch = getRuntime();

                    state = LaunchState.UP;
                    INTAKE_RUN = false;
                    INTAKE_LEVEL_TWO_RUN = false;
                }
                break;
            case UP:
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
                launchTimer.reset();
                state = LaunchState.WAIT;
                timeAtLaunch = getRuntime();
                break;
            case WAIT:
                if(launchTimer.seconds() >= 1){
                    state = LaunchState.DOWN;
                }
                break;
            case DOWN:
                state = LaunchState.SPIN_UP;
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
                timeAtLaunch = getRuntime();
                LAUNCHER_RUN = false;
                break;
        }
    }

    public void runLauncherThreeNear() {
        switch(state) {
            case SPIN_UP:
                LauncherMotor.setVelocity(LAUNCHER_NEAR_TARGET, AngleUnit.RADIANS);
                if (abs(LauncherMotor.getVelocity(AngleUnit.RADIANS) - LAUNCHER_NEAR_TARGET) <= LAUNCH_THRESHOLD * 2) {
                    timeAtLaunch = getRuntime();
                    state = LaunchState.UP;
                }
                break;
            case UP:
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
                state = LaunchState.WAIT;
                timeAtLaunch = getRuntime();
                break;
            case WAIT:
                if(getRuntime() - timeAtLaunch >= 0.2){
                    LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
                }
                if(getRuntime() - timeAtLaunch >= 0.6){
                    state = LaunchState.DOWN;
                }
                break;
            case DOWN:
                state = LaunchState.SPIN_UP_TWO;
                timeAtLaunch = getRuntime();
                break;
            case SPIN_UP_TWO:
                LauncherMotor.setVelocity(LAUNCHER_NEAR_TARGET, AngleUnit.RADIANS);
                if(getRuntime() - timeAtLaunch >= 0.3){
                    INTAKE_RUN = false;
                    INTAKE_LEVEL_TWO_RUN = false;
                } else if(getRuntime() - timeAtLaunch >= 0.1){
                    INTAKE_RUN = true;
                    INTAKE_LEVEL_TWO_RUN = true;
                }
                if (abs(LauncherMotor.getVelocity(AngleUnit.RADIANS) - LAUNCHER_NEAR_TARGET) <= LAUNCH_THRESHOLD * 2) {
                    timeAtLaunch = getRuntime();
                    state = LaunchState.UP_TWO;
                }
                break;
            case UP_TWO:
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
                state = LaunchState.WAIT_TWO;
                timeAtLaunch = getRuntime();
                break;
            case WAIT_TWO:
                if(getRuntime() - timeAtLaunch >= 0.4){
                    state = LaunchState.DOWN_TWO;
                    timeAtLaunch = getRuntime();
                }
                break;
            case DOWN_TWO:
                LAUNCHER_RUN_THREE = false;
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
                timeAtLaunch = getRuntime();
                INTAKE_LEVEL_TWO_RUN = true;
                LAUNCHER_RUN = true;
                if(getRuntime() - timeAtLaunch >= 0.2){
                    state = LaunchState.SPIN_UP;
                }
                break;
        }
    }
    public void runLauncherThreeFar() {
        switch(state) {
            case SPIN_UP:
                LauncherMotor.setVelocity(LAUNCHER_FAR_TARGET, AngleUnit.RADIANS);
                if (abs(LauncherMotor.getVelocity(AngleUnit.RADIANS) - LAUNCHER_FAR_TARGET) <= 2*LAUNCH_THRESHOLD) {
                    timeAtLaunch = getRuntime();
                    state = LaunchState.UP;
                }
                break;
            case UP:
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
                state = LaunchState.WAIT;
                timeAtLaunch = getRuntime();
                break;
            case WAIT:
                if(getRuntime() - timeAtLaunch >= 0.2){
                    LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
                }
                if(getRuntime() - timeAtLaunch >= 0.6){
                    state = LaunchState.DOWN;
                }
                break;
            case DOWN:
                state = LaunchState.SPIN_UP_TWO;
                timeAtLaunch = getRuntime();
                break;
            case SPIN_UP_TWO:
                LauncherMotor.setVelocity(LAUNCHER_NEAR_TARGET, AngleUnit.RADIANS);
                if(getRuntime() - timeAtLaunch >= 0.3){
                    INTAKE_RUN = false;
                    INTAKE_LEVEL_TWO_RUN = false;
                } else if(getRuntime() - timeAtLaunch >= 0.1){
                    INTAKE_RUN = true;
                    INTAKE_LEVEL_TWO_RUN = true;
                }
                if (abs(LauncherMotor.getVelocity(AngleUnit.RADIANS) - LAUNCHER_NEAR_TARGET) <= LAUNCH_THRESHOLD * 2) {
                    timeAtLaunch = getRuntime();
                    state = LaunchState.UP_TWO;
                }
                break;
            case UP_TWO:
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
                state = LaunchState.WAIT_TWO;
                timeAtLaunch = getRuntime();
                break;
            case WAIT_TWO:
                if(getRuntime() - timeAtLaunch >= 0.4){
                    state = LaunchState.DOWN_TWO;
                    timeAtLaunch = getRuntime();
                }
                break;
            case DOWN_TWO:
                LAUNCHER_RUN_THREE = false;
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
                timeAtLaunch = getRuntime();
                INTAKE_LEVEL_TWO_RUN = true;
                LAUNCHER_RUN = true;
                if(getRuntime() - timeAtLaunch >= 0.2){
                    state = LaunchState.SPIN_UP;
                }
                break;
        }
    }

}
