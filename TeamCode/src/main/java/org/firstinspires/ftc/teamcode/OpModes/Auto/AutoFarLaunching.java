package org.firstinspires.ftc.teamcode.OpModes.Auto;

import static org.firstinspires.ftc.teamcode.aProccedural.Components.LauncherHandServo;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.LauncherMotor;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.leftFront;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.leftRear;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.rightFront;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.rightRear;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.Idle_Vel;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.Launcher_close_Vel;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.Launcher_far_Vel;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.TimeTwo;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.firing;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.intake_stop;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.loading;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.main_intake_Powers;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.aProccedural.Components;

@Autonomous
public class AutoFarLaunching extends OpMode {
    double targetVel = Launcher_far_Vel;
    ElapsedTime Intake_Time = new ElapsedTime();
    ElapsedTime Launcher_Time = new ElapsedTime();
    ElapsedTime Strafe_Time = new ElapsedTime();
    double timeAtStart;
    public enum LaunchState {
        IDLE,
        SPIN_UP,
        LOAD_BALL,
        FIRE_BALL,
        LOAD_BALL_TWO,
        FIRE_BALL_TWO,
        LOAD_BALL_THREE,
        FIRE_BALL_THREE,
        DRIVE_FORWARD,
        END
    }

     LaunchState state = LaunchState.IDLE;

    @Override
    public void init() {
        Components.initComponents(hardwareMap);
        LauncherHandServo.setPosition(loading);
    }
    @Override
    public void start() {
        LauncherMotor.setVelocity(targetVel);
        state = LaunchState.IDLE;
    }


    @Override
    public void loop() {
        telemetry.addLine("Launching state information");
        telemetry.addData("State", state);
        telemetry.addData("Launcher Velocity: ", LauncherMotor.getVelocity());
        telemetry.addData("Intake time", Intake_Time.seconds());
        telemetry.addData("Launcher time", Launcher_Time.seconds());
        telemetry.update();

            switch (state) {
                case IDLE:
                    Intake_Time.reset();
                    state = LaunchState.SPIN_UP;

                case SPIN_UP:
                    LauncherMotor.setVelocity(targetVel);
                    LauncherHandServo.setPosition(loading);
                    if (Intake_Time.seconds() >= 2) {
                        Launcher_Time.reset();
                        state = LaunchState.FIRE_BALL;
                    }
                    break;

                case FIRE_BALL:
                    LauncherMotor.setVelocity(targetVel);
                    // Use a 90-95% threshold so it actually fires even if the motor is slightly slow
                    if (LauncherMotor.getVelocity() >= (targetVel * 0.95)) {
                        LauncherHandServo.setPosition(firing);
                        if (Launcher_Time.seconds() >= TimeTwo) {
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
                        if (Launcher_Time.seconds() >= TimeTwo) {
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
                        if (Launcher_Time.seconds() >= TimeTwo) {
                            // Reset everything
                            Strafe_Time.reset();
                            state = LaunchState.DRIVE_FORWARD;
                        }
                    }
                    break;
                case DRIVE_FORWARD:
                    LauncherMotor.setVelocity(Idle_Vel);
                    leftFront.setPower(-.2);
                    rightFront.setPower(-.2);
                    leftRear.setPower(-.2);
                    rightRear.setPower(-.2);
                    Strafe_Time.reset();
                    state = LaunchState.END;
                    break;
                case END:
                    if (Strafe_Time.seconds() >= TimeTwo) {
                        leftFront.setPower(0);
                        rightFront.setPower(0);
                        leftRear.setPower(0);
                        rightRear.setPower(0);
                    }
                    break;
            }
        }
}

