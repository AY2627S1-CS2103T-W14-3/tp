package seedu.address.testutil;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.logic.commands.EditCommand.EditFriendDescriptor;
import seedu.address.model.friend.Email;
import seedu.address.model.friend.Friend;
import seedu.address.model.friend.Name;
import seedu.address.model.friend.Phone;
import seedu.address.model.tag.Tag;

/**
 * A utility class to help with building EditFriendDescriptor objects.
 */
public class EditFriendDescriptorBuilder {

    private EditFriendDescriptor descriptor;

    public EditFriendDescriptorBuilder() {
        descriptor = new EditFriendDescriptor();
    }

    public EditFriendDescriptorBuilder(EditFriendDescriptor descriptor) {
        this.descriptor = new EditFriendDescriptor(descriptor);
    }

    /**
     * Returns an {@code EditFriendDescriptor} with fields containing {@code friend}'s details
     */
    public EditFriendDescriptorBuilder(Friend friend) {
        descriptor = new EditFriendDescriptor();
        descriptor.setName(friend.getName());
        descriptor.setPhone(friend.getPhone());
        descriptor.setEmail(friend.getEmail());
        descriptor.setTags(friend.getTags());
    }

    /**
     * Sets the {@code Name} of the {@code EditFriendDescriptor} that we are building.
     */
    public EditFriendDescriptorBuilder withName(String name) {
        descriptor.setName(new Name(name));
        return this;
    }

    /**
     * Sets the {@code Phone} of the {@code EditFriendDescriptor} that we are building.
     */
    public EditFriendDescriptorBuilder withPhone(String phone) {
        descriptor.setPhone(new Phone(phone));
        return this;
    }

    /**
     * Sets the {@code Email} of the {@code EditFriendDescriptor} that we are building.
     */
    public EditFriendDescriptorBuilder withEmail(String email) {
        descriptor.setEmail(new Email(email));
        return this;
    }

    /**
     * Parses the {@code tags} into a {@code Set<Tag>} and sets it to the {@code EditFriendDescriptor}
     * that we are building.
     */
    public EditFriendDescriptorBuilder withTags(String... tags) {
        Set<Tag> tagSet = Stream.of(tags).map(Tag::new).collect(Collectors.toSet());
        descriptor.setTags(tagSet);
        return this;
    }

    public EditFriendDescriptor build() {
        return descriptor;
    }
}
