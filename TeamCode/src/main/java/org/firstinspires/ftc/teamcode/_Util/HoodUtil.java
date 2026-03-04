package org.firstinspires.ftc.teamcode._Util;

import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherHoodServo;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.cameraTiltServo;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.CAMERA_START_POS;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.LAUNCHER_RUN;

import com.qualcomm.robotcore.util.ElapsedTime;

public class HoodUtil {
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
    public HoodUtil() {
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
    public void updateHoodState(AprilTagMethod aprilTagMethod) {
        if(!aprilTagMethod.isTagVisible()) {
            if(timeout.seconds() > 0.2){
                hoodState = HoodState.Idle;
            }
        } else {
            timeout.reset();
            if(aprilTagMethod.isTagVisible()) {
                double range = aprilTagMethod.getTagDistance();
                if(range > 95){
                    hoodState = HoodState.Far;
                    if(!LAUNCHER_RUN) {
                        LauncherMotor.setVelocity(1500);
                    }
                    LauncherHoodServo.setPosition(0.875);// 0.00714286*range*1.25
                } else {
                    hoodState = HoodState.Near2;
                    if (range < 55) {
                        LauncherHoodServo.setPosition(0.4);
                    } else if (range < 75) {
                        LauncherHoodServo.setPosition(0.5);
                    } else {
                        LauncherHoodServo.setPosition(0.7);
                    }
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
