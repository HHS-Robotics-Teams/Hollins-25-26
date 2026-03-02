package org.firstinspires.ftc.teamcode._OpModes.Auto.Blue;

import static org.firstinspires.ftc.teamcode._OpModes.Auto.Blue.BlueNear3x9Gateless.AutoState.*;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherHoodServo;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherSafetyServo;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCH_TICK_VELOCITY_NEAR;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.Launch_Time;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.SAFETY_HOLDING;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.SAFTEY_FIRING;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.timeoutTime;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.InstantAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.TranslationalVelConstraint;
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
public class BlueNear3x9Gateless extends OpMode {
    IntakeUtil intakeUtil = new IntakeUtil();
    MecanumDrive drive;

    InstantAction runIntake = new InstantAction(() -> {
        LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_NEAR);
        intakeUtil.intakeOn();
        LauncherSafetyServo.setPosition(SAFETY_HOLDING);
    });
    InstantAction offIntake = new InstantAction(() -> {
        intakeUtil.intakeOff();
        LauncherSafetyServo.setPosition(SAFTEY_FIRING);
    });
    InstantAction setVelocity = new InstantAction(() -> LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_NEAR));
    enum AutoState{
        START,
        LAUNCH_ONE,
        RESET_ONE,
        DRIVE_TO_INTAKE_ONE,
        SPIN_UP_TWO,
        LAUNCH_TWO,
        RESET_TWO,
        DRIVE_TO_INTAKE_TWO,
        DRIVE_TO_LAUNCH_THREE,
        LAUNCH_THREE,
        RESET_THREE,
        DRIVE_TO_INTAKE_THREE,
        DRIVE_TO_LAUNCH_FOUR,
        LAUNCH_FOUR,
        RESET_FOUR,
        PARK,
        END
    }
    AutoState state = START;
    Action turnToLaunch;
    Action driveToIntakeOne;
    Action driveToIntakeTwo;
    Action driveToIntakeThree;
    LightUtil lightUtil;
    AutoUtil autoUtil;
    ElapsedTime launchTimer = new ElapsedTime(ElapsedTime.SECOND_IN_NANO);
    ElapsedTime faultTimer = new ElapsedTime(ElapsedTime.SECOND_IN_NANO);

    @Override
    public void init() {
        autoUtil = new AutoUtil(.175);
        drive = new MecanumDrive(hardwareMap, new Pose2d(-54,-48,Math.toRadians(-135)));
        state = START;
        Components.initComponents(hardwareMap);
        turnToLaunch = drive.actionBuilder(new Pose2d(-54,-48,Math.toRadians(-135)))
                .strafeToLinearHeading(new Vector2d(-46.5, -38.25),Math.toRadians(-125))
                .afterTime(0.05, setVelocity)
                .build();
        driveToIntakeOne = drive.actionBuilder(new Pose2d(-46.5,-38.25,Math.toRadians(-125)))
                .strafeToLinearHeading(new Vector2d(-15,-24),Math.toRadians(-90))
                .afterDisp(.1, runIntake)
                .strafeToConstantHeading(new Vector2d(-15,-51))
                .afterDisp(35, offIntake)
                .strafeToLinearHeading(new Vector2d(-36,-36),Math.toRadians(-133))
                .build();
        driveToIntakeTwo = drive.actionBuilder(new Pose2d(-36,-36,Math.toRadians(-133)))
                .strafeToLinearHeading(new Vector2d(9,-24),Math.toRadians(-90))
                .afterTime(0.1, runIntake)
                .strafeToConstantHeading(new Vector2d(9, -56))
                .setTangent(Math.toRadians(-90))
                //.strafeToConstantHeading(new Vector2d(9,-58))
                .afterDisp(35, offIntake)
                .strafeToLinearHeading(new Vector2d(-24,-24),Math.toRadians(-138))
                .build();
        driveToIntakeThree = drive.actionBuilder(new Pose2d(-24, -24, Math.toRadians(-138)))
                .strafeToLinearHeading(new Vector2d(30.5, -24), Math.toRadians(-90))
                .afterTime(0.1, runIntake)
                .strafeToConstantHeading(new Vector2d(30.5,-56))
                .setTangent(Math.toRadians(-90))
                //.strafeToConstantHeading(new Vector2d(30.5,-56))
                .afterDisp(35, offIntake)
                .strafeToLinearHeading(new Vector2d(-36, -26), Math.toRadians(-130))
                .build();
        telemetry.addLine("Ready to Launch");
        lightUtil = new LightUtil(2, hardwareMap);
        lightUtil.makeGreen();
        lightUtil.updateLights();
    }

    @Override
    public void start(){
        //LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_NEAR  100);
        LauncherHoodServo.setPosition(0.05);
        lightUtil.makeOff();
        lightUtil.updateLights();
        faultTimer.reset();
        LauncherMotor.setPower(1);
        Actions.runBlocking(turnToLaunch);
        LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_NEAR - 115);
    }

    @Override
    public void stop () {
        int x = Thread.activeCount();
        for(int i = 0; i < x;  i++){
            Thread.currentThread().interrupt();
        }
        requestOpModeStop();
    }

    @Override
    public void loop() {
           telemetry.addLine(autoUtil.currentReadings());
        //   telemetry.addData("State:", state);
        //   telemetry.addData("Launcher Velocity", LauncherMotor.getVelocity());
        //   telemetry.addData("Launch Timer", launchTimer.seconds());
        //   telemetry.addData("Intake timer", intakeTimer.seconds());
        //   telemetry.addData("Launcher Current", LauncherMotor.getCurrent(CurrentUnit.AMPS));
        //   telemetry.update();
        //   telemetry.addData("is bot empty?", autoUtil.isBotEmpty());

        switch (state){
            case START:
                LauncherSafetyServo.setPosition(SAFTEY_FIRING);
                LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_NEAR - 115);
                state = LAUNCH_ONE;
                break;
            case LAUNCH_ONE:
                if (LauncherMotor.getVelocity() >= LAUNCH_TICK_VELOCITY_NEAR - 115){
                    LauncherSafetyServo.setPosition(SAFTEY_FIRING);
                    intakeUtil.launchStart();
                    launchTimer.reset();
                    state = RESET_ONE;
                    autoUtil.resetEmptyTimer();
                }
                break;
            case RESET_ONE:
                if (launchTimer.seconds() >= Launch_Time /*|| autoUtil.isBotEmpty()*/) {
                    LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_NEAR + 145);
                    LauncherHoodServo.setPosition(0.2);
                    intakeUtil.launchEnd();
                    state = DRIVE_TO_INTAKE_ONE;
                }
                break;
            case DRIVE_TO_INTAKE_ONE:
                Actions.runBlocking(driveToIntakeOne);
                state = SPIN_UP_TWO;
                break;
            case SPIN_UP_TWO:
                LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_NEAR + 145);
                state = LAUNCH_TWO;
                break;
            case LAUNCH_TWO:
                if(LauncherMotor.getVelocity() >= (LAUNCH_TICK_VELOCITY_NEAR + 145)){
                    intakeUtil.launchStart();
                    state = RESET_TWO;
                    launchTimer.reset();
                    autoUtil.resetEmptyTimer();
                }
                break;
            case RESET_TWO:
                if (launchTimer.seconds() >= Launch_Time /*|| autoUtil.isBotEmpty()*/) {
                    LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_NEAR + 160);
                    intakeUtil.launchEnd();
                    state = DRIVE_TO_INTAKE_TWO;
                    launchTimer.reset();
                }
                break;

            case DRIVE_TO_INTAKE_TWO:
                Actions.runBlocking(driveToIntakeTwo);
                state = DRIVE_TO_LAUNCH_THREE;
                break;
            case DRIVE_TO_LAUNCH_THREE:
                LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_NEAR + 160);
                state = LAUNCH_THREE;
                break;
            case LAUNCH_THREE:
                if(LauncherMotor.getVelocity() >= (LAUNCH_TICK_VELOCITY_NEAR + 160 )) {
                    intakeUtil.launchStart();
                    state = RESET_THREE;
                    launchTimer.reset();
                    autoUtil.resetEmptyTimer();
                }
                break;
            case RESET_THREE:
                if (launchTimer.seconds() >= Launch_Time /*|| autoUtil.isBotEmpty()*/) {
                    LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_NEAR + 175);
                    intakeUtil.launchEnd();
                    state = DRIVE_TO_INTAKE_THREE;
                    launchTimer.reset();
                }
                break;
            case DRIVE_TO_INTAKE_THREE:
                timeoutTime = 1.25;
                Actions.runBlocking(driveToIntakeThree);
                state = DRIVE_TO_LAUNCH_FOUR;
//                if(faultTimer.seconds() > 29.95){
//                    state = AutoState.PARK;
//                }
//                break;
            case DRIVE_TO_LAUNCH_FOUR:
                LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_NEAR + 175);
                state = LAUNCH_FOUR;
                break;
            case LAUNCH_FOUR:
                if(LauncherMotor.getVelocity() >= (LAUNCH_TICK_VELOCITY_NEAR + 175)) {
                    intakeUtil.launchStart();
                    launchTimer.reset();
                    autoUtil.resetEmptyTimer();
                    autoUtil.changeTimeout(.2);
                    state = RESET_FOUR;
                }
                break;
            case RESET_FOUR:
                if(launchTimer.seconds() >= Launch_Time/*|| autoUtil.isBotEmpty()*/){
                    LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_NEAR + 175);
                    intakeUtil.launchEnd();
                    state = PARK;
                }
                break;
            case PARK:
                LauncherMotor.setVelocity(0);
                intakeUtil.intakeOff();
                state = END;
                break;
            case END:
                requestOpModeStop();
                break;
        }

    }
}

