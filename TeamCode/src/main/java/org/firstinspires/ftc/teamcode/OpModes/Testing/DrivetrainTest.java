package org.firstinspires.ftc.teamcode.OpModes.Testing;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.aProccedural.Input;
@Disabled
@TeleOp (name = "Drivetrain Test", group = "Test")
public class DrivetrainTest extends OpMode {
    private DcMotorEx leftFront;
    private DcMotorEx rightFront;
    private DcMotorEx leftRear;
    private DcMotorEx rightRear;
    Input input = new Input();
    @Override
    public void init() {
        leftFront = hardwareMap.get(DcMotorEx.class, "leftFront");
        rightFront = hardwareMap.get(DcMotorEx.class, "rightFront");
        leftRear = hardwareMap.get(DcMotorEx.class, "leftRear");
        rightRear = hardwareMap.get(DcMotorEx.class, "rightRear");
    }

    @Override
    public void loop() {
        input.pollGamepad(gamepad1);
        if (input.y.down()){
            leftFront.setPower(.5);
        }
        if (input.b.down()){
            leftRear.setPower(.5);
        }
        if (input.x.down()){
            rightRear.setPower(.5);
        }
        if (input.a.down()){
            rightFront.setPower(.5);
        }
        if (input.left_bumper.down()){
            leftFront.setPower(0);
            leftRear.setPower(0);
            rightRear.setPower(0);
            rightFront.setPower(0);
        }
        telemetry.addLine("Press Y to set left front power");
        telemetry.addLine("Press B to set left rear power");
        telemetry.addLine("Press X to set right rear power");
        telemetry.addLine("Press A to set right front power");
        telemetry.addData("Leftfront current", leftFront.getCurrent(CurrentUnit.AMPS));
        telemetry.addData("Rightfront current", rightFront.getCurrent(CurrentUnit.AMPS));
        telemetry.addData("Leftrear current", leftRear.getCurrent(CurrentUnit.AMPS));
        telemetry.addData("Rightrear current", rightRear.getCurrent(CurrentUnit.AMPS));
        telemetry.update();

    }
}
