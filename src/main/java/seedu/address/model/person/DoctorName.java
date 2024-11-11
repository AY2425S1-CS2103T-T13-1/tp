package seedu.address.model.person;

/**
 * Represents a Doctor's name in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidName(String)}
 */
public class DoctorName extends Name {

    public static final String MESSAGE_CONSTRAINTS =
            "Names should only contain alphanumeric characters and spaces, and it should not be blank";

    /*
     * The first character of the address must not be a whitespace,
     * otherwise " " (a blank string) becomes a valid input.
     */
    public static final String VALIDATION_REGEX = "[\\p{Alnum}-.()@/'][\\p{Alnum}-.()@/' ]*";

    public final String doctorName;
    /**
     * Constructs a {@code DoctorName}.
     *
     * @param name A valid name.
     */
    public DoctorName(String name) {
        super(name);
        this.doctorName = "Dr " + name;
    }

    /**
     * Returns true if a given string is a valid name.
     */
    public static boolean isValidName(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    /*
     * Returns the name of the doctor, with the "Dr " prefix.
     */
    public String getDoctorName() {
        return doctorName;
    }

    @Override
    public String toString() {
        return fullName;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DoctorName)) {
            return false;
        }

        DoctorName otherName = (DoctorName) other;
        return fullName.equals(otherName.fullName);
    }

    @Override
    public int hashCode() {
        return fullName.hashCode();
    }

}
