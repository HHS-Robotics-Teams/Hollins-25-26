package org.firstinspires.ftc.teamcode.OpModes.Auto.Red;

import static org.firstinspires.ftc.teamcode.OpModes.Auto.PathFactory.redFarLaunchPose;
import static org.firstinspires.ftc.teamcode.OpModes.Auto.PathFactory.intakeWaitTime;
import static org.firstinspires.ftc.teamcode.Util.LauncherUtil.LaunchState.EXIT;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.IntakeMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherFingerServo;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherSafetyServo;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LeftSideFeedRoller;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.INTAKE_POWER;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_FINGER_DOWN_POS;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCH_TICK_VELOCITY_FAR;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.SAFETY_HOLDING;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.InstantAction;
import com.acmerobotics.roadrunner.InstantFunction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.OpModes.Auto.PathFactory;
import org.firstinspires.ftc.teamcode.Util.LauncherUtil;
import org.firstinspires.ftc.teamcode._Proccedural.Components;

@Autonomous
public class RedFarAutolaunch3Plus6 extends OpMode {
    MecanumDrive drive;
    InstantAction IntakePickup = new InstantAction(new InstantFunction() {
        @Override
        public void run() {
            IntakeMotor.setPower(INTAKE_POWER);
            LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
            LeftSideFeedRoller.setPower(0);
            LauncherSafetyServo.setPosition(SAFETY_HOLDING);
        }
    });

    enum AutoState{
        START,
        LAUNCH_ONE,
        LAUNCH_TWO,
        LAUNCH_THREE,
        DRIVE_TO_INTAKE_ONE,
        DRIVE_TO_LAUNCH_TWO,
        DRIVE_TO_INTAKE_TWO,
        DRIVE_TO_LAUNCH_THREE,
        PARK,
        END
    }
    AutoState state = AutoState.START;
    Action turnToLaunch;
    Action driveToIntakeOne;
    Action driveToLaunchOne;
    Action driveToIntakeTwo;
    Action driveToLaunchThree;
    PathFactory factory;
    LauncherUtil launcherUtil;

    @Override
    public void init() {

        state = AutoState.START;
        drive = new MecanumDrive(hardwareMap, new Pose2d(72 - (16.25/2), ((double) 13 / 2), Math.toRadians(180)));
        factory = new PathFactory(drive);
        Components.initComponents(hardwareMap);
        launcherUtil = new LauncherUtil("RED");
        turnToLaunch = drive.actionBuilder(new Pose2d(72 - (16.25/2), ((double) 13 / 2), Math.toRadians(180)))
                .splineToLinearHeading(redFarLaunchPose, Math.toRadians(175))
                .build();

        driveToIntakeOne = drive.actionBuilder(redFarLaunchPose)
                .splineToSplineHeading(new Pose2d(39,30,Math.toRadians(90)),Math.toRadians(90))
                .afterDisp(1,IntakePickup)
                .waitSeconds(intakeWaitTime)
                .lineToY(41)
                .waitSeconds(.3)
                .lineToY(48)
                .build();

        driveToLaunchOne = factory.redFarLaunchPath(new Pose2d(39,48,Math.toRadians(90)));

        driveToIntakeTwo = drive.actionBuilder(redFarLaunchPose)
                .splineToSplineHeading(new Pose2d(16,30,Math.toRadians(90)),Math.toRadians(90))
                .afterDisp(1,IntakePickup)
                .waitSeconds(intakeWaitTime)
                .waitSeconds(.3)
                .lineToY(48)
                .build();

        driveToLaunchThree = factory.redFarLaunchPath(new Pose2d(16,48,Math.toRadians(90)));
    }

    @Override
    public void start(){
        LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_FAR);
        LauncherSafetyServo.setPosition(SAFETY_HOLDING);
        launcherUtil.cancelLaunch();
        Actions.runBlocking(turnToLaunch);
    }

    @Override
    public void loop() {
        telemetry.addData("State:", state);
        telemetry.addData("Launcher Velocity", LauncherMotor.getVelocity());
        telemetry.addData("Launcher Current", LauncherMotor.getCurrent(CurrentUnit.AMPS));
        telemetry.update();

        switch (state){
            case START:
                state = AutoState.LAUNCH_ONE;
                break;
            case LAUNCH_ONE:
                launcherUtil.runLauncher();
                if(launcherUtil.getLaunchState() == EXIT){
                    launcherUtil.cancelLaunch();
                    state = AutoState.DRIVE_TO_INTAKE_ONE;
                }
                break;
            case DRIVE_TO_INTAKE_ONE:
                Actions.runBlocking(driveToIntakeOne);
                state = AutoState.DRIVE_TO_LAUNCH_TWO;
                break;
            case DRIVE_TO_LAUNCH_TWO:
                IntakeMotor.setPower(0);
                LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_FAR);
                Actions.runBlocking(driveToLaunchOne);
                state = AutoState.LAUNCH_TWO;
                break;
            case LAUNCH_TWO:
                launcherUtil.runLauncher();
                if(launcherUtil.getLaunchState() == EXIT){
                    launcherUtil.cancelLaunch();
                    state = AutoState.DRIVE_TO_INTAKE_TWO;
                }
                break;
            case DRIVE_TO_INTAKE_TWO:
                IntakeMotor.setPower(INTAKE_POWER);
                LauncherSafetyServo.setPosition(SAFETY_HOLDING);
                Actions.runBlocking(driveToIntakeTwo);
                state = AutoState.DRIVE_TO_LAUNCH_THREE;
                break;
            case DRIVE_TO_LAUNCH_THREE:
                LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_FAR - 25);
                IntakeMotor.setPower(0);
                Actions.runBlocking(driveToLaunchThree);
                state = AutoState.LAUNCH_THREE;
                break;
            case LAUNCH_THREE:
                launcherUtil.runLauncher();
                if(launcherUtil.getLaunchState() == EXIT){
                    launcherUtil.cancelLaunch();
                    state = AutoState.PARK;
                }
                break;
            case PARK:
                LauncherMotor.setPower(0);
                LeftSideFeedRoller.setPower(0);
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
                Actions.runBlocking(drive.actionBuilder(redFarLaunchPose).lineToX(30).build());
                state = AutoState.END;
                break;
            case END:
                requestOpModeStop();
                break;
        }

    }
}