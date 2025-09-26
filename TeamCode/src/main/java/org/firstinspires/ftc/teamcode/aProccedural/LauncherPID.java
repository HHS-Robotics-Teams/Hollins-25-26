package org.firstinspires.ftc.teamcode.aProccedural;

import static org.firstinspires.ftc.teamcode.aProccedural.Components.LauncherMotor;

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
    private static double lastTime;
    private static double currentVel = 0;
    private static double totalError = 0;

    //TODO Tune these
    public static double LauncherkP = .5;
    public static double LauncherkI = .2;
    public static double LauncherkD = .2;

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

        double p = normalizeLauncherPID(
                LauncherkP * currentError + //P term
                LauncherkI * totalError +   //I term
                LauncherkD * errorChange    //D term
        );

        LauncherMotor.setPower(p);

        //More var updating
        lastVel = currentVel;
        lastTime = currentTime;
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

    public static double getLauncherCurentVelocity() {
        return currentVel;
    }

    public static void setLauncherTargetVelocity(double launcherTargetVelocity) {
        LauncherTargetVelocity = launcherTargetVelocity;
    }
}
