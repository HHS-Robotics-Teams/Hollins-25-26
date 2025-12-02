package org.firstinspires.ftc.teamcode.Util;

import com.qualcomm.robotcore.util.ElapsedTime;

public class PIDFlywheel {

    ElapsedTime timer = new ElapsedTime();
    double kP = 0.7;
    double kI = 0.0;
    double kD = 0.05;

    public double integral = 0;
    private double lastError = 0;
    private long lastTime = 0;
    public PIDFlywheel(double kP, double kI, double kD) {
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
        lastTime = System.nanoTime();
    }
    public double update(double targetVelocity, double currentVelocity) {
        long now = System.nanoTime();
        double dt = (now - lastTime) / 1e9;   // convert ns → seconds
        lastTime = now;

        double error = targetVelocity - currentVelocity;

        // Integral term
        integral += error * dt;

        // Derivative term
        double derivative = (error - lastError) / dt;
        lastError = error;

        // PID output
        return (kP * error) + (kI * integral) + (kD * derivative);
    }

}
