import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class CurrencyConverter {
    private final Map<String, Double> currencyRates = new HashMap<>();

    public CurrencyConverter() {
        loadRates();
    }

    public void loadRates() {
        try {
            URL url = new URL("https://nbt.tj/tj/kurs/export_xml.php?date=2024-11-04&export=xmlout");
            InputStream inputStream = url.openStream();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            NodeList valutes = document.getElementsByTagName("Valute");
            for (int i = 0; i < valutes.getLength(); i++) {
                Element valute = (Element) valutes.item(i);
                String charCode = valute.getElementsByTagName("CharCode").item(0).getTextContent();
                double rate = Double.parseDouble(valute.getElementsByTagName("Value").item(0).getTextContent());
                int nominal = Integer.parseInt(valute.getElementsByTagName("Nominal").item(0).getTextContent());
                currencyRates.put(charCode, rate / nominal);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public double convert(double amount, String fromCurrency, String toCurrency) {
        if (!currencyRates.containsKey(fromCurrency) || !currencyRates.containsKey(toCurrency)) {
            return 0;
        }
        double fromRate = currencyRates.get(fromCurrency);
        double toRate = currencyRates.get(toCurrency);
        return amount * fromRate / toRate;
    }

    public String[] getCurrencyCodes() {
        return currencyRates.keySet().toArray(new String[0]);
    }
}
