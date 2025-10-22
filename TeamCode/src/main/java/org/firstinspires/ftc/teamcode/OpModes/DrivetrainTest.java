package org.firstinspires.ftc.teamcode.OpModes;

import static org.firstinspires.ftc.teamcode.aProccedural.Components.initComponents;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.leftFront;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.leftRear;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.rightFront;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.rightRear;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.aProccedural.Input;

@TeleOp (name = "Drivetrain Test", group = "Test")
public class DrivetrainTest extends OpMode {
    Input input = new Input();
    @Override
    public void init() {
        initComponents(hardwareMap);
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
    }
}
