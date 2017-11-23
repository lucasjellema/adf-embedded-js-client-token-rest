package nl.amis.rest;

public class Person {
    private String firstName;
    private String lastName;
    private String country;
    private String job;
    
    public Person() {
        super();
    }

    public Person(String firstName, String lastName, String country, String job) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.country = country;
        this.job = job;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountry() {
        return country;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getJob() {
        return job;
    }
}
