package org.firstinspires.ftc.teamcode.OpModes.TeleOp;

import static org.firstinspires.ftc.teamcode.aProccedural.Components.IntakeMotor;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.leftFront;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.leftRear;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.rightFront;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.rightRear;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.INTAKE_POWER;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.INTAKE_REVERSED;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.INTAKE_RUN;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCHER_FAR;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCHER_IDLE;
import static org.firstinspires.ftc.teamcode.aProccedural.LauncherPID.getLauncherCurentVelocity;
import static org.firstinspires.ftc.teamcode.aProccedural.LauncherPID.getLauncherTargetVelocity;
import static org.firstinspires.ftc.teamcode.aProccedural.LauncherPID.initLauncherPID;
import static org.firstinspires.ftc.teamcode.aProccedural.LauncherPID.setLauncherTargetVelocity;
import static org.firstinspires.ftc.teamcode.aProccedural.LauncherPID.updateLauncherPID;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.aProccedural.Components;
import org.firstinspires.ftc.teamcode.aProccedural.Input;

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
        initLauncherPID(getRuntime(), LAUNCHER_FAR);
    }

    @Override
    public void loop() {
        input.pollGamepad(gamepad1);

        /* ---------- Launcher ---------- */
        if(input.a.down()){
            if(getLauncherTargetVelocity()==LAUNCHER_IDLE){
                setLauncherTargetVelocity(LAUNCHER_FAR);
            } else {
                setLauncherTargetVelocity(LAUNCHER_IDLE);
            }
        }
        updateLauncherPID(getRuntime());


        /* ---------- Intake ---------- */
        if(input.b.down()){
            INTAKE_REVERSED =! INTAKE_REVERSED;
        }
        if(input.x.down()){
            INTAKE_RUN =! INTAKE_RUN;
        }
        if(INTAKE_RUN){
            if(!INTAKE_REVERSED){
                IntakeMotor.setPower(INTAKE_POWER);
            } else {
                IntakeMotor.setPower(-INTAKE_POWER);
            }
        }

        /* ---------- Drivetrain ---------- */

        //Drivetrain movement values
        double forward = gamepad1.left_stick_y;
        double strafes = gamepad1.left_stick_x;
        double rotates = gamepad1.right_stick_x;

        //Setting Powers
        leftFront .setPower(forward + strafes + rotates);
        rightFront.setPower(forward - strafes - rotates);
        leftRear  .setPower(forward - strafes + rotates);
        rightRear .setPower(forward + strafes - rotates);

        /* ---------- Telemetry ---------- */
        telemetry.addLine("--------- Comp Drive Running ---------");
        telemetry.addLine("Intake running: "    + INTAKE_RUN);
        telemetry.addLine("Intake reversed: "   + INTAKE_REVERSED);
        telemetry.addLine("Launcher Velocity: " + getLauncherCurentVelocity());
        telemetry.addLine("Launcher TargetVel: "   + getLauncherTargetVelocity());
    }

}
