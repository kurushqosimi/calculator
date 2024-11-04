public class TemperatureConverter {

    public static double convert(double value, String fromUnit, String toUnit) {
        if (fromUnit.equals(toUnit)) return value;

        double celsiusValue;
        switch (fromUnit) {
            case "Цельсий":
                celsiusValue = value;
                break;
            case "Фаренгейт":
                celsiusValue = (value - 32) * 5 / 9;
                break;
            case "Кельвин":
                celsiusValue = value - 273.15;
                break;
            case "Ранкин":
                celsiusValue = (value - 491.67) * 5 / 9;
                break;
            case "Ньютон":
                celsiusValue = value * 100 / 33;
                break;
            default:
                throw new IllegalArgumentException("Неподдерживаемая единица температуры");
        }

        switch (toUnit) {
            case "Цельсий":
                return celsiusValue;
            case "Фаренгейт":
                return celsiusValue * 9 / 5 + 32;
            case "Кельвин":
                return celsiusValue + 273.15;
            case "Ранкин":
                return (celsiusValue + 273.15) * 9 / 5;
            case "Ньютон":
                return celsiusValue * 33 / 100;
            default:
                throw new IllegalArgumentException("Неподдерживаемая единица температуры");
        }
    }

    public static String[] getUnits() {
        return new String[]{"Цельсий", "Фаренгейт", "Кельвин", "Ранкин", "Ньютон"};
    }

}
