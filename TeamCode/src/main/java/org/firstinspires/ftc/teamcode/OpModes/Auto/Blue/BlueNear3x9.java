package org.firstinspires.ftc.teamcode.OpModes.Auto.Blue;

import static org.firstinspires.ftc.teamcode.OpModes.Auto.PathFactory.blueNearLaunchPose;
import static org.firstinspires.ftc.teamcode.OpModes.Auto.PathFactory.bluePGPPickupStartPose;
import static org.firstinspires.ftc.teamcode.OpModes.Auto.PathFactory.bluePPGPickupStartPose;
import static org.firstinspires.ftc.teamcode.OpModes.Auto.PathFactory.intakeWaitTime;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.ConveyorMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.IntakeMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LeftSideFeedRoller;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.INTAKE_POWER;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCH_TICK_VELOCITY_NEAR;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.InstantAction;
import com.acmerobotics.roadrunner.InstantFunction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.OpModes.Auto.PathFactory;
import org.firstinspires.ftc.teamcode.Util.AprilTagMethod;
import org.firstinspires.ftc.teamcode._Proccedural.Components;

@Autonomous
public class BlueNear3x9 extends OpMode {
    MecanumDrive drive;

    InstantAction runIntake = new InstantAction(new InstantFunction() {
        @Override
        public void run() {
            LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_NEAR);
            IntakeMotor.setPower(0);
            ConveyorMotor.setPower(0);
            LeftSideFeedRoller.setPower(0);
        }
    });
    double BVM;

    enum AutoState{
        START,
        SPIN_UP,
        LAUNCH_ONE,
        RESET_ONE,
        DRIVE_TO_INTAKE_ONE,
        INTAKE_ONE,
        DRIVE_TO_LAUNCH_TWO,
        SPIN_UP_TWO,
        LAUNCH_TWO,
        RESET_TWO,
        DRIVE_TO_INTAKE_TWO,
        INTAKE_TWO,
        DRIVE_TO_LAUNCH_THREE,
        SPIN_UP_THREE,
        LAUNCH_THREE,
        RESET_THREE,
        DRIVE_TO_INTAKE_THREE,
        INTAKE_THREE,
        DRIVE_TO_LAUNCH_FOUR,
        SPIN_UP_FOUR,
        LAUNCH_FOUR,
        RESET_FOUR,
        //LAUNCH_FIVE,
        //RESET_FIVE,
        //LAUNCH_SIX,
        //RESET_SIX,
        //LAUNCH_SEVEN,
        //RESET_SEVEN,
        //LAUNCH_EIGHT,
        //RESET_EIGHT,
        //LAUNCH_NINE,
        //RESET_NINE,
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
    Action driveToIntakeThree;
    Action driveToLaunchFour;
    Action park;
    PathFactory factory;


    ElapsedTime launchTimer = new ElapsedTime(ElapsedTime.SECOND_IN_NANO);
    ElapsedTime intakeTimer = new ElapsedTime(ElapsedTime.SECOND_IN_NANO);
    ElapsedTime specialTimer = new ElapsedTime(ElapsedTime.SECOND_IN_NANO);



    @Override
    public void init() {

        drive = new MecanumDrive(hardwareMap, new Pose2d(-55,-50,Math.toRadians(-135)));
        factory = new PathFactory(drive);
        state = AutoState.START;
        Components.initComponents(hardwareMap);
        turnToLaunch = drive.actionBuilder( new Pose2d(-55,-50,Math.toRadians(-135)))
                .afterDisp(.5, runIntake)
                .lineToYLinearHeading(-19,Math.toRadians(-131))
                .build();
        //driveToIntakeOne = factory.bluePPGPickupPath(blueNearLaunchPose);
        driveToIntakeOne = drive.actionBuilder(blueNearLaunchPose)
                .splineToLinearHeading(new Pose2d(-13,-29,Math.toRadians(-90)),Math.toRadians(-90))
                .afterDisp(.1, runIntake)
                .waitSeconds(intakeWaitTime)
                .lineToY(-56)
                .build();
        telemetry.addLine("Trajectory 1 built");
        driveToLaunchOne = factory.blueNearLaunchPath(bluePPGPickupStartPose);
        telemetry.addLine("Trajectory 2 built");
        //driveToIntakeTwo = factory.bluePGPPickupPath(blueNearLaunchPose);
        driveToIntakeTwo = drive.actionBuilder(blueNearLaunchPose)
                .splineToLinearHeading(new Pose2d(11,-32,Math.toRadians(-90)),Math.toRadians(-90))
                .afterDisp(1, runIntake)
                .waitSeconds(intakeWaitTime)
                .lineToY(-41)
                .lineToY(-56)
                .build();
        telemetry.addLine("Trajectory 3 built");
        driveToLaunchThree = factory.blueNearLaunchPath(bluePGPPickupStartPose);
        telemetry.addLine("Trajectory 4 built");
        //driveToIntakeThree = factory.blueGPPPickupPath(blueNearLaunchPose);
        driveToIntakeThree = drive.actionBuilder(blueNearLaunchPose)
                .splineToLinearHeading(new Pose2d(36,-30,Math.toRadians(-90)),Math.toRadians(-90))
                .afterDisp(.1, runIntake)
                .waitSeconds(intakeWaitTime)
                .lineToY(-41)
                .lineToY(-56)
                .build();
        telemetry.addLine("Trajectory 5 built");
        driveToLaunchFour = factory.blueNearLaunchPath(bluePGPPickupStartPose);
        telemetry.addLine("Trajectory 6 built");
        park = factory.blueParkPath(blueNearLaunchPose);
        telemetry.addLine("Trajectory 7 built");
        telemetry.addLine("Ready to Start");
    }

    @Override
    public void start(){
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
                LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_NEAR);
                intakeTimer.reset();
                state = AutoState.LAUNCH_ONE;
                break;

            case LAUNCH_ONE:
                if (LauncherMotor.getVelocity() >= LAUNCH_TICK_VELOCITY_NEAR){ // do not change this time
                    IntakeMotor.setPower(INTAKE_POWER);
                    ConveyorMotor.setPower(INTAKE_POWER);
                    LeftSideFeedRoller.setPower(1);
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
                Actions.runBlocking(driveToIntakeOne);
                state = AutoState.INTAKE_ONE;
                break;
            case INTAKE_ONE:
                state = AutoState.DRIVE_TO_LAUNCH_TWO;
                break;
            case DRIVE_TO_LAUNCH_TWO:
                LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_NEAR);
                Actions.runBlocking(driveToLaunchOne);
                intakeTimer.reset();
                state = AutoState.SPIN_UP_TWO;
                break;
            case SPIN_UP_TWO:
                LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_NEAR);
                state = AutoState.LAUNCH_TWO;
                break;
            case LAUNCH_TWO:
                if(LauncherMotor.getVelocity() >= LAUNCH_TICK_VELOCITY_NEAR){
                    IntakeMotor.setPower(INTAKE_POWER);
                    ConveyorMotor.setPower(INTAKE_POWER);
                    LeftSideFeedRoller.setPower(1);
                    state = AutoState.RESET_TWO;
                    launchTimer.reset();
                }
                break;
            case RESET_TWO:
                if (launchTimer.seconds() >= 3) { // todo change Time
                    LauncherMotor.setVelocity(400);
                    IntakeMotor.setPower(0);
                    ConveyorMotor.setPower(0);
                    LeftSideFeedRoller.setPower(0);
                    state = AutoState.DRIVE_TO_INTAKE_TWO;
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
                LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_NEAR);
                Actions.runBlocking(driveToLaunchThree);
                state = AutoState.SPIN_UP_THREE;
                break;
            case SPIN_UP_THREE:
                LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_NEAR);
                state = AutoState.LAUNCH_THREE;
                break;
            case LAUNCH_THREE:
                if(LauncherMotor.getVelocity() >= LAUNCH_TICK_VELOCITY_NEAR) { // do not change this time
                    IntakeMotor.setPower(INTAKE_POWER);
                    ConveyorMotor.setPower(INTAKE_POWER);
                    LeftSideFeedRoller.setPower(1);
                    state = AutoState.RESET_THREE;
                    launchTimer.reset();
                }
                break;
            case RESET_THREE:
                if (launchTimer.seconds() >= 3) { // todo change Time
                    LauncherMotor.setVelocity(400);
                    IntakeMotor.setPower(0);
                    ConveyorMotor.setPower(0);
                    LeftSideFeedRoller.setPower(0);
                    state = AutoState.DRIVE_TO_INTAKE_THREE;
                    launchTimer.reset();
                }
                break;
            case DRIVE_TO_INTAKE_THREE:
                IntakeMotor.setPower(INTAKE_POWER);
                Actions.runBlocking(driveToIntakeThree);
                state = AutoState.INTAKE_THREE;
                break;
            case INTAKE_THREE:
                state = AutoState.DRIVE_TO_LAUNCH_FOUR;
                break;
            case DRIVE_TO_LAUNCH_FOUR:
                LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_NEAR);
                Actions.runBlocking(driveToLaunchThree);
                state = AutoState.SPIN_UP_FOUR;
                break;
            case SPIN_UP_FOUR:
                LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_NEAR);
                state = AutoState.LAUNCH_FOUR;
                break;
            case LAUNCH_FOUR:
                if(LauncherMotor.getVelocity() >= LAUNCH_TICK_VELOCITY_NEAR) { // do not change this time
                    IntakeMotor.setPower(INTAKE_POWER);
                    ConveyorMotor.setPower(INTAKE_POWER);
                    LeftSideFeedRoller.setPower(1);
                    state = AutoState.RESET_FOUR;
                    launchTimer.reset();
                }
                break;
            case RESET_FOUR:
                if (launchTimer.seconds() >= 3) {
                    LauncherMotor.setVelocity(400);
                    IntakeMotor.setPower(0);
                    ConveyorMotor.setPower(0);
                    LeftSideFeedRoller.setPower(0);
                    state = AutoState.END;
                    launchTimer.reset();
                }
                break;

            case END:
                Actions.runBlocking(park);
                requestOpModeStop();
                break;
        }

    }
}

