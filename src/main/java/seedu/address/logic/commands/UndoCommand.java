package seedu.address.logic.commands;

import seedu.address.model.Model;
import static java.util.Objects.requireNonNull;

public class UndoCommand extends Command {

    public static final String COMMAND_WORD = "undo";
    public static final String MESSAGE_SUCCESS = "Address book has undone previous command!";

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        //TODO: Implement undo command
        //model.undoCommand();
        return new CommandResult(MESSAGE_SUCCESS);
    }
}

