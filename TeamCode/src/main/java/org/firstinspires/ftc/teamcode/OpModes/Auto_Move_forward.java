package org.firstinspires.ftc.teamcode.OpModes;

import static org.firstinspires.ftc.teamcode.aProccedural.Components.LauncherMotor;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.leftFront;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.leftRear;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.rightFront;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.rightRear;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.aProccedural.Components;

@Autonomous
public class Auto_Move_forward extends OpMode {
    double timeAtStart;

    @Override
    public void init() {
        Components.initComponents(hardwareMap);
    }

    @Override
    public void start() {
        leftFront.setPower(.2);
        rightFront.setPower(.2);
        leftRear.setPower(.2);
        rightRear.setPower(.2);
        LauncherMotor.setPower(.3);
        timeAtStart = getRuntime();
    }

    @Override
    public void loop() {
        if (getRuntime() > (timeAtStart + 3)) {
            leftFront.setPower(0);
            rightFront.setPower(0);
            leftRear.setPower(0);
            rightRear.setPower(0);
            LauncherMotor.setPower(0);
        }
       if(LauncherMotor.getVelocity()>=20){
           LauncherMotor.setVelocity(15);
       }
    }
}
