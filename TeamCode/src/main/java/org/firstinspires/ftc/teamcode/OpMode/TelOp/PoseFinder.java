package org.firstinspires.ftc.teamcode.OpMode.TelOp;


import static org.firstinspires.ftc.teamcode.aProccedural.Constants.ClawInterval;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.TiltInterval;
import static org.firstinspires.ftc.teamcode.aProccedural.RobotComponents.arm_tilt;
import static org.firstinspires.ftc.teamcode.aProccedural.RobotComponents.claw_tilt;
import static org.firstinspires.ftc.teamcode.aProccedural.RobotComponents.pincer_left;


import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.aProccedural.RobotComponents;

@TeleOp (name = "PoseFinder",group = "Testing")
public class PoseFinder extends OpMode {
    @Override
    public void init() {
        RobotComponents.init(hardwareMap);
        arm_tilt.setTargetPosition(0);
        claw_tilt.setTargetPosition(0);

        arm_tilt.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        arm_tilt.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        arm_tilt.setPower(1);

        claw_tilt.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        claw_tilt.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        claw_tilt.setPower(1);



        pincer_left.setPosition(0);
    }

    @Override
    public void loop() {
        double servoPosition = pincer_left.getPosition();
        int armPos = arm_tilt.getCurrentPosition();
        int clawPos = claw_tilt.getCurrentPosition();

        telemetry.addData("arm tilt position", arm_tilt.getCurrentPosition());
        telemetry.addData("claw tilt position", claw_tilt.getCurrentPosition());
        telemetry.addData("Pincer position", servoPosition);
        telemetry.update();

        if (gamepad1.right_trigger > .5) {
            servoPosition += 0.1;
            pincer_left.setPosition(servoPosition);
        } else if (gamepad1.right_bumper) {
            servoPosition -= 0.1;
            pincer_left.setPosition(servoPosition);
        }
        if (gamepad1.left_trigger > .5) {
            clawPos += ClawInterval ;
            claw_tilt.setTargetPosition(clawPos);
        } else if (gamepad1.left_bumper) {
            clawPos -= ClawInterval;
            claw_tilt.setTargetPosition(clawPos);
        }
        if (gamepad1.dpad_up) {
            armPos += TiltInterval;
            arm_tilt.setTargetPosition(armPos);
        } else if (gamepad1.dpad_down) {
            armPos -= TiltInterval;
            arm_tilt.setTargetPosition(armPos);
        }
    }
}
