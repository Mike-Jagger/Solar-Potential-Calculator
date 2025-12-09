PHYPHOX SOLAR IRRADIANCE MONITOR

1. OVERVIEW

This application serves as a bridge between your smartphone's light sensor and
solar energy estimation. It connects to the Phyphox app (Physical Phone Experiments)
over your local Wi-Fi network to retrieve live illuminance data (Lux).

Using this live data, the application:
    1. Converts Lux to Solar Irradiance (W/m²) using calibration logic.
    2. Simulates power generation based on specific rooftop conditions.
    3. Models different solar panel technologies (Monocrystalline vs Polycrystalline).
    4. Matches the generated power against a database of household appliances.

2. PREREQUISITES

Make sure to have the following prerequisites:
    - Java Development Kit (JDK): Version 8 or higher.
    - Phyphox App: Installed on your Android or iOS device.
        - Android: https://play.google.com/store/apps/details?id=de.rwth_aachen.phyphox
        - iOS: https://www.google.com/search?q=https://itunes.apple.com/us/app/phyphox/id1127914602
    - Network: Your computer and smartphone must be connected to the SAME Wi-Fi network.

3. COMPILATION INSTRUCTIONS

The project relies on standard Java libraries with no external JAR dependencies.
    1. Open your terminal or command prompt.
    2. Navigate to the folder containing the .java source files.
    3. Run the following command to compile all classes:
        javac *.java

4. RUNNING THE APPLICATION

Once compiled, you can start the monitor using the main class.
    1. On your Smartphone:
        a. Open Phyphox.
        b. Select the "Light" experiment.
        c. Tap the menu (three dots) -> "Enable/Allow Remote Access".
        d. Note the URL displayed at the bottom (e.g., http://192.168.1.10:8080).

    2. On your Computer (Terminal) Run the following command:
        java PhyphoxSolarMonitor

    3. Follow the On-Screen Prompts:
        - Enter the URL from your phone.
        - Configure your virtual rooftop (area, shading, orientation).
        - Select your panel technology (Polycrystalline or Mono crystalline).
        - Choose a calibration method (Standard is easiest, Reference is more accurate).
        - Live updates on which appliances can be powered should update as you fiddle with your phone's selfie camera
          by placing your hand on sensor to simulate clouds or shading

5. PROJECT STRUCTURE & CLASS DESCRIPTIONS

The project is refactored into modular classes to separate concerns:
    A. Main Entry Point
        - PhyphoxSolarMonitor.java: The "Brain" of the app. It handles user input, initializes the components, and runs the
        main loop that fetches data and updates the display.

    B. Hardware Abstraction
        - PhyphoxClient.java: Handles the networking. It performs HTTP GET requests to the phone and parses the JSON
        response to extract the Lux value.
        - SolarPanel.java (Abstract): Defines the base structure for a solar panel.
        - MonocrystallinePanel.java: High-efficiency panel model with temperature coefficients.
        - PolycrystallinePanel.java: Lower-efficiency panel model with degradation factors.

    C. Environment Modeling
        - Rooftop.java: Calculates the "Effective Area" of your roof based on orientation (South/North/East/West) and shading.
        - Calibration.java: Manages the conversion factor (k) to translate Illuminance (Lux) into Irradiance (W/m²).

    D. Logic & Data
        - SolarCalculator.java: The calculation engine. It combines sensor data, rooftop data, and panel data to output the
        final Wattage.
        - Appliance.java: A simple data structure representing a household device.
        - ApplianceDatabase.java: A static repository of common appliances used to visualize what the power output actually
        means (e.g., "1x TV").

6. TROUBLESHOOTING

In case you encounter the following errors (or similar) please make sure to follow the steps outlined to troubleshoot or
contact the dev team:
    - "Connection Refused" / "Host Unreachable":
        1. Ensure phone and PC are on the same Wi-Fi.
        2. Ensure the screen on the phone is ON (some phones cut Wi-Fi when locked).
        3. Check if your firewall is blocking Java.
        4. In case the above don't work try to hotspot your computer using your phone, then re-run the app (works for some
        users).

    - "Received null data from sensor":
        1. The Phyphox app might be paused. Press the "Play" button in the app.
        2. Ensure you are using the "Light" experiment specifically.
        3. MAKE SURE NOT TO LEAVE THE APP DURING THE EXPERIMENT.

    - "Power Output is 0":
        1. Ensure you aren't covering the sensor (located on your phone's selfie camera).
        2. Check if Shading Factor was set to 1.0 (100% shade) and reduce to a reasonable amount if that's the case.
        3. Ensure it is daytime (or that there is proper lighting in the room for initial testing)!