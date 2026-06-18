package views;

import javax.swing.*;
import java.awt.GridLayout;
import java.io.*;
import java.util.*;

import Models.Group;

public class NewGroupView extends JFrame {

    private GroupsView parent;
    private java.util.List<Group> groups;
    private JTextField groupNameField = new JTextField(15);
    private JTextArea descriptionArea = new JTextArea(3, 20);
    private JButton saveButton = new JButton("Save Group");
    private JButton cancelButton = new JButton("Cancel");

    public NewGroupView(Group dummy, GroupsView parent) {
        this.parent = parent;
        this.groups = new ArrayList<>();
        loadGroupsData();

        setTitle("New Group");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Group Name:"));
        panel.add(groupNameField);
        panel.add(new JLabel("Description:"));
        panel.add(new JScrollPane(descriptionArea));
        panel.add(saveButton);
        panel.add(cancelButton);
        add(panel);

        saveButton.addActionListener(e -> addGroup());
        cancelButton.addActionListener(e -> dispose());

        setVisible(true);
    }

    private void addGroup() {
        String name = groupNameField.getText().trim();
        String desc = descriptionArea.getText().trim();

        if (name.isEmpty() || desc.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Group newGroup = new Group(name, desc);

        for (Group g : groups) {
            if (g.hasSameData(newGroup)) {
                JOptionPane.showMessageDialog(this, "The group is already created.");
                return;
            }
        }

        groups.add(newGroup);
        saveAllGroups();

        JOptionPane.showMessageDialog(this, "Group saved successfully!");
        parent.loadGroups();
        dispose();
    }

    private void loadGroupsData() {
        groups.clear();
        File file = new File("Groups.dat");
        if (!file.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            while (true) {
                try {
                    groups.add((Group) ois.readObject());
                } catch (EOFException eof) {
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Error loading groups: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveAllGroups() {
        File file = new File("Groups.dat");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            for (Group g : groups) {
                oos.writeObject(g);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving group: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}