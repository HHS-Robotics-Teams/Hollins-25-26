package org.firstinspires.ftc.teamcode._OpModes.Auto.Blue;

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
public class GateBlueNear3x9 extends OpMode {
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
    AutoState state = AutoState.START;
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
        drive = new MecanumDrive(hardwareMap, new Pose2d(-55,-49,Math.toRadians(-135)));
        state = AutoState.START;
        Components.initComponents(hardwareMap);
        turnToLaunch = drive.actionBuilder(new Pose2d(-55,-49,Math.toRadians(-135)))
                .strafeToLinearHeading(new Vector2d(-46.5, -38.25),Math.toRadians(-125),new TranslationalVelConstraint(60))
                .afterTime(0.05, setVelocity)
                .build();
        driveToIntakeOne = drive.actionBuilder(new Pose2d(-46.5,-38.25,Math.toRadians(-125)))
                .strafeToLinearHeading(new Vector2d(-14,-24),Math.toRadians(-90))
                .afterDisp(.1, runIntake)
                .strafeToConstantHeading(new Vector2d(-14,-56), new TranslationalVelConstraint(50))
                .afterDisp(7.5, offIntake)
                .strafeToLinearHeading(new Vector2d(-24,-24),Math.toRadians(-130))
                .build();
        driveToIntakeTwo = drive.actionBuilder(new Pose2d(-24,-24,Math.toRadians(-130)))
                .strafeToLinearHeading(new Vector2d(9,-28),Math.toRadians(-90))
                .afterTime(0.1, runIntake)
                .strafeToConstantHeading(new Vector2d(9, -61),new TranslationalVelConstraint(50))
                .waitSeconds(0.05)
                .lineToYConstantHeading(-50) // to save time lessen this distance
                .strafeToConstantHeading(new Vector2d(-4,-60))
                .afterDisp(7.5, offIntake)
                .strafeToLinearHeading(new Vector2d(-24,-24),Math.toRadians(-130))
                .build();
        driveToIntakeThree = drive.actionBuilder(new Pose2d(-24, -24, Math.toRadians(-130)))
                .strafeToLinearHeading(new Vector2d(29.7, -24), Math.toRadians(-90))
                .afterTime(0.1, runIntake)
                .strafeToConstantHeading(new Vector2d(29.7,-63.5),new TranslationalVelConstraint(50))
                .lineToYConstantHeading(-50)
                .afterDisp(7.5, offIntake)
                .strafeToLinearHeading(new Vector2d(-36, -26), Math.toRadians(-122.5))
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
        LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_NEAR - 125);
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
                LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_NEAR - 125);
                state = AutoState.LAUNCH_ONE;
                break;
            case LAUNCH_ONE:
                if (LauncherMotor.getVelocity() >= LAUNCH_TICK_VELOCITY_NEAR - 125){
                    LauncherSafetyServo.setPosition(SAFTEY_FIRING);
                    intakeUtil.launchStart();
                    launchTimer.reset();
                    state = AutoState.RESET_ONE;
                    autoUtil.resetEmptyTimer();
                }
                break;
            case RESET_ONE:
                if (launchTimer.seconds() >= Launch_Time || autoUtil.isBotEmpty()) {
                    LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_NEAR + 153);
                    LauncherHoodServo.setPosition(0.2);
                    intakeUtil.launchEnd();
                    state = AutoState.DRIVE_TO_INTAKE_ONE;
                }
                break;
            case DRIVE_TO_INTAKE_ONE:
                Actions.runBlocking(driveToIntakeOne);
                state = AutoState.SPIN_UP_TWO;
                break;
            case SPIN_UP_TWO:
                LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_NEAR + 153);
                state = AutoState.LAUNCH_TWO;
                break;
            case LAUNCH_TWO:
                if(LauncherMotor.getVelocity() >= (LAUNCH_TICK_VELOCITY_NEAR + 153)){
                    intakeUtil.launchStart();
                    state = AutoState.RESET_TWO;
                    launchTimer.reset();
                    autoUtil.resetEmptyTimer();
                }
                break;
            case RESET_TWO:
                if (launchTimer.seconds() >= Launch_Time || autoUtil.isBotEmpty()) {
                    LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_NEAR + 160);
                    intakeUtil.launchEnd();
                    state = AutoState.DRIVE_TO_INTAKE_TWO;
                    launchTimer.reset();
                }
                break;

            case DRIVE_TO_INTAKE_TWO:
                Actions.runBlocking(driveToIntakeTwo);
                state = AutoState.DRIVE_TO_LAUNCH_THREE;
                break;
            case DRIVE_TO_LAUNCH_THREE:
                LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_NEAR + 160);
                state = AutoState.LAUNCH_THREE;
                break;
            case LAUNCH_THREE:
                if(LauncherMotor.getVelocity() >= (LAUNCH_TICK_VELOCITY_NEAR + 160 )) {
                    intakeUtil.launchStart();
                    state = AutoState.RESET_THREE;
                    launchTimer.reset();
                    autoUtil.resetEmptyTimer();
                }
                break;
            case RESET_THREE:
                if (launchTimer.seconds() >= Launch_Time || autoUtil.isBotEmpty()) {
                    LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_NEAR + 190);
                    intakeUtil.launchEnd();
                    state = AutoState.DRIVE_TO_INTAKE_THREE;
                    launchTimer.reset();
                }
                break;
            case DRIVE_TO_INTAKE_THREE:
                timeoutTime = 1.25;
                Actions.runBlocking(driveToIntakeThree);
                state = AutoState.DRIVE_TO_LAUNCH_FOUR;
//                if(faultTimer.seconds() > 29.95){
//                    state = AutoState.PARK;
//                }
//                break;
            case DRIVE_TO_LAUNCH_FOUR:
                LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_NEAR + 190);
                state = AutoState.LAUNCH_FOUR;
                break;
            case LAUNCH_FOUR:
                if(LauncherMotor.getVelocity() >= (LAUNCH_TICK_VELOCITY_NEAR + 190)) {
                    intakeUtil.launchStart();
                    launchTimer.reset();
                    autoUtil.resetEmptyTimer();
                    autoUtil.changeTimeout(.2);
                    state = AutoState.RESET_FOUR;
                }
                break;
            case RESET_FOUR:
                if(launchTimer.seconds() >= Launch_Time || autoUtil.isBotEmpty()){
                    LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_NEAR + 190);
                    intakeUtil.launchEnd();
                    state = AutoState.PARK;
                }
                break;
            case PARK:
                LauncherMotor.setVelocity(0);
                intakeUtil.intakeOff();
                state = AutoState.END;
                break;
            case END:
                requestOpModeStop();
                break;
        }

    }
}

