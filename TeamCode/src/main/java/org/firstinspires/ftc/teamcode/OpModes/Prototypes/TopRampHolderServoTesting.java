package org.firstinspires.ftc.teamcode.OpModes.Prototypes;

//import static org.firstinspires.ftc.teamcode.aProccedural.Components.TopRampHolderServo;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.aProccedural.Components;
import org.firstinspires.ftc.teamcode.aProccedural.Input;

@TeleOp
public class TopRampHolderServoTesting extends OpMode {
    
    Input input = new Input();
    @Override
    public void init() {
        Components.initComponents(hardwareMap);
    }

    @Override
    public void start() {
        //TopRampHolderServo.setPosition(0);
    }
    
    @Override
    public void loop() {
        input.pollGamepad(gamepad1);
        if(input.a.down()){
            //TopRampHolderServo.setPosition(TopRampHolderServo.getPosition() + 0.05);
        }
        if(input.b.down()){
            //TopRampHolderServo.setPosition(TopRampHolderServo.getPosition() - 0.05);
        }
        //telemetry.addData("Position: ", TopRampHolderServo.getPosition());
    }
}
