package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.teamcode.util.Flags.LAUNCHER_RUN;
import static org.firstinspires.ftc.teamcode.util.Flags.resetFlags;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.util.Flags;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.components.BindingsComponent;
import dev.nextftc.core.components.SubsystemComponent;
import dev.nextftc.ftc.Gamepads;
import dev.nextftc.ftc.NextFTCOpMode;
import dev.nextftc.ftc.components.BulkReadComponent;
import dev.nextftc.hardware.driving.MecanumDriverControlled;
import dev.nextftc.hardware.impl.MotorEx;

@TeleOp
public class exampleOpModeTwo extends NextFTCOpMode {
    public exampleOpModeTwo() {
        addComponents(new SubsystemComponent(ExampleSystem.INSTANCE),
                BindingsComponent.INSTANCE,
                BulkReadComponent.INSTANCE);
    }

    private final MotorEx frontLeft = new MotorEx("frontLeft");//.reversed() if needed
    private final MotorEx frontRight = new MotorEx("frontRight");//.reversed() if needed
    private final MotorEx backLeft = new MotorEx("backLeft");//.reversed() if needed
    private final MotorEx backRight = new MotorEx("backRight");//.reversed() if needed

    @Override
    public void onInit() {
        resetFlags();
    }

    @Override
    public void onStartButtonPressed() {
        Command driverControlled = new MecanumDriverControlled(
                frontLeft,
                frontRight,
                backLeft,
                backRight,
                Gamepads.gamepad1().leftStickY().negate(),
                Gamepads.gamepad1().leftStickX(),
                Gamepads.gamepad1().rightStickX()
        );
        driverControlled.schedule();

        Gamepads.gamepad1().rightTrigger().greaterThan(0.2)
                .and(() -> LAUNCHER_RUN = false)
                .whenBecomesTrue(ExampleSystem.INSTANCE.spinUp.then(ExampleSystem.INSTANCE.launch));

        Gamepads.gamepad1().rightTrigger().greaterThan(0.2)
                .and(() -> LAUNCHER_RUN = true)
                .whenBecomesTrue(ExampleSystem.INSTANCE.idle);
    }
}
