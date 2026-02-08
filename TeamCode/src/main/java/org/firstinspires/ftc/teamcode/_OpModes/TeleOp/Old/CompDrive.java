package org.firstinspires.ftc.teamcode._OpModes.TeleOp.Old;

import static org.firstinspires.ftc.teamcode._Proccedural.Components.IntakeMotor;

import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherFingerServo;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LeftSideFeedRoller;

import static org.firstinspires.ftc.teamcode._Proccedural.Components.leftBack;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.leftFront;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.rightBack;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.rightFront;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.INTAKE_LEVEL_TWO_RUN;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.INTAKE_POWER;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.INTAKE_REVERSED;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.INTAKE_RUN;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_FAR_TARGET;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_FINGER_DOWN_POS;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_FINGER_UP_POS;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_IDLE;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_NEAR_TARGET;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_RUN;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_RUN_TWO;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCH_FAR;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCH_THRESHOLD;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.DriveSlowdown;
import static java.lang.Math.abs;
import static java.lang.Math.max;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode._Util.AprilTagHelper;
import org.firstinspires.ftc.teamcode._Proccedural.Components;
import org.firstinspires.ftc.teamcode._Proccedural.Input;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
@Disabled
@Deprecated
@TeleOp
public class CompDrive extends OpMode {
    private AprilTagHelper tagHelper;
    //Instantiated new input
    Input input = new Input();
    //temp time used mainly in state machines
    static double timeAtLaunch;
    ElapsedTime launchTimer = new ElapsedTime();
    ElapsedTime IntakeTimer = new ElapsedTime();
    //enum for launching state machines
    enum LaunchState{
        SPIN_UP,
        UP,
        WAIT,
        DOWN,
        INTAKE,
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
        //tagHelper = new AprilTagHelper(hardwareMap, "Webcam");


        /* ---------- Telemetry ---------- */
        telemetry.addLine("--------- Init Complete ---------");
    }
    @Override
    public void init_loop() {
        // Call this every loop to get current detections
        AprilTagDetection tag = tagHelper.getFirstTag();
        if (tag != null && tag.ftcPose != null) { // Added null check for ftcPose
            telemetry.addLine("--- AprilTag Detected! ---");
            telemetry.addData("Tag ID", tag.id);
            // Display name if available
            telemetry.addData("Tag Name", tag.metadata != null ? tag.metadata.name : "N/A");
            telemetry.addData("X (in)", "%.2f", tag.ftcPose.x);
            telemetry.addData("Y (in)", "%.2f", tag.ftcPose.y);
            telemetry.addData("Z (in)", "%.2f", tag.ftcPose.z);
            telemetry.addData("Yaw (deg)", "%.2f", tag.ftcPose.yaw);
            telemetry.addData("Pitch (deg)", "%.2f", tag.ftcPose.pitch);
            telemetry.addData("Roll (deg)", "%.2f", tag.ftcPose.roll);
        } else {
            telemetry.addLine("--- No AprilTag Detected ---");
        }
        telemetry.update();
    }

    @Override
    public void start() {
        //resets from other flags
        INTAKE_RUN = false;
        INTAKE_LEVEL_TWO_RUN = false;
        LAUNCHER_RUN_TWO = false;
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
            LAUNCHER_RUN_TWO = !LAUNCHER_RUN_TWO;
        }

        //while LAUNCHER_RUN flag is true, launch one
        //while LAUNCHER_RUN_THREE flag is true, launch 3
        //otherwise let motor float
        if (LAUNCHER_RUN) {
            if(LAUNCH_FAR){
                runLauncherOnceFar();
            } else {
                runLauncherOnceNear(); }
        } else if(LAUNCHER_RUN_TWO) {
            if(LAUNCH_FAR){
                runLauncherTwoFar();
            } else {
                runLauncherTwoNear();
        }
        } else {
            LauncherMotor.setPower(LAUNCHER_IDLE);
            /* ---------- Launcher Finger (Manual) ---------- */
            if (input.x_square.held()) {
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
            } else {
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
            }
        }
        if(input.back.down()){
            LAUNCH_FAR = !LAUNCH_FAR;
        }

        /* ---------- Intake ---------- */
        if (input.b_circle.down()) {
            //Reverses intake, side rollers, and launcher
            INTAKE_REVERSED = !INTAKE_REVERSED;
            INTAKE_RUN = !INTAKE_RUN;
            INTAKE_LEVEL_TWO_RUN = !INTAKE_LEVEL_TWO_RUN;
        }
        //toggles intake on and off
        INTAKE_RUN = input.left_trigger.held();
        //toggles second level on and off
        INTAKE_LEVEL_TWO_RUN = input.left_bumper.held();


        if (INTAKE_RUN) {
            if (!INTAKE_REVERSED) {
                //run intake & rollers
                IntakeMotor.setPower(INTAKE_POWER);
                LeftSideFeedRoller.setPower(1);

            } else {
                //run everything backwards
                IntakeMotor.setPower(-INTAKE_POWER);
                LeftSideFeedRoller.setPower(-1);

            }
        } else {
            //no intake
            IntakeMotor.setPower(0);
        }

        if (INTAKE_LEVEL_TWO_RUN) {
            if (!INTAKE_REVERSED) {
                //run rollers
                LeftSideFeedRoller.setPower(1);

            } else {
                //run everything backwards
                LeftSideFeedRoller.setPower(-1);

            }
        } else {
            LeftSideFeedRoller.setPower(0);

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
        // slow down
        if (input.left_stick_button.down() && input.right_stick_button.down()){
            DriveSlowdown = true;

        } else if (input.left_stick_button.down() || input.right_stick_button.down())
            DriveSlowdown = false;
        if (DriveSlowdown){
            rotates = rotates / 3;
            strafes = strafes / 3;
            forward = forward / 3;
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
        telemetry.addData("Driveslowdown?", DriveSlowdown);
        telemetry.addData("Intake running? ", INTAKE_RUN);
        telemetry.addData("Intake second level running?", INTAKE_LEVEL_TWO_RUN);
        telemetry.addData("Intake reversed? ", INTAKE_REVERSED);
        telemetry.addData("Launcher running? ", LAUNCHER_RUN);
        telemetry.addData("Launcher running Two? ", LAUNCHER_RUN_TWO);
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
    public void runLauncherTwoNear() {
        switch (state) {
            case SPIN_UP:
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

                IntakeTimer.reset();
                timeAtLaunch = getRuntime();

                state = LaunchState.INTAKE;
                break;
            case INTAKE:
                LeftSideFeedRoller.setPower(1);


                if (IntakeTimer.seconds() >=2){
                    state = LaunchState.SPIN_UP_TWO;

                }
                break;
            case SPIN_UP_TWO:
                LeftSideFeedRoller.setPower(0);


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
                state = LaunchState.SPIN_UP;
                timeAtLaunch = getRuntime();
                break;

        }
    }
    public void runLauncherTwoFar() {
        switch (state) {
            case SPIN_UP:
                LauncherMotor.setVelocity(LAUNCHER_FAR_TARGET, AngleUnit.RADIANS);
                if (abs(LauncherMotor.getVelocity(AngleUnit.RADIANS) - LAUNCHER_FAR_TARGET) <= 2 * LAUNCH_THRESHOLD) {
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
                if (getRuntime() - timeAtLaunch >= 0.2) {
                    LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
                }
                if (getRuntime() - timeAtLaunch >= 0.6) {
                    state = LaunchState.DOWN;
                }
                break;
            case DOWN:
                IntakeTimer.reset();
                timeAtLaunch = getRuntime();
                state = LaunchState.INTAKE;
                break;
            case INTAKE:
                LeftSideFeedRoller.setPower(1);


                if (IntakeTimer.seconds() >=2){
                    state = LaunchState.SPIN_UP_TWO;
                }
                break;
            case SPIN_UP_TWO:
                LeftSideFeedRoller.setPower(0);


                LauncherMotor.setVelocity(LAUNCHER_NEAR_TARGET, AngleUnit.RADIANS);
                if (getRuntime() - timeAtLaunch >= 0.3) {
                    INTAKE_RUN = false;
                    INTAKE_LEVEL_TWO_RUN = false;
                } else if (getRuntime() - timeAtLaunch >= 0.1) {
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
                if (getRuntime() - timeAtLaunch >= 0.4) {
                    state = LaunchState.SPIN_UP;
                    timeAtLaunch = getRuntime();
                }
                break;

//    public void runLauncherThreeNear() {
//        switch(state) {
//            case SPIN_UP:
//                LauncherMotor.setVelocity(LAUNCHER_NEAR_TARGET, AngleUnit.RADIANS);
//                if (abs(LauncherMotor.getVelocity(AngleUnit.RADIANS) - LAUNCHER_NEAR_TARGET) <= LAUNCH_THRESHOLD * 2) {
//                    timeAtLaunch = getRuntime();
//                    state = LaunchState_V2.UP;
//                }
//                break;
//            case UP:
//                LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
//                state = LaunchState_V2.WAIT;
//                timeAtLaunch = getRuntime();
//                break;
//            case WAIT:
//                if(getRuntime() - timeAtLaunch >= 0.2){
//                    LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
//                }
//                if(getRuntime() - timeAtLaunch >= 0.6){
//                    state = LaunchState_V2.DOWN;
//                }
//                break;
//            case DOWN:
//                state = LaunchState_V2.SPIN_UP_TWO;
//                timeAtLaunch = getRuntime();
//                break;
//            case SPIN_UP_TWO:
//                LauncherMotor.setVelocity(LAUNCHER_NEAR_TARGET, AngleUnit.RADIANS);
//                if(getRuntime() - timeAtLaunch >= 0.3){
//                    INTAKE_RUN = false;
//                    INTAKE_LEVEL_TWO_RUN = false;
//                } else if(getRuntime() - timeAtLaunch >= 0.1){
//                    INTAKE_RUN = true;
//                    INTAKE_LEVEL_TWO_RUN = true;
//                }
//                if (abs(LauncherMotor.getVelocity(AngleUnit.RADIANS) - LAUNCHER_NEAR_TARGET) <= LAUNCH_THRESHOLD * 2) {
//                    timeAtLaunch = getRuntime();
//                    state = LaunchState_V2.UP_TWO;
//                }
//                break;
//            case UP_TWO:
//                LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
//                state = LaunchState_V2.WAIT_TWO;
//                timeAtLaunch = getRuntime();
//                break;
//            case WAIT_TWO:
//                if(getRuntime() - timeAtLaunch >= 0.4){
//                    state = LaunchState_V2.DOWN_TWO;
//                    timeAtLaunch = getRuntime();
//                }
//                break;
//            case DOWN_TWO:
//                LAUNCHER_RUN_THREE = false;
//                LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
//                timeAtLaunch = getRuntime();
//                INTAKE_LEVEL_TWO_RUN = true;
//                LAUNCHER_RUN = true;
//                if(getRuntime() - timeAtLaunch >= 0.2){
//                    state = LaunchState_V2.SPIN_UP;
//                }
//                break;
//        }
//    }
//    public void runLauncherThreeFar() {
//        switch(state) {
//            case SPIN_UP:
//                LauncherMotor.setVelocity(LAUNCHER_FAR_TARGET, AngleUnit.RADIANS);
//                if (abs(LauncherMotor.getVelocity(AngleUnit.RADIANS) - LAUNCHER_FAR_TARGET) <= 2*LAUNCH_THRESHOLD) {
//                    timeAtLaunch = getRuntime();
//                    state = LaunchState_V2.UP;
//                }
//                break;
//            case UP:
//                LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
//                state = LaunchState_V2.WAIT;
//                timeAtLaunch = getRuntime();
//                break;
//            case WAIT:
//                if(getRuntime() - timeAtLaunch >= 0.2){
//                    LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
//                }
//                if(getRuntime() - timeAtLaunch >= 0.6){
//                    state = LaunchState_V2.DOWN;
//                }
//                break;
//            case DOWN:
//                state = LaunchState_V2.SPIN_UP_TWO;
//                timeAtLaunch = getRuntime();
//                break;
//            case SPIN_UP_TWO:
//                LauncherMotor.setVelocity(LAUNCHER_NEAR_TARGET, AngleUnit.RADIANS);
//                if(getRuntime() - timeAtLaunch >= 0.3){
//                    INTAKE_RUN = false;
//                    INTAKE_LEVEL_TWO_RUN = false;
//                } else if(getRuntime() - timeAtLaunch >= 0.1){
//                    INTAKE_RUN = true;
//                    INTAKE_LEVEL_TWO_RUN = true;
//                }
//                if (abs(LauncherMotor.getVelocity(AngleUnit.RADIANS) - LAUNCHER_NEAR_TARGET) <= LAUNCH_THRESHOLD * 2) {
//                    timeAtLaunch = getRuntime();
//                    state = LaunchState_V2.UP_TWO;
//                }
//                break;
//            case UP_TWO:
//                LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
//                state = LaunchState_V2.WAIT_TWO;
//                timeAtLaunch = getRuntime();
//                break;
//            case WAIT_TWO:
//                if(getRuntime() - timeAtLaunch >= 0.4){
//                    state = LaunchState_V2.DOWN_TWO;
//                    timeAtLaunch = getRuntime();
//                }
//                break;
//            case DOWN_TWO:
//                LAUNCHER_RUN_THREE = false;
//                LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
//                timeAtLaunch = getRuntime();
//                INTAKE_LEVEL_TWO_RUN = true;
//                LAUNCHER_RUN = true;
//                state = LaunchState_V2.SPIN_UP;
//                break;
//        }
        }
    }
    }
