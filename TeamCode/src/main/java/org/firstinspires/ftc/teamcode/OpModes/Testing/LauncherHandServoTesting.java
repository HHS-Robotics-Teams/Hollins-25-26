package org.firstinspires.ftc.teamcode.OpModes.Testing;

import static org.firstinspires.ftc.teamcode.aProccedural.Components.LauncherHandServo;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.initComponents;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.aProccedural.Input;

@TeleOp
public class LauncherHandServoTesting extends OpMode {
    Input input = new Input();

    @Override
    public void init() {
        initComponents(hardwareMap);
        LauncherHandServo.setPosition(0);

    }

    @Override
    public void loop() {
        if (input.dpad_up.down()) {
            LauncherHandServo.setPosition(LauncherHandServo.getPosition() + .05);
        }
        if (input.dpad_down.down()) {
            LauncherHandServo.setPosition(LauncherHandServo.getPosition() - .05);
        }
        telemetry.addData("Launcher Hand Servo Position", LauncherHandServo.getPosition());
        telemetry.update();
    }
}
