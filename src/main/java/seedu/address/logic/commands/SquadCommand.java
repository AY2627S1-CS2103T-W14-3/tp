package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.Model;
import seedu.address.model.friend.Friend;
import seedu.address.model.game.GameName;

/** Selects a random squad from displayed friends, always including the user. */
public class SquadCommand extends Command {

    public static final String COMMAND_WORD = "squad";

    public static final String MESSAGE_USAGE = "Invalid command format. Usage: " + COMMAND_WORD + " "
            + PREFIX_GAME + "GAME "
            + PREFIX_NAME + "NUMBER_OF_PEOPLE";
    public static final String MESSAGE_SUCCESS = "Found squad of %1$d people:";
    public static final String MESSAGE_INVALID_SIZE = "Invalid squad size. Enter a whole number from 1 to 20.";
    public static final String MESSAGE_INVALID_GAME = "Invalid game name %s. Enter 1–50 printable characters "
            + "containing at least one letter or digit.";
    public static final String MESSAGE_INSUFFICIENT = "Could not find a squad of %d people.";
    public static final int MAX_SIZE = 20;

    private final GameName gameName;
    private final int size;
    private final Random random;

    /** Creates a command using a new random source. */
    public SquadCommand(GameName gameName, int size) {
        this(gameName, size, new Random());
    }

    /** Allows tests to supply a deterministic random source. */
    SquadCommand(GameName gameName, int size, Random random) {
        this.gameName = requireNonNull(gameName);
        checkArgument(size >= 1 && size <= MAX_SIZE, MESSAGE_INVALID_SIZE);
        this.size = size;
        this.random = requireNonNull(random);
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        List<Friend> displayed = new ArrayList<>(model.getFilteredFriendList());
        List<Integer> eligible = new ArrayList<>();
        for (int i = 0; i < displayed.size(); i++) {
            if (displayed.get(i).getGames().stream().anyMatch(game -> game.getGameName().equals(gameName))) {
                eligible.add(i);
            }
        }
        if (eligible.size() < size - 1) {
            return new CommandResult(String.format(MESSAGE_INSUFFICIENT, size));
        }
        Collections.shuffle(eligible, random);
        List<Integer> selected = new ArrayList<>(eligible.subList(0, size - 1));
        selected.sort(Comparator.naturalOrder());
        StringBuilder feedback = new StringBuilder(String.format(MESSAGE_SUCCESS, size)).append("\n1. Me");
        String ownUsername = usernameFor(model.getGameMates().getMyself());
        if (!ownUsername.isEmpty()) {
            feedback.append(" (username: ").append(ownUsername).append(")");
        }
        for (int row = 0; row < selected.size(); row++) {
            int index = selected.get(row);
            Friend friend = displayed.get(index);
            feedback.append("\n").append(row + 2).append(". ").append(friend.getName()).append(" (");
            String username = usernameFor(friend);
            if (!username.isEmpty()) {
                feedback.append("username: ").append(username).append(", ");
            }
            feedback.append("index: ").append(index + 1).append(")");
        }
        return new CommandResult(feedback.toString());
    }

    private String usernameFor(Friend friend) {
        return friend.getGames().stream().filter(game -> game.getGameName().equals(gameName))
                .map(game -> game.getUsername().value).findFirst().orElse("");
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof SquadCommand otherSquadCommand)) {
            return false;
        }

        return gameName.equals(otherSquadCommand.gameName)
                && size == otherSquadCommand.size;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("gameName", gameName)
                .add("size", size)
                .toString();
    }
}
