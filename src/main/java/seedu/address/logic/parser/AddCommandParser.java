package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DOC_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DOC_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DOC_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMERGENCY_CONTACT_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMERGENCY_CONTACT_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMERGENCY_CONTACT_RELATIONSHIP;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Stream;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Doctor;
import seedu.address.model.person.DoctorName;
import seedu.address.model.person.Email;
import seedu.address.model.person.EmergencyContact;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Relationship;
import seedu.address.model.tag.Tag;

/**
 * Parses input arguments and creates a new AddCommand object
 */
public class AddCommandParser implements Parser<AddCommand> {

    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values
     * in the given
     * {@code ArgumentMultimap}.
     */
    public static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }

    /**
     * Parses the given {@code String} of arguments in the context of the AddCommand
     * and returns an AddCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    public AddCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL,
                PREFIX_ADDRESS, PREFIX_EMERGENCY_CONTACT_NAME,
                PREFIX_EMERGENCY_CONTACT_PHONE, PREFIX_EMERGENCY_CONTACT_RELATIONSHIP, PREFIX_DOC_NAME,
                PREFIX_DOC_PHONE, PREFIX_DOC_EMAIL, PREFIX_TAG);

        // Compulsory fields
        if (!arePrefixesPresent(argMultimap, PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS,
                PREFIX_EMERGENCY_CONTACT_NAME, PREFIX_EMERGENCY_CONTACT_PHONE, PREFIX_EMERGENCY_CONTACT_RELATIONSHIP,
                PREFIX_DOC_NAME, PREFIX_DOC_PHONE, PREFIX_DOC_EMAIL)
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS,
                PREFIX_EMERGENCY_CONTACT_NAME, PREFIX_EMERGENCY_CONTACT_PHONE, PREFIX_EMERGENCY_CONTACT_RELATIONSHIP,
                PREFIX_DOC_NAME, PREFIX_DOC_PHONE, PREFIX_DOC_EMAIL);
        Name name = ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get());
        Phone phone = ParserUtil.parsePhone(argMultimap.getValue(PREFIX_PHONE).get());
        Email email = ParserUtil.parseEmail(argMultimap.getValue(PREFIX_EMAIL).get());
        Address address = ParserUtil.parseAddress(argMultimap.getValue(PREFIX_ADDRESS).get());

        Name ecName = ParserUtil.parseName(argMultimap.getValue(PREFIX_EMERGENCY_CONTACT_NAME).get());
        Phone ecPhone = ParserUtil.parsePhone(argMultimap.getValue(PREFIX_EMERGENCY_CONTACT_PHONE).get());
        Relationship ecRelationship = ParserUtil.parseRelationship(
                argMultimap.getValue(PREFIX_EMERGENCY_CONTACT_RELATIONSHIP).get());
        EmergencyContact emergencyContact = new EmergencyContact(ecName, ecPhone, ecRelationship);
        Set<EmergencyContact> emergencyContacts = new LinkedHashSet<>();
        emergencyContacts.add(emergencyContact);

        DoctorName doctorName = ParserUtil.parseDoctorName(argMultimap.getValue(PREFIX_DOC_NAME).get());
        Phone doctorPhone = ParserUtil.parsePhone(argMultimap.getValue(PREFIX_DOC_PHONE).get());
        Email doctorEmail = ParserUtil.parseEmail(argMultimap.getValue(PREFIX_DOC_EMAIL).get());
        Doctor doctor = new Doctor(doctorName, doctorPhone, doctorEmail);

        Set<Tag> tagList = ParserUtil.parseTags(argMultimap.getAllValues(PREFIX_TAG));

        Person person = new Person(name, phone, email, address, emergencyContacts, doctor, tagList);

        return new AddCommand(person);
    }

}
