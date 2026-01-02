package org.firstinspires.ftc.teamcode._Proccedural;

public class Constants {

    /* Constants */
    public static double INTAKE_POWER = 1;
    public static final double LAUNCHER_IDLE = 0.35;

    public static final double INTAKE_PPR = 537.7;
    public static final double INTAKE_HOLD_POS = 0;

    public static double LAUNCHER_NEAR_TARGET = 2; //Todo Adjust value
    public static double LAUNCHER_FAR_TARGET = 2.2;
    public static double LAUNCHER_FAR_TARGET_FIRST = 2.05;
    public static double LAUNCHER_FAR_TARGET_THIRD = 2.45;
    public static final double LAUNCH_THRESHOLD = .021;
    public static final double LAUNCH_TICK_VELOCITY_NEAR = 800;
    public static final double LAUNCH_TICK_VELOCITY_FAR = 1000;
    public static final double LAUNCH_TICK_VEL_THRESHOLD = 35;
    public static final double LAUNCHED_THRESHOLD = 1;

    public static final double LAUNCHER_FINGER_UP_POS = 0.6;

    public static final double LAUNCHER_FINGER_DOWN_POS = 0.9;
    public static final double LAUNCHER_HOOD_UP_POS = 0.7;
    public static final double LAUNCHER_HOOD_DOWN_POS = 0;
    public static final double SAFETY_HOLDING = 1.0;
    public static final double SAFTEY_FIRING = 0.5;
    public static final double FINGER_UP_TIME = 0.75;
    /* Timings */
    public static double DWELL_TIME = 0.5;
    public static double LAUNCHING_TIME = 1.0;

    /* FLAGS */
    public static boolean INTAKE_RUN;
    public static boolean INTAKE_LEVEL_TWO_RUN;
    public static boolean INTAKE_REVERSED = false;
    public static boolean LAUNCHER_RUN = false;
    public static boolean LAUNCHER_RUN_TWO = false;
    public static boolean LAUNCHER_RUN_THREE = false;
    public static boolean LAUNCH_FAR = true;
    public static boolean DriveSlowdown = false;

    public static int numShot = 0;
}
