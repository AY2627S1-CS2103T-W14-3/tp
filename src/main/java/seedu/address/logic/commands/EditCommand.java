package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_FRIENDS;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.friend.Email;
import seedu.address.model.friend.Friend;
import seedu.address.model.friend.Name;
import seedu.address.model.friend.Phone;
import seedu.address.model.tag.Tag;

/**
 * Edits the details of an existing friend in GameMates.
 */
public class EditCommand extends Command {

    public static final String COMMAND_WORD = "edit";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the friend identified "
            + "by the index number used in the displayed friend list. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_PHONE + "[PHONE]] "
            + "[" + PREFIX_EMAIL + "[EMAIL]] "
            + "[" + PREFIX_TAG + "TAG]...\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_PHONE + "91234567 "
            + PREFIX_EMAIL + "johndoe@example.com";

    public static final String MESSAGE_EDIT_FRIEND_SUCCESS = "Edited %1$s";
    public static final String MESSAGE_NOT_EDITED = "No details were passed to the command. "
            + "Use a parameter (p/PHONE_NUMBER, e/EMAIL, n/NAME, etc.) "
                    + "to edit specific details.";
    public static final String MESSAGE_DUPLICATE_FRIEND = "This friend already exists in GameMates.";

    public static final String MESSAGE_INVALID_INDEX = "Invalid friend index. Enter a positive integer.";
    public static final String MESSAGE_EMPTY_LIST =
            "No friends are currently displayed. Use list to show all friends.";
    public static final String MESSAGE_INDEX_NOT_FOUND =
            "No friend exists at index %1$d. Choose an index from 1 to %2$d.";

    private final Index index;
    private final EditFriendDescriptor editFriendDescriptor;

    /**
     * @param index of the friend in the filtered friend list to edit
     * @param editFriendDescriptor details to edit the friend with
     */
    public EditCommand(Index index, EditFriendDescriptor editFriendDescriptor) {
        requireNonNull(index);
        requireNonNull(editFriendDescriptor);

        this.index = index;
        this.editFriendDescriptor = new EditFriendDescriptor(editFriendDescriptor);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Friend> lastShownList = model.getFilteredFriendList();

        if (lastShownList.isEmpty()) {
            throw new CommandException(MESSAGE_EMPTY_LIST);
        }
        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(String.format(MESSAGE_INDEX_NOT_FOUND,
                    index.getOneBased(), lastShownList.size()));
        }

        Friend friendToEdit = lastShownList.get(index.getZeroBased());
        Friend editedFriend = createEditedFriend(friendToEdit, editFriendDescriptor);

        if (!friendToEdit.isSameFriend(editedFriend) && model.hasFriend(editedFriend)) {
            throw new CommandException(MESSAGE_DUPLICATE_FRIEND);
        }

        model.setFriend(friendToEdit, editedFriend);
        model.updateFilteredFriendList(PREDICATE_SHOW_ALL_FRIENDS);
        return new CommandResult(formatSuccessMessage(friendToEdit, editedFriend));
    }

    /**
     * Formats feedback with only the details whose stored values changed.
     */
    static String formatSuccessMessage(Friend original, Friend edited) {
        StringBuilder feedback = new StringBuilder(String.format(MESSAGE_EDIT_FRIEND_SUCCESS, edited.getName()));
        if (!original.getName().fullName.equals(edited.getName().fullName)) {
            feedback.append("\n- changed name to ").append(edited.getName());
        }
        if (!original.getPhone().equals(edited.getPhone())) {
            feedback.append(edited.getPhone().isEmpty() ? "\n- removed phone number"
                    : "\n- changed phone number to " + edited.getPhone());
        }
        if (!original.getEmail().equals(edited.getEmail())) {
            feedback.append(edited.getEmail().isEmpty() ? "\n- removed email"
                    : "\n- changed email to " + edited.getEmail());
        }
        if (!original.getTags().equals(edited.getTags())) {
            feedback.append(edited.getTags().isEmpty() ? "\n- removed tags" : "\n- changed tags to "
                    + edited.getTags().stream().map(tag -> tag.tagName).sorted()
                            .collect(Collectors.joining(", ")));
        }
        return feedback.toString();
    }

    /**
     * Creates and returns a {@code Friend} with the details of {@code friendToEdit}
     * edited with {@code editFriendDescriptor}.
     */
    private static Friend createEditedFriend(Friend friendToEdit, EditFriendDescriptor editFriendDescriptor) {
        assert friendToEdit != null;

        Name updatedName = editFriendDescriptor.getName().orElse(friendToEdit.getName());
        Phone updatedPhone = editFriendDescriptor.getPhone().orElse(friendToEdit.getPhone());
        Email updatedEmail = editFriendDescriptor.getEmail().orElse(friendToEdit.getEmail());
        Set<Tag> updatedTags = editFriendDescriptor.getTags().orElse(friendToEdit.getTags());

        return new Friend(updatedName, updatedPhone, updatedEmail,
                friendToEdit.getGames(), updatedTags);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditCommand otherEditCommand)) {
            return false;
        }

        return index.equals(otherEditCommand.index)
                && editFriendDescriptor.equals(otherEditCommand.editFriendDescriptor);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("editFriendDescriptor", editFriendDescriptor)
                .toString();
    }

    /**
     * Stores the details to edit the friend with. Each provided field value will replace the
     * corresponding field value of the friend. Empty phone/email values remove those details.
     */
    public static class EditFriendDescriptor {
        private Name name;
        private Phone phone;
        private Email email;
        private Set<Tag> tags;

        public EditFriendDescriptor() {}

        /**
         * Copy constructor.
         * A defensive copy of {@code tags} is used internally.
         */
        public EditFriendDescriptor(EditFriendDescriptor toCopy) {
            setName(toCopy.name);
            setPhone(toCopy.phone);
            setEmail(toCopy.email);
            setTags(toCopy.tags);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, phone, email, tags);
        }

        public void setName(Name name) {
            this.name = name;
        }

        public Optional<Name> getName() {
            return Optional.ofNullable(name);
        }

        public void setPhone(Phone phone) {
            this.phone = phone;
        }

        public Optional<Phone> getPhone() {
            return Optional.ofNullable(phone);
        }

        public void setEmail(Email email) {
            this.email = email;
        }

        public Optional<Email> getEmail() {
            return Optional.ofNullable(email);
        }

        /**
         * Sets {@code tags} to this object's {@code tags}.
         * A defensive copy of {@code tags} is used internally.
         */
        public void setTags(Set<Tag> tags) {
            this.tags = (tags != null) ? new HashSet<>(tags) : null;
        }

        /**
         * Returns an unmodifiable tag set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code tags} is null.
         */
        public Optional<Set<Tag>> getTags() {
            return (tags != null) ? Optional.of(Collections.unmodifiableSet(tags)) : Optional.empty();
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditFriendDescriptor otherEditFriendDescriptor)) {
                return false;
            }

            return Objects.equals(name, otherEditFriendDescriptor.name)
                    && Objects.equals(phone, otherEditFriendDescriptor.phone)
                    && Objects.equals(email, otherEditFriendDescriptor.email)
                    && Objects.equals(tags, otherEditFriendDescriptor.tags);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .add("name", name)
                    .add("phone", phone)
                    .add("email", email)
                    .add("tags", tags)
                    .toString();
        }
    }
}
