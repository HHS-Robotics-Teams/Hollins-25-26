package org.firstinspires.ftc.teamcode.OpModes.Telop;

import static org.firstinspires.ftc.teamcode.aProccedural.Components.LauncherHandServo;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.LauncherMotor;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.initComponents;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.intakeMotor;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.intakeSecondRollerMotor;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.leftFeedRoller;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.leftFront;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.leftRear;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.rightFeedRoller;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.rightFront;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.rightRear;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.Idle_Vel;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LastLaunching_Close;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LastLaunching_Far;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.Launcher_close_Vel;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.Launcher_far_Vel;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.Launching_Close;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.Launching_Far;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.first_intake_Powers;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.intake_reversed;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.intake_stop;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.isIntaking;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.loading;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.main_intake_Powers;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.second_intake_Powers;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.targetVel;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.aProccedural.Input;

@TeleOp
public class CompDriveV3 extends OpMode {
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
        LauncherHandServo.setPosition(loading);
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
        if (LastLaunching_Far) {
            Launching_Far = true;
        } else if (LastLaunching_Close) {
            Launching_Close = true;
        }
        if (!Launching_Far && !Launching_Close) {
            Launching_Close = true;
        } else if (!LastLaunching_Far && !LastLaunching_Close) {
            Launching_Close = true;
        }
        if (input.right_trigger.down()) {
            state = LaunchState.SPIN_UP;
        }

        /* Intake */

        if (input.left_trigger.down()) main_intake_Powers();
        else if (input.left_bumper.down()) first_intake_Powers();
        else if (input.right_bumper.down()) second_intake_Powers();
        else if (input.left_trigger.up()||input.left_bumper.up()||input.right_bumper.up()){
            intake_stop();
        }

        intake_reversed = input.x.held();


            /* Manual Hand Movements */
            if (input.dpad_up.down()) {
                rightFeedRoller.setPower(1);
                leftFeedRoller.setPower(1);
            }
            if (input.dpad_down.down()) {
                intake_stop();
            }




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
            telemetry.addData("Intake Running", isIntaking);
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
                        Launcher_Time.reset();
                        state = LaunchState.FIRE_BALL;
                        break;

                    case FIRE_BALL:
                        LauncherMotor.setVelocity(targetVel);
                        LauncherHandServo.setPosition(.8);
                        // Use a 90-95% threshold so it actually fires even if the motor is slightly slow
                        main_intake_Powers();
                        rightFeedRoller.setPower(1);
                        leftFeedRoller.setPower(1);
                        Intake_Time.reset();
                        state = LaunchState.LOAD_BALL_TWO;

                        break;
                    case LOAD_BALL_TWO:
                        if (Intake_Time.seconds() >= 5) {
                            intake_stop();
                            LauncherHandServo.setPosition(loading);
                            rightFeedRoller.setPower(0);
                            leftFeedRoller.setPower(0);
                            Launching_Far = false;
                            Launching_Close = false;
                            isIntaking = true;
                            state = LaunchState.STOP_AND_RESET;
                        }
                        break;

                    case STOP_AND_RESET:
                        state = LaunchState.IDLE;
                        break;
                }
            } else {
                // Default state when not shooting
                LauncherMotor.setVelocity(Idle_Vel);
            }
        }
    }

