package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Rotation2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.core.colorscheme.ColorManager;
import com.noahbres.meepmeep.core.colorscheme.ColorPalette;
import com.noahbres.meepmeep.core.colorscheme.ColorScheme;
import com.noahbres.meepmeep.core.colorscheme.scheme.ColorSchemeBlueDark;
import com.noahbres.meepmeep.core.colorscheme.scheme.ColorSchemeBlueLight;
import com.noahbres.meepmeep.core.colorscheme.scheme.ColorSchemeRedDark;
import com.noahbres.meepmeep.core.colorscheme.scheme.ColorSchemeRedLight;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.DriveTrainType;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

public class MeepMeepTesting {
    public static void main(String[] args) {
        MeepMeep meepMeep = new MeepMeep(700); // 800 for most devices, 700 for club laptop

        Pose2d blueFarStartPos = new Pose2d(72-(17/2), -(12/2), Math.toRadians(180));
        Pose2d blueNearStartPose = new Pose2d(-65,-36,Math.toRadians(45));
        Pose2d redNearStartPose = new Pose2d(-65,36,Math.toRadians(-45));
        Pose2d redFarStartPose = new Pose2d(72-(17/2), (12/2), Math.toRadians(180));

        Pose2d blueFarLaunchPose = new Pose2d(50, -10, Math.toRadians(-157));
        Pose2d blueNearLaunchPose = new Pose2d(-16, -16, Math.toRadians(-135));
        Pose2d redFarLaunchPose = new Pose2d(50, 10, Math.toRadians(157));
        Pose2d redNearLaunchPose = new Pose2d(-16, 16, Math.toRadians(135));
        Pose2d bluePPGPickupStartPose = new Pose2d(-11.75,-30,Math.toRadians(-90));
        Pose2d bluePGPPickupStartPose = new Pose2d(11.75,-30,Math.toRadians(-90));
        Pose2d blueGPPPickupStartPose = new Pose2d(11.75+24,-30,Math.toRadians(-90));
        Pose2d redPPGPickupStartPose = new Pose2d(-11.75,30,Math.toRadians(90));
        Pose2d redPGPPickupStartPose = new Pose2d(11.75,30,Math.toRadians(90));
        Pose2d redGPPPickupStartPose = new Pose2d(11.75+24,30,Math.toRadians(90));
        double intakeWaitTime = 0.2;
        double launchWaitTime = 4;

        final double intakeDriveY = 46;
        final double intakeFinalY = 30;


        RoadRunnerBotEntity blueFar = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 14 )
                .setDimensions(12,17)
                .setDriveTrainType(DriveTrainType.MECANUM)
                .setStartPose(blueFarStartPos)
                .setColorScheme(new ColorSchemeBlueLight())
                .build();

        RoadRunnerBotEntity blueNear = new DefaultBotBuilder(meepMeep)
                .setConstraints(60,60,Math.PI,Math.PI,14)
                .setDimensions(12,17)
                .setDriveTrainType(DriveTrainType.MECANUM)
                .setStartPose(blueNearStartPose)
                .setColorScheme(new ColorSchemeBlueDark())
                .build();

        RoadRunnerBotEntity redFar = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 14 )
                .setDimensions(12,17)
                .setDriveTrainType(DriveTrainType.MECANUM)
                .setStartPose(blueFarStartPos)
                .setColorScheme(new ColorSchemeRedLight())
                .build();

        RoadRunnerBotEntity redNear = new DefaultBotBuilder(meepMeep)
                .setConstraints(60,60,Math.PI,Math.PI,14)
                .setDimensions(12,17)
                .setDriveTrainType(DriveTrainType.MECANUM)
                .setStartPose(blueNearStartPose)
                .setColorScheme(new ColorSchemeRedDark())
                .build();

        blueFar.runAction(blueFar.getDrive().actionBuilder(blueFarStartPos)
                .lineToX(55)
                .splineToLinearHeading(blueFarLaunchPose, Math.toRadians(-175))
                .waitSeconds(launchWaitTime)
                .splineToSplineHeading(blueGPPPickupStartPose,blueFarLaunchPose.heading)
                .waitSeconds(intakeWaitTime)
                .lineToY(-intakeDriveY)
                .waitSeconds(.1)
                .lineToY(-30)
                .splineToLinearHeading(blueFarLaunchPose,blueGPPPickupStartPose.heading.minus(Rotation2d.exp(Math.PI)))
                .waitSeconds(launchWaitTime)
                .splineToLinearHeading(bluePGPPickupStartPose,blueFarLaunchPose.heading)
                .waitSeconds(intakeWaitTime)
                .lineToY(-intakeDriveY)
                .waitSeconds(.1)
                .lineToY(-30)
                .splineToLinearHeading(blueFarLaunchPose,bluePGPPickupStartPose.heading.minus(Rotation2d.exp(Math.PI)))
                .waitSeconds(launchWaitTime)
                .lineToX(38)
                .build());

        blueNear.runAction(blueNear.getDrive().actionBuilder(blueNearStartPose)
                .splineToLinearHeading(blueNearLaunchPose,blueNearStartPose.heading)
                .waitSeconds(launchWaitTime)
                .splineToLinearHeading(bluePPGPickupStartPose,blueNearLaunchPose.heading)
                .waitSeconds(intakeWaitTime)
                .lineToY(-intakeDriveY)
                .waitSeconds(.1)
                .lineToY(-30)
                .splineToLinearHeading(blueNearLaunchPose,bluePPGPickupStartPose.heading.minus(Rotation2d.exp(Math.PI)))
                .waitSeconds(launchWaitTime)
                .splineToLinearHeading(bluePGPPickupStartPose,blueNearLaunchPose.heading)
                .waitSeconds(intakeWaitTime)
                .lineToY(-intakeDriveY)
                .waitSeconds(.1)
                .lineToY(-30)
                .splineToLinearHeading(blueNearLaunchPose,bluePGPPickupStartPose.heading.minus(Rotation2d.exp(Math.PI)))
                .waitSeconds(launchWaitTime)
                .lineToX(-38)
                .build());
        redFar.runAction(redFar.getDrive().actionBuilder(redFarStartPose)
                .lineToX(55)
                .splineToLinearHeading(redFarLaunchPose, Math.toRadians(-175))
                .waitSeconds(launchWaitTime)
                .splineToSplineHeading(redGPPPickupStartPose,redFarLaunchPose.heading)
                .waitSeconds(intakeWaitTime)
                .lineToY(intakeDriveY)
                .waitSeconds(.1)
                .lineToY(30)
                .splineToLinearHeading(redFarLaunchPose,redGPPPickupStartPose.heading.minus(Rotation2d.exp(Math.PI)))
                .waitSeconds(launchWaitTime)
                .splineToLinearHeading(redPGPPickupStartPose,redFarLaunchPose.heading)
                .waitSeconds(intakeWaitTime)
                .lineToY(intakeDriveY)
                .waitSeconds(.1)
                .lineToY(30)
                .splineToLinearHeading(redFarLaunchPose,redPGPPickupStartPose.heading.minus(Rotation2d.exp(Math.PI)))
                .waitSeconds(launchWaitTime)
                .lineToX(38)
                .build());

        redNear.runAction(blueNear.getDrive().actionBuilder(redNearStartPose)
                .splineToLinearHeading(redNearLaunchPose,redNearStartPose.heading)
                .waitSeconds(launchWaitTime)
                .splineToLinearHeading(redPPGPickupStartPose,redNearLaunchPose.heading)
                .waitSeconds(intakeWaitTime)
                .lineToY(intakeDriveY)
                .waitSeconds(.1)
                .lineToY(30)
                .splineToLinearHeading(redNearLaunchPose,redPPGPickupStartPose.heading.minus(Rotation2d.exp(Math.PI)))
                .waitSeconds(launchWaitTime)
                .splineToLinearHeading(redPGPPickupStartPose,redNearLaunchPose.heading)
                .waitSeconds(intakeWaitTime)
                .lineToY(intakeDriveY)
                .waitSeconds(.1)
                .lineToY(30)
                .splineToLinearHeading(redNearLaunchPose,redPGPPickupStartPose.heading.minus(Rotation2d.exp(Math.PI)))
                .waitSeconds(launchWaitTime)
                .lineToX(-38)
                .build());

        meepMeep.setBackground(MeepMeep.Background.FIELD_DECODE_JUICE_BLACK)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(blueFar)
                .addEntity(blueNear)
                .addEntity(redNear)
                .addEntity(redFar)
                .start();
    }
}
