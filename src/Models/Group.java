package Models;

import Observables.MyObservable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class Group extends MyObservable implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private String nom;
    private String description;
    private java.util.List<Contact> contacts;

    public Group() {
        this.id = UUID.randomUUID().toString();
        this.contacts = new ArrayList<>();
    }

    public Group(String nom, String description) {
        this();
        this.nom = nom;
        this.description = description;
    }

    public String getId() { return id; }

    public void ajouterContact(Contact contact) {
        if (!contacts.contains(contact)) {
            contacts.add(contact);
            setChanged();
            notifyObservers();
        }
    }

    public void deleteContact(Contact contact) {
        if (contacts.remove(contact)) {
            setChanged();
            notifyObservers();
        }
    }

    public String getNom() { return nom; }
    public String getDescription() { return description; }
    public java.util.List<Contact> getContacts() { return new ArrayList<>(contacts); }

    public void setNom(String nom) {
        this.nom = nom;
        setChanged();
        notifyObservers();
    }

    public void setDescription(String description) {
        this.description = description;
        setChanged();
        notifyObservers();
    }

    public int getNombreContacts() {
        return contacts == null ? 0 : contacts.size();
    }

    @Override
    public String toString() {
        return getNom() + " with " + getNombreContacts() + " contacts";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Group)) return false;
        Group group = (Group) o;
        return Objects.equals(id, group.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public boolean hasSameData(Group other) {
        if (other == null) return false;
        return Objects.equals(nom, other.nom) &&
               Objects.equals(description, other.description);
    }
}