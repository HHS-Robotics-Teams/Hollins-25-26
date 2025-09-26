package org.firstinspires.ftc.teamcode.aProccedural;



import static org.firstinspires.ftc.teamcode.aProccedural.Constants.PIVOT_HORIZONTAL_POS;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * File to store all hardware code
 */
public class Components {

    //Instantiate Drive Motors
    public static DcMotor leftFront;
    public static DcMotor rightFront;
    public static DcMotor leftRear;
    public static DcMotor rightRear;

    //Instantiate Launcher Motors
    public static DcMotorEx LauncherMotor;

    //Instantiate Pivot Motors
    public static DcMotor IntakeMotor;


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

        //Drive Motor Settings
        leftFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        leftRear.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightRear.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        //Initialize Launcher
        LauncherMotor = hardwareMap.get(DcMotorEx.class, "leftLauncherMotor");

        //Launcher Settings
        LauncherMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        //Initialize Pivot Motor
        IntakeMotor = hardwareMap.get(DcMotorEx.class, "pivotMotor");
        IntakeMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        IntakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

    }


}
