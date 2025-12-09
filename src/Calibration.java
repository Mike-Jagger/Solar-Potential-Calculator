public class Calibration {
    private double factor = 0.0079; // default lux → W/m^2

    public void calibrate(double referenceDni, Double lux) {
        if (lux != null && lux > 0) {
            factor = referenceDni / lux;
            System.out.println("Calibration complete. Factor = " + factor);
        }
    }

    public double getFactor() {
        return factor;
    }
}