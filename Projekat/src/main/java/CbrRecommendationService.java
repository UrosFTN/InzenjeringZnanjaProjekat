import ucm.gaia.jcolibri.cbrcore.*;
import ucm.gaia.jcolibri.method.retrieve.RetrievalResult;
import ucm.gaia.jcolibri.method.retrieve.NNretrieval.NNScoringMethod;
import ucm.gaia.jcolibri.method.retrieve.NNretrieval.NNConfig;
import ucm.gaia.jcolibri.method.retrieve.NNretrieval.similarity.global.Average;
import ucm.gaia.jcolibri.method.retrieve.NNretrieval.similarity.local.Equal;
import ucm.gaia.jcolibri.method.retrieve.NNretrieval.similarity.local.Interval;
import ucm.gaia.jcolibri.method.retrieve.selection.SelectCases;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CbrRecommendationService {
    
    private Model model;
    private final String namespace = "http://www.iz-projekat/film-sema#";
    private Collection<CBRCase> cases;
    private NNConfig simConfig;
    
    public CbrRecommendationService() {
        try {
            initializeRdfModel();
            initializeCbrSystem();
            System.out.println("CBR sistem uspešno inicijalizovan sa " + 
                             cases.size() + " filmova");
        } catch (Exception e) {
            System.err.println("Greška pri inicijalizaciji CBR sistema: " + e.getMessage());
            throw e;
        }
    }
    
    private void initializeRdfModel() {
        try {
            model = ModelFactory.createDefaultModel();
            FileManager.get().readModel(model, "src/main/resources/film_sema.ttl", "TTL");
            FileManager.get().readModel(model, "src/main/resources/film_podaci.ttl", "TTL");
        } catch (Exception e) {
            throw new RuntimeException("Greška pri učitavanju ontologije: " + e.getMessage());
        }
    }
    
    private void initializeCbrSystem() {
        try {
            cases = createCasesFromOntology();
            
            simConfig = createSimilarityConfig();
            
        } catch (Exception e) {
            throw new RuntimeException("Greška pri inicijalizaciji CBR sistema: " + e.getMessage());
        }
    }
    
    private Collection<CBRCase> createCasesFromOntology() {
        List<MovieCase> movieCases = loadMoviesFromOntology();
        
        Collection<CBRCase> cbrCases = new ArrayList<>();
        
        for (MovieCase movieCase : movieCases) {
            CBRCase cbrCase = new CBRCase();
            cbrCase.setDescription((CaseComponent) movieCase);
            cbrCases.add(cbrCase);
        }
        
        return cbrCases;
    }
    
    private NNConfig createSimilarityConfig() {
        NNConfig config = new NNConfig();
        config.setDescriptionSimFunction(new Average());
        
        config.addMapping(new Attribute("zanr", MovieCase.class), new Equal());
        config.setWeight(new Attribute("zanr", MovieCase.class), 0.5);
        
        config.addMapping(new Attribute("reziser", MovieCase.class), new Equal());  
        config.setWeight(new Attribute("reziser", MovieCase.class), 0.3);
        
        config.addMapping(new Attribute("godina", MovieCase.class), new Interval(10));
        config.setWeight(new Attribute("godina", MovieCase.class), 0.15);
        
        config.addMapping(new Attribute("naslov", MovieCase.class), new Equal());
        config.setWeight(new Attribute("naslov", MovieCase.class), 0.05);
        
        return config;
    }
    
    private List<MovieCase> loadMoviesFromOntology() {
        List<MovieCase> movieCases = new ArrayList<>();
        
        String sparqlQuery = 
            "PREFIX : <" + namespace + ">" +
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
            "SELECT DISTINCT ?film ?naslov ?godina ?reziserIme ?reziserPrezime " +
            "(GROUP_CONCAT(DISTINCT ?zanrLabel; separator=\"|\") AS ?zanrovi) WHERE {" +
            "  ?film a :Film ." +
            "  ?film :naslov ?naslov ." +
            "  ?film :godinaIzdanja ?godina ." +
            "  ?film :imaZanr ?zanrObj ." +
            "  ?zanrObj rdfs:label ?zanrLabel ." +
            "  OPTIONAL { " +
            "    ?film :rezirao ?reziserObj . " +
            "    ?reziserObj :licnoIme ?reziserIme . " +
            "    ?reziserObj :prezime ?reziserPrezime " +
            "  }" +
            "} GROUP BY ?film ?naslov ?godina ?reziserIme ?reziserPrezime";
        
        try {
            Query query = QueryFactory.create(sparqlQuery);
            QueryExecution qe = QueryExecutionFactory.create(query, model);
            ResultSet resultSet = qe.execSelect();
            
            while (resultSet.hasNext()) {
                QuerySolution solution = resultSet.nextSolution();
                
                String filmUri = solution.get("film").toString();
                String naslov = getCleanValue(solution, "naslov");
                String zanrovi = getCleanValue(solution, "zanrovi");
                String zanr = zanrovi.contains("|") ? zanrovi.split("\\|")[0] : zanrovi;
                Integer godina = parseYear(getCleanValue(solution, "godina"));
                String reziser = buildDirectorName(solution);
                
                MovieCase movieCase = new MovieCase(filmUri, naslov, zanr, godina, reziser);

                movieCase.setSviZanrovi(zanrovi);
                movieCases.add(movieCase);
            }
            
            qe.close();
        } catch (Exception e) {
            throw new RuntimeException("Greška pri učitavanju filmova iz ontologije: " + e.getMessage());
        }
        
        return movieCases;
    }

    public List<MovieRecommendation> findSimilarMovies(String selectedMovieId) {
        try {
            CBRCase queryCase = findCaseById(selectedMovieId);
            if (queryCase == null) {
                throw new IllegalArgumentException("Film sa ID " + selectedMovieId + " nije pronađen.");
            }
            
            CBRQuery cbrQuery = new CBRQuery();
            cbrQuery.setDescription(queryCase.getDescription());
            
            Collection<RetrievalResult> retrievalResults =
                NNScoringMethod.evaluateSimilarity(cases, cbrQuery, simConfig);
            
            Collection<CBRCase> topCases = SelectCases.selectTopK(retrievalResults, 6); 
            
            List<MovieRecommendation> recommendations = new ArrayList<>();
            
            for (RetrievalResult result : retrievalResults) {
                CBRCase resultCase = result.get_case();
                
                if (topCases.contains(resultCase)) {
                    MovieCase movieCase = (MovieCase) resultCase.getDescription();
                    
                    if (!movieCase.getId().equals(selectedMovieId)) {
                        double similarity = result.getEval();
                        MovieRecommendation recommendation = new MovieRecommendation(movieCase, similarity);
                        recommendations.add(recommendation);
                    }
                    
                    if (recommendations.size() >= 5) break;
                }
            }
            
            return recommendations;
            
        } catch (Exception e) {
            throw new RuntimeException("Greška pri CBR preporuci: " + e.getMessage());
        }
    }

    public List<MovieCase> getAllMovies() {
        List<MovieCase> allMovies = new ArrayList<>();
        
        for (CBRCase cbrCase : cases) {
            MovieCase movieCase = (MovieCase) cbrCase.getDescription();
            allMovies.add(movieCase);
        }
        
        return allMovies;
    }
    
    private CBRCase findCaseById(String movieId) {
        for (CBRCase cbrCase : cases) {
            MovieCase movieCase = (MovieCase) cbrCase.getDescription();
            if (movieCase.getId().equals(movieId)) {
                return cbrCase;
            }
        }
        return null;
    }
    
    private String getCleanValue(QuerySolution solution, String varName) {
        if (solution.get(varName) != null) {
            String value = solution.get(varName).toString();
            if (value.contains("^^")) {
                value = value.substring(0, value.indexOf("^^"));
            }
            if (value.startsWith("\"") && value.endsWith("\"")) {
                value = value.substring(1, value.length() - 1);
            }
            return value;
        }
        return "N/A";
    }
    
    private Integer parseYear(String yearString) {
        try {
            return Integer.parseInt(yearString);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String buildDirectorName(QuerySolution solution) {
        String ime = getCleanValue(solution, "reziserIme");
        String prezime = getCleanValue(solution, "reziserPrezime");
        
        if (!"N/A".equals(ime) && !"N/A".equals(prezime)) {
            return ime + " " + prezime;
        } else if (!"N/A".equals(ime)) {
            return ime;
        } else if (!"N/A".equals(prezime)) {
            return prezime;
        }
        return "N/A";
    }
    
    public static class MovieRecommendation {
        private final MovieCase movieCase;
        private final double similarity;
        
        public MovieRecommendation(MovieCase movieCase, double similarity) {
            this.movieCase = movieCase;
            this.similarity = similarity;
        }
        
        public MovieCase getMovieCase() { return movieCase; }
        public double getSimilarity() { return similarity; }
        
        public String getFormattedSimilarity() {
            return String.format("%.1f%%", similarity * 100);
        }
    }
}