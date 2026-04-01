package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.teamcode.util.Flags.LAUNCHER_RUN;
import static java.lang.Math.abs;

import dev.nextftc.control.ControlSystem;
import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.conditionals.IfElseCommand;
import dev.nextftc.core.commands.utility.InstantCommand;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.hardware.controllable.RunToVelocity;
import dev.nextftc.hardware.impl.MotorEx;
import dev.nextftc.hardware.impl.ServoEx;
import dev.nextftc.hardware.positionable.SetPosition;

public class ExampleSystem implements Subsystem {
    public static final ExampleSystem INSTANCE = new ExampleSystem();
    private ExampleSystem() { }

    private int target_vel = 1100;
    private final MotorEx exampleMotor = new MotorEx("example");
    private final ServoEx exampleServo = new ServoEx("exServo");

    private final ControlSystem motorControl = ControlSystem.builder()
            .velPid(25,1,5)
            .basicFF(45)
            .build();

    public Command idle = new RunToVelocity(motorControl, 1100)
            .then(new SetPosition(exampleServo, 1))
            .requires(this);
    public Command spinUp = new InstantCommand(this::calcTarget)
            .then(new RunToVelocity(motorControl, target_vel)).requires(this /* would also need camera & calculation stuff */);
    public Command launch = new IfElseCommand(() -> abs(exampleMotor.getVelocity() - target_vel) <= 50,
            new SetPosition(exampleServo, 0)
            .then(new InstantCommand(() -> LAUNCHER_RUN = true))).requires(this);
    private void calcTarget() {
        //implementation not shown
        target_vel = (int) (Math.random() * 500) + 1000;
    }

    @Override
    public void periodic() {
        exampleMotor.setPower(motorControl.calculate(exampleMotor.getState()));
    }

}
