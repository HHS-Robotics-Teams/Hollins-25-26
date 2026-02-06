package org.firstinspires.ftc.teamcode._Util;

import static org.firstinspires.ftc.teamcode._Proccedural.Components.leftArtifactCounterDistance;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.rearDistance;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.rearSideDistance;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.rightArtifactCounterDistance;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class AutoUtil {
    ElapsedTime emptyTimer = new ElapsedTime(ElapsedTime.SECOND_IN_NANO);
    private final double timeout;
    public AutoUtil(double timeout) {
        this.timeout = timeout;
        emptyTimer.reset();
    }
    public boolean isBotEmpty() {
        if( leftArtifactCounterDistance.getDistance(DistanceUnit.INCH) <= 6
            || rightArtifactCounterDistance.getDistance(DistanceUnit.INCH) <= 6
            || rearDistance.getDistance(DistanceUnit.INCH) <= 8
            || rearSideDistance.getDistance(DistanceUnit.INCH) <= 4 ) {
            emptyTimer.reset();
        }
        return emptyTimer.seconds() >= timeout;
    }

    public String currentReadings() {
        return "---DISTANCE READINGS---\nLeft: " + leftArtifactCounterDistance.getDistance(DistanceUnit.INCH) + "\nRight: " + rightArtifactCounterDistance.getDistance(DistanceUnit.INCH) + "\nRear: " + rearDistance.getDistance(DistanceUnit.INCH) + "\nRear Side" + rearSideDistance.getDistance(DistanceUnit.INCH);
    }

    public void resetEmptyTimer() {
        emptyTimer.reset();
    }

    public double getTimeout() {
        return timeout;
    }
}
