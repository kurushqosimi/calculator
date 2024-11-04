import java.util.Map;

public class VolumeConverter {
    private static final Map<String, Double> units = Map.ofEntries(
            Map.entry("Литр", 1.0),
            Map.entry("Миллилитр", 0.001),
            Map.entry("Кубический метр", 1000.0),
            Map.entry("Галлон", 3.78541),
            Map.entry("Кубический сантиметр", 0.001),
            Map.entry("Кубический дециметр", 1.0),
            Map.entry("Кубический дюйм", 0.0163871),
            Map.entry("Кубический фут", 28.3168),
            Map.entry("Кубический ярд", 764.555)
    );


    public static double convert(double value, String fromUnit, String toUnit) {
        double baseValue = value * units.get(fromUnit);
        return baseValue / units.get(toUnit);
    }

    public static String[] getUnits() {
        return units.keySet().toArray(new String[0]);
    }
}
