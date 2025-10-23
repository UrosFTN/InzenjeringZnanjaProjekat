import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;
import java.util.ArrayList;
import java.util.List;

public class SparqlQueryService {
    
    private Model model;
    private final String namespace = "http://www.iz-projekat/film-sema#";

    public SparqlQueryService() {
        initializeModel();
    }
    
    private void initializeModel() {
        try {
            model = ModelFactory.createDefaultModel();
            FileManager.get().readModel(model, "src/main/resources/film_sema.ttl", "TTL");
            FileManager.get().readModel(model, "src/main/resources/film_podaci.ttl", "TTL");
        } catch (Exception e) {
            throw new RuntimeException("Greska pri učitavanju ontologije: " + e.getMessage());
        }
    }
    
    /**
     * @param zanr naziv zanra za pretragu
     * @return lista rezultata pretrage
     */
    public List<FilmResult> searchByGenre(String zanr) {
        String sparqlQuery = 
            "PREFIX : <" + namespace + ">" +
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
            "SELECT ?film ?naslov ?godina ?trajanje ?budzet ?reziserIme ?reziserPrezime " +
            "(GROUP_CONCAT(DISTINCT ?zanrLabel; separator=\", \") AS ?sviZanrovi) WHERE {" +
            "  ?film a :Film ." +
            "  ?film :naslov ?naslov ." +
            "  ?film :godinaIzdanja ?godina ." +
            "  OPTIONAL { ?film :trajanjeMinuta ?trajanje }" +
            "  OPTIONAL { ?film :budzetUSD ?budzet }" +
            "  OPTIONAL { ?film :rezirao ?reziserObj . ?reziserObj :licnoIme ?reziserIme . ?reziserObj :prezime ?reziserPrezime }" +
            "  ?film :imaZanr ?zanrObj ." +
            "  ?zanrObj rdfs:label ?zanrLabel ." +
            "  FILTER(EXISTS { ?film :imaZanr ?testZanr . ?testZanr rdfs:label ?testLabel . FILTER(LCASE(str(?testLabel)) = LCASE(\"" + zanr + "\")) })" +
            "} GROUP BY ?film ?naslov ?godina ?trajanje ?budzet ?reziserIme ?reziserPrezime";
        
        return executeQuery(sparqlQuery);
    }
    
    /**
     * @param zanr naziv žanra za pretragu
     * @param godina godina izdanja filma
     * @return lista rezultata pretrage
     */
    public List<FilmResult> searchByGenreAndYear(String zanr, int godina) {
        String sparqlQuery = 
            "PREFIX : <" + namespace + ">" +
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
            "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>" +
            "SELECT ?film ?naslov ?godina ?trajanje ?budzet ?reziserIme ?reziserPrezime " +
            "(GROUP_CONCAT(DISTINCT ?zanrLabel; separator=\", \") AS ?sviZanrovi) WHERE {" +
            "  ?film a :Film ." +
            "  ?film :naslov ?naslov ." +
            "  ?film :godinaIzdanja ?godina ." +
            "  OPTIONAL { ?film :trajanjeMinuta ?trajanje }" +
            "  OPTIONAL { ?film :budzetUSD ?budzet }" +
            "  OPTIONAL { ?film :rezirao ?reziserObj . ?reziserObj :licnoIme ?reziserIme . ?reziserObj :prezime ?reziserPrezime }" +
            "  ?film :imaZanr ?zanrObj ." +
            "  ?zanrObj rdfs:label ?zanrLabel ." +
            "  FILTER(EXISTS { ?film :imaZanr ?testZanr . ?testZanr rdfs:label ?testLabel . FILTER(LCASE(str(?testLabel)) = LCASE(\"" + zanr + "\")) })" +
            "  FILTER(xsd:int(?godina) = " + godina + ")" +
            "} GROUP BY ?film ?naslov ?godina ?trajanje ?budzet ?reziserIme ?reziserPrezime";
        
        return executeQuery(sparqlQuery);
    }
    
    /**
     * @param sparqlQuery SPARQL upit string
     * @return lista film rezultata
     */
    private List<FilmResult> executeQuery(String sparqlQuery) {
        List<FilmResult> results = new ArrayList<>();
        
        try {
            Query query = QueryFactory.create(sparqlQuery);
            QueryExecution qe = QueryExecutionFactory.create(query, model);
            ResultSet resultSet = qe.execSelect();
            
            while (resultSet.hasNext()) {
                QuerySolution solution = resultSet.nextSolution();
                
                String naslov = getStringValue(solution, "naslov");
                String godina = getStringValue(solution, "godina");
                String trajanje = getStringValue(solution, "trajanje");
                String budzet = getStringValue(solution, "budzet");
                String reziser = buildDirectorName(solution);
                String zanrovi = getStringValue(solution, "sviZanrovi");
                
                FilmResult result = new FilmResult(naslov, godina, trajanje, budzet, reziser, zanrovi);
                results.add(result);
            }
            
            qe.close();
        } catch (Exception e) {
            throw new RuntimeException("Greška pri izvršavanju SPARQL upita: " + e.getMessage());
        }
        
        return results;
    }
    
    private String getStringValue(QuerySolution solution, String varName) {
        if (solution.get(varName) != null) {
            String value = solution.get(varName).toString();
            // Uklanja RDF tip podataka (npr. "1999"^^xsd:gYear -> "1999")
            if (value.contains("^^")) {
                value = value.substring(0, value.indexOf("^^"));
            }
            // Uklanja navodne znake
            if (value.startsWith("\"") && value.endsWith("\"")) {
                value = value.substring(1, value.length() - 1);
            }
            return value;
        }
        return "N/A";
    }
    
    private String buildDirectorName(QuerySolution solution) {
        String ime = getStringValue(solution, "reziserIme");
        String prezime = getStringValue(solution, "reziserPrezime");
        
        if (!"N/A".equals(ime) && !"N/A".equals(prezime)) {
            return ime + " " + prezime;
        } else if (!"N/A".equals(ime)) {
            return ime;
        } else if (!"N/A".equals(prezime)) {
            return prezime;
        }
        return "N/A";
    }
}