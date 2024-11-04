import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.StringTokenizer;
import java.math.BigInteger;

public class MultiModeCalculator {
    private JFrame frame;
    private JTextField inputField;
    private JComboBox<String> modeComboBox;
    private JPanel modePanel;
    private boolean isSecondFunction = false;
    // Добавляем поле для выбора системы счисления
    private JComboBox<Integer> baseComboBox;
    private Map<Integer, JLabel> baseLabels = new HashMap<>();
    private JPanel displayPanel;


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MultiModeCalculator().createAndShowGUI());
    }

    private void createAndShowGUI() {
        frame = new JFrame("Multi-Mode Calculator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 600);

        inputField = new JTextField();
        inputField.setHorizontalAlignment(JTextField.RIGHT);
        inputField.setFont(new Font("Arial", Font.PLAIN, 24));
        inputField.setEditable(true);
        inputField.setPreferredSize(new Dimension(400, 60));
        frame.add(inputField, BorderLayout.NORTH);

        modeComboBox = new JComboBox<>(new String[]{"Обычный", "Инженерный", "Система счисления", "Конвертер"});
        modeComboBox.addActionListener(new ModeChangeListener());
        frame.add(modeComboBox, BorderLayout.SOUTH);

        modePanel = new JPanel();
        frame.add(modePanel, BorderLayout.CENTER);
        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != '+' && c != '-' && c != '*' && c != '/' && c != '.' && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_ENTER && c != '(' && c != ')') {
                    e.consume(); // Игнорируем все символы, кроме перечисленных
                }
            }
        });

        baseComboBox = new JComboBox<>();
        for (int i = 2; i <= 16; i++) {
            baseComboBox.addItem(i);
        }
        baseComboBox.addActionListener(e -> updateBaseMode());
        baseComboBox.setVisible(false);
        frame.add(baseComboBox, BorderLayout.EAST);

        // Панель для отображения значений в разных системах счисления
        displayPanel = new JPanel(new GridLayout(0, 1));
        for (int base = 2; base <= 16; base++) {
            JLabel label = new JLabel("Base " + base + ": ");
            baseLabels.put(base, label);
            displayPanel.add(label);
        }

        displayPanel.setVisible(false); // Изначально скрываем панель
        frame.add(displayPanel, BorderLayout.WEST);

        updateModePanel("Обычный");

        frame.setVisible(true);
    }

    private void updateBaseMode() {
        // Определяем новую выбранную систему счисления
        int selectedBase = (int) baseComboBox.getSelectedItem();

        // Получаем текущее значение из inputField
        String input = inputField.getText().trim();

        // Проверка, если в поле ввода что-то есть и текущая система отличается от новой
        if (!input.isEmpty() && previousBase != selectedBase) {
            try {
                // Проверяем, есть ли оператор
                int operatorIndex = findLastOperatorIndex(input);

                if (operatorIndex == -1) {
                    // Если операторов нет, просто конвертируем значение
                    BigInteger decimalValue = new BigInteger(input, previousBase);
                    String newBaseValue = decimalValue.toString(selectedBase).toUpperCase();
                    inputField.setText(newBaseValue);
                } else {
                    // Если оператор есть, разбиваем строку на число и оператор
                    String numberPart = input.substring(0, operatorIndex).trim();
                    String operatorPart = input.substring(operatorIndex).trim();

                    BigInteger decimalValue = new BigInteger(numberPart, previousBase);
                    String newBaseValue = decimalValue.toString(selectedBase).toUpperCase();

                    inputField.setText(newBaseValue + " " + operatorPart);
                }
            } catch (NumberFormatException e) {
                inputField.setText("Ошибка"); // Если значение не соответствует предыдущей системе, выводим ошибку
            }
        }

        // Обновляем состояние кнопок и меток для новой системы счисления
        enableButtonsForBase(selectedBase);
        updateBaseLabels();

        // Обновляем previousBase, чтобы отслеживать текущую выбранную систему
        previousBase = selectedBase;
    }

    // Метод для нахождения последнего оператора
    private int findLastOperatorIndex(String input) {
        int index = -1;
        String operators = "+-*/";
        for (char op : operators.toCharArray()) {
            int opIndex = input.lastIndexOf(op);
            if (opIndex > index) {
                index = opIndex;
            }
        }
        return index;
    }

    private int previousBase = 10;



    private void enableButtonsForBase(int base) {
        for (Component comp : modePanel.getComponents()) {
            if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                String text = button.getText();

                // Включаем только нужные буквы в зависимости от выбранной системы счисления
                if (isNumeric(text)) {
                    int value = Integer.parseInt(text);
                    button.setEnabled(value < base);
                } else if (text.equals("A")) {
                    button.setEnabled(base > 10);
                } else if (text.equals("B")) {
                    button.setEnabled(base > 11);
                } else if (text.equals("C")) {
                    button.setEnabled(base > 12);
                } else if (text.equals("D")) {
                    button.setEnabled(base > 13);
                } else if (text.equals("E")) {
                    button.setEnabled(base > 14);
                } else if (text.equals("F")) {
                    button.setEnabled(base > 15);
                } else if (text.equals("CE") || text.equals("⌫")) {
                    button.setEnabled(true); // Кнопки "CE" и "⌫" всегда включены
                } else {
                    button.setEnabled(true); // Оставляем операторы включёнными
                }
            }
        }
    }


    private boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void updateBaseLabels() {
        // Получаем значение из inputField и конвертируем его в разные системы счисления
        try {
            String input = inputField.getText();
            if (input.isEmpty()) return;

            int selectedBase = (int) baseComboBox.getSelectedItem();

            // Пробуем сконвертировать введенное значение в десятичное число
            int decimalValue;
            try {
                decimalValue = Integer.parseInt(input, selectedBase);
            } catch (NumberFormatException e) {
                // Если ввод недопустим для выбранной системы счисления, показываем ошибку
                for (JLabel label : baseLabels.values()) {
                    label.setText("Ошибка");
                }
                return;
            }

            // Обновляем каждую метку с конвертацией в разные системы счисления
            for (int base = 2; base <= 16; base++) {
                try {
                    String baseRepresentation = Integer.toString(decimalValue, base).toUpperCase();
                    baseLabels.get(base).setText("Base " + base + ": " + baseRepresentation);
                } catch (NumberFormatException e) {
                    // Устанавливаем сообщение об ошибке для конкретной системы счисления
                    baseLabels.get(base).setText("Ошибка");
                }
            }
        } catch (Exception e) {
            // Обрабатываем любую другую неожиданную ошибку
            for (JLabel label : baseLabels.values()) {
                label.setText("Ошибка");
            }
        }
    }

    // Добавление слушателя для кнопок
    private class BaseCalculatorListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            try {
                switch (command) {
                    case "=" -> handleBaseEvaluation();
                    case "+" -> inputField.setText(inputField.getText() + " + ");
                    case "-" -> inputField.setText(inputField.getText() + " - ");
                    case "*" -> inputField.setText(inputField.getText() + " * ");
                    case "/" -> inputField.setText(inputField.getText() + " / ");
                    case "+/-" -> toggleSign();
                    case "CE" -> inputField.setText("");
                    case "⌫" -> handleBackspace();
                    default -> inputField.setText(inputField.getText() + command);
                }
            } catch (Exception ex) {
                inputField.setText("Ошибка");
            }
        }

        private void toggleSign() {
            String text = inputField.getText().trim();
            if (text.isEmpty()) {
                return;
            }

            // Проверяем, есть ли уже знак "-"
            if (text.startsWith("-")) {
                // Удаляем знак "-" для положительного значения
                inputField.setText(text.substring(1));
            } else {
                // Добавляем знак "-" для отрицательного значения
                inputField.setText("-" + text);
            }
        }

        private void handleBaseEvaluation() {
            String expression = formatExpression(inputField.getText().trim());
            if (expression.isEmpty()) {
                inputField.setText("Ошибка");
                return;
            }
            try {
                int selectedBase = (int) baseComboBox.getSelectedItem();
                Stack<BigInteger> values = new Stack<>();
                Stack<String> operators = new Stack<>();

                String[] tokens = expression.split(" ");
                for (String token : tokens) {
                    if (isNumeric(token, selectedBase)) {
                        values.push(parseBase(token, selectedBase));  // Конвертируем операнд в десятичное значение
                    } else if (token.equals("(")) {
                        operators.push(token);  // Скобку добавляем в стек операторов
                    } else if (token.equals(")")) {
                        // Выполняем операции до тех пор, пока не найдем открывающую скобку
                        while (!operators.isEmpty() && !operators.peek().equals("(")) {
                            values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
                        }
                        operators.pop(); // Удаляем открывающую скобку из стека
                    } else if (isOperator(token)) {
                        // Обрабатываем оператор с учетом приоритета
                        while (!operators.isEmpty() && precedence(token) <= precedence(operators.peek())) {
                            values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
                        }
                        operators.push(token);
                    }
                }

                // Выполняем оставшиеся операции
                while (!operators.isEmpty()) {
                    values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
                }

                BigInteger result = values.pop();

                // Конвертируем результат обратно в выбранную систему счисления
                inputField.setText(formatResultForBase(result, selectedBase));
                updateBaseLabels();
            } catch (Exception e) {
                inputField.setText("Ошибка");
            }
        }

        // Функция для добавления пробелов вокруг операторов и скобок
        private String formatExpression(String expression) {
            return expression.replaceAll("([+\\-*/()<>]{1,2})", " $1 ");
        }

        // Функция для проверки, является ли строка числом в заданной системе счисления
        private boolean isNumeric(String str, int base) {
            try {
                new BigInteger(str, base);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        // Функция для применения оператора к двум значениям
        private BigInteger applyOperator(String operator, BigInteger b, BigInteger a) {
            return switch (operator) {
                case "+" -> a.add(b);
                case "-" -> a.subtract(b);
                case "*" -> a.multiply(b);
                case "/" -> {
                    if (b.equals(BigInteger.ZERO)) throw new ArithmeticException("Деление на ноль");
                    yield a.divide(b);
                }
                case "<<" -> a.shiftLeft(b.intValue());  // Побитовый сдвиг влево
                case ">>" -> a.shiftRight(b.intValue()); // Побитовый сдвиг вправо
                default -> throw new IllegalArgumentException("Неверный оператор: " + operator);
            };
        }

        // Функция для проверки, является ли строка оператором
        private boolean isOperator(String token) {
            return token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/") || token.equals("<<") || token.equals(">>");
        }

        // Приоритет операторов
        private int precedence(String operator) {
            return switch (operator) {
                case "+", "-" -> 1;
                case "*", "/" -> 2;
                case "<<", ">>" -> 3; // Устанавливаем приоритет для побитовых операторов
                default -> -1;
            };
        }




        private BigInteger parseBase(String value, int base) {
            return new BigInteger(value, base);
        }

        private String formatResultForBase(BigInteger value, int base) {
            return value.toString(base).toUpperCase();
        }

        private void handleBackspace() {
            String currentText = inputField.getText();
            if (!currentText.isEmpty()) {
                inputField.setText(currentText.substring(0, currentText.length() - 1));
            }
        }
    }

    private void addBaseConversionButtons() {
        String[] buttons = {"A", "<<", ">>", "CE", "⌫", "B", "(", ")", "%", "/", "C", "7", "8", "9", "*", "D", "4", "5", "6", "-", "E", "1", "2", "3", "+", "F", "", "0", ".", "="};
        BaseCalculatorListener listener = new BaseCalculatorListener();

        for (String text : buttons) {
            JButton button = new JButton(text);
            button.addActionListener(listener); // Назначаем слушатель
            button.setEnabled(isButtonValidForBase(text, (int) baseComboBox.getSelectedItem())); // Устанавливаем видимость кнопок на основе системы счисления
            modePanel.add(button);
        }
    }

    private boolean isButtonValidForBase(String text, int base) {
        // Проверяем, включена ли кнопка в зависимости от текущей системы счисления
        if (text.matches("[A-F]")) {
            int value = text.charAt(0) - 'A' + 10;
            return base > value;
        }
        if (text.matches("[0-9]")) {
            int value = Integer.parseInt(text);
            return value < base;
        }
        return true;
    }


    private void updateModePanel(String mode) {
        modePanel.removeAll();

        switch (mode) {
            case "Обычный":
                modePanel.setLayout(new GridLayout(6, 4));
                addStandardButtons();
                displayPanel.setVisible(false);
                baseComboBox.setVisible(false);
                break;
            case "Инженерный":
                modePanel.setLayout(new GridLayout(7, 5));
                addSсientificButtons();
                displayPanel.setVisible(false);
                baseComboBox.setVisible(false);
                break;
            case "Система счисления":
                modePanel.setLayout(new GridLayout(6, 5));
                addBaseConversionButtons();
                displayPanel.setVisible(true);
                baseComboBox.setVisible(true);
                baseComboBox.setSelectedItem(10);
                enableButtonsForBase(10);
                break;
            case "Конвертер":
                displayPanel.setVisible(false);
                baseComboBox.setVisible(false);
                modePanel.setLayout(new BorderLayout());
                modePanel.add(new ConverterPanel());
                break;
        }

        modePanel.revalidate();
        modePanel.repaint();
    }

    private void addStandardButtons() {
        String[] buttons = {"%", "CE", "C", "⌫", "1/x", "x^2", "√ₓ", "/", "7", "8", "9", "*", "4", "5", "6", "-", "1", "2", "3", "+", "±", "0", ".", "="};
        for (String text : buttons) {
            JButton button = new JButton(text);
            button.addActionListener(new StandardCalculatorListener());
            modePanel.add(button);
        }
    }

    private void addSсientificButtons() {
        String[] buttons = {"2nd", "π", "e", "C", "⌫", "x^2", "1/x", "|x|", "exp", "mod", "√ₓ", "(", ")", "n!", "÷", "x^y", "7", "8", "9", "×", "10^x", "4", "5", "6", "-", "log", "1", "2", "3", "+", "ln", "+/-", "0", ".", "="};
        for (String text : buttons) {
            JButton button = new JButton(text);
            button.addActionListener(new ScientificCalculatorListener());
            modePanel.add(button);
        }
    }

    private void addConverterButtons() {
        String[] buttons = {"Length", "Mass", "Volume", "Temperature", "Currency", "Area", "Convert"};
        for (String text : buttons) {
            JButton button = new JButton(text);
            button.addActionListener(new ConverterListener());
            modePanel.add(button);
        }
    }

    private class ModeChangeListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String selectedMode = (String) modeComboBox.getSelectedItem();
            assert selectedMode != null;
            updateModePanel(selectedMode);
        }
    }

    private class StandardCalculatorListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            try {
                switch (command) {
                    case "=" -> handleEvaluation();
                    case "CE", "C" -> inputField.setText("");
                    case "⌫" -> handleBackspace();
                    case "1/x" -> handleReciprocal();
                    case "x^2" -> handleSquare();
                    case "√ₓ" -> handleSquareRoot();
                    case "±" -> handleNegate();
                    case "%" -> handlePercentage();
                    default -> inputField.setText(inputField.getText() + command);
                }
            } catch (Exception ex) {
                inputField.setText("Ошибка");
            }
        }

        private void handleEvaluation() {
            String expression = inputField.getText().trim();

            if (!expression.isEmpty() && !Character.isDigit(expression.charAt(0))) {
                expression = "0" + expression;
            }

            if (expression.isEmpty()) {
                inputField.setText("Ошибка");
                return;
            }
            BigDecimal result = evaluateExpression(expression);
            inputField.setText(result.stripTrailingZeros().toPlainString());
        }

        private void handleBackspace() {
            String currentText = inputField.getText();
            if (!currentText.isEmpty()) {
                inputField.setText(currentText.substring(0, currentText.length() - 1));
            }
        }

        private void handleReciprocal() {
            BigDecimal value = new BigDecimal(inputField.getText());
            if (value.compareTo(BigDecimal.ZERO) == 0) {
                inputField.setText("Ошибка");
            } else {
                inputField.setText(BigDecimal.ONE.divide(value, 10, BigDecimal.ROUND_HALF_UP).toPlainString());
            }
        }

        private void handleSquare() {
            BigDecimal value = new BigDecimal(inputField.getText());
            inputField.setText(value.multiply(value).toPlainString());
        }

        private void handleSquareRoot() {
            BigDecimal value = new BigDecimal(inputField.getText());
            if (value.compareTo(BigDecimal.ZERO) < 0) {
                inputField.setText("Ошибка");
            } else {
                inputField.setText(String.valueOf(Math.sqrt(value.doubleValue())));
            }
        }

        private void handleNegate() {
            if (!inputField.getText().isEmpty()) {
                BigDecimal value = new BigDecimal(inputField.getText());
                inputField.setText(value.negate().toPlainString());
            }
        }

        private void handlePercentage() {
            BigDecimal value = new BigDecimal(inputField.getText());
            inputField.setText(value.divide(BigDecimal.valueOf(100), 10, BigDecimal.ROUND_HALF_UP).toPlainString());
        }
    }

    private class ScientificCalculatorListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();

            try {
                switch (command) {
                    case "2nd":
                        toggleSecondFunction();
                        break;
                    case "C":
                        inputField.setText("");
                        break;
                    case "⌫":
                        String text = inputField.getText();
                        if (!text.isEmpty()) {
                            inputField.setText(text.substring(0, text.length() - 1));
                        }
                        break;
                    case "π":
                        inputField.setText(inputField.getText() + Math.PI);
                        break;
                    case "e":
                        inputField.setText(inputField.getText() + Math.E);
                        break;
                    case "1/x":
                        handleReciprocal();
                        break;
                    case "|x|":
                        double absValue = Math.abs(Double.parseDouble(inputField.getText()));
                        inputField.setText(String.valueOf(absValue));
                        break;
                    case "exp":
                        double expValue = Math.exp(Double.parseDouble(inputField.getText()));
                        inputField.setText(String.valueOf(expValue));
                        break;
                    case "√ₓ":
                        handleSquareRoot();
                        break;
                    case "mod":
                        inputField.setText(inputField.getText() + " mod ");
                        break;
                    case "n!":
                        handleFactorial();
                        break;
                    case "x^2":
                        double squared = Math.pow(Double.parseDouble(inputField.getText()), 2);
                        inputField.setText(String.valueOf(squared));
                        break;
                    case "x^y":
                        inputField.setText(inputField.getText() + "^");
                        break;
                    case "x^3":  // Альтернативная функция для x^2
                        double cubed = Math.pow(Double.parseDouble(inputField.getText()), 3);
                        inputField.setText(String.valueOf(cubed));
                        break;
                    case "√x":
                        double sqrt = Math.sqrt(Double.parseDouble(inputField.getText()));
                        inputField.setText(String.valueOf(sqrt));
                        break;
                    case "3√x":  // Альтернативная функция для √x
                        double cubeRoot = Math.cbrt(Double.parseDouble(inputField.getText()));
                        inputField.setText(String.valueOf(cubeRoot));
                        break;
                    case "10^x":
                        double tenPowerX = Math.pow(10, Double.parseDouble(inputField.getText()));
                        inputField.setText(String.valueOf(tenPowerX));
                        break;
                    case "2^x":  // Альтернативная функция для 10^x
                        double twoPowerX = Math.pow(2, Double.parseDouble(inputField.getText()));
                        inputField.setText(String.valueOf(twoPowerX));
                        break;
                    case "log":
                        double logValue = Math.log10(Double.parseDouble(inputField.getText()));
                        inputField.setText(String.valueOf(logValue));
                        break;
                    case "log₂":  // Альтернативная функция для log
                        double logBase2 = Math.log(Double.parseDouble(inputField.getText())) / Math.log(2);
                        inputField.setText(String.valueOf(logBase2));
                        break;
                    case "ln":
                        double lnValue = Math.log(Double.parseDouble(inputField.getText()));
                        inputField.setText(String.valueOf(lnValue));
                        break;
                    case "e^x":  // Альтернативная функция для ln
                        double expValue2 = Math.exp(Double.parseDouble(inputField.getText()));
                        inputField.setText(String.valueOf(expValue2));
                        break;
                    case "+/-":
                        double negation = -Double.parseDouble(inputField.getText());
                        inputField.setText(String.valueOf(negation));
                        break;
                    case "=":
                        handleEvaluation();
                        break;
                    default:
                        inputField.setText(inputField.getText() + command);
                }
            } catch (Exception ex) {
                inputField.setText("Ошибка");
            }
        }

        private void handleEvaluation() {

            String expression = inputField.getText().trim();

            if (!expression.isEmpty() && !Character.isDigit(expression.charAt(0))) {
                expression = "0" + expression;
            }

            if (expression.isEmpty()) {
                inputField.setText("Ошибка");
                return;
            }
            try {
                if (expression.contains("^")) {
                    String[] parts = expression.split("\\^");
                    if (parts.length == 2) {
                        BigDecimal base = new BigDecimal(parts[0].trim());
                        int exponent = Integer.parseInt(parts[1].trim());
                        BigDecimal result = base.pow(exponent);
                        inputField.setText(result.stripTrailingZeros().toPlainString());
                    } else {
                        inputField.setText("Ошибка");
                    }
                } else {
                    // First, replace certain tokens to standardize the input format.
                    expression = expression.replace("^", "**") // Custom power operator representation
                            .replace("mod", "mod") // Replacing mod with Java modulus operator
                            .replace("ln", "Math.log") // Natural logarithm
                            .replace("log", "Math.log10") // Base-10 logarithm
                            .replace("π", String.valueOf(Math.PI)).replace("e", String.valueOf(Math.E));

                    // The following function is built to handle all standard mathematical operations.
                    BigDecimal result = evaluateExpression(expression);
                    inputField.setText(result.stripTrailingZeros().toPlainString());
                }
            } catch (Exception ex) {
                inputField.setText("Ошибка");
            }
        }


        private void handleMod() {
            String[] parts = inputField.getText().split("mod");
            if (parts.length == 2) {
                try {
                    BigDecimal a = new BigDecimal(parts[0].trim());
                    BigDecimal b = new BigDecimal(parts[1].trim());
                    BigDecimal result = a.remainder(b);
                    inputField.setText(result.stripTrailingZeros().toPlainString());
                } catch (NumberFormatException ex) {
                    inputField.setText("Ошибка");
                }
            } else {
                inputField.setText("Ошибка");
            }
        }

        private void handleFactorial() {
            try {
                int value = Integer.parseInt(inputField.getText());
                if (value < 0) {
                    inputField.setText("Ошибка");
                } else {
                    int result = factorial(value);
                    inputField.setText(String.valueOf(result));
                }
            } catch (NumberFormatException ex) {
                inputField.setText("Ошибка");
            }
        }

        private void handleReciprocal() {
            BigDecimal value = new BigDecimal(inputField.getText());
            if (value.compareTo(BigDecimal.ZERO) == 0) {
                inputField.setText("Ошибка");
            } else {
                inputField.setText(BigDecimal.ONE.divide(value, 10, BigDecimal.ROUND_HALF_UP).toPlainString());
            }
        }

        private void handleSquareRoot() {
            BigDecimal value = new BigDecimal(inputField.getText());
            if (value.compareTo(BigDecimal.ZERO) < 0) {
                inputField.setText("Ошибка");
            } else {
                inputField.setText(String.valueOf(Math.sqrt(value.doubleValue())));
            }
        }
    }

    private BigDecimal evaluateExpression(String expression) {
        try {
            expression = expression.replace("%", "/100");
            expression = expression.replace("π", String.valueOf(Math.PI)).replace("e", String.valueOf(Math.E)).replace("^", "**"); // Используем как степень
            return new BigDecimal(eval(expression));
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    private int factorial(int n) {
        if (n < 0) throw new IllegalArgumentException("Факториал не определен для отрицательных чисел");
        int result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }

    private String eval(String expression) {
        try {
            String rpn = toRPN(expression); // Шаг 1: Преобразуем выражение в обратную польскую запись
            BigDecimal result = evaluateRPN(rpn); // Шаг 2: Вычисляем RPN
            return result.toString();
        } catch (Exception e) {
            return "Ошибка";
        }
    }

    // Шаг 1: Преобразование в обратную польскую запись
    private String toRPN(String expression) {
        StringBuilder output = new StringBuilder();
        Stack<String> operators = new Stack<>();
        StringTokenizer tokens = new StringTokenizer(expression, "+-*/() ", true);
        boolean previousWasOperator = true; // Флаг для определения, идет ли `-` после оператора или в начале

        while (tokens.hasMoreTokens()) {
            String token = tokens.nextToken().trim();
            if (token.isEmpty()) continue;

            if (isNumber(token)) {
                output.append(token).append(" ");
                previousWasOperator = false;
            } else if (token.equals("(")) {
                operators.push(token);
                previousWasOperator = true;
            } else if (token.equals(")")) {
                while (!operators.isEmpty() && !operators.peek().equals("(")) {
                    output.append(operators.pop()).append(" ");
                }
                operators.pop(); // Удаляем "(" из стека
                previousWasOperator = false;
            } else if (token.equals("-") && previousWasOperator) {
                // Если "-" стоит после оператора или в начале, считаем его унарным
                output.append("u-").append(" "); // Используем "u-" для унарного минуса
            } else if (isOperator(token)) {
                while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(token)) {
                    output.append(operators.pop()).append(" ");
                }
                operators.push(token);
                previousWasOperator = true;
            }
        }

        while (!operators.isEmpty()) {
            output.append(operators.pop()).append(" ");
        }

        return output.toString().trim();
    }

    // Шаг 2: Вычисление выражения в обратной польской записи
    private BigDecimal evaluateRPN(String rpn) {
        Stack<BigDecimal> stack = new Stack<>();
        StringTokenizer tokens = new StringTokenizer(rpn);

        while (tokens.hasMoreTokens()) {
            String token = tokens.nextToken();
            if (isNumber(token)) {
                stack.push(new BigDecimal(token));
            } else if (isOperator(token)) {
                BigDecimal b = stack.pop();
                BigDecimal a = stack.pop();
                switch (token) {
                    case "+":
                        stack.push(a.add(b));
                        break;
                    case "-":
                        stack.push(a.subtract(b));
                        break;
                    case "*":
                        stack.push(a.multiply(b));
                        break;
                    case "/":
                        stack.push(a.divide(b, 10, BigDecimal.ROUND_HALF_UP)); // округление для точности
                        break;
                    case "**":
                        stack.push(a.pow(b.intValue())); // Power operation
                        break;
                    case "mod":
                        stack.push(a.remainder(b)); // Use remainder to calculate modulus
                        break;
                    case "%":
                        stack.push(a.remainder(b)); // Modulus
                        break;
                }
            }
        }

        return stack.pop();
    }

    // Проверка, является ли строка числом
    private boolean isNumber(String token) {
        try {
            new BigDecimal(token);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Проверка, является ли строка оператором
    private boolean isOperator(String token) {
        return token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/") || token.equals("mod");
    }

    // Приоритет операторов
    private int precedence(String operator) {
        switch (operator) {
            case "+":
            case "-":
                return 1;
            case "*":
            case "/":
            case "mod":
                return 2;
            default:
                return -1;
        }
    }

    private void toggleSecondFunction() {
        // Переключаем состояние 2nd функции
        isSecondFunction = !isSecondFunction;

        // Проходим по всем кнопкам и изменяем текст на альтернативный или исходный
        for (Component comp : modePanel.getComponents()) {
            if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                String text = button.getText();

                // Меняем текст на кнопках в зависимости от состояния 2nd
                switch (text) {
                    case "x^2":
                        button.setText(isSecondFunction ? "x^3" : "x^2");
                        break;
                    case "x^3":
                        button.setText(isSecondFunction ? "x^3" : "x^2");
                        break;
                    case "√x":
                        button.setText(isSecondFunction ? "3√x" : "√x");
                        break;
                    case "3√x":
                        button.setText(isSecondFunction ? "3√x" : "√x");
                        break;
                    case "10^x":
                        button.setText(isSecondFunction ? "2^x" : "10^x");
                        break;
                    case "2^x":
                        button.setText(isSecondFunction ? "2^x" : "10^x");
                        break;
                    case "log":
                        button.setText(isSecondFunction ? "log₂" : "log");
                        break;
                    case "log₂":
                        button.setText(isSecondFunction ? "log₂" : "log");
                        break;
                    case "ln":
                        button.setText(isSecondFunction ? "e^x" : "ln");
                        break;
                    case "e^x":
                        button.setText(isSecondFunction ? "e^x" : "ln");
                        break;
                }
            }
        }

        // Перерисовываем панель для отображения изменений
        modePanel.revalidate();
        modePanel.repaint();
    }


    private class ConverterListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Logic for conversions (length, mass, volume, temperature, currency, area)
            // Placeholder for implementation
        }
    }
}
