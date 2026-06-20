# Contact & Group Management System

A Java Swing application for managing contacts and groups with support for phone numbers, persistent storage, and real‑time updates via the Observer pattern.

## 📖 Description

This system provides a user‑friendly interface to manage a personal or professional contact directory. It allows users to:

- Create, view, update, and delete **contacts**, each with one or more phone numbers.
- Organise contacts into **groups**.
- Add, update, or remove contacts within a group.
- Search and sort contacts and groups.
- Persist all data in binary files using Java serialization.

The application implements the **Observer pattern** to automatically refresh views when data changes (e.g., when a contact is updated, all open views reflect the change immediately).

## ✨ Features

### 👤 Contact Management
- Add, edit, and delete contacts (first name, last name, city).
- Each contact can have **multiple phone numbers** (region code + number).
- View detailed contact information including all phone numbers.
- Sort contacts by first name, last name, or city.
- Real‑time search filtering as you type.

### 📂 Group Management
- Create, edit, and delete groups with a name and description.
- Add existing contacts to a group, update contact details inside a group, or remove contacts from a group.
- View group details with a list of all members.

### 🔍 Search & Sort
- Live search on contacts (by name or city) and groups (by name or description).
- Sorting of contacts by any field with a single click.

### 💾 Data Persistence
- All data stored in three binary files:
  - `Contacts.dat` – all contacts
  - `Groups.dat` – all groups
  - `GroupsContacts.dat` – relationships (optional, but group contacts are serialized within groups)
- Uses Java `ObjectInputStream`/`ObjectOutputStream` for robust serialization.

### 🔔 Observer Pattern
- Custom `MyObservable` and `MyObserver` implementations ensure that changes made in one view (e.g., updating a contact) are automatically reflected in all other open windows (e.g., group member lists, contact lists).

## 🛠️ Technologies Used

- **Java 8+** – Core language
- **Swing** – Graphical user interface
- **Serialization** – Data persistence
- **Observer Pattern** – Custom implementation for reactive UI updates
- **UUID** – Unique identifiers for contacts and groups

## 🚀 Getting Started

### Prerequisites
- Java Development Kit (JDK) 8 or higher
- Any Java IDE (Eclipse, IntelliJ IDEA, NetBeans) or command‑line build tool

### Running the Application

1. **Clone the repository**  
   ```bash
   git clone https://github.com/yourusername/contact-manager.git
   cd contact-manager
   ```

2. **Compile and run**  
   From your IDE, open the `src/views/MainView.java` class and run it.

   Or from the command line:
   ```bash
   javac -d bin src/**/*.java
   java -cp bin views.MainView
   ```

3. **First run**  
   The application will create empty data files on the first launch. Start by adding a few contacts and groups to explore the features.

### Building a JAR (optional)
```bash
jar cf contactmanager.jar -C bin .
java -jar contactmanager.jar
```

## 📖 Usage

### Main Menu
- Click **Contacts** to open the contact management window.
- Click **Groups** to open the group management window.

### Contacts View
- **Add New Contact** – fill in the form.
- **Update Contact** – select a contact and modify its fields.
- **Delete Contact** – confirm deletion.
- **View Contact** – open a detailed dialog to manage phone numbers (add, update, remove).
- **Sort** – use the buttons to sort by first name, last name, or city.
- **Search** – type in the search box to filter contacts live.

### Groups View
- **Add New Group** – provide a name and description.
- **Update Group** – change the group's name or description.
- **Delete Group** – remove the group (contacts are not deleted).
- **View Group** – open a dialog to add, update, or remove contacts from the group.
- **Search** – filter groups by name or description.

## 📁 Project Structure

```
NFA035-main/
├── src/
│   ├── Models/                     – Domain entities
│   │   ├── Contact.java            – Contact with phone numbers
│   │   ├── Group.java              – Group with contacts
│   │   └── PhoneNumber.java        – Phone number (region + number)
│   ├── Observables/                – Custom observable base
│   │   └── MyObservable.java       – Observable implementation
│   ├── Observers/                  – Observer interface
│   │   └── MyObserver.java         – Observer callback
│   └── views/                      – GUI windows
│       ├── MainView.java           – Entry point with two buttons
│       ├── ContactsView.java       – Contact list management
│       ├── ContactUpdateView.java  – Edit contact form
│       ├── NewContactView.java     – New contact form
│       ├── GroupsView.java         – Group list management
│       ├── GroupUpdateView.java    – Edit group form
│       └── NewGroupView.java       – New group form
├── bin/                            – Compiled classes
├── Contacts.dat                    – Serialized contacts
├── Groups.dat                      – Serialized groups
├── GroupsContacts.dat              – Serialized group‑contact links (not used directly)
└── README.md
```

## 🤝 Contributing

Contributions are welcome! Feel free to open issues or submit pull requests with improvements, bug fixes, or new features.

## 📄 License

This project is for educational purposes. You may use and modify it under the terms of the [MIT License](LICENSE).
