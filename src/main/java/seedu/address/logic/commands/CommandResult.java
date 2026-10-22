package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.Optional;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.friend.Friend;
import seedu.address.model.game.GameName;

/**
 * Represents the result of a command execution.
 */
public class CommandResult {

    private final String feedbackToUser;

    /** Help information should be shown to the user. */
    private final boolean showHelp;

    /** The application should exit. */
    private final boolean exit;

    /** The friend whose details the UI should show, when a command has one unambiguous result. */
    private final Friend friendToShow;

    /** An optional filter applied only to the selected friend's game cards. */
    private final GameName gameFilter;

    /** Whether the current details, including any game filter, should remain visible. */
    private final boolean preserveDetails;

    /**
     * Constructs a {@code CommandResult} with the specified fields.
     */
    public CommandResult(String feedbackToUser, boolean showHelp, boolean exit) {
        this(feedbackToUser, showHelp, exit, null, null);
    }

    /**
     * Constructs a {@code CommandResult} that asks the UI to show a friend's details.
     */
    public CommandResult(String feedbackToUser, Friend friendToShow) {
        this(feedbackToUser, friendToShow, null);
    }

    /**
     * Constructs a result selecting a friend and optionally filtering their displayed games.
     * A non-null game filter requires a selected friend.
     */
    public CommandResult(String feedbackToUser, Friend friendToShow, GameName gameFilter) {
        this(feedbackToUser, false, false, friendToShow, gameFilter);
    }

    private CommandResult(String feedbackToUser, boolean showHelp, boolean exit,
            Friend friendToShow, GameName gameFilter) {
        this(feedbackToUser, showHelp, exit, friendToShow, gameFilter, false);
    }

    private CommandResult(String feedbackToUser, boolean showHelp, boolean exit,
            Friend friendToShow, GameName gameFilter, boolean preserveDetails) {
        this.feedbackToUser = requireNonNull(feedbackToUser);
        this.showHelp = showHelp;
        this.exit = exit;
        this.friendToShow = friendToShow;
        if (gameFilter != null) {
            requireNonNull(friendToShow);
        }
        this.gameFilter = gameFilter;
        this.preserveDetails = preserveDetails;
    }

    /**
     * Constructs a {@code CommandResult} with the specified {@code feedbackToUser},
     * and other fields set to their default value.
     */
    public CommandResult(String feedbackToUser) {
        this(feedbackToUser, false, false);
    }

    /** Constructs feedback that leaves the current details view intact. */
    public static CommandResult preservingDetails(String feedbackToUser) {
        return new CommandResult(feedbackToUser, false, false, null, null, true);
    }

    public boolean isPreserveDetails() {
        return preserveDetails;
    }

    public String getFeedbackToUser() {
        return feedbackToUser;
    }

    public boolean isShowHelp() {
        return showHelp;
    }

    public boolean isExit() {
        return exit;
    }

    /**
     * Returns the single friend selected by a command, if it has an unambiguous result.
     */
    public Optional<Friend> getFriendToShow() {
        return Optional.ofNullable(friendToShow);
    }

    /** Returns the game-name filter for the details panel, or empty to show all games. */
    public Optional<GameName> getGameFilter() {
        return Optional.ofNullable(gameFilter);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof CommandResult otherCommandResult)) {
            return false;
        }

        return feedbackToUser.equals(otherCommandResult.feedbackToUser)
                && showHelp == otherCommandResult.showHelp
                && exit == otherCommandResult.exit
                && preserveDetails == otherCommandResult.preserveDetails
                && Objects.equals(friendToShow, otherCommandResult.friendToShow)
                && Objects.equals(gameFilter, otherCommandResult.gameFilter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(feedbackToUser, showHelp, exit, friendToShow, gameFilter, preserveDetails);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("feedbackToUser", feedbackToUser)
                .add("showHelp", showHelp)
                .add("exit", exit)
                .add("friendToShow", getFriendToShow())
                .add("gameFilter", getGameFilter())
                .add("preserveDetails", preserveDetails)
                .toString();
    }

}
