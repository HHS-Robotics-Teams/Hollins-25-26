package org.firstinspires.ftc.teamcode._OpModes.Prototypes.old;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode._Proccedural.Input;

@TeleOp
@Deprecated
@Disabled
public class TempServoTesting extends OpMode {

    Servo temp;
    Input input = new Input();
    double x = 0.05;
    @Override
    public void init() {
        temp = hardwareMap.get(Servo.class, "temp");
    }

    @Override
    public void loop() {
        input.pollGamepad(gamepad1);

        if(input.x_square.down()){
            temp.setPosition(temp.getPosition() + x);
        }
        if(input.y_triangle.down()){
            temp.setPosition(temp.getPosition() - x);
        }
        if(input.a_cross.down()){
            x += 0.01;
        }
        if(input.b_circle.down()){
            x -= 0.01;
        }
        telemetry.addLine("X to increase pos by " + x);
        telemetry.addLine("Y to decrease pos by " + x);
        telemetry.addLine("A to increase delta by 0.01");
        telemetry.addLine("B to decrease delta by 0.01");

        telemetry.addData("Pos: ", temp.getPosition());
        telemetry.addData("Delta: ", x);

    }
}
