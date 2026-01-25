package org.firstinspires.ftc.teamcode.OpModes.Auto;

import static org.firstinspires.ftc.teamcode._Proccedural.Components.intake;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.launcher;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.leftFeeder;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.leftFront;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.leftRear;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.rightFeeder;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.rightFront;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.rightRear;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.FEED_TIME_SECONDS;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.FULL_SPEED;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LeftIntakeFeeder;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.RightIntakeFeeder;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_MIN_VELOCITY;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_TARGET_VELOCITY;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.STOP_SPEED;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.teamcode._Proccedural.Components;

import org.firstinspires.ftc.teamcode.OpModes.Telop.CompDriveV3;


@Autonomous
public class BasicMoveShootAuto extends OpMode {
    double LAUNCHER_TARGET_VELOCITY = 1325;
    double LAUNCHER_MIN_VELOCITY = 1175;
    ElapsedTime Intake_Time = new ElapsedTime();
    ElapsedTime Launcher_Time = new ElapsedTime();
    ElapsedTime Strafe_Time = new ElapsedTime();
    double timeAtStart;
    public enum LaunchState {
        IDLE,
        SPIN_UP,
        LAUNCH,
        LAUNCHING,
        LAUNCHING_HOPPER,
        STRAFE,
        END
    }

    LaunchState state = LaunchState.IDLE;

    @Override
    public void init() {
        Components.initComponents(hardwareMap);

    }
    @Override
    public void start() {

        state = LaunchState.IDLE;
    }


    @Override
    public void loop() {
//      sends information to the driver station
        telemetry.addLine("Launching state information");
        telemetry.addData("State", state);
        telemetry.addData("Launcher Velocity: ", launcher.getVelocity());
        telemetry.addData("Intake time", Intake_Time.seconds());
        telemetry.addData("Launcher time", Launcher_Time.seconds());
        telemetry.update();

        // Launching Artifacts Loop
        switch (state) {
            case IDLE:
                if (getRuntime() > (timeAtStart + 1.25)) { //todo tune this value
                    leftFront.setPower(0);
                    rightFront.setPower(0);
                    leftRear.setPower(0);
                    rightRear.setPower(0);
                    launcher.setPower(0);
                    state = LaunchState.SPIN_UP;
                    Launcher_Time.reset();
                }
                break;

            case SPIN_UP:
                launcher.setVelocity(LAUNCHER_TARGET_VELOCITY);
                if (launcher.getVelocity() > LAUNCHER_MIN_VELOCITY) {
                    state = LaunchState.LAUNCHING;
                    Intake_Time.reset();
                }
                break;



            case LAUNCHING:
                if (Intake_Time.seconds() > FEED_TIME_SECONDS) {
                    leftFeeder.setPower(FULL_SPEED);
                    rightFeeder.setPower(FULL_SPEED);
                    intake.setPower(-0.85);
                    state = LaunchState.LAUNCHING;
//
                    if(Intake_Time.seconds() >= 3){
                        state = LaunchState.LAUNCHING_HOPPER;
                    }

                    if(Launcher_Time.seconds() >= 20){
                        state = LaunchState.STRAFE;
                    }

                }
                break;

            case LAUNCHING_HOPPER:
                if (Intake_Time.seconds() > FEED_TIME_SECONDS) {
                    LeftIntakeFeeder.setPower(FULL_SPEED);
                    RightIntakeFeeder.setPower(-FULL_SPEED);
                    intake.setPower(0);

                }
                    if (Intake_Time.seconds() > 5) {
                        intake.setPower(0.85);
                        Intake_Time.reset();
                        state = LaunchState.LAUNCHING;
                }
                break;

            case STRAFE:
                launcher.setVelocity(500);
                rightFeeder.setPower(0);
                leftFeeder.setPower(0);
                intake.setPower(0);
                LeftIntakeFeeder.setPower(0);
                RightIntakeFeeder.setPower(0);
            //  movement code TODO: Duc Code and tune
                leftFront.setPower(-.5);
                rightFront.setPower(.5);
                leftRear.setPower(.5);
                rightRear.setPower(-.5);
                Strafe_Time.reset();
                state = LaunchState.END;
                break;

            case END:
                if (Strafe_Time.seconds() >= 4) {
                    leftFront.setPower(0);
                    rightFront.setPower(0);
                    leftRear.setPower(0);
                    rightRear.setPower(0);
                }
                break;
        }
    }
}
