package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.DriveTrainType;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

public class BlueFarHumPlayerGateZone {
    public static void main(String[] args) {
        MeepMeep meepMeep = new MeepMeep(600);

        Pose2d startPos = new Pose2d(63.5, -6, Math.toRadians(180));
        Pose2d blueFarLaunchPose = new Pose2d(50, -10, Math.toRadians(-153));
        double intakeWaitTime = 0.2;
        double launchWaitTime = 4;

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 14 )
                .setDimensions(12,17)
                .setDriveTrainType(DriveTrainType.MECANUM)
                .setStartPose(startPos)
                .build();

        myBot.runAction(myBot.getDrive().actionBuilder(startPos)
                .splineToLinearHeading(blueFarLaunchPose, Math.toRadians(-175))
                .waitSeconds(4)
                .strafeToLinearHeading(new Vector2d(60,-56),Math.toRadians(-90))
                .waitSeconds(0.2)
                .lineToYConstantHeading(-58)
                .strafeToLinearHeading(new Vector2d(61,-58),Math.toRadians(-80))
                .lineToXConstantHeading(63)
                .waitSeconds(0.2)
                .strafeToLinearHeading(new Vector2d(50,-10),Math.toRadians(-155))
                .build());

        meepMeep.setBackground(MeepMeep.Background.FIELD_DECODE_JUICE_BLACK)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}
