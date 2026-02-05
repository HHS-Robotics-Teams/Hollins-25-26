package org.firstinspires.ftc.teamcode._OpModes.Auto.Blue;

import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherSafetyServo;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCH_TICK_VELOCITY_FAR;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.Launch_Time;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.SAFETY_HOLDING;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.SAFTEY_FIRING;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.InstantAction;
import com.acmerobotics.roadrunner.InstantFunction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode._OpModes.Auto.zOLD.PathFactory;
import org.firstinspires.ftc.teamcode._Util.IntakeUtil;
import org.firstinspires.ftc.teamcode._Util.LightUtil;
import org.firstinspires.ftc.teamcode._Proccedural.Components;

@Autonomous
public class BlueVeryFar3x6 extends OpMode {
    MecanumDrive drive;
    IntakeUtil intakeUtil = new IntakeUtil();
    InstantAction IntakePickup = new InstantAction(new InstantFunction() {
        @Override
        public void run() {
            intakeUtil.intakeOn();
        }
    });

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
    ElapsedTime parkable = new ElapsedTime(ElapsedTime.SECOND_IN_NANO);

    @Override
    public void init() {
        drive = new MecanumDrive(hardwareMap, new Pose2d(72 - (16.25/2), -(13 / 2), Math.toRadians(180)));
        state = AutoState.START;
        Components.initComponents(hardwareMap);
        turnToLaunch = drive.actionBuilder(new Pose2d(72 - (16.25/2), -(13 / 2), Math.toRadians(180)))
                .splineToLinearHeading(new Pose2d(50, -10, Math.toRadians(-155)), Math.toRadians(-175))
                .afterDisp(0.01, new InstantFunction() {
                    @Override
                    public void run() {
                        LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_FAR );
                    }
                })
                .build();
        driveToIntakeOne = drive.actionBuilder(new Pose2d(50, -10, Math.toRadians(-155)))
                .afterDisp(20, new InstantFunction() {
                    @Override
                    public void run() {
                        intakeUtil.intakeOn();
                    }
                })
                .strafeToLinearHeading(new Vector2d(62,-50),Math.toRadians(-75))
                .waitSeconds(.2)
                //.lineToYConstantHeading(-58)
                .strafeToLinearHeading(new Vector2d(66,-58),Math.toRadians(-60))
                .waitSeconds(0.5)
                .turnTo(Math.toRadians(-90))
                .lineToY(-60)
                .waitSeconds(0.5)
                .strafeToLinearHeading(new Vector2d(50, -10), Math.toRadians(-155))
                .afterDisp(50, new InstantFunction() {
                    @Override
                    public void run() {
                        intakeUtil.intakeOff();
                    }
                })
                .build();
        driveToIntakeTwo = drive.actionBuilder(new Pose2d(52, -10, Math.toRadians(-155)))
                .splineToSplineHeading(new Pose2d(38,-28,Math.toRadians(-90)),Math.toRadians(-90))
                .afterDisp(1,IntakePickup)
                .waitSeconds(0.2)
                .lineToY(-41)
                .lineToY(-59)
                .splineToLinearHeading(new Pose2d(53, -10, Math.toRadians(-154)),Math.toRadians(-155.75))
                .build();
        telemetry.addLine("Ready to Launch");
        LightUtil util = new LightUtil(2, hardwareMap);
        util.makeGreen();
        util.updateLights();
    }

    @Override
    public void start(){
        parkable.reset();
        LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_FAR + 275);
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
        if(parkable.seconds() >= 29.95){
            state = AutoState.END;
        }
        telemetry.addData("State:", state);
        telemetry.addData("Launcher Velocity", LauncherMotor.getVelocity());
        telemetry.addData( "Launch Timer", launchTimer.seconds());
        telemetry.addData("Intake timer", intakeTimer.seconds());
        telemetry.addData("Launcher Current", LauncherMotor.getCurrent(CurrentUnit.AMPS));
        telemetry.update();

        switch (state){
            case START:
                LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_FAR + 340);
                intakeTimer.reset();
                state = AutoState.SPIN_UP;
                break;

            case SPIN_UP:
                LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_FAR + 340);
                state = AutoState.LAUNCH_ONE;
                LauncherSafetyServo.setPosition(SAFTEY_FIRING);
                break;
            case LAUNCH_ONE:
                if (LauncherMotor.getVelocity() >= LAUNCH_TICK_VELOCITY_FAR + 340){ // do not change this time
                   intakeUtil.intakeOn();
                    launchTimer.reset();
                    state = AutoState.RESET_ONE;
                }
                break;
            case RESET_ONE:
                if (launchTimer.seconds() >= Launch_Time) { // todo change Time
                    LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_FAR);
                    intakeUtil.launchEnd();
                    state = AutoState.DRIVE_TO_INTAKE_ONE;
                }
                break;
            case DRIVE_TO_INTAKE_ONE:

                LauncherSafetyServo.setPosition(SAFETY_HOLDING);
                state = AutoState.DRIVE_TO_LAUNCH_TWO;
                Actions.runBlocking(driveToIntakeOne);
                break;
            case DRIVE_TO_LAUNCH_TWO:
                intakeTimer.reset();
                state = AutoState.END;
                break;
            case SPIN_UP_TWO:
                LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_FAR + 275);
                state = AutoState.LAUNCH_TWO;
                launchTimer.reset();
                break;
            case LAUNCH_TWO:
                if(LauncherMotor.getVelocity() >= LAUNCH_TICK_VELOCITY_FAR + 275){
                   intakeUtil.intakeOn();
                    LauncherSafetyServo.setPosition(SAFTEY_FIRING);
                    launchTimer.reset();
                    state = AutoState.RESET_TWO;
                }
                break;
            case RESET_TWO:
                if (launchTimer.seconds() >= Launch_Time) { // todo change Time
                    LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_FAR);
                    intakeUtil.launchEnd();
                    state = AutoState.PARK;
                }
                break;
            case DRIVE_TO_INTAKE_TWO:
                LauncherSafetyServo.setPosition(SAFETY_HOLDING);
                intakeUtil.intakeOn();
                Actions.runBlocking(driveToIntakeTwo);
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

                LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_FAR + 275);
                state = AutoState.LAUNCH_THREE;
                break;
            case LAUNCH_THREE:
                if (LauncherMotor.getVelocity() >= LAUNCH_TICK_VELOCITY_FAR + 275) { // todo change Time
                    LauncherSafetyServo.setPosition(SAFTEY_FIRING);
                   intakeUtil.intakeOn();
                    launchTimer.reset();
                    state = AutoState.RESET_THREE;
                }
                break;
            case RESET_THREE:
                if(launchTimer.seconds() >= Launch_Time) {
                    intakeUtil.launchEnd();
                    state = AutoState.PARK;
                }
                break;

            case PARK:
                LauncherSafetyServo.setPosition(SAFETY_HOLDING);
                LauncherMotor.setVelocity(600);
                Actions.runBlocking(drive.actionBuilder(new Pose2d(50, -10, Math.toRadians(-154))).lineToX(40).build());
                state = AutoState.END;
                break;
            case END:
                requestOpModeStop();
                break;
        }

    }
}

