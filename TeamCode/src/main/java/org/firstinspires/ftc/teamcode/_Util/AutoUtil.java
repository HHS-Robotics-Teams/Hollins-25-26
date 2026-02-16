package org.firstinspires.ftc.teamcode._Util;

import static org.firstinspires.ftc.teamcode._Proccedural.Components.centerDistance;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.rearDistance;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.rearSideDistance;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.frontDistance;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.rearTopDistance;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class AutoUtil {
    ElapsedTime emptyTimer;
    private double timeout;
    public AutoUtil(double timeout) {
        this.timeout = timeout;
        emptyTimer = new ElapsedTime(ElapsedTime.SECOND_IN_NANO);
        emptyTimer.reset();
    }
    public boolean isBotEmpty() {
        if( centerDistance.getDistance(DistanceUnit.INCH) <= 5.5
            || frontDistance.getDistance(DistanceUnit.INCH) <= 7
            || rearDistance.getDistance(DistanceUnit.INCH) <= 11.5
            || rearSideDistance.getDistance(DistanceUnit.INCH) <= 6
            || rearTopDistance.getDistance(DistanceUnit.INCH) <= 9) {
            emptyTimer.reset();
        }
        return emptyTimer.seconds() >= timeout;
    }

    public String currentReadings() {
        return "---DISTANCE READINGS---\nCenter: " + centerDistance.getDistance(DistanceUnit.INCH) + "\nFront: " + frontDistance.getDistance(DistanceUnit.INCH) + "\nRear: " + rearDistance.getDistance(DistanceUnit.INCH) + "\nRear Side" + rearSideDistance.getDistance(DistanceUnit.INCH) +"\nRear Top Distance" + rearTopDistance.getDistance(DistanceUnit.INCH) + "\nTimer time" + emptyTimer.seconds();
    }

    public void resetEmptyTimer() {
        emptyTimer.reset();
    }
    public void changeTimeout(double timeout) {
        this.timeout = timeout;
    }

    public double getTimeout() {
        return timeout;
    }
}
