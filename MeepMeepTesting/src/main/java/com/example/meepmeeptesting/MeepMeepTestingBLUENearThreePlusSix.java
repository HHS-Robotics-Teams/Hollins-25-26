package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.Pose2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.DriveTrainType;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;
// Tuned looks good to run
public class MeepMeepTestingBLUENearThreePlusSix {
    public static void main(String[] args) {
        MeepMeep meepMeep = new MeepMeep(600);

        Pose2d startPos = new Pose2d(-55, -50, Math.toRadians(-135));
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
                .lineToYLinearHeading(-20,Math.toRadians(-131.5))
                .waitSeconds(1)
                .splineToSplineHeading(new Pose2d(-11,-30,Math.toRadians(-90)),Math.toRadians(-90))
                .lineToY(-36)
                .waitSeconds(.3)
                .lineToY(-40)
                .waitSeconds(.3)
                .lineToY(-46)
                .splineToLinearHeading(new Pose2d(-24,-20,Math.toRadians(-131.5)),Math.toRadians(-90))
                .waitSeconds(1)
                .splineToSplineHeading(new Pose2d(10.5,-30,Math.toRadians(-90)),Math.toRadians(-90))
                .lineToY(-36)
                .waitSeconds(.3)
                .lineToY(-40)
                .waitSeconds(.3)
                .lineToY(-46)
                .splineToLinearHeading(new Pose2d(-24,-20,Math.toRadians(-131.5)),Math.toRadians(-90))
                .build());

        meepMeep.setBackground(MeepMeep.Background.FIELD_DECODE_JUICE_BLACK)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}
