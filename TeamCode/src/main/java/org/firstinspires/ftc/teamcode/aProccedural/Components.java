package org.firstinspires.ftc.teamcode.aProccedural;

import com.acmerobotics.roadrunner.ftc.LazyImu;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;

/**
 * File to store all hardware code
 */
public class Components {

    //Instantiate Drive Motors
    public static DcMotor leftFront;
    public static DcMotor rightFront;
    public static DcMotor leftBack;
    public static DcMotor rightBack;

    //Instantiate Launcher Motor
    public static DcMotorEx LauncherMotor;

    //Instantiate Intake Motor
    public static DcMotor IntakeMotor;

    //Instantiate Servos
    public static Servo LauncherFingerServo;
    //public static Servo TopRampHolderServo;
    public static CRServo LeftSideFeedRoller;
    public static CRServo RightSideFeedRoller;
    public static CRServo IntakeSecondLevelServo;
    public static Servo ParkingStopServo;

    public static LazyImu imu;
    public static WebcamName webcam;
    public static DistanceSensor artifactCounterDistance;


    /**
        Method to initialize components
        param hardwareMap is hardwareMap
     */
    public static void initComponents(HardwareMap hardwareMap){

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

        //Initialize Launcher
        LauncherMotor = hardwareMap.get(DcMotorEx.class, "LauncherMotor");
        LauncherMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        LauncherMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        LauncherMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        //Initialize Intake Motor
        IntakeMotor = hardwareMap.get(DcMotorEx.class, "IntakeMotor");
        IntakeMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        IntakeMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        IntakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        //Initialize Servos
        LauncherFingerServo = hardwareMap.get(Servo.class, "LauncherFingerServo");
        LeftSideFeedRoller = hardwareMap.get(CRServo.class, "LeftSideFeedRoller");
        RightSideFeedRoller = hardwareMap.get(CRServo.class, "RightSideFeedRoller");
        RightSideFeedRoller.setDirection(DcMotorSimple.Direction.REVERSE);
        IntakeSecondLevelServo = hardwareMap.get(CRServo.class, "IntakeSecondLevelServo");


        //Initialize Sensors
        //imu = new LazyHardwareMapImu(hardwareMap, "imu", new RevHubOrientationOnRobot(
        //      RevHubOrientationOnRobot.LogoFacingDirection.RIGHT, RevHubOrientationOnRobot.UsbFacingDirection.UP));
        webcam = hardwareMap.get(WebcamName.class, "Webcam");// todo fix is wrong class
        //artifactCounterDistance = hardwareMap.get(DistanceSensor.class, "artifactCounter");

    }


}
