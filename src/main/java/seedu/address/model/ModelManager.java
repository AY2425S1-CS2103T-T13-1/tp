package seedu.address.model;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.filename.Filename;
import seedu.address.model.person.Person;

/**
 * Represents the in-memory model of the address book data.
 */
public class ModelManager implements Model {
    public static final String ARCHIVE_DIRNAME = "archive";
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    // Stack to store the history of address book states for undo functionality
    private final VersionedAddressBook versionedAddressBook;
    private final UserPrefs userPrefs;
    private final FilteredList<Person> filteredPersons;

    /**
     * Initializes a ModelManager with the given addressBook and userPrefs.
     */
    public ModelManager(ReadOnlyAddressBook addressBook, ReadOnlyUserPrefs userPrefs) {
        requireAllNonNull(addressBook, userPrefs);

        logger.fine("Initializing with address book: " + addressBook + " and user prefs " + userPrefs);

        this.versionedAddressBook = new VersionedAddressBook(addressBook);
        this.userPrefs = new UserPrefs(userPrefs);
        filteredPersons = new FilteredList<>(this.versionedAddressBook.getPersonList());

    }

    public ModelManager() {
        this(new AddressBook(), new UserPrefs());
    }

    //=========== UserPrefs ==================================================================================

    @Override
    public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
        requireNonNull(userPrefs);
        this.userPrefs.resetData(userPrefs);
    }

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefs.setGuiSettings(guiSettings);
    }

    @Override
    public Path getAddressBookFilePath() {
        return userPrefs.getAddressBookFilePath();
    }

    @Override
    public void setAddressBookFilePath(Path addressBookFilePath) {
        requireNonNull(addressBookFilePath);
        userPrefs.setAddressBookFilePath(addressBookFilePath);
    }

    //=========== AddressBook ================================================================================

    @Override
    public void setAddressBook(ReadOnlyAddressBook addressBook) {
        this.versionedAddressBook.resetData(addressBook); // Reset the versioned address book's data
        saveAddressBook();
    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        return versionedAddressBook;
    }

    @Override
    public Path getArchiveDirectoryPath() {
        Path source = this.getAddressBookFilePath();
        assert source != null : "Address book file path is null";

        Path archiveDir = Paths.get(source.getParent().toString(), ARCHIVE_DIRNAME);

        if (!Files.exists(archiveDir)) {
            logger.info("No archive directory found.");
        }

        return archiveDir;
    }

    @Override
    public void archiveAddressBook(Filename filename) throws IOException {
        Path source = this.getAddressBookFilePath();
        assert source != null : "Address book file path is null";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String timestamp = LocalDateTime.now().format(formatter);
        String archiveFilename = source.getFileName().toString().replace(".json", "") + "-"
                + timestamp + (filename.toString().isEmpty() ? "" : "-" + filename) + ".json";
        Path destination = Paths.get(this.getArchiveDirectoryPath().toString(), archiveFilename);

        Files.createDirectories(destination.getParent());
        Files.copy(source, destination, REPLACE_EXISTING);
        logger.info("Address book has been archived!");
    }

    @Override
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return versionedAddressBook.hasPerson(person);
    }

    @Override
    public void deletePerson(Person target) {
        versionedAddressBook.removePerson(target);
        // No need updateFilteredPList as FilteredList<> auto-updates from addressBook4
        saveAddressBook();
    }

    @Override
    public void addPerson(Person person) {
        versionedAddressBook.addPerson(person);
        updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        // FilteredList<> auto-updates from addressBook, BUT may not show, if alr looking at filtered list
        saveAddressBook();
    }

    @Override
    public void setPerson(Person target, Person editedPerson) {
        requireAllNonNull(target, editedPerson);
        versionedAddressBook.setPerson(target, editedPerson);
        saveAddressBook();
    }

    // ============ Undo and Redo Methods ================================================================

    /**
     * Commits the current state of the address book to history.
     */
    @Override
    public void saveAddressBook() {
        versionedAddressBook.save();
    }

    /**
     * Restores the previous state of the address book (undo).
     */
    @Override
    public void undoAddressBook() {
        if (canUndoAddressBook()) {
            versionedAddressBook.undo();
            updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        }
    }

    /**
     * Returns true if there is a previous state to undo.
     */
    @Override
    public boolean canUndoAddressBook() {
        return versionedAddressBook.canUndo();
    }

    @Override
    public void redoAddressBook() {
        if (canRedoAddressBook()) {
            versionedAddressBook.redo();
            updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        }
    }

    @Override
    public boolean canRedoAddressBook() {
        return versionedAddressBook.canRedo();
    }

    //=========== Filtered Person List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Person} backed by the internal list of
     * {@code versionedAddressBook}
     */
    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return filteredPersons;
    }

    @Override
    public void updateFilteredPersonList(Predicate<Person> predicate) {
        requireNonNull(predicate);
        filteredPersons.setPredicate(predicate);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ModelManager)) {
            return false;
        }

        ModelManager otherModelManager = (ModelManager) other;
        return versionedAddressBook.equals(otherModelManager.versionedAddressBook)
                && userPrefs.equals(otherModelManager.userPrefs)
                && filteredPersons.equals(otherModelManager.filteredPersons);
    }

}
