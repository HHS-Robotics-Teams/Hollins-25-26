package org.firstinspires.ftc.teamcode.aProccedural;

public class Constants {

    /* Constants */
    public static double INTAKE_POWER = 1;

    public static final double LAUNCHER_IDLE = 0.25;
    public static final double LAUNCHER_FAR_BASE = .85; //todo tune

    public static double LAUNCHER_FAR_TARGET = 2.9; //todo tune
    public static final double LAUNCH_THRESHOLD = 0.02; //todo tune
    public static final double LAUNCHED_THRESHOLD = 1; //todo tune

    public static final double LAUNCHER_FINGER_UP_POS = 0.7;
    public static final double LAUNCHER_FINGER_DOWN_POS = 0.4;

    /* FLAGS */
    public static boolean INTAKE_RUN;
    public static boolean INTAKE_REVERSED = false;
    public static boolean LAUNCHER_HOLDER_ENABLE = true;
    public static boolean LAUNCHER_RUN = false;
    public static boolean LAUNCHER_RUN_THREE = false;

    public static int numShot = 0;

    public static double timeAtLaunch = 0;
}
