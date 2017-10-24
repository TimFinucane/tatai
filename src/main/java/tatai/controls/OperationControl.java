package tatai.controls;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import tatai.model.question.Operation;
import tatai.model.question.Operator;

class OperationControl extends TitledPane {
    public OperationControl(Operation operation) {
        setAlignment(Pos.CENTER);
        setText("Operation");

        HBox main = new HBox(10);
        main.setAlignment(Pos.CENTER);

        GridPane grid = new GridPane();
        grid.setVgap(5);
        grid.setHgap(5);
        grid.setAlignment(Pos.CENTER);

        grid.add(_operatorBoxes[0], 0, 0);
        grid.add(_operatorBoxes[1], 1, 0);
        grid.add(_operatorBoxes[2], 0, 1);
        grid.add(_operatorBoxes[3], 1, 1);

        main.getChildren().addAll(_parenthesisedBox, grid);

        setContent(main);

        _operation = operation;

        // Initialize check boxes
        for(Operator op : operation.operatorsProperty()) {
            for(CheckBox box : _operatorBoxes) {
                if(op.symbol().equals(box.getText())) {
                    box.setSelected(true);
                }
            }
        }

        _parenthesisedBox.setSelected(_operation.enclosedProperty().getValue());

        _operation.enclosedProperty().bind(_parenthesisedBox.selectedProperty());
        _operation.operatorsProperty().bind(Bindings.createObjectBinding(() -> {
                    ObservableList<Operator> ops = FXCollections.observableArrayList();
                    for(CheckBox box : _operatorBoxes) {
                        for(Operator.Type type : Operator.Type.values()) {
                            if(box.getText().equals(type.symbol()) && box.isSelected())
                                ops.add(type.create());
                        }
                    }
                    return ops;
                },
                _operatorBoxes[0].selectedProperty(),
                _operatorBoxes[1].selectedProperty(),
                _operatorBoxes[2].selectedProperty(),
                _operatorBoxes[3].selectedProperty()
        ));

        // Add constraints to boxes
        for(CheckBox box : _operatorBoxes)
            box.selectedProperty().addListener((observed, old, newVal) -> applyCheckConstraints(box, newVal));
    }

    /**
     * Ensures that the user can never have no checkboxes selected
     */
    private void    applyCheckConstraints(CheckBox box, Boolean newVal) {
        // If the user is turning a box on the constraint holds
        if(newVal)
            return;

        // If another box is checked then the constraint holds
        for(CheckBox opBox : _operatorBoxes) {
            if(opBox.isSelected() && opBox != box)
                return;
        }

        // The constraint doesnt hold. Block the user action
        // TODO: Message user?
        Platform.runLater(() -> box.setSelected(true));
    }

    private Operation       _operation;

    private CheckBox        _parenthesisedBox = new CheckBox("Brackets");
    private CheckBox        _operatorBoxes[] = new CheckBox[]{
            new CheckBox(Operator.Type.ADD.symbol()),
            new CheckBox(Operator.Type.SUBTRACT.symbol()),
            new CheckBox(Operator.Type.MULTIPLY.symbol()),
            new CheckBox(Operator.Type.DIVIDE.symbol())};
}
