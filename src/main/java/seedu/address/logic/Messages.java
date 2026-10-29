package seedu.address.logic;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.logic.parser.Prefix;
import seedu.address.model.friend.Friend;

/**
 * Container for user visible messages.
 */
public class Messages {

    public static final String MESSAGE_UNKNOWN_COMMAND = "Unknown command.";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT = "Invalid command format!\n%1$s";
    public static final String MESSAGE_INVALID_FRIEND_DISPLAYED_INDEX = "The friend index provided is invalid.";
    public static final String MESSAGE_FRIENDS_LISTED_OVERVIEW = "%1$d friend(s) listed!";
    public static final String MESSAGE_DUPLICATE_ARCHIVED_FRIEND =
            "This friend is archived. Use archivelist to find them, then unarchive their displayed index.";
    public static final String MESSAGE_DUPLICATE_FRIEND = "This friend already exists in GameMates.";
    public static final String MESSAGE_DUPLICATE_MYSELF = "This name is already used by your Myself profile.";
    public static final String MESSAGE_PROFILE_NAME_CONFLICT =
            "This name is already used by a friend. Choose a different name for your profile.";
    public static final String MESSAGE_MYSELF_NOT_INITIALIZED =
            "Set your profile name first using \"edit myself n/NAME\" command.";
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
     * Formats the {@code friend} for display to the user.
     */
    public static String format(Friend friend) {
        final StringBuilder builder = new StringBuilder();
        builder.append(friend.getName())
                .append("; Phone: ")
                .append(friend.getPhone())
                .append("; Email: ")
                .append(friend.getEmail())
                .append("; Tags: ");
        friend.getTags().forEach(builder::append);
        return builder.toString();
    }

}
