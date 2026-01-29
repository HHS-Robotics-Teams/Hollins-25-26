package org.firstinspires.ftc.teamcode.OpModes.Testing;

import static org.firstinspires.ftc.teamcode.aProccedural.Components.LauncherHandServo;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.LauncherMotor;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.initComponents;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.leftFeedRoller;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.rightFeedRoller;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.Launcher_close_Vel;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.first_intake_Powers;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.intake_reversed;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.intake_stop;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.main_intake_Powers;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.second_intake_Powers;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.aProccedural.Input;

@TeleOp
public class LauncherHandServoTesting extends OpMode {
    Input input = new Input();

    @Override
    public void init() {
        initComponents(hardwareMap);

    }

    @Override
    public void loop() {
        input.pollGamepad(gamepad1);
        LauncherMotor.setVelocity(Launcher_close_Vel);
        if (input.dpad_up.held()) {
            leftFeedRoller.setPower(1);
            rightFeedRoller.setPower(1);
        }
        else  {
            leftFeedRoller.setPower(0);
            rightFeedRoller.setPower(0);
        }
        if (input.x.down()){
            intake_reversed = true;
        }
        if (input.left_trigger.held()) {
            main_intake_Powers();
        }
        if (input.left_bumper.held()) {
            first_intake_Powers();
        }
        if (input.right_bumper.held()) {
            second_intake_Powers();
        } else {
            intake_stop();
        }

        telemetry.update();
    }
}
