package org.firstinspires.ftc.teamcode.OpModes.Prototypes;

import static org.firstinspires.ftc.teamcode.HollinsMadeUtil.IntakeV2Util.initIntake;
import static org.firstinspires.ftc.teamcode.HollinsMadeUtil.IntakeV2Util.updateIntake;
import static org.firstinspires.ftc.teamcode._Proccedural.Components.IntakeMotor;
import static org.firstinspires.ftc.teamcode._Proccedural.Constants.INTAKE_PPR;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode._Proccedural.Components;
import org.firstinspires.ftc.teamcode._Proccedural.Input;

@TeleOp
@Disabled
public class IntakeV2Testing extends OpMode {
    Input input = new Input();
    boolean a = false;
    boolean b = false;
    @Override
    public void init() {
        Components.initComponents(hardwareMap);
        initIntake();
    }

    @Override
    public void loop() {
        input.pollGamepad(gamepad1);
        a = input.a.held();
        b = input.b.held();
        updateIntake(a,b);
        telemetry.addData("pos: ", IntakeMotor.getCurrentPosition());
        telemetry.addData("target: ", IntakeMotor.getTargetPosition());
        telemetry.addData("pos on rev: ", IntakeMotor.getCurrentPosition() % INTAKE_PPR);
        telemetry.addData("num revs: ", (int) (IntakeMotor.getCurrentPosition() / INTAKE_PPR));
        telemetry.addData("a", a);
        telemetry.addData("b", b);
    }
}
