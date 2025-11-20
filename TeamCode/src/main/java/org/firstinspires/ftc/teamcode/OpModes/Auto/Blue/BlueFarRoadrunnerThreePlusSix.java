package org.firstinspires.ftc.teamcode.OpModes.Auto.Blue;

import static org.firstinspires.ftc.teamcode.OpModes.Auto.PathFactory.blueFarLaunchPose;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.IntakeMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherFingerServo;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LeftSideFeedRoller;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.INTAKE_POWER;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_FAR_TARGET;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_FAR_TARGET_FIRST;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_FAR_TARGET_SECOND;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_FAR_TARGET_THIRD;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_FINGER_DOWN_POS;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_FINGER_UP_POS;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCH_THRESHOLD;

import static java.lang.Math.abs;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.HollinsMadeUtil.AprilTagMethod;
import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.OpModes.Auto.PathFactory;
import org.firstinspires.ftc.teamcode._Proccedural.Components;

@Autonomous
public class BlueFarRoadrunnerThreePlusSix extends OpMode {

    MecanumDrive drive;

    enum AutoState{
        START,
        SPIN_UP,
        LAUNCH_ONE,
        RESET_ONE,
        FEED_ONE,
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
    PathFactory factory;

    ElapsedTime launchTimer = new ElapsedTime(ElapsedTime.SECOND_IN_NANO);
    ElapsedTime intakeTimer = new ElapsedTime(ElapsedTime.SECOND_IN_NANO);
    ElapsedTime specialTimer = new ElapsedTime(ElapsedTime.SECOND_IN_NANO);

    @Override
    public void init() {
        drive = new MecanumDrive(hardwareMap, new Pose2d(72-(17/2), -(12/2), Math.toRadians(180)));
        factory = new PathFactory(drive);
        state = AutoState.START;
        Components.initComponents(hardwareMap);
        turnToLaunch = drive.actionBuilder(new Pose2d(72-(17/2), -(12/2), Math.toRadians(180)))
                .lineToX(55)
                .splineToLinearHeading(blueFarLaunchPose, Math.toRadians(-175))
                .build();
        driveToIntakeOne = factory.blueGPPPickupPath(blueFarLaunchPose);
        driveToLaunchOne = factory.blueFarLaunchPath(new Pose2d(11.75+24+3,-42,Math.toRadians(-90)));
        driveToIntakeTwo = factory.bluePGPPickupPath(blueFarLaunchPose);

    }

    @Override
    public void start(){
    }

    @Override
    public void loop() {
        telemetry.addData("State:", state);
        telemetry.addData("Lancher Velocity", LauncherMotor.getVelocity(AngleUnit.RADIANS));
        telemetry.addData( "Launch Timer", launchTimer.seconds());
        telemetry.addData("Intake timer", intakeTimer.seconds());
        telemetry.update();
        switch (state){
            case START:
                //LauncherMotor.setPower(1);
                LauncherMotor.setVelocity(2.3, AngleUnit.RADIANS);
                Actions.runBlocking(turnToLaunch);
                state = AutoState.SPIN_UP;
                break;
            case SPIN_UP:
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
                if(abs(LauncherMotor.getVelocity(AngleUnit.RADIANS) - 2.3) <= LAUNCH_THRESHOLD){
                    LeftSideFeedRoller.setPower(1);
                    state = AutoState.LAUNCH_ONE;
                    launchTimer.reset();
                }
                break;
            case LAUNCH_ONE:
                if(launchTimer.seconds() >= 1.0){
                    LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
                    LeftSideFeedRoller.setPower(0);
                    state = AutoState.RESET_ONE;
                    intakeTimer.reset();
                }
                break;
            case RESET_ONE:
                IntakeMotor.setPower(INTAKE_POWER);
                if (intakeTimer.seconds() >= 1.0){
                    LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
                    LauncherMotor.setVelocity(2.5, AngleUnit.RADIANS);
                    if(abs(LauncherMotor.getVelocity(AngleUnit.RADIANS) - 2.5) <= LAUNCH_THRESHOLD){
                        LeftSideFeedRoller.setPower(1);
                        state = AutoState.LAUNCH_TWO;

                        launchTimer.reset();
                    }
                }
                break;
            case LAUNCH_TWO:
                if(launchTimer.seconds() >= 1.0){
                    IntakeMotor.setPower(0);
                    LeftSideFeedRoller.setPower(0);
                    LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
                    state = AutoState.RESET_TWO;
                    specialTimer.reset();
                    intakeTimer.reset();
                }
                break;
            case RESET_TWO: // Todo Find better soulution to not feeding 3rd ball
                if (specialTimer.seconds() >= 1.0) {
                    IntakeMotor.setPower(INTAKE_POWER);
                    LauncherMotor.setVelocity(2.5, AngleUnit.RADIANS);
                    if (intakeTimer.seconds() >= 1.75) {
                        LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
                        if (abs(LauncherMotor.getVelocity(AngleUnit.RADIANS) - 2.5) <= LAUNCH_THRESHOLD) {
                            LeftSideFeedRoller.setPower(1);
                            state = AutoState.LAUNCH_THREE;
                            launchTimer.reset();
                        }
                    }
                }
                break;
            case LAUNCH_THREE:
                if(launchTimer.seconds() >= 1.0){
                    LeftSideFeedRoller.setPower(0);
                    LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
                    IntakeMotor.setPower(0);
                    intakeTimer.reset();
                    state = AutoState.RESET_THREE;
                }
                break;
            case RESET_THREE:
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
                intakeTimer.reset();
                state = AutoState.SPIN_UP_TWO;
                break;
            case SPIN_UP_TWO:
                IntakeMotor.setPower(0);
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
                LauncherMotor.setVelocity(2.3, AngleUnit.RADIANS);
                if (abs(LauncherMotor.getVelocity(AngleUnit.RADIANS) - 2.3) <= LAUNCH_THRESHOLD) {
                        LeftSideFeedRoller.setPower(1);
                        state = AutoState.LAUNCH_FOUR;
                        launchTimer.reset();
                }
                break;
            case LAUNCH_FOUR:
                if(launchTimer.seconds() >= 1.0){
                    LeftSideFeedRoller.setPower(0);
                    LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
                    state = AutoState.RESET_FOUR;
                    intakeTimer.reset();
                }
                break;
            case RESET_FOUR:
                IntakeMotor.setPower(INTAKE_POWER);
                if (intakeTimer.seconds() >= 1.0) {
                    LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
                    LauncherMotor.setVelocity(2.5, AngleUnit.RADIANS);
                    if (abs(LauncherMotor.getVelocity(AngleUnit.RADIANS) - (2.5)) <= LAUNCH_THRESHOLD) {
                        LeftSideFeedRoller.setPower(1);
                        state = AutoState.LAUNCH_FIVE;
                        launchTimer.reset();
                    }
                }
                break;
            case LAUNCH_FIVE:
                IntakeMotor.setPower(0);
                if(launchTimer.seconds() >= 1.0){
                    LeftSideFeedRoller.setPower(0);
                    LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
                    state = AutoState.RESET_FIVE;
                    intakeTimer.reset();
                }
                break;
            case RESET_FIVE:
                IntakeMotor.setPower(INTAKE_POWER);
                if (intakeTimer.seconds() >= 1.0) {
                    LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
                    LauncherMotor.setVelocity(2.5, AngleUnit.RADIANS);
                    if (abs(LauncherMotor.getVelocity(AngleUnit.RADIANS) - 2.5) <= LAUNCH_THRESHOLD ) {
                        LeftSideFeedRoller.setPower(1);
                        state = AutoState.LAUNCH_SIX;
                        launchTimer.reset();
                    }
                }
                break;
            case LAUNCH_SIX:
                IntakeMotor.setPower(0);
                if(launchTimer.seconds() >= 1.0){
                    LeftSideFeedRoller.setPower(0);
                    LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
                    state = AutoState.RESET_SIX;
                    intakeTimer.reset();
                }
                break;
            case RESET_SIX:
                state = AutoState.END;
                launchTimer.reset();
                intakeTimer.reset();
                break;
            case DRIVE_TO_INTAKE_TWO:
                Actions.runBlocking(driveToIntakeTwo);
                break;
            case INTAKE_TWO:
                break;
            case DRIVE_TO_LAUNCH_THREE:
                break;
            case SPIN_UP_THREE:
                break;
            case LAUNCH_SEVEN:
                break;
            case RESET_SEVEN:
                break;
            case LAUNCH_EIGHT:
                break;
            case RESET_EIGHT:
                break;
            case LAUNCH_NINE:
                break;
            case RESET_NINE:
                break;
            case PARK:
                break;
            case END:
                break;
        }

    }
}
