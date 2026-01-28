package org.firstinspires.ftc.teamcode.OpModes.Auto.OLD;

import static org.firstinspires.ftc.teamcode.OpModes.Auto.PathFactory.blueFarLaunchPose;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.IntakeMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherFingerServo;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherSafetyServo;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LeftSideFeedRoller;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.INTAKE_POWER;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_FINGER_DOWN_POS;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_FINGER_UP_POS;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.SAFETY_HOLDING;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.SAFTEY_FIRING;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.InstantAction;
import com.acmerobotics.roadrunner.InstantFunction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.Util.AprilTagMethod;
import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.OpModes.Auto.PathFactory;
import org.firstinspires.ftc.teamcode._Proccedural.Components;

@Deprecated
@Disabled
@Autonomous
public class RpmBlueFarThreePlusThree extends OpMode {

    MecanumDrive drive;
    InstantAction runIntake = new InstantAction(new InstantFunction() {
        @Override
        public void run() {
            LauncherMotor.setPower(0.8); // Todo Tune
            IntakeMotor.setPower(INTAKE_POWER);
            LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
            LeftSideFeedRoller.setPower(0);
            LauncherSafetyServo.setPosition(SAFETY_HOLDING);
    }
});
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
        drive = new MecanumDrive(hardwareMap, new Pose2d(63.5, -6, Math.toRadians(180)));
        factory = new PathFactory(drive);
        state = AutoState.START;
        Components.initComponents(hardwareMap);
        turnToLaunch = drive.actionBuilder(new Pose2d(63.5, -6, Math.toRadians(180)))
                .afterDisp(1, runIntake)
                .lineToX(55)
                .splineToLinearHeading(blueFarLaunchPose, Math.toRadians(-175))
                .build();
        driveToIntakeOne = factory.blueGPPPickupPath(blueFarLaunchPose);
        driveToLaunchOne = factory.blueFarLaunchPath(new Pose2d(11.75+24+3,-42,Math.toRadians(-90)));
        driveToIntakeTwo = factory.bluePGPPickupPath(blueFarLaunchPose);

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
        telemetry.addData("Launcher Motor", LauncherMotor.getCurrent(CurrentUnit.AMPS));
        telemetry.update();

        switch (state){
            case START:
                intakeTimer.reset();
                state = AutoState.SPIN_UP;
                break;
            case SPIN_UP:
                IntakeMotor.setPower(0);
                LauncherSafetyServo.setPosition(SAFTEY_FIRING); // Todo Tune velocity
                if(LauncherMotor.getVelocity() >= 1500) /*&& intakeTimer.seconds() >= .5)*/ {  // do not change this time
                    LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
                    LeftSideFeedRoller.setPower(1);
                    launchTimer.reset();
                    state = AutoState.LAUNCH_ONE;
                }
                break;
            case LAUNCH_ONE:
                if(launchTimer.seconds() >= 0.5){ // do not change this time
                    LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
                    IntakeMotor.setPower(INTAKE_POWER);
                    LeftSideFeedRoller.setPower(0);
                    intakeTimer.reset();
                    state = AutoState.RESET_ONE;
                }
                break;
            case RESET_ONE: //Todo Tune Velocity
                if ((LauncherMotor.getVelocity() >= 1500) && ( intakeTimer.seconds() >= 0.5)){
                    LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
                    IntakeMotor.setPower(0);
                    LeftSideFeedRoller.setPower(1);
                    launchTimer.reset();
                    state = AutoState.LAUNCH_TWO;
                }
                break;
            case LAUNCH_TWO:
                if(launchTimer.seconds() >= 1.0){ // do not change this time
                    LeftSideFeedRoller.setPower(0);
                    LauncherMotor.setPower(.35);
                    LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
                    IntakeMotor.setPower(INTAKE_POWER);
                    intakeTimer.reset();
                    state = AutoState.RESET_TWO;
                }
                break;
            case RESET_TWO: // Todo tune Velocity
                if ((LauncherMotor.getVelocity() >= 1500) && (intakeTimer.seconds() >= 1.0)) {
                    LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
                    LeftSideFeedRoller.setPower(1);
                    launchTimer.reset();
                    state = AutoState.LAUNCH_THREE;
                }
                break;
            case LAUNCH_THREE:
                if(launchTimer.seconds() >= 1.5){ // do not change this time
                    IntakeMotor.setPower(0);
                    LeftSideFeedRoller.setPower(0);
                    LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
                    state = AutoState.RESET_THREE;
                    intakeTimer.reset();

                }
                break;
            case RESET_THREE:
                LauncherMotor.setPower(.2);
                IntakeMotor.setPower(INTAKE_POWER);
                LauncherSafetyServo.setPosition(SAFETY_HOLDING);
                state = AutoState.DRIVE_TO_INTAKE_ONE;
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
                LauncherSafetyServo.setPosition(SAFTEY_FIRING);
                LauncherMotor.setPower(.8); // todo tune velocity
                if((LauncherMotor.getVelocity() >= 1500) && (intakeTimer.seconds() >= 0.5)){
                    LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
                    LeftSideFeedRoller.setPower(1);
                    launchTimer.reset();
                    state = AutoState.LAUNCH_FOUR;
                }
                break;
            case LAUNCH_FOUR:
                if(launchTimer.seconds() >= 1.0){
                    LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
                    LeftSideFeedRoller.setPower(0);
                    IntakeMotor.setPower(INTAKE_POWER);
                    intakeTimer.reset();
                    state = AutoState.RESET_FOUR;
                }
                break;
            case RESET_FOUR: // todo tune velocity
                if ((LauncherMotor.getVelocity() >= 1500) && (intakeTimer.seconds() >= 0.5)) {
                    IntakeMotor.setPower(0);
                    LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
                    LeftSideFeedRoller.setPower(1);
                    launchTimer.reset();
                    state = AutoState.LAUNCH_FIVE;

                }
                break;
            case LAUNCH_FIVE:
                if(launchTimer.seconds() >= 1.0){ // do not change this time
                    LeftSideFeedRoller.setPower(1);
                    LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
                    IntakeMotor.setPower(INTAKE_POWER);
                    intakeTimer.reset();
                    state = AutoState.RESET_FIVE;
                }
                break;
            case RESET_FIVE: // todo tune velocity
                if ((LauncherMotor.getVelocity() >= 1500) && (intakeTimer.seconds() >= 0.5)) {
                    LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
                    IntakeMotor.setPower(0);
                    LeftSideFeedRoller.setPower(1);
                    launchTimer.reset();
                    state = AutoState.LAUNCH_SIX;
                }
                break;
            case LAUNCH_SIX:
                if(launchTimer.seconds() >= 1.0){ // do not change this time
                    LeftSideFeedRoller.setPower(0);
                    IntakeMotor.setPower(INTAKE_POWER);
                    LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
                    intakeTimer.reset();
                    state = AutoState.RESET_SIX;
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
