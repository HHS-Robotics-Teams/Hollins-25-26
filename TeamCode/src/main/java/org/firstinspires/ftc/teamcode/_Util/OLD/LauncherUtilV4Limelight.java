package org.firstinspires.ftc.teamcode._Util.OLD;

import static org.firstinspires.ftc.teamcode._Proccedural.Components.ConveyorMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.IntakeMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherSafetyServo;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LeftSideFeedRoller;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.frontFeedRoller;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.leftBack;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.leftFront;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.limelight;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.rightBack;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.rightFront;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.rightSideFeedRoller;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.INTAKE_POWER;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_RUN;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCH_TICK_VELOCITY_NEAR;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.SAFTEY_FIRING;
import static org.firstinspires.ftc.teamcode._Util.OLD.LauncherUtilV4Limelight.LaunchState.*;
import static org.firstinspires.ftc.teamcode._Util.OLD.thanksJedison.llAngle;
import static org.firstinspires.ftc.teamcode._Util.OLD.thanksJedison.llDistance;
import static java.lang.Math.abs;
import static java.lang.Math.pow;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode._Util.AutoUtil;
import org.firstinspires.ftc.teamcode._Util.LightUtil;

/**
 * Runs flywheel style launcher logic to make teleOp code more readable
 * Includes camera vision & automatic alignment
 */
@Deprecated
public class LauncherUtilV4Limelight {
    public enum LaunchState {
        FIND_TAG,
        SPIN_UP_AND_MOVE,
        LAUNCH,
        EXIT,
        DISTANCE_CHECK
    }
    private LaunchState launchState;
    private AutoUtil autoUtil = new AutoUtil(0.25);
    private double target;
    private final String color;
    private final boolean isAuto;
    private double theta;
    private double phi;
    private final LightUtil lightUtil;
    private final HoodUtilV2 hoodUtil;
    private volatile LLResult result;
    private thanksJedison jedison;
    ElapsedTime launchTimer = new ElapsedTime(ElapsedTime.SECOND_IN_NANO);
    ElapsedTime timeout = new ElapsedTime(ElapsedTime.SECOND_IN_NANO);

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
    public LauncherUtilV4Limelight(String color, boolean isAuto, LightUtil lightUtil, HardwareMap hw) {
        if (color.equals("BLUE")) {
            limelight.pipelineSwitch(0);
        } else if (color.equals("RED")) {
            limelight.pipelineSwitch(1);
        }
        launchState = FIND_TAG;
        this.color = color;
        this.isAuto = isAuto;
        this.lightUtil = lightUtil;
        hoodUtil = new HoodUtilV2();
        jedison = new thanksJedison(hw);
    }

    /**
     * Ends launch & resets state machine
     */
    public void cancelLaunch() {
        LauncherMotor.setVelocity(1000);
        IntakeMotor.setPower(0);
        ConveyorMotor.setPower(0);
        LeftSideFeedRoller.setPower(0);
        rightSideFeedRoller.setPower(0);
        frontFeedRoller.setPower(0);
        LAUNCHER_RUN = false;
    }

    /**
     * @return current launch state
     */
    public LaunchState getLaunchState() {
        return launchState;
    }

    public void overwriteHoodState(HoodUtilV2.HoodState state){
        hoodUtil.overwriteHoodState(state);
    }
    public void updateHood(){
        hoodUtil.updateHoodState(result);
    }

    /**
     * Main method for launcher
     * Call every loop while launching
     * @return String of telemetry
     */
    public String runLauncher() {
        if (limelight.getLatestResult() != null && limelight.getLatestResult().isValid()) {
            result = limelight.getLatestResult();
        }
        jedison.update();
        setLightOff();
        hoodUtil.updateHoodState(result);
        LauncherSafetyServo.setPosition(SAFTEY_FIRING);
        lightUtil.makeOff();
        lightUtil.updateLights();
        if ((result == null) || (result.isValid() && !isAuto)) {
            if(timeout.seconds() > 0.25) {
                launchState = FIND_TAG;
                spinUp();
                IntakeMotor.setPower(0);
                ConveyorMotor.setPower(0);
                LeftSideFeedRoller.setPower(0);
                rightSideFeedRoller.setPower(0);
                frontFeedRoller.setPower(0);
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
                IntakeMotor.setPower(0);
                ConveyorMotor.setPower(0);
                LeftSideFeedRoller.setPower(0);
                rightSideFeedRoller.setPower(0);
                frontFeedRoller.setPower(0);
                LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_NEAR);
                launchState = FIND_TAG;
                LAUNCHER_RUN = false;
                break;
            case FIND_TAG:
                if (result == null || !result.isValid() || !isAuto) {
                    launchState = FIND_TAG;
                    return "No Tag Visible";
                } else if (result.isValid()) {
                    if ((moveToLaunch()) && isSpunUp()) {
                        launchState = LAUNCH;
                        launchTimer.reset();
                        autoUtil.resetEmptyTimer();
                    }
                }
                break;
            case LAUNCH:
                setLightOff();
                IntakeMotor.setPower(INTAKE_POWER);
                ConveyorMotor.setPower(INTAKE_POWER);
                LeftSideFeedRoller.setPower(1);
                rightSideFeedRoller.setPower(1);
                frontFeedRoller.setPower(1);
                if(target > 1500) {
                    IntakeMotor.setPower(0.6);
                    ConveyorMotor.setPower(0.6);
                    LeftSideFeedRoller.setPower(0.6);
                    rightSideFeedRoller.setPower(0.6);
                    frontFeedRoller.setPower(0.6);
                }
                if (launchTimer.seconds() >= 2.5 || autoUtil.isBotEmpty()) { //todo check this time
                    launchState = EXIT;
                }
                break;

        }

        return "Launch In Progress, current state: " + launchState
                + "\n" + "PHI: " + phi
                + "\n" + "Theta: " + theta
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
        if (isAuto) {
            return true;
        } else {
            double theta = llAngle;
            double range = llDistance;
            double margin;
            double turnPower;
            if (range >= 90) {
                phi = 2;
                margin = 2;
                if (color.equals("RED")) {
                    phi = 3.75;
                }
                turnPower = 0.2;
            } else {
                phi = 0;
                margin = 3;
                if (color.equals("RED")) {
                    phi = 1;
                }
                turnPower = 0.15;
            }
            if (theta >= phi + margin) {
                leftFront.setPower(-turnPower * abs(theta - phi));
                rightBack.setPower(turnPower * abs(theta - phi));
                leftBack.setPower(-turnPower * abs(theta - phi));
                rightFront.setPower(turnPower * abs(theta - phi));
            } else if (theta <= phi - margin) {
                leftFront.setPower(turnPower * abs(theta - phi));
                rightBack.setPower(-turnPower * abs(theta - phi));
                leftBack.setPower(turnPower * abs(theta - phi));
                rightFront.setPower(-turnPower * abs(theta - phi));
            } else {
                leftFront.setPower(0);
                rightBack.setPower(0);
                leftBack.setPower(0);
                rightFront.setPower(0);
                return true;
            }
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
        double range;
        if(result != null && result.isValid()){
            range = llDistance;
            //target = 940.08429 * pow((1.00383), range);
            if(range < 105) {
                target = 976 * pow(range, 0.0844869);
                if(range < 55){
                    target -= 60;
                }
            } else {
                target = 252 * pow(range, 0.406511);
            }
            target -= 125;
        } else if (isAuto) {
            target = 925;
        } else {
            target = 1100;
        }
        if(launchState.equals(LAUNCH)){
            target += 50;
        }
        //target *= 11.85 / voltageSensor.getVoltage();
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
    public void updateLights() {
        lightUtil.updateLights();
    }
    public LightUtil.LightState getLightState() {
        return lightUtil.getLightState();
    }
}
