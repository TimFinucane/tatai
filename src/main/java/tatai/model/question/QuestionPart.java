package tatai.model.question;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.util.Pair;
import util.NumberConstraint;

/**
 * The base part for a question component. Is able to generate numbers, as well as
 * produce, in Tag form, the question
 */
public abstract class QuestionPart {
    /**
     * A tag is text detailing the appearance, as well as
     * a series of inner tags that have text contributing to this one
     */
    public static class Tag {
        @SafeVarargs
        Tag(QuestionPart owner, String text, Pair<Tag, Integer>... tags) {
            this.owner = owner;
            this.text = text;
            this.tags = tags;
        }

        public QuestionPart owner;
        public String               text;
        // The array of tags contained inside this tag, as well as
        //  the index to their starting position in the text
        public Pair<Tag, Integer>   tags[];
    }

    void bindPart(ObjectBinding<Tag> tagRelation) {
        tag.bind(tagRelation);
    }

    public Property<Tag>                    tagProperty() {
        return tag;
    }
    public Operation                        parent() {
        return parent;
    }
    public abstract Pair<String, Integer>   generate(NumberConstraint number);

    Operation                   parent = null;
    private Property<Tag>       tag = new SimpleObjectProperty<>();
}