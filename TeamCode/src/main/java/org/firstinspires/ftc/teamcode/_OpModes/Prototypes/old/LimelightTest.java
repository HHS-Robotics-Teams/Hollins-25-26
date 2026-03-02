package org.firstinspires.ftc.teamcode._OpModes.Prototypes.old;

import static org.firstinspires.ftc.teamcode._Proccedural.Components.limelight;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.firstinspires.ftc.teamcode._Proccedural.Components;
import org.firstinspires.ftc.teamcode._Proccedural.Input;

@TeleOp
@Disabled
@Deprecated
public class LimelightTest extends OpMode {

    Input input = new Input();

    @Override
    public void init() {
        Components.initComponents(hardwareMap);
    }

    @Override
    public void start() {
        limelight.start();
        limelight.pipelineSwitch(0);
    }

    @Override
    public void loop() {
        input.pollGamepad(gamepad1);
        LLResult result = limelight.getLatestResult();
        if(input.a_cross.down()){
            if(result.getPipelineIndex() == 1){
                limelight.pipelineSwitch(0);
            } else {
                limelight.pipelineSwitch(1);
            }
        }

        double tx = result.getTx(); // How far left or right the target is (degrees)
        double ty = result.getTy(); // How far up or down the target is (degrees)
        double ta = result.getTa(); // How big the target looks (0%-100% of the image)

        telemetry.addData("Target X", tx);
        telemetry.addData("Target Y", ty);
        telemetry.addData("Target Area", ta);

        Pose3D botpose_mt2 = result.getBotpose_MT2();
        if (botpose_mt2 != null) {
            double x = botpose_mt2.getPosition().x;
            double y = botpose_mt2.getPosition().y;
            telemetry.addData("MT2 Location:", "(" + x + ", " + y + ")");
        }

        double dist = result.getBotposeAvgDist();
        Position pos = result.getBotpose_MT2().getPosition();
        YawPitchRollAngles angles = result.getBotpose_MT2().getOrientation();
        double theta = angles.getYaw(AngleUnit.DEGREES);
        telemetry.addData("Distance", dist);
        telemetry.addData("Pos", pos);
        telemetry.addData("Angles", angles);
        telemetry.addData("Theta", theta);

    }
}
