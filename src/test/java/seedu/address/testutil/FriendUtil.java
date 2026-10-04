package seedu.address.testutil;

import java.util.Set;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.EditCommand.EditFriendDescriptor;
import seedu.address.model.friend.Friend;
import seedu.address.model.tag.Tag;

import static seedu.address.logic.parser.CliSyntax.*;

/**
 * A utility class for Friend.
 */
public class FriendUtil {

    /**
     * Returns an add command string for adding the {@code friend}.
     */
    public static String getAddCommand(Friend friend) {
        return AddCommand.COMMAND_WORD + " " + getFriendDetails(friend);
    }

    /**
     * Returns the part of command string for the given {@code friend}'s details.
     */
    public static String getFriendDetails(Friend friend) {
        StringBuilder sb = new StringBuilder();
        sb.append(PREFIX_NAME + friend.getName().fullName + " ");
        sb.append(PREFIX_PHONE + friend.getPhone().value + " ");
        sb.append(PREFIX_EMAIL + friend.getEmail().value + " ");
        sb.append(PREFIX_ADDRESS + friend.getAddress().value + " ");
        friend.getTags().stream().forEach(
            s -> sb.append(PREFIX_TAG + s.tagName + " ")
        );
        return sb.toString();
    }

    /**
     * Returns the part of command string for the given {@code EditFriendDescriptor}'s details.
     */
    public static String getEditFriendDescriptorDetails(EditFriendDescriptor descriptor) {
        StringBuilder sb = new StringBuilder();
        descriptor.getName().ifPresent(name -> sb.append(PREFIX_NAME).append(name.fullName).append(" "));
        descriptor.getPhone().ifPresent(phone -> sb.append(PREFIX_PHONE).append(phone.value).append(" "));
        descriptor.getEmail().ifPresent(email -> sb.append(PREFIX_EMAIL).append(email.value).append(" "));
        descriptor.getAddress().ifPresent(address -> sb.append(PREFIX_ADDRESS).append(address.value).append(" "));
        if (descriptor.getTags().isPresent()) {
            Set<Tag> tags = descriptor.getTags().get();
            if (tags.isEmpty()) {
                sb.append(PREFIX_TAG);
            } else {
                tags.forEach(s -> sb.append(PREFIX_TAG).append(s.tagName).append(" "));
            }
        }
        return sb.toString();
    }
}
