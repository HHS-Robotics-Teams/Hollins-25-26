package org.firstinspires.ftc.teamcode.OpModes.Testing;

import static org.firstinspires.ftc.teamcode.aProccedural.Components.holderServo;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.initComponents;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.aProccedural.Input;
@Deprecated
@Disabled
@TeleOp
public class LauncherTesting extends OpMode {
    Input input = new Input();
    DcMotor motor;
    double power = 0.25;
    boolean stop = true;
    double Holder_Servo_position;
    @Override
    public void init() {
        initComponents(hardwareMap);
        motor = hardwareMap.get(DcMotor.class, "LauncherMotor");
    }

    @Override
    public void loop() {

        holderServo.setPosition(0);
        Holder_Servo_position = holderServo.getPosition();

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
        if (input.right_bumper.down()){
            Holder_Servo_position += 0.1;
        } else if (input.left_bumper.down()){
            Holder_Servo_position -= 0.1;

        }
        telemetry.addData("Current Power: ", power);
        telemetry.addLine("Press X to increase Power \nPress Y to decrease power\n Press A to stop/start \nPress B to reverse");
        telemetry.addData("Holder Servo position", Holder_Servo_position);

        telemetry.update();



    }
}
