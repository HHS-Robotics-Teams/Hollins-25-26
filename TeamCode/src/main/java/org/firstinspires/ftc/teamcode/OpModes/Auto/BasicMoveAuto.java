package org.firstinspires.ftc.teamcode.OpModes.Auto;

import static org.firstinspires.ftc.teamcode._Proccedural.Components.rightFront;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.rightRear;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode._Proccedural.Components;

@Autonomous
public class BasicMoveAuto extends OpMode {
    ElapsedTime t = new ElapsedTime(ElapsedTime.Resolution.SECONDS);
    @Override
    public void init() {
        Components.initComponents(hardwareMap);
    }

    @Override
    public void start() {
        t.reset();
        Components.leftFront.setPower(0.4);
        Components.leftRear.setPower(0.4);
        rightFront.setPower(0.4);
        rightRear.setPower(0.4);
    }

    @Override
    public void loop() {
        if(t.seconds() >= 0.75){
            Components.leftFront.setPower(0);
            Components.leftRear.setPower(0);
            rightFront.setPower(0);
            rightRear.setPower(0);
            requestOpModeStop();
        }
    }
}
