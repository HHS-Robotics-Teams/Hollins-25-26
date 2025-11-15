package org.firstinspires.ftc.teamcode.HollinsMadeUtil;

import static org.firstinspires.ftc.teamcode._Proccedural.Components.IntakeMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.INTAKE_HOLD_POS;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.INTAKE_PPR;
import static java.lang.Math.abs;

import com.qualcomm.robotcore.hardware.DcMotor;

public class IntakeV2Util {

    static int interval = 100;
    public static void initIntake() {
        IntakeMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        IntakeMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }
    public static void updateIntake(boolean on, boolean reversed){
        if(on){
            if(reversed){
                IntakeMotor.setTargetPosition(IntakeMotor.getCurrentPosition() - interval);
            }
            else {
                IntakeMotor.setTargetPosition(IntakeMotor.getCurrentPosition() + interval);
            }
        } else {
            IntakeMotor.setTargetPosition(
                (int) (((
                    IntakeMotor.getCurrentPosition() / INTAKE_PPR)//num revolutions
                    * INTAKE_PPR) //ticks to run to
                    + INTAKE_HOLD_POS //offset for hold pos
            ));
        }
        IntakeMotor.setPower((double) abs(IntakeMotor.getCurrentPosition() - IntakeMotor.getTargetPosition()) / 85);

    }
}
