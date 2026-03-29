package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.teamcode.util.Flags.LAUNCHER_RUN;
import static org.firstinspires.ftc.teamcode.util.Flags.resetFlags;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.conditionals.IfElseCommand;
import dev.nextftc.core.commands.delays.Delay;
import dev.nextftc.core.commands.groups.SequentialGroup;
import dev.nextftc.core.components.BindingsComponent;
import dev.nextftc.core.components.SubsystemComponent;
import dev.nextftc.ftc.NextFTCOpMode;
import dev.nextftc.ftc.components.BulkReadComponent;
import kotlin.jvm.functions.Function0;

@Autonomous
public class exampleOpMode extends NextFTCOpMode {
    public exampleOpMode() {
        addComponents(
                new SubsystemComponent(ExampleSystem.INSTANCE),
                BulkReadComponent.INSTANCE,
                BindingsComponent.INSTANCE
        );
    }
    private Command run() {
        return new SequentialGroup(
                ExampleSystem.INSTANCE.idle,
                new Delay(0.25),
                ExampleSystem.INSTANCE.spinUp,
                ExampleSystem.INSTANCE.launch,
                new IfElseCommand(() -> LAUNCHER_RUN, run().endAfter(0.5))
        );
    }

    @Override
    public void onInit() {
        resetFlags();
    }
    @Override
    public void onStartButtonPressed() {
        run().schedule();
        //then do other stuff
    }
}
