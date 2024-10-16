package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.model.Model;


/**
 * Represents an Redo command that reverses the reversed last modification made to the address book.
 * This command can be used to restore the previous state of the address book after an edit,
 * addition, or deletion.
 */
public class RedoCommand extends Command {

    public static final String COMMAND_WORD = "redo";
    public static final String MESSAGE_SUCCESS = "Address book has redone previous command!";
    public static final String MESSAGE_FAILURE = "Address book failed to be redone. Please try again later.";

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        if (model.canRedoAddressBook()) {
            model.redoAddressBook();
            return new CommandResult(MESSAGE_SUCCESS);
        } else {
            return new CommandResult(MESSAGE_FAILURE);
        }
    }
}
