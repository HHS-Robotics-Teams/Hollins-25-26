package org.firstinspires.ftc.teamcode.Util.oldUtil;


import static org.firstinspires.ftc.teamcode._Proccedural.Components.IntakeMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherFingerServo;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LeftSideFeedRoller;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.artifactCounterDistance;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.leftBack;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.leftFront;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.rightBack;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.rightFront;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.INTAKE_POWER;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_FINGER_DOWN_POS;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_FINGER_UP_POS;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_IDLE;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_RUN;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCH_TICK_VELOCITY_FAR;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCH_TICK_VELOCITY_NEAR;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCH_TICK_VEL_THRESHOLD;
import static java.lang.Math.abs;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.OpModes.Auto.PathFactory;
import org.firstinspires.ftc.teamcode.Util.AprilTagMethod;

@Deprecated
public class LauncherUtilV2 {
    AprilTagMethod aprilTagMethod;
    public enum LaunchState_V2 {
        FIND_TAG,
        SPIN_UP_AND_MOVE,
        FINAL_CHECK,
        LAUNCH,
        RESET_SHOT,
        INTAKE,
        EXIT
    }
    private LaunchState_V2 launchState;

    public enum LAUNCH_LOCS_V2 {
        mid_field,
        far,
        far_across,
    }
    private LAUNCH_LOCS_V2 location;
    PathFactory factory;
    double target;
    String color;
    MecanumDrive drive;

    ElapsedTime intakeTimer = new ElapsedTime(ElapsedTime.SECOND_IN_NANO);
    ElapsedTime launchTimer = new ElapsedTime(ElapsedTime.SECOND_IN_NANO);
    ElapsedTime resetTimer = new ElapsedTime(ElapsedTime.SECOND_IN_NANO);
    public LaunchState_V2 getLaunchState()    {return launchState;}
    public LAUNCH_LOCS_V2 getLaunchLocation() {return location;}

    public LauncherUtilV2(AprilTagMethod aprilTagMethod, String color, MecanumDrive drive){
        this.aprilTagMethod = aprilTagMethod;
        launchState = LaunchState_V2.FIND_TAG;
        location = LAUNCH_LOCS_V2.mid_field;
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
                launchState = LaunchState_V2.FIND_TAG;
                LauncherMotor.setPower(LAUNCHER_IDLE);
                LAUNCHER_RUN = false;
                break;
            case FIND_TAG:
                if(aprilTagMethod.isTagVisible() && aprilTagMethod.tagMatchesAlliance(color)){
                    launchState = LaunchState_V2.SPIN_UP_AND_MOVE;
                    if(aprilTagMethod.getTagDistance() <= 60) {
                        location = LAUNCH_LOCS_V2.mid_field;
                    } else if(aprilTagMethod.getTagDistance() <= 80) {
                        location = LAUNCH_LOCS_V2.far;
                    } else {
                        location = LAUNCH_LOCS_V2.far_across;
                    }
                }
                break;
            case SPIN_UP_AND_MOVE:
                if(moveToLaunch()&&isSpunUp()){
                    launchState = LaunchState_V2.LAUNCH;
                    launchTimer.reset();
                }
                break;
            case LAUNCH:
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
                LeftSideFeedRoller.setPower(1);
                if(launchTimer.seconds() >= 0.8){
                    launchState = LaunchState_V2.RESET_SHOT;
                    resetTimer.reset();
                }
                break;
            case RESET_SHOT:
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
                LeftSideFeedRoller.setPower(0);
                if(artifactCounterDistance.getDistance(DistanceUnit.INCH) >= 8){
                    launchState = LaunchState_V2.EXIT;
                } else if (resetTimer.seconds() >= 0.25) {
                    intakeTimer.reset();
                    launchState = LaunchState_V2.INTAKE;
                }
                break;
            case INTAKE:
                IntakeMotor.setPower(INTAKE_POWER);
                if(intakeTimer.seconds() >= .75){
                    launchState = LaunchState_V2.FINAL_CHECK;
                }
                break;
            case FINAL_CHECK:
                IntakeMotor.setPower(0);
                if(moveToLaunch()&&isSpunUp()){
                    launchState = LaunchState_V2.LAUNCH;
                    launchTimer.reset();
                    intakeTimer.reset();
                }
                break;
        }
        return "Launch In Progress, current state: " + launchState
                + "\n" + "PHI: " + phi
                + "\n" + "Theta: " + theta;
    }

    double theta;
    double range;
    double phi;
    private boolean moveToLaunch() {
        theta = aprilTagMethod.getTagBearing();
        range = aprilTagMethod.getTagDistance()+2;
        double margin = 3;
        if(range >= 75){
            target = LAUNCH_TICK_VELOCITY_FAR + 125;
            phi = 3;
            if(color.equals("RED")){
                phi = 2.5;
            }
        } else {
            target = LAUNCH_TICK_VELOCITY_NEAR + 75;
            phi = 0;
            if(color.equals("RED")){
                phi = 1;
            }
            margin = 6;
        }
        if(theta >= phi + 2) {
            leftFront.setPower (-0.35);
            rightBack.setPower ( 0.35);
            leftBack.setPower  (-0.35);
            rightFront.setPower( 0.35);
        } else if (theta <= phi - 2) {
            leftFront.setPower ( 0.35);
            rightBack.setPower (-0.35);
            leftBack.setPower  ( 0.35);
            rightFront.setPower(-0.35);
        } else {
            leftFront.setPower (0);
            rightBack.setPower (0);
            leftBack.setPower  (0);
            rightFront.setPower(0);
            return true;
        }
        return abs(theta - phi) <= margin;
    }
    private boolean isSpunUp() {
        return abs(LauncherMotor.getVelocity() - target) <= LAUNCH_TICK_VEL_THRESHOLD;
    }
}
