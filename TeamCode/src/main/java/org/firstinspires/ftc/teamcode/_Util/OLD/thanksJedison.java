package org.firstinspires.ftc.teamcode._Util.OLD;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.limelight;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.HardwareMap;

@Deprecated
public class thanksJedison {
    Limelight3A ll;

    double mountingPitchAngle = 0.0;
    double height = 0.0;

    public static double llAngle;
    public static double llDistance;

    thanksJedison(HardwareMap hw) {
        ll = limelight;
        llDistance = 0;
        llAngle = 0;
    }

    void update() {
        LLResult r = ll.getLatestResult();

        if(r==null || !r.isValid()) return; // make sure the result is valid and we detected an april-tag

        llAngle = r.getTx(); // This is the angle to the detection

        llDistance = getDistance(r);
    }

    double getDistance(LLResult r) {
        double goalATHeight = 29.5;
        return (29.5 - height) / Math.tan((mountingPitchAngle + r.getTy()) * 180.0/Math.PI);
    }
}