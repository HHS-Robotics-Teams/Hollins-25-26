package org.firstinspires.ftc.teamcode.OpModes.TeleOp.Old;

import static org.firstinspires.ftc.teamcode._Proccedural.Components.IntakeMotor;

import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherFingerServo;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LeftSideFeedRoller;

import static org.firstinspires.ftc.teamcode._Proccedural.Components.imu;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.leftBack;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.leftFront;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.rightBack;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.rightFront;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.DWELL_TIME;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.DriveSlowdown;
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
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_RUN_THREE;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_RUN_TWO;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHING_TIME;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCH_FAR;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCH_THRESHOLD;
import static java.lang.Math.abs;
import static java.lang.Math.max;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.teamcode.Util.AprilTagMethod;
import org.firstinspires.ftc.teamcode._Proccedural.Components;
import org.firstinspires.ftc.teamcode._Proccedural.Input;

@TeleOp
public class CompDriveV2 extends OpMode {
    //Instantiated new input
    Input input = new Input();
    AprilTagMethod aprilTagDetector;
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
        INTAKE_SECOND,
        SPIN_UP_TWO,
        UP_TWO,
        WAIT_TWO,
        DOWN_TWO,
        INTAKE_THIRD,
        SPIN_UP_THREE,
        UP_THREE,
        WAIT_THREE,
        DOWN_THREE,

    }
    static LaunchState state = LaunchState.SPIN_UP;

    @Override
    public void init() {
        //Initialize Components
        Components.initComponents(hardwareMap);

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
            INTAKE_REVERSED = false;
           state = LaunchState.SPIN_UP;
        }
        if(input.right_bumper.down()){
            LAUNCHER_RUN_THREE = !LAUNCHER_RUN_THREE;
            INTAKE_REVERSED = false;
            state = LaunchState.SPIN_UP;
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
                LauncherMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            } else {
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
            }
        }
        if(input.back.down()){
            LAUNCH_FAR = !LAUNCH_FAR;
        }

        /* ---------- Intake ---------- */
        if (input.b.down()) {
            // Reverses intake
            INTAKE_REVERSED = !INTAKE_REVERSED;
        }

        // Hold left trigger to run the main intake motor
        if(!LAUNCHER_RUN && !LAUNCHER_RUN_THREE) INTAKE_RUN = input.left_trigger.held();

        // Press left bumper to TOGGLE the second level intake on/off
        if(!LAUNCHER_RUN && !LAUNCHER_RUN_THREE) INTAKE_LEVEL_TWO_RUN = input.left_bumper.held();

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

        /* ---------- Drivetrain ---------- */

        if(input.start.down()){
            imu.resetYaw();
            imu.initialize(new IMU.Parameters(new RevHubOrientationOnRobot(RevHubOrientationOnRobot.LogoFacingDirection.LEFT, RevHubOrientationOnRobot.UsbFacingDirection.UP)));
        }


        //Drivetrain movement values
        double forward = -gamepad1.left_stick_y  * 0.8;
        double strafes =  gamepad1.left_stick_x  * 1.0;
        double rotates =  gamepad1.right_stick_x * 0.6;


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
        if (input.left_stick_button.down() || input.right_stick_button.down()){
            DriveSlowdown = !DriveSlowdown;
        }
        if (DriveSlowdown){
            rotates = rotates / 3;
            strafes = strafes / 3;
            forward = forward / 3;
        }



        //Power fixer
        double denominator = max((abs(forward) + abs(strafes) + abs(rotates)), 1);

        if(abs(imu.getRobotOrientation(AxesReference.INTRINSIC, AxesOrder.XYZ, AngleUnit.DEGREES).thirdAngle) <= 30){
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
        telemetry.addData("Intake second level running?", INTAKE_LEVEL_TWO_RUN);
        telemetry.addData("Intake reversed? ", INTAKE_REVERSED);
        telemetry.addData("Launcher running? ", LAUNCHER_RUN);
        telemetry.addData("Launcher running Three? ", LAUNCHER_RUN_THREE);
        telemetry.addData("Launcher Far?", LAUNCH_FAR);
        telemetry.addData("Launcher Velocity: ", LauncherMotor.getVelocity(AngleUnit.RADIANS));
        telemetry.addData("Launcher State: ", state);
        telemetry.addData("Launcher time" ,launchTimer.seconds());
        telemetry.addData("Intake time", IntakeTimer.seconds());
        telemetry.addLine("");
    }

    public void runLauncherOnceNear() {
        switch(state) {
            case SPIN_UP:
                LauncherMotor.setVelocity(LAUNCHER_NEAR_TARGET, AngleUnit.RADIANS);
                if (abs(LauncherMotor.getVelocity(AngleUnit.RADIANS) - LAUNCHER_NEAR_TARGET) <= LAUNCH_THRESHOLD*2) {
                    INTAKE_RUN = false;
                    INTAKE_LEVEL_TWO_RUN = false;
                    state = LaunchState.UP;
                }
                break;
            case UP:
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
                INTAKE_LEVEL_TWO_RUN = true;
                LeftSideFeedRoller.setPower(INTAKE_POWER);
                //INTAKE_RUN = true;
                launchTimer.reset();
                state = LaunchState.WAIT;
                break;

            case WAIT:
                if(launchTimer.seconds() >= DWELL_TIME){ //Todo Change Time
                    INTAKE_LEVEL_TWO_RUN = false;
                    LeftSideFeedRoller.setPower(0);
                    state = LaunchState.DOWN;
                }
                break;

            case DOWN:
                state = LaunchState.SPIN_UP;
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
                LAUNCHER_RUN = false;
                break;
        }
    }
    public void runLauncherOnceFar() {
        switch(state) {
            case SPIN_UP:
                LauncherMotor.setVelocity(LAUNCHER_FAR_TARGET, AngleUnit.RADIANS);
                if (abs(LauncherMotor.getVelocity(AngleUnit.RADIANS) - LAUNCHER_FAR_TARGET) <= LAUNCH_THRESHOLD*2) {

                    state = LaunchState.UP;
                    INTAKE_RUN = false;

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
        switch (state) {
            case SPIN_UP:
                LauncherMotor.setVelocity(LAUNCHER_NEAR_TARGET, AngleUnit.RADIANS);
                // Ensure intake is off before the first shot
                INTAKE_RUN = false;
                INTAKE_LEVEL_TWO_RUN = false;
                // Once motor is at speed, move to launch the first Ball
                if (abs(LauncherMotor.getVelocity(AngleUnit.RADIANS) - LAUNCHER_NEAR_TARGET) <= LAUNCH_THRESHOLD * 2) {
                    state = LaunchState.UP;
                }
                break;
            case UP:
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
                INTAKE_LEVEL_TWO_RUN = true;
                launchTimer.reset();
                state = LaunchState.WAIT;
                break;
            case WAIT:
                if (launchTimer.seconds() >= DWELL_TIME) { // Todo Change Time
                    INTAKE_LEVEL_TWO_RUN = false;
                    state = LaunchState.DOWN;
                }
                break;
            case DOWN:
                // Retract the finger after the first launch
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
                launchTimer.reset();
                // Transition to the INTAKE state to load the second Ball
                state = LaunchState.INTAKE_SECOND;
                break;

            case INTAKE_SECOND:
                INTAKE_RUN = true;
                INTAKE_LEVEL_TWO_RUN = true; // Run the second level as well
                if (launchTimer.seconds() >= LAUNCHING_TIME) { // todo Change Time
                    // Stop the intake and prepare for the second shot
                    INTAKE_RUN = false;
                    INTAKE_LEVEL_TWO_RUN = false;
                    state = LaunchState.SPIN_UP_TWO;
                }
                break;
            case SPIN_UP_TWO:
                // Re-verify motor speed for the second shot
                LauncherMotor.setVelocity(LAUNCHER_NEAR_TARGET, AngleUnit.RADIANS);
                if (abs(LauncherMotor.getVelocity(AngleUnit.RADIANS) - LAUNCHER_NEAR_TARGET) <= LAUNCH_THRESHOLD * 2) {
                    state = LaunchState.UP_TWO;
                }
                break;
            case UP_TWO:
                // Push the second ball into the launcher
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
                INTAKE_LEVEL_TWO_RUN = true;
                launchTimer.reset();
                state = LaunchState.WAIT_TWO;
                break;
            case WAIT_TWO:
                // Wait for the finger to move
                if (launchTimer.seconds() >= DWELL_TIME) { //Todo Change Time
                    INTAKE_LEVEL_TWO_RUN = false;
                    state = LaunchState.DOWN_TWO;
                }
                break;

            case DOWN_TWO:
                // Retract the finger, completing the sequence
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
                state = LaunchState.INTAKE_THIRD; // Reset the state machine for the next command
                break;
            case INTAKE_THIRD:
                INTAKE_RUN = true;
                INTAKE_LEVEL_TWO_RUN = true; // Run the second level as well
                if (launchTimer.seconds() >= (LAUNCHING_TIME + 0.5 )) { // todo Change Time
                    // Stop the intake and prepare for the second shot
                    INTAKE_RUN = false;
                    INTAKE_LEVEL_TWO_RUN = false;
                    state = LaunchState.SPIN_UP_THREE;
                }
            case SPIN_UP_THREE:
                // Re-verify motor speed for the second shot
                LauncherMotor.setVelocity(LAUNCHER_NEAR_TARGET, AngleUnit.RADIANS);
                if (abs(LauncherMotor.getVelocity(AngleUnit.RADIANS) - LAUNCHER_NEAR_TARGET) <= LAUNCH_THRESHOLD * 2) {
                    state = LaunchState.UP_THREE;
                }
                break;
            case UP_THREE:
                // Push the third ball into the launcher
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
                INTAKE_LEVEL_TWO_RUN = true;
                launchTimer.reset();
                state = LaunchState.WAIT_THREE;
                break;
            case WAIT_THREE:
                // Wait for the finger to move
                if (launchTimer.seconds() >= DWELL_TIME) { //Todo Change Time
                    INTAKE_RUN = false;
                    INTAKE_LEVEL_TWO_RUN = false;
                    state = LaunchState.DOWN_THREE;
                }
                break;
            case DOWN_THREE:
                // Retract the finger, completing the sequence
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
                LAUNCHER_RUN_THREE = false; // Turn off the launch flag
                state = LaunchState.SPIN_UP; // Reset the state machine for the next command
                break;
        }

    }
    public void runLauncherThreeFar() {
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
                state = LaunchState.INTAKE_SECOND;
                break;
            case INTAKE_SECOND:
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
        }
    }

}
