package views;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.*;
import java.util.*;
import java.util.function.Function;

import Models.Contact;
import Models.PhoneNumber;

public class ContactsView extends JFrame {

    private java.util.List<Contact> contacts;
    private JButton sortByFirstName = new JButton("Sort by First Name");
    private JButton sortByLastName = new JButton("Sort by Last Name");
    private JButton sortByCity = new JButton("Sort by City");
    private JButton addNewContact = new JButton("Add New Contact");
    private JButton updateContact = new JButton("Update Contact");
    private JButton deleteContact = new JButton("Delete Contact");
    private JButton viewContact = new JButton("View Contact");
    private JButton cancelButton = new JButton("Cancel");

    private JTextField searchField = new JTextField(20);
    private DefaultListModel<Contact> listModel = new DefaultListModel<>();
    private JList<Contact> contactsList = new JList<>(listModel);

    public ContactsView(Contact dummy) {
        contacts = new ArrayList<>();
        setTitle("Contacts");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(sortByFirstName);
        topPanel.add(sortByLastName);
        topPanel.add(sortByCity);
        topPanel.add(searchField);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addNewContact);
        buttonPanel.add(updateContact);
        buttonPanel.add(deleteContact);
        buttonPanel.add(viewContact);
        buttonPanel.add(cancelButton);

        JScrollPane scrollPane = new JScrollPane(contactsList);
        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        loadContacts();

        addNewContact.addActionListener(e -> new NewContactView(new Contact(), this));
        cancelButton.addActionListener(e -> dispose());

        updateContact.addActionListener(e -> {
            Contact selected = contactsList.getSelectedValue();
            if (selected != null) {
                new ContactUpdateView(selected, this);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a contact to update");
            }
        });

        deleteContact.addActionListener(e -> deleteSelectedContact());
        viewContact.addActionListener(e -> viewContact());

        sortByFirstName.addActionListener(e -> sortContactsBy(Contact::getPrenom));
        sortByLastName.addActionListener(e -> sortContactsBy(Contact::getNom));
        sortByCity.addActionListener(e -> sortContactsBy(Contact::getVille));

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { searchContacts(); }
            public void removeUpdate(DocumentEvent e) { searchContacts(); }
            public void insertUpdate(DocumentEvent e) { searchContacts(); }
        });

        setVisible(true);
    }

    /* ---------- Data loading / saving ---------- */

    public void loadContacts() {
        listModel.clear();
        contacts.clear();
        File file = new File("Contacts.dat");
        if (!file.exists() || file.length() == 0) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            while (true) {
                try {
                    Contact c = (Contact) ois.readObject();
                    contacts.add(c);
                    listModel.addElement(c);
                } catch (EOFException eof) {
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Error loading contacts: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void saveAllContacts() {
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

    /* ---------- CRUD actions ---------- */

    private void deleteSelectedContact() {
        Contact selected = contactsList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a contact to delete");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete " + selected.getNom() + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            contacts.remove(selected);
            updateListModel(contacts);
            saveAllContacts();
        }
    }

    private void viewContact() {
        Contact selected = contactsList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a contact to view");
            return;
        }
        JDialog dialog = new JDialog(this, "Contact Details", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JTextArea infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setText("First Name: " + selected.getPrenom() + "\n"
                + "Last Name: " + selected.getNom() + "\n"
                + "City: " + selected.getVille() + "\n\nPhone Numbers:\n");
        java.util.List<PhoneNumber> phones = selected.getNumbers();
        for (PhoneNumber phone : phones) {
            infoArea.append(phone.toString() + "\n");
        }
        dialog.add(new JScrollPane(infoArea), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addPhoneNumber = new JButton("Add Phone Number");
        JButton updatePhoneBtn = new JButton("Update Phone");
        JButton removePhoneBtn = new JButton("Remove Phone");

        // --- Add Phone ---
        addPhoneNumber.addActionListener(e -> {
            String regionCode = JOptionPane.showInputDialog(dialog, "Enter Region Code:");
            if (regionCode == null) return;
            String number = JOptionPane.showInputDialog(dialog, "Enter Phone Number:");
            if (number == null) return;
            selected.addPhoneNumber(new PhoneNumber(regionCode.trim(), number.trim()));
            saveAllContacts();
            JOptionPane.showMessageDialog(dialog, "Phone number added.");
            dialog.dispose();
            viewContact(); // refresh
        });

        // --- Update Phone ---
        updatePhoneBtn.addActionListener(e -> {
            java.util.List<PhoneNumber> phoneNumbers = selected.getNumbers();
            if (phoneNumbers.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "No phone numbers to update.");
                return;
            }
            PhoneNumber selectedPhone = (PhoneNumber) JOptionPane.showInputDialog(dialog,
                    "Select a phone to update:", "Update Phone",
                    JOptionPane.PLAIN_MESSAGE, null, phoneNumbers.toArray(), phoneNumbers.get(0));
            if (selectedPhone != null) {
                String newRegion = JOptionPane.showInputDialog(dialog,
                        "Enter new region code:", selectedPhone.getRegionCode());
                if (newRegion == null) return;
                String newNumber = JOptionPane.showInputDialog(dialog,
                        "Enter new phone number:", selectedPhone.getNumber());
                if (newNumber == null) return;
                selectedPhone.setRegionCode(newRegion.trim());
                selectedPhone.setNumber(newNumber.trim());
                saveAllContacts();
                JOptionPane.showMessageDialog(dialog, "Phone updated.");
                dialog.dispose();
                viewContact(); // refresh
            }
        });

        // --- Remove Phone(s) ---
        removePhoneBtn.addActionListener(e -> {
            java.util.List<PhoneNumber> phoneNumbers = selected.getNumbers();
            if (phoneNumbers.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "No phone numbers to remove.");
                return;
            }
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            java.util.List<JCheckBox> checkBoxes = new ArrayList<>();
            for (PhoneNumber phone : phoneNumbers) {
                JCheckBox cb = new JCheckBox(phone.toString());
                checkBoxes.add(cb);
                panel.add(cb);
            }
            int result = JOptionPane.showConfirmDialog(dialog, panel,
                    "Select phones to remove", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                java.util.List<PhoneNumber> toRemove = new ArrayList<>();
                for (int i = 0; i < checkBoxes.size(); i++) {
                    if (checkBoxes.get(i).isSelected()) {
                        toRemove.add(phoneNumbers.get(i));
                    }
                }
                if (toRemove.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "No phone numbers selected.");
                    return;
                }
                int confirm = JOptionPane.showConfirmDialog(dialog,
                        "Are you sure you want to delete the selected phone(s)?",
                        "Confirm Remove", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    selected.getNumbers().removeAll(toRemove);
                    selected.setChanged();
                    selected.notifyObservers();
                    saveAllContacts();
                    JOptionPane.showMessageDialog(dialog, "Phone number(s) removed.");
                    dialog.dispose();
                    viewContact(); // refresh
                }
            }
        });

        buttonPanel.add(addPhoneNumber);
        buttonPanel.add(updatePhoneBtn);
        buttonPanel.add(removePhoneBtn);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    /* ---------- Utility ---------- */

    private void updateListModel(java.util.List<Contact> newContacts) {
        listModel.clear();
        if (newContacts != null) {
            newContacts.forEach(listModel::addElement);
        }
    }

    private void sortContactsBy(Function<Contact, String> keyExtractor) {
        if (contacts == null || contacts.isEmpty()) return;
        contacts.sort(Comparator.comparing(keyExtractor, String.CASE_INSENSITIVE_ORDER));
        updateListModel(contacts);
    }

    private void searchContacts() {
        String raw = searchField.getText();
        if (raw == null) raw = "";
        String query = normalize(raw).toLowerCase(Locale.ROOT).trim();
        if (query.isEmpty()) {
            updateListModel(contacts);
            return;
        }
        java.util.List<Contact> filtered = new ArrayList<>();
        for (Contact contact : contacts) {
            String nom = normalize(Optional.ofNullable(contact.getNom()).orElse("")).toLowerCase(Locale.ROOT);
            String prenom = normalize(Optional.ofNullable(contact.getPrenom()).orElse("")).toLowerCase(Locale.ROOT);
            String ville = normalize(Optional.ofNullable(contact.getVille()).orElse("")).toLowerCase(Locale.ROOT);
            if (nom.contains(query) || prenom.contains(query) || ville.contains(query)) {
                filtered.add(contact);
            }
        }
        updateListModel(filtered);
    }

    private static String normalize(String s) {
        if (s == null) return "";
        String n = java.text.Normalizer.normalize(s, java.text.Normalizer.Form.NFD);
        return n.replaceAll("\\p{M}", "");
    }
}