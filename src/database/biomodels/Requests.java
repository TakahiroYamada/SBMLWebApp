/**
* @author Akira Funahashi <funa@symbio.jst.go.jp>
*/
package database.biomodels;

import org.apache.commons.compress.harmony.unpack200.bytecode.forms.ThisFieldRefForm;
import org.apache.http.client.methods.HttpGet;

import java.util.Objects;

/**
 * @author Mihai Glon\u021b mglont@ebi.ac.uk
 */
@SuppressWarnings("WeakerAccess")
public final class Requests {
    // can also use the Caltech instance: http://biomodels.caltech.edu/
    public static final String BIOMODELS_EBI_BASE = "https://www.ebi.ac.uk/biomodels/";
    public static final String SEARCH_CMD = "search?query=modelformat%3A%22SBML%22%20AND%20curationstatus%3AManually%20curated%20AND%20levelversion%3AL2V*&sort=id-desc";
    public static final String GET_FILES_CMD = "model/files/";
    public static final String DLD_MODEL_CMD = "model/download/";
    public static final int SEARCH_RESULTS_PER_PAGE = 100;
    public static final String APPLICATION_NAME = "SBMLWebApp";
    public static final String VERSION = "1.0.0";
    
    public static HttpGet newGetModelRequest(String model) {
        final String url = getModelRequestUrl(BIOMODELS_EBI_BASE, model);
        return constructJsonGetRequest(url);
    }

    public static HttpGet newCuratedModelSearchRequest() {
        return newCuratedModelSearchRequest(0);
    }

    public static HttpGet newCuratedModelSearchRequest(int offset) {
        String url = String.format("%s%s&offset=%d&numResults=%d", BIOMODELS_EBI_BASE,
                SEARCH_CMD, offset, SEARCH_RESULTS_PER_PAGE);
        return constructJsonGetRequest(url);
    }

    public static HttpGet  newGetModelFileRequest(String modelId, String fileName) {
        String url = String.format("%s%s%s?filename=%s", BIOMODELS_EBI_BASE, DLD_MODEL_CMD,
                Objects.requireNonNull(modelId, "The model identifier is required"),
                Objects.requireNonNull(fileName, "Model file name is required"));
        return constructJsonGetRequest(url);
    }

    public static HttpGet newGetFilesRequest(String modelId) {
        String getFilesUrl = String.format("%s%s%s", BIOMODELS_EBI_BASE, GET_FILES_CMD,
                Objects.requireNonNull(modelId, "The model identifier is required"));
        return constructJsonGetRequest(getFilesUrl);
    }

    private static String getModelRequestUrl(String base, String model) {
        return String.format("%s%s",
                Objects.requireNonNull(base, "Cannot build a model retrieval request without a base URI"),
                Objects.requireNonNull(model, "Model identifier (e.g. BIOMD0000000001) required"));
    }

    private static HttpGet constructJsonGetRequest(String uri) {
        final HttpGet request = new HttpGet(uri);
        String agentName = APPLICATION_NAME + "/" + VERSION;
        // please include a point of contact, such as a website or support email address
        String contact   = "http://celldesigner.org";
        String userAgent = String.format("%s <%s>", agentName, contact);
        // System.out.printf("Performing request %s with user agent %s.%n", uri, userAgent);
        request.setHeader("User-Agent", userAgent);
        request.setHeader("Accept", "application/json");

        return request;
    }
}
