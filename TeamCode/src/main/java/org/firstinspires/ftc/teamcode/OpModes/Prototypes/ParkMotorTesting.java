package org.firstinspires.ftc.teamcode.OpModes.Prototypes;


import static org.firstinspires.ftc.teamcode._Proccedural.Constants.DriveSlowdown;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_FINGER_DOWN_POS;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_FINGER_UP_POS;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_HOOD_DOWN_POS;
import static java.lang.Math.abs;
import static java.lang.Math.max;

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
//@Disabled
//@Deprecated
public class ParkMotorTesting extends OpMode {


    //Instantiate Drive Motors
    public static DcMotor Parking_Motor;

    Input input = new Input();
    @Override
    public void init() {
        Parking_Motor = hardwareMap.get(DcMotorEx.class, "ParkingMotor");
        Parking_Motor.setTargetPosition(0);
        Parking_Motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        telemetry.addLine("WARNING: UPON START LAUNCHER AND INTAKE WILL START SPINNING");
    }

    public void start() {
        Parking_Motor.setTargetPosition(0);
        Parking_Motor.setPower(1);

    }

    @Override
    public void loop() {


        if(input.x.down()){
            Parking_Motor.setTargetPosition(Parking_Motor.getCurrentPosition() + 20 );
        }
        if(input.y.down()){
            Parking_Motor.setTargetPosition(Parking_Motor.getCurrentPosition() - 20 );
        }

        input.pollGamepad(gamepad1);


        telemetry.addData("Parking Motor position: ", Parking_Motor.getCurrentPosition() );
        telemetry.update();

    }
}
