package org.firstinspires.ftc.teamcode.OpModes.Testing;

import static org.firstinspires.ftc.teamcode.aProccedural.Components.LauncherHandServo;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.LauncherMotor;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.initComponents;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.intakeMotor;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.intakeSecondRollerMotor;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.firing;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.intake_stop;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.loading;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.main_intake_Powers;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.second_intake_Powers;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.aProccedural.Input;
@Disabled
@TeleOp
public class LauncherVelocityTuning extends OpMode {
    Input input = new Input();
    double Velocity = 300;

    @Override
    public void init(){
        initComponents(hardwareMap);
        LauncherHandServo.setPosition(loading);
        telemetry.speak("Robot is spinning Danger Danger");
        telemetry.update();

    }
    @Override
    public void loop() {
        input.pollGamepad(gamepad1);

        if (input.dpad_up.down()){
            Velocity += 100;
        } if (input.dpad_down.down()){
            Velocity -= 100;
        }

        if (input.right_trigger.held()){
            LauncherMotor.setVelocity(Velocity);
        } else {
            LauncherMotor.setVelocity(0);
        }
        if (input.x.down()){
            LauncherHandServo.setPosition(firing);
        } if (input.x.up()) {
            LauncherHandServo.setPosition(loading);
        }
        if (input.left_trigger.held()){
            main_intake_Powers();
        }
        else {
            intake_stop();
        }

        telemetry.addLine("Press Dpad Up to increase Velocity \nPress Dpad Down to decrease Velocity");
        telemetry.addLine("Press A to set Velocity");
        telemetry.addData("Launcher Velocity: ", LauncherMotor.getVelocity());
        telemetry.update();
    }
}

