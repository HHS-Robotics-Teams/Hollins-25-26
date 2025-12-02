package org.firstinspires.ftc.teamcode.Util;


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

/**
 * AutoPIDTuner
 *
 * How it works (brief):
 * - Uses only P (I=0, D=0) and gradually increases kP until the velocity
 *   shows a sustained oscillation (detected by repeated sign changes of error).
 * - Records oscillation period Tu (from sign-change timestamps) and Ku (the P that caused it).
 * - Calculates Ziegler-Nichols PID values:
 *     Kp = 0.6 * Ku
 *     Ti = 0.5 * Tu  => Ki = Kp / Ti
 *     Td = 0.125 * Tu => Kd = Kp * Td
 *
 * Safety: This intentionally drives the shooter into oscillation. Clear area before running.
 */
@TeleOp (name = "PID Test FlyWheel")
public class PIDTestFlyWheel extends LinearOpMode {
    DcMotorEx LauncherMotor;
    private PIDFlywheel pid;

    private double kP = 0.0008;
    private double kI = 0.000001;
    private double kD = 0.0002;

    @Override
    public void runOpMode() throws InterruptedException {
        LauncherMotor = hardwareMap.get(DcMotorEx.class, "LauncherMotor");
        LauncherMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        pid = new PIDFlywheel(kP, kI, kD);

        // Target shooter speed (ticks/second or RPM)
        double targetVelocity = 2000;

        waitForStart();

        while (opModeIsActive()) {
            double currentVelocity = LauncherMotor.getVelocity(); // ticks per second

            double pidOutput = pid.update(targetVelocity, currentVelocity);

            // clamp motor power
            pidOutput = Math.max(0, Math.min(pidOutput, 1));

            LauncherMotor.setPower(pidOutput);

            telemetry.addData("Target", targetVelocity);
            telemetry.addData("Velocity", currentVelocity);
            telemetry.addData("Power", pidOutput);
            telemetry.update();
        }
    }
}
