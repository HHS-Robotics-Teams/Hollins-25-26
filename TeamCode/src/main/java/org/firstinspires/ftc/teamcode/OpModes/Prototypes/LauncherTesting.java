package org.firstinspires.ftc.teamcode.OpModes.Prototypes;

import static org.firstinspires.ftc.teamcode.aProccedural.Components.ParkingStopServo;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCHER_FINGER_DOWN_POS;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCHER_FINGER_UP_POS;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.aProccedural.Input;

//testing opmode disabled
@Disabled
@TeleOp
public class LauncherTesting extends OpMode {

    //left
    private DcMotorEx launcher_motor;
    //right
    private DcMotor intake_motor;
    public static CRServo LeftSideFeedRoller;
    public static CRServo RightSideFeedRoller;
    private static Servo LauncherFingerServo;
    //power
    private double power = 0.25;
    private double delta = 0.05;
    Input input = new Input();
    @Override
    public void init() {
        launcher_motor = hardwareMap.get(DcMotorEx.class, "LauncherMotor");
        launcher_motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        intake_motor = hardwareMap.get(DcMotor.class, "IntakeMotor");
        intake_motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        LeftSideFeedRoller = hardwareMap.get(CRServo.class, "LeftSideFeedRoller");
        RightSideFeedRoller = hardwareMap.get(CRServo.class, "RightSideFeedRoller");
        LauncherFingerServo = hardwareMap.get(Servo.class, "LauncherFingerServo");
        RightSideFeedRoller.setDirection(DcMotorSimple.Direction.REVERSE);
        ParkingStopServo = hardwareMap.get(Servo.class,"ParkingStopServo");

        telemetry.addLine("WARNING: UPON START LAUNCHER AND INTAKE WILL START SPINNING");
    }

    public void start() {
        launcher_motor.setPower(power);
        intake_motor.setPower(1);
        LeftSideFeedRoller.setPower(1);
        RightSideFeedRoller.setPower(1);
        ParkingStopServo.setPosition(0);
    }

    @Override
    public void loop() {
        if (gamepad1.start){
            ParkingStopServo.setPosition(0);
        } else {
            ParkingStopServo.setPosition(.5);
        }

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
            RightSideFeedRoller.setPower(1);
        } else {
            LeftSideFeedRoller.setPower(0);
            RightSideFeedRoller.setPower(0);
        }
        // Launcher Finger
        if (gamepad1.right_bumper){
            LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
        } else {
            LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
        }

        //change power
        if(input.x.down()){
            power += delta;
        }
        if(input.y.down()){
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

        if(input.dpad_up.down()){
            delta += 0.01;
        }
        if(input.dpad_down.down()){
            delta -= 0.01;
        }

        input.pollGamepad(gamepad1);

        telemetry.addData("Current power: ", power);
        telemetry.addData("Current speed: ", launcher_motor.getVelocity(AngleUnit.RADIANS));
        telemetry.addData("Current delta: ", delta);
        telemetry.addLine();
        telemetry.addLine("dpad up to increase delta\ndpad down to decrease delta\na to toggle on/off\nx to increase power by delta\ny to decrease power by delta\nb to reverse");
    }
}
