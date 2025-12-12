package org.firstinspires.ftc.teamcode.OpModes.Prototypes;

import static org.firstinspires.ftc.teamcode._Proccedural.Components.intakeFeeder;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode._Proccedural.Components;
import org.firstinspires.ftc.teamcode._Proccedural.Input;

@TeleOp
public class IntakeFeederPosFinder extends OpMode {

    Input input = new Input();
    double pos = 0;
    double delta = 0.05;
    @Override
    public void init() {
        Components.initComponents(hardwareMap);
        intakeFeeder.setPosition(pos);
    }

    @Override
    public void loop() {
        input.pollGamepad(gamepad1);
        telemetry.addLine("A to increase pos by " + delta);
        telemetry.addLine("B to decrease pos by " + delta);

        telemetry.addData("pos:", pos);
        intakeFeeder.setPosition(pos);
        if(input.a.down()){
            pos += delta;
        }
        if(input.b.down()){
            pos -= delta;
        }
    }
}
