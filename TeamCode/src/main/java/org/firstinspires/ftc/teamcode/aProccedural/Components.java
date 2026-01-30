package org.firstinspires.ftc.teamcode.aProccedural;



import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
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
    public static DcMotorEx LauncherMotor;
    public static DcMotor intakeMotor;
    public static DcMotor intakeSecondRollerMotor;
    public static Servo LauncherHandServo;
    public static Servo holderServo;
    public static CRServo leftFeedRoller;
    public static CRServo rightFeedRoller;



    /*
        Method to initialize components
        param hardwareMap is hardwareMap
     */
    public static void initComponents(HardwareMap hardwareMap){

        //Initialize Drive Motors
        leftFront = hardwareMap.get(DcMotor.class, "leftFront");
        rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        leftRear = hardwareMap.get(DcMotor.class, "leftRear");
        rightRear = hardwareMap.get(DcMotor.class, "rightRear");
        LauncherMotor = hardwareMap.get(DcMotorEx.class, "LauncherMotor");
        intakeMotor = hardwareMap.get(DcMotor.class, "intakeMotor");
        intakeSecondRollerMotor = hardwareMap.get(DcMotor.class, "intakeSecondRollerMotor");
        LauncherHandServo = hardwareMap.get(Servo.class, "LauncherHandServo");
        holderServo = hardwareMap.get(Servo.class, "holderServo");
        leftFeedRoller = hardwareMap.get(CRServo.class,"leftFeedRoller");
        rightFeedRoller = hardwareMap.get(CRServo.class,"rightFeedRoller");




        //Drive Motor Settings
        leftFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        leftRear.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightRear.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        leftFront.setDirection(DcMotorSimple.Direction.REVERSE);
        leftRear.setDirection(DcMotorSimple.Direction.REVERSE);
        LauncherMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        LauncherMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        //LauncherMotor.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(75, 7.5, 10, 20));
        LauncherMotor.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(130, 5, 50, 20));

        intakeMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        intakeMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        intakeSecondRollerMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        intakeSecondRollerMotor.setDirection(DcMotorSimple.Direction.FORWARD);

        leftFeedRoller.setDirection(DcMotorSimple.Direction.REVERSE);
        rightFeedRoller.setDirection(DcMotorSimple.Direction.FORWARD);

    }


}
