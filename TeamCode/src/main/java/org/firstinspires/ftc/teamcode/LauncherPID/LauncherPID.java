package org.firstinspires.ftc.teamcode.LauncherPID;

import static org.firstinspires.ftc.teamcode.aProccedural.Components.LauncherMotor;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class LauncherPID {

    /*
     * Target velocity for launcher motor
     * IN RADIANS
     * Remember to account for differences
     * in wheel vs axle tangential velocity
      */
    private static double LauncherTargetVelocity = 0;
    private static double lastVel;
    public static double getLastVel(){return lastVel;}
    private static double lastTime;
    public static double getLastTime(){return lastTime;}
    private static double currentVel = 0;
    private static double totalError = 0;
    public static double getTotalError(){return totalError;}

    //TODO Tune these
    public static double LauncherkP = .5;
    public static double LauncherkI = 0;
    public static double LauncherkD = 0;

    /**
     * Initializes some vars and starts motor spin-up
     * DO NOT RUN IN INIT
     * @param currentTime use getRuntime();
     * @param startTarget target vel
     * DO NOT RUN IN INIT
     */
    public static void initLauncherPID(double currentTime, double startTarget) {
        lastTime = currentTime;
        lastVel = 0;
        setLauncherTargetVelocity(startTarget);
    }

    /**
     * Sets LauncherMotor Power dynamically
     * @param currentTime use getRuntime();
     */
    public static void updateLauncherPID(double currentTime) {
        //Updates vars
        currentVel = LauncherMotor.getVelocity(AngleUnit.RADIANS);
        double timeStep = currentTime - lastTime;
        double currentError = LauncherTargetVelocity - LauncherMotor.getVelocity(AngleUnit.RADIANS);
        //Rectangular approximation of an integral
        totalError += (currentError * timeStep);
        //Secant approximation of a derivative
        double errorChange = (currentError - (LauncherTargetVelocity - lastVel)) / timeStep;

        double powerOutput = normalizeLauncherPID(
                LauncherkP * currentError + //P term
                LauncherkI * totalError +   //I term
                LauncherkD * errorChange    //D term
        );

        //TODO Decide if still needed after testing,
        //mostly is here for fault tolerance
        if((currentVel >= 2.5) && (powerOutput <=0)){
            LauncherMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            powerOutput =0;
        } else {
            LauncherMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        }

        LauncherMotor.setPower(powerOutput);

        //More var updating
        lastVel = currentVel;
        lastTime = currentTime;
    }

    public static boolean getLaunchReadinessStatus() {
        return Math.abs(LauncherMotor.getVelocity(AngleUnit.RADIANS) - LauncherTargetVelocity) <= Math.PI / 6;
    }

    /**
     * @param n power from PID Controller
     * @return value usable for DcMotorEx,
     *         restricted over range -1,1 inclusive
     */
    private static double normalizeLauncherPID(double n) {
        if(n > 1){return 1;}
        else if (n<-1) {return -1;}
        else {return n;}
    }

    public static double getLauncherTargetVelocity() {
        return LauncherTargetVelocity;
    }

    public static double getLauncherCurrentVelocity() {
        return currentVel;
    }

    public static void setLauncherTargetVelocity(double launcherTargetVelocity) {
        LauncherTargetVelocity = launcherTargetVelocity;
    }
}
