package org.firstinspires.ftc.teamcode._OpModes.TeleOp;

import static org.firstinspires.ftc.teamcode._Proccedural.Components.ConveyorMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.IntakeMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherSafetyServo;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.Parking_Motor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.cameraTiltServo;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.leftArtifactCounterDistance;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.leftBack;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.leftFront;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.rightArtifactCounterDistance;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.rightBack;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.rightFront;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.CAMERA_START_POS;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.INTAKE_LEVEL_TWO_RUN;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.INTAKE_POWER;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.INTAKE_REVERSED;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.INTAKE_RUN;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_RUN;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.SAFETY_HOLDING;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.park_Pos;
import static java.lang.Math.abs;
import static java.lang.Math.max;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode._Util.AprilTagMethod;
import org.firstinspires.ftc.teamcode._Util.LauncherUtilV2;
import org.firstinspires.ftc.teamcode._Util.LightUtil;
import org.firstinspires.ftc.teamcode._Proccedural.Components;
import org.firstinspires.ftc.teamcode._Proccedural.Input;

@TeleOp
public class CompDriveV4RED extends OpMode {
    //Instantiated new input
    Input input = new Input();
    AprilTagMethod aprilTagDetector;
    LauncherUtilV2 launcherUtil;
    ElapsedTime intakeTimer = new ElapsedTime(ElapsedTime.SECOND_IN_NANO);

    @Override
    public void init() {
        //Initialize Components
        Components.initComponents(hardwareMap);
        aprilTagDetector = new AprilTagMethod();
        launcherUtil = new LauncherUtilV2("RED", false, new LightUtil(2, hardwareMap));
        /* ---------- Telemetry ---------- */
        telemetry.addLine("--------- Init Complete ---------");
    }
    @Override
    public void init_loop() {
        // April Tag Detector
        aprilTagDetector.updateAndShowTelemetry(telemetry);
    }

    @Override
    public void start() {
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

        //Drivetrain movement values
        double forward = -gamepad1.left_stick_y;
        double strafes = gamepad1.left_stick_x * 1.1;
        double rotates = gamepad1.right_stick_x;

        if (abs(forward) <= 0.15) {
            forward = 0;
        }
        if (abs(strafes) <= 0.15) {
            strafes = 0;
        }
        if (abs(rotates) <= 0.15) {
            rotates = 0;
        }

        //Power fixer
        double denominator = max((abs(forward) + abs(strafes) + abs(rotates)), 1.75);

        //Setting Powers
        leftFront.setPower((forward + strafes + rotates) / denominator);
        rightFront.setPower((forward - strafes - rotates) / denominator);
        leftBack.setPower((forward - strafes + rotates) / denominator);
        rightBack.setPower((forward + strafes - rotates) / denominator);

        /* ---------- Launch ---------- */
        if (input.right_trigger.down()) {
            LAUNCHER_RUN = !LAUNCHER_RUN;
            INTAKE_REVERSED = false;
        }
        if (input.right_bumper.down()) {
            LAUNCHER_RUN = !LAUNCHER_RUN;
            INTAKE_REVERSED = false;
        }

        /* ---------- Intake ---------- */
        if (input.b.down()) {
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

        if(leftArtifactCounterDistance.getDistance(DistanceUnit.INCH) >= 7 || rightArtifactCounterDistance.getDistance(DistanceUnit.INCH) >= 7) {
            intakeTimer.reset();
        }

        // Control the main intake motor
        if (INTAKE_RUN) {
            IntakeMotor.setPower(INTAKE_REVERSED ? -INTAKE_POWER : INTAKE_POWER);
        } else {
            IntakeMotor.setPower(0);
        }

        if (INTAKE_LEVEL_TWO_RUN) {
            double power = INTAKE_REVERSED ? -1 : 1;
            ConveyorMotor.setPower(power);
        } else {
            ConveyorMotor.setPower(0);
        }

        //while LAUNCHER_RUN flag is true launch
        if (LAUNCHER_RUN) {
            telemetry.addLine("Launch Status:" + launcherUtil.runLauncher());
            if(input.right_trigger.down() || input.right_bumper.down()){
                launcherUtil.cancelLaunch();
            }
        } else {
            launcherUtil.resetLauncherUtilTimeout();
            LauncherSafetyServo.setPosition(SAFETY_HOLDING);
            if(launcherUtil.getLaunchState() != LauncherUtilV2.LaunchState.FIND_TAG){
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
