package seedu.address.logic.commands;

import seedu.address.model.Model;
import static java.util.Objects.requireNonNull;

public class RedoCommand extends Command {

    public static final String COMMAND_WORD = "redo";
    public static final String MESSAGE_SUCCESS = "Address book has redone previous command!";

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        //TODO: Implement undo command
        //model.redoCommand();
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
