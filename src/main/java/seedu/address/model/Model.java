package seedu.address.model;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Predicate;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.filename.Filename;
import seedu.address.model.person.Person;

/**
 * The API of the Model component.
 */
public interface Model {
    /**
     * {@code Predicate} that always evaluate to true
     */
    Predicate<Person> PREDICATE_SHOW_ALL_PERSONS = unused -> true;

    /**
     * Replaces user prefs data with the data in {@code userPrefs}.
     */
    void setUserPrefs(ReadOnlyUserPrefs userPrefs);

    /**
     * Returns the user prefs.
     */
    ReadOnlyUserPrefs getUserPrefs();

    /**
     * Returns the user prefs' GUI settings.
     */
    GuiSettings getGuiSettings();

    /**
     * Sets the user prefs' GUI settings.
     */
    void setGuiSettings(GuiSettings guiSettings);

    /**
     * Returns the user prefs' address book file path.
     */
    Path getAddressBookFilePath();

    /**
     * Sets the user prefs' address book file path.
     */
    void setAddressBookFilePath(Path addressBookFilePath);

    /**
     * Replaces address book data with the data in {@code addressBook}.
     */
    void setAddressBook(ReadOnlyAddressBook addressBook);

    /**
     * Returns the AddressBook
     */
    ReadOnlyAddressBook getAddressBook();

    /**
     * Returns the archive directory path.
     */
    Path getArchiveDirectoryPath();

    /**
     * Archives the address book.
     *
     * @param filename the name of the file to archive the address book to.
     * @throws IOException if there was an error writing to the file.
     */
    void archiveAddressBook(Filename filename) throws IOException;

    /**
     * Undoes the previous command that modified the state or storage of the address book.
     */
    void undoAddressBook();

    /**
     * Returns true if there is a previous state in the address book that can be undone.
     *
     * @return true if undo can be performed, false otherwise.
     */
    boolean canUndoAddressBook();

    /**
     * Saves the current state of the address book to history.
     */
    void saveAddressBook();

    /**
     * Restores the next state of the address book (redo).
     * This method reverts the address book to a state that was undone
     * and is available in the redo history, if such a state exists.
     * If there is no state available to redo, no changes will be made.
     */
    void redoAddressBook();

    /**
     * Returns true if there is a future state available to redo.
     * This method checks whether the redo history contains a state
     * that can be restored, meaning if the user has undone a state before
     * and can now move forward to that state again.
     */
    boolean canRedoAddressBook();


    /**
     * Returns true if a person with the same identity as {@code person} exists in the address book.
     */
    boolean hasPerson(Person person);

    /**
     * Deletes the given person.
     * The person must exist in the address book.
     */
    void deletePerson(Person target);

    /**
     * Adds the given person.
     * {@code person} must not already exist in the address book.
     */
    void addPerson(Person person);

    /**
     * Replaces the given person {@code target} with {@code editedPerson}.
     * {@code target} must exist in the address book.
     * The person identity of {@code editedPerson} must not be the same as another existing person in the address book.
     */
    void setPerson(Person target, Person editedPerson);

    /**
     * Returns an unmodifiable view of the filtered person list
     */
    ObservableList<Person> getFilteredPersonList();

    /**
     * Updates the filter of the filtered person list to filter by the given {@code predicate}.
     *
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredPersonList(Predicate<Person> predicate);
}
