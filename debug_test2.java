import de.berufsschule.kasse.model.Verkauf;
import java.nio.charset.Charset;

public class debug_test2 {
    public static void main(String[] args) {
        System.out.println("Default Charset: " + Charset.defaultCharset());
        System.out.println("File encoding: " + System.getProperty("file.encoding"));
        
        Verkauf verkauf = new Verkauf(1, "Testprodukt", 3, 2.50);
        String result = verkauf.toString();
        System.out.println("toString result: '" + result + "'");
        
        // Print each character and its unicode value
        for (int i = 0; i < result.length(); i++) {
            char c = result.charAt(i);
            System.out.println("char[" + i + "]: '" + c + "' (Unicode: " + (int)c + ")");
        }
        
        // Check if Euro symbol is actually there
        System.out.println("Contains € (direct): " + result.contains("€"));
        System.out.println("Euro symbol unicode: " + (int)'€');
    }
}
