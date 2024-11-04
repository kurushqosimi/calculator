import java.util.Map;

public class LengthConverter {
    private static final Map<String, Double> units = Map.ofEntries(
            Map.entry("Метр", 1.0),
            Map.entry("Километр", 1000.0),
            Map.entry("Сантиметр", 0.01),
            Map.entry("Миллиметр", 0.001),
            Map.entry("Микрометр", 0.000001),
            Map.entry("Нанометр", 0.000000001),
            Map.entry("Миля", 1609.34),
            Map.entry("Ярд", 0.9144),
            Map.entry("Фут", 0.3048),
            Map.entry("Дюйм", 0.0254),
            Map.entry("Морская миля", 1852.0),
            Map.entry("Сажень", 2.1336),
            Map.entry("Пядь", 0.2286)
    );


    public static double convert(double value, String fromUnit, String toUnit) {
        double baseValue = value * units.get(fromUnit);
        return baseValue / units.get(toUnit);
    }

    public static String[] getUnits() {
        return units.keySet().toArray(new String[0]);
    }
}
