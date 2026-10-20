package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_FRIENDS;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import io.vavr.control.Option;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.friend.Email;
import seedu.address.model.friend.Friend;
import seedu.address.model.friend.FriendTarget;
import seedu.address.model.friend.Name;
import seedu.address.model.friend.Phone;
import seedu.address.model.tag.Tag;

/**
 * Edits the details of an existing friend or the Myself profile in GameMates.
 */
public class EditCommand extends Command {

    public static final String COMMAND_WORD = "edit";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits a friend by its displayed index "
            + "or your own profile using myself. "
            + "Supplied name and contact values replace existing values. "
            + "Each supplied tag is removed if present or added if absent. "
            + "Use t/ without a value to remove all tags.\n"
            + "Parameters: FRIEND_INDEX|myself (use a positive integer for a friend, or myself for your own profile) "
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_PHONE + "[PHONE]] "
            + "[" + PREFIX_EMAIL + "[EMAIL]] "
            + "[" + PREFIX_TAG + "TAG]...\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_PHONE + "91234567 "
            + PREFIX_EMAIL + "johndoe@example.com";

    public static final String MESSAGE_EDIT_FRIEND_SUCCESS = "Edited %1$s";
    public static final String MESSAGE_NOT_EDITED = "No details were passed to the command. "
            + "Specify at least one parameter ([n/NAME], [p/[PHONE]], [e/[EMAIL]], [t/TAG]...) "
            + "to edit specific details.";

    public static final String MESSAGE_EMPTY_LIST =
            "No friends are currently displayed. Use list to show all friends.";
    public static final String MESSAGE_INDEX_NOT_FOUND =
            "No friend exists at index %1$d. Choose an index from 1 to %2$d.";

    private final Index index;
    private final EditFriendDescriptor editFriendDescriptor;

    /**
     * @param index of the friend in the filtered friend list, or an index selecting Myself
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
        FriendTarget target = FriendTargetResolver.resolve(model, index, () -> {
            int displayedCount = model.getFilteredFriendList().size();
            return displayedCount == 0 ? MESSAGE_EMPTY_LIST
                    : String.format(MESSAGE_INDEX_NOT_FOUND, index.getOneBased(), displayedCount);
        });
        Friend friendToEdit = target.friend();
        Friend editedFriend = createEditedFriend(friendToEdit, editFriendDescriptor);
        FriendValidator.checkCanEdit(model, target, editedFriend);

        model.setFriend(target, editedFriend);
        if (!target.isMyself()) {
            model.updateFilteredFriendList(PREDICATE_SHOW_ALL_FRIENDS);
        }
        return new CommandResult(formatSuccessMessage(friendToEdit, editedFriend));
    }

    /**
     * Creates and returns a {@code Friend} with the details of {@code friendToEdit}
     * edited with {@code editFriendDescriptor}.
     */
    private static Friend createEditedFriend(Friend friendToEdit, EditFriendDescriptor editFriendDescriptor) {
        assert friendToEdit != null;

        Name updatedName = editFriendDescriptor.getName().getOrElse(friendToEdit.getName());
        Phone updatedPhone = editFriendDescriptor.getPhone().getOrElse(friendToEdit.getPhone());
        Email updatedEmail = editFriendDescriptor.getEmail().getOrElse(friendToEdit.getEmail());
        Set<Tag> updatedTags = new HashSet<>(friendToEdit.getTags());
        editFriendDescriptor.getTags().peek(tags -> {
            if (tags.isEmpty()) {
                updatedTags.clear();
            } else {
                for (Tag tag : tags) {
                    if (!updatedTags.remove(tag)) {
                        updatedTags.add(tag);
                    }
                }
            }
        });

        return new Friend(updatedName, updatedPhone, updatedEmail,
                friendToEdit.getGames(), updatedTags, friendToEdit.isPinned());
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
     * Stores the details to edit the friend with. Provided name and contact values replace existing values.
     * Empty phone/email values remove those details. Each supplied tag is removed if present or added if absent.
     * An empty tag set removes all tags.
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

        public Option<Name> getName() {
            return Option.of(name);
        }

        public void setPhone(Phone phone) {
            this.phone = phone;
        }

        public Option<Phone> getPhone() {
            return Option.of(phone);
        }

        public void setEmail(Email email) {
            this.email = email;
        }

        public Option<Email> getEmail() {
            return Option.of(email);
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
         * Returns {@code Option#none()} if {@code tags} is null.
         */
        public Option<Set<Tag>> getTags() {
            return Option.of(tags).map(Collections::unmodifiableSet);
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
