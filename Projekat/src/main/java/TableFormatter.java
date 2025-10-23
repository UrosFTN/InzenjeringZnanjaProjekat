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
        
        // Definise širine kolona - dodana kolona za žanrove
        int naslowWidth = Math.max(15, getMaxLength(filmovi, "naslov"));
        int zanroviWidth = Math.max(12, getMaxLength(filmovi, "zanrovi"));
        int godinaWidth = 6;
        int rezieerWidth = Math.max(12, getMaxLength(filmovi, "reziser"));
        int trajanjeWidth = 8;
        int budzetWidth = 12;
        
        // Printa header tabele
        printSeparator(naslowWidth, zanroviWidth, godinaWidth, rezieerWidth, trajanjeWidth, budzetWidth);
        printRow("NASLOV", "ŽANROVI", "GODINA", "REŽISER", "TRAJANJE", "BUDŽET", 
                naslowWidth, zanroviWidth, godinaWidth, rezieerWidth, trajanjeWidth, budzetWidth);
        printSeparator(naslowWidth, zanroviWidth, godinaWidth, rezieerWidth, trajanjeWidth, budzetWidth);
        
        // Printa svaki red sa podacima filma
        for (FilmResult film : filmovi) {
            String trajanje = formatTrajanje(film.getTrajanje());
            String budzet = formatBudzet(film.getBudzet());
            
            printRow(film.getNaslov(), film.getZanrovi(), film.getGodina(), film.getReziser(), 
                    trajanje, budzet, naslowWidth, zanroviWidth, godinaWidth, rezieerWidth, 
                    trajanjeWidth, budzetWidth);
        }
        
        printSeparator(naslowWidth, zanroviWidth, godinaWidth, rezieerWidth, trajanjeWidth, budzetWidth);
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
     * Printa jedan red tabele sa podacima - dodana kolona za žanrove
     */
    private static void printRow(String naslov, String zanrovi, String godina, String reziser, 
                                String trajanje, String budzet, int... widths) {
        System.out.printf("| %-" + widths[0] + "s | %-" + widths[1] + "s | %-" + widths[2] + "s | %-" + 
                         widths[3] + "s | %-" + widths[4] + "s | %-" + widths[5] + "s |%n",
                         truncate(naslov, widths[0]), 
                         truncate(zanrovi, widths[1]),
                         truncate(godina, widths[2]), 
                         truncate(reziser, widths[3]),
                         truncate(trajanje, widths[4]), 
                         truncate(budzet, widths[5]));
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
                        case "zanrovi": return film.getZanrovi().length();
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