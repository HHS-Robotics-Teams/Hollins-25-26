package org.firstinspires.ftc.teamcode.OpModes.Prototypes;

import static org.firstinspires.ftc.teamcode._Proccedural.Components.ConveyorMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.IntakeMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LeftSideFeedRoller;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.INTAKE_POWER;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCH_TICK_VELOCITY_NEAR;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode._Proccedural.Components;
import org.firstinspires.ftc.teamcode._Proccedural.Input;

@TeleOp
public class New_LauncherTesting extends OpMode {
    Input input = new Input();
    double targetVel = LAUNCH_TICK_VELOCITY_NEAR;
    @Override
    public void init() {
        Components.initComponents(hardwareMap);


    }

    @Override
    public void loop() {
        input.pollGamepad(gamepad1);
        LauncherMotor.setVelocity(targetVel);

        telemetry.addData("Target Velocity:", targetVel);
        telemetry.addData("Actual Velocity:", LauncherMotor.getVelocity(AngleUnit.RADIANS));
        telemetry.addLine("X to increase vel,\nY to decrease vel,\nA to raise finger,\nB to toggle intake");

        if(input.left_trigger.held()){
            ConveyorMotor.setPower(INTAKE_POWER);
            IntakeMotor.setPower(INTAKE_POWER);
            LeftSideFeedRoller.setPower(-1);

        } else if (input.left_bumper.held()) {
            ConveyorMotor.setPower(INTAKE_POWER);
            IntakeMotor.setPower(INTAKE_POWER);
            LeftSideFeedRoller.setPower(1);

        } else {
            ConveyorMotor.setPower(0);
            IntakeMotor.setPower(0);
            LeftSideFeedRoller.setPower(0);
        }
    }
}
