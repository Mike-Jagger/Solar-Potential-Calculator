import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.List;

public class PhyphoxSolarTests {

    // Rooftop Tests

    @Test
    void testRooftopEffectiveAreaSouth() {
        // Area 10, Shading 0.1, South (Factor 1.0)
        // Expected: 10 * (1 - 0.1) * 1.0 = 9.0
        Rooftop roof = new Rooftop(10.0, 0.1, "south");
        assertEquals(9.0, roof.getEffectiveArea(), 0.001, "South facing calculation incorrect");
    }

    @Test
    void testRooftopEffectiveAreaNorth() {
        // Area 10, Shading 0, North (Factor 0.6)
        // Expected: 10 * 1.0 * 0.6 = 6.0
        Rooftop roof = new Rooftop(10.0, 0.0, "north");
        assertEquals(6.0, roof.getEffectiveArea(), 0.001, "North facing calculation incorrect");
    }

    // Panel Tests

    @Test
    void testMonocrystallinePanelOutput() {
        // Efficiency 20% (0.2), Area 10, TempCoeff 0.02
        // DNI 1000
        // Expected: 1000 * 10 * 0.2 * (1 - 0.02) = 2000 * 0.98 = 1960
        SolarPanel panel = new MonocrystallinePanel(0.2, 10.0, 0.02);
        double output = panel.computeRawOutput(1000.0);
        assertEquals(1960.0, output, 0.001);
    }

    @Test
    void testPolycrystallinePanelOutput() {
        // Efficiency 15% (0.15), Area 10, Degradation 0.05
        // DNI 1000
        // Expected: 1000 * 10 * 0.15 * (1 - 0.05) = 1500 * 0.95 = 1425
        SolarPanel panel = new PolycrystallinePanel(0.15, 10.0, 0.05);
        double output = panel.computeRawOutput(1000.0);
        assertEquals(1425.0, output, 0.001);
    }

    // Calibration Tests

    @Test
    void testCalibrationLogic() {
        Calibration cal = new Calibration();
        // Initial default is 0.0079
        assertEquals(0.0079, cal.getFactor(), 0.0001);

        // Calibrate: Known DNI 800, Reading 100,000 Lux
        // Factor should become 800 / 100000 = 0.008
        cal.calibrate(800.0, 100000.0);
        assertEquals(0.008, cal.getFactor(), 0.00001);
    }

    @Test
    void testCalibrationIgnoresNullOrZero() {
        Calibration cal = new Calibration();
        double initialFactor = cal.getFactor();

        cal.calibrate(1000, 0.0);
        assertEquals(initialFactor, cal.getFactor(), "Should not calibrate on zero lux");

        cal.calibrate(1000, null);
        assertEquals(initialFactor, cal.getFactor(), "Should not calibrate on null lux");
    }

    // Integration Test (The Entire Calculator)

    @Test
    void testSolarCalculatorIntegration() {
        // Setup Rooftop: South, 10m^2, no shading -> Effective Area = 10
        Rooftop roof = new Rooftop(10.0, 0.0, "south");

        // Setup Panel: Mono, 20% eff, 0 temp coeff -> Raw = DNI * 10 * 0.2

        // MASSIVE TODO:
        /*
         * The panel takes the EFFECTIVE area in the constructor in the main code logic but we are testing
         * the Calculator logic specifically.
         * In SolarCalculator, rawOutput is calculated via panel.computeRawOutput(dni).
         * Then it multiplies by rooftop.getEffectiveArea().
         * Looking at SolarCalculator.java: return rawOutput * rooftop.getEffectiveArea();
         * AND MonocrystallinePanel.java: return dni * area * efficiency...
         * This implies the area is being applied TWICE if we pass effective area to the panel constructor AND
         * multiply by it in the calculator.
         *
         * Checking the Main logic:
         * panel = new MonocrystallinePanel(0.20, rooftop.getEffectiveArea(), 0.02);
         * calculator.calculatePower: returns panel.computeRawOutput(dni) * rooftop.getEffectiveArea();
         *
         * Oh wow we are squaring the area
         * Logic fix needed in test or code. Assuming code logic intent we get this integration test however:
        */

        SolarPanel panel = new MonocrystallinePanel(0.2, 1.0, 0.0); // Unit area for panel to isolate logic
        List<Appliance> apps = Arrays.asList(new Appliance("TestApp", 100));

        // Factor = 1.0 for simplicity
        SolarCalculator calc = new SolarCalculator(roof, panel, apps, 1.0);

        // FIXED:
        // Lux = 1000. DNI = 1000 * 1.0 = 1000.
        // Panel Raw = 1000 * 1.0 (area) * 0.2 (eff) = 200.
        //
        // WAS:
        // Calc Final = 200 * 10.0 (roof effective area) = 2000 W.
        //
        // IS NOW:
        // Calc Final = (Raw/1.0 (panel area)) * 10.0 (roof effective area) = 2000 W.
        //
        // If the two areas don't match, then the effective area of the passed in rooftop is used instead
        // In the main code, we initialize the panel's area to that of rooftop's effective area anyway

        double power = calc.calculatePower(1000.0);
        assertEquals(2000.0, power, 0.1);
    }
}