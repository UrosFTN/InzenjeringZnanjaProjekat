import java.util.Scanner;
import java.util.List;

public class MenuManager {
    
    private Scanner scanner;
    private SparqlQueryService sparqlService;
    private FuzzyEvaluationService fuzzyService;
    private CbrRecommendationService cbrService;
    
    public MenuManager() {
        this.scanner = new Scanner(System.in);
        this.sparqlService = new SparqlQueryService();
        this.fuzzyService = new FuzzyEvaluationService();
        this.cbrService = new CbrRecommendationService();
    }
    
    public void showMainMenu() {
        boolean running = true;
        
        while (running) {
            printMainMenuHeader();
            int choice = getMenuChoice(1, 4);
            
            switch (choice) {
                case 1:
                    showRecommendationMenu();
                    break;
                case 2:
                    showFuzzyEvaluationMenu();
                    break;
                case 3:
                    showCbrRecommendationMenu();
                    break;
                case 4:
                    System.out.println("Izlazim iz aplikacije...");
                    running = false;
                    break;
            }
            
            if (running) {
                waitForEnter();
            }
        }
    }
    
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
    
    private void evaluateFilmQuality() {
        System.out.println("\n--- FUZZY EVALUACIJA KVALITETA FILMA ---");
        System.out.println("Ocenite sledeće kriterijume na skali od 1 do 10:");
        
        try {
            double rezija = getCriteriaInput("Režija");
            double gluma = getCriteriaInput("Gluma");
            double scenario = getCriteriaInput("Scenario");
            double originalnost = getCriteriaInput("Originalnost");
            double vizuelniEfekti = getCriteriaInput("Vizuelni efekti");
            
            FuzzyEvaluationService.FuzzyResult result = 
                fuzzyService.evaluateFilm(rezija, gluma, scenario, originalnost, vizuelniEfekti);
            
            System.out.println("\n" + "=".repeat(40));
            System.out.println("REZULTAT EVALUACIJE:");
            System.out.println("Kvalitet filma: " + result.getFormattedResult());
            System.out.println("=".repeat(40));
            
        } catch (Exception e) {
            System.out.println("Greška pri evaluaciji: " + e.getMessage());
        }
    }
    
    private void printMainMenuHeader() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("       SISTEM PREPORUKE I OCENE FILMOVA");
        System.out.println("=".repeat(50));
        System.out.println("1. Preporuke filmova (SPARQL)");
        System.out.println("2. Ocenjivanje kvaliteta (Fuzzy)");
        System.out.println("3. Slični filmovi (CBR)");
        System.out.println("4. Izlaz");
        System.out.println("=".repeat(50));
        System.out.print("Izaberite opciju (1-4): ");
    }
    
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

    private void printFuzzyMenuHeader() {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("         FUZZY OCENJIVANJE");
        System.out.println("=".repeat(40));
        System.out.println("1. Oceni novi film");
        System.out.println("2. Nazad na glavni meni");
        System.out.println("=".repeat(40));
        System.out.print("Izaberite opciju (1-2): ");
    }

    private void showCbrRecommendationMenu() {
        boolean inSubmenu = true;
        
        while (inSubmenu) {
            printCbrMenuHeader();
            int choice = getMenuChoice(1, 2);
            
            switch (choice) {
                case 1:
                    findSimilarMovies();
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

    private void printCbrMenuHeader() {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("         CBR PREPORUKE");
        System.out.println("=".repeat(40));
        System.out.println("1. Pronađi slične filmove");
        System.out.println("2. Nazad na glavni meni");
        System.out.println("=".repeat(40));
        System.out.print("Izaberite opciju (1-2): ");
    }
    
    private void findSimilarMovies() {
        System.out.println("\n--- CBR PREPORUKE SLIČNIH FILMOVA ---");
        
        try {
            List<MovieCase> allMovies = cbrService.getAllMovies();
            
            if (allMovies.isEmpty()) {
                System.out.println("Nema dostupnih filmova u bazi.");
                return;
            }
            
            System.out.println("\n=== DOSTUPNI FILMOVI ===");
            for (int i = 0; i < allMovies.size(); i++) {
                MovieCase movie = allMovies.get(i);
                System.out.printf("%d. %s (%d) - %s [%s]\n", 
                                i + 1, movie.getNaslov(), movie.getGodina(), 
                                movie.getReziser(), movie.getSviZanrovi()); 
            }
            
            int selectedIndex = getMovieChoice(allMovies.size()) - 1;
            MovieCase selectedMovie = allMovies.get(selectedIndex);
            
            System.out.println("\nTražim filmove slične sa: " + selectedMovie.getNaslov());
            
            List<CbrRecommendationService.MovieRecommendation> recommendations = 
                cbrService.findSimilarMovies(selectedMovie.getId());
            
            printCbrRecommendationsTable(recommendations);
            
        } catch (Exception e) {
            System.out.println("Greška pri CBR preporuci: " + e.getMessage());
        }
    }
    
    private void printCbrRecommendationsTable(List<CbrRecommendationService.MovieRecommendation> recommendations) {
        if (recommendations.isEmpty()) {
            System.out.println("Nema sličnih filmova.");
            return;
        }
        
        System.out.println("\n=== SLIČNI FILMOVI ===");
        
        int naslovWidth = 18;
        int zanrWidth = 12;
        int godinaWidth = 6;
        int reziserWidth = 15;
        int similarityWidth = 10;
        
        printCbrSeparator(naslovWidth, zanrWidth, godinaWidth, reziserWidth, similarityWidth);
        printCbrRow("NASLOV", "ŽANR", "GODINA", "REŽISER", "SLIČNOST", 
                   naslovWidth, zanrWidth, godinaWidth, reziserWidth, similarityWidth);
        printCbrSeparator(naslovWidth, zanrWidth, godinaWidth, reziserWidth, similarityWidth);
        
        for (CbrRecommendationService.MovieRecommendation rec : recommendations) {
            MovieCase movie = rec.getMovieCase();
            printCbrRow(movie.getNaslov(),
                       movie.getSviZanrovi(), 
                       movie.getGodina().toString(),
                       movie.getReziser(),
                       rec.getFormattedSimilarity(),
                       naslovWidth, zanrWidth, godinaWidth, reziserWidth, similarityWidth);
        }
        
        printCbrSeparator(naslovWidth, zanrWidth, godinaWidth, reziserWidth, similarityWidth);
        System.out.println("Ukupno preporučeno: " + recommendations.size() + " filmova");
    }
    
    private void printCbrSeparator(int... widths) {
        System.out.print("+");
        for (int width : widths) {
            for (int i = 0; i < width + 2; i++) {
                System.out.print("-");
            }
            System.out.print("+");
        }
        System.out.println();
    }
    
    private void printCbrRow(String naslov, String zanr, String godina, String reziser, String similarity, int... widths) {
        System.out.printf("| %-" + widths[0] + "s | %-" + widths[1] + "s | %-" + 
                         widths[2] + "s | %-" + widths[3] + "s | %-" + widths[4] + "s |%n",
                         truncate(naslov, widths[0]), 
                         truncate(zanr, widths[1]),
                         truncate(godina, widths[2]), 
                         truncate(reziser, widths[3]),
                         truncate(similarity, widths[4]));
    }
    
    private String truncate(String text, int maxWidth) {
        if (text.length() <= maxWidth) {
            return text;
        }
        return text.substring(0, maxWidth - 3) + "...";
    }

    private int getMovieChoice(int maxMovies) {
        while (true) {
            System.out.print("\nIzaberite film (1-" + maxMovies + "): ");
            try {
                String input = scanner.nextLine().trim();
                int choice = Integer.parseInt(input);
                if (choice >= 1 && choice <= maxMovies) {
                    return choice;
                }
                System.out.println("Molimo unesite broj između 1 i " + maxMovies + ".");
            } catch (NumberFormatException e) {
                System.out.println("Molimo unesite valjan broj.");
            }
        }
    }

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

    private void waitForEnter() {
        System.out.println("\nPritisnite Enter za nastavak...");
        scanner.nextLine();
    }

    public void close() {
        if (scanner != null) {
            scanner.close();
        }
    }
}