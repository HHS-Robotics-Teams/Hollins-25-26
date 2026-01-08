package org.firstinspires.ftc.teamcode.OpModes.Telop;


import static org.firstinspires.ftc.teamcode.OpModes.Telop.CompDriveV2.LaunchState.SPIN_UP;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.LauncherHandServo;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.LauncherMotor;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.initComponents;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.intakeMotor;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.intakeSecondRollerMotor;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.leftFront;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.leftRear;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.rightFront;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.rightRear;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.Idle_Vel;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.Launcher_close_Vel;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.Launcher_far_Vel;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.Launching_Far;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.Launching_Close;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.firing;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.first_intake_Powers;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.intake_reversed;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.intake_stop;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.loading;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.main_intake_Powers;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.second_intake_Powers;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

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
        FIRE_BALL_THREE
    }

    LaunchState state = LaunchState.IDLE;


    @Override
    public void init() {
        initComponents(hardwareMap);
        telemetry.speak("Robot is spinning Danger Danger");
        telemetry.update();
    }

    @Override
    public void loop() {

        input.pollGamepad(gamepad1);
        /* State machine kill and reset */
        if (input.back.down()){
            LauncherHandServo.setPosition(loading);
            intake_reversed = false;
            Launching_Far = false;
            Launching_Close = false;
            state = LaunchState.IDLE;
        }

        /* Shooting modes */
        if (input.a.down()) { // far trajectory
            Launching_Far = true;
            Launching_Close = false;
        } else { // close trajectory if (input.b.down())
            Launching_Close = true;
            Launching_Far = false;
        }
        if (input.right_trigger.down() && Launching_Far) {
            state = SPIN_UP;
        } else if (input.right_trigger.down() && Launching_Close) {
            state = SPIN_UP;
        }

        /* Intake */
        if (input.x.down()){
            intake_reversed = true;
        }
        if (input.left_trigger.down()) {
            main_intake_Powers();
        } if (input.left_bumper.down()){
            first_intake_Powers();
        } if (input.right_bumper.down()){
            second_intake_Powers();
        } else {
            intake_stop();
        }
        /* Human Player Loading */ //todo might not be necessary
        if (input.x.down()){
            intakeSecondRollerMotor.setPower(1);
        }


        /* ---------- Drivetrain ---------- */

        //Drivetrain movement values
        double forward = -gamepad1.left_stick_y;  //x
        double strafes = gamepad1.left_stick_x;  //y
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

        /* Launching States */
        if (Launching_Far) {
            switch (state) {
                case IDLE:
                    LauncherMotor.setVelocity(Idle_Vel);
                    break;
                case SPIN_UP:
                    LauncherMotor.setVelocity(Launcher_far_Vel);
                    LauncherHandServo.setPosition(loading);
                    intakeMotor.setPower(1);
                    intakeSecondRollerMotor.setPower(1);
                    Intake_Time.reset();
                    state = LaunchState.LOAD_BALL;
                    ;
                    break;
                case LOAD_BALL:
                    if (Intake_Time.seconds() >= 0.2) {
                        intakeMotor.setPower(0);
                        intakeSecondRollerMotor.setPower(0);
                        Launcher_Time.reset();
                        state = LaunchState.FIRE_BALL;
                    }
                    break;
                case FIRE_BALL:
                    LauncherMotor.setVelocity(Launcher_far_Vel);
                    if (LauncherMotor.getVelocity() >= Launcher_far_Vel) {
                        LauncherHandServo.setPosition(firing);
                        if (Launcher_Time.seconds() >= 0.5) {
                            Intake_Time.reset();
                            state = LaunchState.LOAD_BALL_TWO;
                        }
                    }
                    break;
                case LOAD_BALL_TWO:
                    LauncherHandServo.setPosition(loading);
                    intakeMotor.setPower(1);
                    intakeSecondRollerMotor.setPower(1);
                    intakeSecondRollerMotor.setPower(1);
                    if (Intake_Time.seconds() >= 1) {
                        state = LaunchState.FIRE_BALL_TWO;
                    }
                    break;
                case FIRE_BALL_TWO:
                    intakeMotor.setPower(0);
                    intakeSecondRollerMotor.setPower(0);
                    LauncherMotor.setVelocity(Launcher_far_Vel);
                    if (LauncherMotor.getVelocity() >= Launcher_far_Vel) {
                        LauncherHandServo.setPosition(firing);
                        if (Launcher_Time.seconds() >= 0.5) {
                            state = LaunchState.LOAD_BALL_THREE;
                        }
                    }
                case LOAD_BALL_THREE:
                    LauncherHandServo.setPosition(loading);
                    intakeMotor.setPower(1);
                    intakeSecondRollerMotor.setPower(1);
                    if (Intake_Time.seconds() >= 1) {
                        Launcher_Time.reset();
                        state = LaunchState.FIRE_BALL_THREE;
                    }
                    break;
                case FIRE_BALL_THREE:
                    intakeMotor.setPower(0);
                    intakeSecondRollerMotor.setPower(0);
                    LauncherMotor.setVelocity(Launcher_far_Vel);
                    if (LauncherMotor.getVelocity() <= Launcher_far_Vel) {
                        LauncherHandServo.setPosition(firing);
                        if (Launcher_Time.seconds() >= 0.5) {
                            Launching_Far = false;
                            state = LaunchState.IDLE;
                        }
                    }
                    break;
            }
        }
        if (Launching_Close) {
            switch (state) {
                case IDLE:
                    LauncherMotor.setVelocity(Idle_Vel);
                    break;
                case SPIN_UP:
                    LauncherMotor.setVelocity(Launcher_close_Vel);
                    LauncherHandServo.setPosition(loading);
                    intakeMotor.setPower(1);
                    intakeSecondRollerMotor.setPower(1);
                    Intake_Time.reset();
                    state = LaunchState.LOAD_BALL;
                    ;
                    break;
                case LOAD_BALL:
                    if (Intake_Time.seconds() >= 0.2) {
                        intakeMotor.setPower(0);
                        intakeSecondRollerMotor.setPower(0);
                        Launcher_Time.reset();
                        state = LaunchState.FIRE_BALL;
                    }
                    break;
                case FIRE_BALL:
                    LauncherMotor.setVelocity(Launcher_close_Vel);
                    if (LauncherMotor.getVelocity() >= Launcher_close_Vel) {
                        LauncherHandServo.setPosition(firing);
                        if (Launcher_Time.seconds() >= 0.5) {
                            Intake_Time.reset();
                            state = LaunchState.LOAD_BALL_TWO;
                        }
                    }
                    break;
                case LOAD_BALL_TWO:
                    LauncherHandServo.setPosition(loading);
                    intakeMotor.setPower(1);
                    intakeSecondRollerMotor.setPower(1);
                    intakeSecondRollerMotor.setPower(1);
                    if (Intake_Time.seconds() >= 1) {
                        state = LaunchState.FIRE_BALL_TWO;
                    }
                    break;
                case FIRE_BALL_TWO:
                    intakeMotor.setPower(0);
                    intakeSecondRollerMotor.setPower(0);
                    LauncherMotor.setVelocity(Launcher_close_Vel);
                    if (LauncherMotor.getVelocity() >= Launcher_close_Vel) {
                        LauncherHandServo.setPosition(firing);
                        if (Launcher_Time.seconds() >= 0.5) {
                            state = LaunchState.LOAD_BALL_THREE;
                        }
                    }
                case LOAD_BALL_THREE:
                    LauncherHandServo.setPosition(loading);
                    intakeMotor.setPower(1);
                    intakeSecondRollerMotor.setPower(1);
                    if (Intake_Time.seconds() >= 1) {
                        Launcher_Time.reset();
                        state = LaunchState.FIRE_BALL_THREE;
                    }
                    break;
                case FIRE_BALL_THREE:
                    intakeMotor.setPower(0);
                    intakeSecondRollerMotor.setPower(0);
                    LauncherMotor.setVelocity(Launcher_close_Vel);
                    if (LauncherMotor.getVelocity() <= Launcher_close_Vel) {
                        LauncherHandServo.setPosition(firing);
                        if (Launcher_Time.seconds() >= 0.5) {
                            Launching_Close = false;
                            state = LaunchState.IDLE;
                        }
                    }
                    break;
            }
        }
    }
}


