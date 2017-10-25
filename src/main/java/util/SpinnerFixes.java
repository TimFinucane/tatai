package util;

import javafx.application.Platform;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

/**
 * Yes, I had enough code that this was actually worth it
 */
public class SpinnerFixes {
    /**
     * Assignes the normal values to an integer spinner as well as applying the fix
     * Also makes for editable values a formatter to prevent exceptions on wrong user input
     */
    public static void     assign(Spinner<Integer> spinner, int min, int max, int start) {
        spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(min, max, start));
        if(spinner.isEditable())
            spinner.getEditor().setTextFormatter(SafeIntegerTextFormatter.create(start));
        fix(spinner);
    }

    /**
     * This forces the spinner to commit an edit on focus lost, as by default it doesn't for some weird reason.
     */
    public static void     fix(Spinner spinner) {
        spinner.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue)
                spinner.increment(0);
        });
    }

    /**
     * Prevents the min controller from having a value greater than the max
     */
    public static void     tieMinMax(Spinner<Integer> min, Spinner<Integer> max) {
        max.valueProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue < min.getValue())
                Platform.runLater(() -> max.getValueFactory().setValue(oldValue));
        });
        min.valueProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue > max.getValue())
                Platform.runLater(() -> min.getValueFactory().setValue(oldValue));
        });
    }
}
