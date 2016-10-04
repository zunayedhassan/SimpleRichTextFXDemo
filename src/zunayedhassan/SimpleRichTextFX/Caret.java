/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zunayedhassan.SimpleRichTextFX;

import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeType;
import javafx.util.Duration;

/**
 *
 * @author ZUNAYED_PC
 */
public class Caret extends Line {
    public Caret(int size) {
        super(0, 0, 0, size);
        this.setStroke(Color.BLACK);
        this.setStrokeWidth(2);
        this.setStrokeType(StrokeType.CENTERED);
        
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(500), this);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);
        fadeTransition.setCycleCount(Timeline.INDEFINITE);
        fadeTransition.setAutoReverse(true);
        fadeTransition.play();
    }
}
