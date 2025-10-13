package org.firstinspires.ftc.teamcode.OpModes.TeleOp;

import static org.firstinspires.ftc.teamcode.aProccedural.Components.IntakeMotor;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.LeftLauncherHolderServo;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.LauncherMotor;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.RightLauncherHolderServo;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.leftFront;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.leftBack;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.rightFront;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.rightBack;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.INTAKE_POWER;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.INTAKE_REVERSED;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.INTAKE_RUN;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCHER_FAR_BASE;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCHER_HOLDER_ENABLE;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCHER_IDLE;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LEFT_LAUNCHER_HOLDER_HOLDING_POSITION;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LEFT_LAUNCHER_HOLDER_LAUNCH_POSITION;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCHER_RUN;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.RIGHT_LAUNCHER_HOLDER_HOLDING_POSITION;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.RIGHT_LAUNCHER_HOLDER_LAUNCH_POSITION;

import static java.lang.Math.abs;
import static java.lang.Math.max;

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
        telemetry.addLine("WARNING: ROBOT MOVES ON START");
        telemetry.addLine("Launcher & Intake start spinning on start");
        telemetry.speak("Warning: Robot moves on start");
    }

    @Override
    public void start(){
        LauncherMotor.setPower(LAUNCHER_IDLE);
        IntakeMotor.setPower(LAUNCHER_IDLE);
        LeftLauncherHolderServo.setPosition(LEFT_LAUNCHER_HOLDER_HOLDING_POSITION);
        RightLauncherHolderServo.setPosition(RIGHT_LAUNCHER_HOLDER_HOLDING_POSITION);
    }

    @Override
    public void loop() {
        input.pollGamepad(gamepad1);

        /* ---------- Launch ---------- */
        if(input.right_trigger.down()){
            LAUNCHER_RUN =! LAUNCHER_RUN;
        }
        if(LAUNCHER_RUN){
            LauncherMotor.setPower(LAUNCHER_FAR_BASE);
        } else {
            LauncherMotor.setPower(LAUNCHER_IDLE);
        }
        /* ---------- LauncherHolders ---------- */
        if(input.left_bumper.down()){
            LeftLauncherHolderServo.setPosition(LEFT_LAUNCHER_HOLDER_HOLDING_POSITION);
            RightLauncherHolderServo.setPosition(RIGHT_LAUNCHER_HOLDER_HOLDING_POSITION);
        }
        if(input.right_bumper.down()){
            LeftLauncherHolderServo.setPosition(LEFT_LAUNCHER_HOLDER_LAUNCH_POSITION);
            RightLauncherHolderServo.setPosition(RIGHT_LAUNCHER_HOLDER_LAUNCH_POSITION);
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

        //todo change
        if(input.dpad_down.down()){
            INTAKE_POWER += 0.05;
        }
        if(input.dpad_up.down()){
            INTAKE_POWER -= 0.05;
        }

        /* ---------- Drivetrain ---------- */

        //Drivetrain movement values
        double forward =  gamepad1.left_stick_y;
        double strafes = -gamepad1.left_stick_x;
        double rotates =  gamepad1.right_stick_x;
        //Power fixer
        double denominator = max((abs(forward) + abs(strafes) + abs(rotates)), 1);

        //Setting Powers
        leftFront .setPower((forward + strafes + rotates) / denominator);
        rightFront.setPower((forward - strafes - rotates) / denominator);
        leftBack  .setPower((forward - strafes + rotates) / denominator);
        rightBack .setPower((forward + strafes - rotates) / denominator);

        /* ---------- Telemetry ---------- */
        telemetry.addLine("--------- Comp Drive Running ---------");
        telemetry.addData("Intake running? "           , INTAKE_RUN);
        telemetry.addData("Intake reversed? "          , INTAKE_REVERSED);
        telemetry.addData("Launcher holders holding? " , LAUNCHER_HOLDER_ENABLE);
        telemetry.addData("Launcher running? "         , LAUNCHER_RUN);
        telemetry.addData("Launcher Velocity: "        , LauncherMotor.getVelocity(AngleUnit.RADIANS));
        telemetry.addLine("");
    }

}
