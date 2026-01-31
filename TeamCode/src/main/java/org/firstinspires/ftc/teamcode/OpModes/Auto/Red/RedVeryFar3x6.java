package org.firstinspires.ftc.teamcode.OpModes.Auto.Red;

import static org.firstinspires.ftc.teamcode._Proccedural.Components.ConveyorMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.IntakeMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherSafetyServo;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LeftSideFeedRoller;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.INTAKE_POWER;
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
import org.firstinspires.ftc.teamcode.OpModes.Auto.PathFactory;
import org.firstinspires.ftc.teamcode.Util.AprilTagMethod;
import org.firstinspires.ftc.teamcode.Util.LightUtil;
import org.firstinspires.ftc.teamcode._Proccedural.Components;

@Autonomous
public class RedVeryFar3x6 extends OpMode {
    MecanumDrive drive;
    InstantAction IntakePickup = new InstantAction(new InstantFunction() {
        @Override
        public void run() {
            IntakeMotor.setPower(INTAKE_POWER);
            ConveyorMotor.setPower(0.75);
            LeftSideFeedRoller.setPower(0);

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
    AprilTagMethod aprilTagMethod = new AprilTagMethod();
    Action turnToLaunch;
    Action driveToIntakeOne;
    Action driveToLaunchOne;
    Action driveToIntakeTwo;
    Action driveToLaunchThree;
    PathFactory factory;



    ElapsedTime launchTimer = new ElapsedTime(ElapsedTime.SECOND_IN_NANO);
    ElapsedTime intakeTimer = new ElapsedTime(ElapsedTime.SECOND_IN_NANO);



    @Override
    public void init() {
        drive = new MecanumDrive(hardwareMap, new Pose2d(72 - (16.25/2), (13 / 2), Math.toRadians(180)));
        factory = new PathFactory(drive);
        state = AutoState.START;
        Components.initComponents(hardwareMap);
        turnToLaunch = drive.actionBuilder(new Pose2d(72 - (16.25/2), (13 / 2), Math.toRadians(180)))
                .splineToLinearHeading(new Pose2d(50, 10, Math.toRadians(155)), Math.toRadians(175))
                .afterDisp(0.01, new InstantFunction() {
                    @Override
                    public void run() {
                        LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_FAR + 250);
                    }
                })
                .build();
        driveToIntakeOne = drive.actionBuilder(new Pose2d(50, 10, Math.toRadians(155)))
                .afterDisp(20, new InstantFunction() {
                    @Override
                    public void run() {
                        IntakeMotor.setPower(1);
                        ConveyorMotor.setPower(1);
                    }
                })
                .strafeToLinearHeading(new Vector2d(64,54),Math.toRadians(90))
                .waitSeconds(.1)
                .strafeToLinearHeading(new Vector2d(70,58),Math.toRadians(60))
                .waitSeconds(0.4)
                .turnTo(Math.toRadians(90))
                .lineToY(66)
                .waitSeconds(0.2)
                .strafeToLinearHeading(new Vector2d(52, 10), Math.toRadians(155))
                .afterDisp(65, new InstantFunction() {
                    @Override
                    public void run() {
                        IntakeMotor.setPower(0);
                        ConveyorMotor.setPower(0);
                    }
                })
                .build();
        driveToIntakeTwo = drive.actionBuilder(new Pose2d(52, 10, Math.toRadians(155)))
                .splineToSplineHeading(new Pose2d(39,28,Math.toRadians(90)),Math.toRadians(90))
                .afterDisp(1,IntakePickup)
                .waitSeconds(0.2)
                .lineToY(41)
                .lineToY(59)
                .waitSeconds(0.2)
                .splineToLinearHeading(new Pose2d(52, 10, Math.toRadians(150)),Math.toRadians(155.75))
                .build();
        telemetry.addLine("Ready to Launch");
        LightUtil util = new LightUtil(2, hardwareMap);
        util.makeGreen();
        util.updateLights();
    }

    @Override
    public void start(){
        LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_FAR + 200);
        Actions.runBlocking(turnToLaunch);
    }

    @Override
    public void loop() {
        telemetry.addData("State:", state);
        telemetry.addData("Launcher Velocity", LauncherMotor.getVelocity());
        telemetry.addData( "Launch Timer", launchTimer.seconds());
        telemetry.addData("Intake timer", intakeTimer.seconds());
        telemetry.addData("Launcher Current", LauncherMotor.getCurrent(CurrentUnit.AMPS));
        telemetry.update();

        switch (state){
            case START:
                intakeTimer.reset();
                state = AutoState.SPIN_UP;
                break;

            case SPIN_UP:
                LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_FAR + 200);
                state = AutoState.LAUNCH_ONE;
                LauncherSafetyServo.setPosition(SAFTEY_FIRING);
                break;
            case LAUNCH_ONE:
                if (LauncherMotor.getVelocity() >= LAUNCH_TICK_VELOCITY_FAR + 200){ // do not change this time
                    IntakeMotor.setPower(0.75);
                    ConveyorMotor.setPower(0.75);
                    LeftSideFeedRoller.setPower(INTAKE_POWER);
                    launchTimer.reset();
                    state = AutoState.RESET_ONE;
                }
                break;
            case RESET_ONE:
                if (launchTimer.seconds() >= Launch_Time) { // todo change Time
                    LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_FAR);
                    IntakeMotor.setPower(0);
                    ConveyorMotor.setPower(0);
                    LeftSideFeedRoller.setPower(0);
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
                state = AutoState.SPIN_UP_TWO;
                break;
            case SPIN_UP_TWO:
                LauncherSafetyServo.setPosition(SAFTEY_FIRING);
                LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_FAR + 275);
                state = AutoState.LAUNCH_TWO;
                launchTimer.reset();
                break;
            case LAUNCH_TWO:
                if(LauncherMotor.getVelocity() >= LAUNCH_TICK_VELOCITY_FAR + 275){
                    IntakeMotor.setPower(0.75);
                    ConveyorMotor.setPower(0.75);
                    LeftSideFeedRoller.setPower(INTAKE_POWER);
                    launchTimer.reset();
                    state = AutoState.RESET_TWO;
                }
                break;
            case RESET_TWO:
                if (launchTimer.seconds() >= Launch_Time) { // todo change Time
                    LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_FAR);
                    IntakeMotor.setPower(0);
                    ConveyorMotor.setPower(0);
                    LeftSideFeedRoller.setPower(0);
                    state = AutoState.DRIVE_TO_INTAKE_TWO;
                }
                break;
            case DRIVE_TO_INTAKE_TWO:
                LauncherSafetyServo.setPosition(SAFETY_HOLDING);
                IntakeMotor.setPower(INTAKE_POWER);
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
                LauncherSafetyServo.setPosition(SAFTEY_FIRING);
                LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_FAR + 250);
                state = AutoState.LAUNCH_THREE;
                break;
            case LAUNCH_THREE:
                if (LauncherMotor.getVelocity() >= LAUNCH_TICK_VELOCITY_FAR + 250) { // todo change Time
                    IntakeMotor.setPower(0.75);
                    ConveyorMotor.setPower(0.75);
                    LeftSideFeedRoller.setPower(INTAKE_POWER);
                    launchTimer.reset();
                    state = AutoState.RESET_THREE;
                }
                break;
            case RESET_THREE:
                if(launchTimer.seconds() >= Launch_Time) {
                    IntakeMotor.setPower(0);
                    ConveyorMotor.setPower(0);
                    LeftSideFeedRoller.setPower(0);
                    state = AutoState.PARK;
                }
                break;

            case PARK:
                LauncherSafetyServo.setPosition(SAFETY_HOLDING);
                LauncherMotor.setVelocity(400);
                Actions.runBlocking(drive.actionBuilder(new Pose2d(50, 10, Math.toRadians(162.5))).lineToX(40).build());
                state = AutoState.END;
                break;
            case END:
                requestOpModeStop();
                break;
        }

    }
}

