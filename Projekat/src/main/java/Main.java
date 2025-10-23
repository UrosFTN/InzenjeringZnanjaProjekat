public class Main {
    
    public static void main(String[] args) {
        MenuManager menuManager = null;
        
        try {
            menuManager = new MenuManager();
            menuManager.showMainMenu();
            
        } catch (Exception e) {
            System.out.println("Greška pri pokretanju aplikacije: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (menuManager != null) {
                menuManager.close();
            }
        }
    }
}
