package org.firstinspires.ftc.teamcode._Util;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class thanksJedison {
    Limelight3A ll;

    double mountingPitchAngle = 0.0;
    double height = 0.0;

    thanksJedison(HardwareMap hw) {
        ll = hw.get(Limelight3A.class, "name");
    }

    void update() {
        LLResult r = ll.getLatestResult();

        if(r==null) return; // make sure the result is valid and we detected an april-tag

        double angle = r.getTx(); // This is the angle to the detection

        double distance = getDistance(r);
    }

    double getDistance(LLResult r) {
        double goalATHeight = 29.5;
        return (29.5 - height) / Math.tan((mountingPitchAngle + r.getTy()) * 180.0/Math.PI);
    }
}