package org.firstinspires.ftc.teamcode.OpMode.TelOp;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;


@TeleOp(name = "Color Sensor for Yellow, Red, Blue", group = "Sensor")
public class ColorSensorDetectColors extends OpMode {

    private RevColorSensorV3 colorSensor;

    @Override
    public void init() {
        // Initialize the color sensor
        colorSensor = hardwareMap.get(RevColorSensorV3.class, "color_sensor");
        telemetry.speak("Ünknown");
        telemetry.addData("Status", "Initialized");
    }

    @Override
    public void loop() {
        // Read RGB values
        int red = colorSensor.red();
        int green = colorSensor.green();
        int blue = colorSensor.blue();

        // Determine the detected color
        String detectedColor = detectColor(red, green, blue);

        // Display values on telemetry
        telemetry.addData("Light Detected", (OpticalDistanceSensor) colorSensor);
        telemetry.addData("Red", red);
        telemetry.addData("Green", green);
        telemetry.addData("Blue", blue);
        telemetry.addData("Detected Color", detectedColor);
        telemetry.update();
    }

    // Method to determine the color based on RGB thresholds
    private String detectColor(int red, int green, int blue) {
        if ((red < 60 && red > 35) && (green < 100 && green > 65) && (blue < 90 && blue > 50)) {
            return "Floor"; // The floor, duh
        } else if (red > blue) {
            if (green > (red + 21)) {
                return "Yellow";
            } else {
                return "Red";
            }
        } else if (blue > red) {
            return "Blue"; // High blue, low red and green
        } else {
            telemetry.speak("Ünknown");
            return "Ünknown";
        }
    }
}



