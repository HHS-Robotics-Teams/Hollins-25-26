package org.firstinspires.ftc.teamcode.OpModes.TeleOp;

import static org.firstinspires.ftc.teamcode.aProccedural.Components.IntakeMotor;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.LauncherHolderServo;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.LauncherMotor;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.leftFront;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.leftBack;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.rightFront;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.rightBack;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.INTAKE_POWER;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.INTAKE_REVERSED;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.INTAKE_RUN;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCHER_FAR_BASE;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCHER_HOLDER_ENABLE;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCHER_HOLDER_HOLDING_POSITION;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCHER_HOLDER_LAUNCH_POSITION;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCHER_IDLE;
import static org.firstinspires.ftc.teamcode.Math.LauncherPID.getLauncherCurrentVelocity;
import static org.firstinspires.ftc.teamcode.Math.LauncherPID.getLauncherTargetVelocity;
import static org.firstinspires.ftc.teamcode.Math.LauncherPID.initLauncherPID;
import static org.firstinspires.ftc.teamcode.Math.LauncherPID.setLauncherTargetVelocity;
import static org.firstinspires.ftc.teamcode.Math.LauncherPID.updateLauncherPID;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.aProccedural.Components;
import org.firstinspires.ftc.teamcode.aProccedural.Input;
@TeleOp
public class CompDrive extends OpMode {

    //Instantiated new input
    Input input = new Input();


    @Override
    public void init() {
        //Initialize Components
        Components.initComponents(hardwareMap);

        /* ---------- Telemetry ---------- */
        telemetry.addLine("--------- Init Complete ---------");
    }

    @Override
    public void start(){
        //initLauncherPID(getRuntime(), LAUNCHER_FAR_BASE);
        LauncherHolderServo.setPosition(LAUNCHER_HOLDER_HOLDING_POSITION);
    }

    @Override
    public void loop() {
        input.pollGamepad(gamepad1);

        /* ---------- Launcher ---------- */
        if(input.right_trigger.down()){
            if(getLauncherTargetVelocity()<=LAUNCHER_IDLE){
                LauncherMotor.setVelocity(2.75, AngleUnit.RADIANS);
                //setLauncherTargetVelocity(LAUNCHER_FAR_BASE);
            } else {
                LAUNCHER_HOLDER_ENABLE = true;
                LauncherHolderServo.setPosition(LAUNCHER_HOLDER_HOLDING_POSITION);
                LauncherMotor.setVelocity(LAUNCHER_IDLE);
                //setLauncherTargetVelocity(LAUNCHER_IDLE);
            }
        }
        //updateLauncherPID(getRuntime());
        if(LAUNCHER_HOLDER_ENABLE && (input.right_bumper.down())) {//(/*vel error*/Math.abs((getLauncherCurrentVelocity() - getLauncherTargetVelocity())) <= /*margin*/Math.PI / 10) is a future goal
            LAUNCHER_HOLDER_ENABLE = false;
            LauncherHolderServo.setPosition(LAUNCHER_HOLDER_LAUNCH_POSITION);
        }

        /* ---------- Intake ---------- */
        if(input.b.down()){
            INTAKE_REVERSED =! INTAKE_REVERSED;
        }
        if(input.left_trigger.down()){
            INTAKE_RUN =! INTAKE_RUN;
        }
        if(INTAKE_RUN){
            if(!INTAKE_REVERSED){
                IntakeMotor.setPower(INTAKE_POWER);
            } else {
                IntakeMotor.setPower(-INTAKE_POWER);
            }
        } else {
            IntakeMotor.setPower(0);
        }

        /* ---------- Drivetrain ---------- */

        //Drivetrain movement values
        double forward = gamepad1.left_stick_y;
        double strafes = gamepad1.left_stick_x;
        double rotates = gamepad1.right_stick_x;

        //Setting Powers
        leftFront .setPower(forward + strafes + rotates);
        rightFront.setPower(forward - strafes - rotates);
        leftBack  .setPower(forward - strafes + rotates);
        rightBack .setPower(forward + strafes - rotates);

        /* ---------- Telemetry ---------- */
        telemetry.addLine("--------- Comp Drive Running ---------");
        telemetry.addLine("Intake running? "     + INTAKE_RUN);
        telemetry.addLine("Intake reversed? "    + INTAKE_REVERSED);
        telemetry.addLine("Launcher Holder? "    + LAUNCHER_HOLDER_ENABLE);
        telemetry.addLine("Launcher Velocity: "  + getLauncherCurrentVelocity());
        telemetry.addLine("Launcher TargetVel: " + getLauncherTargetVelocity());
    }

}
