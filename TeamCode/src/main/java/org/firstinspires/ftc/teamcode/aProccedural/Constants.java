package org.firstinspires.ftc.teamcode.aProccedural;

import static org.firstinspires.ftc.teamcode.aProccedural.Components.intakeMotor;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.intakeSecondRollerMotor;

public class Constants {
    /* Old launcher Velocity's */
        public static final double shootingFar = .6;
        public static final double shootingClose = .3;
    /* New Launcher Velocity's */
        public static final double Idle_Vel = 500;
        public static final double Launcher_far_Vel = 1150;
        public static final double Launcher_close_Vel = 800;
        public static double targetVel;

    /* Old positions for holder servo */
       public static final double holding = 0.2;
       public static final double shooting = 0.1;

    /* New positions for launcher hand servo */
        public static final double loading = 0.85;
        public static final double firing = 0.6;

    /* Flags */
        public static boolean intake_reversed = false;
        public static boolean Launching_Far = false;
        public static boolean Launching_Close = false;
        public static boolean isIntaking;

    /* Timmers */
        public static final double TimeTwo = 2;
        public static final double TimeOne = 1 ;

    /* Intake Power Methods */
        public static void main_intake_Powers() {
            if (intake_reversed) {
            intakeMotor.setPower(-1);
            intakeSecondRollerMotor.setPower(-1);
            } else {
            intakeMotor.setPower(1);
            intakeSecondRollerMotor.setPower(1);
            }
        }
        public static void first_intake_Powers() {
            if (intake_reversed) {
            intakeMotor.setPower(-1);
            } else {
            intakeMotor.setPower(1);
            }
        }
        public static void second_intake_Powers() {
            if (intake_reversed) {
            intakeSecondRollerMotor.setPower(-1);
            } else {
            intakeSecondRollerMotor.setPower(1);
            }
        }
        public static void intake_stop() {
            intakeMotor.setPower(0);
            intakeSecondRollerMotor.setPower(0);
        }
}

