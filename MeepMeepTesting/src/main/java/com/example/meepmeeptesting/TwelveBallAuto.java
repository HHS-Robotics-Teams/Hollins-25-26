package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.DriveTrainType;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

public class TwelveBallAuto {
    public static void main(String[] args){
        MeepMeep meepMeep = new MeepMeep(700);

        Pose2d startPos = new Pose2d(-52.5,46.5,Math.toRadians(130));

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(120, 120, 1.5 * Math.toRadians(180), 1.5 * Math.toRadians(180), 14 )
                .setDimensions(13,16.5)
                .setDriveTrainType(DriveTrainType.MECANUM)
                .setStartPose(startPos)
                .build();

        myBot.runAction(myBot.getDrive().actionBuilder(startPos)
                .strafeToLinearHeading(new Vector2d(-24, 24),Math.toRadians(135))

                .waitSeconds(3.5)

                .strafeToLinearHeading(new Vector2d(-12,28),Math.toRadians(90))
                .strafeToConstantHeading(new Vector2d(-12,56))
                .waitSeconds(.3)
                .strafeToLinearHeading(new Vector2d(-24,24),Math.toRadians(134))

                .waitSeconds(3)

                .strafeToLinearHeading(new Vector2d(12,28),Math.toRadians(90))
                .strafeToConstantHeading(new Vector2d(12, 65))
                .waitSeconds(.3)
                .lineToYConstantHeading(50)
                .strafeToConstantHeading(new Vector2d(2,58))
                .strafeToLinearHeading(new Vector2d(-24,24),Math.toRadians(135))

                .waitSeconds(3)

                .strafeToLinearHeading(new Vector2d(36, 28), Math.toRadians(90))
                .strafeToConstantHeading(new Vector2d(36, 65))
                .waitSeconds(.3)
                .strafeToLinearHeading(new Vector2d(-24, 24), Math.toRadians(135))

                .waitSeconds(3)

                .strafeToLinearHeading(new Vector2d(-2, 48), Math.toRadians(90))

                .build());

        meepMeep.setBackground(MeepMeep.Background.FIELD_DECODE_JUICE_BLACK)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}
