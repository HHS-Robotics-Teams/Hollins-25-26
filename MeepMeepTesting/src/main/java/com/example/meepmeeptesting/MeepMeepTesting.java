package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.Pose2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.DriveTrainType;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

public class MeepMeepTesting {
    public static void main(String[] args) {
        MeepMeep meepMeep = new MeepMeep(700); // 800 for most devices, 700 for club laptop

        Pose2d startPos = new Pose2d(72-(17/2), -(12/2), Math.toRadians(180));
        double intakeWaitTime = 0.2;
        double launchWaitTime = 4;
        Pose2d blueFarLaunchPose = new Pose2d(50, -10, Math.toRadians(-157));


        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 14 )
                .setDimensions(12,17)
                .setDriveTrainType(DriveTrainType.MECANUM)
                .setStartPose(startPos)
                .build();

        myBot.runAction(myBot.getDrive().actionBuilder(blueFarLaunchPose)
                .splineToLinearHeading(new Pose2d(11.75+24,-30,Math.toRadians(-90)),Math.toRadians(-45))
                .waitSeconds(.2)
                .lineToY(-48)
                .waitSeconds(.1)
                .lineToY(-30)
                .splineToLinearHeading(blueFarLaunchPose, Math.toRadians(Math.toRadians(-90)))
                .build());

        meepMeep.setBackground(MeepMeep.Background.FIELD_DECODE_JUICE_BLACK)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}
