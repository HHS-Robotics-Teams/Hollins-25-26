package org.firstinspires.ftc.teamcode._Proccedural;

public class Constants {
    public static double FEED_TIME_SECONDS = 0.20; //The feeder servos run this long when a shot is requested.
    public static double STOP_SPEED = 0.0; //We send this power to the servos when we want them to stop.
    public static double FULL_SPEED = 1.0;
    public static double LAUNCHER_TARGET_VELOCITY = 1275;
    public static double LAUNCHER_MIN_VELOCITY = 1075;
    public static double TIME_BETWEEN_SHOTS = 2;
    public static double DRIVE_SPEED = 0.5;
    public static double ROTATE_SPEED = 0.2;
    public static double WHEEL_DIAMETER_MM = 96;
    public static double ENCODER_TICKS_PER_REV = 537.7;
    public static double TICKS_PER_MM = (ENCODER_TICKS_PER_REV / (WHEEL_DIAMETER_MM * Math.PI));
    public static double TRACK_WIDTH_MM = 404;
    public static int shotsToFire = 3; //The number of shots to fire in this auto.
    public static double robotRotationAngle = 45;

}
