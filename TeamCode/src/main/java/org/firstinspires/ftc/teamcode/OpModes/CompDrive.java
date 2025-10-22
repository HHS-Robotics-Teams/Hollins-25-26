package org.firstinspires.ftc.teamcode.OpModes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import static org.firstinspires.ftc.teamcode.aProccedural.Components.LauncherMotor;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.holderServo;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.initComponents;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.leftFront;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.leftRear;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.rightFront;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.rightRear;

import static org.firstinspires.ftc.teamcode.aProccedural.Constants.holding;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.shooting;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.shootingClose;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.shootingFar;


import org.firstinspires.ftc.teamcode.aProccedural.Input;

@TeleOp
public class CompDrive extends OpMode {
    Input input = new Input();
    @Override
    public void init() {
        initComponents(hardwareMap);
        telemetry.speak("Robot is spinning Danger Danger");
        telemetry.update();
    }

    @Override
    public void loop() {

        input.pollGamepad(gamepad1);


      /* Shooting modes */
       if(input.a.down()){
           LauncherMotor.setPower(shootingFar);
        } else if (input.b.down()){
            LauncherMotor.setPower(shootingClose);
        }
        else if (input.y.down()) {
            LauncherMotor.setPower(0);
       }
        /* Ball Holder */
        if(input.dpad_up.down()){
            holderServo.setPosition(shooting);

        } else if (input.dpad_down.down()){
            holderServo.setPosition(holding);
        }


        /* ---------- Drivetrain ---------- */

        //Drivetrain movement values
        double forward = -gamepad1.left_stick_y;  //x
        double strafes = gamepad1.left_stick_x;  //y
        double rotates = (gamepad1.right_stick_x * .8); //rx

        //Setting Powers
        leftFront.setPower(forward + strafes + rotates);
        rightFront.setPower(forward - strafes - rotates);
        leftRear.setPower(forward - strafes + rotates);
        rightRear.setPower(forward + strafes - rotates);

        telemetry.addData("Shooter Power:", LauncherMotor.getPower());
        telemetry.addData("Holder Servo Position:", holderServo.getPosition());
        telemetry.update();


    }
}
