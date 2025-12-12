package org.firstinspires.ftc.teamcode.OpModes;

import static org.firstinspires.ftc.teamcode.aProccedural.Components.holderServo;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.initComponents;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.intakeMotor;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.aProccedural.Input;
@TeleOp(name = "Intake Tuning", group = "Tuning")
public class IntakeTuning extends OpMode {
    Input input = new Input();

    @Override
    public void init() {
        initComponents(hardwareMap);
        telemetry.speak("Robot is spinning Danger Danger");
        telemetry.update();

        holderServo.setPosition(0);
        intakeMotor.setPower(0);
    }

    @Override
    public void loop() {
        input.pollGamepad(gamepad1);
        telemetry.addData("holder servo pos", holderServo.getPosition());
        telemetry.update();
        if (input.left_trigger.down()){
            intakeMotor.setPower(1);
        }if (input.left_bumper.down()){
            intakeMotor.setPower(0);
        }

        if (input.dpad_up.down()) {
            holderServo.setPosition(holderServo.getPosition() + .05);
        }
        if (input.dpad_down.down()) {
            holderServo.setPosition(holderServo.getPosition() - .05);
        }
    }
}
