package org.firstinspires.ftc.teamcode.Util;


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

public class LauncherUtil {
    AprilTagMethod aprilTagMethod;
    public enum LaunchState {
        FIND_TAG,
        SPIN_UP_AND_MOVE,
        FINAL_CHECK,
        LAUNCH,
        RESET_SHOT,
        INTAKE,
        EXIT
    }
    private LaunchState launchState;
    double target;
    String color;

    ElapsedTime intakeTimer = new ElapsedTime(ElapsedTime.SECOND_IN_NANO);
    ElapsedTime launchTimer = new ElapsedTime(ElapsedTime.SECOND_IN_NANO);
    ElapsedTime resetTimer = new ElapsedTime(ElapsedTime.SECOND_IN_NANO);
    public LaunchState getLaunchState()    {return launchState;}

    public LauncherUtil(String color){
        aprilTagMethod = new AprilTagMethod();
        launchState = LaunchState.FIND_TAG;
        this.color = color;
    }
    public void cancelLaunch(){
        launchState = LaunchState.FIND_TAG;
        LauncherMotor.setPower(LAUNCHER_IDLE);
    }
    public String runLauncher() {
        if(!aprilTagMethod.isTagVisible() ){
            return "No Tag Visible";
        }
        switch (launchState){
            case EXIT:
                launchState = LaunchState.FIND_TAG;
                LauncherMotor.setPower(LAUNCHER_IDLE);
                LAUNCHER_RUN = false;
                break;
            case FIND_TAG:
                if(aprilTagMethod.isTagVisible() && aprilTagMethod.tagMatchesAlliance(color)){
                    launchState = LaunchState.SPIN_UP_AND_MOVE;
                }
                break;
            case SPIN_UP_AND_MOVE:
                if(moveToLaunch()&&isSpunUp()){
                    launchState = LaunchState.LAUNCH;
                    launchTimer.reset();
                }
                break;
            case LAUNCH:
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
                LeftSideFeedRoller.setPower(1);
                if(launchTimer.seconds() >= 0.8){
                    launchState = LaunchState.RESET_SHOT;
                    resetTimer.reset();
                }
                break;
            case RESET_SHOT:
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
                LeftSideFeedRoller.setPower(0);
                if(artifactCounterDistance.getDistance(DistanceUnit.INCH) >= 8){
                    launchState = LaunchState.EXIT;
                } else if (resetTimer.seconds() >= 0.25) {
                    intakeTimer.reset();
                    launchState = LaunchState.INTAKE;
                }
                break;
            case INTAKE:
                IntakeMotor.setPower(INTAKE_POWER);
                if(intakeTimer.seconds() >= .75){
                    launchState = LaunchState.FINAL_CHECK;
                }
                break;
            case FINAL_CHECK:
                IntakeMotor.setPower(0);
                if(moveToLaunch()&&isSpunUp()){
                    launchState = LaunchState.LAUNCH;
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
        LauncherMotor.setVelocity(target);
        return abs(LauncherMotor.getVelocity() - target) <= LAUNCH_TICK_VEL_THRESHOLD;
    }
}
