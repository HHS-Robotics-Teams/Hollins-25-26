package org.firstinspires.ftc.teamcode._OpModes.Auto.zOLD.Blue;

import static org.firstinspires.ftc.teamcode._OpModes.Auto.zOLD.PathFactory.blueFarLaunchPose;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.IntakeMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.INTAKE_POWER;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_FAR_TARGET;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCH_TICK_VELOCITY_FAR;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.InstantFunction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode._Util.AprilTagMethod;
import org.firstinspires.ftc.teamcode._Util.OLD.LauncherUtil;
import org.firstinspires.ftc.teamcode._Proccedural.Components;

@Autonomous
@Disabled
@Deprecated
public class UtilBlueFarHumPlayerGate extends OpMode {
    MecanumDrive drive;

    enum AutoState{
        START,
        SPIN_UP,
        LAUNCH_ONE,
        LAUNCH_TWO,
        LAUNCH_THREE,
        DRIVE_TO_INTAKE_ONE,
        INTAKE_ONE,
        DRIVE_TO_LAUNCH_TWO,
        SPIN_UP_TWO,
        DRIVE_TO_INTAKE_TWO,
        INTAKE_TWO,
        DRIVE_TO_LAUNCH_THREE,
        SPIN_UP_THREE,
        PARK,
        END
    }
    AutoState state = AutoState.START;
    AprilTagMethod aprilTagMethod = new AprilTagMethod();
    LauncherUtil launcherUtil;
    Action turnToLaunch;
    Action driveToIntakeOne;
    Action driveToLaunchOne;
    Action driveToIntakeTwo;
    Action driveToLaunchTwo;

    @Override
    public void init() {
        drive = new MecanumDrive(hardwareMap, new Pose2d(72 - (16.25/2), -(13 / 2), Math.toRadians(180)));
        state = AutoState.START;
        Components.initComponents(hardwareMap);
        launcherUtil = new LauncherUtil("BLUE", true);
        turnToLaunch = drive.actionBuilder(new Pose2d(72 - (16.25/2), -(13 / 2), Math.toRadians(180)))
                .splineToLinearHeading(blueFarLaunchPose, Math.toRadians(-175))
                .afterDisp(0.01, new InstantFunction() {
                    @Override
                    public void run() {
                        LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_FAR);
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
                .strafeToLinearHeading(new Vector2d(60,-56),Math.toRadians(-90))
                .waitSeconds(0.2)
                .lineToYConstantHeading(-58)
                .strafeToLinearHeading(new Vector2d(61,-58),Math.toRadians(-80))
                .afterDisp(60, new InstantFunction() {
                    @Override
                    public void run() {
                        IntakeMotor.setPower(0);
                    }
                })
                .lineToXConstantHeading(63)
                .waitSeconds(0.2)
                .strafeToLinearHeading(new Vector2d(50,-10),Math.toRadians(-155))
                .build();
    }

    @Override
    public void start() {
        launcherUtil.setTarget(LAUNCH_TICK_VELOCITY_FAR + 125);
        LauncherMotor.setVelocity(LAUNCHER_FAR_TARGET);
        Actions.runBlocking(turnToLaunch);
    }

    @Override
    public void loop() {
        telemetry.addData("State:", state);
        telemetry.addData("Launcher Velocity", LauncherMotor.getVelocity());
        telemetry.addData("Launcher Current", LauncherMotor.getCurrent(CurrentUnit.AMPS));
        switch (state) {
            case START:
                state = AutoState.SPIN_UP;
                break;
            case SPIN_UP:
                state = AutoState.LAUNCH_ONE;
                break;
            case LAUNCH_ONE:
                telemetry.addLine(launcherUtil.runLauncher());
                if(launcherUtil.getLaunchState().equals(LauncherUtil.LaunchState.EXIT)){
                    state = AutoState.DRIVE_TO_INTAKE_ONE;
                }
                break;
            case DRIVE_TO_INTAKE_ONE:
                Actions.runBlocking(driveToIntakeOne);
                state = AutoState.LAUNCH_TWO;
                break;
            case LAUNCH_TWO:
                telemetry.addLine(launcherUtil.runLauncher());
                if(launcherUtil.getLaunchState().equals(LauncherUtil.LaunchState.EXIT)){
                    state = AutoState.DRIVE_TO_INTAKE_TWO;
                }
                break;

        }
    }
}
