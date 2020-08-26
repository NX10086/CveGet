package com.cve;

public class Cvelink {
    String cve_id;
    String link;

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    String summary;

    public String getCve_id() {
        return cve_id;
    }

    public void setCve_id(String cve_id) {
        this.cve_id = cve_id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public String toString() {
        return "Cvelink{" +
                "cve_id='" + cve_id + '\'' +"<br>"+
                ", link='" + link + '\'' +"<br>"+
                ", summary='" + summary + '\'' +
                '}';
    }
}
