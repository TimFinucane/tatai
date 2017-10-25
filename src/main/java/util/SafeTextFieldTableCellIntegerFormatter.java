package util;

import com.sun.media.jfxmedia.logging.Logger;
import javafx.event.Event;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;

import java.lang.reflect.Field;

/**
 * Uses the text formatter to create a text field table cell that formats as an integer.
 * The more ridiculous these changes are, the more ridiculous the names will become
 */
public class SafeTextFieldTableCellIntegerFormatter<S> extends TextFieldTableCell<S, Integer>{
    public SafeTextFieldTableCellIntegerFormatter(int defaultValue) {
        this(SafeIntegerTextFormatter.create(defaultValue));
    }
    private SafeTextFieldTableCellIntegerFormatter(TextFormatter<Integer> formatter) {
        super(formatter.getValueConverter());
        textFormatter = formatter;
    }
    /**
     * This code was retrieved from kleopatra, at the link:
     * https://stackoverflow.com/questions/29724998/javafx-create-table-cell-accepts-numbers-only/29743588#29743588
     * It has been modified to suit this application.
     */
    @Override
    public void startEdit() {
        super.startEdit();

        // Sets the parent's text field to use this text formatter
        if(isEditing()) {
            Class<?> clazz = TextFieldTableCell.class;
            try {
                Field field = clazz.getDeclaredField("textField");
                field.setAccessible(true);
                TextField textField = ((TextField) field.get(this));

                textField.setTextFormatter(textFormatter);
                textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                    if (!isNowFocused)
                        commitEdit(getConverter().fromString(textField.getText()));
                });
            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
                /* Problems beyond the realm of this app */
                Logger.logMsg(Logger.ERROR, "Can't format a TableColumn: " + e.getMessage());
            }
        }
    }

    /**
     * and THIS code was retrieved from:
     * https://gist.github.com/james-d/be5bbd6255a4640a5357#file-editcell-java-L109
     * in which the writer explains that they had to look up the actual source code to retrieve this solution
     */
    @Override
    public void commitEdit(Integer item) {
        // This block is necessary to support commit on losing focus, because
        // the baked-in mechanism sets our editing state to false before we can
        // intercept the loss of focus. The default commitEdit(...) method
        // simply bails if we are not editing...
        if (!isEditing() && !item.equals(getItem())) {
            TableView<S> table = getTableView();
            if (table != null) {
                TableColumn<S, Integer> column = getTableColumn();
                TableColumn.CellEditEvent<S, Integer> event = new TableColumn.CellEditEvent<>(
                        table,
                        new TablePosition<S,Integer>(table, getIndex(), column),
                        TableColumn.editCommitEvent(),
                        item
                );

                Event.fireEvent(column, event);
            }
        }

        super.commitEdit(item);
    }

    private TextFormatter<Integer>  textFormatter;
}
