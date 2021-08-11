/**
* @author Akira Funahashi <funa@symbio.jst.go.jp>
*/
package database.biomodels;

import java.util.List;

/**
 * @author Akira Funahashi <funa@bio.keio.ac.jp>
 */
public class PublicationResponse {
  // The name of the journal where the publication was published
  private String journal;
  // The title of the manuscript
  private String title;
  // The name of the corresponding author’s institution
  private String affiliation;

  // The abstract of the publication
  private String synopsis;
  // The year when the publication was published
  private int year;
  // The month of the year when the publication was published
  private String month;
  // The day of the month when the publication was published
  private int day;
  // The volume number of the journal in which the publication was published
  private String volume;
  // The issue number of the journal volume in where the publication was
  // published
  private String issue;
  // The range of pages of the publication showing in the issue
  private String pages;
  // The publication link where we can access and read it
  private String link;
  // Authors
  private List<PublicationAuthor> authors;

  public static final class PublicationAuthor {
    private String name;
    private String orcid;

    public String getName() {
      return name;
    }
    public String getOrcid() {
      return orcid;
    }
  }

  public PublicationResponse() {
    // default constructor
  }

  @Override
  public String toString() {
    final StringBuilder response = new StringBuilder();

    response.append(journal).append(" (").append(year).append(") [");
    for (PublicationAuthor a : authors) {
      response.append(a.name).append(", ");
    }
    response.append("]");

    return response.toString();
  }

  public String getJournal() {
    return journal;
  }

  public String getTitle() {
    return title;
  }

  public String getAffiliation() {
    return affiliation;
  }

  public String getSynopsis() {
    return synopsis;
  }

  public int getYear() {
    return year;
  }

  public String getMonth() {
    return month;
  }

  public int getDay() {
    return day;
  }

  public String getVolume() {
    return volume;
  }

  public String getIssue() {
    return issue;
  }

  public String getPages() {
    return pages;
  }

  public String getLink() {
    return link;
  }

  public List<PublicationAuthor> getAuthors() {
    return authors;
  }
  
}
