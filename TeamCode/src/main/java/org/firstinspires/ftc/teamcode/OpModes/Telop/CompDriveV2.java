package org.firstinspires.ftc.teamcode.OpModes.Telop;

import static org.firstinspires.ftc.teamcode.aProccedural.Components.LauncherHandServo;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.LauncherMotor;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.initComponents;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.leftFront;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.leftRear;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.rightFront;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.rightRear;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.Idle_Vel;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LastLaunching_Close;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LastLaunching_Far;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.Launcher_close_Vel;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.Launcher_far_Vel;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.Launching_Far;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.Launching_Close;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.TimeOne;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.TimeTwo;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.firing;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.first_intake_Powers;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.intake_reversed;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.intake_stop;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.loading;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.main_intake_Powers;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.second_intake_Powers;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.isIntaking;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.targetVel;


import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.OpModes.Auto.AutoFarLaunching;
import org.firstinspires.ftc.teamcode.aProccedural.Input;

@TeleOp
public class CompDriveV2 extends OpMode {
    ElapsedTime Intake_Time = new ElapsedTime();
    ElapsedTime Launcher_Time = new ElapsedTime();
    Input input = new Input();

    public enum LaunchState {
        IDLE,
        SPIN_UP,
        LOAD_BALL,
        FIRE_BALL,
        LOAD_BALL_TWO,
        FIRE_BALL_TWO,
        LOAD_BALL_THREE,
        FIRE_BALL_THREE,
        STOP_AND_RESET
    }

    LaunchState state = LaunchState.IDLE;


    @Override
    public void init() {
        initComponents(hardwareMap);
        telemetry.speak("Robot is spinning Danger Danger");
        telemetry.update();
    }

    @Override
    public void start() {
        state = LaunchState.IDLE;
    }

    @Override
    public void loop() {

        input.pollGamepad(gamepad1);

        /* State machine kill and reset */
        if (input.back.down()) {
            LauncherHandServo.setPosition(loading);
            LastLaunching_Far = false;
            LastLaunching_Close = false;
            intake_reversed = false;
            Launching_Far = false;
            Launching_Close = true;
            state = LaunchState.IDLE;
        }

        /* Shooting modes */
        if (input.a.down()) { // Select Far
            LastLaunching_Far = true;
            LastLaunching_Close = false;
            Launching_Far = true;
            Launching_Close = false;
        } else if (input.b.down()) { // Select Close
            LastLaunching_Far = false;
            LastLaunching_Close = true;
            Launching_Close = true;
            Launching_Far = false;
        }
        if (LastLaunching_Far){
            Launching_Far = true;
        }
        else if (LastLaunching_Close){
            Launching_Close = true;
        }
        if (!Launching_Far && !Launching_Close) {
            Launching_Close = true;
        }
        else if (!LastLaunching_Far && !LastLaunching_Close){
            Launching_Close = true;
        }
        if (input.right_trigger.down()) {
            state = LaunchState.SPIN_UP;
        }

        /* Intake */
        intake_reversed = input.x.held();
        isIntaking = input.left_trigger.held() || input.left_bumper.held() || input.right_bumper.held();

        if (isIntaking) {
            if (input.left_trigger.held()) main_intake_Powers();
            else if (input.left_bumper.held()) first_intake_Powers();
            else if (input.right_bumper.held()) second_intake_Powers();
        } else {
            intake_stop();
        }
        /* Manual Hand Movements */
        if (input.dpad_up.down()) {
            LauncherHandServo.setPosition(.5);
        } if (input.dpad_down.down()) {
            LauncherHandServo.setPosition(loading);
        }

        /* Intake */

//        intake_reversed = input.x.held();
//
//        if (input.left_trigger.held()) {
//            LauncherHandServo.setPosition(loading);
//            main_intake_Powers();
//        } if (input.left_bumper.held()){
//            LauncherHandServo.setPosition(loading);
//            first_intake_Powers();
//        } if (input.right_bumper.held()){
//            LauncherHandServo.setPosition(loading);
//            second_intake_Powers();
//        } else {
//            intake_stop();
//        }



        /* ---------- Drivetrain ---------- */

        //Drivetrain movement values
        double forward = gamepad1.left_stick_y;  //x
        double strafes = -gamepad1.left_stick_x;  //y
        double rotates = (gamepad1.right_stick_x * .8); //rx

        //Setting Powers
        leftFront.setPower(forward + strafes + rotates);
        rightFront.setPower(forward - strafes - rotates);
        leftRear.setPower(forward - strafes + rotates);
        rightRear.setPower(forward + strafes - rotates);

        telemetry.addLine("Launching state information");
        telemetry.addData("Launching Far", Launching_Far);
        telemetry.addData("Launching Close", Launching_Close);
        telemetry.addData("State", state);
        telemetry.addData("Launcher Velocity: ", LauncherMotor.getVelocity());
        telemetry.addData("Intake Reversed", intake_reversed);
        telemetry.update();

        /* --- Consolidated Launching Logic --- */
        targetVel = Launching_Far ? Launcher_far_Vel : Launcher_close_Vel;

        if (Launching_Far || Launching_Close) {
            switch (state) {
                case IDLE:
                    LauncherMotor.setVelocity(Idle_Vel);
                    break;

                case SPIN_UP:
                    LauncherMotor.setVelocity(targetVel);
                    LauncherHandServo.setPosition(loading);
                    Launcher_Time.reset();
                    state = LaunchState.FIRE_BALL;
                    break;

                case FIRE_BALL:
                    LauncherMotor.setVelocity(targetVel);
                    // Use a 90-95% threshold so it actually fires even if the motor is slightly slow
                    if (LauncherMotor.getVelocity() >= (targetVel * 0.95)) {
                        LauncherHandServo.setPosition(firing);
                        if (Launcher_Time.seconds() >= TimeOne) {
                            Intake_Time.reset();
                            state = LaunchState.LOAD_BALL_TWO;
                        }
                    }
                    break;

                case LOAD_BALL_TWO:
                    LauncherHandServo.setPosition(loading);
                    if (Intake_Time.seconds() >= 1) {
                        main_intake_Powers(); // Assuming this moves balls to the launcher
                        if (Intake_Time.seconds() >= 4) {
                            Launcher_Time.reset();
                            state = LaunchState.FIRE_BALL_TWO;
                        }
                    }
                    break;

                case FIRE_BALL_TWO:
                    intake_stop();
                    LauncherMotor.setVelocity(targetVel);
                    if (LauncherMotor.getVelocity() >= (targetVel * 0.95)) {
                        LauncherHandServo.setPosition(firing);
                        if (Launcher_Time.seconds() >= TimeOne) {
                            Intake_Time.reset();
                            state = LaunchState.LOAD_BALL_THREE;
                        }
                    }
                    break;

                case LOAD_BALL_THREE:
                    LauncherHandServo.setPosition(loading);
                    if (Intake_Time.seconds() >= 1) {
                        main_intake_Powers(); // Assuming this moves balls to the launcher
                        if (Intake_Time.seconds() >= 4) {
                            Launcher_Time.reset();
                            state = LaunchState.FIRE_BALL_THREE;
                        }
                    }
                    break;

                case FIRE_BALL_THREE:
                    intake_stop();
                    LauncherMotor.setVelocity(targetVel);
                    if (LauncherMotor.getVelocity() >= (targetVel * 0.95)) {
                        LauncherHandServo.setPosition(firing);
                        if (Launcher_Time.seconds() >= TimeOne) {
                            // Reset everything
                            Launching_Far = false;
                            Launching_Close = false;
                            state = LaunchState.STOP_AND_RESET;
                        }
                    }
                    break;
                case STOP_AND_RESET:
                    LauncherHandServo.setPosition(loading);
                    state = LaunchState.IDLE;
                    break;
            }
        } else {
            // Default state when not shooting
            LauncherMotor.setVelocity(Idle_Vel);
        }
    }
}
