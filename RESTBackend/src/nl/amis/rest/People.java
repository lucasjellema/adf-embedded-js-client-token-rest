package nl.amis.rest;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement 
public class People {

    @XmlElement 
    private List<Person> persons = new ArrayList<Person>();
    private String collectionLabel;
    private String token;


    public People() {
        super();
    }


    public void setCollectionLabel(String collectionLabel) {
        this.collectionLabel = collectionLabel;
    }

    public String getCollectionLabel() {
        return collectionLabel;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setPersons(List<Person> persons) {
        this.persons = persons;
    }

    public List<Person> getPersons() {
        return persons;
    }
}
