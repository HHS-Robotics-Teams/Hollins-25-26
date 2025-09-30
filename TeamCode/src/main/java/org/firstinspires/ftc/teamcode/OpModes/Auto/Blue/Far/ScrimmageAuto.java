package org.firstinspires.ftc.teamcode.OpModes.Auto.Blue.Far;

import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.MecanumDrive;

public class ScrimmageAuto extends OpMode {

    MecanumDrive mecanumDrive = new MecanumDrive(hardwareMap, new Pose2d(0,0,0));

    enum AutoState {
        WaitForStart,
        Shoot,
        Move,
        Cooldown
    }



    @Override
    public void init() {

    }

    @Override
    public void loop() {

    }
}
