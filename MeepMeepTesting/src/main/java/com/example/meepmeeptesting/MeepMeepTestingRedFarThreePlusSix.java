package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
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
                .strafeToLinearHeading(new Vector2d(50, 12), Math.toRadians(152.5))
                .waitSeconds(2)

                .strafeToLinearHeading(new Vector2d(61,55),Math.toRadians(90))
                .waitSeconds(0.15)
                .strafeToLinearHeading(new Vector2d(67,58),Math.toRadians(75))
                .waitSeconds(0.25)
                .turnTo(Math.toRadians(90))
                .strafeToConstantHeading(new Vector2d(70, 59))
                .waitSeconds(0.25)
                .strafeToLinearHeading(new Vector2d(50, 12), Math.toRadians(152.5))
                .waitSeconds(2)

                .strafeToLinearHeading(new Vector2d(65,20),Math.toRadians(45))
                .strafeToConstantHeading(new Vector2d(74,67))
                .waitSeconds(0.1)
                .strafeToLinearHeading(new Vector2d(50, 12), Math.toRadians(152.5))
                .waitSeconds(2)

                .strafeToLinearHeading(new Vector2d(65,20),Math.toRadians(45))
                .strafeToConstantHeading(new Vector2d(74,67))
                .waitSeconds(0.1)
                .strafeToLinearHeading(new Vector2d(50, 12), Math.toRadians(152.5))
                .waitSeconds(2)

                .strafeToLinearHeading(new Vector2d(74,57),Math.toRadians(45))
                .build());

        meepMeep.setBackground(MeepMeep.Background.FIELD_DECODE_JUICE_BLACK)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}
