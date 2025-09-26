package org.firstinspires.ftc.teamcode.OpModes.TeleOp;

import static org.firstinspires.ftc.teamcode.aProccedural.Components.IntakeMotor;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.LauncherMotor;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.leftFront;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.leftRear;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.rightFront;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.rightRear;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.CONVEYOR_REVERSED;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.CONVEYOR_RUN;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.INTAKE_IDLE_POWER;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.INTAKE_POWER;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.INTAKE_RUN;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCHER_POWER;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCHER_RUN;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LaunchAmt;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.aProccedural.Components;
import org.firstinspires.ftc.teamcode.aProccedural.Constants;
import org.firstinspires.ftc.teamcode.aProccedural.Input;

@TeleOp
public class CompDrive extends OpMode {

    //Instantiated new input
    Input input = new Input();

    private enum LaunchState {
        SPIN_UP,
        LAUNCH_ONE,
        LAUNCH_TWO,
        LAUNCH_THREE,
        SPIN_DOWN
    }
    private LaunchState launchState = LaunchState.SPIN_UP;


    @Override
    public void init() {
        //Initialize Components
        Components.initComponents(hardwareMap);


        /* ---------- Telemetry ---------- */
        telemetry.addLine("--------- Initialization Complete ---------");
    }

    @Override
    public void loop() {


        /* ---------- Launcher ---------- */
        if(input.a.down()){
            LaunchAmt++;
        }
        runLauncher();

        /* ---------- Intake ---------- */
        if(input.b.down()){
            INTAKE_RUN =! INTAKE_RUN;
        }
        if(INTAKE_RUN){
            IntakeMotor.setPower(INTAKE_POWER);
        } else {
            IntakeMotor.setPower(INTAKE_IDLE_POWER);
        }

        /* ---------- Conveyor ---------- */
        if(input.x.down()){
            CONVEYOR_RUN =! CONVEYOR_RUN;
        }
        if(input.y.down()){
            CONVEYOR_REVERSED =! CONVEYOR_REVERSED;
        }
        if(CONVEYOR_RUN){
            //todo this (need to design)
            if(CONVEYOR_REVERSED){

            }
        }


        /* ---------- Drivetrain ---------- */

        //Drivetrain movement values
        double forward = gamepad1.left_stick_y;  //x
        double strafes = gamepad1.left_stick_x;  //y
        double rotates = gamepad1.right_stick_x; //rx

        //Setting Powers
        leftFront.setPower(forward + strafes + rotates);
        rightFront.setPower(forward - strafes - rotates);
        leftRear.setPower(forward - strafes + rotates);
        rightRear.setPower(forward + strafes - rotates);

        /* ---------- Telemetry ---------- */
        telemetry.addLine("--------- Comp Drive Running ---------");
        telemetry.addLine("Intake running status: " + INTAKE_RUN);
        telemetry.addLine("Conveyor running status: " + CONVEYOR_RUN);
        telemetry.addLine("Launcher running status: " + LAUNCHER_RUN);
    }

    public void runLauncher() {
        switch(launchState){
            case SPIN_UP:
                LauncherMotor.setPower(LAUNCHER_POWER);
        }
    }
}
