package seedu.address.logic.commands;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.commons.core.filename.Filename;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.storage.AddressBookStorage;
import seedu.address.storage.JsonAddressBookStorage;

public class DeleteArchiveCommandTest {
    private Path archiveDir;
    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @BeforeEach
    public void setUp() throws IOException {
        archiveDir = Files.createDirectories(model.getArchiveDirectoryPath());
    }

    @AfterEach
    public void tearDown() throws IOException {
        if (!Files.exists(archiveDir)) {
            return;
        }

        Files.walk(archiveDir)
                .map(Path::toFile)
                .forEach(file -> {
                    if (!file.delete()) {
                        file.deleteOnExit();
                    }
                });
    }

    @Test
    public void execute_archiveFileNotFound_success() throws IOException {
        Files.deleteIfExists(archiveDir);
        Filename filename = new Filename("no_archive.json");
        String expectedMessage = String.format(DeleteArchiveCommand.MESSAGE_NOT_FOUND, filename);
        assertCommandSuccess(new DeleteArchiveCommand(filename), model, expectedMessage, model);
    }

    @Test
    public void execute_emptyArchiveFile_success() throws IOException {
        // Create empty archive file
        Filename filename = new Filename("empty_archive.json");
        Path archiveFilepath = Paths.get(archiveDir.toString(), filename.toString());
        Files.createFile(archiveFilepath);

        String expectedMessage = String.format(DeleteArchiveCommand.MESSAGE_SUCCESS, filename);
        assertCommandSuccess(new DeleteArchiveCommand(filename), model, expectedMessage, model);
    }

    @Test
    public void execute_withArchiveFile_success() throws IOException {
        // Save the current address book to a file
        Path source = model.getAddressBookFilePath();
        AddressBookStorage addressBookStorage = new JsonAddressBookStorage(source);
        addressBookStorage.saveAddressBook(model.getAddressBook());

        // Copy the address book file to the archive directory
        Filename filename = new Filename("archive.json");
        Path archiveFilepath = Paths.get(archiveDir.toString(), filename.toString());
        Files.copy(source, archiveFilepath, REPLACE_EXISTING);

        String expectedMessage = String.format(DeleteArchiveCommand.MESSAGE_SUCCESS, filename);
        assertCommandSuccess(new DeleteArchiveCommand(filename), model, expectedMessage, model);
    }

    @Test
    public void equals() {
        DeleteArchiveCommand deleteArchiveFirstCommand = new DeleteArchiveCommand(new Filename("test1"));
        DeleteArchiveCommand deleteArchiveSecondCommand = new DeleteArchiveCommand(new Filename("test2"));

        // same object -> returns true
        assertTrue(deleteArchiveFirstCommand.equals(deleteArchiveFirstCommand));

        // same values -> returns true
        DeleteArchiveCommand deleteArchiveFirstCommandCopy = new DeleteArchiveCommand(new Filename("test1"));
        assertTrue(deleteArchiveFirstCommand.equals(deleteArchiveFirstCommandCopy));

        // different types -> returns false
        assertFalse(deleteArchiveFirstCommand.equals("test1"));

        // null -> returns false
        assertFalse(deleteArchiveFirstCommand.equals(null));

        // different command -> returns false
        assertFalse(deleteArchiveFirstCommand.equals(deleteArchiveSecondCommand));
    }
}
