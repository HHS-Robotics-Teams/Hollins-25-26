package org.firstinspires.ftc.teamcode.HollinsMadeUtil;


import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_IDLE;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCH_THRESHOLD;
import static java.lang.Math.abs;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.ftc.Actions;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.MecanumDrive;

import java.util.Objects;

public class LauncherUtil {
    AprilTagMethod aprilTagMethod;
    private enum LaunchState {
        FIND_TAG,
        SPIN_UP_AND_MOVE,
        FINAL_CHECK,
        LAUNCH,
        RESET_SHOT,
        INTAKE,
        EXIT
    }
    LaunchState launchState;
    private enum LAUNCH_LOCS{
        nearest,
        near,
        mid_field,
        far,
        far_across,
        moving_block
    }
    LAUNCH_LOCS location;
    TrajectoryActionBuilder near;
    double target;
    String color;
    MecanumDrive drive;
    /** @noinspection ClassEscapesDefinedScope*/
    public LaunchState getLaunchState() {
        return launchState;
    }

    public LauncherUtil(AprilTagMethod aprilTagMethod, String color, MecanumDrive drive){
        this.aprilTagMethod = aprilTagMethod;
        launchState = LaunchState.FIND_TAG;
        location = LAUNCH_LOCS.nearest;
        this.color = color;
        this.drive = drive;
    }
    public String runLauncher() {
        if(!aprilTagMethod.isTagVisible() ){
            return "No Tag Visible";
        }
        switch (launchState){
            case EXIT:
                launchState = LaunchState.FIND_TAG;
                LauncherMotor.setPower(LAUNCHER_IDLE);
                break;
            case FIND_TAG:
                if(aprilTagMethod.isTagVisible() && aprilTagMethod.tagMatchesAlliance(color)){
                    launchState = LaunchState.SPIN_UP_AND_MOVE;
                    if(aprilTagMethod.getTagDistance() <= 20){
                        location = LAUNCH_LOCS.nearest;
                    } else if(aprilTagMethod.getTagDistance() <= 44) {
                        location = LAUNCH_LOCS.near;
                    } else if(aprilTagMethod.getTagDistance()<= 60) {
                        location = LAUNCH_LOCS.mid_field;
                    } else if(aprilTagMethod.getTagDistance()<= 80) {
                        location = LAUNCH_LOCS.far;
                    } else {
                        location = LAUNCH_LOCS.far_across;
                    }
                }
                break;
            case SPIN_UP_AND_MOVE:
                calculatePower();
                moveToLaunch();
                if(isAligned() && isSpunUp()){
                    launchState = LaunchState.FINAL_CHECK;
                }
                break;
        }
        LauncherMotor.setVelocity(target, AngleUnit.RADIANS);
        return "Launch In Progress";
    }
    private void calculatePower() {
        double distance = aprilTagMethod.getTagDistance();


        target = 2.4;
    }

    Action trajectory = null;
    Pose2d currentPos;
    private void moveToLaunch() {
        if(location != LAUNCH_LOCS.moving_block){
            currentPos = new Pose2d(0,0, aprilTagMethod.getTagYaw());
            if(Objects.equals(color, "BLUE")) {
                switch (location) {
                    case nearest:
                        near = drive.actionBuilder(currentPos)
                                .splineToSplineHeading(new Pose2d(-50, -40, Math.toRadians(-135)), Math.toRadians(-135));
                        trajectory = near.build();
                        location = LAUNCH_LOCS.moving_block;
                        break;
                    case near:
                        near = drive.actionBuilder(currentPos)
                                .splineToSplineHeading(new Pose2d(-34, -20, Math.toRadians(-135)), Math.toRadians(-135));
                        trajectory = near.build();
                        location = LAUNCH_LOCS.moving_block;
                        break;
                    case mid_field:
                        near = drive.actionBuilder(currentPos)
                                .splineToSplineHeading(new Pose2d(-16, -10, Math.toRadians(-135)), Math.toRadians(-135));
                        trajectory = near.build();
                        location = LAUNCH_LOCS.moving_block;
                        break;
                    case far:
                        near = drive.actionBuilder(currentPos)
                                .splineToLinearHeading(new Pose2d(50, -10, Math.toRadians(-140)), Math.toRadians(-105));
                        trajectory = near.build();
                        location = LAUNCH_LOCS.moving_block;
                        break;
                    case far_across:
                        near = drive.actionBuilder(currentPos)
                                .splineToLinearHeading(new Pose2d(50, -10, Math.toRadians(-150)), Math.toRadians(-105));
                        trajectory = near.build();
                        location = LAUNCH_LOCS.moving_block;
                        break;
                    case moving_block:
                        break;
                }
            } else {
                switch (location) {
                    case nearest:
                        near = drive.actionBuilder(currentPos)
                                .splineToLinearHeading(new Pose2d(-50,40,Math.toRadians(135)),Math.toRadians(105));
                        trajectory = near.build();
                        location = LAUNCH_LOCS.moving_block;
                        break;
                    case near:
                        near = drive.actionBuilder(currentPos)
                                .splineToLinearHeading(new Pose2d(-35,25,Math.toRadians(135)),Math.toRadians(105));
                        trajectory = near.build();
                        location = LAUNCH_LOCS.moving_block;
                        break;
                    case mid_field:
                        near = drive.actionBuilder(currentPos)
                                .splineToLinearHeading(new Pose2d(-15,5,Math.toRadians(135)),Math.toRadians(105));
                        trajectory = near.build();
                        location = LAUNCH_LOCS.moving_block;
                        break;
                    case far:
                        near = drive.actionBuilder(currentPos)
                                .splineToLinearHeading(new Pose2d(50,10,Math.toRadians(150)),Math.toRadians(105));
                        trajectory = near.build();
                        location = LAUNCH_LOCS.moving_block;
                        break;
                    case far_across:
                        near = drive.actionBuilder(currentPos)
                                .splineToLinearHeading(new Pose2d(50,-10,Math.toRadians(140)),Math.toRadians(105));
                        trajectory = near.build();
                        location = LAUNCH_LOCS.moving_block;
                        break;
                    case moving_block:
                        break;
                }
            }
        }
       Actions.runBlocking(new ParallelAction(trajectory));
    }
    private boolean isAligned() {
        return false;
    }
    private boolean isSpunUp() {
        return abs(LauncherMotor.getVelocity(AngleUnit.RADIANS) - target) <= LAUNCH_THRESHOLD;
    }
}
