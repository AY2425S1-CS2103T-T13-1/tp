package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.model.Model;

/**
 * Clears the address book.
 */
public class ArchiveCommand extends Command {

    public static final String COMMAND_WORD = "archive";
    public static final String MESSAGE_SUCCESS = "Address book has been archived!";


    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        //TODO: Implement archive command
        //model.archiveAddressBook();
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
