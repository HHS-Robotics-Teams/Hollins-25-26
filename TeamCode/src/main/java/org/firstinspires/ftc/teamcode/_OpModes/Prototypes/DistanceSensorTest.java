package org.firstinspires.ftc.teamcode._OpModes.Prototypes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode._Proccedural.Components;
import org.firstinspires.ftc.teamcode._Util.AutoUtil;

@TeleOp
public class DistanceSensorTest extends OpMode {
    AutoUtil autoUtil = new AutoUtil(5);
    @Override
    public void init() {
        Components.initComponents(hardwareMap);
    }

    @Override
    public void loop() {
        telemetry.addLine(autoUtil.currentReadings());
    }
}
