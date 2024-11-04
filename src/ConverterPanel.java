import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ConverterPanel extends JPanel {
    private JComboBox<String> conversionTypeComboBox;
    private JComboBox<String> unitFromComboBox;
    private JComboBox<String> unitToComboBox;
    private JTextField inputField;
    private JLabel resultLabel;
    private CurrencyConverter currencyConverter;

    public ConverterPanel() {
        setLayout(new GridLayout(5, 2, 5, 5));

        add(new JLabel("Тип конверсии:"));
        conversionTypeComboBox = new JComboBox<>(new String[]{"Длина", "Масса", "Объем", "Температура", "Валюта", "Площадь"});
        conversionTypeComboBox.addActionListener(e -> updateUnits());
        add(conversionTypeComboBox);

        add(new JLabel("Из:"));
        unitFromComboBox = new JComboBox<>();
        add(unitFromComboBox);

        add(new JLabel("В:"));
        unitToComboBox = new JComboBox<>();
        add(unitToComboBox);

        add(new JLabel("Значение:"));
        inputField = new JTextField();
        add(inputField);

        JButton convertButton = new JButton("Конвертировать");
        convertButton.addActionListener(new ConvertListener());
        add(convertButton);

        add(new JLabel("Результат:"));
        resultLabel = new JLabel("");
        add(resultLabel);

        currencyConverter = new CurrencyConverter();
        updateUnits();
    }

    private void updateUnits() {
        unitFromComboBox.removeAllItems();
        unitToComboBox.removeAllItems();
        String type = (String) conversionTypeComboBox.getSelectedItem();

        switch (type) {
            case "Длина":
                for (String unit : LengthConverter.getUnits()) {
                    unitFromComboBox.addItem(unit);
                    unitToComboBox.addItem(unit);
                }
                break;
            case "Масса":
                for (String unit : MassConverter.getUnits()) {
                    unitFromComboBox.addItem(unit);
                    unitToComboBox.addItem(unit);
                }
                break;
            case "Объем":
                for (String unit : VolumeConverter.getUnits()) {
                    unitFromComboBox.addItem(unit);
                    unitToComboBox.addItem(unit);
                }
                break;
            case "Температура":
                for (String unit : TemperatureConverter.getUnits()) {
                    unitFromComboBox.addItem(unit);
                    unitToComboBox.addItem(unit);
                }
                break;
            case "Валюта":
                for (String unit : currencyConverter.getCurrencyCodes()) {
                    unitFromComboBox.addItem(unit);
                    unitToComboBox.addItem(unit);
                }
                break;
            case "Площадь":
                for (String unit : AreaConverter.getUnits()) {
                    unitFromComboBox.addItem(unit);
                    unitToComboBox.addItem(unit);
                }
                break;
        }
    }

    private class ConvertListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String type = (String) conversionTypeComboBox.getSelectedItem();
            String fromUnit = (String) unitFromComboBox.getSelectedItem();
            String toUnit = (String) unitToComboBox.getSelectedItem();
            double inputValue;
            try {
                inputValue = Double.parseDouble(inputField.getText());
            } catch (NumberFormatException ex) {
                resultLabel.setText("Ошибка ввода");
                return;
            }

            double result = 0;
            switch (type) {
                case "Длина":
                    result = LengthConverter.convert(inputValue, fromUnit, toUnit);
                    break;
                case "Масса":
                    result = MassConverter.convert(inputValue, fromUnit, toUnit);
                    break;
                case "Объем":
                    result = VolumeConverter.convert(inputValue, fromUnit, toUnit);
                    break;
                case "Температура":
                    result = TemperatureConverter.convert(inputValue, fromUnit, toUnit);
                    break;
                case "Валюта":
                    result = currencyConverter.convert(inputValue, fromUnit, toUnit);
                    break;
                case "Площадь":
                    result = AreaConverter.convert(inputValue, fromUnit, toUnit);
                    break;
            }
            resultLabel.setText(String.format("%.2f", result));
        }
    }
}
