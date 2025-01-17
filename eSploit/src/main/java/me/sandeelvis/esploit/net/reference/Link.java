package me.sandeelvis.esploit.net.reference;

/**
 * a simple link reference
 */
public class Link implements Url, Reference {
  private String name;
  private final String url;

  public Link(String name, String link) {
    this.name = name;
    this.url = link;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String getUrl() {
    return url;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getSummary() {
    return url;
  }

  @Override
  public String toString() {
    return "Link: " + url;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null) {
      return false;
    }

    return o.getClass() == this.getClass() && url.equals(((Link) o).url);
  }
}
