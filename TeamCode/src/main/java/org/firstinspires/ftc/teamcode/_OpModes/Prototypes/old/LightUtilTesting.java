package org.firstinspires.ftc.teamcode._OpModes.Prototypes.old;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode._Util.LightUtil;
import org.firstinspires.ftc.teamcode._Proccedural.Input;

@TeleOp
@Deprecated
@Disabled
public class LightUtilTesting extends OpMode {

    Input input = new Input();
    LightUtil lightUtil;
    @Override
    public void init() {
        lightUtil = new LightUtil(3, hardwareMap);
    }

    @Override
    public void loop() {
        telemetry.addData("Num", lightUtil.getNumLights());
        telemetry.addData("State", lightUtil.getLightState());
        input.pollGamepad(gamepad1);
        lightUtil.updateLights();
        if(input.a_cross.down()){
            lightUtil.makeGreen();
        }
        if(input.b_circle.down()){
            lightUtil.makeRed();
        }
        if(input.x_square.down()){
            lightUtil.makeOff();
        }
        if(input.y_triangle.down()){
            lightUtil.makeAmber();
        }
    }
}
