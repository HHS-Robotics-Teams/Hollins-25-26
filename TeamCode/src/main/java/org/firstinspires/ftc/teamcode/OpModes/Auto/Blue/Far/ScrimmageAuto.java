package org.firstinspires.ftc.teamcode.OpModes.Auto.Blue.Far;

import static org.firstinspires.ftc.teamcode.aProccedural.Components.IntakeMotor;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.LauncherMotor;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.LeftLauncherHolderServo;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.RightLauncherHolderServo;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.leftBack;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.leftFront;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.rightBack;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.rightFront;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.INTAKE_POWER;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCHER_FAR_BASE;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCHER_FAR_TARGET;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCH_THRESHOLD;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LEFT_LAUNCHER_HOLDER_HOLDING_POSITION;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LEFT_LAUNCHER_HOLDER_LAUNCH_POSITION;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.RIGHT_LAUNCHER_HOLDER_HOLDING_POSITION;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.RIGHT_LAUNCHER_HOLDER_LAUNCH_POSITION;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.numShot;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.aProccedural.Components;

@Autonomous
public class ScrimmageAuto extends OpMode {

    enum Autostate{
        WAIT_FOR_START,
        SPIN_UP,
        SHOOT,
        DRIVE,
        STOP
    }
    Autostate autostate = Autostate.WAIT_FOR_START;

    @Override
    public void init() {
        Components.initComponents(hardwareMap);
    }

    @Override
    public void start() {
        autostate = Autostate.SPIN_UP;
    }

    double timeAtShot;
    double shootTime = 0.1;

    @Override
    public void loop() {
        switch (autostate){
            case SPIN_UP:
                LauncherMotor.setPower(LAUNCHER_FAR_BASE);
                if(getRuntime() - timeAtShot >= shootTime * 2){IntakeMotor.setPower(0);}
                if(Math.abs(LauncherMotor.getVelocity(AngleUnit.RADIANS) - LAUNCHER_FAR_TARGET) <= LAUNCH_THRESHOLD){
                    autostate = Autostate.SHOOT;
                    numShot++;
                    timeAtShot = getRuntime();
                }
                break;
            case SHOOT:
                LeftLauncherHolderServo.setPosition(LEFT_LAUNCHER_HOLDER_LAUNCH_POSITION);
                RightLauncherHolderServo.setPosition(RIGHT_LAUNCHER_HOLDER_LAUNCH_POSITION);
                if(getRuntime() - timeAtShot >= shootTime){
                    if(numShot >= 3){
                        autostate = Autostate.DRIVE;
                    }
                    LeftLauncherHolderServo.setPosition(LEFT_LAUNCHER_HOLDER_HOLDING_POSITION);
                    RightLauncherHolderServo.setPosition(RIGHT_LAUNCHER_HOLDER_HOLDING_POSITION);
                    IntakeMotor.setPower(INTAKE_POWER);
                }
                break;
            case DRIVE:
                IntakeMotor.setPower(0);
                LauncherMotor.setPower(0);
                rightFront.setPower(0.5);
                leftFront.setPower(0.5);
                leftBack.setPower(0.5);
                rightBack.setPower(0.5);
                if(getRuntime() - timeAtShot >= 1) {
                    autostate = Autostate.STOP;
                }
                break;
            case STOP:
                rightFront.setPower(0);
                leftFront.setPower(0);
                leftBack.setPower(0);
                rightBack.setPower(0);
                LauncherMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                requestOpModeStop();
                break;
        }
    }
}