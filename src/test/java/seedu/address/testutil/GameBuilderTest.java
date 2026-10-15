package seedu.address.testutil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.model.game.Game;
import seedu.address.model.game.meta.Meta;
import seedu.address.model.game.meta.MetaKey;
import seedu.address.model.game.meta.MetaValue;

/**
 * Tests appending metadata without modifying copied or previously built games.
 */
public class GameBuilderTest {

    private static final Meta ROLE = new Meta(new MetaKey("Role"), new MetaValue("Support"));
    private static final Meta STYLE = new Meta(new MetaKey("Style"), new MetaValue("Casual:Mid"));

    @Test
    public void addMeta_chained_preservesPairsAndBuiltGames() {
        GameBuilder builder = new GameBuilder().addMeta(" Role ", " Support ");
        Game first = builder.build();
        Game second = builder.addMeta("Style", "Casual:Mid").build();
        assertEquals(Set.of(ROLE), first.getMetas());
        assertEquals(Set.of(ROLE, STYLE), second.getMetas());
        assertNotEquals(first, second);
    }

    @Test
    public void addMeta_afterCopyOrWithMetas_appendsWithoutChangingSource() {
        Game original = new GameBuilder().withMetas(ROLE).build();
        Game copy = new GameBuilder(original).addMeta("Style", "Casual:Mid").build();
        Game replaced = new GameBuilder().withMetas(ROLE).addMeta("Style", "Casual:Mid").build();
        assertEquals(Set.of(ROLE), original.getMetas());
        assertEquals(Set.of(ROLE, STYLE), copy.getMetas());
        assertEquals(copy, replaced);
    }

    @Test
    public void withMetas_afterAddMeta_replacesAndClearsMetadata() {
        GameBuilder builder = new GameBuilder().addMeta("Role", "Support");
        assertEquals(Set.of(STYLE), builder.withMetas(STYLE).build().getMetas());
        assertEquals(Set.of(), builder.withMetas().build().getMetas());
        assertEquals(Set.of(ROLE), builder.addMeta("Role", "Support").build().getMetas());
    }

    @Test
    public void addMeta_invalidFields_rejectedWithoutModifyingBuilder() {
        GameBuilder builder = new GameBuilder().addMeta("Role", "Support");
        assertThrows(NullPointerException.class, () -> builder.addMeta(null, "Casual"));
        assertThrows(NullPointerException.class, () -> builder.addMeta("Style", null));
        assertThrows(IllegalArgumentException.class, () -> builder.addMeta("Style:Type", "Casual"));
        assertThrows(IllegalArgumentException.class, () -> builder.addMeta("Style", "!!!"));
        assertEquals(Set.of(ROLE), builder.build().getMetas());
    }

    @Test
    public void build_duplicateMetadataKeys_rejected() {
        GameBuilder builder = new GameBuilder().addMeta("Role", "Support").addMeta(" role ", "Mid");
        assertThrows(IllegalArgumentException.class, Meta.MESSAGE_DUPLICATE_KEY, builder::build);
    }
}
