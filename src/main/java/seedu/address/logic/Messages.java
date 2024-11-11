package seedu.address.logic;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.logic.parser.Prefix;
import seedu.address.model.person.EmergencyContact;
import seedu.address.model.person.Person;

/**
 * Container for user visible messages.
 */
public class Messages {

    public static final String MESSAGE_UNKNOWN_COMMAND = "Unknown command";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT = "Invalid command format! \n\n%1$s";
    public static final String MESSAGE_INVALID_PERSON_DISPLAYED_INDEX = "The person index provided is invalid";
    public static final String MESSAGE_INVALID_EMERGENCY_CONTACT_DISPLAYED_INDEX =
            "The emergency contact index provided is invalid";
    public static final String MESSAGE_LAST_EMERGENCY_CONTACT_INDEX =
            "The person must have at least one emergency contact. You cannot delete the last emergency contact.";
    public static final String MESSAGE_PERSONS_LISTED_OVERVIEW = "%1$d persons listed!";
    public static final String MESSAGE_DUPLICATE_FIELDS =
                "Multiple values specified for the following single-valued field(s): ";

    /**
     * Returns an error message indicating the duplicate prefixes.
     */
    public static String getErrorMessageForDuplicatePrefixes(Prefix... duplicatePrefixes) {
        assert duplicatePrefixes.length > 0;

        Set<String> duplicateFields =
                Stream.of(duplicatePrefixes).map(Prefix::toString).collect(Collectors.toSet());

        return MESSAGE_DUPLICATE_FIELDS + String.join(" ", duplicateFields);
    }

    /**
     * Formats the {@code person} for display to the user.
     */
    public static String formatEmergencyContact(EmergencyContact emergencyContact) {
        final StringBuilder builder = new StringBuilder();
        builder.append(emergencyContact.getName())
                .append("; Phone: ")
                .append(emergencyContact.getPhone())
                .append("; Relationship: ")
                .append(emergencyContact.getRelationship());
        return builder.toString();
    }

    /**
     * Formats the {@code person} for display to the user.
     */
    public static String format(Person person) {
        final StringBuilder builder = new StringBuilder();
        builder.append(person.getName())
                .append("\nPhone: ")
                .append(person.getPhone())
                .append("\nEmail: ")
                .append(person.getEmail())
                .append("\nAddress: ")
                .append(person.getAddress())
                .append("\nEmergency Contact(s): ");
        person.getEmergencyContacts().forEach(x -> {
            builder.append(x);
            builder.append("\n");
        });
        builder.append("\nTags: ");
        person.getTags().forEach(builder::append);
        return builder.toString();
    }

}
