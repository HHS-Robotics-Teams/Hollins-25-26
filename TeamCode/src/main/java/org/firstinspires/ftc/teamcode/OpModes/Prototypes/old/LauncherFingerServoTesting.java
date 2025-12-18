package org.firstinspires.ftc.teamcode.OpModes.Prototypes.old;

import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherFingerServo;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherSafetyServo;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LeftSideFeedRoller;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode._Proccedural.Components;
import org.firstinspires.ftc.teamcode._Proccedural.Input;

//testing opmode disabled
@TeleOp
@Disabled
@Deprecated
public class LauncherFingerServoTesting extends OpMode {

    Input input = new Input();
    double power = 0.5;
    @Override
    public void init() {
        Components.initComponents(hardwareMap);
    }

    public void start() {
        LauncherFingerServo.setPosition(0);
        LauncherSafetyServo.setPosition(0);
    }


    @Override
    public void loop() {
        input.pollGamepad(gamepad1);
        if (input.dpad_left.down()){
            LauncherSafetyServo.setPosition(LauncherSafetyServo.getPosition()+.05);
        }
        if (input.dpad_right.down()) {
            LauncherSafetyServo.setPosition(LauncherSafetyServo.getPosition()-.05);
        }

        if(input.x.down()){
            LauncherFingerServo.setPosition(LauncherFingerServo.getPosition()+0.05);
        }
        if(input.y.down()){
            LauncherFingerServo.setPosition(LauncherFingerServo.getPosition()-0.05);
        }
        if(input.a.held()){
            LeftSideFeedRoller.setPower(power);
        } else {
            LeftSideFeedRoller.setPower(0);
        }
        if(input.left_bumper.down()){
            power -= 0.05;
        }
        if(input.right_bumper.down()){
            power += 0.05;
        }
        telemetry.addLine("X to increase");
        telemetry.addLine("Y to decrease");
        telemetry.addData("Launcher Pos: ", LauncherFingerServo.getPosition());
        telemetry.addData("Shooter saftey servo pos", LauncherSafetyServo.getPosition());
        telemetry.addLine("hold A to run feed rollers");
        telemetry.addLine("left bumper to decrease power");
        telemetry.addLine("right bumper to increase power");
        telemetry.addData("Power: ", power);
    }
}
