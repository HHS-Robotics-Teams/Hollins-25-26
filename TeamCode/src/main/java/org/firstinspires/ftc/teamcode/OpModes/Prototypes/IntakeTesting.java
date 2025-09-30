package org.firstinspires.ftc.teamcode.OpModes.Prototypes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.aProccedural.Input;

@TeleOp
public class IntakeTesting extends OpMode {

    //left
    private DcMotor l;
    //right
    //private DcMotor r;
    //power
    private double p = 0.25;
    Input input = new Input();
    @Override
    public void init() {
        l = hardwareMap.get(DcMotor.class, "IntakeMotor");
        l.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        //r = hardwareMap.get(DcMotor.class, "right");
        //r.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        //r.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    @Override
    public void loop() {

        //run
        if(gamepad1.a){
            l.setPower(p);
            //r.setPower(p);
        } else {
            l.setPower(0);
            //r.setPower(0);
        }

        //change power
        if(input.x.down()){
            p += 0.05;
        }
        if(input.y.down()){
            p -= 0.05;
        }

        //Reverse
        if(input.b.down()){
            if(l.getDirection() == DcMotorSimple.Direction.REVERSE){
                l.setDirection(DcMotorSimple.Direction.FORWARD);
                //r.setDirection(DcMotorSimple.Direction.REVERSE);
            } else {
                l.setDirection(DcMotorSimple.Direction.REVERSE);
                //  r.setDirection(DcMotorSimple.Direction.FORWARD);
            }
        }

        input.pollGamepad(gamepad1);

        telemetry.addData("Current speed: ", p);
    }
}