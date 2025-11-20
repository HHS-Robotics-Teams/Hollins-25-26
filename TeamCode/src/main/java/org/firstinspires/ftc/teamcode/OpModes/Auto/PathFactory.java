package org.firstinspires.ftc.teamcode.OpModes.Auto;

import static org.firstinspires.ftc.teamcode._Proccedural.Components.IntakeMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherFingerServo;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LeftSideFeedRoller;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.INTAKE_POWER;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_FINGER_DOWN_POS;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.InstantAction;
import com.acmerobotics.roadrunner.InstantFunction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Rotation2d;

import org.firstinspires.ftc.teamcode.MecanumDrive;

public class PathFactory {
    MecanumDrive drive;
    double intakeWaitTime = 0.2;
    InstantAction runIntake = new InstantAction(new InstantFunction() {
        @Override
        public void run() {
            IntakeMotor.setPower(INTAKE_POWER);
            LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
            LeftSideFeedRoller.setPower(0);
        }
    });
    public static Pose2d blueFarLaunchPose = new Pose2d(50, -10, Math.toRadians(-153));
    double intakeDriveY = 48;
    public static Pose2d blueNearLaunchPose = new Pose2d(-16, -16, Math.toRadians(-135));
    public static Pose2d redFarLaunchPose = new Pose2d(50, 10, Math.toRadians(157));
    public static Pose2d redNearLaunchPose = new Pose2d(-16, 16, Math.toRadians(135));
    public static Pose2d bluePPGPickupStartPose = new Pose2d(-11.75,-30,Math.toRadians(-90));
    public static Pose2d bluePGPPickupStartPose = new Pose2d(11.75,-30,Math.toRadians(-90));
    public static Pose2d blueGPPPickupStartPose = new Pose2d(11.75+24,-30,Math.toRadians(-90));
    public static Pose2d redPPGPickupStartPose = new Pose2d(-11.75,30,Math.toRadians(90));
    public static Pose2d redPGPPickupStartPose = new Pose2d(11.75,30,Math.toRadians(90));
    public static Pose2d redGPPPickupStartPose = new Pose2d(11.75+24,30,Math.toRadians(90));
    public static final double intakeFinalY = 30;

    public PathFactory(MecanumDrive drive){
        this.drive = drive;
    }
    public Action blueNearLaunchPath(Pose2d startPose){
        return drive.actionBuilder(startPose)
                    .splineToLinearHeading(blueNearLaunchPose,startPose.heading.minus(Rotation2d.exp(Math.PI)))
                    .build();
    }
    public Action blueFarLaunchPath(Pose2d startPose){
        return drive.actionBuilder(startPose)
                .splineToLinearHeading(blueFarLaunchPose, startPose.heading.minus(Rotation2d.exp(Math.PI)))
                .build();
    }
    public Action bluePPGPickupPath(Pose2d startPose){
        return drive.actionBuilder(startPose)
                .splineToSplineHeading(bluePPGPickupStartPose,startPose.heading)
                .afterDisp(5, runIntake)
                .waitSeconds(intakeWaitTime)
                .lineToY(-intakeDriveY)
                .build();
    }
    public Action bluePGPPickupPath(Pose2d startPose){
        return drive.actionBuilder(startPose)
                .splineToSplineHeading(bluePGPPickupStartPose,startPose.heading)
                .afterDisp(5, runIntake)
                .waitSeconds(intakeWaitTime)
                .lineToY(-intakeDriveY)
                .build();
    }
    public Action blueGPPPickupPath(Pose2d startPose){
        return drive.actionBuilder(startPose)

                .splineToSplineHeading(new Pose2d(11.75+24+3,-30,Math.toRadians(-90)),Math.toRadians(-45))
                .afterDisp(2, runIntake)
                .splineToSplineHeading(blueGPPPickupStartPose,startPose.heading)
                .afterDisp(5, runIntake)
                .waitSeconds(intakeWaitTime)
                .lineToY(-38)
                .waitSeconds(.3)
                .lineToY(-39)
                .waitSeconds(.3)
                .lineToY(-42)
                .build();

    }
    public Action redPPGPickupPath(Pose2d startPose){
        return drive.actionBuilder(startPose)
                .splineToSplineHeading(redPPGPickupStartPose,startPose.heading)
                .afterDisp(5, runIntake)
                .waitSeconds(intakeWaitTime)
                .lineToY(intakeDriveY)
                .build();
    }
    public Action redPGPPickupPath(Pose2d startPose){
        return drive.actionBuilder(startPose)
                .splineToSplineHeading(redPGPPickupStartPose,startPose.heading)
                .afterDisp(5, runIntake)
                .waitSeconds(intakeWaitTime)
                .lineToY(intakeDriveY)
                .build();
    }
    public Action redGPPPickupPath(Pose2d startPose){
        return drive.actionBuilder(startPose)
                .splineToSplineHeading(redGPPPickupStartPose,startPose.heading)
                .afterDisp(5, runIntake)
                .waitSeconds(intakeWaitTime)
                .lineToY(intakeDriveY)
                .build();
    }
    public Action redNearLaunchPath(Pose2d startPose){
        return drive.actionBuilder(startPose)
                .splineToLinearHeading(redNearLaunchPose,startPose.heading.minus(Rotation2d.exp(Math.PI)))
                .build();
    }
    public Action redFarLaunchPath(Pose2d startPose){
        return drive.actionBuilder(startPose)
                .splineToLinearHeading(redFarLaunchPose,startPose.heading.minus(Rotation2d.exp(Math.PI)))
                .build();
    }
}
