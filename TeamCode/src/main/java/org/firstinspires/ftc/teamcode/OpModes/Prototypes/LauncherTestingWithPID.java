package org.firstinspires.ftc.teamcode.OpModes.Prototypes;

import static org.firstinspires.ftc.teamcode.aProccedural.Components.IntakeMotor;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.LauncherFingerServo;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.LauncherMotor;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.LeftSideFeedRoller;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.RightSideFeedRoller;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.INTAKE_POWER;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCHER_NEAR_TARGET;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCHER_FINGER_DOWN_POS;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCHER_FINGER_UP_POS;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.aProccedural.Components;
import org.firstinspires.ftc.teamcode.aProccedural.Input;

@Disabled
@TeleOp
public class LauncherTestingWithPID extends OpMode {
    Input input = new Input();
    double targetVel = LAUNCHER_NEAR_TARGET;
    double delta = 0.1;
    @Override
    public void init() {
        Components.initComponents(hardwareMap);
    }

    @Override
    public void loop() {
        LauncherMotor.setVelocity(targetVel);
        telemetry.addData("Target Velocity:", targetVel);
        telemetry.addData("Actual Velocity:", LauncherMotor.getVelocity(AngleUnit.RADIANS));
        telemetry.addLine("X to increase vel,\nY to decrease vel,\nA to raise finger,\nB to toggle intake");
        if(input.x.down()){
            targetVel += delta;
        }
        if(input.y.down()){
            targetVel -= delta;
        }
        if(input.a.held()){
            LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
        } else {
            LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
        }
        if(input.b.held()){
            IntakeMotor.setPower(INTAKE_POWER);
            LeftSideFeedRoller.setPower(1);
            RightSideFeedRoller.setPower(1);
        } else {
            IntakeMotor.setPower(0);
            LeftSideFeedRoller.setPower(0);
            RightSideFeedRoller.setPower(0);
        }
        telemetry.addLine("dpad up to increase delta,\ndpad down to decrease delta");
        telemetry.addData("Current delta: ", delta);
        if(input.dpad_up.down()){
            delta += 0.01;
        }
        if(input.dpad_down.down()){
            delta -= 0.01;
        }
    }
}
