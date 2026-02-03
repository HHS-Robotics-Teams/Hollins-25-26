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
        if(input.a.down()){
            lightUtil.makeGreen();
        }
        if(input.b.down()){
            lightUtil.makeRed();
        }
        if(input.x.down()){
            lightUtil.makeOff();
        }
        if(input.y.down()){
            lightUtil.makeAmber();
        }
    }
}
