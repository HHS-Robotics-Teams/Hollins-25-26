package org.firstinspires.ftc.teamcode._OpModes.Prototypes.old;

import static org.firstinspires.ftc.teamcode._Proccedural.Components.leftBack;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.leftFront;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.rightBack;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.rightFront;
import static java.lang.Math.abs;
import static java.lang.Math.max;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode._Proccedural.Components;
import org.firstinspires.ftc.teamcode._Proccedural.Input;

//testing opmode disabled
@Disabled
@Deprecated
@TeleOp
public class DrivetrainTest extends OpMode {

    Input input = new Input();
    double power = 0.00;

    @Override
    public void init() {
        Components.initComponents(hardwareMap);
    }

    @Override
    public void loop() {
        input.pollGamepad(gamepad1);
        /* ---------- Drivetrain ---------- */

        //Drivetrain movement values
        double forward =  gamepad1.left_stick_y;
        double strafes = -gamepad1.left_stick_x * 1.2;
        double rotates = -gamepad1.right_stick_x;

        if(abs(forward) <= 0.2){forward = 0;}
        if(abs(strafes) <= 0.2){strafes = 0;}
        if(abs(rotates) <= 0.2){rotates = 0;}

        if(input.a.held()){
            forward = power;
            strafes = 0;
            rotates = 0;
        }

        if(input.x.down()){
            power += 0.01;
        }
        if(input.y.down()){
            power -= 0.01;
        }
        if(input.dpad_up.down()){
            leftFront.setPower(.5);
        } else if(input.dpad_down.down()){
            leftBack.setPower(.5);
        }else if(input.dpad_left.down()){
            rightFront.setPower(.5);
        }else if(input.dpad_right.down()){
            rightBack.setPower(.5);
        } else {

            //Power fixer
            double denominator = max((abs(forward) + abs(strafes) + abs(rotates)), 1);

            //Setting Powers
            leftFront.setPower((forward + strafes + rotates) / denominator);
            rightFront.setPower((forward - strafes - rotates) / denominator);
            leftBack.setPower((forward - strafes + rotates) / denominator);
            rightBack.setPower((forward + strafes - rotates) / denominator);
        }
        telemetry.addData("Power ", power);
    }
}
