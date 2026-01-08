package org.firstinspires.ftc.teamcode.OpModes.Testing;

import static org.firstinspires.ftc.teamcode.aProccedural.Components.holderServo;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.initComponents;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.intakeMotor;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.intakeSecondRollerMotor;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.first_intake_Powers;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.intake_reversed;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.intake_stop;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.main_intake_Powers;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.second_intake_Powers;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.aProccedural.Input;
@TeleOp(name = "Intake Tuning", group = "Tuning")
public class IntakeTuning extends OpMode {
    Input input = new Input();

    @Override
    public void init() {
        initComponents(hardwareMap);
        intakeMotor.setPower(0);
        intakeSecondRollerMotor.setPower(0);
    }

    @Override
    public void loop() {
        input.pollGamepad(gamepad1);

        if (input.x.down()){
            intake_reversed = true;
        }
        if (input.left_trigger.down()) {
            main_intake_Powers();
        }
        if (input.left_bumper.down()) {
            first_intake_Powers();
        }
        if (input.right_bumper.down()) {
            second_intake_Powers();
        } else {
            intake_stop();
        }
        telemetry.addLine("press X to reverse intake");
        telemetry.addLine("Press Left Trigger both intake rollers ");
        telemetry.addLine("Press Left Bumper to main roller");
        telemetry.addLine("Press Right Bumper to second roller");

    }
}
