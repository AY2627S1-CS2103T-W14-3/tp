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
    private final Remark remark;
    private final Set<Tag> tags;

    /**
     * Every field must be present and not null.
     */
    public Friend(Name name, Phone phone, Email email, Set<Game> games, Remark remark, Set<Tag> tags) {
        requireAllNonNull(name, phone, email, games, remark, tags);
        games.forEach(Objects::requireNonNull);
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.games = Collections.unmodifiableSet(games);
        this.remark = remark;
        this.tags = Collections.unmodifiableSet(tags);
    }

    /**
     * Creates a friend with an empty remark.
     */
    public Friend(Name name, Phone phone, Email email, Set<Game> games, Set<Tag> tags) {
        this(name, phone, email, games, Remark.EMPTY, tags);
    }

    /**
     * Every field must be present and not null.
     */
    public Friend(Name name, Phone phone, Email email, Set<Tag> tags) {
        this(name, phone, email, Collections.emptySet(), tags);
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

    public Remark getRemark() {
        return remark;
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return tags;
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
                && remark.equals(otherFriend.remark)
                && tags.equals(otherFriend.tags);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, phone, email, games, remark, tags);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("phone", phone)
                .add("email", email)
                .add("games", games)
                .add("remark", remark)
                .add("tags", tags)
                .toString();
    }

}
