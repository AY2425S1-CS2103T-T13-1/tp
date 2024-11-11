package seedu.address.testutil;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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
import seedu.address.model.util.SampleDataUtil;

/**
 * A utility class to help with building Person objects.
 */
public class PersonBuilder {

    public static final String DEFAULT_NAME = "Amy Bee";
    public static final String DEFAULT_PHONE = "85355255";
    public static final String DEFAULT_EMAIL = "amy@gmail.com";
    public static final String DEFAULT_ADDRESS = "123, Jurong West Ave 6, #08-111";
    public static final String DEFAULT_ECNAME = "Sarah Lim";
    public static final String DEFAULT_ECPHONE = "85541325";
    public static final String DEFAULT_ECRS = "Parent";
    public static final String DEFAULT_DOC_NAME = "John Doe";
    public static final String DEFAULT_DOC_PHONE = "98927134";
    public static final String DEFAULT_DOC_EMAIL = "johndoe@gmail.com";

    private Name name;
    private Phone phone;
    private Email email;
    private Address address;
    private Set<EmergencyContact> emergencyContacts;
    private Doctor doctor;
    private Set<Tag> tags;

    /**
     * Creates a {@code PersonBuilder} with the default details. The person has only one emergency contact
     */
    public PersonBuilder() {
        name = new Name(DEFAULT_NAME);
        phone = new Phone(DEFAULT_PHONE);
        email = new Email(DEFAULT_EMAIL);
        address = new Address(DEFAULT_ADDRESS);
        EmergencyContact emergencyContact = new EmergencyContact(new Name(DEFAULT_ECNAME),
                new Phone(DEFAULT_ECPHONE), new Relationship(DEFAULT_ECRS));
        emergencyContacts = new LinkedHashSet<>(Arrays.asList(emergencyContact));
        doctor = new Doctor(new DoctorName(DEFAULT_DOC_NAME), new Phone(DEFAULT_DOC_PHONE),
                    new Email(DEFAULT_DOC_EMAIL));
        tags = new HashSet<>();
    }

    /**
     * Initializes the PersonBuilder with the data of {@code personToCopy}.
     */
    public PersonBuilder(Person personToCopy) {
        name = personToCopy.getName();
        phone = personToCopy.getPhone();
        email = personToCopy.getEmail();
        address = personToCopy.getAddress();
        emergencyContacts = personToCopy.getEmergencyContacts();
        doctor = personToCopy.getDoctor();
        tags = new HashSet<>(personToCopy.getTags());
    }

    /**
     * Sets the {@code Name} of the {@code Person} that we are building.
     */
    public PersonBuilder withName(String name) {
        this.name = new Name(name);
        return this;
    }

    /**
     * Parses the {@code tags} into a {@code Set<Tag>} and set it to the
     * {@code Person} that we are building.
     */
    public PersonBuilder withTags(String... tags) {
        this.tags = SampleDataUtil.getTagSet(tags);
        return this;
    }

    /**
     * Sets the {@code Address} of the {@code Person} that we are building.
     */
    public PersonBuilder withAddress(String address) {
        this.address = new Address(address);
        return this;
    }

    /**
     * Sets the {@code Phone} of the {@code Person} that we are building.
     */
    public PersonBuilder withPhone(String phone) {
        this.phone = new Phone(phone);
        return this;
    }

    /**
     * Sets the {@code Email} of the {@code Person} that we are building.
     */
    public PersonBuilder withEmail(String email) {
        this.email = new Email(email);
        return this;
    }

    /**
     * Sets the {@code EmergencyContact} of the {@code Person} that we are
     * building.
     */
    public PersonBuilder withEmergencyContact(String ecName, String ecPhone, String ecrs) {
        EmergencyContact emergencyContact = new EmergencyContact(new Name(ecName), new Phone(ecPhone),
                new Relationship(ecrs));
        this.emergencyContacts = new LinkedHashSet<>(List.of(emergencyContact));
        return this;
    }

    /**
     * Adds an {@code EmergencyContact} to the {@code Person} that we are
     * building.
     */
    public PersonBuilder addEmergencyContact(String ecName, String ecPhone, String ecrs) {
        EmergencyContact emergencyContact = new EmergencyContact(new Name(ecName), new Phone(ecPhone),
                new Relationship(ecrs));
        Set<EmergencyContact> emergencyContactSet = new LinkedHashSet<>(this.emergencyContacts);
        emergencyContactSet.add(emergencyContact);
        this.emergencyContacts = emergencyContactSet;
        return this;
    }

    /**
     * Sets the {@code Doctor Name} of the {@code Person} that we are building.
     */
    public PersonBuilder withDoctorName(String doctorName) {
        this.doctor = new Doctor(new DoctorName(doctorName), doctor.getPhone(), doctor.getEmail());
        return this;
    }

    /**
     * Sets the {@code Doctor Phone} of the {@code Person} that we are building.
     */
    public PersonBuilder withDoctorPhone(String doctorPhone) {
        this.doctor = new Doctor(doctor.getName(), new Phone(doctorPhone), doctor.getEmail());
        return this;
    }

    /**
     * Sets the {@code Doctor Email} of the {@code Person} that we are building.
     */
    public PersonBuilder withDoctorEmail(String doctorEmail) {
        this.doctor = new Doctor(doctor.getName(), doctor.getPhone(), new Email(doctorEmail));
        return this;
    }

    /**
     * Builds a {@code Person} based on the fields set.
     */
    public Person build() {
        assert emergencyContacts.size() > 0;
        return new Person(name, phone, email, address, emergencyContacts, doctor, tags);
    }

}
