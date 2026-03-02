package org.firstinspires.ftc.teamcode._Util.OLD;

import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherHoodServo;
import static org.firstinspires.ftc.teamcode._Util.OLD.HoodUtilV2.HoodState.Far;
import static org.firstinspires.ftc.teamcode._Util.OLD.HoodUtilV2.HoodState.Idle;
import static org.firstinspires.ftc.teamcode._Util.OLD.HoodUtilV2.HoodState.Near2;
import static org.firstinspires.ftc.teamcode._Util.OLD.thanksJedison.llDistance;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.robotcore.util.ElapsedTime;

@Deprecated
public class HoodUtilV2 {
    public enum HoodState {
        Near1,
        Near2,
        Idle,
        Far,
        Far2,
        Near3
    }
    private static HoodState hoodState;
    ElapsedTime timeout = new ElapsedTime(ElapsedTime.SECOND_IN_NANO);
    public HoodUtilV2() {
        hoodState = Idle;
        timeout.reset();
    }

    public void resetTimeout() {
        timeout.reset();
    }
    public void updateHoodState(LLResult result) {
        if(result == null || !result.isValid()) {
            if(timeout.seconds() > 0.2){
                hoodState = Idle;
            }
        } else if (result.getStaleness() > 0.2) {
            hoodState = Idle;
        } else {
            timeout.reset();
            double range = llDistance;
            if(range > 105){
                hoodState = Far;
                LauncherHoodServo.setPosition(0.8);// 0.00714286*range*1.25
            } else {
                hoodState = Near2;
                if (range < 55) {
                    LauncherHoodServo.setPosition(0.4);
                } else {
                    LauncherHoodServo.setPosition(0.6);
                }
            }

        }
    }
    public void overwriteHoodState(HoodState state){
        hoodState = state;
        timeout.reset();
    }
}
