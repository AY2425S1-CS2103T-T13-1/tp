package seedu.address.logic.parser;

import seedu.address.commons.core.filename.Filename;
import seedu.address.logic.commands.DeleteArchiveCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new DeleteArchiveCommand object
 */
public class DeleteArchiveCommandParser implements Parser<DeleteArchiveCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the DeleteArchiveCommand
     * and returns a DeleteArchiveCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    public DeleteArchiveCommand parse(String args) throws ParseException {
        Filename filename = ParserUtil.parseFilename(args);
        return new DeleteArchiveCommand(filename);
    }
}
