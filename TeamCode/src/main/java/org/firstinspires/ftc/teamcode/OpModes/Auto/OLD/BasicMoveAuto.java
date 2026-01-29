package org.firstinspires.ftc.teamcode.OpModes.Auto.OLD;

import static org.firstinspires.ftc.teamcode._Proccedural.Components.leftFront;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.leftBack;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.rightFront;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.rightBack;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode._Proccedural.Components;
@Disabled
@Deprecated
@Autonomous
public class BasicMoveAuto extends OpMode {
    double timeAtStart;

    @Override
    public void init() {
        Components.initComponents(hardwareMap);
    }

    @Override
    public void start() {
        leftFront.setPower(-.5);
        rightFront.setPower(-.5);
        leftBack.setPower(-.5);
        rightBack.setPower(-.5);
        timeAtStart = getRuntime();
    }

    @Override
    public void loop() {
        if(getRuntime() > (timeAtStart + 1)){
            leftFront.setPower(0);
            rightFront.setPower(0);
            leftBack.setPower(0);
            rightBack.setPower(0);
        }
    }
}
