package org.firstinspires.ftc.teamcode._OpModes.TeleOp;

import static org.firstinspires.ftc.teamcode._Proccedural.Components.ConveyorMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.IntakeMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherSafetyServo;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.Parking_Motor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.cameraTiltServo;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.centerDistance;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.frontDistance;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.limelight;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.rearDistance;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.CAMERA_START_POS;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.INTAKE_LEVEL_TWO_RUN;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.INTAKE_POWER;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.INTAKE_REVERSED;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.INTAKE_RUN;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_RUN;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.SAFETY_HOLDING;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.park_Pos;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode._Proccedural.Components;
import org.firstinspires.ftc.teamcode._Proccedural.Input;
import org.firstinspires.ftc.teamcode._Util.AprilTagMethod;
import org.firstinspires.ftc.teamcode._Util.HoodUtil;
import org.firstinspires.ftc.teamcode._Util.LauncherUtilV3;
import org.firstinspires.ftc.teamcode._Util.LightUtil;
import org.firstinspires.ftc.teamcode._Util.TeleOpDrive;

@TeleOp
public class CompDriveV6Blue extends OpMode {
    //Instantiated new input
    Input input = new Input();
    LauncherUtilV3 launcherUtil;
    ElapsedTime intakeTimer = new ElapsedTime(ElapsedTime.SECOND_IN_NANO);

    @Override
    public void init() {
        //Initialize Components
        Components.initComponents(hardwareMap);
        launcherUtil = new LauncherUtilV3("BLUE", false, new LightUtil(2, hardwareMap));
        /* ---------- Telemetry ---------- */
        telemetry.addLine("--------- Init Complete ---------");
    }

    @Override
    public void start() {
        limelight.start();
        //resets from other flags
        INTAKE_RUN = false;
        INTAKE_LEVEL_TWO_RUN = false;
        LAUNCHER_RUN = false;
        LauncherMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        cameraTiltServo.setPosition(CAMERA_START_POS);
        Parking_Motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Parking_Motor.setTargetPosition(0);
        Parking_Motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        Parking_Motor.setPower(1);
        intakeTimer.reset();
    }

    @Override
    public void loop() {
        input.pollGamepad(gamepad1);

        launcherUtil.spinUp();
        launcherUtil.updateLights();
        /* ---------- Drivetrain ---------- */
        TeleOpDrive.run(-gamepad1.left_stick_y,gamepad1.left_stick_x * 1.1, gamepad1.right_stick_x);



        /* ---------- Launch ---------- */
        if (input.right_trigger.down()) {
            launcherUtil.resetLauncherUtilTimeout();
            LAUNCHER_RUN = !LAUNCHER_RUN;
            INTAKE_REVERSED = false;
        }
        if(LAUNCHER_RUN){
            if( input.right_bumper.down()){
                launcherUtil.cancelLaunch();
            }
        }
        if(input.a_cross.down()){
            launcherUtil.overwriteHoodState(HoodUtil.HoodState.Far);
        }

        /* ---------- Intake ---------- */
        if (input.b_circle.down()) {
            // Reverses intake
            INTAKE_REVERSED = !INTAKE_REVERSED;
        }

        // Hold left trigger to run the main intake motor
        if (!LAUNCHER_RUN) INTAKE_RUN = input.left_trigger.held();
        if (!LAUNCHER_RUN) INTAKE_LEVEL_TWO_RUN = input.left_bumper.held() || input.left_trigger.held();

        if(intakeTimer.seconds() > 0.25){
            launcherUtil.setLightGreen();
        } else {
            launcherUtil.setLightRed();
        }

        if(centerDistance.getDistance(DistanceUnit.INCH) >= 4 || frontDistance.getDistance(DistanceUnit.INCH) >= 6.5 || rearDistance.getDistance(DistanceUnit.INCH) >= 11) {
            intakeTimer.reset();
        }

        // Control the main intake motor
        if (INTAKE_RUN) {
            IntakeMotor.setPower(INTAKE_REVERSED ? -INTAKE_POWER : INTAKE_POWER);
        } else {
            IntakeMotor.setPower(0);
        }

        if (INTAKE_LEVEL_TWO_RUN) {
            ConveyorMotor.setPower(INTAKE_REVERSED ? -1 : 1);
        } else {
            ConveyorMotor.setPower(0);
        }

        //while LAUNCHER_RUN flag is true launch
        if (LAUNCHER_RUN) {
            telemetry.addLine("Launch Status:" + launcherUtil.runLauncher());
        } else {
            launcherUtil.resetLauncherUtilTimeout();
            launcherUtil.updateHood();
            LauncherMotor.setVelocity(1000);
            LauncherSafetyServo.setPosition(SAFETY_HOLDING);
            if(launcherUtil.getLaunchState() != LauncherUtilV3.LaunchState.FIND_TAG){
                launcherUtil.cancelLaunch();
            }
        }

        if (input.dpad_down.down()) {
            Parking_Motor.setTargetPosition(park_Pos);
        }
        if (input.dpad_up.down()) {
            Parking_Motor.setTargetPosition(0);
        }

        /* ---------- Telemetry ---------- */
        telemetry.addLine("--------- Comp Drive Running ---------");
        telemetry.addData("Intake running? ", INTAKE_RUN);
        telemetry.addData("Intake reversed? ", INTAKE_REVERSED);
        telemetry.addData("Launcher running? ", LAUNCHER_RUN);
        telemetry.addData("Light Util Status: ", launcherUtil.getLightState());
    }

}
