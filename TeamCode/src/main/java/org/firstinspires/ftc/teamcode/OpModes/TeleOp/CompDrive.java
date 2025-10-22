package org.firstinspires.ftc.teamcode.OpModes.TeleOp;

import static org.firstinspires.ftc.teamcode.aProccedural.Components.IntakeMotor;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.LeftLauncherHolderServo;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.LauncherMotor;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.RightLauncherHolderServo;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.leftFront;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.leftBack;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.rightFront;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.rightBack;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.INTAKE_POWER;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.INTAKE_REVERSED;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.INTAKE_RUN;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCHER_FAR_TARGET;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCHER_HOLDER_ENABLE;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCHER_IDLE;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCHER_RUN_THREE;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCH_THRESHOLD;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LEFT_LAUNCHER_HOLDER_HOLDING_POSITION;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LEFT_LAUNCHER_HOLDER_LAUNCH_POSITION;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCHER_RUN;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.RIGHT_LAUNCHER_HOLDER_HOLDING_POSITION;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.RIGHT_LAUNCHER_HOLDER_LAUNCH_POSITION;

import static java.lang.Math.abs;
import static java.lang.Math.max;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.aProccedural.Components;
import org.firstinspires.ftc.teamcode.aProccedural.Input;

@TeleOp
public class CompDrive extends OpMode {

    //Instantiated new input
    Input input = new Input();
    static double timeAtLaunch;
    enum LaunchState{
        SPIN_UP,
        OPEN,
        WAIT,
        CLOSE,
        SPIN_UP_TWO,
        OPEN_TWO,
        WAIT_TWO,
        CLOSE_TWO
    }
    static LaunchState state = LaunchState.SPIN_UP;

    @Override
    public void init() {
        //Initialize Components
        Components.initComponents(hardwareMap);

        /* ---------- Telemetry ---------- */
        telemetry.addLine("--------- Init Complete ---------");
        telemetry.addLine("WARNING: ROBOT MOVES ON START");
        telemetry.addLine("Launcher & Intake start spinning on start");
        telemetry.speak("Warning: Robot moves on start");
    }

    @Override
    public void start() {
        LauncherMotor.setPower(LAUNCHER_IDLE);
        IntakeMotor.setPower(0);
        LeftLauncherHolderServo.setPosition(LEFT_LAUNCHER_HOLDER_HOLDING_POSITION);
        RightLauncherHolderServo.setPosition(RIGHT_LAUNCHER_HOLDER_HOLDING_POSITION);
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
        if (LAUNCHER_RUN) {
            runLauncherOnce();
        } else if(LAUNCHER_RUN_THREE) {
            runLauncherThree();
        } else {
                LauncherMotor.setPower(LAUNCHER_IDLE);
        }
        /* ---------- LauncherHolders ---------- */
        if (input.x.down()) {
            LeftLauncherHolderServo.setPosition(LEFT_LAUNCHER_HOLDER_HOLDING_POSITION);
            RightLauncherHolderServo.setPosition(RIGHT_LAUNCHER_HOLDER_HOLDING_POSITION);
        }
        if (input.y.down()) {
            LeftLauncherHolderServo.setPosition(LEFT_LAUNCHER_HOLDER_LAUNCH_POSITION);
            RightLauncherHolderServo.setPosition(RIGHT_LAUNCHER_HOLDER_LAUNCH_POSITION);
        }

        /* ---------- Intake ---------- */
        if (input.b.down()) {
            INTAKE_REVERSED = !INTAKE_REVERSED;
        }
        if (input.left_trigger.down()) {
            INTAKE_RUN = !INTAKE_RUN;
        }
        if (INTAKE_RUN) {
            if (!INTAKE_REVERSED) {
                IntakeMotor.setPower(INTAKE_POWER);
            } else {
                IntakeMotor.setPower(-INTAKE_POWER);
            }
        } else {
            IntakeMotor.setPower(0);
        }

        //todo change
        if (input.dpad_down.down()) {
            INTAKE_POWER += 0.05;
        }
        if (input.dpad_up.down()) {
            INTAKE_POWER -= 0.05;
        }

        /* ---------- Drivetrain ---------- */

        //Drivetrain movement values
        double forward = gamepad1.left_stick_y;
        double strafes = -gamepad1.left_stick_x * 1.2;
        double rotates = -gamepad1.right_stick_x;

        if (abs(forward) <= 0.2) {
            forward = 0;
        }
        if (abs(strafes) <= 0.2) {
            strafes = 0;
        }
        if (abs(rotates) <= 0.2) {
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
        telemetry.addData("Intake reversed? ", INTAKE_REVERSED);
        telemetry.addData("Launcher holders holding? ", LAUNCHER_HOLDER_ENABLE);
        telemetry.addData("Launcher running? ", LAUNCHER_RUN);
        telemetry.addData("Launcher Velocity: ", LauncherMotor.getVelocity(AngleUnit.RADIANS));
        telemetry.addData("Launcher State: ", state);
        telemetry.addLine("");
    }

    public void runLauncherOnce() {
        switch(state) {
            case SPIN_UP:
            LauncherMotor.setVelocity(LAUNCHER_FAR_TARGET, AngleUnit.RADIANS);
            if (abs(LauncherMotor.getVelocity(AngleUnit.RADIANS) - LAUNCHER_FAR_TARGET) <= LAUNCH_THRESHOLD) {
                timeAtLaunch = getRuntime();
                state = LaunchState.OPEN;
            }
            break;
            case OPEN:
                LeftLauncherHolderServo.setPosition(LEFT_LAUNCHER_HOLDER_LAUNCH_POSITION);
                RightLauncherHolderServo.setPosition(RIGHT_LAUNCHER_HOLDER_LAUNCH_POSITION);
                if(getRuntime() - timeAtLaunch >= 0.1){
                    INTAKE_RUN = true;
                    state = LaunchState.WAIT;
                    timeAtLaunch = getRuntime();
                }
            break;
            case WAIT:
                if(getRuntime() - timeAtLaunch >= 0.25){
                    INTAKE_RUN = false;
                }
                if(getRuntime() - timeAtLaunch >= 0.4){

                    state = LaunchState.CLOSE;
                }
                break;
            case CLOSE:
                state = LaunchState.SPIN_UP;
                LeftLauncherHolderServo.setPosition(LEFT_LAUNCHER_HOLDER_HOLDING_POSITION);
                RightLauncherHolderServo.setPosition(RIGHT_LAUNCHER_HOLDER_HOLDING_POSITION);
                LAUNCHER_RUN = false;
                break;
        }
    }

    public void runLauncherThree() {
        switch(state) {
            case SPIN_UP:
                LauncherMotor.setVelocity(LAUNCHER_FAR_TARGET, AngleUnit.RADIANS);
                if (abs(LauncherMotor.getVelocity(AngleUnit.RADIANS) - LAUNCHER_FAR_TARGET) <= LAUNCH_THRESHOLD) {
                    timeAtLaunch = getRuntime();
                    state = LaunchState.OPEN;
                }
                break;
            case OPEN:
                LeftLauncherHolderServo.setPosition(LEFT_LAUNCHER_HOLDER_LAUNCH_POSITION);
                RightLauncherHolderServo.setPosition(RIGHT_LAUNCHER_HOLDER_LAUNCH_POSITION);
                if(getRuntime() - timeAtLaunch >= 0.1){
                    INTAKE_RUN = true;
                    state = LaunchState.WAIT;
                    timeAtLaunch = getRuntime();
                }
                break;
            case WAIT:
                if(getRuntime() - timeAtLaunch >= 0.15){
                    INTAKE_RUN = false;
                }
                if(getRuntime() - timeAtLaunch >= 0.4){

                    state = LaunchState.CLOSE;
                }
                break;
            case CLOSE:
                state = LaunchState.SPIN_UP_TWO;
                LeftLauncherHolderServo.setPosition(LEFT_LAUNCHER_HOLDER_HOLDING_POSITION);
                RightLauncherHolderServo.setPosition(RIGHT_LAUNCHER_HOLDER_HOLDING_POSITION);
                INTAKE_RUN = true;
                timeAtLaunch = getRuntime();
                break;
            case SPIN_UP_TWO:
                LauncherMotor.setVelocity(LAUNCHER_FAR_TARGET, AngleUnit.RADIANS);
                if(getRuntime() - timeAtLaunch >= 0.1){
                    INTAKE_RUN = false;
                }
                if (abs(LauncherMotor.getVelocity(AngleUnit.RADIANS) - LAUNCHER_FAR_TARGET) <= LAUNCH_THRESHOLD) {
                    timeAtLaunch = getRuntime();
                    state = LaunchState.OPEN_TWO;
                }
                break;
            case OPEN_TWO:
                LeftLauncherHolderServo.setPosition(LEFT_LAUNCHER_HOLDER_LAUNCH_POSITION);
                RightLauncherHolderServo.setPosition(RIGHT_LAUNCHER_HOLDER_LAUNCH_POSITION);
                if(getRuntime() - timeAtLaunch >= 0.1){
                    INTAKE_RUN = true;
                    state = LaunchState.WAIT_TWO;
                    timeAtLaunch = getRuntime();
                }
                break;
            case WAIT_TWO:
                if(getRuntime() - timeAtLaunch >= 0.15){
                    INTAKE_RUN = false;
                }
                if(getRuntime() - timeAtLaunch >= 0.4){
                    state = LaunchState.CLOSE_TWO;
                }
                break;
            case CLOSE_TWO:
                LAUNCHER_RUN_THREE = false;
                LAUNCHER_RUN = true;
                state = LaunchState.SPIN_UP;
                break;
        }
    }

}
