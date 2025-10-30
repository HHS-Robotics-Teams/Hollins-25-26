package org.firstinspires.ftc.teamcode.OpModes.Auto;

import static org.firstinspires.ftc.teamcode.aProccedural.Components.IntakeMotor;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.LauncherMotor;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.LauncherFingerServo;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.leftBack;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.leftFront;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.rightBack;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.rightFront;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.INTAKE_POWER;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCHED_THRESHOLD;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCHER_FAR_TARGET;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCH_THRESHOLD;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCHER_FINGER_UP_POS;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCHER_FINGER_DOWN_POS;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.numShot;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.aProccedural.Components;

@Autonomous
public class ScrimmageAuto extends OpMode {

    enum Autostate{
        WAIT_FOR_START,
        SPIN_UP,
        SHOOT_FIRST,
        RESET_SHOT,
        SHOOT_SECOND,
        RESET_SECOND,
        SHOOT_THIRD,
        DRIVE,
        STOP
    }
    Autostate autostate = Autostate.WAIT_FOR_START;

    @Override
    public void init() {
        Components.initComponents(hardwareMap);
        numShot = 0;
    }

    @Override
    public void start() {
        autostate = Autostate.SPIN_UP;
        LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
    }

    double timeAtShot;
    double timeAfterShot;
    double timeStartDrive;
    double shootTime = 0.15;

    @Override
    public void loop() {
        switch (autostate){
            case SPIN_UP:
                LauncherMotor.setPower(1);
                if(getRuntime() - timeAtShot >= shootTime * 2){IntakeMotor.setPower(0);}
                if(Math.abs(LauncherMotor.getVelocity(AngleUnit.RADIANS) - LAUNCHER_FAR_TARGET) <= LAUNCH_THRESHOLD){
                    autostate = Autostate.SHOOT_FIRST;
                    numShot++;
                    timeAtShot = getRuntime();
                    LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
                    IntakeMotor.setPower(INTAKE_POWER);
                }
                break;
            case SHOOT_FIRST:
                LauncherMotor.setPower(.5);
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
                IntakeMotor.setPower(0.95);
                if(LauncherMotor.getVelocity(AngleUnit.RADIANS) <= LAUNCHED_THRESHOLD){
                    autostate = Autostate.RESET_SHOT;
                    timeAfterShot = getRuntime();
                }
                break;
            case RESET_SHOT:
                IntakeMotor.setPower(0);
                LauncherMotor.setPower(.8);
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
                if(Math.abs(LauncherMotor.getVelocity(AngleUnit.RADIANS) - LAUNCHER_FAR_TARGET) <= LAUNCH_THRESHOLD){
                    autostate = Autostate.SHOOT_SECOND;
                    numShot++;
                    timeAtShot = getRuntime();
                    LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
                }
                break;
            case SHOOT_SECOND:
                LauncherMotor.setPower(0.5);
                IntakeMotor.setPower(INTAKE_POWER);
                if(getRuntime() - timeAtShot >= 1){
                    autostate = Autostate.DRIVE;
                    timeAfterShot = getRuntime();
                    timeStartDrive = getRuntime();
                }
                break;
            case RESET_SECOND:
                IntakeMotor.setPower(0);
                LauncherMotor.setPower(1);
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
                if(Math.abs(LauncherMotor.getVelocity(AngleUnit.RADIANS) - LAUNCHER_FAR_TARGET - 0.1) <= LAUNCH_THRESHOLD * 3){
                    autostate = Autostate.SHOOT_SECOND;
                    numShot++;
                    timeAtShot = getRuntime();
                }
                break;
            case SHOOT_THIRD:
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
                IntakeMotor.setPower(INTAKE_POWER);
                if(getRuntime() - timeAtShot >= 1){
                    autostate = Autostate.DRIVE;
                    timeStartDrive = getRuntime();
                }
                break;
            case DRIVE:
                IntakeMotor.setPower(0);
                LauncherMotor.setPower(0);
                rightFront.setPower(-.5);
                leftFront.setPower(-.5);
                leftBack.setPower(-.5);
                rightBack.setPower(-.5);
                if((getRuntime() - timeStartDrive) >= 1) {
                    autostate = Autostate.STOP;
                }
                break;
            case STOP:
                rightFront.setPower(0);
                leftFront.setPower(0);
                leftBack.setPower(0);
                rightBack.setPower(0);
                LauncherMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                requestOpModeStop();
                break;
        }
        telemetry.addData("AutoState: ", autostate);
        telemetry.addData("numShot: ", numShot);
    }
}