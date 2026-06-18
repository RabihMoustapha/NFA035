package views;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.*;
import java.util.*;

import Models.Contact;
import Models.Group;

public class GroupsView extends JFrame {

    private java.util.List<Group> groups;
    private Group currentGroup;

    private JButton addGroupButton = new JButton("Add New Group");
    private JButton updateGroupButton = new JButton("Update Group");
    private JButton deleteGroupButton = new JButton("Delete Group");
    private JButton viewGroupButton = new JButton("View Group");
    private JButton cancelButton = new JButton("Cancel");
    private JTextField searchField = new JTextField(20);
    private DefaultListModel<Group> groupModel = new DefaultListModel<>();
    private JList<Group> groupList = new JList<>(groupModel);

    public GroupsView(Group dummy) {
        this.groups = new ArrayList<>();
        loadGroups();

        setTitle("Groups");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(new JLabel("Search:"));
        topPanel.add(searchField);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addGroupButton);
        buttonPanel.add(updateGroupButton);
        buttonPanel.add(deleteGroupButton);
        buttonPanel.add(viewGroupButton);
        buttonPanel.add(cancelButton);

        JScrollPane scrollPane = new JScrollPane(groupList);
        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        updateListModel(groups);

        addGroupButton.addActionListener(e -> new NewGroupView(new Group(), this));
        cancelButton.addActionListener(e -> dispose());
        updateGroupButton.addActionListener(e -> updateSelectedGroup());
        deleteGroupButton.addActionListener(e -> deleteSelectedGroup());
        viewGroupButton.addActionListener(e -> viewSelectedGroup());

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { searchGroups(); }
            public void removeUpdate(DocumentEvent e) { searchGroups(); }
            public void insertUpdate(DocumentEvent e) { searchGroups(); }
        });

        setVisible(true);
    }

    /* ---------- Data persistence ---------- */

    public void loadGroups() {
        groups.clear();
        groupModel.clear();
        File file = new File("Groups.dat");
        if (!file.exists() || file.length() == 0) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            while (true) {
                try {
                    Group g = (Group) ois.readObject();
                    groups.add(g);
                    groupModel.addElement(g);
                } catch (EOFException eof) {
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "Error loading groups: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveGroups() {
        File file = new File("Groups.dat");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            for (Group g : groups) {
                oos.writeObject(g);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving groups: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /* ---------- CRUD ---------- */

    private void updateSelectedGroup() {
        Group selected = groupList.getSelectedValue();
        if (selected != null) {
            new GroupUpdateView(selected, this);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a group to update");
        }
    }

    private void deleteSelectedGroup() {
        Group selected = groupList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a group to delete");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete group \"" + selected.getNom() + "\"?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            groups.remove(selected);
            updateListModel(groups);
            saveGroups();
        }
    }

    private void viewSelectedGroup() {
        Group selected = groupList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a group to view");
            return;
        }
        this.currentGroup = selected;

        JDialog dialog = new JDialog(this, "Group Details", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JTextArea infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setText("Name: " + selected.getNom() + "\n"
                + "Description: " + selected.getDescription() + "\n\nContacts:\n");
        java.util.List<Contact> contacts = selected.getContacts();
        for (Contact c : contacts) {
            infoArea.append(c.toString() + "\n");
        }
        dialog.add(new JScrollPane(infoArea), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addContactBtn = new JButton("Add Contact");
        JButton updateContactBtn = new JButton("Update Contact");
        JButton removeContactBtn = new JButton("Remove Contact");

        // --- Add Contact ---
        addContactBtn.addActionListener(e -> {
            java.util.List<Contact> allContacts = loadAllContacts();
            if (allContacts.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "No contacts found.");
                return;
            }
            JPanel panel = new JPanel(new GridLayout(0, 1));
            java.util.List<JCheckBox> checkBoxes = new ArrayList<>();
            for (Contact c : allContacts) {
                JCheckBox cb = new JCheckBox(c.toString());
                checkBoxes.add(cb);
                panel.add(cb);
            }
            int result = JOptionPane.showConfirmDialog(dialog, new JScrollPane(panel),
                    "Select Contacts to Add", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                boolean added = false;
                for (int i = 0; i < checkBoxes.size(); i++) {
                    if (checkBoxes.get(i).isSelected()) {
                        Contact c = allContacts.get(i);
                        if (!selected.getContacts().contains(c)) {
                            selected.ajouterContact(c);
                            added = true;
                        }
                    }
                }
                if (added) {
                    saveGroups();
                    JOptionPane.showMessageDialog(dialog, "Selected contacts added.");
                    dialog.dispose();
                    viewSelectedGroup(); // refresh
                } else {
                    JOptionPane.showMessageDialog(dialog, "No new contacts were selected.");
                }
            }
        });

        // --- Update Contact ---
        updateContactBtn.addActionListener(e -> {
            if (contacts.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "No contacts to update.");
                return;
            }
            Contact selectedContact = (Contact) JOptionPane.showInputDialog(dialog,
                    "Select a contact to update:", "Update Contact",
                    JOptionPane.PLAIN_MESSAGE, null, contacts.toArray(), contacts.get(0));
            if (selectedContact != null) {
                JTextField nomField = new JTextField(selectedContact.getNom());
                JTextField prenomField = new JTextField(selectedContact.getPrenom());
                JTextField villeField = new JTextField(selectedContact.getVille());
                Object[] fields = { "Nom:", nomField, "Prenom:", prenomField, "Ville:", villeField };
                int result = JOptionPane.showConfirmDialog(dialog, fields, "Update Contact",
                        JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    String nom = nomField.getText().trim();
                    String prenom = prenomField.getText().trim();
                    String ville = villeField.getText().trim();
                    if (!nom.isEmpty() && !prenom.isEmpty()) {
                        selectedContact.setNom(nom);
                        selectedContact.setPrenom(prenom);
                        selectedContact.setVille(ville);
                        saveGroups();
                        dialog.dispose();
                        viewSelectedGroup(); // refresh
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Nom and Prenom cannot be empty.");
                    }
                }
            }
        });

        // --- Remove Contact(s) ---
        removeContactBtn.addActionListener(e -> {
            if (contacts.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "No contacts to remove.");
                return;
            }
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            java.util.List<JCheckBox> checkBoxes = new ArrayList<>();
            for (Contact c : contacts) {
                JCheckBox cb = new JCheckBox(c.toString());
                checkBoxes.add(cb);
                panel.add(cb);
            }
            int result = JOptionPane.showConfirmDialog(dialog, panel,
                    "Select contacts to remove", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                java.util.List<Contact> toRemove = new ArrayList<>();
                for (int i = 0; i < checkBoxes.size(); i++) {
                    if (checkBoxes.get(i).isSelected()) {
                        toRemove.add(contacts.get(i));
                    }
                }
                if (toRemove.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "No contacts selected.");
                    return;
                }
                int confirm = JOptionPane.showConfirmDialog(dialog,
                        "Are you sure you want to delete the selected contact(s)?",
                        "Confirm Remove", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    for (Contact c : toRemove) {
                        selected.deleteContact(c);
                    }
                    saveGroups();
                    JOptionPane.showMessageDialog(dialog, "Contact(s) removed.");
                    dialog.dispose();
                    viewSelectedGroup(); // refresh
                }
            }
        });

        buttonPanel.add(addContactBtn);
        buttonPanel.add(updateContactBtn);
        buttonPanel.add(removeContactBtn);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private java.util.List<Contact> loadAllContacts() {
        java.util.List<Contact> allContacts = new ArrayList<>();
        File file = new File("Contacts.dat");
        if (!file.exists()) return allContacts;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            while (true) {
                try {
                    allContacts.add((Contact) ois.readObject());
                } catch (EOFException eof) {
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "Error loading contacts: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        return allContacts;
    }

    /* ---------- Utilities ---------- */

    private void updateListModel(java.util.List<Group> newGroups) {
        groupModel.clear();
        if (newGroups != null) {
            newGroups.forEach(groupModel::addElement);
        }
    }

    private void searchGroups() {
        String raw = searchField.getText();
        if (raw == null) raw = "";
        String query = normalize(raw).toLowerCase(Locale.ROOT).trim();
        if (query.isEmpty()) {
            updateListModel(groups);
            return;
        }
        java.util.List<Group> filtered = new ArrayList<>();
        for (Group group : groups) {
            String nom = normalize(Optional.ofNullable(group.getNom()).orElse("")).toLowerCase(Locale.ROOT);
            String desc = normalize(Optional.ofNullable(group.getDescription()).orElse("")).toLowerCase(Locale.ROOT);
            if (nom.contains(query) || desc.contains(query)) {
                filtered.add(group);
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