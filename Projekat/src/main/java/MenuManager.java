import java.util.Scanner;
import java.util.List;

/**
 * Klasa za upravljanje menijima aplikacije
 */
public class MenuManager {
    
    private Scanner scanner;
    private SparqlQueryService sparqlService;
    private FuzzyEvaluationService fuzzyService;
    
    /**
     * Konstruktor - inicijalizuje servise
     */
    public MenuManager() {
        this.scanner = new Scanner(System.in);
        this.sparqlService = new SparqlQueryService();
        this.fuzzyService = new FuzzyEvaluationService();
    }
    
    /**
     * Prikazuje glavni meni i upravlja navigacijom
     */
    public void showMainMenu() {
        boolean running = true;
        
        while (running) {
            printMainMenuHeader();
            int choice = getMenuChoice(1, 3);
            
            switch (choice) {
                case 1:
                    showRecommendationMenu();
                    break;
                case 2:
                    showFuzzyEvaluationMenu();
                    break;
                case 3:
                    System.out.println("Izlazim iz aplikacije...");
                    running = false;
                    break;
            }
            
            if (running) {
                waitForEnter();
            }
        }
    }
    
    /**
     * Prikazuje meni za preporuke filmova
     */
    private void showRecommendationMenu() {
        boolean inSubmenu = true;
        
        while (inSubmenu) {
            printRecommendationMenuHeader();
            int choice = getMenuChoice(1, 3);
            
            switch (choice) {
                case 1:
                    searchByGenre();
                    break;
                case 2:
                    searchByGenreAndYear();
                    break;
                case 3:
                    inSubmenu = false;
                    break;
            }
            
            if (inSubmenu) {
                waitForEnter();
            }
        }
    }
    
    /**
     * Prikazuje meni za fuzzy evaluaciju
     */
    private void showFuzzyEvaluationMenu() {
        boolean inSubmenu = true;
        
        while (inSubmenu) {
            printFuzzyMenuHeader();
            int choice = getMenuChoice(1, 2);
            
            switch (choice) {
                case 1:
                    evaluateFilmQuality();
                    break;
                case 2:
                    inSubmenu = false;
                    break;
            }
            
            if (inSubmenu) {
                waitForEnter();
            }
        }
    }
    
    /**
     * Izvršava pretragu filmova po žanru
     */
    private void searchByGenre() {
        System.out.println("\n--- PRETRAGA PO ŽANRU ---");
        String zanr = getGenreInput();
        
        try {
            List<FilmResult> results = sparqlService.searchByGenre(zanr);
            System.out.println("\nRezultati pretrage za žanr: " + zanr);
            TableFormatter.printTable(results);
        } catch (Exception e) {
            System.out.println("Greška pri pretrazi: " + e.getMessage());
        }
    }
    
    /**
     * Izvršava pretragu filmova po žanru i godini
     */
    private void searchByGenreAndYear() {
        System.out.println("\n--- PRETRAGA PO ŽANRU I GODINI ---");
        String zanr = getGenreInput();
        int godina = getYearInput();
        
        try {
            List<FilmResult> results = sparqlService.searchByGenreAndYear(zanr, godina);
            System.out.println("\nRezultati pretrage za žanr: " + zanr + ", godina: " + godina);
            TableFormatter.printTable(results);
        } catch (Exception e) {
            System.out.println("Greška pri pretrazi: " + e.getMessage());
        }
    }
    
    /**
     * Izvršava fuzzy evaluaciju kvaliteta filma
     */
    private void evaluateFilmQuality() {
        System.out.println("\n--- FUZZY EVALUACIJA KVALITETA FILMA ---");
        System.out.println("Ocenite sledeće kriterijume na skali od 1 do 10:");
        
        try {
            double rezija = getCriteriaInput("Režija");
            double gluma = getCriteriaInput("Gluma");
            double scenario = getCriteriaInput("Scenario");
            double originalnost = getCriteriaInput("Originalnost");
            double vizuelniEfekti = getCriteriaInput("Vizuelni efekti");
            
            // Izvršava fuzzy evaluaciju
            FuzzyEvaluationService.FuzzyResult result = 
                fuzzyService.evaluateFilm(rezija, gluma, scenario, originalnost, vizuelniEfekti);
            
            // Prikazuje rezultat
            System.out.println("\n" + "=".repeat(40));
            System.out.println("REZULTAT EVALUACIJE:");
            System.out.println("Kvalitet filma: " + result.getFormattedResult());
            System.out.println("=".repeat(40));
            
        } catch (Exception e) {
            System.out.println("Greška pri evaluaciji: " + e.getMessage());
        }
    }
    
    /**
     * Printa header glavnog menija
     */
    private void printMainMenuHeader() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("       SISTEM PREPORUKE I OCENE FILMOVA");
        System.out.println("=".repeat(50));
        System.out.println("1. Preporuke filmova");
        System.out.println("2. Ocenjivanje kvaliteta");
        System.out.println("3. Izlaz");
        System.out.println("=".repeat(50));
        System.out.print("Izaberite opciju (1-3): ");
    }
    
    /**
     * Printa header menija za preporuke
     */
    private void printRecommendationMenuHeader() {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("         PREPORUKE FILMOVA");
        System.out.println("=".repeat(40));
        System.out.println("1. Pretraga po žanru");
        System.out.println("2. Pretraga po žanru i godini");
        System.out.println("3. Nazad na glavni meni");
        System.out.println("=".repeat(40));
        System.out.print("Izaberite opciju (1-3): ");
    }
    
    /**
     * Printa header menija za fuzzy evaluaciju
     */
    private void printFuzzyMenuHeader() {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("         FUZZY OCENJIVANJE");
        System.out.println("=".repeat(40));
        System.out.println("1. Oceni novi film");
        System.out.println("2. Nazad na glavni meni");
        System.out.println("=".repeat(40));
        System.out.print("Izaberite opciju (1-2): ");
    }
    
    /**
     * Čita izbor korisnika iz menija
     */
    private int getMenuChoice(int min, int max) {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                int choice = Integer.parseInt(input);
                if (choice >= min && choice <= max) {
                    return choice;
                }
                System.out.print("Molimo unesite broj između " + min + " i " + max + ": ");
            } catch (NumberFormatException e) {
                System.out.print("Molimo unesite valjan broj (" + min + "-" + max + "): ");
            }
        }
    }
    
    /**
     * Traži unos žanra od korisnika
     */
    private String getGenreInput() {
        while (true) {
            System.out.print("Unesite žanr filma: ");
            String zanr = scanner.nextLine().trim();
            
            if (!zanr.isEmpty()) {
                return zanr;
            }
            System.out.println("Žanr ne može biti prazan. Molimo pokušajte ponovo.");
        }
    }
    
    /**
     * Traži unos godine od korisnika
     */
    private int getYearInput() {
        while (true) {
            System.out.print("Unesite godinu izdanja: ");
            try {
                String input = scanner.nextLine().trim();
                int godina = Integer.parseInt(input);
                return godina;
            } catch (NumberFormatException e) {
                System.out.println("Godina mora biti broj. Molimo pokušajte ponovo.");
            }
        }
    }
    
    /**
     * Traži unos ocene za kriterijum
     */
    private double getCriteriaInput(String criteriaName) {
        while (true) {
            System.out.print(criteriaName + " (1-10): ");
            try {
                String input = scanner.nextLine().trim();
                double value = Double.parseDouble(input);
                if (value >= 1.0 && value <= 10.0) {
                    return value;
                }
                System.out.println("Ocena mora biti između 1 i 10. Molimo pokušajte ponovo.");
            } catch (NumberFormatException e) {
                System.out.println("Molimo unesite valjan broj između 1 i 10.");
            }
        }
    }
    
    /**
     * Čeka korisnikov Enter za nastavak
     */
    private void waitForEnter() {
        System.out.println("\nPritisnite Enter za nastavak...");
        scanner.nextLine();
    }
    
    /**
     * Zatvara scanner resurse
     */
    public void close() {
        if (scanner != null) {
            scanner.close();
        }
    }
}