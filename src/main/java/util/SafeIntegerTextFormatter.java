package util;

import javafx.scene.control.TextFormatter;
import javafx.util.converter.IntegerStringConverter;

import javax.annotation.Nonnull;

/**
 * Makes a safe version which prevents throwing exceptions at basic user error.
 */
public class SafeIntegerTextFormatter {
    /**
     * Gets a text formatter that DOESN'T THROW ON USER INPUT
     */
    @Nonnull
    public static TextFormatter<Integer> create(int defaultValue) {
        return new TextFormatter<>(
                new IntegerStringConverter(),
                defaultValue,
                (TextFormatter.Change change) -> {
                    if(!change.getControlNewText().matches("-?\\d*"))
                        return null; // Revert value

                    return change;
                }
        );
    }
}
