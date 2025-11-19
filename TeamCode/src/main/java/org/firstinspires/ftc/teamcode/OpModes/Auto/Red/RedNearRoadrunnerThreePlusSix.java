package org.firstinspires.ftc.teamcode.OpModes.Auto.Red;

import static org.firstinspires.ftc.teamcode.OpModes.Auto.PathFactory.blueFarLaunchPose;
import static org.firstinspires.ftc.teamcode.OpModes.Auto.PathFactory.blueNearLaunchPose;
import static org.firstinspires.ftc.teamcode.OpModes.Auto.PathFactory.bluePGPPickupStartPose;
import static org.firstinspires.ftc.teamcode.OpModes.Auto.PathFactory.bluePPGPickupStartPose;
import static org.firstinspires.ftc.teamcode.OpModes.Auto.PathFactory.redNearLaunchPose;
import static org.firstinspires.ftc.teamcode.OpModes.Auto.PathFactory.redPGPPickupStartPose;
import static org.firstinspires.ftc.teamcode.OpModes.Auto.PathFactory.redPPGPickupStartPose;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.IntakeMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherFingerServo;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LeftSideFeedRoller;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.INTAKE_POWER;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_FINGER_DOWN_POS;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_FINGER_UP_POS;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_NEAR_TARGET;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCH_THRESHOLD;
import static java.lang.Math.abs;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.HollinsMadeUtil.AprilTagMethod;
import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.OpModes.Auto.PathFactory;
import org.firstinspires.ftc.teamcode._Proccedural.Components;

@Autonomous
public class RedNearRoadrunnerThreePlusSix extends OpMode {

    MecanumDrive drive;

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
        drive = new MecanumDrive(hardwareMap, new Pose2d(-65,36,Math.toRadians(-45)));
        factory = new PathFactory(drive);
        state = AutoState.START;
        Components.initComponents(hardwareMap);
        turnToLaunch = factory.redNearLaunchPath(new Pose2d(-65,36,Math.toRadians(-45)));
        driveToIntakeOne = factory.redPPGPickupPath(redNearLaunchPose);
        telemetry.addLine("Trajectory 1 built");
        driveToLaunchOne = factory.redNearLaunchPath(redPPGPickupStartPose);
        telemetry.addLine("Trajectory 2 built");
        driveToIntakeTwo = factory.redPGPPickupPath(redNearLaunchPose);
        telemetry.addLine("Trajectory 3 built");
        driveToLaunchThree = factory.redNearLaunchPath(redPGPPickupStartPose);
        telemetry.addLine("Trajectory 4 built");
        telemetry.addLine("Ready to Start");
    }

    @Override
    public void start(){
        LauncherMotor.setPower(1);
    }

    @Override
    public void loop() {
        telemetry.addData("State:", state);
        switch (state){
            case START:
                LauncherMotor.setVelocity(LAUNCHER_NEAR_TARGET, AngleUnit.RADIANS);
                Actions.runBlocking(turnToLaunch);
                state = AutoState.SPIN_UP;
                break;
            case SPIN_UP:
                if(abs(LauncherMotor.getVelocity(AngleUnit.RADIANS) - LAUNCHER_NEAR_TARGET) <= LAUNCH_THRESHOLD){
                    state = AutoState.LAUNCH_ONE;
                    launchTimer.reset();
                }
                break;
            case LAUNCH_ONE:
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
                LeftSideFeedRoller.setPower(1);
                if(launchTimer.seconds() >= 0.8){
                    state = AutoState.RESET_ONE;
                    intakeTimer.reset();
                }
                break;
            case RESET_ONE:
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
                LeftSideFeedRoller.setPower(0);
                IntakeMotor.setPower(INTAKE_POWER);
                if(abs(LauncherMotor.getVelocity(AngleUnit.RADIANS) - LAUNCHER_NEAR_TARGET) <= LAUNCH_THRESHOLD && intakeTimer.seconds() >= .6){
                    state = AutoState.LAUNCH_TWO;
                    launchTimer.reset();
                }
                break;
            case LAUNCH_TWO:
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
                LeftSideFeedRoller.setPower(1);
                if(launchTimer.seconds() >= 0.8){
                    state = AutoState.RESET_TWO;
                    intakeTimer.reset();
                }
                break;
            case RESET_TWO:
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
                LeftSideFeedRoller.setPower(0);
                IntakeMotor.setPower(INTAKE_POWER);
                if(abs(LauncherMotor.getVelocity(AngleUnit.RADIANS) - LAUNCHER_NEAR_TARGET) <= LAUNCH_THRESHOLD && intakeTimer.seconds() > .5){
                    state = AutoState.LAUNCH_THREE;
                    launchTimer.reset();
                }
                break;
            case LAUNCH_THREE:
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
                LeftSideFeedRoller.setPower(1);
                if(launchTimer.seconds() >= 0.8){
                    state = AutoState.RESET_THREE;
                    intakeTimer.reset();
                }
                break;
            case RESET_THREE:
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
                LeftSideFeedRoller.setPower(0);
                IntakeMotor.setPower(INTAKE_POWER);
                state = AutoState.DRIVE_TO_INTAKE_ONE;
                launchTimer.reset();
                intakeTimer.reset();
                break;
            case DRIVE_TO_INTAKE_ONE:
                Actions.runBlocking(driveToIntakeOne);
                state = AutoState.INTAKE_ONE;
                break;
            case INTAKE_ONE:
                state = AutoState.DRIVE_TO_LAUNCH_TWO;
                break;
            case DRIVE_TO_LAUNCH_TWO:
                Actions.runBlocking(driveToLaunchOne);
                state = AutoState.SPIN_UP_TWO;
                break;
            case SPIN_UP_TWO:
                if(abs(LauncherMotor.getVelocity(AngleUnit.RADIANS) - LAUNCHER_NEAR_TARGET) <= LAUNCH_THRESHOLD && intakeTimer.seconds() >= .6){
                    state = AutoState.LAUNCH_FOUR;
                    launchTimer.reset();
                }
                break;
            case LAUNCH_FOUR:
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
                LeftSideFeedRoller.setPower(1);
                if(launchTimer.seconds() >= 0.8){
                    state = AutoState.RESET_FOUR;
                    intakeTimer.reset();
                }
                break;
            case RESET_FOUR:
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
                LeftSideFeedRoller.setPower(0);
                IntakeMotor.setPower(INTAKE_POWER);
                if(abs(LauncherMotor.getVelocity(AngleUnit.RADIANS) - LAUNCHER_NEAR_TARGET) <= LAUNCH_THRESHOLD && intakeTimer.seconds() >= .6){
                    state = AutoState.LAUNCH_FIVE;
                    launchTimer.reset();
                }
                break;
            case LAUNCH_FIVE:
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
                LeftSideFeedRoller.setPower(1);
                if(launchTimer.seconds() >= 0.8){
                    state = AutoState.RESET_FIVE;
                    intakeTimer.reset();
                }
                break;
            case RESET_FIVE:
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
                LeftSideFeedRoller.setPower(0);
                IntakeMotor.setPower(INTAKE_POWER);
                if(abs(LauncherMotor.getVelocity(AngleUnit.RADIANS) - LAUNCHER_NEAR_TARGET) <= LAUNCH_THRESHOLD && intakeTimer.seconds() >= .6){
                    state = AutoState.LAUNCH_SIX;
                    launchTimer.reset();
                }
                break;
            case LAUNCH_SIX:
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
                LeftSideFeedRoller.setPower(1);
                IntakeMotor.setPower(0);
                if(launchTimer.seconds() >= 0.8){
                    state = AutoState.RESET_SIX;
                    intakeTimer.reset();
                }
                break;
            case RESET_SIX:
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
                LeftSideFeedRoller.setPower(0);
                IntakeMotor.setPower(INTAKE_POWER);
                state = AutoState.DRIVE_TO_INTAKE_TWO;
                launchTimer.reset();
                intakeTimer.reset();
                break;
            case DRIVE_TO_INTAKE_TWO:
                Actions.runBlocking(driveToIntakeTwo);
                state = AutoState.INTAKE_TWO;
                break;
            case INTAKE_TWO:
                state = AutoState.DRIVE_TO_LAUNCH_THREE;
                break;
            case DRIVE_TO_LAUNCH_THREE:
                Actions.runBlocking(driveToLaunchThree);
                state = AutoState.SPIN_UP_THREE;
                break;
            case SPIN_UP_THREE:
                if(abs(LauncherMotor.getVelocity(AngleUnit.RADIANS) - LAUNCHER_NEAR_TARGET) <= LAUNCH_THRESHOLD && intakeTimer.seconds() >= .6){
                    state = AutoState.LAUNCH_SEVEN;
                    launchTimer.reset();
                }
                break;
            case LAUNCH_SEVEN:
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
                LeftSideFeedRoller.setPower(1);
                IntakeMotor.setPower(0);
                if(launchTimer.seconds() >= 0.8){
                    state = AutoState.RESET_SEVEN;
                    intakeTimer.reset();
                }
                break;
            case RESET_SEVEN:
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
                LeftSideFeedRoller.setPower(0);
                IntakeMotor.setPower(INTAKE_POWER);
                if(abs(LauncherMotor.getVelocity(AngleUnit.RADIANS) - LAUNCHER_NEAR_TARGET) <= LAUNCH_THRESHOLD && intakeTimer.seconds() >= .6){
                    state = AutoState.LAUNCH_EIGHT;
                    launchTimer.reset();
                }
                break;
            case LAUNCH_EIGHT:
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
                LeftSideFeedRoller.setPower(1);
                IntakeMotor.setPower(0);
                if(launchTimer.seconds() >= 0.8){
                    state = AutoState.RESET_EIGHT;
                    intakeTimer.reset();
                }
                break;
            case RESET_EIGHT:
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
                LeftSideFeedRoller.setPower(0);
                IntakeMotor.setPower(INTAKE_POWER);
                if(abs(LauncherMotor.getVelocity(AngleUnit.RADIANS) - LAUNCHER_NEAR_TARGET) <= LAUNCH_THRESHOLD && intakeTimer.seconds() >= .6){
                    state = AutoState.LAUNCH_NINE;
                    launchTimer.reset();
                }
                break;
            case LAUNCH_NINE:
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
                LeftSideFeedRoller.setPower(1);
                IntakeMotor.setPower(0);
                if(launchTimer.seconds() >= 0.8){
                    state = AutoState.RESET_NINE;
                    intakeTimer.reset();
                }
                break;
            case RESET_NINE:
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
                LeftSideFeedRoller.setPower(0);
                state = AutoState.PARK;
                break;
            case PARK:
                LauncherMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                LauncherMotor.setPower(0);
                Actions.runBlocking(drive.actionBuilder(blueFarLaunchPose).lineToX(38).build());
                state = AutoState.END;
                break;
            case END:
                requestOpModeStop();
                break;
        }

    }
}
