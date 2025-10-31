package org.firstinspires.ftc.teamcode.OpModes.Auto.Far;

import static org.firstinspires.ftc.teamcode.Math.WebcamUtil.aprilTagTelemetry;
import static org.firstinspires.ftc.teamcode.Math.WebcamUtil.getTagYaw;
import static org.firstinspires.ftc.teamcode.Math.WebcamUtil.initWebcamFinder;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.IntakeMotor;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.LauncherMotor;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.LauncherFingerServo;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.LeftSideFeedRoller;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.RightSideFeedRoller;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.leftBack;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.leftFront;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.rightBack;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.rightFront;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.webcam;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.INTAKE_POWER;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCHER_FAR_TARGET;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCH_THRESHOLD;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCHER_FINGER_UP_POS;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCHER_FINGER_DOWN_POS;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.aProccedural.Components;

@Autonomous
public class BLUE_FarSensingStateAuto extends OpMode {

    enum AutoState {
        ALIGN_AND_SPIN_UP,
        SHOOT_ONE,
        SHOOT_TWO,
        SHOOT_THREE,
        DRIVE_AND_SPIN_DOWN,
        END
    }
    AutoState autoState = AutoState.ALIGN_AND_SPIN_UP;
    enum ShotState {
        SPIN_UP,
        UP,
        DOWN,
        INTAKE_RESET,
        END
    }
    ShotState shotState = ShotState.SPIN_UP;

    @Override
    public void init() {
        //initWebcamFinder("BLUE");
        Components.initComponents(hardwareMap);
        telemetry.addLine("READY TO START");
    }
    @Override
    public void init_loop() {
        //aprilTagTelemetry(telemetry);
        telemetry.update();
    }

    double tempAutoTime;
    @Override
    public void start() {
        tempAutoTime = getRuntime();
        autoState = AutoState.ALIGN_AND_SPIN_UP;
        shotState = ShotState.SPIN_UP;
    }
    @Override
    public void loop() {
        telemetry.addData("Auto State: ", autoState);
        telemetry.addData("Shot State: ", shotState);
        telemetry.addData("Time since last temp auto time: ", getRuntime() - tempAutoTime);
        switch (autoState){
            case ALIGN_AND_SPIN_UP:
                LauncherMotor.setVelocity(LAUNCHER_FAR_TARGET, AngleUnit.RADIANS);
                if(/*alignWithWebcam("BLUE") ||*/ (getRuntime() - tempAutoTime >= 2)){
                    autoState = AutoState.SHOOT_ONE;
                    tempAutoTime = getRuntime();
                }
                break;
            case SHOOT_ONE:
                shoot();
                if(shotState == ShotState.END){
                    autoState = AutoState.SHOOT_TWO;
                }
                telemetry.addLine("wowee");
                break;
            case SHOOT_TWO:
                shoot();
                if(shotState == ShotState.END){
                    autoState = AutoState.SHOOT_THREE;
                }
                break;
            case SHOOT_THREE:
                shoot();
                if(shotState == ShotState.END){
                    autoState = AutoState.DRIVE_AND_SPIN_DOWN;
                    tempAutoTime = getRuntime();
                }
                break;
            case DRIVE_AND_SPIN_DOWN:
                IntakeMotor.setPower(0);
                LauncherMotor.setPower(0);
                rightFront.setPower(.5);
                leftFront.setPower(.5);
                leftBack.setPower(.5);
                rightBack.setPower(.5);
                if((getRuntime() - tempAutoTime) >= 1) {
                    autoState = AutoState.END;
                }
                break;
            case END:
                LauncherMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                rightFront.setPower(0);
                leftFront.setPower(0);
                leftBack.setPower(0);
                rightBack.setPower(0);
                requestOpModeStop();
        }

        //aprilTagTelemetry(telemetry);
        telemetry.update();
    }

    double shotTempTime;

    public void shoot() {
        switch(shotState){
            case SPIN_UP:

                telemetry.addLine("wowee\n" +
                        "                telemetry.addLine(\"wowee\");");
                LauncherMotor.setVelocity(LAUNCHER_FAR_TARGET, AngleUnit.RADIANS);
                if(Math.abs(LauncherMotor.getVelocity(AngleUnit.RADIANS) - LAUNCHER_FAR_TARGET) <= LAUNCH_THRESHOLD){
                    shotState = ShotState.UP;
                    shotTempTime = getRuntime();
                }
                break;
            case UP:
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
                if(getRuntime() - shotTempTime >= 0.4){
                    shotState = ShotState.DOWN;
                    shotTempTime = getRuntime();
                }
                break;
            case DOWN:
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
                if(getRuntime() - shotTempTime >= 0.1){
                    LeftSideFeedRoller.setPower(1);
                    RightSideFeedRoller.setPower(1);
                }
                if(getRuntime() - shotTempTime >= 0.2) {
                    shotState = ShotState.INTAKE_RESET;
                    shotTempTime = getRuntime();
                }
                break;
            case INTAKE_RESET:
                IntakeMotor.setPower(INTAKE_POWER);
                if(getRuntime() - shotTempTime >= 0.2){
                    shotState = ShotState.END;
                    shotTempTime = getRuntime();
                }
                break;
            case END:
                IntakeMotor.setPower(0);
                LeftSideFeedRoller.setPower(0);
                RightSideFeedRoller.setPower(0);
                shotState = ShotState.SPIN_UP;
                break;
        }
    }

    //todo tune
    public static boolean alignWithWebcam(String color) {
        switch (color){
            case("BLUE"):
                if(getTagYaw() >= 20){
                    leftBack.setPower(0.4);
                    rightBack.setPower(-0.4);
                    leftFront.setPower(0.4);
                    rightFront.setPower(-0.4);
                    return false;
                } else if(getTagYaw() <= -20){
                    leftBack.setPower(-0.4);
                    rightBack.setPower(0.4);
                    leftFront.setPower(-0.4);
                    rightFront.setPower(0.4);
                    return false;
                } else {
                    leftBack.setPower(0);
                    rightBack.setPower(0);
                    leftFront.setPower(0);
                    rightFront.setPower(0);
                    return true;
                }
            case("RED"):
                if(getTagYaw() <= -20){
                    leftBack.setPower(0.4);
                    rightBack.setPower(-0.4);
                    leftFront.setPower(0.4);
                    rightFront.setPower(-0.4);
                    return false;
                } else if(getTagYaw() >= 20){
                    leftBack.setPower(-0.4);
                    rightBack.setPower(0.4);
                    leftFront.setPower(-0.4);
                    rightFront.setPower(0.4);
                    return false;
                } else {
                    leftBack.setPower(0);
                    rightBack.setPower(0);
                    leftFront.setPower(0);
                    rightFront.setPower(0);
                    return true;
                }
        }
        return false;
    }
}
