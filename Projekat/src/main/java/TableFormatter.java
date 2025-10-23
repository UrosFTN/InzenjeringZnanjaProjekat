import java.util.List;

/**
 * Klasa za formatiranje rezultata pretrage u tabelu
 */
public class TableFormatter {
    
    /**
     * Formatira listu filmova u tabelu sa kolonama
     */
    public static void printTable(List<FilmResult> filmovi) {
        if (filmovi.isEmpty()) {
            System.out.println("Nema rezultata za zadatu pretragu.");
            return;
        }
        
        // Definise širine kolona
        int naslowWidth = Math.max(15, getMaxLength(filmovi, "naslov"));
        int godinaWidth = 8;
        int rezieerWidth = Math.max(12, getMaxLength(filmovi, "reziser"));
        int trajanjeWidth = 10;
        int budzetWidth = 15;
        
        // Printa header tabele
        printSeparator(naslowWidth, godinaWidth, rezieerWidth, trajanjeWidth, budzetWidth);
        printRow("NASLOV", "GODINA", "REZISER", "TRAJANJE", "BUDZET", 
                naslowWidth, godinaWidth, rezieerWidth, trajanjeWidth, budzetWidth);
        printSeparator(naslowWidth, godinaWidth, rezieerWidth, trajanjeWidth, budzetWidth);
        
        // Printa svaki red sa podacima filma
        for (FilmResult film : filmovi) {
            String trajanje = formatTrajanje(film.getTrajanje());
            String budzet = formatBudzet(film.getBudzet());
            
            printRow(film.getNaslov(), film.getGodina(), film.getReziser(), 
                    trajanje, budzet, naslowWidth, godinaWidth, rezieerWidth, 
                    trajanjeWidth, budzetWidth);
        }
        
        printSeparator(naslowWidth, godinaWidth, rezieerWidth, trajanjeWidth, budzetWidth);
        System.out.println("Ukupno pronađeno: " + filmovi.size() + " filmova");
    }
    
    /**
     * Printa separator liniju između redova
     */
    private static void printSeparator(int... widths) {
        System.out.print("+");
        for (int width : widths) {
            for (int i = 0; i < width + 2; i++) {
                System.out.print("-");
            }
            System.out.print("+");
        }
        System.out.println();
    }
    
    /**
     * Printa jedan red tabele sa podacima
     */
    private static void printRow(String naslov, String godina, String reziser, 
                                String trajanje, String budzet, int... widths) {
        System.out.printf("| %-" + widths[0] + "s | %-" + widths[1] + "s | %-" + 
                         widths[2] + "s | %-" + widths[3] + "s | %-" + widths[4] + "s |%n",
                         truncate(naslov, widths[0]), 
                         truncate(godina, widths[1]), 
                         truncate(reziser, widths[2]),
                         truncate(trajanje, widths[3]), 
                         truncate(budzet, widths[4]));
    }
    
    /**
     * Skraćuje text ako je duži od maksimalne širine
     */
    private static String truncate(String text, int maxWidth) {
        if (text.length() <= maxWidth) {
            return text;
        }
        return text.substring(0, maxWidth - 3) + "...";
    }
    
    /**
     * Nalazi maksimalnu dužinu stringa za određeno polje
     */
    private static int getMaxLength(List<FilmResult> filmovi, String field) {
        return filmovi.stream()
                .mapToInt(film -> {
                    switch (field) {
                        case "naslov": return film.getNaslov().length();
                        case "reziser": return film.getReziser().length();
                        default: return 0;
                    }
                })
                .max()
                .orElse(10);
    }
    
    /**
     * Formatira trajanje za prikaz
     */
    private static String formatTrajanje(String trajanje) {
        if ("N/A".equals(trajanje)) {
            return "N/A";
        }
        return trajanje + " min";
    }
    
    /**
     * Formatira budžet za prikaz
     */
    private static String formatBudzet(String budzet) {
        if ("N/A".equals(budzet)) {
            return "N/A";
        }
        try {
            long broj = Long.parseLong(budzet);
            if (broj >= 1000000) {
                return String.format("$%.1fM", broj / 1000000.0);
            } else if (broj >= 1000) {
                return String.format("$%.1fK", broj / 1000.0);
            } else {
                return "$" + broj;
            }
        } catch (NumberFormatException e) {
            return budzet;
        }
    }
}