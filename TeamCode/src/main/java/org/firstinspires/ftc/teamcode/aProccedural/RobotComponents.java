package org.firstinspires.ftc.teamcode.aProccedural;


import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;


import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;


public class RobotComponents {

    public static Servo pincer_left = null;

    public static DcMotor claw_tilt = null;
    public static DcMotor arm_tilt = null;
    public static DcMotor leftMotor;
    public static DcMotor rightMotor;
    public static ColorSensor colorSensor;
    public static WebcamName Webcam;



    public static void init(HardwareMap hardwareMap) {
        // Initialize motors from hardware map
        leftMotor = hardwareMap.dcMotor.get("left_motor");
        rightMotor = hardwareMap.dcMotor.get("right_motor");
        claw_tilt = hardwareMap.dcMotor.get("claw tilt");
        arm_tilt = hardwareMap.dcMotor.get("tilt");
        pincer_left = hardwareMap.servo.get("pincer left");

        colorSensor = hardwareMap.get(ColorSensor.class, "color_sensor");
        Webcam = hardwareMap.get(WebcamName.class, "Webcam");



        // Reverse the direction of the right motor
        rightMotor.setDirection(DcMotor.Direction.REVERSE);

        leftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        rightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        arm_tilt.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        claw_tilt.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
    }

}
