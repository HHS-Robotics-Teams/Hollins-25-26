package org.firstinspires.ftc.teamcode._Util;

import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherHoodServo;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.cameraTiltServo;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.CAMERA_START_POS;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.robotcore.util.ElapsedTime;

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
        hoodState = HoodState.Idle;
        timeout.reset();
    }
    public void updateHoodPose() {
        switch (hoodState){
            case Near1:
                cameraTiltServo.setPosition(CAMERA_START_POS);
                break;
            case Near2:
                cameraTiltServo.setPosition(0.825);
                break;
            case Idle:
                cameraTiltServo.setPosition(0.85);
                break;
            case Far:
                cameraTiltServo.setPosition(0.9);
                break;
        }
    }
    public void resetTimeout() {
        timeout.reset();
    }
    public void updateHoodState(LLResult result) {
        if(result.isValid()) {
            if(timeout.seconds() > 0.2){
                hoodState = HoodState.Idle;
            }
        } else {
            timeout.reset();
            double range = result.getBotposeAvgDist();
            if(range > 105){
                hoodState = HoodState.Far;
                LauncherHoodServo.setPosition(0.8);// 0.00714286*range*1.25
            } else {
                hoodState = HoodState.Near2;
                if (range < 55) {
                    LauncherHoodServo.setPosition(0.4);
                } else {
                    LauncherHoodServo.setPosition(0.6);
                }
            }

        }
        updateHoodPose();
    }
    public void overwriteHoodState(HoodState state){
        hoodState = state;
        timeout.reset();
        updateHoodPose();
    }
}
