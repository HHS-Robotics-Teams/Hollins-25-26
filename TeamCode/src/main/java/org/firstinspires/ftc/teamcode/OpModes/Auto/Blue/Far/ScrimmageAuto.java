package org.firstinspires.ftc.teamcode.OpModes.Auto.Blue.Far;

import static org.firstinspires.ftc.teamcode.LauncherPID.LauncherPID.getLaunchReadinessStatus;
import static org.firstinspires.ftc.teamcode.LauncherPID.LauncherPID.initLauncherPID;
import static org.firstinspires.ftc.teamcode.LauncherPID.LauncherPID.setLauncherTargetVelocity;
import static org.firstinspires.ftc.teamcode.LauncherPID.LauncherPID.updateLauncherPID;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.LauncherHolderServo;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCHER_FAR;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCHER_HOLDER_HOLDING_POSITION;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCHER_HOLDER_LAUNCH_POSITION;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.LauncherPID.LauncherPID;
import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.aProccedural.Components;

public class ScrimmageAuto extends OpMode {

    Pose2d startPose = new Pose2d(0,0,0);
    MecanumDrive mecanumDrive = new MecanumDrive(hardwareMap, startPose);

    enum AutoState {
        WaitForStart,
        Shoot,
        Move,
        Cooldown
    }
    AutoState state = AutoState.WaitForStart;

    TrajectoryActionBuilder trajectory;
    double launchTime;


    @Override
    public void init() {
        Components.initComponents(hardwareMap);

        trajectory = mecanumDrive.actionBuilder(startPose)
                .lineToYConstantHeading(10)
                .endTrajectory();
    }

    public void start() {
        initLauncherPID(getRuntime(), LAUNCHER_FAR);
        LauncherHolderServo.setPosition(LAUNCHER_HOLDER_HOLDING_POSITION);
        state = AutoState.Shoot;
    }

    @Override
    public void loop() {
        updateLauncherPID(getRuntime());
        switch(state) {
            case WaitForStart:
                state = AutoState.Shoot;
                break;
            case Shoot:
                if(getLaunchReadinessStatus()) {
                    LauncherHolderServo.setPosition(LAUNCHER_HOLDER_LAUNCH_POSITION);
                    launchTime = getRuntime();
                    if(getRuntime() - launchTime >= 1){
                        state = AutoState.Move;
                        setLauncherTargetVelocity(0);
                    }
                } else {
                    LauncherHolderServo.setPosition(LAUNCHER_HOLDER_HOLDING_POSITION);
                }
                break;
            case Move:
                Actions.runBlocking(new SequentialAction(
                        trajectory.build()
                ));
                break;
            case Cooldown:
                requestOpModeStop();
                break;
        }
    }
}
