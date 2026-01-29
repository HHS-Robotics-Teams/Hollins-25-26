package org.firstinspires.ftc.teamcode.Util;

import static org.firstinspires.ftc.teamcode._Proccedural.Components.ConveyorMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.IntakeMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherSafetyServo;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LeftSideFeedRoller;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.leftArtifactCounterDistance;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.leftBack;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.leftFront;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.rearDistance;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.rightArtifactCounterDistance;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.rightBack;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.rightFront;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.INTAKE_POWER;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_IDLE;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_RUN;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCH_TICK_VELOCITY_NEAR;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCH_TICK_VEL_THRESHOLD;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.SAFTEY_FIRING;
import static java.lang.Math.abs;
import static java.lang.Math.pow;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Light;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

/**
 * Runs flywheel style launcher logic to make teleOp code more readable
 * Includes camera vision & automatic alignment
 */
public class LauncherUtilV2 {
    AprilTagMethod aprilTagMethod;
    public enum LaunchState {
        FIND_TAG,
        SPIN_UP_AND_MOVE,
        LAUNCH,
        EXIT,
        DISTANCE_CHECK
    }
    private LaunchState launchState;
    private double target;
    private final String color;
    private final boolean isAuto;
    private double theta;
    private double phi;
    private double launchTime;
    private final LightUtil lightUtil;
    ElapsedTime launchTimer = new ElapsedTime(ElapsedTime.SECOND_IN_NANO);

    /**
     * Sets target vel manually
     * basically never used
     * see also DcMotor.setVelocity
     * @param target the velocity (in ticks) to be set
     */
    public void setTarget(double target) {
        this.target = target;
    }

    /**
     * Constructor
     * @param color "BLUE" or "RED" for camera logic
     * @param isAuto true for auto, false for not
     */
    public LauncherUtilV2(String color, boolean isAuto, LightUtil lightUtil) {
        aprilTagMethod = new AprilTagMethod();
        launchState = LaunchState.FIND_TAG;
        this.color = color;
        this.isAuto = isAuto;
        this.lightUtil = lightUtil;
        launchTime = 0.5;
    }

    /**
     * Ends launch & resets state machine
     */
    public void cancelLaunch() {
        launchState = LaunchState.FIND_TAG;
        LauncherMotor.setPower(LAUNCHER_IDLE);
    }

    /**
     * @return current launch state
     */
    public LaunchState getLaunchState() {
        return launchState;
    }

    /**
     * Main method for launcher
     * Call every loop while launching
     * @return String of telemetry
     */
    public String runLauncher() {
        LauncherSafetyServo.setPosition(SAFTEY_FIRING);
        lightUtil.updateLights();
        if (!aprilTagMethod.isTagVisible() && !isAuto) {
            return "No Tag Visible" + "\nState:" + launchState;
        } else {
            isSpunUp();
        }
        switch (launchState) {
            case EXIT:
                IntakeMotor.setPower(0);
                ConveyorMotor.setPower(0);
                LeftSideFeedRoller.setPower(0);
                launchState = LaunchState.FIND_TAG;
                LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_NEAR);
                leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
                rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
                leftBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
                rightBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
                LAUNCHER_RUN = false;
                break;
            case FIND_TAG:
                if (!aprilTagMethod.isTagVisible() && !isAuto) {
                    return "No Tag Visible";
                } else if (isAuto || aprilTagMethod.tagMatchesAlliance(color)) {
                    launchState = LaunchState.DISTANCE_CHECK;
                    break;
                }
                break;
            case SPIN_UP_AND_MOVE:
                if (!aprilTagMethod.isTagVisible() && !isAuto) {
                    return "No Tag Visible";
                } else if ((moveToLaunch()) && isSpunUp()) {
                    launchState = LaunchState.LAUNCH;
                    launchTimer.reset();
                }
                break;
            case LAUNCH:
                setLightOff();
                IntakeMotor.setPower(INTAKE_POWER);
                ConveyorMotor.setPower(INTAKE_POWER);
                LeftSideFeedRoller.setPower(1);
                if (launchTimer.seconds() >= launchTime) { //todo check this time
                    launchState = LaunchState.DISTANCE_CHECK;
                }
                break;
            case DISTANCE_CHECK:
                setLightAmber();
                launchState = LaunchState.SPIN_UP_AND_MOVE;
                if(leftArtifactCounterDistance.getDistance(DistanceUnit.INCH) <= 6 || rightArtifactCounterDistance.getDistance(DistanceUnit.INCH) <= 6){
                    launchTime = 2.5;
                } else if (rearDistance.getDistance(DistanceUnit.INCH) <= 4){
                    launchTime = 0.75;
                } else {
                    launchState = LaunchState.EXIT;
                }
                break;

        }

        return "Launch In Progress, current state: " + launchState
                + "\n" + "PHI: " + phi
                + "\n" + "Theta: " + theta
                + "\n" + "Distance Measurements:\n(left) " + leftArtifactCounterDistance.getDistance(DistanceUnit.INCH)
                + "\n(right) " + rightArtifactCounterDistance.getDistance(DistanceUnit.INCH)
                + "\n(rear) " + rearDistance.getDistance(DistanceUnit.INCH)
                + "\nTargetVel: " + target
                + "\nCurrentVel: " + LauncherMotor.getVelocity();
    }

    /**
     * Alignment code & power calculations
     * Skips if isAuto is true
     * @return boolean used in runLauncher()
     * Other users: tune phi & margin to game & spot
     */
    private boolean moveToLaunch() {
        if(isAuto){
            return true;
        } else if (!aprilTagMethod.isTagVisible() ) {
            return false;
        } else {
            theta = aprilTagMethod.getTagBearing();
            double range = aprilTagMethod.getTagDistance();
            double margin;
            if (range >= 90) {
                phi = 2.75;
                margin = 1.5;
            } else {
                phi = 0;
                margin = 3;
            }
            double turnPower = 0.55;
            if (theta >= phi + (margin + 5)) {
                leftFront.setPower(-turnPower);
                rightBack.setPower(turnPower);
                leftBack.setPower(-turnPower);
                rightFront.setPower(turnPower);
            } else if (theta <= phi - (margin + 5)) {
                leftFront.setPower(turnPower);
                rightBack.setPower(-turnPower);
                leftBack.setPower(turnPower);
                rightFront.setPower(-turnPower);
            } else {
                leftFront.setPower(0);
                rightBack.setPower(0);
                leftBack.setPower(0);
                rightFront.setPower(0);
                leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                leftBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                rightBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            }
            target = 890.08429 * pow((1.00483), range);
            LauncherMotor.setVelocity(target);
            return abs(theta - phi) <= margin + 5;
        }
    }

    /**
     * Helper for runLauncher() (sets vel)
     * @return boolean true if velocity is within threshold
     */
    private boolean isSpunUp() {
        if(aprilTagMethod.isTagVisible()){
            double range = aprilTagMethod.getTagDistance();
            target = 890.08429 * pow((1.00483), range);
        }
        LauncherMotor.setVelocity(target);
        return abs(LauncherMotor.getVelocity() - target) <= LAUNCH_TICK_VEL_THRESHOLD;
    }

    public void setLightRed() {
        lightUtil.makeRed();
    }
    public void setLightAmber() {
        lightUtil.makeAmber();
    }
    public void setLightGreen() {
        lightUtil.makeGreen();
    }
    public void setLightOff() {
        lightUtil.makeOff();
    }
    public void updateLights() {lightUtil.updateLights();}
    public LightUtil.LightState getLightState() {
        return lightUtil.getLightState();
    }
}
