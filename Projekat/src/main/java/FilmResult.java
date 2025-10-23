/**
 * Klasa koja predstavlja rezultat pretrage filma
 */
public class FilmResult {
    private String naslov;
    private String godina;
    private String trajanje;
    private String budzet;
    private String reziser;
    
    /**
     * Konstruktor za kreiranje film rezultata
     */
    public FilmResult(String naslov, String godina, String trajanje, String budzet, String reziser) {
        this.naslov = naslov;
        this.godina = godina;
        this.trajanje = trajanje;
        this.budzet = budzet;
        this.reziser = reziser;
    }
    
    // Getteri za pristup podacima
    public String getNaslov() { return naslov; }
    public String getGodina() { return godina; }
    public String getTrajanje() { return trajanje; }
    public String getBudzet() { return budzet; }
    public String getReziser() { return reziser; }
}