package org.firstinspires.ftc.teamcode.OpModes.Prototypes.old;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.LED;

import org.firstinspires.ftc.teamcode._Proccedural.Input;

import java.util.ArrayList;

@TeleOp
@Deprecated
@Disabled
public class LightIndicatorTesting extends OpMode {
    LED upFacingGreen;
    LED upFacingRed;
    LED leftFacingRed;
    LED leftFacingGreen;
    LED backFacingRed;
    LED backFacingGreen;
    private enum State {
        RED,
        AMBER,
        GREEN,
        OFF
    }
    State state = LightIndicatorTesting.State.OFF;
    Input input = new Input();
    ArrayList<LED> green = new ArrayList<>();
    ArrayList<LED> red = new ArrayList<>();
    @Override
    public void init() {
        upFacingGreen = hardwareMap.get(LED.class, "green1");
        upFacingRed = hardwareMap.get(LED.class, "red1");
        leftFacingGreen = hardwareMap.get(LED.class, "green2");
        leftFacingRed = hardwareMap.get(LED.class, "red2");
        backFacingGreen = hardwareMap.get(LED.class, "green3");
        backFacingRed = hardwareMap.get(LED.class, "red3");
        green.add(upFacingGreen);
        green.add(leftFacingGreen);
        green.add(backFacingGreen);
        red.add(upFacingRed);
        red.add(backFacingRed);
        red.add(leftFacingRed);
    }

    @Override
    public void loop() {
        input.pollGamepad(gamepad1);

        if(input.a.down()){
            if(state.equals(State.RED)){
                state = State.AMBER;
            } else if (state.equals(State.AMBER)){
                state = State.GREEN;
            } else if (state.equals(State.GREEN)){
                state = State.OFF;
            } else {
                state = State.RED;
            }
        }


        switch (state){
            case OFF:
                for(LED q : green){
                    q.off();
                }
                for(LED q : red){
                    q.off();
                }
                break;
            case RED:
                for(LED q : green){
                    q.off();
                }
                for(LED q : red){
                    q.on();
                }
                break;
            case AMBER:
                for(LED q : green){
                    q.on();
                }
                for(LED q : red){
                    q.on();
                }
                break;
            case GREEN:
                for(LED q : green){
                    q.on();
                }
                for(LED q : red){
                    q.off();
                }
                break;
        }
    }
}
