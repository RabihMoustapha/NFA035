package Models;

import Observables.MyObservable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class Contact extends MyObservable implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private String nom;
    private String prenom;
    private String ville;
    private java.util.List<PhoneNumber> telephoneNumbers;

    public Contact() {
        this.id = UUID.randomUUID().toString();
        this.telephoneNumbers = new ArrayList<>();
    }

    public Contact(String nom, String prenom, String ville) {
        this();
        this.nom = nom;
        this.prenom = prenom;
        this.ville = ville;
    }

    public String getId() { return id; }

    public void addPhoneNumber(PhoneNumber pn) {
        telephoneNumbers.add(pn);
        setChanged();
        notifyObservers();
    }

    public void deletePhoneNumber(PhoneNumber number) {
        if (telephoneNumbers.remove(number)) {
            setChanged();
            notifyObservers();
        }
    }

    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getVille() { return ville; }
    public java.util.List<PhoneNumber> getNumbers() { return telephoneNumbers; }

    public void setNom(String nom) {
        this.nom = nom;
        setChanged();
        notifyObservers();
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
        setChanged();
        notifyObservers();
    }

    public void setVille(String ville) {
        this.ville = ville;
        setChanged();
        notifyObservers();
    }

    @Override
    public String toString() {
        return nom + " " + prenom + " lives in " + ville;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Contact)) return false;
        Contact contact = (Contact) o;
        return Objects.equals(id, contact.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public boolean hasSameData(Contact other) {
        if (other == null) return false;
        return Objects.equals(nom, other.nom) &&
               Objects.equals(prenom, other.prenom) &&
               Objects.equals(ville, other.ville);
    }
}