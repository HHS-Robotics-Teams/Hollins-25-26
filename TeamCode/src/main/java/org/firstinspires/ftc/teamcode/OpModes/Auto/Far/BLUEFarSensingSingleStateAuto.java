package org.firstinspires.ftc.teamcode.OpModes.Auto.Far;

import static org.firstinspires.ftc.teamcode.Math.WebcamUtil.getTagYaw;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.IntakeMotor;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.LauncherMotor;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.LauncherFingerServo;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.LeftSideFeedRoller;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.RightSideFeedRoller;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.leftBack;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.leftFront;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.rightBack;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.rightFront;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.INTAKE_POWER;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCHER_FAR_TARGET;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCH_THRESHOLD;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCHER_FINGER_UP_POS;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCHER_FINGER_DOWN_POS;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.aProccedural.Components;

@Autonomous
public class BLUEFarSensingSingleStateAuto extends OpMode {

    enum AutoState {
        ALIGN_AND_SPIN_UP,
        SHOOT_ONE,
        SPIN_UP,
        UP,
        DOWN,
        INTAKE_RESET,
        END,
        SPIN_UP_TWO,
        UP_TWO,
        DOWN_TWO,
        INTAKE_RESET_TWO,
        SPIN_UP_THREE,
        UP_THREE,
        DOWN_THREE
    }

    AutoState autoState = AutoState.ALIGN_AND_SPIN_UP;

    ElapsedTime shotTimerOne = new ElapsedTime();
    ElapsedTime shotTimerTwo = new ElapsedTime();

    @Override
    public void init() {
        //initWebcamFinder("BLUE");
        Components.initComponents(hardwareMap);
        telemetry.addLine("READY TO START");
    }

    @Override
    public void init_loop() {
        //aprilTagTelemetry(telemetry);
        telemetry.update();
    }

    double tempAutoTime;

    @Override
    public void start() {
        tempAutoTime = getRuntime();
        autoState = AutoState.ALIGN_AND_SPIN_UP;
    }

    @Override
    public void loop() {
        telemetry.addData("Auto State: ", autoState);
        telemetry.addData("Time since last temp auto time: ", getRuntime() - tempAutoTime);
        switch (autoState) {
            case ALIGN_AND_SPIN_UP:
                LauncherMotor.setPower(1);
                if (/*alignWithWebcam("BLUE") ||*/ (getRuntime() - tempAutoTime >= 2)) {
                    autoState = AutoState.SHOOT_ONE;
                    tempAutoTime = getRuntime();
                }
                break;
            case SPIN_UP:
                LauncherMotor.setVelocity(LAUNCHER_FAR_TARGET, AngleUnit.RADIANS);
                if(Math.abs(LauncherMotor.getVelocity(AngleUnit.RADIANS) - LAUNCHER_FAR_TARGET - .1) <= LAUNCH_THRESHOLD){
                    autoState = AutoState.UP;
                    shotTimerOne.reset();
                    LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
                }
                if(Math.abs(LauncherMotor.getVelocity(AngleUnit.RADIANS) - LAUNCHER_FAR_TARGET - .1) <= LAUNCH_THRESHOLD*2){
                    IntakeMotor.setPower(0);
                }
                break;
            case UP:
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
                if(shotTimerOne.seconds() >= 0.75){
                    autoState = AutoState.DOWN;
                    shotTimerTwo.reset();
                }
                break;
            case DOWN:
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
                if(shotTimerTwo.seconds() >= 0.1){
                    LeftSideFeedRoller.setPower(1);
                    RightSideFeedRoller.setPower(1);
                }
                if(shotTimerTwo.seconds() >= 0.2) {
                    autoState = AutoState.INTAKE_RESET;
                    shotTimerOne.reset();
                }
                break;
            case INTAKE_RESET:
                IntakeMotor.setPower(INTAKE_POWER);
                if(shotTimerOne.seconds() >= 0.75){
                    autoState = AutoState.END;
                    shotTimerTwo.reset();
                }
                break;
                //todo below
            case SPIN_UP_TWO:
                LauncherMotor.setVelocity(LAUNCHER_FAR_TARGET, AngleUnit.RADIANS);
                if(Math.abs(LauncherMotor.getVelocity(AngleUnit.RADIANS) - LAUNCHER_FAR_TARGET) <= LAUNCH_THRESHOLD){
                    shotTimerOne.reset();
                }
                if(Math.abs(LauncherMotor.getVelocity(AngleUnit.RADIANS) - LAUNCHER_FAR_TARGET) <= LAUNCH_THRESHOLD*2){
                    IntakeMotor.setPower(0);
                }
                break;
            case UP_TWO:
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
                if(shotTimerOne.seconds() >= 0.75){
                    shotTimerTwo.reset();
                }
                break;
            case DOWN_TWO:
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
                if(shotTimerTwo.seconds() >= 0.1){
                    LeftSideFeedRoller.setPower(1);
                    RightSideFeedRoller.setPower(1);
                }
                if(shotTimerTwo.seconds() >= 0.2) {
                    shotTimerOne.reset();
                }
                break;
            case INTAKE_RESET_TWO:
                IntakeMotor.setPower(INTAKE_POWER);
                if(shotTimerOne.seconds() >= 0.75){
                    shotTimerTwo.reset();
                }
                break;

            case END:
                IntakeMotor.setPower(0);
                LeftSideFeedRoller.setPower(0);
                RightSideFeedRoller.setPower(0);
                shotTimerTwo.reset();
                shotTimerOne.reset();
                break;
        }


        //aprilTagTelemetry(telemetry);
        telemetry.update();
    }
}
