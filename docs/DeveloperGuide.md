---
layout: page
title: Developer Guide
---
* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

* This project is based on the AddressBook-Level3 project created by the SE-EDU initiative.
* Libraries used: JavaFX, Jackson, JUnit5
* The undo and redo features were inspired by the proposed implementation found in [AB3's Developer Guide](https://se-education.org/addressbook-level3/DeveloperGuide.html#proposed-undoredo-feature).

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

<div markdown="span" class="alert alert-primary">

:bulb: **Tip:** The `.puml` files used to create diagrams in this document can be found in the `docs/diagrams` folder. Refer to the [_PlantUML Tutorial_ at se-edu/guides](https://se-education.org/guides/tutorials/plantUml.html) to learn how to create and edit diagrams.
</div>

### Architecture

<img src="images/ArchitectureDiagram.png" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

Given below is a quick overview of main components and how they interact with each other.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete 1`.

<img src="images/ArchitectureSequenceDiagram.png" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* implements its functionality using a concrete `{Component Name}Manager` class (which follows the corresponding API `interface` mentioned in the previous point.

For example, the `Logic` component defines its API in the `Logic.java` interface and implements its functionality using the `LogicManager.java` class which follows the `Logic` interface. Other components interact with a given component through its interface rather than the concrete class (reason: to prevent outside component's being coupled to the implementation of a component), as illustrated in the (partial) class diagram below.

<img src="images/ComponentManagers.png" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/Ui.java)

![Structure of the UI Component](images/UiClassDiagram.png)

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `PersonListPanel`, `StatusBarFooter` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class which captures the commonalities between classes that represent parts of the visible GUI.

The `UI` component uses the JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [`MainWindow`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/resources/view/MainWindow.fxml)

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component, as it displays `Person` object residing in the `Model`.

### Logic component

**API** : [`Logic.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<img src="images/LogicClassDiagram.png" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1")` API call as an example.

![Interactions Inside the Logic Component for the `delete 1` Command](images/DeleteSequenceDiagram.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</div>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, it is passed to an `AddressBookParser` object which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
   Note that although this is shown as a single step in the diagram above (for simplicity), in the code it can take several interactions (between the command object and the `Model`) to achieve.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned back from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<img src="images/ParserClasses.png" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name e.g., `AddCommandParser`) which uses the other classes shown above to parse the user command and create a `XYZCommand` object (e.g., `AddCommand`) which the `AddressBookParser` returns back as a `Command` object.
* All `XYZCommandParser` classes (e.g., `AddCommandParser`, `DeleteCommandParser`, ...) inherit from the `Parser` interface so that they can be treated similarly where possible e.g, during testing.

### Model component
**API** : [`Model.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/model/Model.java)

<img src="images/ModelClassDiagram.png" width="450" />


The `Model` component,

* stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* stores the currently 'selected' `Person` objects (e.g., results of a search query) as a separate _filtered_ list which is exposed to outsiders as an unmodifiable `ObservableList<Person>` that can be 'observed' e.g. the UI can be bound to this list so that the UI automatically updates when the data in the list change.
* stores a `UserPref` object that represents the user’s preferences. This is exposed to the outside as a `ReadOnlyUserPref` objects.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

<div markdown="span" class="alert alert-info">:information_source: **Note:** An alternative (arguably, a more OOP) model is given below. It has a `Tag` list in the `AddressBook`, which `Person` references. This allows `AddressBook` to only require one `Tag` object per unique tag, instead of each `Person` needing their own `Tag` objects.<br>

<img src="images/BetterModelClassDiagram.png" width="450" />

</div>


### Storage component

**API** : [`Storage.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/storage/Storage.java)

<img src="images/StorageClassDiagram.png" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* inherits from both `AddressBookStorage` and `UserPrefStorage`, which means it can be treated as either one (if only the functionality of only one is needed).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### Add feature

#### Implementation

The image below shows the class diagram of a Person object and its related class attributes.

![Person Class Diagram](images/PersonClassDiagram.png)

The Person object is made up of several attributes:
* `Name`: The name of the patient.
* `Phone`: The phone number of the patient.
* `Email`: The email of the patient.
* `Address`: The address of the patient.
* `Doctor`: The doctor assigned to the patient.
* `Emergency Contact`: A list of emergency contacts of the patient.
* `Tags`: Additional information about the patient.

The Doctor object is also made up of attributes:
* `Doctor Name`: The name and title of the doctor.
* `Phone`: The phone number of the doctor.
* `Email`: The email of the doctor.

The Emergency Contact object is also made up of attributes:
* `Name`: The name of the emergency contact.
* `Phone`: The phone number of the emergency contact.
* `Relationship`: The relationship of the emergency contact to the patient.

#### Feature details

1. MedConnect will verify that the parameters supplied by the user follow a set of relevant restrictions for the respective parameters.
2. If any invalid parameter is provided, an error will be thrown, informing the user which parameter violates the restrictions. The format for the valid input for that parameter will be displayed to the user.
3. If all parameters are valid, a new `Person` entry will be created and stored in the `VersionedAddressBook`.

#### Design Considerations:

**Aspect: The required input of parameters:**

* **Alternative 1 (current choice):** Make all parameters compulsory, except Tags.
  * Pros: Will not have missing data when it is needed in an emergency.
  * Cons: Add Command is lengthy to type out, might be hard to remember the syntax.
* **Alternative 2:** Make only a few specific parameters compulsory.
  * Pros: Patient registration will be faster.
  * Cons: If user forgets to update missing details, there will be no one to contact in an emergency.

We opted for Alternative 1 to make almost all parameters compulsory as the autocomplete feature we implemented will aid users in typing out the Add Command.

### Undo/redo feature

#### Implementation

The proposed undo/redo mechanism is facilitated by `VersionedAddressBook`. It extends `AddressBook` with an undo/redo history, stored internally as an `addressBookStateList` and `currentStatePointer`. Additionally, it implements the following operations:

* `VersionedAddressBook#commit()` — Saves the current address book state in its history.
* `VersionedAddressBook#undo()` — Restores the previous address book state from its history.
* `VersionedAddressBook#redo()` — Restores a previously undone address book state from its history.

These operations are exposed in the `Model` interface as `Model#commitAddressBook()`, `Model#undoAddressBook()` and `Model#redoAddressBook()` respectively.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedAddressBook` will be initialized with the initial address book state, and the `currentStatePointer` pointing to that single address book state.

![UndoRedoState0](images/UndoRedoState0.png)

Step 2. The user executes `delete 5` command to delete the 5th person in the address book. The `delete` command calls `Model#commitAddressBook()`, causing the modified state of the address book after the `delete 5` command executes to be saved in the `addressBookStateList`, and the `currentStatePointer` is shifted to the newly inserted address book state.

![UndoRedoState1](images/UndoRedoState1.png)

Step 3. The user executes `add n/David …​` to add a new person. The `add` command also calls `Model#commitAddressBook()`, causing another modified address book state to be saved into the `addressBookStateList`.

![UndoRedoState2](images/UndoRedoState2.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** If a command fails its execution, it will not call `Model#commitAddressBook()`, so the address book state will not be saved into the `addressBookStateList`.

</div>

Step 4. The user now decides that adding the person was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undoAddressBook()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous address book state, and restores the address book to that state.

![UndoRedoState3](images/UndoRedoState3.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** If the `currentStatePointer` is at index 0, pointing to the initial AddressBook state, then there are no previous AddressBook states to restore. The `undo` command uses `Model#canUndoAddressBook()` to check if this is the case. If so, it will return an error to the user rather
than attempting to perform the undo.

</div>

The following sequence diagram shows how an undo operation goes through the `Logic` component:

![UndoSequenceDiagram](images/UndoSequenceDiagram-Logic.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifeline for `UndoCommand` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline reaches the end of diagram.

</div>

Similarly, how an undo operation goes through the `Model` component is shown below:

![UndoSequenceDiagram](images/UndoSequenceDiagram-Model.png)

The `redo` command does the opposite — it calls `Model#redoAddressBook()`, which shifts the `currentStatePointer` once to the right, pointing to the previously undone state, and restores the address book to that state.

<div markdown="span" class="alert alert-info">:information_source: **Note:** If the `currentStatePointer` is at index `addressBookStateList.size() - 1`, pointing to the latest address book state, then there are no undone AddressBook states to restore. The `redo` command uses `Model#canRedoAddressBook()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.

</div>

Step 5. The user then decides to execute the command `list`. Commands that do not modify the address book, such as `list`, will usually not call `Model#commitAddressBook()`, `Model#undoAddressBook()` or `Model#redoAddressBook()`. Thus, the `addressBookStateList` remains unchanged.

![UndoRedoState4](images/UndoRedoState4.png)

Step 6. The user executes `clear`, which calls `Model#commitAddressBook()`. Since the `currentStatePointer` is not pointing at the end of the `addressBookStateList`, all address book states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `add n/David …​` command. This is the behavior that most modern desktop applications follow.

![UndoRedoState5](images/UndoRedoState5.png)

The following activity diagram summarizes what happens when a user executes a new command:

<img src="images/CommitActivityDiagram.png" width="250" />

#### Design considerations:

**Aspect: How undo & redo executes:**

* **Alternative 1 (current choice):** Saves the entire address book.
  * Pros: Easy to implement.
  * Cons: May have performance issues in terms of memory usage.

* **Alternative 2:** Individual command knows how to undo/redo by
  itself.
  * Pros: Will use less memory (e.g. for `delete`, just save the person being deleted).
  * Cons: We must ensure that the implementation of each individual command are correct.

_{more aspects and alternatives to be added}_

### Data archiving

#### Implementation

The archive functionality in MedConnect is facilitated by the `ModelManager` class. It handles the archiving, listing, loading, and deleting of archived contact data. The `ModelManager` interacts with the `Model` and `Storage` components to manage archived data.

For example, the sequence diagram below illustrates the interactions within the `ModelManager` component when the `archive` command is executed.

![ArchiveSequenceDiagram.png](images%2FArchiveSequenceDiagram.png)

--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, configuration, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [Configuration guide](Configuration.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product Scope

---

#### **Target User Profile:**

- **User Role:** Healthcare Administrator
- **Workplace:** Old folks home for dementia patients
- **Responsibilities:**
    - Manage and update contact details for patients, doctors, and next-of-kin.
    - Respond quickly to emergency situations by accessing relevant contacts.
    - Maintain communication efficiency with minimal manual processes.

- **Key Characteristics:**
    - Handles large volumes of contact data on a daily basis.
    - Prefers using desktop applications over mobile or web-based alternatives.
    - Skilled at typing and prefers keyboard shortcuts over mouse interactions for speed.
    - Comfortable with using command-line interfaces (CLI) for fast data entry and retrieval.

#### **Value Proposition:**

MedConnect offers a **streamlined contact management system** tailored for healthcare administrators. Its key features include:

- **Efficient Lookup and Update:** Quickly find and update contact information for patients, their emergency contacts, and healthcare staff.
- **Time-Sensitive Operations:** When every second counts, MedConnect ensures that administrators can contact the right person immediately.
- **Command-Line First:** Optimized for users who prefer CLI, allowing for rapid data entry and navigation without reliance on graphical interfaces.
- **Comprehensive Contact Database:** Centralizes all relevant contact details, reducing the need for multiple systems and improving response times in emergencies.

---

### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`
### Beginner User Stories


| Priority | As a …​                  | I want to …​                                    | So that I can…​                                                            |
|----------|--------------------------|-------------------------------------------------|----------------------------------------------------------------------------|
| `* * *`  | new user                 | familiarise myself with the app                 | play around with dummy data without compromising real patient data         |
| `* * *`  | healthcare administrator | add new doctors and patients                    | easily reach out to them when needed                                       |
| `* * *`  | healthcare administrator | update contact details                          | ensure all contact information is accurate and current                     |
| `* *`    | healthcare administrator | search contacts by name or assigned doctor      | quickly find and connect with the right person in high-pressure situations |
| `* * *`  | healthcare administrator | view patient emergency contact details          | notify next-of-kin during urgent medical events                            |
| `* *`    | healthcare administrator | add multiple emergency contacts for each person | reach different emergency contacts when one is uncontactable               |
| `* * *`  | healthcare administrator | assign doctors to patients                      | easily track which doctor is responsible for each patient                  |
| `* * *`  | healthcare administrator | delete outdated patient contacts                | ensure all information is relevant and current                             |


### Intermediate User Stories

| Priority | As a …                   | I want to …                              | So that I can…                                                               |
|----------|--------------------------|------------------------------------------|------------------------------------------------------------------------------|
| `* *`    | healthcare administrator | filter contacts by their doctor          | view a consolidated list of all the patients a doctor is responsible for     |
| `* *`    | healthcare administrator | sort patients by their admission time    | provide appropriate care to longer-term patients                             |
| `* *`    | healthcare administrator | tag important notes to patients          | remember special considerations about certain patients                       |
| `* *`    | healthcare administrator | archive outdated contacts                | maintain a clean and relevant contact list without losing historical records |
| `* *`    | healthcare administrator | load backup archived data                | restore a backup copy in case of data corruption or user error              |

### Advanced User Stories

| Priority | As a …                   | I want to …                                   | So that I can…                                                       |
|----------|--------------------------|-----------------------------------------------|----------------------------------------------------------------------|
| `*`      | healthcare administrator | view a history of previous interactions       | have a complete record of communications for reference               |
| `* *`    | healthcare administrator | import contact data in bulk                   | keep the database up-to-date without manual entry                    |
| `* *`    | healthcare administrator | export contact information                    | provide it to others or have a backup in case of system failures     |
| `* *`    | healthcare administrator | create templates for emergency communications | send critical messages quickly during emergencies                    |
| `*`      | healthcare administrator | secure access with user authentication        | ensure only authorized personnel can access or update sensitive data |

---

### Use cases

(For all use cases below, the **System** is the `AddressBook` and the **Actor** is the `user`, unless specified otherwise)

---
### Use Case: Add a New Contact

**System:** MedConnect

**Actor:** Healthcare Administrator

#### **Main Success Scenario (MSS):**
1. User requests to add a new patient contact.
2. MedConnect prompts the user to enter patient details:
    - Name
    - Phone Number
    - Address
    - Email
    - Doctor Name
    - Doctor Phone Number
    - Doctor Email
    - Emergency Contact Name
    - Emergency Contact Phone Number
    - Emergency Contact Relationship to Patient
3. User enters the required details.
4. MedConnect validates the provided information.
5. MedConnect successfully adds the new contact to the address book.
6. MedConnect confirms that the contact was added successfully.

   **Use case ends.**

#### **Extensions:**

**3a.** The entered details are invalid (e.g., phone number contains letters).
- **3a1.** MedConnect informs the user of the invalid details.
- **3a2.** User corrects the invalid details and resubmits.

  **Use case resumes from step 4.**

**3b.** Some required fields are missing.
- **3b1.** MedConnect prompts the user to complete the missing fields.
- **3b2.** User provides the missing information.

  **Use case resumes from step 4.**

**3c.** There are duplicate fields provided (e.g., 2 name fields).
- **3c1.** MedConnect informs the user of the duplicate field(s).
- **3c2.** User removes the duplicate parameters and resubmits.

  **Use case resumes from step 4.**

**6a.** The contact already exists in the system (e.g., duplicate phone number).
- **6a1.** MedConnect notifies the user of the duplicate entry.

  **Use case ends.**

---


### Use Case: Edit a Contact

**System:** MedConnect
**Actor:** Healthcare Administrator


#### **Main Success Scenario (MSS):**
1. User requests to list all contacts.
2. MedConnect retrieves and shows a list of all contacts.
3. User requests to edit a patient's details.
4. MedConnect prompts the user to enter the following details:
    - Index of the patient to be edited
    - New data of the field(s) to be edited
5. MedConnect validates the provided information.
6. MedConnect successfully edits the contact with the new data.
7. MedConnect confirms that the contact was edited successfully.

   **Use case ends.**

#### **Extensions:**

**2a.** The patient list is empty.

  - **Use case ends.**

**4a.** The given index is invalid (e.g., out of range).
- **4a1.** MedConnect informs the user of the invalid index.

  **Use case resumes from step 3.**

**4b.** The entered details are invalid (e.g., phone number contains letters).
- **4b1.** MedConnect informs the user of the invalid details.
- **4b2.** User corrects the invalid details and resubmits.

  **Use case resumes from step 5.**

---

### Use Case: Delete a Contact

**System:** MedConnect

**Actor:** Healthcare Administrator


#### **Main Success Scenario (MSS):**
1. User requests to list all contacts.
2. MedConnect retrieves and shows a list of all contacts.
3. User requests to delete a specific contact by index.
4. MedConnect removes the contact from the database.

   **Use case ends.**


#### **Extensions:**

**2a.** The contact list is empty.


- **Use case ends.**


**3a.** The given index is invalid (e.g., out of range).
- **3a1.** MedConnect informs the user of the invalid index.

  **Use case resumes from step 2.**

---

### Use Case: Add Emergency Contacts

**System:** MedConnect

**Actor:** Healthcare Administrator

#### **Main Success Scenario (MSS):**
1. User requests to list all contacts.
2. MedConnect retrives and shows a list of all contacts.
3. User requests to add a new emergency contact to a patient.
4. MedConnect prompts the user to enter the following details:
    - Index of the patient
    - Emergency Contact Name
    - Emergency Contact Phone Number
    - Emergency Contact Relationship to Patient
5. User enters the required details.
6. MedConnect adds the emergency contact to the patient's details.

   **Use case ends.**

#### **Extensions:**

**2a.** The contact list is empty.

- **Use case ends.**

**5a.** The index given is out of range.
- **5a1.** MedConnect informs the user of the invalid index.

  **Use case resumes from step 3.**

**5b.** The emergency contact already exists under the patient. (e.g., duplicate phone number).
- **5b1.** MedConnect notifies the user that the emergency contact already exists.

  **Use case ends.**

**5c.** The emergency contact relationship provided is invalid.
- **5c1.** MedConnect informs the user of possible relationship types.
- **5c2.** User corrects the invalid relationship type and resubmits.

  **Use case resumes from step 6.**

---

### Use Case: Find Contacts

**System:** MedConnect
**Actor:** Healthcare Administrator

#### **Main Success Scenario (MSS):**
1. User requests to find a patient by their name.
2. MedConnect prompts the user to provide a name to search for.
3. User provides a name.
4. MedConnect returns a list of patients who matches the provided name.

   **Use case ends.**

#### **Extensions:**

**3a.** User provides a blank name.
- **3a1.** MedConnect notifies the user to provide a name.

  **Use case resumes from step 3.**

**4a.** There are no patients who match the provided name.
- **4a1.** MedConnect returns a list of 0 patients.

  **Use case ends.**

---

### Use Case: Archive Contacts

**System:** MedConnect
**Actor:** Healthcare Administrator

#### **Main Success Scenario (MSS):**
1. User requests to archive the address book with a description.
2. MedConnect confirms that the contact data has been successfully archived.

   **Use case ends.**

#### **Extensions:**

**1a.** The given description is invalid.
- **1a1.** MedConnect informs the user of the invalid description.
- **1a2.** User corrects the invalid description and resubmits.

  **Use case resumes from step 2.**

---

### Use Case: Load Archived Contacts

**System:** MedConnect
**Actor:** Healthcare Administrator

#### **Main Success Scenario (MSS):**
1. User requests to load an archive file.
2. MedConnect prompts the user to provide the file name of an archive file in the archives folder.
3. User enters a file name.
4. MedConnect validates the file name and checks for a matching file.
5. MedConnect loads the archived contacts from the file into the address book.

    **Use case ends.**

#### **Extensions:**

**3a.** The given file name is invalid.
- **3a1.** MedConnect notifies the user that the file name contains invalid characters.
- **3a2.** User corrects the file name and resubmits.

  **Use case resumes from step 4.**

**3b.** The given file name does not match any existing file.
- **3b1.** MedConnect notifies the user that a file with the given file name is not found.
- **3b2.** User enters the file name of an existing file in the archives folder and resubmits.

  **Use case resumes from step 4.**

---

### Use Case: Delete Archive File

**System:** MedConnect
**Actor:** Healthcare Administrator

#### **Main Success Scenario (MSS):**
1. User requests to delete an archive file.
2. MedConnect prompts the user to provide the file name of an archive file in the archives folder.
3. User enters a file name.
4. MedConnect validates the file name and checks for a matching file.
5. MedConnect deletes the archive file.

    **Use case ends.**

#### **Extensions:**

**3a.** The given file name is invalid.
- **3a1.** MedConnect notifies the user that the file name contains invalid characters.
- **3a2.** User corrects the file name and resubmits.

  **Use case resumes from step 4.**

**4a.** The given file name does not match any existing file.
- **4a1.** MedConnect notifies the user that a file with the given file name is not found.
- **4a2.** User enters the file name of an existing file in the archives folder and resubmits.

  **Use case resumes from step 4.**

---

### Non-Functional Requirements (NFRs)

#### 1. Performance Requirements:
- **Responsiveness:**
  The system should respond to user commands (e.g., adding, updating, or viewing contacts) within **2 seconds** for typical operations under normal usage conditions (i.e., up to 1000 contacts in the database).

- **Scalability:**
  MedConnect should support operations with **up to 5000 contacts** without performance degradation. Basic operations such as retrieving or adding contacts should not exceed a response time of **3 seconds** under this load.

#### 2. Reliability and Availability:
- **System Uptime:**
  MedConnect must be available for use at least **99% of the time**, especially during hospital operating hours (24/7 access). Regular maintenance should be scheduled during off-peak times.

- **Disaster Recovery and Backup:**
  Contact data must be backed up **daily** to prevent data loss. The system should be able to recover from backup within **2 hours** of a failure.

#### 3. Usability Requirements:
- **Typing Efficiency:**
  The system should be optimized for keyboard-only interactions. A user familiar with the system should be able to complete key operations (e.g., adding a new contact, viewing emergency contacts) in less than **30 seconds** using only the keyboard.

- **Error Handling and Feedback:**
  The system must provide **immediate feedback** (within 1 second) when an error occurs, such as invalid input or missing fields. The user should be able to correct errors without restarting the operation.

#### 4. Security and Privacy Requirements:
- **Data Encryption (Future Consideration):**
  All sensitive patient and contact information must be encrypted when stored locally. The system should implement industry-standard encryption (e.g., AES-256) for both storage and transmission of data.

- **User Authentication (Future Consideration):**
  If the system evolves to handle multiple users, it must implement **role-based access control** with password authentication. Healthcare administrators should have different access levels than general staff.

#### 5. Data and Storage Requirements:
- **Human-Editable File Format:**
  Contact information should be stored in a **human-readable and editable format** (e.g., `.txt` or `.csv`) so that administrators can manually access and modify data if needed.

- **Data Integrity:**
  The system must ensure that no data is lost or corrupted during common operations (e.g., adding, updating, or deleting contacts). **Transaction-like behavior** must be implemented to ensure all data operations either succeed fully or fail without partially corrupting data.

#### 6. Compatibility and Portability:
- **Cross-Platform Support:**
  MedConnect must be compatible with **mainstream operating systems** (Windows, macOS, Linux) and function seamlessly on systems with **Java 17 or higher** installed.

#### 7. Compliance Requirements:
- **Healthcare Data Regulations:**
  MedConnect must adhere to local and international healthcare data protection regulations, such as **HIPAA** in the US, or similar regulations in other regions, ensuring the privacy and security of sensitive medical information.

#### 8. Maintainability and Extensibility:
- **Modular Design:**
  The system must be designed with a modular structure, allowing future extensions such as additional data fields or user roles without requiring significant rework.

- **Testability:**
  MedConnect must be **easily testable**, with automated tests that can cover at least **80% of the codebase**. Each core feature (e.g., adding a contact, deleting outdated contacts) should have dedicated test cases.
---

### Glossary

- **Mainstream OS**: Windows, Linux, Unix, MacOS

- **MedConnect**: A healthcare application designed to help healthcare administrators manage patient and staff contact information efficiently.

- **Healthcare Administrator**: The primary user of MedConnect, responsible for managing patient and staff contact details in a healthcare environment. They ensure that the contact information is up-to-date for communication during critical situations.

- **Emergency Contact**: The designated person or persons to be notified in case of a patient emergency, usually including details such as their name, phone number, and relationship to the patient.

- **Primary Contact Method**: The main communication method for a person in the system, typically used for emergency situations.

- **Command-Line Interface (CLI)**: A method of interacting with MedConnect through typed text commands, allowing fast input for users who prefer typing over graphical interfaces.

- **Modifier**: An additional parameter in commands used to filter or customize operations, such as viewing only specific types of contact details (e.g., emergency, email, phone).

- **Mainstream Operating Systems**: Common operating systems on which MedConnect can run, including Windows, macOS, and Linux.

- **Encryption**: The process of encoding sensitive data, such as patient information, to protect it from unauthorized access.

- **HIPAA**: The Health Insurance Portability and Accountability Act, a US regulation that ensures the security and privacy of patient data. MedConnect must comply with such regulations in healthcare environments.

- **Java 17**: The version of Java required to run MedConnect, which ensures compatibility and performance across different operating systems.

- **Human-Editable File**: A data file format (e.g., `.txt` or `.csv`) that can be easily accessed and modified by healthcare administrators without needing special software.

- **Role-Based Access Control (RBAC)**: A security mechanism that restricts access to data based on the user’s role, allowing different permissions for administrators and general staff (for future implementation).

- **System Uptime**: The percentage of time that MedConnect is available and operational, measured as part of reliability goals.

- **Backup**: The process of creating copies of MedConnect's data to ensure it can be restored in the event of data loss or system failure.

- **Test Coverage**: The percentage of the system's code that is covered by automated tests, ensuring that key features and functionality are reliably tested.

- **AES-256**: A widely-used encryption standard that secures sensitive data using a 256-bit encryption key, ensuring strong protection of patient information.


--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<div markdown="span" class="alert alert-info">:information_source: **Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</div>

### Launch and shutdown

1. Initial launch

   1. Download the latest jar file [here](https://github.com/AY2425S1-CS2103T-T13-1/tp/releases) and copy into an empty folder.

   1. Open a terminal window and `cd` into the same folder.

   1. Enter `java -jar medconnect.jar` into the terminal. Expected: Shows the GUI with a set of sample contacts. The window size may not be optimum but it is resizable.

1. Saving window preferences

   1. Resize the window to an optimum size. Move the window to a different location. Close the window.

   1. Re-launch the app by double-clicking the jar file.<br>
       Expected: The most recent window size and location is retained.

1. Shutdown

    There are multiple ways to exit the application:

   1. Use the `exit` command.

   1. Click `File` in the top left corner, then `Exit` in the dropdown menu.

   1. Click the red 'X' of the application window.

   1. Use the keyboard shortcut `Alt + F4`.

### Deleting a patient

1. Deleting a patient while all patients are being shown<br>

    <div markdown="span" class="alert alert-primary">
        **Prerequisites:**<br>
        1. List all patients using the `list` command. <br>
        2. Multiple persons in the list.
    </div>

    | Test case input                                      | Expected behaviour                                                       | Expected message                                 |
    |------------------------------------------------------|--------------------------------------------------------------------------|--------------------------------------------------|
    | `delete 1`                                           | First contact is deleted from the list.                                  | Deleted Person: [PERSON DETAILS]                 |
    | `delete 1 ec/1`                                      | The first emergency contact of the first contact in the list is deleted. | Added emergency contact: [PERSON DETAILS]        |
    | `delete 0`                                           | Error message is shown.                                                  | Invalid command format! [CORRECT COMMAND FORMAT] |
    | `delete 2 ec/0`                                      | Error message is shown.                                                  | Index is not a non-zero unsigned integer.        |
    | `delete ec/1`                                        | Error message is shown                                                   | Invalid command format! [CORRECT COMMAND FORMAT] |
    | `delete ec/x`<br> (x > number of emergency contacts) | Error message is shown                                                   | The emergency contact index provided is invalid  |
    | `delete x` <br> (x > number of contacts)             | Error message is shown                                                   | The person index provided is invalid             |


2. Deleting a patient while a filtered list is being shown

    <div markdown="span" class="alert alert-primary">
        **Prerequisites:** The patient list is filtered using the `find` or `finddoc` command.
    </div>

    | Test case input                                      | Expected behaviour                                                       | Expected message                                 |
    |------------------------------------------------------|--------------------------------------------------------------------------|--------------------------------------------------|
    | `delete 1`                                           | First contact is deleted from the list.                                  | Deleted Person: [PERSON DETAILS]                 |
    | `delete 1 ec/1`                                      | The first emergency contact of the first contact in the list is deleted. | Added emergency contact: [PERSON DETAILS]        |
    | `delete 0`                                           | Error message is shown.                                                  | Invalid command format! [CORRECT COMMAND FORMAT] |
    | `delete 2 ec/0`                                      | Error message is shown.                                                  | Index is not a non-zero unsigned integer.        |
    | `delete ec/1`                                        | Error message is shown                                                   | Invalid command format! [CORRECT COMMAND FORMAT] |
    | `delete ec/x`<br> (x > number of emergency contacts) | Error message is shown                                                   | The emergency contact index provided is invalid  |
    | `delete x` <br> (x > number of contacts)             | Error message is shown                                                   | The person index provided is invalid             |

### Adding a patient

1. Adding a patient while any number of patients are being shown.
    <div markdown="span" class="alert alert-primary">
           **Note:** Due to the long length of valid `add` commands, the usage of `xx/PARAMETER...` will refer to all remaining compulsory parameters that have not been mentioned for that test case, with valid inputs for the respective parameters.<br>
           **Prerequisites:**  List all patients using the `list` command. Patients are sorted by the time they were added to MedConnect.
   </div>

   | Test case input                                  | Expected behaviour                                                 | Expected message                                                                                               |
   |--------------------------------------------------|--------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------|
   | `add n/Ryan p/98765432 xx/PARAMETER...`          | A new patient John Doe is added to the bottom of the patient list. | New person added: [PERSON DETAILS]                                                                             |
   | `add n/Ryan n/Daniel p/98765432 xx/PARAMETER...` | Error message is shown.                                            | Multiple values specified for the following single-valued field(s): n/                                         |
   | `add n/Ryan`                                     | Error message is shown.                                            | Invalid command format! [CORRECT COMMAND FORMAT]                                                               |
   | `add`                                            | Error message is shown.                                            | Invalid command format! [CORRECT COMMAND FORMAT]                                                               |
   | `add n/`                                         | Error message is shown                                             | Invalid command format! [CORRECT COMMAND FORMAT]                                                               |
   | `add p/???`                                      | Error message is shown                                             | Invalid command format! [CORRECT COMMAND FORMAT]                                                               |
   | `add n/John+Doe xx/PARAMETER`                    | Error message is shown                                             | Names should only contain alphanumeric characters and spaces, and it should not be blank                       |
   | `add p/98@1532 xx/PARAMETER`                     | Error message is shown                                             | Phone numbers should only contain numbers, and it should be at least 3 digits long                             |
   | `add ecrs/knight xx/PARAMETER`                   | Error message is shown                                             | Relationship type should be Parent, Child, Sibling, Spouse, Grandparent or Relative or their gendered variants |

### Editing a patient

1. Editing a patient while any number of patients are being shown.
     <div markdown="span" class="alert alert-primary">
           **Prerequisites:**  List all patients using the `list` command. Patients are sorted by the time they were added to MedConnect.
   </div>

   | Test case input                                                                                  | Expected behaviour                                                                                           | Expected message                                                                         |
   |--------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------|
   | `edit 1 n/Ryan p/98765432 e/ryan@hotmail.com`                                                    | The name, phone and email of the first patient in the list is edited to the new values provided as arguments | Edited person: [PERSON DETAILS]                                                          |
   | `edit 1`                                                                                         | Error message is shown.                                                                                      | At least one field to edit must be provided.                                             |
   | `edit 1 n/`                                                                                      | Error message is shown.                                                                                      | Names should only contain alphanumeric characters and spaces, and it should not be blank |
   | `edit 1 n/John p/`                                                                               | Error message is shown.                                                                                      | Phone numbers should only contain numbers, and it should be at least 3 digits long       |
   | `edit`                                                                                           | Error message is shown                                                                                       | Invalid command format! [CORRECT COMMAND FORMAT]                                         |
   | `edit 2 ecname/John Doe`                                                                         | Error message is shown                                                                                       | At least one emergency contact index to edit must be provided.                           |
   | `edit 2 ec/2`                                                                                    | Error message is shown                                                                                       | At least one emergency contact field to edit must be provided.                           |
   | `edit 1 ec/x ecname/Heather ecphone/5137985 ecrs/Sibling`<br> (x > number of emergency contacts) | Error message is shown                                                                                       | The emergency contact index provided is invalid                                          |
   | `edit x n/Heather` <br> (x > number of contacts)                                                 | Error message is shown                                                                                       | The person index provided is invalid                                                     |

### Adding an emergency contact to a patient
1. Editing a patient while any number of patients are being shown.
     <div markdown="span" class="alert alert-primary">
           **Prerequisites:**  List all patients using the `list` command. Patients are sorted by the time they were added to MedConnect.
   </div>

    | Test case input                                                                    | Expected behaviour                                                                                           | Expected message                                                                                               |
   |------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------|
    | `addec 1 ecname/Sarah Lim ecphone/91234567 ecrs/Granddaughter`                     | The name, phone and email of the first patient in the list is edited to the new values provided as arguments | Added emergency contact: [PERSON DETAILS]                                                                      |
    | `addec 1`                                                                          | Error message is shown.                                                                                      | Invalid command format! [CORRECT COMMAND FORMAT]                                                               |
    | `addec ecname/Sarah Lim ecphone/91234567 ecrs/Granddaughter`                       | Error message is shown.                                                                                      | Invalid command format! [CORRECT COMMAND FORMAT]                                                               |
    | `addec 1 ecname/Sarah Lim ecphone/91234567`                                        | Error message is shown.                                                                                      | Invalid command format! [CORRECT COMMAND FORMAT]                                                               |
    | `addec 1 ecname/Sarah Lim ecphone/91234567 ecrs/Neighbor`                          | Error message is shown                                                                                       | Relationship type should be Parent, Child, Sibling, Spouse, Grandparent or Relative or their gendered variants |
    | `addec 2 ecname/D%#P! ecphone/91234567 ecrs/Son`                                   | Error message is shown                                                                                       | Names should only contain alphanumeric characters and spaces, and it should not be blank                       |
    | `addec x ecname/Heather ecphone/5137985 ecrs/Sibling`<br> (x > number of contacts) | Error message is shown                                                                                       | The person index provided is invalid                                                                           |

 ### Saving data

  1. Dealing with missing/corrupted data files

    1. _{explain how to simulate a missing/corrupted file, and the expected behavior}_

1. _{ more test cases …​ }_

## Appendix: Effort

Developing MedConnect as a brownfield project from the upgrading of AB3 was challenging for us as a team of relatively junior software engineers who did not have much experience in a software engineering project. For some of our team members, the only prior software engineering experience we had was our Orbital project.

Initially, we faced many challenges in managing the Git workflow of creating issues, creating branches, merging branches and pull requests. This was because the process was new to most of the group and we carefully took the time to learn the proper workflow and avoid merge conflicts.

Another challenge we faced was implementing the autocomplete feature. Since MedConnect was directed to be used by fast typists, we brainstormed the idea of having an autocomplete feature to greatly benefit them. However, this idea was quite foreign to all of us and we took a great deal of time in figuring out how to tackle this problem. In due time, we managed to figure out a solution as a team and it is now implemented in the current version of MedConnect.

Finally, for the undo and redo feature, we adapted the proposed implementation provided in the developer guide of AB3. This greatly reduced the effort required for these features as there are many ways to implement them. A more complex solution would be for each command to have its own respective undo and redo implementation. However, we followed the proposed implementation of saving the AddressBook in states and having a pointer that points to the current state. The pointer would move between states upon execution of the undo and redo commands.

Overall, we faced many challenges as a team that we had to overcome over a short runway. We managed to stay afloat and tackle these challenges through constant communication and good teamwork between team members, helping each other out swiftly and decisively.
