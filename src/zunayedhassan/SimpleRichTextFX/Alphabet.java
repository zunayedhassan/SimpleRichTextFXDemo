/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zunayedhassan.SimpleRichTextFX;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 *
 * @author ZUNAYED_PC
 */
public class Alphabet extends HBox {
    public static int DEFAULT_FONT_SIZE = 12;
    
    private Text _character = new Text();
    private StackPane _characterRoom = new StackPane(this._character);
    private StackPane _caretRoom = new StackPane();
    private RichText _parent = null;
    private boolean _isBold = false;
    private boolean _isItalic = false;
    
    protected boolean isMouseClickedOn = false;
    protected boolean isMouseOver = false;
    
    public SimpleBooleanProperty SelectProperty = new SimpleBooleanProperty(false);
    
    public Alphabet(RichText parent) {
        this._parent = parent;
        this._character.setFont(Font.font(DEFAULT_FONT_SIZE));
        
        this.getChildren().addAll(
                this._characterRoom,
                this._caretRoom
        );
        
        // Event
        this.SelectProperty.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean isSelected) {
                if (isSelected) {
                    setBackground(new Background(new BackgroundFill(Color.CORNFLOWERBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
                }
                else {
                    setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
                }
            }
        });
        
        this.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() != MouseButton.SECONDARY) {
                    isMouseClickedOn = true;
                    _parent.DeselectAll();
                    _parent.ClearCaret();
                    SetCaret();
                }
            }
        });
        
        this._character.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                _updateSize(newValue);
            }
        });
    }
    
    public Alphabet(RichText parent, String character) {
        this(parent);
        this.SetCharacter(character);
        this._updateSize(character);
    }
    
    public Alphabet(RichText parent, String character, int fontSize) {
        this(parent);
        this.SetCharacter(character);
        this._updateSize(character);
        this._character.setFont(Font.font(fontSize));
    }
    
    public void SetCharacter(String character) {
        this._character.setText(character);
    }
    
    public void SetCaret() {
        this.ClearCaret();
        int size = (int) this._character.getFont().getSize();
        
        this._caretRoom.getChildren().add(new Caret((int) this._character.getFont().getSize()));
    }
    
    public void ClearCaret() {
        this._caretRoom.getChildren().clear();
    }
    
    public Caret GetCaret() {
        Caret caret = null;
        
        if (this._caretRoom.getChildren().size() > 0) {
            caret = (Caret) this._caretRoom.getChildren().get(0);
        }
        
        return caret;
    }
    
    public boolean IsCaretExists() {
        if (this.GetCaret() == null) {
            return false;
        }
        
        return true;
    }
    
    public String GetText() {
        return this._character.getText();
    }
    
    public void SetSelect(boolean isSelect) {
        this.SelectProperty.set(isSelect);
    }
    
    public boolean IsSelected() {
        return this.SelectProperty.get();
    }
    
    public void SetFontSize(int size) {
        String familyName = this._character.getFont().getFamily();
        this._character.setFont(Font.font(familyName, size));
    }
    
    public void SetFont(String fontfamily, int size) {
        this._character.setFont(Font.font(fontfamily, size));
    }
    
    public void SetBold(boolean isBold) {
        this._isBold = isBold;
        this._character.setFont(Font.font(this._character.getFont().getFamily(), this._isBold ? FontWeight.BOLD : FontWeight.NORMAL, this._isItalic ? FontPosture.ITALIC : FontPosture.REGULAR, this._character.getFont().getSize()));
    }
    
    public void SetItalic(boolean isItalic) {
        this._isItalic = isItalic;
        this._character.setFont(Font.font(this._character.getFont().getFamily(), this._isBold ? FontWeight.BOLD : FontWeight.NORMAL, this._isItalic ? FontPosture.ITALIC : FontPosture.REGULAR, this._character.getFont().getSize()));
    }
    
    public void SetUnderline(boolean isUnderline) {
        this._character.setUnderline(isUnderline);
    }
    
    public void SetStrikethrough(boolean isStrikethrough) {
        this._character.setStrikethrough(isStrikethrough);
    }
    
    public void SetColor(Color color) {
        this._character.setFill(color);
    }
    
    private void _updateSize(String newValue) {
        if (newValue.equals("")) {
            _characterRoom.setMinWidth(5);
        }
        else {
            _characterRoom.setMinWidth(0);
        }
    }
}
