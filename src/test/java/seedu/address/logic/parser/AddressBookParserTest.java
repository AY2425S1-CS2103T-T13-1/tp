package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.logic.commands.CommandTestUtil.ECNAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.ECPHONE_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.ECRS_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ECNAME_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ECPHONE_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ECRS_AMY;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.AddEmergencyContactCommand;
import seedu.address.logic.commands.ArchiveCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.DeleteArchiveCommand;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.DeleteCommand.DeleteCommandDescriptor;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.FindDoctorCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListArchiveFilesCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.LoadArchiveCommand;
import seedu.address.logic.commands.RedoCommand;
import seedu.address.logic.commands.UndoCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.DoctorNameContainsKeywordsPredicate;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.testutil.EditPersonDescriptorBuilder;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.PersonUtil;

public class AddressBookParserTest {

    private final AddressBookParser parser = new AddressBookParser();

    @Test
    public void parseCommand_add() throws Exception {
        Person person = new PersonBuilder().build();
        AddCommand command = (AddCommand) parser.parseCommand(PersonUtil.getAddCommand(person));
        assertEquals(new AddCommand(person), command);
    }

    @Test
    public void parseCommand_clear() throws Exception {
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD) instanceof ClearCommand);
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD + " 3") instanceof ClearCommand);
    }

    @Test
    public void parseCommand_delete() throws Exception {
        DeleteCommand command = (DeleteCommand) parser.parseCommand(
                DeleteCommand.COMMAND_WORD + " " + INDEX_FIRST_PERSON.getOneBased());
        assertEquals(new DeleteCommand(INDEX_FIRST_PERSON, new DeleteCommandDescriptor()), command);
    }

    @Test
    public void parseCommand_edit() throws Exception {
        Person person = new PersonBuilder().build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(person).build();
        EditCommand command = (EditCommand) parser.parseCommand(EditCommand.COMMAND_WORD + " "
                + INDEX_FIRST_PERSON.getOneBased() + " " + PersonUtil.getEditPersonDescriptorDetails(descriptor));
        assertEquals(new EditCommand(INDEX_FIRST_PERSON, descriptor), command);
    }

    @Test
    public void parseCommand_addEmergencyContact() throws Exception {
        Person person = new PersonBuilder()
                .withEmergencyContact(VALID_ECNAME_AMY, VALID_ECPHONE_AMY, VALID_ECRS_AMY).build();
        String descriptor = ECNAME_DESC_AMY + ECPHONE_DESC_AMY + ECRS_DESC_AMY;
        AddEmergencyContactCommand command = (AddEmergencyContactCommand) parser.parseCommand(
                AddEmergencyContactCommand.COMMAND_WORD + " "
                + INDEX_FIRST_PERSON.getOneBased() + descriptor);
        assertEquals(new AddEmergencyContactCommand(INDEX_FIRST_PERSON, person.getFirstEmergencyContact()), command);
    }

    @Test
    public void parseCommand_exit() throws Exception {
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD) instanceof ExitCommand);
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD + " 3") instanceof ExitCommand);
    }

    @Test
    public void parseCommand_find() throws Exception {
        List<String> keywords = Arrays.asList("foo", "bar", "baz");
        FindCommand command = (FindCommand) parser.parseCommand(
                FindCommand.COMMAND_WORD + " " + keywords.stream().collect(Collectors.joining(" ")));
        assertEquals(new FindCommand(new NameContainsKeywordsPredicate(keywords)), command);
    }

    @Test
    public void parseCommand_help() throws Exception {
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD) instanceof HelpCommand);
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD + " 3") instanceof HelpCommand);
    }

    @Test
    public void parseCommand_list() throws Exception {
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD) instanceof ListCommand);
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD + " 3") instanceof ListCommand);
    }

    @Test
    public void parseCommand_archive() throws Exception {
        assertTrue(parser.parseCommand(ArchiveCommand.COMMAND_WORD) instanceof ArchiveCommand);
    }

    @Test
    public void parseCommand_listArchiveFiles() throws Exception {
        assertTrue(parser.parseCommand(ListArchiveFilesCommand.COMMAND_WORD) instanceof ListArchiveFilesCommand);
        assertTrue(parser.parseCommand(ListArchiveFilesCommand.COMMAND_WORD + " 3") instanceof ListArchiveFilesCommand);
    }

    @Test
    public void parseCommand_loadArchive() throws Exception {
        assertTrue(parser.parseCommand(LoadArchiveCommand.COMMAND_WORD) instanceof LoadArchiveCommand);
    }

    @Test
    public void parseCommand_deleteArchive() throws Exception {
        assertTrue(parser.parseCommand(DeleteArchiveCommand.COMMAND_WORD) instanceof DeleteArchiveCommand);
    }

    @Test
    public void parseCommand_undo() throws Exception {
        assertTrue(parser.parseCommand(UndoCommand.COMMAND_WORD) instanceof UndoCommand);
    }

    @Test
    public void parseCommand_redo() throws Exception {
        assertTrue(parser.parseCommand(RedoCommand.COMMAND_WORD) instanceof RedoCommand);
    }

    @Test
    public void parseCommand_find_doctor() throws Exception {
        List<String> keywords = Arrays.asList("foo", "bar", "baz");
        FindDoctorCommand command = (FindDoctorCommand) parser.parseCommand(
                FindDoctorCommand.COMMAND_WORD + " " + keywords.stream().collect(Collectors.joining(" ")));
        assertEquals(new FindDoctorCommand(new DoctorNameContainsKeywordsPredicate(keywords)), command);
    }

    @Test
    public void parseCommand_unrecognisedInput_throwsParseException() {
        assertThrows(ParseException.class, String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE), ()
                -> parser.parseCommand(""));
    }

    @Test
    public void parseCommand_unknownCommand_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("unknownCommand"));
    }
}
