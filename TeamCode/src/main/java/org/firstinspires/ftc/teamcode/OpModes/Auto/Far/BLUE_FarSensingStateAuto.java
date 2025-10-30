package org.firstinspires.ftc.teamcode.OpModes.Auto.Far;

import static org.firstinspires.ftc.teamcode.Math.WebcamUtil.aprilTagTelemetry;
import static org.firstinspires.ftc.teamcode.Math.WebcamUtil.initWebcamFinder;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.IntakeMotor;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.LauncherMotor;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.LauncherFingerServo;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.leftBack;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.leftFront;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.rightBack;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.rightFront;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCHER_FAR_TARGET;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCH_THRESHOLD;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCHER_FINGER_UP_POS;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCHER_FINGER_DOWN_POS;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.aProccedural.Components;

@Autonomous
public class BLUE_FarSensingStateAuto extends OpMode {

    enum AutoState {
        ALIGN_AND_SPIN_UP,
        SHOOT_ONE,
        SHOOT_TWO,
        SHOOT_THREE,
        DRIVE_AND_SPIN_DOWN,
        END
    }
    AutoState autoState = AutoState.ALIGN_AND_SPIN_UP;
    enum ShotState {
        SPIN_UP,
        OPEN,
        INTAKE_MOVE,
        CLOSE,
        INTAKE_TWO,
        END
    }
    ShotState shotState = ShotState.SPIN_UP;

    @Override
    public void init() {
        initWebcamFinder("BLUE");
        Components.initComponents(hardwareMap);
        telemetry.addLine("READY TO START");
    }

    double tempAutoTime;
    @Override
    public void loop() {
        switch (autoState){
            case ALIGN_AND_SPIN_UP:
                LauncherMotor.setVelocity(LAUNCHER_FAR_TARGET);
                alignWithWebcam("BLUE");
                break;
            case SHOOT_ONE:
                shoot();
                if(shotState == ShotState.END){
                    autoState = AutoState.SHOOT_TWO;
                }
                break;
            case SHOOT_TWO:
                shoot();
                if(shotState == ShotState.END){
                    autoState = AutoState.SHOOT_THREE;
                }
                break;
            case SHOOT_THREE:
                shoot();
                if(shotState == ShotState.END){
                    autoState = AutoState.DRIVE_AND_SPIN_DOWN;
                    tempAutoTime = getRuntime();
                }
                break;
            case DRIVE_AND_SPIN_DOWN:
                IntakeMotor.setPower(0);
                LauncherMotor.setPower(0);
                rightFront.setPower(-.5);
                leftFront.setPower(-.5);
                leftBack.setPower(-.5);
                rightBack.setPower(-.5);
                if((getRuntime() - tempAutoTime) >= 1) {
                    autoState = AutoState.END;
                }
                break;
            case END:
                rightFront.setPower(0);
                leftFront.setPower(0);
                leftBack.setPower(0);
                rightBack.setPower(0);
                requestOpModeStop();
        }

        aprilTagTelemetry(telemetry);
        telemetry.update();
    }

    double shotTempTime;
    double INTAKE_MOVE_AMOUNT_FOR_SHOT = 2786.2/2;
    private void shoot() {
        switch(shotState){
            case SPIN_UP:
                LauncherMotor.setVelocity(LAUNCHER_FAR_TARGET);
                if(Math.abs(LauncherMotor.getVelocity(AngleUnit.RADIANS) - LAUNCHER_FAR_TARGET) <= LAUNCH_THRESHOLD){
                    shotState = ShotState.OPEN;
                    shotTempTime = getRuntime();
                }
                break;
            case OPEN:
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
                if(getRuntime() - shotTempTime >= 0.1){
                    shotState = ShotState.INTAKE_MOVE;
                    shotTempTime = getRuntime();
                    IntakeMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                    IntakeMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    IntakeMotor.setTargetPosition(0);
                }
                break;
            case INTAKE_MOVE:
                IntakeMotor.setTargetPosition((int) (/*1/2 a revolution*/INTAKE_MOVE_AMOUNT_FOR_SHOT));
                if(IntakeMotor.getCurrentPosition() >= INTAKE_MOVE_AMOUNT_FOR_SHOT - 50){
                    shotState = ShotState.CLOSE;
                    shotTempTime = getRuntime();
                }
                break;
            case CLOSE:
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
                if(getRuntime() - shotTempTime >= 0.1){
                    shotState = ShotState.INTAKE_TWO;
                    shotTempTime = getRuntime();
                    IntakeMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                    IntakeMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    IntakeMotor.setTargetPosition(0);
                }
                break;
            case INTAKE_TWO:
                IntakeMotor.setTargetPosition((int) (/*1/2 a revolution*/2 * INTAKE_MOVE_AMOUNT_FOR_SHOT));
                if(IntakeMotor.getCurrentPosition() >= 2 * INTAKE_MOVE_AMOUNT_FOR_SHOT - 50){
                    shotState = ShotState.END;
                    shotTempTime = getRuntime();
                }
                break;
            case END:

        }
    }
    public static void alignWithWebcam(String color) {
        switch (color){
            case("BLUE"):

                break;
            case("RED"):
                break;
        }
    }
}
