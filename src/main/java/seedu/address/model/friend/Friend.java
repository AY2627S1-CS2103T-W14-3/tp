package seedu.address.model.friend;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.game.Game;
import seedu.address.model.tag.Tag;

/**
 * Represents a Friend in GameMates.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Friend {

    public static final Friend DEFAULT_MYSELF = new Friend(new Name("Myself"), Phone.EMPTY, Email.EMPTY, Set.of());

    // Identity fields
    private final Name name;
    private final Phone phone;
    private final Email email;

    // Data fields
    private final Set<Game> games;
    private final Set<Tag> tags;
    private final boolean archived;
    private final boolean pinned;

    /**
     * Every field must be present and not null.
     */
    public Friend(Name name, Phone phone, Email email, Set<Game> games, Set<Tag> tags) {
        this(name, phone, email, games, tags, false, false);
    }

    /** Creates a friend with the given archive status and preserved details. */
    public Friend(Name name, Phone phone, Email email, Set<Game> games, Set<Tag> tags, boolean archived) {
        this(name, phone, email, games, tags, archived, false);
    }

    /** Creates a friend with the given archive and pin statuses. */
    public Friend(Name name, Phone phone, Email email, Set<Game> games, Set<Tag> tags, boolean archived,
            boolean pinned) {
        requireAllNonNull(name, phone, email, games, tags);
        games.forEach(Objects::requireNonNull);
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.games = Collections.unmodifiableSet(games);
        this.tags = Collections.unmodifiableSet(tags);
        this.archived = archived;
        this.pinned = pinned;
    }

    /**
     * Every field must be present and not null.
     */
    public Friend(Name name, Phone phone, Email email, Set<Tag> tags) {
        this(name, phone, email, Collections.emptySet(), tags);
    }

    private Friend(Friend original, Set<Game> replacementGames) {
        requireAllNonNull(original, replacementGames);
        replacementGames.forEach(Objects::requireNonNull);
        name = original.name;
        phone = original.phone;
        email = original.email;
        tags = original.tags;
        games = Set.copyOf(replacementGames);
        archived = original.archived;
        pinned = original.pinned;
    }

    public boolean isArchived() {
        return archived;
    }

    /** Returns a copy with the given archive status, preserving all other fields. */
    public Friend withArchived(boolean archived) {
        return new Friend(name, phone, email, games, tags, archived, pinned);
    }

    public Name getName() {
        return name;
    }

    public Phone getPhone() {
        return phone;
    }

    public Email getEmail() {
        return email;
    }

    /**
     * Returns an immutable game set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Game> getGames() {
        return games;
    }

    /** Copies all friend/profile fields while replacing only games. */
    public Friend withGames(Set<Game> replacementGames) {
        return new Friend(this, replacementGames);
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return tags;
    }

    /** Returns whether this friend is pinned to the top of the friend list. */
    public boolean isPinned() {
        return pinned;
    }

    /** Returns a copy with the given pin status, preserving all other fields. */
    public Friend withPinned(boolean pinned) {
        return new Friend(name, phone, email, games, tags, archived, pinned);
    }

    /**
     * Returns true if both friends have the same name, ignoring case and repeated spaces.
     * This defines a weaker notion of equality between two friends.
     */
    public boolean isSameFriend(Friend otherFriend) {
        if (otherFriend == this) {
            return true;
        }

        return otherFriend != null
                && name.equals(otherFriend.name);
    }

    /**
     * Returns true if both friends have the same identity and data fields.
     * This defines a stronger notion of equality between two friends.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Friend otherFriend)) {
            return false;
        }

        return name.equals(otherFriend.name)
                && phone.equals(otherFriend.phone)
                && email.equals(otherFriend.email)
                && games.equals(otherFriend.games)
                && tags.equals(otherFriend.tags)
                && archived == otherFriend.archived
                && pinned == otherFriend.pinned;
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, phone, email, games, tags, archived, pinned);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("phone", phone)
                .add("email", email)
                .add("games", games)
                .add("tags", tags)
                .add("archived", archived)
                .add("pinned", pinned)
                .toString();
    }

}
