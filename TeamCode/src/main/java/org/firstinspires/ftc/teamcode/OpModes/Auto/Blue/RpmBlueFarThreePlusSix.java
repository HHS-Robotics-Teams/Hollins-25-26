package org.firstinspires.ftc.teamcode.OpModes.Auto.Blue;

import static org.firstinspires.ftc.teamcode.OpModes.Auto.PathFactory.blueFarLaunchPose;
import static org.firstinspires.ftc.teamcode.OpModes.Auto.PathFactory.intakeWaitTime;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.IntakeMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherFingerServo;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherSafetyServo;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LeftSideFeedRoller;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.INTAKE_POWER;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_FINGER_DOWN_POS;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_FINGER_UP_POS;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCH_TICK_VELOCITY_FAR;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.SAFETY_HOLDING;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.SAFTEY_FIRING;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.InstantAction;
import com.acmerobotics.roadrunner.InstantFunction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.Util.AprilTagMethod;
import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.OpModes.Auto.PathFactory;
import org.firstinspires.ftc.teamcode._Proccedural.Components;

@Autonomous
public class RpmBlueFarThreePlusSix extends OpMode {
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
                .build();
        driveToIntakeOne = drive.actionBuilder(blueFarLaunchPose)
                .splineToSplineHeading(new Pose2d(39,-30,Math.toRadians(-90)),Math.toRadians(-90))
                .afterDisp(1,IntakePickup)
                .waitSeconds(intakeWaitTime)
                .lineToY(-41)
                .waitSeconds(.3)
                .lineToY(-48)
                .build();
        driveToLaunchOne = factory.blueFarLaunchPath(new Pose2d(39,-48,Math.toRadians(-90)));
        driveToIntakeTwo = drive.actionBuilder(blueFarLaunchPose)
                .splineToSplineHeading(new Pose2d(16,-30,Math.toRadians(-90)),Math.toRadians(-90))
                .afterDisp(1,IntakePickup)
                .waitSeconds(intakeWaitTime)
//                .lineToY(-36)
//                .waitSeconds(.3)
//                .lineToY(-40)
                .waitSeconds(.3)
                .lineToY(-48)
 //               .lineToY(-46)
                .build();
        driveToLaunchThree = factory.blueFarLaunchPath(new Pose2d(16,-48,Math.toRadians(-90)));
    }

    @Override
    public void start(){
        LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_FAR);
        LauncherSafetyServo.setPosition(SAFETY_HOLDING);
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
                IntakeMotor.setPower(0);
                LauncherSafetyServo.setPosition(SAFTEY_FIRING);
                LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_FAR);
                if((LauncherMotor.getVelocity() >= LAUNCH_TICK_VELOCITY_FAR) && (intakeTimer.seconds() >= .5)) {  // do not change this time
                    LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
                    LeftSideFeedRoller.setPower(1);
                    state = AutoState.LAUNCH_ONE;
                    launchTimer.reset();
                }
                break;
            case LAUNCH_ONE:
                if(launchTimer.seconds() >= 1.5){ // do not change this timeI
                    LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
                    IntakeMotor.setPower(INTAKE_POWER);
                    LeftSideFeedRoller.setPower(0);
                    intakeTimer.reset();
                    state = AutoState.RESET_ONE;

                }
                break;
            case RESET_ONE:
                LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_FAR);
                if ((LauncherMotor.getVelocity() >= LAUNCH_TICK_VELOCITY_FAR) && ( intakeTimer.seconds() >= 0.5)){
                    IntakeMotor.setPower(0);
                    LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
                    LeftSideFeedRoller.setPower(1);
                    state = AutoState.LAUNCH_TWO;
                    launchTimer.reset();

                }
                break;
            case LAUNCH_TWO:
                if(launchTimer.seconds() >= 1.0){ // do not change this time
                    LeftSideFeedRoller.setPower(0);
                    LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
                    IntakeMotor.setPower(INTAKE_POWER);
                    state = AutoState.RESET_TWO;
                    specialTimer.reset();
                    intakeTimer.reset();
                }
                break;
            case RESET_TWO: // Todo Find better solution to not feeding 3rd ball probable mechanical
                LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_FAR);
                if ((LauncherMotor.getVelocity() >= LAUNCH_TICK_VELOCITY_FAR) && (intakeTimer.seconds() >= 0.5)) {
                    LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
                    LeftSideFeedRoller.setPower(1);
                    state = AutoState.LAUNCH_THREE;
                    launchTimer.reset();
                }

                break;
            case LAUNCH_THREE:
                if(launchTimer.seconds() >= 1.0){ // do not change this time
                    IntakeMotor.setPower(0);
                    LeftSideFeedRoller.setPower(0);
                    LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
                    intakeTimer.reset();
                    state = AutoState.RESET_THREE;
                }
                break;
            case RESET_THREE:
                LauncherMotor.setVelocity(300);
                IntakeMotor.setPower(INTAKE_POWER);
                LauncherSafetyServo.setPosition(SAFETY_HOLDING);
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
                IntakeMotor.setPower(0);
                LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_FAR);
                Actions.runBlocking(driveToLaunchOne);
                intakeTimer.reset();
                state = AutoState.SPIN_UP_TWO;
                break;
            case SPIN_UP_TWO:
                IntakeMotor.setPower(0);
                LauncherSafetyServo.setPosition(SAFTEY_FIRING);
                LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_FAR + 25  );
                if((LauncherMotor.getVelocity() >= (LAUNCH_TICK_VELOCITY_FAR +25)) && (intakeTimer.seconds() >= 0.5)){
                    LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
                    LeftSideFeedRoller.setPower(1);
                    state = AutoState.LAUNCH_FOUR;
                    launchTimer.reset();
                }
                break;
            case LAUNCH_FOUR:
                if(launchTimer.seconds() >= 1.0){ // do not change this time
                    LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
                    IntakeMotor.setPower(INTAKE_POWER);
                    state = AutoState.RESET_FOUR;
                    intakeTimer.reset();
                }
                break;
            case RESET_FOUR:
                LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_FAR + 25);
                if ((LauncherMotor.getVelocity() >= (LAUNCH_TICK_VELOCITY_FAR + 25)) && (intakeTimer.seconds() >= 0.5)) {
                    IntakeMotor.setPower(0);
                    LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
                    LeftSideFeedRoller.setPower(1);
                    state = AutoState.LAUNCH_FIVE;
                    launchTimer.reset();
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
            case RESET_FIVE:
                LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_FAR + 25);
                if ((LauncherMotor.getVelocity() >= (LAUNCH_TICK_VELOCITY_FAR + 25)) && (intakeTimer.seconds() >= 0.5)) {
                    LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
                    IntakeMotor.setPower(0);
                    LeftSideFeedRoller.setPower(1);
                    state = AutoState.LAUNCH_SIX;
                    launchTimer.reset();

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
                IntakeMotor.setPower(INTAKE_POWER);
                LauncherSafetyServo.setPosition(SAFETY_HOLDING);
                Actions.runBlocking(driveToIntakeTwo);
                state = AutoState.INTAKE_TWO;
                break;
            case INTAKE_TWO:
                state = AutoState.DRIVE_TO_LAUNCH_THREE;
                break;
            case DRIVE_TO_LAUNCH_THREE:
                LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_FAR - 25);
                IntakeMotor.setPower(0);
                Actions.runBlocking(driveToLaunchThree);
                intakeTimer.reset();
                state = AutoState.SPIN_UP_THREE;
                break;
            case SPIN_UP_THREE:
                IntakeMotor.setPower(0);
                LauncherSafetyServo.setPosition(SAFTEY_FIRING);
                LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_FAR + 25);
                if((LauncherMotor.getVelocity() >=  (LAUNCH_TICK_VELOCITY_FAR + 25)) && (intakeTimer.seconds() >= 0.5)){
                    LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
                    LeftSideFeedRoller.setPower(1);
                    state = AutoState.LAUNCH_SEVEN;
                    launchTimer.reset();
                }
                break;
            case LAUNCH_SEVEN:
                if(launchTimer.seconds() >= 1.0){ // do not change this time
                    LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
                    IntakeMotor.setPower(INTAKE_POWER);
                    state = AutoState.RESET_SEVEN;
                    intakeTimer.reset();
                }
                break;
            case RESET_SEVEN:
                LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_FAR + 25) ;
                if ((LauncherMotor.getVelocity() >= (LAUNCH_TICK_VELOCITY_FAR + 25)) && (intakeTimer.seconds() >= 0.5)) {
                    IntakeMotor.setPower(0);
                    LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
                    LeftSideFeedRoller.setPower(1);
                    state = AutoState.LAUNCH_EIGHT;
                    launchTimer.reset();
                }

                break;
            case LAUNCH_EIGHT:
                if(launchTimer.seconds() >= 1.0){ // do not change this time
                    LeftSideFeedRoller.setPower(1);
                    LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
                    IntakeMotor.setPower(INTAKE_POWER);
                    intakeTimer.reset();
                    state = AutoState.RESET_EIGHT;

                }
                break;
            case RESET_EIGHT:
                LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_FAR + 25);
                if ((LauncherMotor.getVelocity() >= (LAUNCH_TICK_VELOCITY_FAR + 25)) && (intakeTimer.seconds() >= 0.5)) {
                    LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
                    IntakeMotor.setPower(0);
                    LeftSideFeedRoller.setPower(1);
                    state = AutoState.LAUNCH_NINE;
                    launchTimer.reset();
                }
                break;

            case LAUNCH_NINE:
                if(launchTimer.seconds() >= 1.0){ // do not change this time
                    LeftSideFeedRoller.setPower(0);
                    IntakeMotor.setPower(INTAKE_POWER);
                    LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
                    intakeTimer.reset();
                    state = AutoState.RESET_NINE;
                }
                break;
            case RESET_NINE:
                LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_FAR + 25);
                if ((LauncherMotor.getVelocity() >= (LAUNCH_TICK_VELOCITY_FAR + 25)) && (intakeTimer.seconds() >= 0.5)) {
                    LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
                    IntakeMotor.setPower(0);
                    LeftSideFeedRoller.setPower(1);
                    state = AutoState.PARK;
                    launchTimer.reset();
                    intakeTimer.reset();
                }
                break;
            case PARK:
                LauncherMotor.setPower(0);
                LeftSideFeedRoller.setPower(0);
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
                Actions.runBlocking(drive.actionBuilder(blueFarLaunchPose).lineToX(30).build());
                state = AutoState.END;
                break;
            case END:
                requestOpModeStop();
                break;
        }

    }
}

