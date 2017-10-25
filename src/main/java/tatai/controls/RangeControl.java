package tatai.controls;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import tatai.model.question.Range;
import util.SpinnerFixes;

class RangeControl extends TitledPane {
    RangeControl(Range range) {
        setText("Range");
        setAlignment(Pos.CENTER);

        // Formatting stuff
        GridPane main = new GridPane();
        main.setVgap(10);
        main.setHgap(5);
        main.setAlignment(Pos.CENTER);

        _minFld = new Spinner<>(1, 99, range.minProperty().get());
        _maxFld = new Spinner<>(1, 99, range.maxProperty().get());
        _minFld.setEditable(true);
        _maxFld.setEditable(true);

        main.add(new Label("Min"), 0, 0);
        main.add(new Label("Max"), 0, 1);
        main.add(_minFld, 1, 0);
        main.add(_maxFld, 1, 1);

        setContent(main);

        range.minProperty().bind(_minFld.valueProperty());
        range.maxProperty().bind(_maxFld.valueProperty());

        SpinnerFixes.tieMinMax(_minFld, _maxFld);
    }

    private Spinner<Integer> _minFld;
    private Spinner<Integer> _maxFld;
}
