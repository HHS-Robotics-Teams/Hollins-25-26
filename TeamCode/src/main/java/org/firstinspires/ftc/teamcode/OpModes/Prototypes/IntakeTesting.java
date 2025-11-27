package org.firstinspires.ftc.teamcode.OpModes.Prototypes;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode._Proccedural.Input;

//testing opmode disabled

@Disabled
@TeleOp
public class IntakeTesting extends OpMode {

    //left
    private DcMotorEx intake_motor;
    //right
    //private DcMotor r;
    //power
    private double power = 0.25;
    Input input = new Input();
    @Override
    public void init() {
        intake_motor = hardwareMap.get(DcMotorEx.class, "IntakeMotor");
        intake_motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    @Override
    public void loop() {

        //run
        if(gamepad1.a){
            intake_motor.setPower(power);
        } else {
            intake_motor.setPower(0);
        }

        //change power
        if(input.x.down()){
            power += 0.05;
        }
        if(input.y.down()){
            power -= 0.05;
        }

        //Reverse
        if(input.b.down()){
            if(intake_motor.getDirection() == DcMotorSimple.Direction.REVERSE){
                intake_motor.setDirection(DcMotorSimple.Direction.FORWARD);
            } else {
                intake_motor.setDirection(DcMotorSimple.Direction.REVERSE);
            }
        }

        input.pollGamepad(gamepad1);

        telemetry.addData("Current power: ", power);
        telemetry.addData("Current speed: ", intake_motor.getVelocity(AngleUnit.RADIANS));
        telemetry.addLine();
        telemetry.addLine("a to toggle on/off\nx to increase power by delta\ny to decrease power by delta\nb to reverse");

    }
}
