package org.firstinspires.ftc.teamcode.OpModes.Prototypes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.aProccedural.Input;

@TeleOp
public class LauncherTesting extends OpMode {

    //left
    private DcMotorEx l;
    //right
    private DcMotor r;
    //power
    private double p = 0.25;
    private double delta = 0.05;
    Input input = new Input();
    @Override
    public void init() {
        l = hardwareMap.get(DcMotorEx.class, "LauncherMotor");
        l.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        r = hardwareMap.get(DcMotor.class, "IntakeMotor");
        r.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    @Override
    public void loop() {

        //run
        if(gamepad1.a){
            l.setPower(p);
            r.setPower(1);
        } else {
            l.setPower(0);
            r.setPower(0);
        }

        //change power
        if(input.x.down()){
            p += delta;
        }
        if(input.y.down()){
            p -= delta;
        }

        //Reverse
        if(input.b.down()){
            if(l.getDirection() == DcMotorSimple.Direction.REVERSE){
                l.setDirection(DcMotorSimple.Direction.FORWARD);
                r.setDirection(DcMotorSimple.Direction.FORWARD);
            } else {
                l.setDirection(DcMotorSimple.Direction.REVERSE);
                r.setDirection(DcMotorSimple.Direction.REVERSE);
            }
        }

        if(input.dpad_up.down()){
            delta += 0.01;
        }
        if(input.dpad_down.down()){
            delta -= 0.01;
        }

        input.pollGamepad(gamepad1);

        telemetry.addData("Current power: ", p);
        telemetry.addData("Current speed: ", l.getVelocity(AngleUnit.RADIANS));
    }
}