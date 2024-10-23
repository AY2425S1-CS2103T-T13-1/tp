package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;

/**
 * Lists all archive files in the archive folder.
 */
public class ListArchiveFilesCommand extends Command {
    private static final Logger logger = LogsCenter.getLogger(ListArchiveFilesCommand.class);

    public static final String COMMAND_WORD = "listArchives";

    public static final String MESSAGE_SUCCESS = "Listed all archive files.";
    public static final String MESSAGE_NO_ARCHIVE = "No archive files found.";
    public static final String MESSAGE_FAILURE = "Failed to find archive files. Please try again later.";

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);

        Path source = model.getAddressBookFilePath();
        assert source != null : "Address book file path is null";

        Path archiveDir = Paths.get(source.getParent().toString(), "archive");
        if (!Files.exists(archiveDir)) {
            return new CommandResult(MESSAGE_NO_ARCHIVE);
        }

        List<String> archiveFiles = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(archiveDir, "*.json")) {
            for (Path entry : stream) {
                archiveFiles.add(entry.getFileName().toString());
            }
        } catch (IOException e) {
            logger.severe("Failed to list archive files: " + e.getMessage());
            return new CommandResult(MESSAGE_FAILURE);
        }

        if (archiveFiles.isEmpty()) {
            return new CommandResult(MESSAGE_NO_ARCHIVE);
        }

        String resultMessage = String.join("\n", archiveFiles);

        logger.info("Listed all archive files.");
        return new CommandResult(MESSAGE_SUCCESS + "\n" + resultMessage);
    }
}
