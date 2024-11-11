package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.commands.AddEmergencyContactCommand.MESSAGE_DUPLICATE_EMERGENCY_CONTACT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DOC_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DOC_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DOC_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMERGENCY_CONTACT_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMERGENCY_CONTACT_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMERGENCY_CONTACT_RELATIONSHIP;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMERGENCY_CONTACT_TO_EDIT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
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
 * Edits the details of an existing person in the address book.
 */
public class EditCommand extends Command {

    public static final String COMMAND_WORD = "edit";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the person identified "
            + "by the index number used in the displayed person list. "
            + "Existing values will be overwritten by the input values.\n\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_PHONE + "PHONE] "
            + "[" + PREFIX_EMAIL + "EMAIL] "
            + "[" + PREFIX_ADDRESS + "ADDRESS] "
            + "[" + PREFIX_EMERGENCY_CONTACT_TO_EDIT + "EMERGENCY_CONTACT_INDEX] "
            + "[" + PREFIX_EMERGENCY_CONTACT_NAME + "EMERGENCY CONTACT NAME] "
            + "[" + PREFIX_EMERGENCY_CONTACT_PHONE + "EMERGENCY CONTACT PHONE] "
            + "[" + PREFIX_EMERGENCY_CONTACT_RELATIONSHIP + "EMERGENCY CONTACT RELATIONSHIP]) "
            + "[" + PREFIX_DOC_NAME + "DOCTOR NAME] "
            + "[" + PREFIX_DOC_PHONE + "DOCTOR PHONE] "
            + "[" + PREFIX_DOC_EMAIL + "DOCTOR EMAIL] "
            + "[" + PREFIX_TAG + "TAG]...\n\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_PHONE + "91234567 "
            + PREFIX_EMAIL + "johndoe@example.com "
            + PREFIX_EMERGENCY_CONTACT_TO_EDIT + "1 "
            + PREFIX_EMERGENCY_CONTACT_NAME + "John Kentucky";

    public static final String MESSAGE_EDIT_PERSON_SUCCESS = "Edited Person: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_PERSON =
        "A person with the same phone number already exists in the address book.";
    public static final String MESSAGE_EMERGENCY_CONTACT_NOT_EDITED = "At least one emergency contact field to edit "
            + "must be provided.";
    public static final String MESSAGE_EMERGENCY_CONTACT_FIELDS_INVALID = "At least one emergency contact index to "
            + "edit must be provided.";

    private final Index index;
    private final EditPersonDescriptor editPersonDescriptor;

    /**
     * @param index                of the person in the filtered person list to edit
     * @param editPersonDescriptor details to edit the person with
     */
    public EditCommand(Index index, EditPersonDescriptor editPersonDescriptor) {
        requireNonNull(index);
        requireNonNull(editPersonDescriptor);
        this.index = index;
        this.editPersonDescriptor = new EditPersonDescriptor(editPersonDescriptor);
    }

    /**
     * Creates and returns a {@code Person} with the details of {@code personToEdit}
     * edited with {@code editPersonDescriptor}.
     */
    private static Person createEditedPerson(Person personToEdit, EditPersonDescriptor editPersonDescriptor)
            throws CommandException {
        assert personToEdit != null;

        Name updatedName = editPersonDescriptor.getName().orElse(personToEdit.getName());
        Phone updatedPhone = editPersonDescriptor.getPhone().orElse(personToEdit.getPhone());
        Email updatedEmail = editPersonDescriptor.getEmail().orElse(personToEdit.getEmail());
        Address updatedAddress = editPersonDescriptor.getAddress().orElse(personToEdit.getAddress());
        Set<EmergencyContact> updatedEmergencyContacts = new LinkedHashSet<>();

        if (editPersonDescriptor.getIndexOfEmergencyContactToEdit().isPresent()) {
            Index index = editPersonDescriptor.getIndexOfEmergencyContactToEdit().get();
            Set<EmergencyContact> personEmergencyContacts = personToEdit.getEmergencyContacts();
            if (index.getZeroBased() >= personEmergencyContacts.size()) {
                throw new CommandException(Messages.MESSAGE_INVALID_EMERGENCY_CONTACT_DISPLAYED_INDEX);
            }

            EmergencyContact emergencyContactToUpdate = personToEdit.getEmergencyContact(index);
            EmergencyContact updatedEmergencyContact =
                    createEditedEmergencyContact(emergencyContactToUpdate, editPersonDescriptor);

            if (personToEdit.hasEmergencyContact(updatedEmergencyContact)) {
                throw new CommandException(MESSAGE_DUPLICATE_EMERGENCY_CONTACT);
            }
            updatedEmergencyContacts =
                    updateEmergencyContacts(personEmergencyContacts, updatedEmergencyContact, index);
        } else {
            updatedEmergencyContacts = personToEdit.getEmergencyContacts();
        }

        Doctor updatedDoctor = createEditedDoctor(personToEdit.getDoctor(), editPersonDescriptor);
        Set<Tag> updatedTags = editPersonDescriptor.getTags().orElse(personToEdit.getTags());

        return new Person(updatedName, updatedPhone, updatedEmail, updatedAddress,
                updatedEmergencyContacts, updatedDoctor, updatedTags);
    }

    private static Set<EmergencyContact> updateEmergencyContacts(Set<EmergencyContact> personEmergencyContacts,
                                                                  EmergencyContact updatedEmergencyContact,
                                                                  Index index) {
        assert !personEmergencyContacts.isEmpty();

        Set<EmergencyContact> updatedEmergencyContacts = new LinkedHashSet<>();
        int i = index.getZeroBased();

        for (EmergencyContact emergencyContact : personEmergencyContacts) {
            if (i == 0) {
                updatedEmergencyContacts.add(updatedEmergencyContact);
            } else {
                updatedEmergencyContacts.add(emergencyContact);
            }
            i = i - 1;
        }
        return updatedEmergencyContacts;
    }

    private static EmergencyContact createEditedEmergencyContact(EmergencyContact emergencyContactToEdit,
            EditPersonDescriptor editPersonDescriptor) {
        assert emergencyContactToEdit != null;

        Name updatedName = editPersonDescriptor.getEmergencyContactName().orElse(emergencyContactToEdit.getName());
        Phone updatedPhone = editPersonDescriptor.getEmergencyContactPhone()
                .orElse(emergencyContactToEdit.getPhone());
        Relationship updatedRelationship = editPersonDescriptor.getEmergencyContactRelationship()
                .orElse(emergencyContactToEdit.getRelationship());

        return new EmergencyContact(updatedName, updatedPhone, updatedRelationship);
    }

    private static Doctor createEditedDoctor(Doctor doctorToEdit,
            EditPersonDescriptor editPersonDescriptor) {
        assert doctorToEdit != null;

        DoctorName updatedName = editPersonDescriptor.getDoctorName().orElse(doctorToEdit.getName());
        Phone updatedPhone = editPersonDescriptor.getDoctorPhone()
                .orElse(doctorToEdit.getPhone());
        Email updatedEmail = editPersonDescriptor.getDoctorEmail()
                .orElse(doctorToEdit.getEmail());

        return new Doctor(updatedName, updatedPhone, updatedEmail);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToEdit = lastShownList.get(index.getZeroBased());
        Person editedPerson = createEditedPerson(personToEdit, editPersonDescriptor);

        if (!personToEdit.isSamePerson(editedPerson) && model.hasPerson(editedPerson)) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }

        model.setPerson(personToEdit, editedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditCommand)) {
            return false;
        }

        EditCommand otherEditCommand = (EditCommand) other;
        return index.equals(otherEditCommand.index)
                && editPersonDescriptor.equals(otherEditCommand.editPersonDescriptor);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("editPersonDescriptor", editPersonDescriptor)
                .toString();
    }

    /**
     * Stores the details to edit the person with. Each non-empty field value will
     * replace the
     * corresponding field value of the person.
     */
    public static class EditPersonDescriptor {
        private Name name;
        private Phone phone;
        private Email email;
        private Address address;
        private Index indexOfEmergencyContactToEdit;
        private Name emergencyContactName;
        private Phone emergencyContactPhone;
        private Relationship emergencyContactRelationship;
        private DoctorName doctorName;
        private Phone doctorPhone;
        private Email doctorEmail;
        private Set<Tag> tags;

        public EditPersonDescriptor() {
        }

        /**
         * Copy constructor.
         * A defensive copy of {@code tags} is used internally.
         */
        public EditPersonDescriptor(EditPersonDescriptor toCopy) {
            setName(toCopy.name);
            setPhone(toCopy.phone);
            setEmail(toCopy.email);
            setAddress(toCopy.address);
            setIndexOfEmergencyContactToEdit(toCopy.indexOfEmergencyContactToEdit);
            setEmergencyContactName(toCopy.emergencyContactName);
            setEmergencyContactPhone(toCopy.emergencyContactPhone);
            setEmergencyContactRelationship(toCopy.emergencyContactRelationship);
            setDoctorName(toCopy.doctorName);
            setDoctorPhone(toCopy.doctorPhone);
            setDoctorEmail(toCopy.doctorEmail);
            setTags(toCopy.tags);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, phone, email, address, indexOfEmergencyContactToEdit,
                    emergencyContactName, emergencyContactPhone, emergencyContactRelationship,
                    doctorName, doctorPhone, doctorEmail, tags);
        }

        public boolean isAnyDoctorFieldEdited() {
            return CollectionUtil.isAnyNonNull(doctorName, doctorPhone, doctorEmail);
        }

        public Optional<Name> getName() {
            return Optional.ofNullable(name);
        }

        public void setName(Name name) {
            this.name = name;
        }

        public Optional<Phone> getPhone() {
            return Optional.ofNullable(phone);
        }

        public void setPhone(Phone phone) {
            this.phone = phone;
        }

        public Optional<Email> getEmail() {
            return Optional.ofNullable(email);
        }

        public void setEmail(Email email) {
            this.email = email;
        }

        public Optional<Address> getAddress() {
            return Optional.ofNullable(address);
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        public Optional<Name> getEmergencyContactName() {
            return Optional.ofNullable(emergencyContactName);
        }

        public void setEmergencyContactName(Name emergencyContactName) {
            this.emergencyContactName = emergencyContactName;
        }

        public Optional<Phone> getEmergencyContactPhone() {
            return Optional.ofNullable(emergencyContactPhone);
        }

        public void setEmergencyContactPhone(Phone emergencyContactPhone) {
            this.emergencyContactPhone = emergencyContactPhone;
        }

        public Optional<Relationship> getEmergencyContactRelationship() {
            return Optional.ofNullable(emergencyContactRelationship);
        }

        public void setEmergencyContactRelationship(Relationship emergencyContactRelationship) {
            this.emergencyContactRelationship = emergencyContactRelationship;
        }

        public Optional<Index> getIndexOfEmergencyContactToEdit() {
            return Optional.ofNullable(indexOfEmergencyContactToEdit);
        }

        public void setIndexOfEmergencyContactToEdit(Index indexOfEmergencyContactToEdit) {
            this.indexOfEmergencyContactToEdit = indexOfEmergencyContactToEdit;
        }

        public Optional<DoctorName> getDoctorName() {
            return Optional.ofNullable(doctorName);
        }

        public void setDoctorName(DoctorName doctorName) {
            this.doctorName = doctorName;
        }

        public Optional<Phone> getDoctorPhone() {
            return Optional.ofNullable(doctorPhone);
        }

        public void setDoctorPhone(Phone doctorPhone) {
            this.doctorPhone = doctorPhone;
        }

        public Optional<Email> getDoctorEmail() {
            return Optional.ofNullable(doctorEmail);
        }

        public void setDoctorEmail(Email doctorEmail) {
            this.doctorEmail = doctorEmail;
        }

        /**
         * Returns an unmodifiable tag set, which throws
         * {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code tags} is null.
         */
        public Optional<Set<Tag>> getTags() {
            return (tags != null) ? Optional.of(Collections.unmodifiableSet(tags)) : Optional.empty();
        }

        /**
         * Sets {@code tags} to this object's {@code tags}.
         * A defensive copy of {@code tags} is used internally.
         */
        public void setTags(Set<Tag> tags) {
            this.tags = (tags != null) ? new HashSet<>(tags) : null;
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditPersonDescriptor)) {
                return false;
            }

            EditPersonDescriptor otherEditPersonDescriptor = (EditPersonDescriptor) other;
            return Objects.equals(name, otherEditPersonDescriptor.name)
                    && Objects.equals(phone, otherEditPersonDescriptor.phone)
                    && Objects.equals(email, otherEditPersonDescriptor.email)
                    && Objects.equals(address, otherEditPersonDescriptor.address)
                    && Objects.equals(indexOfEmergencyContactToEdit,
                            otherEditPersonDescriptor.indexOfEmergencyContactToEdit)
                    && Objects.equals(emergencyContactName, otherEditPersonDescriptor.emergencyContactName)
                    && Objects.equals(emergencyContactPhone, otherEditPersonDescriptor.emergencyContactPhone)
                    && Objects.equals(emergencyContactRelationship,
                            otherEditPersonDescriptor.emergencyContactRelationship)
                    && Objects.equals(doctorName, otherEditPersonDescriptor.doctorName)
                    && Objects.equals(doctorPhone, otherEditPersonDescriptor.doctorPhone)
                    && Objects.equals(doctorEmail, otherEditPersonDescriptor.doctorEmail)
                    && Objects.equals(tags, otherEditPersonDescriptor.tags);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .add("name", name)
                    .add("phone", phone)
                    .add("email", email)
                    .add("address", address)
                    .add("index of emergency contact to edit", indexOfEmergencyContactToEdit)
                    .add("emergency contact name", emergencyContactName)
                    .add("emergency contact phone", emergencyContactPhone)
                    .add("emergency contact relationship", emergencyContactRelationship)
                    .add("doctor name", doctorName)
                    .add("doctor phone", doctorPhone)
                    .add("doctor email", doctorEmail)
                    .add("tags", tags)
                    .toString();
        }
    }
}
