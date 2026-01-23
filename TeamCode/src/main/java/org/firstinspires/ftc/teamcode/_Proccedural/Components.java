package org.firstinspires.ftc.teamcode._Proccedural;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.Util.AprilTagHelper;

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
    public static DcMotor ConveyorMotor;

    // Instantiate Parking Motor
    public static DcMotor Parking_Motor;

    //Instantiate Servos
    public static Servo LauncherFingerServo;
    public static Servo LauncherSafetyServo;
    public static CRServo LeftSideFeedRoller;
    public static Servo cameraTiltServo;
    public static Servo LauncherHoodServo;

    //Instantiate Sensors
    public static IMU imu;
    public static WebcamName webcam;
    public static AprilTagHelper tagHelper;
    public static DistanceSensor rightArtifactCounterDistance;
    public static DistanceSensor leftArtifactCounterDistance;
    public static DistanceSensor rearDistance;

    /**
     * Method to initialize components
     * param hardwareMap is hardwareMap
     */
    public static void initComponents(HardwareMap hardwareMap) {

        //Initialize Drive Motors
        leftFront = hardwareMap.get(DcMotor.class, "leftFront");
        rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        leftBack = hardwareMap.get(DcMotor.class, "leftBack");
        rightBack = hardwareMap.get(DcMotor.class, "rightBack");

        //Reversing Motors
        leftFront.setDirection(DcMotorSimple.Direction.REVERSE);
        leftBack.setDirection(DcMotorSimple.Direction.REVERSE);

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
        MotorConfigurationType type = LauncherMotor.getMotorType().clone(); //DO NOT TOUCH
        type.setAchieveableMaxRPMFraction(1.0); //DO NOT TOUCH
        LauncherMotor.setMotorType(type); //DO NOT TOUCH
        LauncherMotor.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(120, 5, 4.5, 15)); //DO NOT TOUCH

        //Initialize Intake Motor
        IntakeMotor = hardwareMap.get(DcMotorEx.class, "IntakeMotor");
        IntakeMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        IntakeMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        IntakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        ConveyorMotor = hardwareMap.get(DcMotor.class, "ConveyorMotor");
        ConveyorMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        ConveyorMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        ConveyorMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        //Initialize Parking Motor
        Parking_Motor = hardwareMap.get(DcMotorEx.class, "ParkingMotor");
        Parking_Motor.setDirection(DcMotorSimple.Direction.REVERSE);
        Parking_Motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        //Initialize Servos
        //LauncherHoodServo = hardwareMap.get(Servo.class, "LauncherHoodServo");
        //LauncherFingerServo = hardwareMap.get(Servo.class, "LauncherFingerServo");
        LauncherSafetyServo = hardwareMap.get(Servo.class, "LauncherSafetyServo");
        LeftSideFeedRoller = hardwareMap.get(CRServo.class, "LeftSideFeedRoller");
        LeftSideFeedRoller.setDirection(DcMotorSimple.Direction.FORWARD);

        //Initialize Sensors
        imu = hardwareMap.get(IMU.class, "imu");
        imu.initialize(new IMU.Parameters(new RevHubOrientationOnRobot(RevHubOrientationOnRobot.LogoFacingDirection.LEFT, RevHubOrientationOnRobot.UsbFacingDirection.UP)));
        webcam = hardwareMap.get(WebcamName.class, "Webcam");
        tagHelper = new AprilTagHelper(hardwareMap, "Webcam");
        rightArtifactCounterDistance = hardwareMap.get(DistanceSensor.class, "rightArtifactCounter");
        leftArtifactCounterDistance = hardwareMap.get(DistanceSensor.class, "leftArtifactCounter");
        rearDistance = hardwareMap.get(DistanceSensor.class, "rearDistance");
        cameraTiltServo = hardwareMap.get(Servo.class, "cameraTiltServo");

        //other
        for (LynxModule m : hardwareMap.getAll(LynxModule.class)){
            m.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);
        }

    }

}
