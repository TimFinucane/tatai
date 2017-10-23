package tatai.controls;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Pair;
import tatai.model.question.QuestionReader;

/**
 * Allows the user to create their own question
 */
public class CustomQuestionControl extends Region {
    public CustomQuestionControl() {
        this(new QuestionPart.Range());
    }
    public CustomQuestionControl(String original) {
        this(QuestionPart.generate(QuestionReader.read(original).head()));
    }
    private CustomQuestionControl(QuestionPart root) {
        VBox left = new VBox(15, _textFlow, _infoLbl);
        _main.getChildren().addAll(left, _root);

        getChildren().add(_main);

        updateFlow();

        _root.appearanceProperty().addListener(
                (ObservableValue<? extends QuestionPart.Tag> a, QuestionPart.Tag b, QuestionPart.Tag newTag) ->
                        updateFlow());
    }

    /**
     * Updates the textflow with new tag structure
     */
    private void            updateFlow() {
        _textFlow.getChildren().clear();
        updateFlow(_root.appearanceProperty().getValue(), 0);
    }
    /**
     * Updates the textflow with new tag structure
     * @param depth Used for colour coordination, determines colour of given text
     */
    private void            updateFlow(QuestionPart.Tag rootTag, int depth) {
        if(rootTag.tags == null) {
            addText(rootTag, depth);
            return;
        }

        // Colour text before the first tag as the root tag
        if(rootTag.tags[0].getValue() > 0)
            addText(rootTag, 0, rootTag.tags[0].getValue(), depth);

        updateFlow(rootTag.tags[0].getKey(), depth + 1);

        for(int i = 1; i < rootTag.tags.length; i++) {
            // Colour space between previous and this tag as root
            if(rootTag.tags[i-1].getValue() + rootTag.tags[i-1].getKey().text.length() < rootTag.tags[i].getValue()) {
                addText(
                    rootTag,
                    rootTag.tags[i-1].getValue() + rootTag.tags[i-1].getKey().text.length(),
                    rootTag.tags[i].getValue(),
                    depth);
            }
            updateFlow(rootTag.tags[i].getKey(), depth + 1);
        }

        // Colour text after last tag
        if(rootTag.tags[rootTag.tags.length-1].getValue() < rootTag.text.length()) {
            addText(
                rootTag,
                rootTag.tags[rootTag.tags.length-1].getValue(),
                rootTag.text.length(),
                depth);
        }
    }

    /**
     * Switches to viewing the QuestionPart the user clicked on
     */
    private void            select(QuestionPart part) {
        _main.getChildren().remove(_selected);
        _selected = part;
        _main.getChildren().add(_selected);
    }

    /**
     * Gets the QuestionPart at the index given in the text flow
     */
    private QuestionPart    getPartAt(int index) {
        return getPartAt(index, _root.appearanceProperty().getValue());
    }
    /**
     * Gets the QuestionPart at the specified index into the given parent QuestionPart
     */
    private QuestionPart    getPartAt(int index, QuestionPart.Tag part) {
        for(Pair<QuestionPart.Tag, Integer> tag : _root.appearanceProperty().getValue().tags) {
            // Check if the tag contains the index
            if(index > tag.getValue() && index < (tag.getValue() + tag.getKey().text.length()))
                return getPartAt(index - tag.getValue(), tag.getKey()); // Go deeper
        }

        // If it's not in any of the children, it must be in this
        return part.questionPart;
    }

    private void            addText(QuestionPart.Tag tag, int start, int end, int depth) {
        Text text = new Text(tag.text.substring(start, end));
        text.setFill(colours[depth]);

        // Ensure that when the user clicks this bit, we select it
        text.setOnMouseClicked(event -> select(tag.questionPart));

        _textFlow.getChildren().add(text);
    }
    private void            addText(QuestionPart.Tag tag, int depth) {
        addText(tag, 0, tag.text.length(), depth);
    }

    private HBox            _main = new HBox(15);
    private TextFlow        _textFlow = new TextFlow();
    private Label           _infoLbl  = new Label();

    private QuestionPart    _selected;
    private QuestionPart    _root;

    // TODO: Give to CSS to choose?
    // Colours for the text flow
    private static final Color colours[] = new Color[]{
            Color.BLANCHEDALMOND,
            Color.ALICEBLUE,
            Color.ROSYBROWN,
            Color.AZURE
    };
}
