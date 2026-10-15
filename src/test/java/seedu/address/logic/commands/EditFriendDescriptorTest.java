package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.EditCommand.EditFriendDescriptor;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.EditFriendDescriptorBuilder;

public class EditFriendDescriptorTest {

    @Test
    public void tags_setAndCopy_protectAgainstExternalMutation() {
        Tag tag = new Tag(VALID_TAG_HUSBAND);
        Set<Tag> tags = new HashSet<>(Set.of(tag));
        EditFriendDescriptor descriptor = new EditFriendDescriptor();
        descriptor.setTags(tags);
        EditFriendDescriptor copy = new EditFriendDescriptor(descriptor);

        tags.clear();
        assertEquals(Set.of(tag), descriptor.getTags().get());
        assertThrows(UnsupportedOperationException.class, () -> descriptor.getTags().get().clear());

        descriptor.setTags(Set.of());
        assertTrue(descriptor.getTags().isDefined());
        assertTrue(descriptor.getTags().get().isEmpty());
        assertEquals(Set.of(tag), copy.getTags().get());
    }

    @Test
    public void equals() {
        // same values -> returns true
        EditFriendDescriptor descriptorWithSameValues = new EditFriendDescriptor(DESC_AMY);
        assertTrue(DESC_AMY.equals(descriptorWithSameValues));

        // same object -> returns true
        assertTrue(DESC_AMY.equals(DESC_AMY));

        // null -> returns false
        assertFalse(DESC_AMY.equals(null));

        // different types -> returns false
        assertFalse(DESC_AMY.equals(5));

        // different values -> returns false
        assertFalse(DESC_AMY.equals(DESC_BOB));

        // different name -> returns false
        EditFriendDescriptor editedAmy = new EditFriendDescriptorBuilder(DESC_AMY).withName(VALID_NAME_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different phone -> returns false
        editedAmy = new EditFriendDescriptorBuilder(DESC_AMY).withPhone(VALID_PHONE_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different email -> returns false
        editedAmy = new EditFriendDescriptorBuilder(DESC_AMY).withEmail(VALID_EMAIL_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different tags -> returns false
        editedAmy = new EditFriendDescriptorBuilder(DESC_AMY).withTags(VALID_TAG_HUSBAND).build();
        assertFalse(DESC_AMY.equals(editedAmy));
    }

    @Test
    public void toStringMethod() {
        EditFriendDescriptor editFriendDescriptor = new EditFriendDescriptor();
        String expected = EditFriendDescriptor.class.getCanonicalName() + "{name="
                + editFriendDescriptor.getName().getOrNull() + ", phone="
                + editFriendDescriptor.getPhone().getOrNull() + ", email="
                + editFriendDescriptor.getEmail().getOrNull() + ", tags="
                + editFriendDescriptor.getTags().getOrNull() + "}";
        assertEquals(expected, editFriendDescriptor.toString());
    }
}
