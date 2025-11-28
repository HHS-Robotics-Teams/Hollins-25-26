package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.Pose2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.DriveTrainType;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

// tuned pathing seems to work well
public class MeepMeepTestingRedFarThreePlusSix {
    public static void main(String[] args) {
        MeepMeep meepMeep = new MeepMeep(600);

        Pose2d startPos = new Pose2d(63.5, 6, Math.toRadians(180));
        Pose2d RedFarLaunchPose = new Pose2d(50, 10, Math.toRadians(153));
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
                .splineToLinearHeading(RedFarLaunchPose, Math.toRadians(175))
                .waitSeconds(4)
                .splineToSplineHeading(new Pose2d(36,30,Math.toRadians(90)),Math.toRadians(90))
                .lineToY(36)
                .waitSeconds(.3)
                .lineToY(40)
                .waitSeconds(.3)
                .lineToY(46)
                .splineToLinearHeading(RedFarLaunchPose, Math.toRadians(175))
                .waitSeconds(4)
                .splineToSplineHeading(new Pose2d(12,30,Math.toRadians(90)),Math.toRadians(90))
                .lineToY(36)
                .waitSeconds(.3)
                .lineToY(40)
                .waitSeconds(.3)
                .lineToY(46)
                .splineToLinearHeading(RedFarLaunchPose, Math.toRadians(175))
                .waitSeconds(4)
                .build());

        meepMeep.setBackground(MeepMeep.Background.FIELD_DECODE_JUICE_BLACK)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}
