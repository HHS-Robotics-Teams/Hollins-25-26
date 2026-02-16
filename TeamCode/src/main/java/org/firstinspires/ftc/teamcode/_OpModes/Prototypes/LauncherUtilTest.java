package org.firstinspires.ftc.teamcode._OpModes.Prototypes;

import static org.firstinspires.ftc.teamcode._Proccedural.Components.centerDistance;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.rearDistance;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.frontDistance;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCH_TICK_VELOCITY_NEAR;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode._Util.OLD.LauncherUtilV2;
import org.firstinspires.ftc.teamcode._Util.LightUtil;
import org.firstinspires.ftc.teamcode._Proccedural.Components;

@Autonomous
@Disabled
@Deprecated
public class LauncherUtilTest extends OpMode {

    LauncherUtilV2 utilV2;
    @Override
    public void init() {
        Components.initComponents(hardwareMap);
        utilV2 = new LauncherUtilV2("BLUE",true, new LightUtil(2, hardwareMap));
        telemetry.addLine("INIT COMPLETE\nWARNING, ON START LAUNCHER WILL BEGIN FIRING");
    }

    @Override
    public void loop() {
        utilV2.setTarget(LAUNCH_TICK_VELOCITY_NEAR);
        telemetry.addLine(utilV2.runLauncher());
        telemetry.addData("STATE:", utilV2.getLaunchState());
        telemetry.addData("rear distance", rearDistance.getDistance(DistanceUnit.INCH));
        telemetry.addData("left arifact distance", centerDistance.getDistance(DistanceUnit.INCH));
        telemetry.addData("right arifact  distance", frontDistance.getDistance(DistanceUnit.INCH));
        telemetry.update();
    }
}
