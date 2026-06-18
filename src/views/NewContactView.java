package views;

import javax.swing.*;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.*;
import java.util.*;

import Models.Contact;

public class NewContactView extends JFrame {

    private ContactsView parent;
    private java.util.List<Contact> contacts;
    private JTextField firstNameField = new JTextField(15);
    private JTextField lastNameField = new JTextField(15);
    private JTextField cityField = new JTextField(15);
    private JButton saveButton = new JButton("Save");
    private JButton cancelButton = new JButton("Cancel");

    public NewContactView(Contact c, ContactsView parent) {
        this.parent = parent;
        this.contacts = new ArrayList<>();
        loadContacts();

        setTitle("New Contact");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel form = new JPanel(new GridLayout(0, 1, 5, 5));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        form.add(new JLabel("First Name:"));
        form.add(firstNameField);
        form.add(new JLabel("Last Name:"));
        form.add(lastNameField);
        form.add(new JLabel("City:"));
        form.add(cityField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        form.add(buttonPanel);
        add(form);

        saveButton.addActionListener(e -> saveContact());
        cancelButton.addActionListener(e -> dispose());

        setVisible(true);
    }

    private void saveContact() {
        String prenom = firstNameField.getText().trim();
        String nom = lastNameField.getText().trim();
        String ville = cityField.getText().trim();

        if (prenom.isEmpty() || nom.isEmpty() || ville.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Contact newContact = new Contact(nom, prenom, ville);

        for (Contact existing : contacts) {
            if (existing.hasSameData(newContact)) {
                JOptionPane.showMessageDialog(this, "The contact is already entered.");
                return;
            }
        }

        contacts.add(newContact);
        writeAllContacts();
        JOptionPane.showMessageDialog(this, "Contact saved successfully!");
        parent.loadContacts();
        dispose();
    }

    private void loadContacts() {
        contacts.clear();
        File file = new File("Contacts.dat");
        if (!file.exists() || file.length() == 0) return;

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
        }
    }

    private void writeAllContacts() {
        File file = new File("Contacts.dat");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            for (Contact c : contacts) {
                oos.writeObject(c);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving contacts: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}