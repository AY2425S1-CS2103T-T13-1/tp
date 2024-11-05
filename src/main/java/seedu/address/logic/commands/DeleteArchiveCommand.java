package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.filename.Filename;
import seedu.address.model.Model;

/**
 * Deletes an archive file.
 */
public class DeleteArchiveCommand extends Command {
    public static final String COMMAND_WORD = "deleteArchive";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Deletes an archive file.\n"
            + "Parameters: FILENAME\n"
            + "Example: " + COMMAND_WORD + "addressbook-20241023_114324-example.json";

    public static final String MESSAGE_SUCCESS = "Deleted archive file: %1$s";
    public static final String MESSAGE_NOT_FOUND = "Archive file not found: %1$s";
    public static final String MESSAGE_FAILURE = "Failed to delete archive file: %1$s";

    private static final Logger logger = LogsCenter.getLogger(DeleteArchiveCommand.class);

    private final Filename archiveFilename;

    public DeleteArchiveCommand(Filename archiveFilename) {
        this.archiveFilename = archiveFilename;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);

        Path archiveFile = Paths.get(model.getArchiveDirectoryPath().toString(), archiveFilename.toString());
        if (!Files.exists(archiveFile)) {
            logger.info("Archive file not found: " + archiveFilename);
            return new CommandResult(String.format(MESSAGE_NOT_FOUND, archiveFilename));
        }

        try {
            Files.deleteIfExists(archiveFile);
        } catch (IOException e) {
            logger.severe("Failed to delete archive file: " + e.getMessage());
            return new CommandResult(String.format(MESSAGE_FAILURE, archiveFilename));
        }

        logger.info("Deleted archive file: " + archiveFilename);
        return new CommandResult(String.format(MESSAGE_SUCCESS, archiveFilename));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DeleteArchiveCommand)) {
            return false;
        }

        DeleteArchiveCommand otherDeleteCommand = (DeleteArchiveCommand) other;
        return archiveFilename.equals(otherDeleteCommand.archiveFilename);
    }
}
