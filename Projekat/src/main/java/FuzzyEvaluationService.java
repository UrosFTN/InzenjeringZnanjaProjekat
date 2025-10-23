import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;

public class FuzzyEvaluationService {
    
    private FIS fis;
    private FunctionBlock functionBlock;
    
    public FuzzyEvaluationService() {
        initializeFuzzySystem();
    }

    private void initializeFuzzySystem() {
        try {
            // Učitava FCL fajl
            String fclFile = "src/main/resources/film_fuzzy.fcl";
            fis = FIS.load(fclFile, true);
            
            if (fis == null) {
                throw new RuntimeException("Greška pri učitavanju FCL fajla: " + fclFile);
            }
            
            // Dobija function block
            functionBlock = fis.getFunctionBlock("film_kvalitet");
            
            if (functionBlock == null) {
                throw new RuntimeException("Function block 'film_kvalitet' nije pronađen u FCL fajlu");
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Greška pri inicijalizaciji fuzzy sistema: " + e.getMessage());
        }
    }
    
    public FuzzyResult evaluateFilm(double rezija, double gluma, double scenario, 
                                  double originalnost, double vizuelniEfekti) {
        
        validateInput(rezija, "Režija");
        validateInput(gluma, "Gluma");
        validateInput(scenario, "Scenario");
        validateInput(originalnost, "Originalnost");
        validateInput(vizuelniEfekti, "Vizuelni efekti");
        
        try {
            functionBlock.setVariable("rezija", rezija);
            functionBlock.setVariable("gluma", gluma);
            functionBlock.setVariable("scenario", scenario);
            functionBlock.setVariable("originalnost", originalnost);
            functionBlock.setVariable("vizuelni_efekti", vizuelniEfekti);
            
            functionBlock.evaluate();
            
            double kvalitetVrednost = functionBlock.getVariable("kvalitet").getValue();
            
            String kategorijaKvaliteta = mapToQualityCategory(kvalitetVrednost);
            
            return new FuzzyResult(kvalitetVrednost, kategorijaKvaliteta);
            
        } catch (Exception e) {
            throw new RuntimeException("Greška pri fuzzy evaluaciji: " + e.getMessage());
        }
    }

    private void validateInput(double value, String parameterName) {
        if (value < 1.0 || value > 10.0) {
            throw new IllegalArgumentException(
                parameterName + " mora biti između 1.0 i 10.0, a uneto je: " + value);
        }
    }

    private String mapToQualityCategory(double value) {
        if (value <= 2.0) {
            return "Loš";
        } else if (value <= 4.0) {
            return "Slab";
        } else if (value <= 6.0) {
            return "Prosečan";
        } else if (value <= 8.0) {
            return "Dobar";
        } else {
            return "Odličan";
        }
    }

    public static class FuzzyResult {
        private final double numericValue;
        private final String category;
        
        public FuzzyResult(double numericValue, String category) {
            this.numericValue = numericValue;
            this.category = category;
        }
        
        public double getNumericValue() {
            return numericValue;
        }
        
        public String getCategory() {
            return category;
        }
 
        public String getFormattedResult() {
            return String.format("%s (%.1f/10)", category, numericValue);
        }
        
        @Override
        public String toString() {
            return getFormattedResult();
        }
    }
}