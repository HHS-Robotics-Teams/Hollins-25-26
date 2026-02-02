package org.firstinspires.ftc.teamcode.Util;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.LED;

import java.util.ArrayList;

public class LightUtil {
    private final int numLights;
    public enum LightState {
        RED,
        AMBER,
        GREEN,
        OFF
    }
    private LightState lightState;
    private final ArrayList<LED> greens = new ArrayList<>();
    private final ArrayList<LED> reds = new ArrayList<>();

    /**
     * Constructor for LightUtil obj
     * @param numLights number of lights on robot
     * @param hardwareMap the hardwaremap
     */
    public LightUtil(int numLights, HardwareMap hardwareMap){
        this.numLights = numLights;
        for(int i = 1; i <= numLights; i++){
            greens.add(hardwareMap.get(LED.class, ("green" + i)));
            reds.add(hardwareMap.get(LED.class, ("red" + i)));
        }
        lightState = LightState.OFF;
    }

    /**
     * main updating method for lights
     * to change color call one of makeColor()
     * and then run this method each loop
     */
    public void updateLights() {
        switch (lightState){
            case OFF:
                for(LED light : greens){
                    light.off();
                }
                for(LED light : reds){
                    light.off();
                }
                break;
            case RED:
                for(LED light : greens){
                    light.off();
                }
                for(LED light : reds){
                    light.on();
                }
                break;
            case AMBER:
                for(LED light : greens){
                    light.on();
                }
                for(LED light : reds){
                    light.on();
                }
                break;
            case GREEN:
                for(LED light : greens){
                    light.on();
                }
                for(LED light : reds){
                    light.off();
                }
                break;
        }
    }
    public void makeRed() {
        lightState = LightState.RED;
    }
    public void makeAmber() {
        lightState = LightState.AMBER;
    }
    public void makeGreen() {
        lightState = LightState.GREEN;
    }
    public void makeOff() {
        lightState = LightState.OFF;
    }
    public LightState getLightState() {
        return lightState;
    }
    public int getNumLights() {
        return numLights;
    }
}
