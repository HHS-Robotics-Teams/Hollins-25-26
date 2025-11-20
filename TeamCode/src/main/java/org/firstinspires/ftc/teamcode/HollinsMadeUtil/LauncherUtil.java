package org.firstinspires.ftc.teamcode.HollinsMadeUtil;


import static org.firstinspires.ftc.teamcode._Proccedural.Components.IntakeMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherFingerServo;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LeftSideFeedRoller;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.artifactCounterDistance;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.INTAKE_POWER;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_FAR_TARGET;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_FINGER_DOWN_POS;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_FINGER_UP_POS;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_IDLE;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_NEAR_TARGET;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCH_THRESHOLD;
import static java.lang.Math.abs;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Quaternion;
import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.OpModes.Auto.PathFactory;
import org.firstinspires.ftc.teamcode.OpModes.Auto.Red.RedNearRoadrunnerThreePlusSix;

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
    PathFactory factory;
    double target;
    String color;
    MecanumDrive drive;
    ElapsedTime timer = new ElapsedTime(ElapsedTime.SECOND_IN_NANO);
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
                moveToLaunch();
                if(isSpunUp()&&poseFailsafe()){
                    launchState = LaunchState.FINAL_CHECK;
                }
                break;
            case FINAL_CHECK:
                if(abs(LauncherMotor.getVelocity(AngleUnit.RADIANS)-target) <= LAUNCH_THRESHOLD){
                    launchState = LaunchState.LAUNCH;
                    timer.reset();
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
                if(artifactCounterDistance.getDistance(DistanceUnit.INCH) >= 3){
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
        }
        LauncherMotor.setVelocity(target, AngleUnit.RADIANS);
        return "Launch In Progress\nCurrent Velocity:" + LauncherMotor.getVelocity(AngleUnit.RADIANS) + "\nTarget Velocity:" + target;
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
                    case nearest:
                        target = 1.8;
                        near = drive.actionBuilder(currentPos)
                                .splineToSplineHeading(new Pose2d(-50, -40, Math.toRadians(-135)), Math.toRadians(-135));
                        trajectory = near.build();
                        location = LAUNCH_LOCS.moving_block;
                        break;
                    case near:
                        target = 1.9;
                        near = drive.actionBuilder(currentPos)
                                .splineToSplineHeading(new Pose2d(-34, -20, Math.toRadians(-135)), Math.toRadians(-135));
                        trajectory = near.build();
                        location = LAUNCH_LOCS.moving_block;
                        break;
                    case mid_field:
                        target = LAUNCHER_NEAR_TARGET;
                        trajectory = factory.blueNearLaunchPath(currentPos);
                        location = LAUNCH_LOCS.moving_block;
                        break;
                    case far:
                        target = LAUNCHER_FAR_TARGET;
                        trajectory = factory.blueFarLaunchPath(currentPos);
                        location = LAUNCH_LOCS.moving_block;
                        break;
                    case far_across:
                        target = 2.6;
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
                        target = 1.8;
                        near = drive.actionBuilder(currentPos)
                                .splineToLinearHeading(new Pose2d(-50,40,Math.toRadians(135)),Math.toRadians(105));
                        trajectory = near.build();
                        location = LAUNCH_LOCS.moving_block;
                        break;
                    case near:
                        target = 1.9;
                        near = drive.actionBuilder(currentPos)
                                .splineToLinearHeading(new Pose2d(-35,25,Math.toRadians(135)),Math.toRadians(105));
                        trajectory = near.build();
                        location = LAUNCH_LOCS.moving_block;
                        break;
                    case mid_field:
                        target = LAUNCHER_NEAR_TARGET;
                        trajectory = factory.redNearLaunchPath(currentPos);
                        location = LAUNCH_LOCS.moving_block;
                        break;
                    case far:
                        target = LAUNCHER_FAR_TARGET;
                        trajectory = factory.redFarLaunchPath(currentPos);
                        location = LAUNCH_LOCS.moving_block;
                        break;
                    case far_across:
                        target = 2.6;
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
        LauncherMotor.setVelocity(target, AngleUnit.RADIANS);
        Actions.runBlocking(new ParallelAction(trajectory));
    }
    private boolean poseFailsafe(){
        if(aprilTagMethod.getTagX()<= -2 && color == "BLUE"){
            return false;
        } else if(aprilTagMethod.getTagX()>=  2 && color == "RED"){
            return false;
        }
        return true;
    }

    private boolean isSpunUp() {
        return abs(LauncherMotor.getVelocity(AngleUnit.RADIANS) - target) <= LAUNCH_THRESHOLD;
    }
}
