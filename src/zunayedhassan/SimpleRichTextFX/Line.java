/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zunayedhassan.SimpleRichTextFX;

import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.input.MouseButton;

/**
 *
 * @author ZUNAYED_PC
 */
public class Line extends HBox {
    private RichText _parent = null;
    
    protected boolean isLinePressed = false;
    boolean isCurrentLineWithMouseOverFound = false;
    
    public Line(RichText parent) {
        this._parent = parent;
        this._initialize();
        this.setAlignment(Pos.BASELINE_LEFT);
        
        // Event
        this.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() != MouseButton.SECONDARY) {
                    boolean isMouseClickedOnCharacter = false;
                
                    for (Node characterNode : getChildren()) {
                        if (((Alphabet) characterNode).isMouseClickedOn) {
                            isMouseClickedOnCharacter = true;
                            ((Alphabet) characterNode).isMouseClickedOn = true;
                            break;
                        }
                    }

                    if (!isMouseClickedOnCharacter) {
                        _parent.ClearCaret();
                        ((Alphabet) getChildren().get(getChildren().size() - 1)).SetCaret();
                    }
                }
            }
        });
        
        this.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                isLinePressed = true;
            }
        });
        
        this.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                isLinePressed = false;
                _parent.currentSelectedLine = -1;
            }
        });
        
        this.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                test(event);
            }
        });
    }
    
    private void _initialize() {
        Alphabet newAlphabet = new Alphabet(this._parent, "");
        this.getChildren().add(newAlphabet);
        newAlphabet.SetCaret();
    }
    
    public int GetTotalCharacters() {
        return (this.getChildren().size() - 1);
    }
    
    public String GetText(int beginIndex, int endIndex) {
        String text = "";
        
        if ((beginIndex >= 0) &&
            (endIndex < this.getChildren().size()) &&
            (beginIndex <= endIndex) &&
            (this.GetTotalCharacters() > 0)) {
            
            for (int i = beginIndex + 1; i <= endIndex + 1; i++) {
                text += ((Alphabet) this.getChildren().get(i)).GetText();
            }
            
        }
        
        return text;
    }
    
    public void SelectAll() {
        for (Node node : getChildren()) {
            ((Alphabet) node).SetSelect(true);
            ((Alphabet) node).isMouseOver = true;
        }
    }
    
    public static boolean IS_WITHIN_GIVEN_AREA(Point2D givenPoint, Node givenArea) {
        Bounds givenAreaScreenBounds = givenArea.localToScreen(givenArea.getBoundsInLocal());
        
        double givenAreaMinX = givenAreaScreenBounds.getMinX();
        double givenAreaMinY = givenAreaScreenBounds.getMinY();
        double givenAreaMaxX = givenAreaScreenBounds.getMaxX();
        double givenAreaMaxY = givenAreaScreenBounds.getMaxY();
        
        if (((givenPoint.getX() >= givenAreaMinX) && (givenPoint.getX() <= givenAreaMaxX)) &&
            ((givenPoint.getY() >= givenAreaMinY) && (givenPoint.getY() <= givenAreaMaxY))) {
            return true;
        }
        
        return false;
    }
    
    public static int GET_INITIAL_LINE_PRESSED_INDEX(RichText richText) {
        for (int i = 0; i < richText.getChildren().size(); i++) {
            zunayedhassan.SimpleRichTextFX.Line line = (zunayedhassan.SimpleRichTextFX.Line) richText.getChildren().get(i);
            
            if (line.isLinePressed) {
                return i;
            }
        }
        
        return -1;
    }
    
    public void SetSelectCharactersFrom(int begin, int end) {
        if ((begin <= this.GetTotalCharacters()) && (end <= this.GetTotalCharacters())) {
            for (int i = begin; i <= end; i++) {
                ((Alphabet) this.getChildren().get(i)).SetSelect(true);
            }
        }
    }
    
    public Boolean IsMouseSelectionTopToBottom(Point2D mouse) {
        Point2D mousePosition = new Point2D(mouse.getX(), mouse.getY());
        int initialMousePressedIndex = GET_INITIAL_LINE_PRESSED_INDEX(this._parent);
        
        for (int i = 0; i < this._parent.GetTotalLines(); i++) {
            zunayedhassan.SimpleRichTextFX.Line currentLine = (zunayedhassan.SimpleRichTextFX.Line) this._parent.getChildren().get(i);
            
            if (IS_WITHIN_GIVEN_AREA(mousePosition, currentLine)) {
                this._parent.currentSelectedLine = i;
                break;
            }
        }
        
        Boolean isTopToBottom = false;
        
        if (initialMousePressedIndex < this._parent.currentSelectedLine) {
            isTopToBottom = true;
        }
        else if (initialMousePressedIndex > this._parent.currentSelectedLine) {
            isTopToBottom = false;
        }
        else {
            isTopToBottom = null;
        }
        
        return isTopToBottom;
    }
    
    private void test(MouseEvent event) {
        Point2D mousePosition = new Point2D(event.getScreenX(), event.getScreenY());
        int initialMousePressedIndex = GET_INITIAL_LINE_PRESSED_INDEX(this._parent);
        
        Boolean isTopToBottom = this.IsMouseSelectionTopToBottom(mousePosition);
        
        this.isCurrentLineWithMouseOverFound = false;
        
        for (int i = 0; i < this._parent.GetTotalLines(); i++) {
            zunayedhassan.SimpleRichTextFX.Line currentLine = (zunayedhassan.SimpleRichTextFX.Line) this._parent.getChildren().get(i);
            
            if (IS_WITHIN_GIVEN_AREA(mousePosition, currentLine)) {
                for (int j = 0; j <= currentLine.GetTotalCharacters(); j++) {
                    Alphabet currentCharacter = (Alphabet) currentLine.getChildren().get(j);
                    
                    if (IS_WITHIN_GIVEN_AREA(mousePosition, currentCharacter)) {
                        isCurrentLineWithMouseOverFound = true;
                        
                        if (!currentCharacter.isMouseOver) {
                            currentCharacter.SetSelect(!currentCharacter.IsSelected());
                            currentCharacter.isMouseOver = true;
                        }
                    }
                    else {
                        currentCharacter.isMouseOver = false;
                    }
                }
            }
            
            if (isCurrentLineWithMouseOverFound) {
                break;
            }
        }
        
        int currentMouseOverIndex = -1;
        
        for (currentMouseOverIndex = 0; currentMouseOverIndex < this._parent.GetTotalLines(); currentMouseOverIndex++) {
            if (IS_WITHIN_GIVEN_AREA(mousePosition, (zunayedhassan.SimpleRichTextFX.Line) this._parent.getChildren().get(currentMouseOverIndex))) {
                break;
            }
        }
        
        if (isTopToBottom != null) {
            if (isTopToBottom) {
                for (int i = 0; i < this._parent.GetTotalLines(); i++) {
                    zunayedhassan.SimpleRichTextFX.Line currentLine = (zunayedhassan.SimpleRichTextFX.Line) this._parent.getChildren().get(i);
                    
                    if (!IS_WITHIN_GIVEN_AREA(mousePosition, currentLine)) {
                        if (i == initialMousePressedIndex) {
                            for (int j = currentLine.GetTotalCharacters(); j >= 0; j--) {
                                Alphabet character = (Alphabet) currentLine.getChildren().get(j);
                                
                                if (character.IsSelected()) {
                                    break;
                                }
                                
                                character.SetSelect(!character.IsSelected());
                                character.isMouseOver = true;
                            }
                        }
                        else if ((i >= initialMousePressedIndex) && (i <= currentMouseOverIndex)) {
                            currentLine.SelectAll();
                        }
                    }
                    else {
                        for (int j = 0; j <= currentLine.GetTotalCharacters(); j++) {
                            Alphabet character = (Alphabet) currentLine.getChildren().get(j);

                            if (character.IsSelected()) {
                                break;
                            }

                            character.SetSelect(true);
                            character.isMouseOver = true;
                        }
                    }
                }
            }
            else {
                for (int i = this._parent.GetTotalLines() - 1; i >= 0; i--) {
                    zunayedhassan.SimpleRichTextFX.Line currentLine = (zunayedhassan.SimpleRichTextFX.Line) this._parent.getChildren().get(i);
                    
                    if (!IS_WITHIN_GIVEN_AREA(mousePosition, currentLine)) {
                        if (i == initialMousePressedIndex) {
                            for (int j = 0; j <= currentLine.GetTotalCharacters(); j++) {
                                Alphabet character = (Alphabet) currentLine.getChildren().get(j);
                                
                                if (character.IsSelected()) {
                                    break;
                                }
                                
                                character.SetSelect(!character.IsSelected());
                                character.isMouseOver = true;
                            }
                        }
                        else if ((i <= initialMousePressedIndex) && (i >= currentMouseOverIndex)) {
                            currentLine.SelectAll();
                        }
                    }
                    else {
                        for (int j = currentLine.GetTotalCharacters(); j >= 0; j--) {
                            Alphabet character = (Alphabet) currentLine.getChildren().get(j);

                            if (character.IsSelected()) {
                                break;
                            }

                            character.SetSelect(true);
                            character.isMouseOver = true;
                        }
                    }
                }
            }
        }
        
        event.consume();
    }
    
    protected zunayedhassan.SimpleRichTextFX.Line getNewLine() {
        return new Line(this._parent);
    }
    
    public void SetLeftJustify() {
        this.setAlignment(Pos.BASELINE_LEFT);
    }
    
    public void SetCenterJustify() {
        this.setAlignment(Pos.BASELINE_CENTER);
    }
    
    public void SetRightJustify() {
        this.setAlignment(Pos.BASELINE_RIGHT);
    }
}
