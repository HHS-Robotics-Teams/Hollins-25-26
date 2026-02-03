package org.firstinspires.ftc.teamcode._OpModes.Prototypes.old;


import static org.firstinspires.ftc.teamcode._Proccedural.Constants.DriveSlowdown;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_FINGER_DOWN_POS;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_FINGER_UP_POS;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_HOOD_DOWN_POS;
import static java.lang.Math.abs;
import static java.lang.Math.max;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode._Proccedural.Input;

//testing opmode disabled

@TeleOp
@Disabled
@Deprecated
public class LauncherTesting extends OpMode {


    //Instantiate Drive Motors
    public static DcMotor leftFront;
    public static DcMotor rightFront;
    public static DcMotor leftBack;
    public static DcMotor rightBack;


    //left
    private DcMotorEx launcher_motor;
    //right
    private DcMotor intake_motor;
    public static CRServo LeftSideFeedRoller;
    //public static CRServo RightSideFeedRoller;
    //public static  CRServo IntakeSecondLevelServo;
    private static Servo LauncherFingerServo;
    private static Servo LauncherHoodServo;
    //power
    private double power = 0.25;
    private double delta = 0.05;
    Input input = new Input();
    @Override
    public void init() {
        launcher_motor = hardwareMap.get(DcMotorEx.class, "LauncherMotor");
        launcher_motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        launcher_motor.setDirection(DcMotorSimple.Direction.REVERSE);
        intake_motor = hardwareMap.get(DcMotor.class, "IntakeMotor");
        intake_motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        LauncherHoodServo = hardwareMap.get(Servo.class, "LauncherHoodServo");

        LeftSideFeedRoller = hardwareMap.get(CRServo.class, "LeftSideFeedRoller");
        LeftSideFeedRoller.setDirection(DcMotorSimple.Direction.REVERSE);
        //RightSideFeedRoller = hardwareMap.get(CRServo.class, "RightSideFeedRoller");
        LauncherFingerServo = hardwareMap.get(Servo.class, "LauncherFingerServo");
        //RightSideFeedRoller.setDirection(DcMotorSimple.Direction.REVERSE);
        //Initialize Drive Motors
        leftFront = hardwareMap.get(DcMotor.class, "leftFront");
        rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        leftBack = hardwareMap.get(DcMotor.class, "leftBack");
        rightBack = hardwareMap.get(DcMotor.class, "rightBack");

        //Reversing Motors
        leftFront.setDirection(DcMotorSimple.Direction.REVERSE);
        rightBack.setDirection(DcMotorSimple.Direction.REVERSE);

        //Drive Motor Settings
        leftFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        leftBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        //IntakeSecondLevelServo = hardwareMap.get(CRServo.class, "IntakeSecondLevelServo");

        telemetry.addLine("WARNING: UPON START LAUNCHER AND INTAKE WILL START SPINNING");
    }

    public void start() {
        launcher_motor.setPower(power);
        intake_motor.setPower(1);
        LeftSideFeedRoller.setPower(1);
        LauncherHoodServo.setPosition(LAUNCHER_HOOD_DOWN_POS);
        //RightSideFeedRoller.setPower(1);

    }

    @Override
    public void loop() {


        //run
        if(gamepad1.a){
            launcher_motor.setPower(power);
        } else {
            launcher_motor.setPower(0);
        }
        //Intake
        if (gamepad1.left_trigger >= .3){
            intake_motor.setPower(1);
        } else {
            intake_motor.setPower(0);
        }
        // Feed Rollers
        if (gamepad1.right_trigger >= .3){
            LeftSideFeedRoller.setPower(1);
            //RightSideFeedRoller.setPower(1);
            //IntakeSecondLevelServo.setPower(1);
        } else {
            LeftSideFeedRoller.setPower(0);
            //RightSideFeedRoller.setPower(0);
            //IntakeSecondLevelServo.setPower(0);
        }
        // Launcher Finger
        if (gamepad1.right_bumper){
            LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
        } else {
            LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
        }
        if(input.x.down()){
            LauncherHoodServo.setPosition(LauncherHoodServo.getPosition()+0.05);
        }
        if(input.y.down()){
            LauncherHoodServo.setPosition(LauncherHoodServo.getPosition()-0.05);
        }

        //change power
        if(input.dpad_up.down()){
            power += delta;
        }
        if(input.dpad_down.down()){
            power -= delta;
        }

        //Reverse
        if(input.b.down()){
            if(launcher_motor.getDirection() == DcMotorSimple.Direction.REVERSE){
                launcher_motor.setDirection(DcMotorSimple.Direction.FORWARD);
                intake_motor.setDirection(DcMotorSimple.Direction.FORWARD);
            } else {
                launcher_motor.setDirection(DcMotorSimple.Direction.REVERSE);
                intake_motor.setDirection(DcMotorSimple.Direction.REVERSE);
            }
        }

//        if(input.dpad_up.down()){
//            delta += 0.01;
//        }
//        if(input.dpad_down.down()){
//            delta -= 0.01;
//        }

        input.pollGamepad(gamepad1);

        /* ---------- Drivetrain ---------- */


        //Drivetrain movement values
        double forward = -gamepad1.left_stick_y  * 0.8;
        double strafes =  gamepad1.left_stick_x  * 1;
        double rotates = -gamepad1.right_stick_x * 0.6;


        if (abs(forward) <= 0.15) {
            forward = 0;
        }
        if (abs(strafes) <= 0.15) {
            strafes = 0;
        }
        if (abs(rotates) <= 0.15) {
            rotates = 0;
        }
        // slow down
        if (input.left_stick_button.down() || input.right_stick_button.down()){
            DriveSlowdown = !DriveSlowdown;
        }
        if (DriveSlowdown){
            rotates = rotates / 3;
            strafes = strafes / 3;
            forward = forward / 3;
        }



        //Power fixer
        double denominator = max((abs(forward) + abs(strafes) + abs(rotates)), 1);

        //Setting Powers
        leftFront.setPower((forward + strafes + rotates) / denominator);
        rightFront.setPower((forward - strafes - rotates) / denominator);
        leftBack.setPower((forward - strafes + rotates) / denominator);
        rightBack.setPower((forward + strafes - rotates) / denominator);

        telemetry.addData("Current power: ", power);
        telemetry.addData("Current speed: ", launcher_motor.getVelocity());
        telemetry.addData("Current delta: ", delta);
        telemetry.addData("Shooter Hood Pos", LauncherHoodServo.getPosition());
        telemetry.addLine();
        telemetry.addLine("dpad up to increase delta\ndpad down to decrease delta\na to toggle on/off\nx to increase power by delta\ny to decrease power by delta\nb to reverse");
    }
}
