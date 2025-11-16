package com.example.meepmeeptesting;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.DriveTrainType;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

import org.jetbrains.annotations.NotNull;

public class MeepMeepTesting {
    public static void main(String[] args) {
        MeepMeep meepMeep = new MeepMeep(800);

        Pose2d startPos = new Pose2d(72-(17/2), -(12/2), Math.toRadians(180));
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
                .lineToX(55)
                .splineToLinearHeading(new Pose2d(50, -10, Math.toRadians(-157)), Math.toRadians(-175))
                .waitSeconds(launchWaitTime)
                .splineToSplineHeading(new Pose2d(11.75+24,-30,Math.toRadians(-90)),Math.toRadians(-105))
                .waitSeconds(intakeWaitTime)
                .lineToY(-52)
                .splineToLinearHeading(new Pose2d(50, -10, Math.toRadians(-157)), Math.toRadians(-140))
                .waitSeconds(launchWaitTime)
                .splineToSplineHeading(new Pose2d(11.75,-30,Math.toRadians(-90)),Math.toRadians(-105))
                .waitSeconds(intakeWaitTime)
                .lineToY(-48)
                .splineToLinearHeading(new Pose2d(50, -10, Math.toRadians(-157)), Math.toRadians(-140))
                .waitSeconds(launchWaitTime)
                .lineToX(40)
                .build());

        meepMeep.setBackground(MeepMeep.Background.FIELD_DECODE_JUICE_BLACK)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}