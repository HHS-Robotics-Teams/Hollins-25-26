package org.firstinspires.ftc.teamcode._OpModes.Auto.temp;

import static org.firstinspires.ftc.teamcode._OpModes.Auto.temp.BlueVeryFarNoSpike.AutoState.*;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherHoodServo;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherSafetyServo;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCH_TICK_VELOCITY_FAR;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.Launch_Time;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.SAFETY_HOLDING;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.SAFTEY_FIRING;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.timeoutTime;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.InstantAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode._Proccedural.Components;
import org.firstinspires.ftc.teamcode._Util.AutoUtil;
import org.firstinspires.ftc.teamcode._Util.IntakeUtil;
import org.firstinspires.ftc.teamcode._Util.LightUtil;

@Autonomous
@Deprecated
@Disabled
public class BlueVeryFarNoSpike extends OpMode {
    MecanumDrive drive;
    IntakeUtil intakeUtil = new IntakeUtil();
    InstantAction IntakePickup = new InstantAction(() -> intakeUtil.intakeOn());
    AutoUtil autoUtil;
    LightUtil lightUtil;
    enum AutoState{
        START,
        SPIN_UP,
        LAUNCH_ONE,
        RESET_ONE,
        LAUNCH_TWO,
        RESET_TWO,
        LAUNCH_THREE,
        RESET_THREE,
        DRIVE_TO_INTAKE_ONE,
        INTAKE_ONE,
        DRIVE_TO_LAUNCH_TWO,
        SPIN_UP_TWO,
        LAUNCH_FOUR,
        RESET_FOUR,
        LAUNCH_FIVE,
        RESET_FIVE,
        LAUNCH_SIX,
        RESET_SIX,
        DRIVE_TO_INTAKE_TWO,
        INTAKE_TWO,
        DRIVE_TO_LAUNCH_THREE,
        SPIN_UP_THREE,
        LAUNCH_SEVEN,
        RESET_SEVEN,
        LAUNCH_EIGHT,
        RESET_EIGHT,
        LAUNCH_NINE,
        RESET_NINE,
        PARK,
        END,
        DRIVE_TO_INTAKE_THREE,
        SPIN_UP_FOUR
    }
    AutoState state = START;
    Action turnToLaunch;
    Action driveToIntakeOne;
    Action driveToIntakeTwo;
    Action driveToIntakeThree;
    ElapsedTime launchTimer = new ElapsedTime(ElapsedTime.SECOND_IN_NANO);
    ElapsedTime intakeTimer = new ElapsedTime(ElapsedTime.SECOND_IN_NANO);

    @Override
    public void init() {
        drive = new MecanumDrive(hardwareMap, new Pose2d(72 - (16.25/2), -(13 / 2) - 24, Math.toRadians(-180)));
        state = START;
        Components.initComponents(hardwareMap);
        turnToLaunch = drive.actionBuilder(new Pose2d(72 - (16.25/2), -(13 / 2) - 24, Math.toRadians(-180)))
                .waitSeconds(0.75)
                .strafeToLinearHeading(new Vector2d(50, -12), Math.toRadians(-152.5))
                .build();
        driveToIntakeOne = drive.actionBuilder(new Pose2d(50, -12, Math.toRadians(-150)))
                .afterDisp(20, () -> intakeUtil.intakeOn())
                .strafeToLinearHeading(new Vector2d(65,-55),Math.toRadians(-90))
                .waitSeconds(0.1)
                .strafeToConstantHeading(new Vector2d(68,-60))
                .strafeToLinearHeading(new Vector2d(50, -12), Math.toRadians(-152.5))
                .afterDisp(5, () -> intakeUtil.intakeOff())
                .build();
        driveToIntakeTwo = drive.actionBuilder(new Pose2d(50, -12, Math.toRadians(-152.5)))
                .afterDisp(20, () -> intakeUtil.intakeOn())
                //.splineToLinearHeading(new Pose2d(74,-67,Math.toRadians(-90)), Math.toRadians(-90))
                .strafeToLinearHeading(new Vector2d(65,-55),Math.toRadians(-90))
                .strafeToLinearHeading(new Vector2d(68, -65),Math.toRadians(-90))
                .waitSeconds(0.1)
                .strafeToLinearHeading(new Vector2d(50, -12), Math.toRadians(-152.49))
                .afterDisp(5, () -> intakeUtil.intakeOff())
                .build();
        driveToIntakeThree = drive.actionBuilder(new Pose2d(50, -12, Math.toRadians(-152.5)))
                .afterDisp(20, () -> intakeUtil.intakeOn())
                .strafeToLinearHeading(new Vector2d(65,-55),Math.toRadians(-90))
                .strafeToSplineHeading(new Vector2d(68, -65),Math.toRadians(-90))
                .waitSeconds(0.1)
                .strafeToLinearHeading(new Vector2d(50, -12), Math.toRadians(-152.49))
                .afterDisp(5, () -> intakeUtil.intakeOff())
                .build();

        telemetry.addLine("Ready to Launch");
        lightUtil = new LightUtil(2, hardwareMap);
        lightUtil.makeGreen();
        lightUtil.updateLights();
        autoUtil = new AutoUtil(0.4);
    }

    @Override
    public void start(){
        lightUtil.makeOff();
        lightUtil.updateLights();
        LauncherHoodServo.setPosition(0.8);
        LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_FAR + 375);
        Actions.runBlocking(turnToLaunch);
    }


    @Override
    public void stop () {
        for(int i = 0; i < Thread.activeCount(); i++){
            Thread.currentThread().interrupt();
        }
        requestOpModeStop();
    }


    @Override
    public void loop() {
        // telemetry.addData("State:", state);
        //        telemetry.addData("Launcher Velocity", LauncherMotor.getVelocity());
        //        telemetry.addData( "Launch Timer", launchTimer.seconds());
        //        telemetry.addData("Intake timer", intakeTimer.seconds());
        //        telemetry.addData("Launcher Current", LauncherMotor.getCurrent(CurrentUnit.AMPS));
        //        telemetry.update();
        telemetry.addLine(autoUtil.currentReadings());

        switch (state){
            case START:
                intakeTimer.reset();
                state = SPIN_UP;
                break;

            case SPIN_UP:
                LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_FAR + 300);
                state = LAUNCH_ONE;
                LauncherSafetyServo.setPosition(SAFTEY_FIRING);
                break;
            case LAUNCH_ONE:
                if (LauncherMotor.getVelocity() >= LAUNCH_TICK_VELOCITY_FAR + 300){ // do not change this time
                    intakeUtil.launchStart(0.8, 1);
                    launchTimer.reset();
                    autoUtil.resetEmptyTimer();
                    state = RESET_ONE;
                }
                break;
            case RESET_ONE:
                if (launchTimer.seconds() >= Launch_Time || autoUtil.isBotEmpty()) { // todo change Time
                    LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_FAR);
                    intakeUtil.launchEnd();
                    state = DRIVE_TO_INTAKE_ONE;
                }
                break;
            case DRIVE_TO_INTAKE_ONE:
                LauncherSafetyServo.setPosition(SAFETY_HOLDING);
                state = DRIVE_TO_LAUNCH_TWO;
                Actions.runBlocking(driveToIntakeOne);
                intakeUtil.intakeOff();
                break;
            case DRIVE_TO_LAUNCH_TWO:
                intakeTimer.reset();
                state = SPIN_UP_TWO;
                break;
            case SPIN_UP_TWO:
                LauncherSafetyServo.setPosition(SAFTEY_FIRING);
                LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_FAR + 310);
                state = LAUNCH_TWO;
                launchTimer.reset();
                autoUtil.resetEmptyTimer();
                break;
            case LAUNCH_TWO:
                if(LauncherMotor.getVelocity() >= LAUNCH_TICK_VELOCITY_FAR + 310){
                    intakeUtil.launchStart(0.8, 1);
                    launchTimer.reset();
                    autoUtil.resetEmptyTimer();
                    state = RESET_TWO;
                }
                break;
            case RESET_TWO:
                if (launchTimer.seconds() >= Launch_Time || autoUtil.isBotEmpty()) { // todo change Time
                    LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_FAR);
                    intakeUtil.intakeOff();
                    state = DRIVE_TO_INTAKE_TWO;
                }
                break;
            case DRIVE_TO_INTAKE_TWO:
                LauncherSafetyServo.setPosition(SAFETY_HOLDING);
                intakeUtil.intakeOn();
                Actions.runBlocking(driveToIntakeTwo);
                intakeUtil.intakeOff();
                state = INTAKE_TWO;
                break;
            case INTAKE_TWO:
                state = DRIVE_TO_LAUNCH_THREE;
                break;
            case DRIVE_TO_LAUNCH_THREE:
                intakeTimer.reset();
                state = SPIN_UP_THREE;
                break;
            case SPIN_UP_THREE:
                LauncherSafetyServo.setPosition(SAFTEY_FIRING);
                LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_FAR + 320);
                state = LAUNCH_THREE;
                break;
            case LAUNCH_THREE:
                if (LauncherMotor.getVelocity() >= LAUNCH_TICK_VELOCITY_FAR + 320) { // todo change Time
                    intakeUtil.launchStart(0.8, 1);
                    launchTimer.reset();
                    autoUtil.resetEmptyTimer();
                    state = RESET_THREE;
                }
                break;
            case RESET_THREE:
                if(launchTimer.seconds() >= Launch_Time || autoUtil.isBotEmpty()) {
                    intakeUtil.intakeOff();
                    state = DRIVE_TO_INTAKE_THREE;
                }
                break;
            case DRIVE_TO_INTAKE_THREE:
                LauncherSafetyServo.setPosition(SAFETY_HOLDING);
                intakeUtil.intakeOn();
                timeoutTime = 1.35;
                Actions.runBlocking(driveToIntakeThree);
                intakeUtil.intakeOff();
                state = LAUNCH_FOUR;
                break;
            case LAUNCH_FOUR:
                if (LauncherMotor.getVelocity() >= LAUNCH_TICK_VELOCITY_FAR + 340) { // todo change Time
                    intakeUtil.launchStart(0.8, 1);
                    launchTimer.reset();
                    autoUtil.resetEmptyTimer();
                    state = RESET_FOUR;
                }
                break;
            case RESET_FOUR:
                if(launchTimer.seconds() >= Launch_Time || autoUtil.isBotEmpty()) {
                    intakeUtil.intakeOff();
                    state = PARK;
                }
                break;
            case PARK:
                LauncherSafetyServo.setPosition(SAFETY_HOLDING);
                LauncherMotor.setVelocity(400);
                Actions.runBlocking(drive.actionBuilder(new Pose2d(50, -12, Math.toRadians(-162.5))).lineToX(40).build());
                state = END;
                break;
            case END:
                requestOpModeStop();
                break;
        }

    }
}


