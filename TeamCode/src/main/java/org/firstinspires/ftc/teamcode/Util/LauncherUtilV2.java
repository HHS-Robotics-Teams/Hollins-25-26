package org.firstinspires.ftc.teamcode.Util;


import static org.firstinspires.ftc.teamcode._Proccedural.Components.IntakeMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherFingerServo;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LeftSideFeedRoller;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.artifactCounterDistance;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.INTAKE_POWER;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_FINGER_DOWN_POS;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_FINGER_UP_POS;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_IDLE;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCH_TICK_VELOCITY_FAR;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCH_TICK_VELOCITY_NEAR;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCH_TICK_VEL_THRESHOLD;
import static java.lang.Math.abs;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.OpModes.Auto.PathFactory;

import java.util.Objects;

public class LauncherUtilV2 {
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
    private LaunchState launchState;

    private enum LAUNCH_LOCS{
        nearest,
        near,
        mid_field,
        far,
        far_across,
        moving_block
    }
    private LAUNCH_LOCS location;
    TrajectoryActionBuilder near;
    PathFactory factory;
    double target;
    String color;
    MecanumDrive drive;

    ElapsedTime timer = new ElapsedTime(ElapsedTime.SECOND_IN_NANO);
    /** @noinspection ClassEscapesDefinedScope*/
    public LaunchState getLaunchState()    {return launchState;}
    /** @noinspection ClassEscapesDefinedScope*/
    public LAUNCH_LOCS getLaunchLocation() {return location;}

    public LauncherUtilV2(AprilTagMethod aprilTagMethod, String color, MecanumDrive drive){
        this.aprilTagMethod = aprilTagMethod;
        launchState = LaunchState.FIND_TAG;
        location = LAUNCH_LOCS.nearest;
        this.color = color;
        this.drive = drive;
        factory = new PathFactory(drive);
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
                     if(aprilTagMethod.getTagDistance() <= 60) {
                        location = LAUNCH_LOCS.mid_field;
                    } else if(aprilTagMethod.getTagDistance() <= 80) {
                        location = LAUNCH_LOCS.far;
                    } else {
                        location = LAUNCH_LOCS.far_across;
                    }
                }
                break;
            case SPIN_UP_AND_MOVE:
                moveToLaunch();
                if(isSpunUp()){
                    launchState = LaunchState.LAUNCH;
                }
                break;
            case LAUNCH:
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
                LeftSideFeedRoller.setPower(1);
                if(timer.seconds() >= 0.8){
                    launchState = LaunchState.RESET_SHOT;
                    timer.reset();
                }
                break;
            case RESET_SHOT:
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
                LeftSideFeedRoller.setPower(0);
                if(artifactCounterDistance.getDistance(DistanceUnit.INCH) >= 8){
                    launchState = LaunchState.EXIT;
                } else {
                    timer.reset();
                    launchState = LaunchState.INTAKE;
                }
                break;
            case INTAKE:
                IntakeMotor.setPower(INTAKE_POWER);
                if(timer.seconds() >= .75){
                    launchState = LaunchState.FINAL_CHECK;
                }
                break;
            case FINAL_CHECK:
                IntakeMotor.setPower(0);
                if(isSpunUp()){
                    launchState = LaunchState.LAUNCH;
                }
                break;
        }
        return "Launch In Progress";
    }

    Action trajectory = null;
    Pose2d currentPos;
    private void moveToLaunch() {
        if(location != LAUNCH_LOCS.moving_block){
            double theta = Math.toRadians(90 - (abs(aprilTagMethod.getTagBearing()) + abs(aprilTagMethod.getTagYaw())));
            double xOffset = (aprilTagMethod.getTagDistance() * Math.sin(theta)) + aprilTagMethod.getTagXPos();
            double yOffset = (aprilTagMethod.getTagDistance() * Math.cos(theta)) + aprilTagMethod.getTagYPos();
            double heading = 0;
            if(color.equals("BLUE")){
                heading = Math.toRadians(-90 - theta);
            }
            if(color.equals("RED")){
                heading = Math.toRadians( 90 + theta);
                yOffset = -yOffset;
            }
            currentPos = new Pose2d(xOffset,yOffset,heading);
            if(Objects.equals(color, "BLUE")) {
                switch (location) {
                    case mid_field:
                        LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_NEAR);
                        target = LAUNCH_TICK_VELOCITY_NEAR;
                        trajectory = factory.blueNearLaunchPath(currentPos);
                        location = LAUNCH_LOCS.moving_block;
                        break;
                    case far:
                        LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_FAR);
                        target = LAUNCH_TICK_VELOCITY_FAR;
                        trajectory = factory.blueFarLaunchPath(currentPos);
                        location = LAUNCH_LOCS.moving_block;
                        break;
                    case far_across:
                        LauncherMotor.setVelocity(1100);
                        target = 1100;
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
                    case mid_field:
                        LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_NEAR);
                        target = LAUNCH_TICK_VELOCITY_NEAR;
                        trajectory = factory.redNearLaunchPath(currentPos);
                        location = LAUNCH_LOCS.moving_block;
                        break;
                    case far:
                        LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_FAR);
                        target = LAUNCH_TICK_VELOCITY_FAR;
                        trajectory = factory.redFarLaunchPath(currentPos);
                        location = LAUNCH_LOCS.moving_block;
                        break;
                    case far_across:
                        LauncherMotor.setVelocity(1100);
                        target = 1100;
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
       Actions.runBlocking(new ParallelAction(trajectory) );
    }
    private boolean isSpunUp() {
        return abs(LauncherMotor.getVelocity() - target) <= LAUNCH_TICK_VEL_THRESHOLD;
    }
}
