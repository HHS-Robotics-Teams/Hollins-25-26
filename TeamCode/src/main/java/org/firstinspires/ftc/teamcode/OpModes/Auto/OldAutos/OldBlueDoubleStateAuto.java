package org.firstinspires.ftc.teamcode.OpModes.Auto.OldAutos;

import static org.firstinspires.ftc.teamcode._Proccedural.Components.IntakeMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherFingerServo;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LeftSideFeedRoller;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.leftBack;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.leftFront;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.rightBack;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.rightFront;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.INTAKE_POWER;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_FAR_TARGET;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_FINGER_DOWN_POS;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_FINGER_UP_POS;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCH_THRESHOLD;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode._Proccedural.Components;

@Deprecated

@Autonomous
public class OldBlueDoubleStateAuto extends OpMode {

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
        UP,
        DOWN,
        INTAKE_RESET,
        END
    }
    ShotState shotState = ShotState.SPIN_UP;
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
        shotState = ShotState.SPIN_UP;
    }
    @Override
    public void loop() {
        telemetry.addData("Auto State: ", autoState);
        telemetry.addData("Shot State: ", shotState);
        telemetry.addData("Time since last temp auto time: ", getRuntime() - tempAutoTime);
        switch (autoState){
            case ALIGN_AND_SPIN_UP:
                LauncherMotor.setPower(1);
                if(/*alignWithWebcam("BLUE") ||*/ (getRuntime() - tempAutoTime >= 2)){
                    autoState = AutoState.SHOOT_ONE;
                    tempAutoTime = getRuntime();
                }
                break;
            case SHOOT_ONE:
                shoot();
                if(shotState == ShotState.END){
                    autoState = AutoState.SHOOT_TWO;
                    IntakeMotor.setPower(INTAKE_POWER);
                }
                telemetry.addLine("wowee");
                break;
            case SHOOT_TWO:
                shoot();
                if(shotState == ShotState.END){
                    autoState = AutoState.SHOOT_THREE;
                    IntakeMotor.setPower(INTAKE_POWER);
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
                rightFront.setPower(.5);
                leftFront.setPower(.5);
                leftBack.setPower(.5);
                rightBack.setPower(.5);
                if((getRuntime() - tempAutoTime) >= 1) {
                    autoState = AutoState.END;
                }
                break;
            case END:
                LauncherMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                rightFront.setPower(0);
                leftFront.setPower(0);
                leftBack.setPower(0);
                rightBack.setPower(0);
                requestOpModeStop();
        }

        //aprilTagTelemetry(telemetry);
        telemetry.update();
    }

    public void shoot() {
        switch(shotState){
            case SPIN_UP:
                LauncherMotor.setVelocity(LAUNCHER_FAR_TARGET, AngleUnit.RADIANS);
                if(Math.abs(LauncherMotor.getVelocity(AngleUnit.RADIANS) - LAUNCHER_FAR_TARGET) <= LAUNCH_THRESHOLD){
                    shotState = ShotState.UP;
                    shotTimerOne.reset();
                    if(autoState == AutoState.SHOOT_ONE){
                        LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
                    }
                }
                if(Math.abs(LauncherMotor.getVelocity(AngleUnit.RADIANS) - LAUNCHER_FAR_TARGET) <= LAUNCH_THRESHOLD*2){
                    IntakeMotor.setPower(0);
                }
                break;
            case UP:
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
                if(shotTimerOne.seconds() >= 0.75){
                    shotState = ShotState.DOWN;
                    shotTimerTwo.reset();
                }
                break;
            case DOWN:
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
                if(shotTimerTwo.seconds() >= 0.1){
                    LeftSideFeedRoller.setPower(1);

                }
                if(shotTimerTwo.seconds() >= 0.2) {
                    shotState = ShotState.INTAKE_RESET;
                    shotTimerOne.reset();
                }
                break;
            case INTAKE_RESET:
                IntakeMotor.setPower(INTAKE_POWER);
                if(shotTimerOne.seconds() >= 0.75){
                    shotState = ShotState.END;
                    shotTimerTwo.reset();
                }
                break;
            case END:
                IntakeMotor.setPower(0);
                LeftSideFeedRoller.setPower(0);

                shotState = ShotState.SPIN_UP;
                shotTimerTwo.reset();
                shotTimerOne.reset();
                break;
        }
    }

}
