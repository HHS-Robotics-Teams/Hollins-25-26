package org.firstinspires.ftc.teamcode.OpModes.Auto.Far;

import static org.firstinspires.ftc.teamcode._Proccedural.Components.IntakeMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherFingerServo;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LeftSideFeedRoller;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.leftBack;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.leftFront;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.rightBack;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.rightFront;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.FINGER_UP_TIME;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.INTAKE_POWER;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_FAR_TARGET;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_FAR_TARGET_FIRST;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_FAR_TARGET_THIRD;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_FINGER_DOWN_POS;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_FINGER_UP_POS;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_IDLE;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCH_THRESHOLD;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.HollinsMadeUtil.AprilTagHelper;
import org.firstinspires.ftc.teamcode._Proccedural.Components;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

@Autonomous
public class BLUEFarSensingSingleStateAuto extends OpMode {

    enum AutoState {
        ALIGN_AND_SPIN_UP,
        SPIN_UP,
        UP,
        DOWN,
        INTAKE_RESET,
        SPIN_UP_TWO,
        UP_TWO,
        DOWN_TWO,
        INTAKE_RESET_TWO,
        SPIN_UP_THREE,
        UP_THREE,
        DOWN_THREE,
        END_SHOT_SEQ_ONE,
        MOVE_TO_INTAKE,
        INTAKE_START,
        INTAKE_END,
        MOVE_TO_SHOT,
        ALIGN_AND_SPIN_UP_TWO,
        SPIN_UP_FOUR,
        UP_FOUR,
        DOWN_FOUR,
        INTAKE_RESET_FOUR,
        SPIN_UP_FIVE,
        UP_FIVE,
        DOWN_FIVE,
        INTAKE_RESET_FIVE,
        SPIN_UP_SIX,
        UP_SIX,
        END_SHOT_SEQ_TWO,
        PARK,
        END
    }
    AutoState autoState = AutoState.ALIGN_AND_SPIN_UP;
    private AprilTagHelper tagHelper;
    //Pose2d startPose = new Pose2d(0,0,0);
    //MecanumDrive drive = new MecanumDrive(hardwareMap, startPose);
    ElapsedTime shotTimerOne = new ElapsedTime();
    ElapsedTime shotTimerTwo = new ElapsedTime();

    @Override
    public void init() {
        //initWebcamFinder("BLUE");
        Components.initComponents(hardwareMap);
        LauncherMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        tagHelper = new AprilTagHelper(hardwareMap, "Webcam");
        telemetry.addLine("READY TO START");

    }

    @Override
    public void init_loop() {
        // Call this every loop to get current detections
        AprilTagDetection tag = tagHelper.getFirstTag();
        if (tag != null && tag.ftcPose != null) { // Added null check for ftcPose
            telemetry.addLine("--- AprilTag Detected! ---");
            telemetry.addData("Tag ID", tag.id);
            // Display name if available
            telemetry.addData("Tag Name", tag.metadata != null ? tag.metadata.name : "N/A");
            telemetry.addData("X (in)", "%.2f", tag.ftcPose.x);
            telemetry.addLine("X = .27");
            //telemetry.addData("Y (in)", "%.2f", tag.ftcPose.y);
            //telemetry.addData("Z (in)", "%.2f", tag.ftcPose.z);
            telemetry.addData("Yaw (deg)", "%.2f", tag.ftcPose.yaw);
            telemetry.addLine("Yaw = -32");
            //telemetry.addData("Pitch (deg)", "%.2f", tag.ftcPose.pitch);
            //telemetry.addData("Roll (deg)", "%.2f", tag.ftcPose.roll);
        } else {
            telemetry.addLine("--- No AprilTag Detected ---");
        }
        telemetry.update();
    }

    double tempAutoTime;

    @Override
    public void start() {
        if (tagHelper != null) {
            tagHelper.stop();
        }
        tempAutoTime = getRuntime();
        autoState = AutoState.ALIGN_AND_SPIN_UP;
    }

    @Override
    public void loop() {
        telemetry.addData("Auto State: ", autoState);
        telemetry.addData("Time since last temp auto time: ", getRuntime() - tempAutoTime);
        telemetry.addData("Shooter motor Velocity", LauncherMotor.getVelocity(AngleUnit.RADIANS));
        telemetry.update();

        switch (autoState) {
            case ALIGN_AND_SPIN_UP:
                LauncherMotor.setPower(1);
                if(LauncherMotor.getVelocity(AngleUnit.RADIANS) > LAUNCHER_FAR_TARGET){
                    LauncherMotor.setVelocity(LAUNCHER_FAR_TARGET);
                }
                if (/*alignWithWebcam("BLUE") || */(getRuntime() - tempAutoTime >= 2)) {
                    autoState = AutoState.SPIN_UP;
                    tempAutoTime = getRuntime();
                }
                break;
            case SPIN_UP:
                LauncherMotor.setVelocity(LAUNCHER_FAR_TARGET, AngleUnit.RADIANS);
                if(Math.abs(LauncherMotor.getVelocity(AngleUnit.RADIANS) - LAUNCHER_FAR_TARGET_FIRST - .2 ) <= LAUNCH_THRESHOLD){
                    autoState = AutoState.UP;
                    shotTimerOne.reset();

                }
                break;
            case UP:

                LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
                if(shotTimerOne.seconds() >= FINGER_UP_TIME){
                    autoState = AutoState.DOWN;
                    shotTimerTwo.reset();
                }
                break;
            case DOWN:
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
                LeftSideFeedRoller.setPower(1);

                if(shotTimerTwo.seconds() >= 0.2) {
                    autoState = AutoState.INTAKE_RESET;
                    shotTimerOne.reset();
                }
                break;
            case INTAKE_RESET:
                if(shotTimerOne.seconds() >= .75){
                    autoState = AutoState.SPIN_UP_TWO;
                    shotTimerTwo.reset();
                }
                break;
            case SPIN_UP_TWO:
                LeftSideFeedRoller.setPower(0);


                LauncherMotor.setVelocity(LAUNCHER_FAR_TARGET, AngleUnit.RADIANS);
                if(Math.abs(LauncherMotor.getVelocity(AngleUnit.RADIANS) - LAUNCHER_FAR_TARGET) <= LAUNCH_THRESHOLD && shotTimerTwo.seconds() >= 1){
                    shotTimerOne.reset();
                    shotTimerTwo.reset();
                    autoState = AutoState.UP_TWO;
                }
                break;
            case UP_TWO:
                IntakeMotor.setPower(INTAKE_POWER);
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
                if(shotTimerOne.seconds() >= FINGER_UP_TIME){
                    shotTimerTwo.reset();
                    autoState = AutoState.DOWN_TWO;
                }
                break;
            case DOWN_TWO:
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
                if(shotTimerTwo.seconds() >= 0.1){
                    LeftSideFeedRoller.setPower(1);


                }
                if(shotTimerTwo.seconds() >= 0.2) {
                    shotTimerOne.reset();
                    autoState = AutoState.INTAKE_RESET_TWO;
                }
                break;
            case INTAKE_RESET_TWO:
                if(shotTimerOne.seconds() >= 0.65){
                    shotTimerTwo.reset();
                    autoState = AutoState.SPIN_UP_THREE;
                    IntakeMotor.setPower(0);
                }
                break;
            case SPIN_UP_THREE:
                LauncherMotor.setVelocity(LAUNCHER_FAR_TARGET_THIRD, AngleUnit.RADIANS);
                if(Math.abs(LauncherMotor.getVelocity(AngleUnit.RADIANS) - LAUNCHER_FAR_TARGET_THIRD) <= LAUNCH_THRESHOLD){
                    shotTimerOne.reset();
                    shotTimerTwo.reset();
                    autoState = AutoState.UP_THREE;
                }
                if(Math.abs(LauncherMotor.getVelocity(AngleUnit.RADIANS) - LAUNCHER_FAR_TARGET) <= LAUNCH_THRESHOLD*4){
                    IntakeMotor.setPower(0);
                }
                break;
            case UP_THREE:
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
                if(shotTimerOne.seconds() >= FINGER_UP_TIME){
                    shotTimerTwo.reset();
                    autoState = AutoState.DOWN_THREE;
                }
                break;
            case DOWN_THREE:
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
                if(shotTimerTwo.seconds() >= 0.2) {
                    shotTimerOne.reset();
                    autoState = AutoState.END_SHOT_SEQ_ONE;
                }
                break;
            case END_SHOT_SEQ_ONE:
                IntakeMotor.setPower(0);
                LeftSideFeedRoller.setPower(0);

                LauncherMotor.setPower(LAUNCHER_IDLE);
                shotTimerTwo.reset();
                shotTimerOne.reset();
                autoState = AutoState.PARK; //todo fix
                break;
            case PARK:
                leftBack.setPower(.4);
                rightBack.setPower(.4);
                leftFront.setPower(.4);
                rightFront.setPower(.4);
                shotTimerTwo.reset();
                shotTimerOne.reset();
                tempAutoTime = getRuntime();
                autoState = AutoState.END;
                break;
            case END:
                if(shotTimerOne.seconds() >= .67){
                    leftBack.setPower(0);
                    rightBack.setPower(0);
                    leftFront.setPower(0);
                    rightFront.setPower(0);
                    leftBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                    leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                    rightBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                    rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                    requestOpModeStop();
                }
                break;
        }

        //aprilTagTelemetry(telemetry);
        telemetry.update();
    }
    @Override
    public void stop() {
        LauncherMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        LauncherMotor.setPower(0);
    }
}
