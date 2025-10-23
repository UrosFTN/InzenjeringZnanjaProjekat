/**
 * Glavna klasa aplikacije za preporuku i ocenjivanje filmova
 */
public class Main {
    
    /**
     * Glavna metoda - pokreće aplikaciju
     */
    public static void main(String[] args) {
        MenuManager menuManager = null;
        
        try {
            // Kreira i pokreće menu manager
            menuManager = new MenuManager();
            menuManager.showMainMenu();
            
        } catch (Exception e) {
            System.out.println("Greška pri pokretanju aplikacije: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Oslobađa resurse
            if (menuManager != null) {
                menuManager.close();
            }
        }
    }
}
