import ucm.gaia.jcolibri.cbrcore.Attribute;
import ucm.gaia.jcolibri.cbrcore.CaseComponent;

public class MovieCase implements CaseComponent {
    
    private String naslov;
    private String zanr;  // glavni žanr za similarity
    private String sviZanrovi; // svi žanrovi za prikaz
    private Integer godina;
    private String reziser;
    private String id;
    
    public MovieCase(String id, String naslov, String zanr, Integer godina, String reziser) {
        this.id = id;
        this.naslov = naslov;
        this.zanr = zanr;
        this.sviZanrovi = zanr; // default je isti kao glavni žanr
        this.godina = godina;
        this.reziser = reziser;
    }

    public String getId() { return id; }
    public String getNaslov() { return naslov; }
    public String getZanr() { return zanr; }
    public String getSviZanrovi() { return sviZanrovi; } 
    public Integer getGodina() { return godina; }
    public String getReziser() { return reziser; }

    public void setId(String id) { this.id = id; }
    public void setNaslov(String naslov) { this.naslov = naslov; }
    public void setZanr(String zanr) { this.zanr = zanr; }
    public void setSviZanrovi(String sviZanrovi) { this.sviZanrovi = sviZanrovi; }
    public void setGodina(Integer godina) { this.godina = godina; }
    public void setReziser(String reziser) { this.reziser = reziser; }
    
    @Override
    public Attribute getIdAttribute() {
        return new Attribute("id", this.getClass());
    }

    @Override
    public String toString() {
        return String.format("MovieCase{id='%s', naslov='%s', zanr='%s', godina=%d, reziser='%s'}", 
                           id, naslov, zanr, godina, reziser);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        MovieCase movieCase = (MovieCase) obj;
        return id != null ? id.equals(movieCase.id) : movieCase.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}