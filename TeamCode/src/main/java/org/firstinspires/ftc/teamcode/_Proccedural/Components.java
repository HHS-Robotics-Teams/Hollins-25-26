package org.firstinspires.ftc.teamcode._Proccedural;


import static com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.FORWARD;
import static com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.REVERSE;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * File to store all hardware code
 */
public class Components {

    //Instantiate Drive Motors
    public static DcMotor leftFront;
    public static DcMotor rightFront;
    public static DcMotor leftRear;
    public static DcMotor rightRear;
    public static DcMotorEx launcher;
    public static CRServo leftFeeder;
    public static CRServo rightFeeder;
    public static DcMotor intake;
    public static CRServo LeftIntakeFeeder;
    public static CRServo RightIntakeFeeder;
    public static IMU imu;

    /*
        Method to initialize components
        param hardwareMap is hardwareMap
     */
    public static void initComponents(HardwareMap hardwareMap){

        //Initialize Motors
        leftFront = hardwareMap.get(DcMotor.class, "leftFront");
        rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        leftRear = hardwareMap.get(DcMotor.class, "leftBack");
        rightRear = hardwareMap.get(DcMotor.class, "rightBack");
        launcher = hardwareMap.get(DcMotorEx.class, "launcher");
        leftFeeder = hardwareMap.get(CRServo.class, "leftFeeder");
        rightFeeder = hardwareMap.get(CRServo.class, "rightFeeder");
        intake = hardwareMap.get(DcMotor.class, "intake");
        LeftIntakeFeeder = hardwareMap.get(CRServo.class, "LeftIntakeFeeder");
        RightIntakeFeeder = hardwareMap.get(CRServo.class, "RightIntakeFeeder");



        //Drive Motor Settings
        leftFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        leftRear.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightRear.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // Drive Motor Reverse
        leftRear.setDirection(REVERSE);
        leftFront.setDirection(REVERSE);
        rightRear.setDirection(FORWARD);
        rightFront.setDirection(REVERSE);

        // Launcher Motor Settings
        launcher.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(300, 0, 0, 10));
        launcher.setDirection(REVERSE);

        // Feeder Servo Settings
        leftFeeder.setDirection(REVERSE);
        rightFeeder.setDirection(FORWARD);

        // Intake Motor Settings
        intake.setDirection(REVERSE);
        intake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        imu = hardwareMap.get(IMU.class, "imu");
        imu.initialize(new IMU.Parameters(new RevHubOrientationOnRobot(RevHubOrientationOnRobot.LogoFacingDirection.RIGHT, RevHubOrientationOnRobot.UsbFacingDirection.FORWARD)));
    }


}
