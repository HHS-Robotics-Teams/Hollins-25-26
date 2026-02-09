package org.firstinspires.ftc.teamcode._Util;

import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherHoodServo;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.cameraTiltServo;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.CAMERA_START_POS;

import com.qualcomm.robotcore.util.ElapsedTime;

public class HoodUtil {
    public enum HoodState {
        Near1,
        Near2,
        Idle,
        Far
    }
    private static HoodState hoodState;
    ElapsedTime timeout = new ElapsedTime(ElapsedTime.SECOND_IN_NANO);
    public HoodUtil() {
        hoodState = HoodState.Idle;
        timeout.reset();
    }
    public void updateHoodPose() {
        switch (hoodState){
            case Near1:
                cameraTiltServo.setPosition(CAMERA_START_POS);
                LauncherHoodServo.setPosition(0.2);
                break;
            case Near2:
                cameraTiltServo.setPosition(0.825);
                LauncherHoodServo.setPosition(0.275);
                break;
            case Idle:
                cameraTiltServo.setPosition(0.85);
                LauncherHoodServo.setPosition(0.4);
                break;
            case Far:
                cameraTiltServo.setPosition(0.9);
                LauncherHoodServo.setPosition(0.8);
                break;
        }
    }
    public void resetTimeout() {
        timeout.reset();
    }
    public void updateHoodState(AprilTagMethod aprilTagMethod) {
        if(!aprilTagMethod.isTagVisible()) {
            if(timeout.seconds() > 0.2){
                hoodState = HoodState.Idle;
            }
        } else {
            timeout.reset();
            if(aprilTagMethod.isTagVisible()) {
                if (aprilTagMethod.getTagDistance() < 50) {
                    hoodState = HoodState.Near1;
                } else if (aprilTagMethod.getTagDistance() < 80) {
                    hoodState = HoodState.Near2;
                } else {
                    hoodState = HoodState.Far;
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
