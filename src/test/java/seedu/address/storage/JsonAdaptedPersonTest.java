package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.storage.JsonAdaptedPerson.MISSING_FIELD_MESSAGE_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.EmergencyContact;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
public class JsonAdaptedPersonTest {
    private static final String INVALID_NAME = "R#chel";
    private static final String INVALID_PHONE = "+651234";
    private static final String INVALID_ADDRESS = " ";
    private static final String INVALID_EMAIL = "example.com";
    private static final String INVALID_ECNAME = "K0m4!";
    private static final String INVALID_ECPHONE = "Q@+4567";
    private static final String INVALID_ECRS = "M0th@r";
    private static final String INVALID_TAG = "#friend";
    private static final String INVALID_DOCTOR_NAME = "J+hn Doe";
    private static final String INVALID_DOCTOR_PHONE = "+123456";
    private static final String INVALID_DOCTOR_EMAIL = "johndoe.com";

    private static final String VALID_NAME = BENSON.getName().toString();
    private static final String VALID_PHONE = BENSON.getPhone().toString();
    private static final String VALID_EMAIL = BENSON.getEmail().toString();
    private static final String VALID_ADDRESS = BENSON.getAddress().toString();
    private static final String VALID_ECNAME = BENSON.getFirstEmergencyContact().getName().toString();
    private static final String VALID_ECPHONE = BENSON.getFirstEmergencyContact().getPhone().toString();
    private static final String VALID_ECRS = BENSON.getFirstEmergencyContact().getRelationship().toString();
    private static final String VALID_DOCTOR_NAME = BENSON.getDoctor().getName().toString();
    private static final String VALID_DOCTOR_PHONE = BENSON.getDoctor().getPhone().toString();
    private static final String VALID_DOCTOR_EMAIL = BENSON.getDoctor().getEmail().toString();
    private static final List<JsonAdaptedTag> VALID_TAGS = BENSON.getTags().stream()
            .map(JsonAdaptedTag::new)
            .collect(Collectors.toList());
    private static final List<JsonAdaptedEmergencyContact> VALID_EMERGENCY_CONTACTS =
            BENSON.getEmergencyContacts().stream()
            .map(JsonAdaptedEmergencyContact::new)
            .collect(Collectors.toList());

    @Test
    public void toModelType_validPersonDetails_returnsPerson() throws Exception {
        JsonAdaptedPerson person = new JsonAdaptedPerson(BENSON);
        assertEquals(BENSON, person.toModelType());
    }

    @Test
    public void toModelType_invalidName_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(INVALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                        VALID_EMERGENCY_CONTACTS,
                        new JsonAdaptedDoctor(VALID_DOCTOR_NAME, VALID_DOCTOR_PHONE, VALID_DOCTOR_EMAIL),
                        VALID_TAGS);
        String expectedMessage = Name.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullName_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(null, VALID_PHONE, VALID_EMAIL,
                VALID_ADDRESS, VALID_EMERGENCY_CONTACTS,
                new JsonAdaptedDoctor(VALID_DOCTOR_NAME, VALID_DOCTOR_PHONE, VALID_DOCTOR_EMAIL),
                VALID_TAGS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidPhone_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, INVALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                        VALID_EMERGENCY_CONTACTS,
                        new JsonAdaptedDoctor(VALID_DOCTOR_NAME, VALID_DOCTOR_PHONE, VALID_DOCTOR_EMAIL),
                        VALID_TAGS);
        String expectedMessage = Phone.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullPhone_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, null,
                VALID_EMAIL, VALID_ADDRESS,
                VALID_EMERGENCY_CONTACTS,
                new JsonAdaptedDoctor(VALID_DOCTOR_NAME, VALID_DOCTOR_PHONE, VALID_DOCTOR_EMAIL),
                VALID_TAGS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidEmail_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, INVALID_EMAIL, VALID_ADDRESS,
                        VALID_EMERGENCY_CONTACTS,
                        new JsonAdaptedDoctor(VALID_DOCTOR_NAME, VALID_DOCTOR_PHONE, VALID_DOCTOR_EMAIL),
                        VALID_TAGS);
        String expectedMessage = Email.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullEmail_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, null,
                VALID_ADDRESS, VALID_EMERGENCY_CONTACTS,
                new JsonAdaptedDoctor(VALID_DOCTOR_NAME, VALID_DOCTOR_PHONE, VALID_DOCTOR_EMAIL),
                VALID_TAGS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Email.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidAddress_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, INVALID_ADDRESS,
                        VALID_EMERGENCY_CONTACTS,
                        new JsonAdaptedDoctor(VALID_DOCTOR_NAME, VALID_DOCTOR_PHONE, VALID_DOCTOR_EMAIL),
                        VALID_TAGS);
        String expectedMessage = Address.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullAddress_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, null,
                VALID_EMERGENCY_CONTACTS,
                new JsonAdaptedDoctor(VALID_DOCTOR_NAME, VALID_DOCTOR_PHONE, VALID_DOCTOR_EMAIL),
                VALID_TAGS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Address.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidTags_throwsIllegalValueException() {
        List<JsonAdaptedTag> invalidTags = new ArrayList<>(VALID_TAGS);
        invalidTags.add(new JsonAdaptedTag(INVALID_TAG));
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                        VALID_EMERGENCY_CONTACTS,
                        new JsonAdaptedDoctor(VALID_DOCTOR_NAME, VALID_DOCTOR_PHONE, VALID_DOCTOR_EMAIL),
                        invalidTags);
        assertThrows(IllegalValueException.class, person::toModelType);
    }

    @Test
    public void toModelType_nullEmergencyContacts_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                null,
                new JsonAdaptedDoctor(VALID_DOCTOR_NAME, VALID_DOCTOR_PHONE, VALID_DOCTOR_EMAIL),
                VALID_TAGS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, EmergencyContact.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidEmergencyContactName_throwsIllegalValueException() {
        List<JsonAdaptedEmergencyContact> invalidEmergencyContacts = new ArrayList<>();
        invalidEmergencyContacts.add(new JsonAdaptedEmergencyContact(INVALID_ECNAME, VALID_ECPHONE, VALID_ECRS));
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                        invalidEmergencyContacts,
                        new JsonAdaptedDoctor(VALID_DOCTOR_NAME, VALID_DOCTOR_PHONE, VALID_DOCTOR_EMAIL),
                        VALID_TAGS);
        assertThrows(IllegalValueException.class, person::toModelType);
    }

    @Test
    public void toModelType_invalidEmergencyContactPhone_throwsIllegalValueException() {
        List<JsonAdaptedEmergencyContact> invalidEmergencyContacts = new ArrayList<>();
        invalidEmergencyContacts.add(new JsonAdaptedEmergencyContact(VALID_ECNAME, INVALID_ECPHONE, VALID_ECRS));
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                        invalidEmergencyContacts,
                        new JsonAdaptedDoctor(VALID_DOCTOR_NAME, VALID_DOCTOR_PHONE, VALID_DOCTOR_EMAIL),
                        VALID_TAGS);
        assertThrows(IllegalValueException.class, person::toModelType);
    }

    @Test
    public void toModelType_invalidEmergencyContactRelationship_throwsIllegalValueException() {
        List<JsonAdaptedEmergencyContact> invalidEmergencyContacts = new ArrayList<>();
        invalidEmergencyContacts.add(new JsonAdaptedEmergencyContact(VALID_ECNAME, VALID_ECPHONE, INVALID_ECRS));
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                        invalidEmergencyContacts,
                        new JsonAdaptedDoctor(VALID_DOCTOR_NAME, VALID_DOCTOR_PHONE, VALID_DOCTOR_EMAIL),
                        VALID_TAGS);
        assertThrows(IllegalValueException.class, person::toModelType);
    }

    @Test
    public void toModelType_nullDoctor_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                VALID_EMERGENCY_CONTACTS,
                null, VALID_TAGS);
        assertThrows(IllegalValueException.class, person::toModelType);
    }

    @Test
    public void toModelType_invalidDoctorName_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                        VALID_EMERGENCY_CONTACTS,
                        new JsonAdaptedDoctor(INVALID_DOCTOR_NAME, VALID_DOCTOR_PHONE, VALID_DOCTOR_EMAIL),
                        VALID_TAGS);
        assertThrows(IllegalValueException.class, person::toModelType);
    }

    @Test
    public void toModelType_invalidDoctorPhone_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                        VALID_EMERGENCY_CONTACTS,
                        new JsonAdaptedDoctor(VALID_DOCTOR_NAME, INVALID_DOCTOR_PHONE, VALID_DOCTOR_EMAIL),
                        VALID_TAGS);
        assertThrows(IllegalValueException.class, person::toModelType);
    }

    @Test
    public void toModelType_invalidDoctorEmail_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                        VALID_EMERGENCY_CONTACTS,
                        new JsonAdaptedDoctor(VALID_DOCTOR_NAME, VALID_DOCTOR_PHONE, INVALID_DOCTOR_EMAIL),
                        VALID_TAGS);
        assertThrows(IllegalValueException.class, person::toModelType);
    }
}
