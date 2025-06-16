import de.berufsschule.kasse.model.Verkauf;

public class debug_test {
    public static void main(String[] args) {
        Verkauf verkauf = new Verkauf(1, "Testprodukt", 3, 2.50);
        String result = verkauf.toString();
        System.out.println("toString result: '" + result + "'");
        System.out.println("Contains '3x': " + result.contains("3x"));
        System.out.println("Contains 'Testprodukt': " + result.contains("Testprodukt"));
        System.out.println("Contains '2,50': " + result.contains("2,50"));
        System.out.println("Contains '7,50': " + result.contains("7,50"));
        System.out.println("Contains '€': " + result.contains("€"));
    }
}
