package org.firstinspires.ftc.teamcode._OpModes.Prototypes.old;

import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherHoodServo;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherMotor;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode._Proccedural.Components;
import org.firstinspires.ftc.teamcode._Proccedural.Input;
import org.firstinspires.ftc.teamcode._Util.IntakeUtil;

//testing opmode disabled
@TeleOp
public class LauncherHoodServoTesting extends OpMode {

    Input input = new Input();
    double power = 0.5;
    double TargetVel;
    IntakeUtil intakeUtil = new IntakeUtil();
    @Override
    public void init() {
        Components.initComponents(hardwareMap);
    }

    public void start() {
        LauncherHoodServo.setPosition(.2);
    }


    @Override
    public void loop() {
        input.pollGamepad(gamepad1);
        //LauncherMotor.setVelocity(TargetVel);
        if (input.left_trigger.held()) {
            intakeUtil.launchStart();
         } else if (input.left_trigger.up()){
            intakeUtil.launchEnd();
        }
        if (input.dpad_up.down()){
            LauncherMotor.setVelocity(LauncherMotor.getVelocity() + 50);
        }
        if (input.dpad_down.down()){
            LauncherMotor.setVelocity(LauncherMotor.getVelocity() - 50);
        }
        if(input.x_square.down()){
            LauncherHoodServo.setPosition(LauncherHoodServo.getPosition()+0.05);
        }
        if(input.y_triangle.down()){
            LauncherHoodServo.setPosition(LauncherHoodServo.getPosition()-0.05);
        }


        telemetry.addLine("X to increase");
        telemetry.addLine("Y to decrease");
        telemetry.addData("Launcher Hood Pos: ", LauncherHoodServo.getPosition());
        telemetry.addData("Launcher Velocity", LauncherMotor.getVelocity());
        telemetry.addLine("hold A to run feed rollers");
        telemetry.addLine("left bumper to decrease power");
        telemetry.addLine("right bumper to increase power");
        telemetry.addData("Power: ", power);
    }
}
