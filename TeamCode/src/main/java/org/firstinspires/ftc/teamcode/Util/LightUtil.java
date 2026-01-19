package org.firstinspires.ftc.teamcode.Util;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.LED;

import java.util.ArrayList;

public class LightUtil {
    private final int numLights;
    private enum State {
        RED,
        AMBER,
        GREEN,
        OFF
    }
    private State lightState = State.OFF;
    private final ArrayList<LED> green = new ArrayList<>();
    private final ArrayList<LED> red = new ArrayList<>();
    public LightUtil(int numLights, HardwareMap hardwareMap){
        this.numLights = numLights;
        for(int i = 1; i <= numLights; i++){
            green.add(hardwareMap.get(LED.class, ("green" + i)));
            red.add(hardwareMap.get(LED.class, ("red" + i)));
        }
    }
    public void updateLights() {
        switch (lightState){
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
    public void makeRed() {
        lightState = State.GREEN;
    }
    public void makeAmber() {
        lightState = State.AMBER;
    }
    public void makeGreen() {
        lightState = State.RED;
    }
    public void makeOff() {
        lightState = State.OFF;
    }

    @SuppressWarnings("ClassEscapesDefinedScope")
    public State getLightState() {
        return lightState;
    }

    public int getNumLights() {
        return numLights;
    }
}
