import ucm.gaia.jcolibri.cbrcore.Attribute;
import ucm.gaia.jcolibri.cbrcore.CaseComponent;

/**
 * jCOLIBRI MovieCase klasa koja predstavlja film kao CBR slučaj
 */
public class MovieCase implements CaseComponent {
    
    private String naslov;
    private String zanr;  // glavni žanr za similarity
    private String sviZanrovi; // svi žanrovi za prikaz
    private Integer godina;
    private String reziser;
    private String id;
    
    /**
     * Konstruktor za kreiranje movie case-a
     */
    public MovieCase() {
        this.id = "";
        this.naslov = "";
        this.zanr = "";
        this.godina = 0;
        this.reziser = "";
    }
    
    /**
     * Konstruktor sa parametrima
     */
    public MovieCase(String id, String naslov, String zanr, Integer godina, String reziser) {
        this.id = id;
        this.naslov = naslov;
        this.zanr = zanr;
        this.sviZanrovi = zanr; // default je isti kao glavni žanr
        this.godina = godina;
        this.reziser = reziser;
    }
    
    // Getteri za jCOLIBRI atribute
    public String getId() { return id; }
    public String getNaslov() { return naslov; }
    public String getZanr() { return zanr; } // glavni žanr za similarity
    public String getSviZanrovi() { return sviZanrovi; } // svi žanrovi za prikaz
    public Integer getGodina() { return godina; }
    public String getReziser() { return reziser; }
    
    // Setteri za jCOLIBRI atribute
    public void setId(String id) { this.id = id; }
    public void setNaslov(String naslov) { this.naslov = naslov; }
    public void setZanr(String zanr) { this.zanr = zanr; }
    public void setSviZanrovi(String sviZanrovi) { this.sviZanrovi = sviZanrovi; }
    public void setGodina(Integer godina) { this.godina = godina; }
    public void setReziser(String reziser) { this.reziser = reziser; }
    
    /**
     * jCOLIBRI zahteva implementaciju getIdAttribute
     */
    @Override
    public Attribute getIdAttribute() {
        return new Attribute("id", this.getClass());
    }
    
    /**
     * String reprezentacija za debugging
     */
    @Override
    public String toString() {
        return String.format("MovieCase{id='%s', naslov='%s', zanr='%s', godina=%d, reziser='%s'}", 
                           id, naslov, zanr, godina, reziser);
    }
    
    /**
     * Equals metoda za poređenje filmova
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        MovieCase movieCase = (MovieCase) obj;
        return id != null ? id.equals(movieCase.id) : movieCase.id == null;
    }
    
    /**
     * HashCode za korišćenje u kolekcijama
     */
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}