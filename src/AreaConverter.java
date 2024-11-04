import java.util.Map;

public class AreaConverter {
    private static final Map<String, Double> units = Map.of(
            "Квадратный метр", 1.0,
            "Гектар", 10000.0,
            "Квадратный километр", 1_000_000.0,
            "Квадратный сантиметр", 0.0001,
            "Квадратный миллиметр", 0.000001,
            "Акр", 4046.86
    );

    public static double convert(double value, String fromUnit, String toUnit) {
        double baseValue = value * units.get(fromUnit);
        return baseValue / units.get(toUnit);
    }

    public static String[] getUnits() {
        return units.keySet().toArray(new String[0]);
    }
}
