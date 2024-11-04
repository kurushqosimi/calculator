import java.util.Map;

public class MassConverter {
    private static final Map<String, Double> units = Map.ofEntries(
            Map.entry("Килограмм", 1.0),
            Map.entry("Грамм", 0.001),
            Map.entry("Тонна", 1000.0),
            Map.entry("Фунт", 0.453592),
            Map.entry("Унция", 0.0283495),
            Map.entry("Миллиграмм", 1e-6),
            Map.entry("Микрограмм", 1e-9),
            Map.entry("Центнер", 100.0),
            Map.entry("Стоун", 6.35029),
            Map.entry("Гран", 0.0000647989)
    );


    public static double convert(double value, String fromUnit, String toUnit) {
        double baseValue = value * units.get(fromUnit);
        return baseValue / units.get(toUnit);
    }

    public static String[] getUnits() {
        return units.keySet().toArray(new String[0]);
    }
}
