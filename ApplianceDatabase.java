import java.util.Arrays;
import java.util.List;

public class ApplianceDatabase {
    public static List<Appliance> getAppliances() {
        return Arrays.asList(
            new Appliance("LED Light Bulb", 9),
            new Appliance("Phone Charger", 15),
            new Appliance("Wi-Fi Router", 20),
            new Appliance("Laptop Charging", 65),
            new Appliance("Ceiling Fan", 75),
            new Appliance("LED TV (42 inch)", 100),
            new Appliance("Desktop PC", 250),
            new Appliance("Coffee Maker", 800),
            new Appliance("Microwave", 1000)
        );
    }
}