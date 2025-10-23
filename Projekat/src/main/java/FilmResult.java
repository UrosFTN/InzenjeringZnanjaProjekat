public class FilmResult {
    private String naslov;
    private String godina;
    private String trajanje;
    private String budzet;
    private String reziser;
    private String zanrovi; 

    public FilmResult(String naslov, String godina, String trajanje, String budzet, String reziser, String zanrovi) {
        this.naslov = naslov;
        this.godina = godina;
        this.trajanje = trajanje;
        this.budzet = budzet;
        this.reziser = reziser;
        this.zanrovi = zanrovi;
    }
    
    public String getNaslov() { return naslov; }
    public String getGodina() { return godina; }
    public String getTrajanje() { return trajanje; }
    public String getBudzet() { return budzet; }
    public String getReziser() { return reziser; }
    public String getZanrovi() { return zanrovi; }
}