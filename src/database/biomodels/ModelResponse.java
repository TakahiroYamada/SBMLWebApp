package database.biomodels;


/**
 * @author Mihai Glon\u021b mglont@ebi.ac.uk
 */
public class ModelResponse {
    private String name;
    private String description;
    private String submissionId;
    private String publicationId;
    private long firstPublished;
    private FormatResponse format;
    private PublicationResponse publication;
    private ModelFilesResponse files;

    public static final class FormatResponse {
      private String name;
      private String version;

      public String getName() {
        return name;
      }
      public String getVersion() {
        return version;
      }
    }
    
    public ModelResponse() {
        // default constructor
    }

    @Override
    public String toString() {
        final StringBuilder response = new StringBuilder();

        response.append("Model id ")
                .append(submissionId)
                .append(" (")
                .append(format.name)
                .append(",")
                .append(format.version)
                .append(")")
                .append(" (")
                .append(name)
                .append(") [")
                .append(publication)
                .append("]");

        return response.toString();
    }

    public String getName() {
      return name;
    }

    public String getDescription() {
      return description;
    }

    public String getSubmissionId() {
      return submissionId;
    }

    public String getPublicationId() {
      return publicationId;
    }

    public long getFirstPublished() {
      return firstPublished;
    }

    public PublicationResponse getPublication() {
      return publication;
    }

    public FormatResponse getFormat() {
      return format;
    }

    public ModelFilesResponse getFiles() {
      return files;
    }

}
