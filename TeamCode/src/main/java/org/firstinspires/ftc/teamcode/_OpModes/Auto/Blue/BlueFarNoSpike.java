package org.firstinspires.ftc.teamcode._OpModes.Auto.Blue;

import static org.firstinspires.ftc.teamcode._OpModes.Auto.Blue.BlueVeryFarSpikeMark.AutoState.DRIVE_TO_INTAKE_ONE;
import static org.firstinspires.ftc.teamcode._OpModes.Auto.Blue.BlueVeryFarSpikeMark.AutoState.DRIVE_TO_INTAKE_TWO;
import static org.firstinspires.ftc.teamcode._OpModes.Auto.Blue.BlueVeryFarSpikeMark.AutoState.DRIVE_TO_LAUNCH_THREE;
import static org.firstinspires.ftc.teamcode._OpModes.Auto.Blue.BlueVeryFarSpikeMark.AutoState.DRIVE_TO_LAUNCH_TWO;
import static org.firstinspires.ftc.teamcode._OpModes.Auto.Blue.BlueVeryFarSpikeMark.AutoState.END;
import static org.firstinspires.ftc.teamcode._OpModes.Auto.Blue.BlueVeryFarSpikeMark.AutoState.INTAKE_TWO;
import static org.firstinspires.ftc.teamcode._OpModes.Auto.Blue.BlueVeryFarSpikeMark.AutoState.LAUNCH_ONE;
import static org.firstinspires.ftc.teamcode._OpModes.Auto.Blue.BlueVeryFarSpikeMark.AutoState.LAUNCH_THREE;
import static org.firstinspires.ftc.teamcode._OpModes.Auto.Blue.BlueVeryFarSpikeMark.AutoState.LAUNCH_TWO;
import static org.firstinspires.ftc.teamcode._OpModes.Auto.Blue.BlueVeryFarSpikeMark.AutoState.PARK;
import static org.firstinspires.ftc.teamcode._OpModes.Auto.Blue.BlueVeryFarSpikeMark.AutoState.RESET_ONE;
import static org.firstinspires.ftc.teamcode._OpModes.Auto.Blue.BlueVeryFarSpikeMark.AutoState.RESET_THREE;
import static org.firstinspires.ftc.teamcode._OpModes.Auto.Blue.BlueVeryFarSpikeMark.AutoState.RESET_TWO;
import static org.firstinspires.ftc.teamcode._OpModes.Auto.Blue.BlueVeryFarSpikeMark.AutoState.SPIN_UP;
import static org.firstinspires.ftc.teamcode._OpModes.Auto.Blue.BlueVeryFarSpikeMark.AutoState.SPIN_UP_THREE;
import static org.firstinspires.ftc.teamcode._OpModes.Auto.Blue.BlueVeryFarSpikeMark.AutoState.SPIN_UP_TWO;
import static org.firstinspires.ftc.teamcode._OpModes.Auto.Blue.BlueVeryFarSpikeMark.AutoState.START;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherHoodServo;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherSafetyServo;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCH_TICK_VELOCITY_FAR;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.Launch_Time;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.SAFETY_HOLDING;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.SAFTEY_FIRING;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.InstantAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode._Proccedural.Components;
import org.firstinspires.ftc.teamcode._Util.AutoUtil;
import org.firstinspires.ftc.teamcode._Util.IntakeUtil;
import org.firstinspires.ftc.teamcode._Util.LightUtil;

@Autonomous
public class BlueFarNoSpike extends OpMode {
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
        END
    }
    AutoState state = AutoState.START;
    Action turnToLaunch;
    Action driveToIntakeOne;
    Action driveToIntakeTwo;
    ElapsedTime launchTimer = new ElapsedTime(ElapsedTime.SECOND_IN_NANO);
    ElapsedTime intakeTimer = new ElapsedTime(ElapsedTime.SECOND_IN_NANO);

    @Override
    public void init() {
        drive = new MecanumDrive(hardwareMap, new Pose2d(72 - (16.25/2), -((double) 13 / 2) - 24, Math.toRadians(-180)));
        state = AutoState.START;
        Components.initComponents(hardwareMap);
        turnToLaunch = drive.actionBuilder(new Pose2d(72 - (16.25/2), -((double) 13 / 2) - 24, Math.toRadians(-180)))
                .waitSeconds(0.85)
                .strafeToLinearHeading(new Vector2d(50, -12), Math.toRadians(-155.5))
                .build();
        driveToIntakeOne = drive.actionBuilder(new Pose2d(50, -12, Math.toRadians(-155.5)))
                .afterDisp(20, () -> intakeUtil.intakeOn())
                .strafeToLinearHeading(new Vector2d(60,-57),Math.toRadians(-75))
                .waitSeconds(.2)
                .strafeToLinearHeading(new Vector2d(64,-57), Math.toRadians(-75))
                .waitSeconds(.2)
                .strafeToLinearHeading(new Vector2d(67,-62),Math.toRadians(-60))
                .waitSeconds(0.5)
                .strafeToLinearHeading(new Vector2d(50, -13), Math.toRadians(-155.5))
                .afterDisp(65, () -> intakeUtil.intakeOff())
                .build();
        driveToIntakeTwo = drive.actionBuilder(new Pose2d(50, -13, Math.toRadians(-155.5)))
                .afterDisp(20, () -> intakeUtil.intakeOn())
                .strafeToLinearHeading(new Vector2d(70,-60),Math.toRadians(-75))
                .waitSeconds(.2)
                .strafeToLinearHeading(new Vector2d(68,-63),Math.toRadians(-90))
                .waitSeconds(0.5)
                .strafeToLinearHeading(new Vector2d(50, -13), Math.toRadians(-155.5))
                .afterDisp(65, () -> intakeUtil.intakeOff())
                .build();
        telemetry.addLine("Ready to Launch");
        lightUtil = new LightUtil(2, hardwareMap);
        lightUtil.makeGreen();
        lightUtil.updateLights();
        autoUtil = new AutoUtil(0.2);
    }

    @Override
    public void start(){
        lightUtil.makeOff();
        lightUtil.updateLights();
        LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_FAR + 350);
        LauncherHoodServo.setPosition(0.85);
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

        switch (state){
            case START:
                intakeTimer.reset();
                state = AutoState.SPIN_UP;
                break;

            case SPIN_UP:
                LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_FAR + 350);
                state = AutoState.LAUNCH_ONE;
                LauncherSafetyServo.setPosition(SAFTEY_FIRING);
                break;
            case LAUNCH_ONE:
                if (LauncherMotor.getVelocity() >= LAUNCH_TICK_VELOCITY_FAR + 350){ // do not change this time
                    intakeUtil.launchStart(0.5, 0.5);
                    launchTimer.reset();
                    autoUtil.resetEmptyTimer();
                    state = AutoState.RESET_ONE;
                }
                break;
            case RESET_ONE:
                if(launchTimer.seconds() > 0.45){
                    LauncherHoodServo.setPosition(0.75);
                }
                if (launchTimer.seconds() >= Launch_Time || autoUtil.isBotEmpty() ) { // todo change Time
                    LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_FAR);
                    intakeUtil.launchEnd();
                    state = AutoState.DRIVE_TO_INTAKE_ONE;
                    LauncherHoodServo.setPosition(0.85);
                }
                break;
            case DRIVE_TO_INTAKE_ONE:
                LauncherSafetyServo.setPosition(SAFETY_HOLDING);
                state = AutoState.DRIVE_TO_LAUNCH_TWO;
                Actions.runBlocking(driveToIntakeOne);
                intakeUtil.intakeOff();
                break;
            case DRIVE_TO_LAUNCH_TWO:
                intakeTimer.reset();
                state = AutoState.SPIN_UP_TWO;
                break;
            case SPIN_UP_TWO:
                LauncherSafetyServo.setPosition(SAFTEY_FIRING);
                LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_FAR + 375);
                state = AutoState.LAUNCH_TWO;
                launchTimer.reset();
                autoUtil.resetEmptyTimer();
                break;
            case LAUNCH_TWO:
                if(LauncherMotor.getVelocity() >= LAUNCH_TICK_VELOCITY_FAR + 375){
                    intakeUtil.launchStart(0.8, 0.8);
                    launchTimer.reset();
                    autoUtil.resetEmptyTimer();
                    state = AutoState.RESET_TWO;
                }
                break;
            case RESET_TWO:
                if(launchTimer.seconds() > 0.45){
                    LauncherHoodServo.setPosition(0.75);
                }
                if (launchTimer.seconds() >= Launch_Time || autoUtil.isBotEmpty()) { // todo change Time
                    LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_FAR);
                    intakeUtil.intakeOff();
                    LauncherHoodServo.setPosition(0.85);
                    state = AutoState.DRIVE_TO_INTAKE_TWO;
                }
                break;
            case DRIVE_TO_INTAKE_TWO:
                LauncherSafetyServo.setPosition(SAFETY_HOLDING);
                intakeUtil.intakeOn();
                Actions.runBlocking(driveToIntakeTwo);
                intakeUtil.intakeOff();
                state = AutoState.INTAKE_TWO;
                break;
            case INTAKE_TWO:
                state = AutoState.DRIVE_TO_LAUNCH_THREE;
                break;
            case DRIVE_TO_LAUNCH_THREE:
                intakeTimer.reset();
                state = AutoState.SPIN_UP_THREE;
                break;
            case SPIN_UP_THREE:
                LauncherSafetyServo.setPosition(SAFTEY_FIRING);
                LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_FAR + 375);
                state = AutoState.LAUNCH_THREE;
                break;
            case LAUNCH_THREE:
                if (LauncherMotor.getVelocity() >= LAUNCH_TICK_VELOCITY_FAR + 375) { // todo change Time
                    intakeUtil.launchStart(0.8, 0.8);
                    launchTimer.reset();
                    autoUtil.resetEmptyTimer();
                    state = AutoState.RESET_THREE;
                }
                break;
            case RESET_THREE:
                if(launchTimer.seconds() > 0.45){
                    LauncherHoodServo.setPosition(0.75);
                }
                if(launchTimer.seconds() >= Launch_Time || autoUtil.isBotEmpty()) {
                    intakeUtil.intakeOff();
                    state = AutoState.PARK;
                }
                break;
            case PARK:
                LauncherSafetyServo.setPosition(SAFETY_HOLDING);
                LauncherMotor.setVelocity(400);
                Actions.runBlocking(drive.actionBuilder(new Pose2d(50, -12, Math.toRadians(-162.5)))
                        .afterDisp(20, () -> intakeUtil.intakeOn())
                        .strafeToLinearHeading(new Vector2d(60,-57),Math.toRadians(-75))
                        .waitSeconds(.2)
                        .strafeToLinearHeading(new Vector2d(64,-57), Math.toRadians(-75))
                        .waitSeconds(.2)
                        .strafeToLinearHeading(new Vector2d(68,-63),Math.toRadians(-60))
                        .build());
                state = AutoState.END;
                break;
            case END:
                intakeUtil.intakeOff();
                requestOpModeStop();
                break;
        }

    }
}


