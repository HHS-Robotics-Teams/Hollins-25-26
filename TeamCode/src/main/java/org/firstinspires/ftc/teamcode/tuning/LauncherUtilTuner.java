package org.firstinspires.ftc.teamcode.tuning;

import static org.firstinspires.ftc.teamcode._Proccedural.Components.ConveyorMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.IntakeMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LeftSideFeedRoller;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.leftArtifactCounterDistance;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.rearDistance;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.rearSideDistance;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.rightArtifactCounterDistance;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCH_TICK_VELOCITY_NEAR;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode._Util.AprilTagMethod;
import org.firstinspires.ftc.teamcode._Proccedural.Components;
import org.firstinspires.ftc.teamcode._Proccedural.Input;

@TeleOp
public class LauncherUtilTuner extends OpMode {
    Input input = new Input();
    double target = LAUNCH_TICK_VELOCITY_NEAR;
    AprilTagMethod aprilTagMethod = new AprilTagMethod();
    @Override
    public void init() {
        Components.initComponents(hardwareMap);
    }

    @Override
    public void init_loop() {
        telemetry.addLine("--- Distance Sensor Readings ---");
        telemetry.addData("Front Left", leftArtifactCounterDistance.getDistance(DistanceUnit.INCH));
        telemetry.addData("Front Right", rightArtifactCounterDistance.getDistance(DistanceUnit.INCH));
        telemetry.addData("Rear 1", rearDistance.getDistance(DistanceUnit.INCH));
        telemetry.addData("Rear Side", rearSideDistance.getDistance(DistanceUnit.INCH));
    }

    @Override
    public void loop() {
        input.pollGamepad(gamepad1);
        LauncherMotor.setVelocity(target);
        telemetry.addData("target: ", target);
        if(input.a_cross.down()){
            target += 50;
        }
        if(input.b_circle.down()){
            target -= 50;
        }
        if(input.left_trigger.held()){
            IntakeMotor.setPower(1);
            ConveyorMotor.setPower(1);
            LeftSideFeedRoller.setPower(1);
        } else {
            IntakeMotor.setPower(0);
            ConveyorMotor.setPower(0);
            LeftSideFeedRoller.setPower(0);
        }
        if(aprilTagMethod.isTagVisible()){
            telemetry.addData("Tag Distance", aprilTagMethod.getTagDistance());
            telemetry.addData("phi", aprilTagMethod.getTagBearing());
        }
    }
}
