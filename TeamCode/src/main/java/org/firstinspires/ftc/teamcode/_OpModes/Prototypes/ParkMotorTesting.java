package org.firstinspires.ftc.teamcode._OpModes.Prototypes;


import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode._Proccedural.Input;

//testing opmode disabled

@TeleOp
@Deprecated
@Disabled
public class ParkMotorTesting extends OpMode {


    //Instantiate Drive Motors
    public static DcMotor Parking_Motor;

    Input input = new Input();
    @Override
    public void init() {
        Parking_Motor = hardwareMap.get(DcMotorEx.class, "ParkingMotor");
        Parking_Motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
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


        if(input.x_square.down()){
            Parking_Motor.setTargetPosition(Parking_Motor.getCurrentPosition() + 20 );
        }
        if(input.y_triangle.down()){
            Parking_Motor.setTargetPosition(Parking_Motor.getCurrentPosition() - 20 );
        }

        input.pollGamepad(gamepad1);


        telemetry.addData("Parking Motor position: ", Parking_Motor.getCurrentPosition() );
        telemetry.update();

    }
}
