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
                .splineToSplineHeading(new Pose2d(11.75+24,-30,Math.toRadians(-90)),Math.toRadians(-45))
                .waitSeconds(.2)
                .lineToY(-48)
                .waitSeconds(.1)
                .lineToY(-30)
                .splineToLinearHeading(blueFarLaunchPose, Math.toRadians(startPose.heading.minus(Rotation2d.exp(0))))
                .build());

        meepMeep.setBackground(MeepMeep.Background.FIELD_DECODE_JUICE_BLACK)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}
