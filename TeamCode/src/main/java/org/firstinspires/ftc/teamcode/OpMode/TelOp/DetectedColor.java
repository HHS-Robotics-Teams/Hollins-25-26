package org.firstinspires.ftc.teamcode.OpMode.TelOp;
import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.hardware.ColorSensor;

public class DetectedColor {
    // Static variable to store the detected color
    public static String color = "Unknown"; // Default value

    // Method to update the detected color using the color sensor
    public static void updateColor(ColorSensor colorSensor) {
        // Read RGB values from the sensor
        int red = colorSensor.red();
        int green = colorSensor.green();
        int blue = colorSensor.blue();

        // Determine the color based on thresholds
        if ((red < 60 && red > 35) && (green < 100 && green > 65) && (blue < 90 && blue > 50)) {
            color = "Floor";
        } else if (red > blue) {
            if (green > (red + 21)) {
                color = "Yellow";
            } else {
                color = "Red";
            }
        } else if (blue > red) {
            color = "Blue";
        } else {
            color = "Unknown";
        }
    }

        // Getter for the detected color
        public static String getColor () {
            return color;
        }
    }




