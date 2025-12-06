import java.util.List;

public class SolarCalculator {
    private Rooftop rooftop;
    private SolarPanel panel;
    private List<Appliance> appliances;
    private double luxFactor;

    public SolarCalculator(Rooftop rooftop, SolarPanel panel, List<Appliance> appliances, double luxFactor) {
        this.rooftop = rooftop;
        this.panel = panel;
        this.appliances = appliances;
        this.luxFactor = luxFactor;
    }

    public double calculatePower(double lux) {
        double dni = lux * luxFactor;
        double rawOutput = panel.computeRawOutput(dni);
        return rawOutput * rooftop.getEffectiveArea();
    }

    public void matchAppliances(double power) {
        System.out.println("Available Power: " + power + " W");
        for (Appliance app : appliances) {
            if (power >= app.getWatts()) {
                int qty = (int) (power / app.getWatts());
                System.out.println(" [✓] " + app + " x " + qty);
            } else {
                System.out.println(" [ ] " + app + " - Insufficient");
            }
        }
    }
}