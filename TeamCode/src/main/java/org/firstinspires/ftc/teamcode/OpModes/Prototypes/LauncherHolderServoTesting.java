package org.firstinspires.ftc.teamcode.OpModes.Prototypes;

import static org.firstinspires.ftc.teamcode.aProccedural.Components.LauncherHolderServo;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCHER_HOLDER_HOLDING_POSITION;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCHER_HOLDER_LAUNCH_POSITION;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.aProccedural.Components;
import org.firstinspires.ftc.teamcode.aProccedural.Input;

@TeleOp
public class LauncherHolderServoTesting extends OpMode {

    Input input = new Input();
    @Override
    public void init() {
        Components.initComponents(hardwareMap);
    }

    public void start() {
        LauncherHolderServo.setPosition(0);
    }

    @Override
    public void loop() {
        input.pollGamepad(gamepad1);
        if(input.x.down()){
            LauncherHolderServo.setPosition(LauncherHolderServo.getPosition()+0.05);
        }
        if(input.y.down()){
            LauncherHolderServo.setPosition(LauncherHolderServo.getPosition()-0.05);
        }
        telemetry.addLine("X to increase");
        telemetry.addLine("Y to decrease");
        telemetry.addData("Launcher Pos: ", LauncherHolderServo.getPosition());
    }
}
