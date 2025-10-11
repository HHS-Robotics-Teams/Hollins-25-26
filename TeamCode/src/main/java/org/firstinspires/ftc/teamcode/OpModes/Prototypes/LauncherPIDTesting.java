package org.firstinspires.ftc.teamcode.OpModes.Prototypes;

import static org.firstinspires.ftc.teamcode.Math.LauncherPID.getLastTime;
import static org.firstinspires.ftc.teamcode.Math.LauncherPID.getLastVel;
import static org.firstinspires.ftc.teamcode.Math.LauncherPID.getTotalError;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.IntakeMotor;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.LauncherMotor;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.INTAKE_POWER;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCHER_FAR_BASE;
import static org.firstinspires.ftc.teamcode.Math.LauncherPID.LauncherkD;
import static org.firstinspires.ftc.teamcode.Math.LauncherPID.LauncherkI;
import static org.firstinspires.ftc.teamcode.Math.LauncherPID.LauncherkP;
import static org.firstinspires.ftc.teamcode.Math.LauncherPID.getLauncherTargetVelocity;
import static org.firstinspires.ftc.teamcode.Math.LauncherPID.initLauncherPID;
import static org.firstinspires.ftc.teamcode.Math.LauncherPID.setLauncherTargetVelocity;
import static org.firstinspires.ftc.teamcode.Math.LauncherPID.updateLauncherPID;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.aProccedural.Components;
import org.firstinspires.ftc.teamcode.aProccedural.Input;

@TeleOp
@Disabled
public class LauncherPIDTesting extends OpMode {
    //Instantiated new input
    Input input = new Input();
    double delta = 0.05;


    @Override
    public void init() {
        //Initialize Components
        Components.initComponents(hardwareMap);

        /* ---------- Telemetry ---------- */
        telemetry.addLine("--------- Initialization Complete ---------");
    }

    @Override
    public void start() {
        initLauncherPID(getRuntime(), LAUNCHER_FAR_BASE);
    }

    @Override
    public void loop() {
        input.pollGamepad(gamepad1);

        /* ---------- Launcher ---------- */
        if (input.a.down()) {
            if(getLauncherTargetVelocity() == 0) {
                setLauncherTargetVelocity(LAUNCHER_FAR_BASE);
                IntakeMotor.setPower(INTAKE_POWER);
            } else {
                setLauncherTargetVelocity(0);
                IntakeMotor.setPower(0);
            }
        }
        updateLauncherPID(getRuntime());

        if(input.b.down()){
            LauncherkP += delta;
        }
        if(input.x.down()){
            LauncherkI += delta;
        }
        if(input.y.down()){
            LauncherkD += delta;
        }
        if(input.right_trigger.down()){
            delta += 0.01;
        }
        if(input.left_trigger.down()){
            delta -= 0.01;
        }
        if(input.right_bumper.down()){
            delta /= 10;
        }
        if(input.left_bumper.down()){
            delta *= 10;
        }

        telemetry.addLine("'A' to toggle motor\n" +
                "'B' to change kP\n" +
                "'X' to change kI\n" +
                "'Y' to change kD\n" +
                "'RT' to increase delta + 0.01\n" +
                "'LT' to decrease delta - 0.01\n" +
                "'RB' to delta x10\n" +
                "'LB' to delta /10");
        telemetry.addLine();
        telemetry.addLine("Target: " + getLauncherTargetVelocity());
        telemetry.addLine("Current: " + LauncherMotor.getVelocity(AngleUnit.RADIANS));
        telemetry.addLine("Error: " + (getLauncherTargetVelocity() - LauncherMotor.getVelocity(AngleUnit.RADIANS)));
        telemetry.addLine();
        telemetry.addLine("kP: " + LauncherkP);
        telemetry.addLine("kI: " + LauncherkI);
        telemetry.addLine("kD: " + LauncherkD);
        telemetry.addLine();
        telemetry.addLine("Delta: " + delta);
        telemetry.addLine();
        telemetry.addLine("Total Error: " + getTotalError());
        telemetry.addLine("Last Time: " + getLastTime());
        telemetry.addLine("Time: " + getRuntime());
        telemetry.addLine("Delta Time: " + (getRuntime() - getLastTime()) );
        telemetry.addLine("Last Vel: " + getLastVel());
    }
}
