package org.firstinspires.ftc.teamcode._Util;

import static org.firstinspires.ftc.teamcode._Proccedural.Components.ConveyorMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.IntakeMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LauncherSafetyServo;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.LeftSideFeedRoller;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.rightSideFeedRoller;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.SAFETY_HOLDING;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.SAFTEY_FIRING;

public class IntakeUtil {
    /**
     * runs all at max power
     */
    public void launchStart(){
        launchStart(1);
    }
    /**
     * runs all at power
     * @param power power to run
     */
    public void launchStart(double power){
        launchStart(1,1);
    }

    /**
     * runs all
     * @param intake intake power
     * @param rollers roller power
     */
    public void launchStart(double intake, double rollers){
        LauncherSafetyServo.setPosition(SAFTEY_FIRING);
        LeftSideFeedRoller.setPower(rollers);
        rightSideFeedRoller.setPower(rollers);
        IntakeMotor.setPower(intake);
        ConveyorMotor.setPower(intake);
    }

    /**
     * turns all off
     */
    public void launchEnd() {
        LeftSideFeedRoller.setPower(0);
        rightSideFeedRoller.setPower(0);
        IntakeMotor.setPower(0);
        ConveyorMotor.setPower(0);
    }

    /**
     * runs just intake no rollers at max
     */
    public void intakeOn() {
        intakeOn(1);
    }

    /**
     * runs just intake no rollers
     * @param power power to run
     */
    public void intakeOn(double power) {
        LauncherSafetyServo.setPosition(SAFETY_HOLDING);
        LeftSideFeedRoller.setPower(0);
        rightSideFeedRoller.setPower(0);
        IntakeMotor.setPower(power);
        ConveyorMotor.setPower(power);
    }

    /**
     * turns all off
     */
    public void intakeOff() {
        launchEnd();
    }
}
