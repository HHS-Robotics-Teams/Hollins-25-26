package org.firstinspires.ftc.teamcode.OpModes.Telop;

import static org.firstinspires.ftc.teamcode._Proccedural.Components.intake;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.intakeFeeder;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.launcher;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.leftFeeder;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.leftFront;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.leftRear;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.rightFeeder;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.rightFront;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.rightRear;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.FEED_TIME_SECONDS;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.FULL_SPEED;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_MIN_VELOCITY;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_TARGET_VELOCITY;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.STOP_SPEED;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode._Proccedural.Components;
import org.firstinspires.ftc.teamcode._Proccedural.Input;

@TeleOp
public class CompDriveV3 extends OpMode {
    ElapsedTime feederTimer = new ElapsedTime();
    Input input = new Input();

    enum LaunchStateTelop {
        IDLE,
        SPIN_UP,
        LAUNCH,
        LAUNCHING,
    }

    LaunchStateTelop launchState;


    @Override
    public void init() {
        Components.initComponents(hardwareMap);
        launchState = LaunchStateTelop.IDLE;

    }

    @Override
    public void loop() {

        input.pollGamepad(gamepad1);

        //intake stuff
        if (input.left_bumper.held()) {
            intake.setPower(0.65);
        } else {
            intake.setPower(0);
        }
        if (input.a.down()) {
            if(intakeFeeder.getPosition() != 0){
                intakeFeeder.setPosition(0);
            } else {
                intakeFeeder.setPosition(0.65);
            }
        } else {

        }

        /*
         * Here we give the user control of the speed of the launcher motor without automatically
         * queuing a shot.
         */
        if (gamepad1.y) {
            launcher.setVelocity(LAUNCHER_TARGET_VELOCITY);
        } else if (gamepad1.b) { // stop flywheel
            launcher.setVelocity(STOP_SPEED);
        }

        /*
         * Now we call our "Launch" function.
         */
        launch(gamepad1.rightBumperWasPressed());

        double y = gamepad1.left_stick_y; // Remember, Y stick value is reversed
        double x = -gamepad1.left_stick_x * 1.1; // Counteract imperfect strafing
        double rx = gamepad1.right_stick_x;

        // Denominator is the largest motor power (absolute value) or 1
        // This ensures all the powers maintain the same ratio,
        // but only if at least one is out of the range [-1, 1]
        double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);

        double frontLeftPower = (y + x + rx) / denominator;
        double backLeftPower = (y - x + rx) / denominator;
        double frontRightPower = (y - x - rx) / denominator;
        double backRightPower = (y + x - rx) / denominator;

        //todo add slowdown code

        leftFront.setPower(frontLeftPower);
        leftRear.setPower(backLeftPower);
        rightFront.setPower(frontRightPower);
        rightRear.setPower(backRightPower);

        /*
         * Show the state and motor powers
         */
        telemetry.addData("State", launchState);
        telemetry.addData("motorSpeed", launcher.getVelocity());

    }

    /*
     * Code to run ONCE after the driver hits STOP
     */

/*
    @Override
    public void stop() {
        requestOpModeStop();
    }
    */

    void launch(boolean shotRequested) {
        switch (launchState) {
            case IDLE:
                if (shotRequested) {
                    launchState = LaunchStateTelop.SPIN_UP;
                }
                break;
            case SPIN_UP:
                launcher.setVelocity(LAUNCHER_TARGET_VELOCITY);
                if (launcher.getVelocity() > LAUNCHER_MIN_VELOCITY) {
                    launchState = LaunchStateTelop.LAUNCH;
                }
                break;
            case LAUNCH:
                leftFeeder.setPower(FULL_SPEED);
                rightFeeder.setPower(FULL_SPEED);
                feederTimer.reset();
                launchState = LaunchStateTelop.LAUNCHING;
                break;
            case LAUNCHING:
                if (feederTimer.seconds() > FEED_TIME_SECONDS) {
                    launchState = LaunchStateTelop.IDLE;
                    leftFeeder.setPower(STOP_SPEED);
                    rightFeeder.setPower(STOP_SPEED);
                }
                break;
        }
    }
}

