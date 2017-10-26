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

        Spinner<Integer> minFld = new Spinner<>(1, 99, range.minProperty().get());
        Spinner<Integer> maxFld = new Spinner<>(1, 99, range.maxProperty().get());
        SpinnerFixes.fix(minFld);
        SpinnerFixes.fix(maxFld);
        minFld.setEditable(true);
        maxFld.setEditable(true);

        main.add(new Label("Min"), 0, 0);
        main.add(new Label("Max"), 0, 1);
        main.add(minFld, 1, 0);
        main.add(maxFld, 1, 1);

        setContent(main);

        range.minProperty().bind(minFld.valueProperty());
        range.maxProperty().bind(maxFld.valueProperty());

        SpinnerFixes.tieMinMax(minFld, maxFld);
    }

}
