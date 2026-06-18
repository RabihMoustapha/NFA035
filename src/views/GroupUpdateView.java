package views;

import javax.swing.*;
import java.awt.GridLayout;
import java.io.*;
import java.util.*;

import Models.Group;

public class GroupUpdateView extends JFrame {

    private GroupsView parent;
    private JTextField nameField;
    private JTextArea descriptionArea;
    private JButton saveButton, cancelButton;
    private Group group;

    public GroupUpdateView(Group group, GroupsView parent) {
        this.group = group;
        this.parent = parent;
        setTitle("Update Group");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        nameField = new JTextField(group.getNom(), 20);
        descriptionArea = new JTextArea(group.getDescription(), 5, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");

        JPanel formPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        formPanel.add(new JLabel("Group Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Description:"));
        formPanel.add(new JScrollPane(descriptionArea));
        formPanel.add(saveButton);
        formPanel.add(cancelButton);

        add(formPanel);

        saveButton.addActionListener(e -> saveUpdatedGroup());
        cancelButton.addActionListener(e -> dispose());

        setVisible(true);
    }

    private void saveUpdatedGroup() {
        String newName = nameField.getText().trim();
        String newDesc = descriptionArea.getText().trim();

        if (newName.isEmpty() || newDesc.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        group.setNom(newName);
        group.setDescription(newDesc);

        java.util.List<Group> allGroups = loadAllGroups();
        for (int i = 0; i < allGroups.size(); i++) {
            if (allGroups.get(i).equals(group)) {
                allGroups.set(i, group);
                break;
            }
        }
        saveAllGroups(allGroups);

        JOptionPane.showMessageDialog(this, "Group updated successfully.");
        parent.loadGroups();
        dispose();
    }

    private java.util.List<Group> loadAllGroups() {
        java.util.List<Group> list = new ArrayList<>();
        File file = new File("Groups.dat");
        if (!file.exists()) return list;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            while (true) {
                try {
                    list.add((Group) ois.readObject());
                } catch (EOFException eof) {
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load groups: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        return list;
    }

    private void saveAllGroups(java.util.List<Group> list) {
        File file = new File("Groups.dat");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            for (Group g : list) {
                oos.writeObject(g);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Failed to save group: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}