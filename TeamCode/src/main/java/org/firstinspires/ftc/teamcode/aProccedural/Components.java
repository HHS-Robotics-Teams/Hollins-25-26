package org.firstinspires.ftc.teamcode.aProccedural;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

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
    public static Servo LeftLauncherHolderServo;
    public static Servo RightLauncherHolderServo;


    //todo public static OpenCvWebcam webcam;


    /*
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

        //Initialize Pivot Motor
        IntakeMotor = hardwareMap.get(DcMotorEx.class, "IntakeMotor");
        IntakeMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        IntakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        //Initialize Servos
        LeftLauncherHolderServo = hardwareMap.get(Servo.class, "LeftLauncherHolderServo");
        RightLauncherHolderServo = hardwareMap.get(Servo.class, "RightLauncherHolderServo");

        //todo Initalize Webcam
        //webcam = hardwareMap.get(OpenCvWebcam.class, "webcam");

    }


}
