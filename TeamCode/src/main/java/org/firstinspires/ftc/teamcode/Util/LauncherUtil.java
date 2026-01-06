package org.firstinspires.ftc.teamcode.Util;

import static org.firstinspires.ftc.teamcode._Proccedural.Components.IntakeMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherFingerServo;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherHoodServo;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LeftSideFeedRoller;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.cameraTiltServo;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.leftArtifactCounterDistance;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.leftBack;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.leftFront;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.rearDistance;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.rightArtifactCounterDistance;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.rightBack;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.rightFront;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.INTAKE_POWER;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_FINGER_DOWN_POS;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_FINGER_UP_POS;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_HOOD_DOWN_POS;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_HOOD_UP_POS;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_IDLE;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_RUN;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCH_TICK_VELOCITY_FAR;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCH_TICK_VELOCITY_NEAR;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCH_TICK_VEL_THRESHOLD;
import static java.lang.Math.abs;

import com.qualcomm.robotcore.hardware.DcMotor;
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
        EXIT,
        DISTANCE_CHECK
    }
    private LaunchState launchState;
    double target;
    String color;
    boolean isAuto;

    ElapsedTime intakeTimer = new ElapsedTime(ElapsedTime.SECOND_IN_NANO);
    ElapsedTime launchTimer = new ElapsedTime(ElapsedTime.SECOND_IN_NANO);
    ElapsedTime resetTimer = new ElapsedTime(ElapsedTime.SECOND_IN_NANO);

    public void setTarget(double target) {
        this.target = target;
    }

    public LauncherUtil(String color, boolean isAuto) {
        aprilTagMethod = new AprilTagMethod();
        launchState = LaunchState.FIND_TAG;
        this.color = color;
        this.isAuto = isAuto;
        alignTimer.reset();
    }

    public void cancelLaunch() {
        launchState = LaunchState.FIND_TAG;
        LauncherMotor.setPower(LAUNCHER_IDLE);
    }
    public LaunchState getLaunchState() {
        return launchState;
    }
    public String runLauncher() {
        if (!aprilTagMethod.isTagVisible() ) {
            if(cameraTiltServo.getPosition() <= 0.3){
                cameraTiltServo.setPosition(cameraTiltServo.getPosition() + 0.05);
            } else {
                cameraTiltServo.setPosition(cameraTiltServo.getPosition() - 0.05);
            }
            return "No Tag Visible";
        }
        switch (launchState) {
            case EXIT:
                if (!aprilTagMethod.isTagVisible() ) {
                    return "No Tag Visible";
                }
                launchState = LaunchState.FIND_TAG;
                LauncherMotor.setPower(LAUNCHER_IDLE);
                LAUNCHER_RUN = false;
                break;
            case FIND_TAG:
                boolean a = alignHood();
                if (!aprilTagMethod.isTagVisible() ) {
                    return "No Tag Visible\nHood Alignment Status: " + a;
                }
                if (aprilTagMethod.isTagVisible() && aprilTagMethod.tagMatchesAlliance(color)) {
                    launchState = LaunchState.SPIN_UP_AND_MOVE;
                }
                break;
            case SPIN_UP_AND_MOVE:
                if (!aprilTagMethod.isTagVisible() ) {
                    return "No Tag Visible";
                }
                if ((moveToLaunch()) && isSpunUp()) {
                    launchState = LaunchState.LAUNCH;
                    leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
                    rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
                    leftBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
                    rightBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
                    launchTimer.reset();
                }
                break;
            case LAUNCH:
                if (!aprilTagMethod.isTagVisible() ) {
                    return "No Tag Visible";
                }
                LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
                LeftSideFeedRoller.setPower(1);
                if (launchTimer.seconds() >= 0.4) {
                    launchState = LaunchState.RESET_SHOT;
                    LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
                    resetTimer.reset();
                }
                break;
            case RESET_SHOT:
                if (!aprilTagMethod.isTagVisible() ) {
                    return "No Tag Visible";
                }
                LeftSideFeedRoller.setPower(0);
                resetTimer.reset();
                launchState = LaunchState.DISTANCE_CHECK;
                break;
            case INTAKE:
                if (!aprilTagMethod.isTagVisible() ) {
                    return "No Tag Visible";
                }
                IntakeMotor.setPower(INTAKE_POWER);
                if (intakeTimer.seconds() >= 0.4) {
                    launchState = LaunchState.FINAL_CHECK;
                }
                break;
            case FINAL_CHECK:
                if (!aprilTagMethod.isTagVisible() ) {
                    if(isAuto){
                        launchState = LaunchState.LAUNCH;
                        launchTimer.reset();
                        intakeTimer.reset();
                    }
                    return "No Tag Visible";
                }
                IntakeMotor.setPower(0);
                if (moveToLaunch()&&isSpunUp()) {
                    launchState = LaunchState.LAUNCH;
                    launchTimer.reset();
                    intakeTimer.reset();
                }
                break;
            case DISTANCE_CHECK:
                if (!aprilTagMethod.isTagVisible() ) {
                    return "No Tag Visible";
                }
                if (resetTimer.seconds() >= 0.4) {
                    if (rearDistance.getDistance(DistanceUnit.INCH) <= 2) {
                        launchState = LaunchState.EXIT;
                    }
                    else if (rearDistance.getDistance(DistanceUnit.INCH) <= 6) {
                        launchState = LaunchState.FINAL_CHECK;
                    } else if (rearDistance.getDistance(DistanceUnit.INCH) <= 11) {
                        launchState = LaunchState.INTAKE;
                        intakeTimer.reset();
                    } else {
                        launchState = LaunchState.EXIT;
                    }
                break;
                }
            return "Launch In Progress, current state: " + launchState
                    + "\n" + "PHI: " + phi
                    + "\n" + "Theta: " + theta
                    + "\n" + "Distance Measurements: " + leftArtifactCounterDistance.getDistance(DistanceUnit.INCH) + ", " + rightArtifactCounterDistance.getDistance(DistanceUnit.INCH) + ", " + rearDistance.getDistance(DistanceUnit.INCH);
        }
        return "Launch In Progress, current state: " + launchState
                + "\n" + "PHI: " + phi
                + "\n" + "Theta: " + theta;
    }
    double theta;
    double range;
    double phi;
    private boolean moveToLaunch() {
        if(isAuto){
            return true;
        }
        if (!aprilTagMethod.isTagVisible() ) {
            return false;
        }
        theta = aprilTagMethod.getTagBearing();
        range = aprilTagMethod.getTagDistance()+2;
        double margin = 3;
        if (range >= 90) {
            target = LAUNCH_TICK_VELOCITY_FAR + 125;
            phi = 3;
            if (color.equals("RED")) {
                phi = 2.5;
            }
        } else if (range < 90 && range > 75){
            target = LAUNCH_TICK_VELOCITY_NEAR + 150;
            phi = 0;
            if (color.equals("RED")) {
                phi = 1;
            }
            margin = 6;
        } else if (range < 60 && range > 40){
            target = LAUNCH_TICK_VELOCITY_NEAR;
            phi = 0;
            if (color.equals("RED")) {
                phi = 1;
            }
            margin = 6;
        }
        else {
            target = LAUNCH_TICK_VELOCITY_NEAR + 100;
            phi = 0;
            if (color.equals("RED")) {
              phi = 1;
            }
            margin = 6;
        }
        if (theta >= phi + 2) {
            leftFront.setPower (-turnPower);
            rightBack.setPower ( turnPower);
            leftBack.setPower  (-turnPower);
            rightFront.setPower( turnPower);
        } else if (theta <= phi - 2) {
            leftFront.setPower ( turnPower);
            rightBack.setPower (-turnPower);
            leftBack.setPower  ( turnPower);
            rightFront.setPower(-turnPower);
        } else {
            leftFront.setPower (0);
            rightBack.setPower (0);
            leftBack.setPower  (0);
            rightFront.setPower(0);
            leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            leftBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            rightBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            return true;
        }
        return abs(theta - phi) <= margin;
    }
    double turnPower = 0.25;
    private boolean isSpunUp() {
        LauncherMotor.setVelocity(target);
        if(!alignHood()) {
            return false;
        }

        return abs(LauncherMotor.getVelocity() - target) <= LAUNCH_TICK_VEL_THRESHOLD;
    }
    double hoodTarget = LAUNCHER_HOOD_UP_POS;
    double lastTarget = hoodTarget;
    double maxRange = 100; //temp
    double hoodTicksPerInchRange = (LAUNCHER_HOOD_UP_POS - LAUNCHER_HOOD_DOWN_POS) / maxRange; //temp
    //supposed to return false if not aligned
    //run servo to pos
    //and then return true
    ElapsedTime alignTimer = new ElapsedTime(ElapsedTime.SECOND_IN_NANO);
    private boolean alignHood() {
        LauncherHoodServo.setPosition(0.2);
        return true;
    }
}
