package org.firstinspires.ftc.teamcode.OpModes.Prototypes;

import static org.firstinspires.ftc.teamcode.aProccedural.Components.LauncherFingerServo;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.aProccedural.Components;
import org.firstinspires.ftc.teamcode.aProccedural.Input;

//testing opmode disabled
@Disabled
@TeleOp
public class LauncherFingerServoTesting extends OpMode {

    Input input = new Input();
    @Override
    public void init() {
        Components.initComponents(hardwareMap);
    }

    public void start() {
        LauncherFingerServo.setPosition(0);
    }

    @Override
    public void loop() {
        input.pollGamepad(gamepad1);
        if(input.x.down()){
            LauncherFingerServo.setPosition(LauncherFingerServo.getPosition()+0.05);
        }
        if(input.y.down()){
            LauncherFingerServo.setPosition(LauncherFingerServo.getPosition()-0.05);
        }
        telemetry.addLine("X to increase");
        telemetry.addLine("Y to decrease");
        telemetry.addData("Launcher Pos: ", LauncherFingerServo.getPosition());
    }
}
