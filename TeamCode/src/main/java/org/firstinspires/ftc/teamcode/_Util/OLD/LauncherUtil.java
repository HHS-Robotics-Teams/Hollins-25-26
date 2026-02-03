package org.firstinspires.ftc.teamcode._Util.OLD;

import static org.firstinspires.ftc.teamcode._Proccedural.Components.IntakeMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherFingerServo;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LeftSideFeedRoller;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.leftArtifactCounterDistance;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.leftBack;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.leftFront;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.rearDistance;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.rearSideDistance;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.rightArtifactCounterDistance;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.rightBack;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.rightFront;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.INTAKE_POWER;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_FINGER_DOWN_POS;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_FINGER_UP_POS;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_IDLE;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_RUN;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCH_TICK_VELOCITY_NEAR;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCH_TICK_VEL_THRESHOLD;
import static java.lang.Math.abs;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode._Util.AprilTagMethod;

/**
 * Runs flywheel style launcher logic to make teleOp code more readable
 * Includes camera vision & automatic alignment
 */
@Deprecated
public class LauncherUtil {
    AprilTagMethod aprilTagMethod;
    public enum LaunchState {
        FIND_TAG,
        SPIN_UP_AND_MOVE,
        CHECK_AGAIN,
        LAUNCH,
        RESET_SHOT,
        INTAKE,
        EXIT,
        DISTANCE_CHECK
    }
    private LaunchState launchState;
    private double target;
    private final String color;
    private final boolean isAuto;
    private double theta;
    private double phi;

    ElapsedTime intakeTimer = new ElapsedTime(ElapsedTime.SECOND_IN_NANO);
    ElapsedTime launchTimer = new ElapsedTime(ElapsedTime.SECOND_IN_NANO);
    ElapsedTime resetTimer = new ElapsedTime(ElapsedTime.SECOND_IN_NANO);

    /**
     * Sets target vel manually
     * basically never used
     * see also DcMotor.setVelocity
     * @param target the velocity (in RPM) to be set
     */
    public void setTarget(double target) {
        this.target = target;
    }

    /**
     * Constructor
     * @param color "BLUE" or "RED" for camera logic
     * @param isAuto true for auto, false for not
     */
    public LauncherUtil(String color, boolean isAuto) {
        aprilTagMethod = new AprilTagMethod();
        launchState = LaunchState.FIND_TAG;
        this.color = color;
        this.isAuto = isAuto;
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
        if (!aprilTagMethod.isTagVisible() ) {
            return "No Tag Visible";
        }
        switch (launchState) {
            case EXIT:
                launchState = LaunchState.FIND_TAG;
                LauncherMotor.setVelocity(LAUNCH_TICK_VELOCITY_NEAR);
                LAUNCHER_RUN = false;
                break;
            case FIND_TAG:
                if (!aprilTagMethod.isTagVisible() ) {
                    return "No Tag Visible";
                } else if (aprilTagMethod.tagMatchesAlliance(color)) {
                    launchState = LaunchState.SPIN_UP_AND_MOVE;
                    break;
                }
                break;
            case SPIN_UP_AND_MOVE:
                if (!aprilTagMethod.isTagVisible() ) {
                    return "No Tag Visible";
                } else if ((moveToLaunch()) && isSpunUp()) {
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
                } else {
                    LauncherFingerServo.setPosition(LAUNCHER_FINGER_UP_POS);
                    LeftSideFeedRoller.setPower(1);
                    if (launchTimer.seconds() >= 0.25) {
                        launchState = LaunchState.RESET_SHOT;
                        resetTimer.reset();
                    }
                }
                break;
            case RESET_SHOT:
                if (!aprilTagMethod.isTagVisible() ) {
                    return "No Tag Visible";
                } else {
                    LauncherFingerServo.setPosition(LAUNCHER_FINGER_DOWN_POS);
                    LeftSideFeedRoller.setPower(0);
                    resetTimer.reset();
                    launchState = LaunchState.DISTANCE_CHECK;
                    }
                break;
            case INTAKE:
                if (!aprilTagMethod.isTagVisible() ) {
                    return "No Tag Visible";
                    } else {
                    IntakeMotor.setPower(INTAKE_POWER);
                    if (intakeTimer.seconds() >= 0.4) { //todo check this time could be less
                        launchState = LaunchState.CHECK_AGAIN;
                        isSpunUp();
                    }
                }
                break;
            case CHECK_AGAIN:
                IntakeMotor.setPower(0);
                if(isAuto) {
                    launchState = LaunchState.LAUNCH;
                    launchTimer.reset();
                } else if (!aprilTagMethod.isTagVisible() ) {
                    return "No Tag Visible";
                } else {
                    isSpunUp();
                    launchState = LaunchState.SPIN_UP_AND_MOVE;
                }
                break;
            case DISTANCE_CHECK:
                if (!aprilTagMethod.isTagVisible() ) {
                    return "No Tag Visible";
                } else if (resetTimer.seconds() >= 0.2) { //todo check this time could be less
                    if (rearDistance.getDistance(DistanceUnit.INCH) <= 6 || rearSideDistance.getDistance(DistanceUnit.INCH) <= 4) {
                        launchState = LaunchState.CHECK_AGAIN;
                    } else if (rearDistance.getDistance(DistanceUnit.INCH) <= 11
                           || leftArtifactCounterDistance.getDistance(DistanceUnit.INCH) <= 5
                           || rightArtifactCounterDistance.getDistance(DistanceUnit.INCH) <= 5) {
                        launchState = LaunchState.INTAKE;
                        intakeTimer.reset();
                        isSpunUp();
                    } else {
                        launchState = LaunchState.EXIT;
                    }
                    break;
                }
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
            double range = aprilTagMethod.getTagDistance() + 2;
            double margin;
            if (range >= 90) {
                phi = 2.75;
                margin = 3;
            } else {
                phi = 0;
                margin = 6;
            }
            double turnPower = 0.25;
            if (theta >= phi + margin - 1) {
                leftFront.setPower(-turnPower);
                rightBack.setPower(turnPower);
                leftBack.setPower(-turnPower);
                rightFront.setPower(turnPower);
            } else if (theta <= phi - margin + 1) {
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
                return true;
            }
            target = (0.0132275 * range * range) + (1.52116 * range) + 732.98942;
            return abs(theta - phi) <= margin;
        }
    }

    /**
     * Helper for runLauncher() (sets vel)
     * @return boolean true if velocity is within threshold
     */
    private boolean isSpunUp() {
        LauncherMotor.setVelocity(target);
        return abs(LauncherMotor.getVelocity() - target) <= LAUNCH_TICK_VEL_THRESHOLD;
    }
}
