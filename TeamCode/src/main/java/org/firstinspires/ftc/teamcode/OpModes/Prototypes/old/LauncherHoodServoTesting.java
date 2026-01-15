package org.firstinspires.ftc.teamcode.OpModes.Prototypes.old;

import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherHoodServo;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode._Proccedural.Components;
import org.firstinspires.ftc.teamcode._Proccedural.Input;

//testing opmode disabled
@Disabled
@TeleOp
@Deprecated
public class LauncherHoodServoTesting extends OpMode {

    Input input = new Input();
    double power = 0.5;
    @Override
    public void init() {
        Components.initComponents(hardwareMap);
    }

    public void start() {
        LauncherHoodServo.setPosition(0);
    }


    @Override
    public void loop() {
        input.pollGamepad(gamepad1);

        if(input.x.down()){
            LauncherHoodServo.setPosition(LauncherHoodServo.getPosition()+0.05);
        }
        if(input.y.down()){
            LauncherHoodServo.setPosition(LauncherHoodServo.getPosition()-0.05);
        }

        telemetry.addLine("X to increase");
        telemetry.addLine("Y to decrease");
        telemetry.addData("Launcher Hood Pos: ", LauncherHoodServo.getPosition());
        telemetry.addLine("hold A to run feed rollers");
        telemetry.addLine("left bumper to decrease power");
        telemetry.addLine("right bumper to increase power");
        telemetry.addData("Power: ", power);
    }
}
