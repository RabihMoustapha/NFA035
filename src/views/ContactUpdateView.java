package views;

import javax.swing.*;
import java.awt.GridLayout;
import java.io.*;
import java.util.*;

import Models.Contact;

public class ContactUpdateView extends JFrame {

    private ContactsView parent;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField cityField;
    private JButton saveButton, cancelButton;
    private Contact originalContact;

    public ContactUpdateView(Contact contact, ContactsView parent) {
        this.originalContact = contact;
        this.parent = parent;
        setTitle("Update Contact");
        setSize(350, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        firstNameField = new JTextField(contact.getPrenom(), 20);
        lastNameField = new JTextField(contact.getNom(), 20);
        cityField = new JTextField(contact.getVille(), 20);

        saveButton = new JButton("Save Changes");
        cancelButton = new JButton("Cancel");

        JPanel formPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        formPanel.add(new JLabel("First Name:"));
        formPanel.add(firstNameField);
        formPanel.add(new JLabel("Last Name:"));
        formPanel.add(lastNameField);
        formPanel.add(new JLabel("City:"));
        formPanel.add(cityField);
        formPanel.add(saveButton);
        formPanel.add(cancelButton);

        add(formPanel);

        saveButton.addActionListener(e -> updateContact());
        cancelButton.addActionListener(e -> dispose());

        setVisible(true);
    }

    private void updateContact() {
        String prenom = firstNameField.getText().trim();
        String nom = lastNameField.getText().trim();
        String ville = cityField.getText().trim();

        if (prenom.isEmpty() || nom.isEmpty() || ville.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        java.util.List<Contact> contacts = loadContactsFromFile();
        if (contacts == null) return;

        for (Contact c : contacts) {
            if (c.equals(originalContact)) {
                c.setPrenom(prenom);
                c.setNom(nom);
                c.setVille(ville);
                break;
            }
        }

        writeContactsToFile(contacts);
        JOptionPane.showMessageDialog(this, "Contact updated successfully.");
        parent.loadContacts();
        dispose();
    }

    private java.util.List<Contact> loadContactsFromFile() {
        java.util.List<Contact> contacts = new ArrayList<>();
        File file = new File("Contacts.dat");
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            while (true) {
                try {
                    contacts.add((Contact) ois.readObject());
                } catch (EOFException eof) {
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Error reading contacts: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        return contacts;
    }

    private void writeContactsToFile(java.util.List<Contact> contacts) {
        File file = new File("Contacts.dat");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            for (Contact c : contacts) {
                oos.writeObject(c);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving contacts: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}