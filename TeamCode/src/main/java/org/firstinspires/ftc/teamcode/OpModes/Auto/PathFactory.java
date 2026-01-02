package org.firstinspires.ftc.teamcode.OpModes.Auto;

import static org.firstinspires.ftc.teamcode._Proccedural.Components.IntakeMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherFingerServo;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherSafetyServo;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LeftSideFeedRoller;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.INTAKE_POWER;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_FINGER_DOWN_POS;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.SAFETY_HOLDING;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.InstantAction;
import com.acmerobotics.roadrunner.InstantFunction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Rotation2d;

import org.firstinspires.ftc.teamcode.MecanumDrive;

public class PathFactory {
    MecanumDrive drive;
    public static double intakeWaitTime = 0.2;
    InstantAction runIntake = new InstantAction(new InstantFunction() {
        @Override
        public void run() {
            IntakeMotor.setPower(INTAKE_POWER);
            LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
            LeftSideFeedRoller.setPower(0);
        }
    });
    InstantAction runLauncher = new InstantAction(new InstantFunction() {
        @Override
        public void run() {
            LauncherMotor.setPower(0.8); // Todo Tune
            LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
            LeftSideFeedRoller.setPower(0);
            LauncherSafetyServo.setPosition(SAFETY_HOLDING);
        }
    });
    public static Pose2d blueFarLaunchPose = new Pose2d(50, -10, Math.toRadians(-152.5));
    public static Pose2d blueNearLaunchPose = new Pose2d(-12,-10,Math.toRadians(-130));
    public static Pose2d redFarLaunchPose = new Pose2d(50, 10, Math.toRadians(155));
    public static Pose2d redNearLaunchPose = new Pose2d(-24,20,Math.toRadians(130));
    public static Pose2d bluePPGPickupStartPose = new Pose2d(-11.75,-30,Math.toRadians(-90));
    public static Pose2d bluePGPPickupStartPose = new Pose2d(11.75,-30,Math.toRadians(-90));
    public static Pose2d redPPGPickupStartPose = new Pose2d(-11.75,30,Math.toRadians(90));
    public static Pose2d redPGPPickupStartPose = new Pose2d(11.75,30,Math.toRadians(90));
    double intakeDriveY = 48;


    public PathFactory(MecanumDrive drive){
        this.drive = drive;
    }
    public Action blueNearLaunchPath(Pose2d startPose){
        return drive.actionBuilder(startPose)
                    .splineToLinearHeading(new Pose2d(-23,-19,Math.toRadians(-132)),Math.toRadians(-90))
                    .build();
    }
    public Action blueFarLaunchPath(Pose2d startPose){
        return drive.actionBuilder(startPose)
                .splineToLinearHeading(blueFarLaunchPose, Math.toRadians(startPose.heading.minus(Rotation2d.exp(0))))
                .afterDisp(1,runLauncher)
                .build();
    }
    public Action bluePPGPickupPath(Pose2d startPose){
        return drive.actionBuilder(startPose)
                .splineToLinearHeading(new Pose2d(-13,-29,Math.toRadians(-90)),Math.toRadians(-90))
                .afterDisp(.1, runIntake)
                .waitSeconds(intakeWaitTime)
//                .lineToY(-34)
//                .waitSeconds(.4)
//                .lineToY(-40)
//                .waitSeconds(.35)
                .lineToY(-56)
                .build();
    }
    public Action bluePGPPickupPath(Pose2d startPose){
        return drive.actionBuilder(startPose)
                .splineToLinearHeading(new Pose2d(12,-32,Math.toRadians(-90)),Math.toRadians(-90))
                .afterDisp(1, runIntake)
                .waitSeconds(intakeWaitTime)
//                .lineToY(-34)
//                .waitSeconds(.3)
                .lineToY(-41)   
//                .waitSeconds(.35)
                .lineToY(-56)
                .build();
    }
    public Action blueGPPPickupPath(Pose2d startPose){
        return drive.actionBuilder(startPose)
                .splineToLinearHeading(new Pose2d(36,-30,Math.toRadians(-90)),Math.toRadians(-90))
                .afterDisp(2, runIntake)
                .waitSeconds(intakeWaitTime)
                .lineToY(-38)
                .waitSeconds(.3)
                .lineToY(-39)
                .waitSeconds(.3)
                .lineToY(-45)
                .build();

    }public Action redPPGPickupPath(Pose2d startPose){
        return drive.actionBuilder(startPose)
                .splineToLinearHeading(new Pose2d(-11.75,30,Math.toRadians(90)),Math.toRadians(90))
                .afterDisp(2, runIntake)
                .waitSeconds(intakeWaitTime)
                .lineToY(36)
                .waitSeconds(.3)
                .lineToY(40)
                .waitSeconds(.3)
                .lineToY(52)
                .build();
    }
    public Action redPGPPickupPath(Pose2d startPose){
        return drive.actionBuilder(startPose)
                .splineToLinearHeading(new Pose2d(11.75,30,Math.toRadians(90)),Math.toRadians(90))
                .afterDisp(2, runIntake)
                .waitSeconds(intakeWaitTime)
                .lineToY(38)
                .waitSeconds(.3)
                .lineToY(39)
                .waitSeconds(.3)
                .lineToY(52)
                .build();
    }
    public Action redGPPPickupPath(Pose2d startPose){
        return drive.actionBuilder(startPose)
                .splineToLinearHeading(new Pose2d(36,30,Math.toRadians(90)),Math.toRadians(90))
                .afterDisp(2, runIntake)
                .waitSeconds(intakeWaitTime)
                .lineToY(35)
                .waitSeconds(.3)
                .lineToY(39)
                .waitSeconds(.3)
                .lineToY(42)
                .build();
    }
    public Action redNearLaunchPath(Pose2d startPose){
        return drive.actionBuilder(startPose)
                .splineToLinearHeading(new Pose2d(-24,20,Math.toRadians(131.5)),Math.toRadians(90))
                .build();
    }
    public Action redFarLaunchPath(Pose2d startPose){
        return drive.actionBuilder(startPose)
                .splineToLinearHeading(new Pose2d(50, 10, Math.toRadians(155)), Math.toRadians(90))
                .build();
    }
}
