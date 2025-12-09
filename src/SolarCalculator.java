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

        // FIX: Use ... * rooftop.getEffectiveArea() if the effective area of the passed rooftop doesn't match the area
        // of the panel.

        if (Double.compare(rooftop.getEffectiveArea(), panel.getArea()) != 0) {
            return (panel.computeRawOutput(dni) / panel.getArea()) * rooftop.getEffectiveArea();
        }

        // FIX: Remove * rooftop.getEffectiveArea() if the effective area of the passed rooftop matches the area
        // of the panel.
        // The panel is already initialized with the effective area in Main.
        // Multiplying here again (in case the values match) would result in (Area * Area).
        return panel.computeRawOutput(dni);
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