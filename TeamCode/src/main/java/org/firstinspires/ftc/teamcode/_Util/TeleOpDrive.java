package org.firstinspires.ftc.teamcode._Util;

import static org.firstinspires.ftc.teamcode._Proccedural.Components.leftBack;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.leftFront;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.rightBack;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.rightFront;
import static java.lang.Math.abs;
import static java.lang.Math.max;

public class TeleOpDrive {

    @SuppressWarnings("ReassignedVariable")
    public static void run(double forward, double strafes, double rotates){
        //thresholds
        if (abs(forward) <= 0.05) {
            forward = 0;
        }
        if (abs(strafes) <= 0.05) {
            strafes = 0;
        }
        if (abs(rotates) <= 0.05) {
            rotates = 0;
        }

        //Power fixer
        double denominator = max((abs(forward) + abs(strafes) + abs(rotates)), 1.90);

        //Setting Powers
        leftFront.setPower((forward + strafes + rotates) / denominator);
        rightFront.setPower((forward - strafes - rotates) / denominator);
        leftBack.setPower((forward - strafes + rotates) / denominator);
        rightBack.setPower((forward + strafes - rotates) / denominator);
    }

}
