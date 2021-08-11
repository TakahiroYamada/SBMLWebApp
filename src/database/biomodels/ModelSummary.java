package database.biomodels;

@SuppressWarnings("unused")
public class ModelSummary implements Comparable<ModelSummary> {
    private String id;
    private String name;
    private String url;

    public String toString() {
        return id + '\t' + name + '\t' + url;
    }

    public String getId() {
        return id;
    }

    public String getName() {
      return name;
    }

    public String getUrl() {
      return url;
    }

    @Override
    public int compareTo(ModelSummary o) {
        return id.compareTo(o.getId());
    }

    public boolean equals(Object o) {
        return (o instanceof ModelSummary) && ((ModelSummary) o).getId().equals(id);
    }
}
