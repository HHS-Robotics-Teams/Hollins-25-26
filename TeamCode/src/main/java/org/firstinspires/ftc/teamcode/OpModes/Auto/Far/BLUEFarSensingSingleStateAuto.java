package org.firstinspires.ftc.teamcode.OpModes.Auto.Far;

import static org.firstinspires.ftc.teamcode.Math.WebcamUtil.getTagYaw;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.IntakeMotor;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.LauncherMotor;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.LauncherFingerServo;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.LeftSideFeedRoller;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.RightSideFeedRoller;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.leftBack;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.leftFront;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.rightBack;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.rightFront;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.INTAKE_POWER;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCHER_FAR_TARGET;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCHER_IDLE;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCH_THRESHOLD;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCHER_FINGER_UP_POS;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCHER_FINGER_DOWN_POS;

import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.aProccedural.Components;

@Autonomous
public class BLUEFarSensingSingleStateAuto extends OpMode {

    enum AutoState {
        ALIGN_AND_SPIN_UP,
        SPIN_UP,
        UP,
        DOWN,
        INTAKE_RESET,
        SPIN_UP_TWO,
        UP_TWO,
        DOWN_TWO,
        INTAKE_RESET_TWO,
        SPIN_UP_THREE,
        UP_THREE,
        DOWN_THREE,
        END_SHOT_SEQ_ONE,
        MOVE_TO_INTAKE,
        INTAKE_START,
        INTAKE_END,
        MOVE_TO_SHOT,
        ALIGN_AND_SPIN_UP_TWO,
        SPIN_UP_FOUR,
        UP_FOUR,
        DOWN_FOUR,
        INTAKE_RESET_FOUR,
        SPIN_UP_FIVE,
        UP_FIVE,
        DOWN_FIVE,
        INTAKE_RESET_FIVE,
        SPIN_UP_SIX,
        UP_SIX,
        END_SHOT_SEQ_TWO,
        PARK,
        END
    }
    AutoState autoState = AutoState.ALIGN_AND_SPIN_UP;

    Pose2d startPose = new Pose2d(0,0,0);
    MecanumDrive drive = new MecanumDrive(hardwareMap, startPose);
    ElapsedTime shotTimerOne = new ElapsedTime();
    ElapsedTime shotTimerTwo = new ElapsedTime();

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
    }

    @Override
    public void loop() {
        telemetry.addData("Auto State: ", autoState);
        telemetry.addData("Time since last temp auto time: ", getRuntime() - tempAutoTime);
        switch (autoState) {
            case ALIGN_AND_SPIN_UP:
                LauncherMotor.setPower(1);
                IntakeMotor.setPower(INTAKE_POWER);
                if (/*alignWithWebcam("BLUE") ||*/ (getRuntime() - tempAutoTime >= 2)) {
                    autoState = AutoState.SPIN_UP;
                    tempAutoTime = getRuntime();
                }
                break;
            case SPIN_UP:
                LauncherMotor.setVelocity(LAUNCHER_FAR_TARGET, AngleUnit.RADIANS);
                if(Math.abs(LauncherMotor.getVelocity(AngleUnit.RADIANS) - LAUNCHER_FAR_TARGET - .1) <= LAUNCH_THRESHOLD){
                    autoState = AutoState.UP;
                    shotTimerOne.reset();
                    LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
                }
                if(Math.abs(LauncherMotor.getVelocity(AngleUnit.RADIANS) - LAUNCHER_FAR_TARGET - .1) <= LAUNCH_THRESHOLD*4){
                    IntakeMotor.setPower(0);
                }
                break;
            case UP:
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
                if(shotTimerOne.seconds() >= 0.55){
                    autoState = AutoState.DOWN;
                    shotTimerTwo.reset();
                }
                break;
            case DOWN:
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
                if(shotTimerTwo.seconds() >= 0.1){
                    LeftSideFeedRoller.setPower(1);
                    RightSideFeedRoller.setPower(1);
                }
                if(shotTimerTwo.seconds() >= 0.2) {
                    autoState = AutoState.INTAKE_RESET;
                    shotTimerOne.reset();
                }
                break;
            case INTAKE_RESET:
                IntakeMotor.setPower(INTAKE_POWER);
                if(shotTimerOne.seconds() >= 0.55){
                    autoState = AutoState.SPIN_UP_TWO;
                    shotTimerTwo.reset();
                }
                break;
            case SPIN_UP_TWO:
                LauncherMotor.setVelocity(LAUNCHER_FAR_TARGET, AngleUnit.RADIANS);
                if(Math.abs(LauncherMotor.getVelocity(AngleUnit.RADIANS) - LAUNCHER_FAR_TARGET) <= LAUNCH_THRESHOLD){
                    shotTimerOne.reset();
                    autoState = AutoState.UP_TWO;
                }
                if(Math.abs(LauncherMotor.getVelocity(AngleUnit.RADIANS) - LAUNCHER_FAR_TARGET) <= LAUNCH_THRESHOLD*4){
                    IntakeMotor.setPower(0);
                }
                break;
            case UP_TWO:
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
                if(shotTimerOne.seconds() >= 0.55){
                    shotTimerTwo.reset();
                    autoState = AutoState.DOWN_TWO;
                }
                break;
            case DOWN_TWO:
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
                if(shotTimerTwo.seconds() >= 0.1){
                    LeftSideFeedRoller.setPower(1);
                    RightSideFeedRoller.setPower(1);
                }
                if(shotTimerTwo.seconds() >= 0.2) {
                    shotTimerOne.reset();
                    autoState = AutoState.INTAKE_RESET_TWO;
                }
                break;
            case INTAKE_RESET_TWO:
                IntakeMotor.setPower(INTAKE_POWER);
                if(shotTimerOne.seconds() >= 0.75){
                    shotTimerTwo.reset();
                    autoState = AutoState.SPIN_UP_THREE;
                }
                break;
            case SPIN_UP_THREE:
                LauncherMotor.setVelocity(LAUNCHER_FAR_TARGET, AngleUnit.RADIANS);
                if(Math.abs(LauncherMotor.getVelocity(AngleUnit.RADIANS) - LAUNCHER_FAR_TARGET) <= LAUNCH_THRESHOLD){
                    shotTimerOne.reset();
                    autoState = AutoState.UP_THREE;
                }
                if(Math.abs(LauncherMotor.getVelocity(AngleUnit.RADIANS) - LAUNCHER_FAR_TARGET) <= LAUNCH_THRESHOLD*4){
                    IntakeMotor.setPower(0);
                }
                break;
            case UP_THREE:
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
                if(shotTimerOne.seconds() >= 0.55){
                    shotTimerTwo.reset();
                    autoState = AutoState.DOWN_THREE;
                }
                break;
            case DOWN_THREE:
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
                if(shotTimerTwo.seconds() >= 0.2) {
                    shotTimerOne.reset();
                    autoState = AutoState.END_SHOT_SEQ_ONE;
                }
                break;
            case END_SHOT_SEQ_ONE:
                IntakeMotor.setPower(0);
                LeftSideFeedRoller.setPower(0);
                RightSideFeedRoller.setPower(0);
                LauncherMotor.setPower(LAUNCHER_IDLE);
                shotTimerTwo.reset();
                shotTimerOne.reset();
                autoState = AutoState.PARK;
                break;
            case PARK:
                leftBack.setPower(1);
                rightBack.setPower(1);
                leftFront.setPower(1);
                rightFront.setPower(1);
                tempAutoTime = getRuntime();
                break;
            case END:
                if(getRuntime() - tempAutoTime >= .75){
                    leftBack.setPower(0);
                    rightBack.setPower(0);
                    leftFront.setPower(0);
                    rightFront.setPower(0);
                    requestOpModeStop();
                }
                break;
        }

        //aprilTagTelemetry(telemetry);
        telemetry.update();
    }
    public void stop() {
        LauncherMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        LauncherMotor.setPower(0);
    }
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
