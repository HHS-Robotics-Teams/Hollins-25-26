package org.firstinspires.ftc.teamcode.OpModes.Auto.Blue;

import static org.firstinspires.ftc.teamcode.OpModes.Auto.PathFactory.blueFarLaunchPose;
import static org.firstinspires.ftc.teamcode.OpModes.Auto.PathFactory.intakeWaitTime;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.ConveyorMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.IntakeMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherFingerServo;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherHoodServo;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherSafetyServo;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LeftSideFeedRoller;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.INTAKE_POWER;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_FINGER_DOWN_POS;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_FINGER_UP_POS;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCH_TICK_VELOCITY_FAR;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCH_TICK_VELOCITY_NEAR;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.SAFETY_HOLDING;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.SAFTEY_FIRING;
import static java.lang.Math.abs;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.InstantAction;
import com.acmerobotics.roadrunner.InstantFunction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.OpModes.Auto.PathFactory;
import org.firstinspires.ftc.teamcode.Util.AprilTagMethod;
import org.firstinspires.ftc.teamcode._Proccedural.Components;
@Deprecated
@Disabled
@Autonomous
public class BlueVeryFar3x6 extends OpMode {
    MecanumDrive drive;
    InstantAction IntakePickup = new InstantAction(new InstantFunction() {
        @Override
        public void run() {
            IntakeMotor.setPower(INTAKE_POWER);
            IntakeMotor.setPower(0);
            ConveyorMotor.setPower(0);
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
    ElapsedTime specialTimer = new ElapsedTime(ElapsedTime.SECOND_IN_NANO);



    @Override
    public void init() {
        drive = new MecanumDrive(hardwareMap, new Pose2d(72 - (16.25/2), -(13 / 2), Math.toRadians(180)));
        factory = new PathFactory(drive);
        state = AutoState.START;
        Components.initComponents(hardwareMap);
        turnToLaunch = drive.actionBuilder(new Pose2d(72 - (16.25/2), -(13 / 2), Math.toRadians(180)))
                .splineToLinearHeading(blueFarLaunchPose, Math.toRadians(-175))
                .afterDisp(0.01, new InstantFunction() {
                    @Override
                    public void run() {
                        LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_FAR );
                    }
                })
                .build();
        driveToIntakeOne = drive.actionBuilder(blueFarLaunchPose)
                .afterDisp(40, new InstantFunction() {
                    @Override
                    public void run() {
                        IntakeMotor.setPower(INTAKE_POWER);
                    }
                })
                .splineToLinearHeading(new Pose2d(60,-58,Math.toRadians(-90)),Math.toRadians(-90))
                .waitSeconds(0.2)
                //.lineToYConstantHeading(-58)
                .strafeToLinearHeading(new Vector2d(61,-58),Math.toRadians(-60))
                .waitSeconds(0.2)
                .splineToLinearHeading(new Pose2d(64,-60,Math.toRadians(-40)),Math.toRadians(-90))
                .waitSeconds(0.2)

                .build();
        driveToLaunchOne = factory.blueFarLaunchPath(new Pose2d(39,-48,Math.toRadians(-90)));
        driveToIntakeTwo = drive.actionBuilder(blueFarLaunchPose)
                .splineToSplineHeading(new Pose2d(39,-30,Math.toRadians(-90)),Math.toRadians(-90))
                .afterDisp(1,IntakePickup)
                .waitSeconds(intakeWaitTime)
                .lineToY(-41)
                .waitSeconds(.3)
                .lineToY(-48)
                .build();
        driveToLaunchThree = factory.blueFarLaunchPath(new Pose2d(39,-48,Math.toRadians(-90)));
    }

    @Override
    public void start(){
        LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_FAR);
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
                LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_FAR);
                intakeTimer.reset();
                state = AutoState.SPIN_UP;
                break;

            case SPIN_UP:
                LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_FAR);
                state = AutoState.LAUNCH_ONE;
                break;
            case LAUNCH_ONE:
                if (LauncherMotor.getVelocity() >= LAUNCH_TICK_VELOCITY_FAR){ // do not change this time
                    IntakeMotor.setPower(INTAKE_POWER);
                    ConveyorMotor.setPower(INTAKE_POWER);
                    LeftSideFeedRoller.setPower(INTAKE_POWER);
                    launchTimer.reset();
                    state = AutoState.RESET_ONE;
                }
                break;
            case RESET_ONE:
                if (launchTimer.seconds() >= 3) { // todo change Time
                    LauncherMotor.setVelocity(400);
                    IntakeMotor.setPower(0);
                    ConveyorMotor.setPower(0);
                    LeftSideFeedRoller.setPower(0);
                    state = AutoState.DRIVE_TO_INTAKE_ONE;
                }
                break;
            case DRIVE_TO_INTAKE_ONE:
                state = AutoState.DRIVE_TO_LAUNCH_TWO;
                Actions.runBlocking(driveToIntakeOne);
                break;
            case DRIVE_TO_LAUNCH_TWO:
                Actions.runBlocking(driveToLaunchOne);
                intakeTimer.reset();
                state = AutoState.SPIN_UP_TWO;
                break;
            case SPIN_UP_TWO:
                LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_FAR);
                state = AutoState.LAUNCH_TWO;
                launchTimer.reset();
                break;
            case LAUNCH_TWO:
                if(LauncherMotor.getVelocity() >= LAUNCH_TICK_VELOCITY_FAR){
                    IntakeMotor.setPower(INTAKE_POWER);
                    ConveyorMotor.setPower(INTAKE_POWER);
                    LeftSideFeedRoller.setPower(INTAKE_POWER);
                    launchTimer.reset();
                    state = AutoState.RESET_TWO;
                }
                break;
            case RESET_TWO:
                if (launchTimer.seconds() >= 3) { // todo change Time
                    LauncherMotor.setVelocity(400);
                    IntakeMotor.setPower(0);
                    ConveyorMotor.setPower(0);
                    LeftSideFeedRoller.setPower(0);
                    state = AutoState.DRIVE_TO_LAUNCH_TWO;
                    launchTimer.reset();
                }
                break;
            case DRIVE_TO_INTAKE_TWO:
                IntakeMotor.setPower(INTAKE_POWER);
                Actions.runBlocking(driveToIntakeTwo);
                state = AutoState.INTAKE_TWO;
                break;
            case INTAKE_TWO:
                state = AutoState.DRIVE_TO_LAUNCH_THREE;
                break;
            case DRIVE_TO_LAUNCH_THREE:
                Actions.runBlocking(driveToLaunchThree);
                intakeTimer.reset();
                state = AutoState.SPIN_UP_THREE;
                break;
            case SPIN_UP_THREE:
                LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_FAR);
                state = AutoState.LAUNCH_THREE;
                break;
            case LAUNCH_THREE:
                if (launchTimer.seconds() >= 3) { // todo change Time
                    LauncherMotor.setVelocity(400);
                    IntakeMotor.setPower(INTAKE_POWER);
                    ConveyorMotor.setPower(INTAKE_POWER);
                    LeftSideFeedRoller.setPower(INTAKE_POWER);
                    launchTimer.reset();
                    state = AutoState.RESET_THREE;
                }
                break;
            case RESET_THREE:
                LauncherMotor.setVelocity(500);
                IntakeMotor.setPower(0);
                ConveyorMotor.setPower(0);
                LeftSideFeedRoller.setPower(0);
                state = AutoState.PARK;
                launchTimer.reset();
                intakeTimer.reset();
                break;

            case PARK:
                LauncherMotor.setVelocity(400);
                Actions.runBlocking(drive.actionBuilder(blueFarLaunchPose).lineToX(30).build());
                state = AutoState.END;
                break;
            case END:
                requestOpModeStop();
                break;
        }

    }
}

