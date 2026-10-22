package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.friend.Friend;
import seedu.address.model.friend.FriendTarget;
import seedu.address.model.game.Game;
import seedu.address.model.game.GameName;
import seedu.address.model.game.Username;
import seedu.address.model.game.meta.Meta;
import seedu.address.model.game.meta.MetaKey;

/** Edits one existing game belonging to a displayed friend or the separately stored profile. */
public class GameEditCommand extends Command {
    public static final String COMMAND_WORD = "gameedit";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Edits an existing game. Parameters: FRIEND_INDEX|myself g/EXISTING_GAME"
            + " [n/NEW_GAME_NAME] [u/USERNAME] [m/KEY:VALUE]...\n"
            + "At least one editable field is required. Omitted fields are preserved."
            + " Metadata replaces the entire set; a single empty m/ clears it."
            + " Keys are unique ignoring case; values preserve case and may contain colons."
            + " An empty u/ clears the username.\n"
            + "Example: gameedit myself g/Valorant u/newPlayer m/Role:Healer";
    public static final String MESSAGE_NOT_EDITED = "Provide at least one of n/, u/ or m/ to edit a game.";
    public static final String MESSAGE_GAME_NOT_FOUND = "%1$s does not have %2$s.";
    public static final String MESSAGE_DUPLICATE_GAME = "%1$s already has %2$s.";
    public static final String MESSAGE_AMBIGUOUS_GAME = "More than one entry matches %s. Check the stored game data.";
    public static final String MESSAGE_SUCCESS = "Edited %1$s's game: %2$s\nUsername: %3$s\nMetadata: %4$s";

    private final Index targetIndex;
    private final GameName existingGame;
    private final EditGameDescriptor changes;

    /** Creates an edit; a descriptor distinguishes omitted fields from an empty metadata replacement. */
    public GameEditCommand(Index targetIndex, GameName existingGame, EditGameDescriptor changes) {
        this.targetIndex = requireNonNull(targetIndex);
        this.existingGame = requireNonNull(existingGame);
        this.changes = requireNonNull(changes);
        checkArgument(changes.isAnyFieldEdited(), MESSAGE_NOT_EDITED);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        FriendTarget target = FriendTargetResolver.resolve(model, targetIndex);
        Friend friend = target.friend();
        List<Game> matches = friend.getGames().stream()
                .filter(game -> game.getGameName().equals(existingGame)).toList();
        if (matches.isEmpty()) {
            throw new CommandException(String.format(MESSAGE_GAME_NOT_FOUND, friend.getName(), existingGame));
        }
        if (matches.size() > 1) {
            throw new CommandException(String.format(MESSAGE_AMBIGUOUS_GAME, existingGame));
        }
        Game original = matches.getFirst();
        Game replacement = new Game(changes.gameName() == null ? original.getGameName() : changes.gameName(),
                changes.username() == null ? original.getUsername() : changes.username(),
                changes.metas() == null ? original.getMetas() : changes.metas());
        if (friend.getGames().stream().anyMatch(game -> game != original && game.isSameGame(replacement))) {
            throw new CommandException(String.format(MESSAGE_DUPLICATE_GAME, friend.getName(),
                    replacement.getGameName()));
        }
        Set<Game> games = new HashSet<>(friend.getGames());
        games.remove(original);
        games.add(replacement);
        Friend updated = friend.withGames(games);
        model.setFriend(target, updated);
        String metadata = replacement.getMetas().isEmpty() ? "None" : replacement.getMetas().stream()
                .map(Meta::toString).sorted().collect(Collectors.joining(", "));
        String feedback = String.format(MESSAGE_SUCCESS, friend.getName(), replacement.getGameName(),
                replacement.getUsername(), metadata);
        return target.isMyself() ? new CommandResult(feedback) : new CommandResult(feedback, updated);
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof GameEditCommand command
                && targetIndex.equals(command.targetIndex) && existingGame.equals(command.existingGame)
                && changes.equals(command.changes);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("targetIndex", targetIndex).add("existingGame", existingGame)
                .add("changes", changes).toString();
    }

    /** Immutable edits. Null means preserve the field; an empty metadata set means clear it. */
    public record EditGameDescriptor(GameName gameName, Username username, Set<Meta> metas) {
        /** Copies supplied metadata and enforces unique normalized keys before execution. */
        public EditGameDescriptor {
            if (metas != null) {
                metas = Set.copyOf(metas);
                Set<MetaKey> keys = new HashSet<>();
                for (Meta meta : metas) {
                    checkArgument(keys.add(meta.getKey()), Meta.MESSAGE_DUPLICATE_KEY);
                }
            }
        }

        /** Returns whether this descriptor supplies any editable field. */
        public boolean isAnyFieldEdited() {
            return gameName != null || username != null || metas != null;
        }
    }
}
