package org.firstinspires.ftc.teamcode._OpModes.Auto.Red;

import static org.firstinspires.ftc.teamcode._Proccedural.Components.ConveyorMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.IntakeMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherSafetyServo;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LeftSideFeedRoller;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.INTAKE_POWER;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCH_TICK_VELOCITY_NEAR;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.Launch_Time;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.SAFETY_HOLDING;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.SAFTEY_FIRING;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.InstantAction;
import com.acmerobotics.roadrunner.InstantFunction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode._OpModes.Auto.zOLD.PathFactory;
import org.firstinspires.ftc.teamcode._Util.LightUtil;
import org.firstinspires.ftc.teamcode._Proccedural.Components;

@Autonomous
public class GATERedNear3x9 extends OpMode {
    MecanumDrive drive;

    InstantAction runIntake = new InstantAction(new InstantFunction() {
        @Override
        public void run() {
            LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_NEAR);
            IntakeMotor.setPower(1);
            ConveyorMotor.setPower(1);
            LeftSideFeedRoller.setPower(0);
            LauncherSafetyServo.setPosition(SAFETY_HOLDING);
        }
    });
    InstantAction offIntake = new InstantAction(new InstantFunction() {
        @Override
        public void run() {
            LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_NEAR + 50);
            IntakeMotor.setPower(0);
            ConveyorMotor.setPower(0);
            LeftSideFeedRoller.setPower(0);
            LauncherSafetyServo.setPosition(SAFTEY_FIRING);
        }
    });
    InstantAction setVelocity = new InstantAction(new InstantFunction() {
        @Override
        public void run() {
            LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_NEAR + 15);

        }
    });

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
        PARK,
        END
    }
    AutoState state = AutoState.START;
    Action turnToLaunch;
    Action driveToIntakeOne;
    Action driveToIntakeTwo;
    Action driveToIntakeThree;
    Action park;
    LightUtil util;

    ElapsedTime launchTimer = new ElapsedTime(ElapsedTime.SECOND_IN_NANO);
    ElapsedTime faultTimer = new ElapsedTime(ElapsedTime.SECOND_IN_NANO);

    @Override
    public void init() {

        drive = new MecanumDrive(hardwareMap, new Pose2d(-57,49,Math.toRadians(135)));
        state = AutoState.START;
        Components.initComponents(hardwareMap);
        turnToLaunch = drive.actionBuilder(new Pose2d(-57,49,Math.toRadians(135)))
                .strafeToLinearHeading(new Vector2d(-24, 24),Math.toRadians(135))
                .afterTime(0.05, setVelocity)
                .build();
        driveToIntakeOne = drive.actionBuilder(new Pose2d(-24,24,Math.toRadians(135)))
                .strafeToLinearHeading(new Vector2d(-12,28),Math.toRadians(90))
                .afterTime(0.1, runIntake)
                .afterDisp(55, offIntake)
                .strafeToConstantHeading(new Vector2d(-12,56))
                .waitSeconds(0.2)
                .strafeToLinearHeading(new Vector2d(-24,24),Math.toRadians(135))
                .build();
        driveToIntakeTwo = drive.actionBuilder(new Pose2d(-24,24,Math.toRadians(135)))
                .strafeToLinearHeading(new Vector2d(12,28),Math.toRadians(90))
                .afterTime(0.1, runIntake)
                .afterDisp(75, offIntake)
                .strafeToConstantHeading(new Vector2d(12, 63))
                .waitSeconds(.3)
                .lineToYConstantHeading(50)
                .strafeToConstantHeading(new Vector2d(-4,58))
                .strafeToLinearHeading(new Vector2d(-24,24),Math.toRadians(135))
                .build();
        driveToIntakeThree = drive.actionBuilder(new Pose2d(-24, 24, Math.toRadians(135)))
                .strafeToLinearHeading(new Vector2d(36, 28), Math.toRadians(90))
                .afterTime(0.1, runIntake)
                .afterDisp(90, offIntake)
                .strafeToConstantHeading(new Vector2d(36, 63))
                .waitSeconds(.3)
                .strafeToLinearHeading(new Vector2d(-24, 24), Math.toRadians(135))
                .build();
        park = drive.actionBuilder(new Pose2d(-24,24,Math.toRadians(135)))
                .strafeToLinearHeading(new Vector2d(-2, 48), Math.toRadians(90))
                .build();
        telemetry.addLine("Ready to Launch");
        util = new LightUtil(2, hardwareMap);
        util.makeGreen();
        util.updateLights();
    }

    @Override
    public void start(){
        LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_NEAR);
        util.makeOff();
        util.updateLights();
        faultTimer.reset();
        LauncherMotor.setPower(1);
        Actions.runBlocking(turnToLaunch);
        LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_NEAR);
    }

    @Override
    public void stop () {
        for(int i = 0; i < Thread.activeCount(); i++){
            Thread.currentThread().interrupt();
        }
        requestOpModeStop();
    }

    @Override
    public void loop() {
        //   telemetry.addData("State:", state);
        //   telemetry.addData("Launcher Velocity", LauncherMotor.getVelocity());
        //   telemetry.addData("Launch Timer", launchTimer.seconds());
        //   telemetry.addData("Intake timer", intakeTimer.seconds());
        //   telemetry.addData("Launcher Current", LauncherMotor.getCurrent(CurrentUnit.AMPS));
        //   telemetry.update();

        switch (state){
            case START:
                LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_NEAR + 40);
                state = AutoState.LAUNCH_ONE;
                break;
            case LAUNCH_ONE:
                if (LauncherMotor.getVelocity() >= LAUNCH_TICK_VELOCITY_NEAR + 40){
                    LauncherSafetyServo.setPosition(SAFTEY_FIRING);
                    IntakeMotor.setPower(INTAKE_POWER);
                    ConveyorMotor.setPower(INTAKE_POWER);
                    LeftSideFeedRoller.setPower(1);
                    launchTimer.reset();
                    state = AutoState.RESET_ONE;
                }
                break;
            case RESET_ONE:
                if (launchTimer.seconds() >= Launch_Time) {
                    LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_NEAR);
                    IntakeMotor.setPower(0);
                    ConveyorMotor.setPower(0);
                    LeftSideFeedRoller.setPower(0);
                    state = AutoState.DRIVE_TO_INTAKE_ONE;
                }
                break;
            case DRIVE_TO_INTAKE_ONE:
                Actions.runBlocking(driveToIntakeOne);
                state = AutoState.SPIN_UP_TWO;
                break;
            case SPIN_UP_TWO:
                LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_NEAR + 110);
                state = AutoState.LAUNCH_TWO;
                break;
            case LAUNCH_TWO:
                if(LauncherMotor.getVelocity() >= (LAUNCH_TICK_VELOCITY_NEAR + 110)){
                    IntakeMotor.setPower(INTAKE_POWER);
                    ConveyorMotor.setPower(INTAKE_POWER);
                    LeftSideFeedRoller.setPower(1);
                    state = AutoState.RESET_TWO;
                    launchTimer.reset();
                }
                break;
            case RESET_TWO:
                if (launchTimer.seconds() >= Launch_Time) {
                    LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_NEAR);
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
                state = AutoState.DRIVE_TO_LAUNCH_THREE;
                break;
            case DRIVE_TO_LAUNCH_THREE:
                LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_NEAR + 140);
                state = AutoState.LAUNCH_THREE;
                break;
            case LAUNCH_THREE:
                if(LauncherMotor.getVelocity() >= (LAUNCH_TICK_VELOCITY_NEAR + 140 )) {
                    IntakeMotor.setPower(INTAKE_POWER);
                    ConveyorMotor.setPower(INTAKE_POWER);
                    LeftSideFeedRoller.setPower(1);
                    state = AutoState.RESET_THREE;
                    launchTimer.reset();
                }
                break;
            case RESET_THREE:
                if (launchTimer.seconds() >= Launch_Time) {
                    LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_NEAR);
                    IntakeMotor.setPower(0);
                    ConveyorMotor.setPower(0);
                    LeftSideFeedRoller.setPower(0);
                    state = AutoState.DRIVE_TO_INTAKE_THREE;
                    launchTimer.reset();
                }
                break;
            case DRIVE_TO_INTAKE_THREE:
                if(faultTimer.seconds() > 25) {
                    state = AutoState.PARK;
                    if(faultTimer.seconds() > 27){
                        state = AutoState.END;
                    }
                    break;
                }
                IntakeMotor.setPower(INTAKE_POWER);
                Actions.runBlocking(driveToIntakeThree);
                state = AutoState.DRIVE_TO_LAUNCH_FOUR;
                if(faultTimer.seconds() > 29.5 - Launch_Time){
                    state = AutoState.PARK;
                }
                break;
            case DRIVE_TO_LAUNCH_FOUR:
                LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_NEAR + 130);
                state = AutoState.LAUNCH_FOUR;
                break;
            case LAUNCH_FOUR:
                if(LauncherMotor.getVelocity() >= (LAUNCH_TICK_VELOCITY_NEAR + 130)) {
                    IntakeMotor.setPower(INTAKE_POWER);
                    ConveyorMotor.setPower(INTAKE_POWER);
                    LeftSideFeedRoller.setPower(1);
                    state = AutoState.PARK;
                    launchTimer.reset();
                }
                break;
            case PARK:
                LauncherMotor.setVelocity(0);
                LeftSideFeedRoller.setPower(0);
                IntakeMotor.setPower(0);
                ConveyorMotor.setPower(0);
                Actions.runBlocking(park);
                state = AutoState.END;
                break;
            case END:
                requestOpModeStop();
                break;
        }

    }
}

