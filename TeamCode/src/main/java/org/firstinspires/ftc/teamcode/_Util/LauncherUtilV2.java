package org.firstinspires.ftc.teamcode._Util;

import static org.firstinspires.ftc.teamcode._Proccedural.Components.ConveyorMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.IntakeMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherHoodServo;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherSafetyServo;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LeftSideFeedRoller;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.cameraTiltServo;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.leftArtifactCounterDistance;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.leftBack;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.leftFront;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.rearDistance;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.rearSideDistance;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.rightArtifactCounterDistance;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.rightBack;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.rightFront;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.rightSideFeedRoller;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.CAMERA_START_POS;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.INTAKE_POWER;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_RUN;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCH_TICK_VELOCITY_NEAR;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.SAFTEY_FIRING;
import static java.lang.Math.abs;
import static java.lang.Math.pow;

import com.qualcomm.robotcore.hardware.DcMotor;
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
    private int numLaunches;
    private final LightUtil lightUtil;
    private final HoodUtil hoodUtil;
    ElapsedTime launchTimer = new ElapsedTime(ElapsedTime.SECOND_IN_NANO);
    ElapsedTime timeout = new ElapsedTime(ElapsedTime.SECOND_IN_NANO);
    ElapsedTime emptyLaunchCheck = new ElapsedTime(ElapsedTime.SECOND_IN_NANO);

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
        hoodUtil = new HoodUtil();
        numLaunches = 0;
    }

    /**
     * Ends launch & resets state machine
     */
    public void cancelLaunch() {
        launchState = LaunchState.FIND_TAG;
        LauncherMotor.setVelocity(1000);
        IntakeMotor.setPower(0);
        ConveyorMotor.setPower(0);
        LeftSideFeedRoller.setPower(0);
        rightSideFeedRoller.setPower(0);
        numLaunches = 0;
        leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        leftBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        rightBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        LAUNCHER_RUN = false;
    }

    /**
     * @return current launch state
     */
    public LaunchState getLaunchState() {
        return launchState;
    }

    public void overwriteHoodState(HoodUtil.HoodState state){
        hoodUtil.overwriteHoodState(state);
    }
    public void updateHood(){
        hoodUtil.updateHoodState(aprilTagMethod);
    }

    /**
     * Main method for launcher
     * Call every loop while launching
     * @return String of telemetry
     */
    public String runLauncher() {
        setLightOff();
        hoodUtil.updateHoodState(aprilTagMethod);
        LauncherSafetyServo.setPosition(SAFTEY_FIRING);
        lightUtil.makeOff();
        lightUtil.updateLights();
        if (!aprilTagMethod.isTagVisible() && !isAuto) {
            if(timeout.seconds() > 0.5) {
                launchState = LaunchState.FIND_TAG;
                spinUp();
                IntakeMotor.setPower(0);
                ConveyorMotor.setPower(0);
                LeftSideFeedRoller.setPower(0);
                rightSideFeedRoller.setPower(0);
                LAUNCHER_RUN = false;
            }
            return "No Tag Visible" + "\nState:" + launchState +"\nTimeout:" + timeout.seconds();
        } else {
            hoodUtil.resetTimeout();
            spinUp();
            timeout.reset();
        }
        switch (launchState) {
            case EXIT:
                numLaunches = 0;
                IntakeMotor.setPower(0);
                ConveyorMotor.setPower(0);
                LeftSideFeedRoller.setPower(0);
                rightSideFeedRoller.setPower(0);
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
                    launchState = LaunchState.FIND_TAG;
                    return "No Tag Visible";
                } else if ((moveToLaunch()) && isSpunUp()) {
                    launchState = LaunchState.LAUNCH;
                    numLaunches++;
                    launchTimer.reset();
                    emptyLaunchCheck.reset();
                }
                break;
            case LAUNCH:
                setLightOff();
                spinUp();
                IntakeMotor.setPower(INTAKE_POWER);
                ConveyorMotor.setPower(INTAKE_POWER);
                LeftSideFeedRoller.setPower(1);
                rightSideFeedRoller.setPower(1);
                if (launchTimer.seconds() >= launchTime) { //todo check this time
                    launchState = LaunchState.DISTANCE_CHECK;
                }
                if (launchTimer.seconds() >= 0.15){
                    target *= 1.3;
                }
                if(leftArtifactCounterDistance.getDistance(DistanceUnit.INCH) >= 5.5 && rightArtifactCounterDistance.getDistance(DistanceUnit.INCH) >= 5.5 && rearDistance.getDistance(DistanceUnit.INCH) >= 10 && rearSideDistance.getDistance(DistanceUnit.INCH) >= 4 && launchTimer.seconds() >= 0.5){
                    if(emptyLaunchCheck.seconds() >= 0.3){
                        launchState = LaunchState.DISTANCE_CHECK;
                    }
                } else {
                    emptyLaunchCheck.reset();
                }
                break;
            case DISTANCE_CHECK:
                launchState = LaunchState.SPIN_UP_AND_MOVE;
                if(numLaunches > 0) {
                    launchState = LaunchState.EXIT;
                } else if(leftArtifactCounterDistance.getDistance(DistanceUnit.INCH) <= 5.5 || rightArtifactCounterDistance.getDistance(DistanceUnit.INCH) <= 5.5){
                    launchTime = 2.5;
                } else if (rearDistance.getDistance(DistanceUnit.INCH) <= 4 || rearSideDistance.getDistance(DistanceUnit.INCH) <= 4){
                    launchTime = 0.75;
                } else {
                    launchTime = 0.75;
                }
                break;

        }

        return "Launch In Progress, current state: " + launchState
                + "\n" + "PHI: " + phi
                + "\n" + "Theta: " + theta
                + "\n" + "Distance Measurements:\n(left) " + leftArtifactCounterDistance.getDistance(DistanceUnit.INCH)
                + "\n(right) " + rightArtifactCounterDistance.getDistance(DistanceUnit.INCH)
                + "\n(rear) " + rearDistance.getDistance(DistanceUnit.INCH)
                + "\n(rear side)" + rearSideDistance.getDistance(DistanceUnit.INCH)
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
            theta = aprilTagMethod.getTagBearing() - 4.75;
            double range = aprilTagMethod.getTagDistance();
            double margin;
            double turnPower;
            if (range >= 90) {
                phi = 2;
                margin = 2.75;
                if (color.equals("RED")) {
                    phi = 3.75;
                }
                turnPower = 0.3;
            } else {
                phi = 0;
                margin = 4.5;
                if (color.equals("RED")) {
                    phi = 1;
                }
                turnPower = 0.375;
            }
            if (theta >= phi + margin) {
                leftFront.setPower(-turnPower);
                rightBack.setPower(turnPower);
                leftBack.setPower(-turnPower);
                rightFront.setPower(turnPower);
            } else if (theta <= phi - margin) {
                leftFront.setPower(turnPower);
                rightBack.setPower(-turnPower);
                leftBack.setPower(turnPower);
                rightFront.setPower(-turnPower);
            } else {
                leftFront.setPower(0);
                rightBack.setPower(0);
                leftBack.setPower(0);
                rightFront.setPower(0);
                return true;
            }
            target = 1050 * pow((1.00383), range); //940.08429
            if(range >= 95){
                target += 115;//change this if far is high/low
            }
            if(launchState != LaunchState.SPIN_UP_AND_MOVE && launchState != LaunchState.FIND_TAG) {
                target += 20;
            }
            LauncherMotor.setVelocity(target);
            return abs(theta - phi) <= margin;
        }
    }

    /**
     * Helper for runLauncher() (sets vel)
     * @return boolean true if velocity is within threshold
     */
    private boolean isSpunUp() {
        spinUp();
        return abs(LauncherMotor.getVelocity() - target) <= 50;
    }

    /**
     * TeleOp always spin up when tag is visible
     * else if not visible idle is 1100
     */
    public void spinUp() {
        double range = 0;
        if(aprilTagMethod.isTagVisible() && aprilTagMethod.tagMatchesAlliance(color)){
            range = aprilTagMethod.getTagDistance();
            target = 940.08429 * pow((1.00383), range);
        } else if (isAuto) {
            target = 925;
        } else {
            target = 1100;
        }
        if(range >= 95){
            target += 30;//change this if far is high/low
        }
        if(launchState != LaunchState.SPIN_UP_AND_MOVE && launchState != LaunchState.FIND_TAG){
            target += 25;
            if(range >= 95){
                target += 125;//change this if far is high/low
            }
        }
        LauncherMotor.setVelocity(target);
    }

    public void resetLauncherUtilTimeout() {
        timeout.reset();
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
