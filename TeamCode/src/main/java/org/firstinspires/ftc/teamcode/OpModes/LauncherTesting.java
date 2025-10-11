package org.firstinspires.ftc.teamcode.OpModes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.aProccedural.Input;

@TeleOp
public class LauncherTesting extends OpMode {
    Input input = new Input();
    DcMotor motor;
    double power = 0.25;
    boolean stop = true;
    @Override
    public void init() {
        motor = hardwareMap.get(DcMotor.class, "LauncherMotor");
    }

    @Override
    public void loop() {
        input.pollGamepad(gamepad1);
        if(input.x.down()){
            power += 0.05;
        }
        if(input.y.down()){
            power -= 0.05;
        }
        if(input.a.down()){
            stop =! stop;
        }
        if(input.b.down()){
            power *= -1;
        }
        if(stop){
            motor.setPower(0);
        } else {
            motor.setPower(power);
        }

        telemetry.addData("Current Power: ", power);
        telemetry.addLine("Press X to increase Power \nPress Y to decrease power\n Press A to stop/start \nPress B to reverse");

    }
}
