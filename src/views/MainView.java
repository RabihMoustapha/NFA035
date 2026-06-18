package views;

import javax.swing.*;
import java.awt.FlowLayout;
import Models.Contact;
import Models.Group;

public class MainView extends JFrame {

    private JButton contactsButton = new JButton("Contacts");
    private JButton groupsButton = new JButton("Groups");

    public MainView() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Contact Management System");
        setSize(400, 150);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new FlowLayout());
        panel.add(contactsButton);
        panel.add(groupsButton);
        add(panel);

        contactsButton.addActionListener(e -> new ContactsView(new Contact()));
        groupsButton.addActionListener(e -> new GroupsView(new Group()));

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainView());
    }
}