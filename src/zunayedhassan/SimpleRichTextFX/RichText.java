/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zunayedhassan.SimpleRichTextFX;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javax.imageio.stream.FileImageInputStream;

/**
 *
 * @author ZUNAYED_PC
 */
public class RichText extends VBox {
    private Caret _caret = new Caret(12);
    private boolean _isCtrlKeyPressed = false;
    private boolean _isShiftKeyPressed = false;
    private Clipboard _clipboard = Clipboard.getSystemClipboard();
    private boolean _isSpellCheckOn = false;
    private ArrayList<String> _dictionary = new ArrayList<>();
    
    protected int currentSelectedLine = -1;
    
    protected MenuItem cutMenuItem = new MenuItem("Cut", this._getIcon("icons/edit-cut.png"));
    protected MenuItem copyMenuItem = new MenuItem("Copy", this._getIcon("icons/edit-copy.png"));
    protected MenuItem pasteMenuItem = new MenuItem("Paste", this._getIcon("icons/edit-paste.png"));
    protected MenuItem selectAllMenuItem = new MenuItem("Select All", this._getIcon("icons/edit-select-all.png"));
    
    protected ContextMenu contextMenu = this._getContextMenu();
    
    public String CurrentFontFamily = "System";
    public int CurrentFontSize = Alphabet.DEFAULT_FONT_SIZE;
    public boolean IsCurrentFontBold = false;
    public boolean IsCurrentFontItalic = false;
    public boolean IsUnderline = false;
    public boolean IsStrikethrough = false;
    public Color CurrentColor = Color.BLACK;
    
    public RichText() {
        this.AddLine();
        this.setCursor(Cursor.TEXT);
        
        // Events
        this.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.SECONDARY) {
                    _showContextMenu(event.getScreenX(), event.getScreenY());
                }
                else {
                    contextMenu.hide();
                }
                
                requestFocus();
                event.consume();
            }
        });
        
        this.cutMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ApplyCut();
            }
        });
        
        this.copyMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ApplyCopy();
            }
        });
        
        this.pasteMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ApplyPaste();
            }
        });
        
        this.selectAllMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ApplySelectAll();
            }
        });
        
        this.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {                
                // [ENTER]
                if (event.getCode() == KeyCode.ENTER) {
                    // If any line is already selected
                    if (IsAnythingSelected()) {
                        for (int i = 0; i < GetTotalLines(); i++) {
                            zunayedhassan.SimpleRichTextFX.Line line = (zunayedhassan.SimpleRichTextFX.Line) getChildren().get(i);
                            
                            ArrayList<Alphabet> selectedCharacters = new ArrayList<>();
                            ArrayList<Alphabet> restOfTheCharacters = new ArrayList<>();
                            
                            boolean isSelectedCharacterFoundAlready = false;
                            int newCaretPosition = 0;

                            for (int j = 0; j < line.getChildren().size(); j++) {
                                Alphabet character = (Alphabet) line.getChildren().get(j);
                                
                                if (character.IsSelected()) {
                                    if (!isSelectedCharacterFoundAlready) {
                                        newCaretPosition = j;
                                        isSelectedCharacterFoundAlready = true;
                                    }
                                    
                                    selectedCharacters.add(character);
                                }
                                else if (isSelectedCharacterFoundAlready) {
                                    restOfTheCharacters.add(character);
                                }
                            }
                            
                            if (selectedCharacters.size() > 0) {
                                for (Alphabet character : selectedCharacters) {
                                    line.getChildren().remove(character);
                                }
                                
                                ClearCaret();
                                InsertLineAt(i + 1);
                                
                                zunayedhassan.SimpleRichTextFX.Line newInsertedLine = (zunayedhassan.SimpleRichTextFX.Line) getChildren().get(i + 1);
                                
                                if (restOfTheCharacters.size() > 0) {
                                    for (Alphabet character : restOfTheCharacters) {
                                        newInsertedLine.getChildren().add(character);
                                    }
                                    
                                    ((Alphabet) newInsertedLine.getChildren().get(0)).SetCaret();
                                }
                            }
                        }
                    }
                    else {
                        boolean isCaretFound = false;

                        for (int i = 0; i < GetTotalLines(); i++) {
                            zunayedhassan.SimpleRichTextFX.Line line = (zunayedhassan.SimpleRichTextFX.Line) getChildren().get(i);

                            for (int j = 0; j < line.getChildren().size(); j++) {
                                Alphabet character = (Alphabet) line.getChildren().get(j);

                                if (character.IsCaretExists()) {
                                    isCaretFound = true;

                                    ArrayList<zunayedhassan.SimpleRichTextFX.Line> restOfTheLines = new ArrayList<>();

                                    if (i <= GetTotalLines() - 2) {
                                        for (int k = i + 1; k < GetTotalLines(); k++) {
                                            restOfTheLines.add((zunayedhassan.SimpleRichTextFX.Line) getChildren().get(k));
                                        }

                                        for (zunayedhassan.SimpleRichTextFX.Line currentLine : restOfTheLines) {
                                            getChildren().remove(currentLine);
                                        }
                                    }

                                    character.ClearCaret();
                                    AddLine();

                                    if (j < line.getChildren().size()) {
                                        ArrayList<Alphabet> restOfTheCharacters = new ArrayList<>();

                                        int start = j + 1;
                                        int end = line.getChildren().size();

                                        if (start < end) {
                                            for (int k = start; k < end; k++) {
                                                Alphabet currentCharacter = ((Alphabet) line.getChildren().get(k));
                                                restOfTheCharacters.add(currentCharacter);
                                            }

                                            for (Node characterFromPreviousLineNode : restOfTheCharacters) {
                                                line.getChildren().remove(characterFromPreviousLineNode);
                                            }

                                            zunayedhassan.SimpleRichTextFX.Line nextLine = (zunayedhassan.SimpleRichTextFX.Line) getChildren().get(i + 1);

                                            for (Node characterFromPreviousLineNode : restOfTheCharacters) {
                                                nextLine.getChildren().add(characterFromPreviousLineNode);
                                            }
                                        }
                                    }

                                    if (restOfTheLines.size() > 0) {
                                        for (zunayedhassan.SimpleRichTextFX.Line currentLine : restOfTheLines) {
                                            getChildren().add(currentLine);
                                        }
                                    }

                                    break;
                                }
                            }

                            if (isCaretFound) {
                                break;
                            }
                        }
                    }
                    
                    CheckForSpellingMistake();
                }
                // [BACKSPACE]
                else if (event.getCode() == KeyCode.BACK_SPACE) {                    
                    if (IsAnythingSelected()) {
                        _getClear();
                    }
                    else {
                        boolean isCaretFound = false;

                        for (int i = 0; i < GetTotalLines(); i++) {
                            zunayedhassan.SimpleRichTextFX.Line line = (zunayedhassan.SimpleRichTextFX.Line) getChildren().get(i);

                            for (int j = 0; j < line.getChildren().size(); j++) {
                                Alphabet character = (Alphabet) line.getChildren().get(j);

                                if (character.IsCaretExists()) {
                                    isCaretFound = true;

                                    if (j > 0) {
                                        line.getChildren().remove(j);
                                        ((Alphabet) line.getChildren().get(j - 1)).SetCaret();
                                    }
                                    else if (i > 0) {
                                        ArrayList<Alphabet> charactersFromPreviousLine = new ArrayList<>();

                                        if (line.GetTotalCharacters() > 0) {
                                            for (int k = 1; k <= line.GetTotalCharacters(); k++) {
                                                charactersFromPreviousLine.add((Alphabet) line.getChildren().get(k));
                                            }

                                            line.getChildren().clear();
                                        }

                                        getChildren().remove(i);

                                        zunayedhassan.SimpleRichTextFX.Line previousLine = (zunayedhassan.SimpleRichTextFX.Line) getChildren().get(i - 1);
                                        
                                        if (previousLine.getChildren().size() - 1 != -1) {
                                            ((Alphabet) previousLine.getChildren().get(previousLine.getChildren().size() - 1)).SetCaret();
                                        }
                                        else {
                                            ((Alphabet) previousLine.getChildren().get(0)).SetCaret();
                                        }

                                        if (charactersFromPreviousLine.size() > 0) {
                                            for (Alphabet characterFromPreviousLine : charactersFromPreviousLine) {
                                                previousLine.getChildren().add(characterFromPreviousLine);
                                            }
                                        }
                                    }

                                    break;
                                }
                            }
                            
                            CheckForSpellingMistake(i);

                            if (isCaretFound) {
                                break;
                            }
                        }
                    }
                }
                // [LEFT]
                else if (!_isCtrlKeyPressed && (event.getCode() == KeyCode.LEFT)) {
                    if (!_isShiftKeyPressed) {
                        DeselectAll();
                    }
                    
                    boolean isCaretFound = false;

                    for (int i = 0; i < GetTotalLines(); i++) {
                        zunayedhassan.SimpleRichTextFX.Line line = (zunayedhassan.SimpleRichTextFX.Line) getChildren().get(i);
                        
                        for (int j = 0; j < line.getChildren().size(); j++) {
                            Alphabet character = (Alphabet) line.getChildren().get(j);
                            
                            if (character.IsCaretExists()) {
                                isCaretFound = true;
                                
                                if (j > 0) {           
                                    ((Alphabet) line.getChildren().get(j)).ClearCaret();
                                    ((Alphabet) line.getChildren().get(j - 1)).SetCaret();
                                }
                                else if (i > 0) {
                                    ((Alphabet) line.getChildren().get(j)).ClearCaret();
                                    
                                    zunayedhassan.SimpleRichTextFX.Line previousLine = (zunayedhassan.SimpleRichTextFX.Line) getChildren().get(i - 1);
                                    ((Alphabet) previousLine.getChildren().get(previousLine.getChildren().size() - 1)).SetCaret();
                                }
                                
                                // [Shift] + [Left]
                                if (_isShiftKeyPressed && (j > 0)) {
                                    ((Alphabet) line.getChildren().get(j)).SetSelect(!((Alphabet) line.getChildren().get(j)).IsSelected());
                                }
                                
                                break;
                            }
                        }
                        
                        if (isCaretFound) {
                            break;
                        }
                    }
                }
                // [RIGHT]
                else if (!_isCtrlKeyPressed && (event.getCode() == KeyCode.RIGHT)) {
                    if (!_isShiftKeyPressed) {
                        DeselectAll();
                    }
                    
                    boolean isCaretFound = false;

                    for (int i = 0; i < GetTotalLines(); i++) {
                        zunayedhassan.SimpleRichTextFX.Line line = (zunayedhassan.SimpleRichTextFX.Line) getChildren().get(i);
                        
                        for (int j = 0; j < line.getChildren().size(); j++) {
                            Alphabet character = (Alphabet) line.getChildren().get(j);
                            
                            if (character.IsCaretExists()) {
                                isCaretFound = true;

                                if ((j == line.getChildren().size() - 1) && (i < GetTotalLines() - 1)) {
                                    ((Alphabet) line.getChildren().get(j)).ClearCaret();
                                    
                                    zunayedhassan.SimpleRichTextFX.Line nextLine = (zunayedhassan.SimpleRichTextFX.Line) getChildren().get(i + 1);
                                    ((Alphabet) nextLine.getChildren().get(0)).SetCaret();
                                }
                                else if (j < line.getChildren().size() - 1) {           
                                    ((Alphabet) line.getChildren().get(j)).ClearCaret();
                                    ((Alphabet) line.getChildren().get(j + 1)).SetCaret();
                                }
                                
                                // [Shift] + [Right]
                                if (_isShiftKeyPressed) {
                                    if (j + 1 != line.getChildren().size()) {
                                        ((Alphabet) line.getChildren().get(j + 1)).SetSelect(!((Alphabet) line.getChildren().get(j + 1)).IsSelected());
                                    }
                                }
                                
                                break;
                            }
                        }
                        
                        if (isCaretFound) {
                            break;
                        }
                    }
                }
                // [UP]
                else if (event.getCode() == KeyCode.UP) {
                    if (!_isShiftKeyPressed) {
                        DeselectAll();
                    }
                    
                    boolean isCaretFound = false;
                    
                    for (int i = 0; i < GetTotalLines(); i++) {
                        zunayedhassan.SimpleRichTextFX.Line line = (zunayedhassan.SimpleRichTextFX.Line) getChildren().get(i);
                        
                        for (int j = 0; j < line.getChildren().size(); j++) {
                            Alphabet character = (Alphabet) line.getChildren().get(j);
                            
                            if (character.IsCaretExists()) {
                                isCaretFound = true;
                                
                                int caretPosition = j;
                                int initialCaretPosition = j;

                                if (i > 0) {
                                    zunayedhassan.SimpleRichTextFX.Line previousLine = (zunayedhassan.SimpleRichTextFX.Line) getChildren().get(i - 1);
                                    character.ClearCaret();
                                    
                                    if (previousLine.getChildren().size() - 1 >= caretPosition) {
                                        ((Alphabet) previousLine.getChildren().get(caretPosition)).SetCaret();
                                    }
                                    else {
                                        caretPosition = previousLine.getChildren().size() - 1;
                                        ((Alphabet) previousLine.getChildren().get(caretPosition)).SetCaret();
                                    }
                                    
                                    // [SHIFT] + [UP]
                                    if (_isShiftKeyPressed) {
                                        if (line.GetTotalCharacters() > 0) {
                                            for (int k = 0; k < initialCaretPosition; k++) {
                                                Alphabet choosenCharacter = ((Alphabet) line.getChildren().get(k + 1));
                                                choosenCharacter.SetSelect(!choosenCharacter.IsSelected());
                                            }
                                        }
                                        
                                        if (previousLine.GetTotalCharacters() > 0) {
                                            int k = caretPosition;
                                            
                                            for (k = previousLine.GetTotalCharacters(); k >= caretPosition; k--) {
                                                Alphabet choosenCharacter = ((Alphabet) previousLine.getChildren().get(k));
                                                choosenCharacter.SetSelect(!choosenCharacter.IsSelected());
                                            }
                                            
                                            ((Alphabet) previousLine.getChildren().get(k + 1)).SetSelect(!((Alphabet) previousLine.getChildren().get(k + 1)).IsSelected());

                                        }
                                    }
                                }
                                else if (i == 0) {
                                    // [SHIFT] + [UP]
                                    if (_isShiftKeyPressed) {
                                        Alphabet choosenCharacter = null;
                                        
                                        for (int k = 0; k <= initialCaretPosition; k++) {
                                            choosenCharacter = ((Alphabet) line.getChildren().get(k));
                                            choosenCharacter.SetSelect(!choosenCharacter.IsSelected());
                                            choosenCharacter.ClearCaret();
                                        }
                                        
                                        ((Alphabet) line.getChildren().get(0)).SetCaret();
                                    }
                                }
                                
                                break;
                            }
                        }
                        
                        if (isCaretFound) {
                            break;
                        }
                    }
                }
                // [Down]
                else if (event.getCode() == KeyCode.DOWN) {
                    if (!_isShiftKeyPressed) {
                        DeselectAll();
                    }
                    
                    boolean isCaretFound = false;
                    
                    for (int i = 0; i < GetTotalLines(); i++) {
                        zunayedhassan.SimpleRichTextFX.Line line = (zunayedhassan.SimpleRichTextFX.Line) getChildren().get(i);
                        
                        for (int j = 0; j < line.getChildren().size(); j++) {
                            Alphabet character = (Alphabet) line.getChildren().get(j);
                            
                            if (character.IsCaretExists()) {
                                isCaretFound = true;

                                int caretPosition = j;
                                int initialCaretPosition = j;
                                
                                if (i < GetTotalLines() - 1) {
                                    zunayedhassan.SimpleRichTextFX.Line nextLine = (zunayedhassan.SimpleRichTextFX.Line) getChildren().get(i + 1);
                                    character.ClearCaret();
                                    
                                    if (nextLine.getChildren().size() >= caretPosition) {
                                        if ((caretPosition < line.GetTotalCharacters() + 1) && (caretPosition != line.getChildren().size())) {
                                            ((Alphabet) nextLine.getChildren().get(caretPosition)).SetCaret();
                                        }
                                    }
                                    else {
                                        ((Alphabet) nextLine.getChildren().get(nextLine.getChildren().size() - 1)).SetCaret();
                                    }
                                    
                                    // [SHIFT] + [DOWN]
                                    if (_isShiftKeyPressed) {
                                        for (int k = initialCaretPosition; k <= line.GetTotalCharacters(); k++) {
                                            if (k < line.getChildren().size() - 1) {
                                                Alphabet choosenCharacter = ((Alphabet) line.getChildren().get(k + 1));
                                                choosenCharacter.SetSelect(!choosenCharacter.IsSelected());
                                            }
                                        }
                                        
                                        if (nextLine.GetTotalCharacters() > 0) {
                                            for (int k = 1; k <= caretPosition; k++) {
                                                if (k < nextLine.getChildren().size()) {
                                                    Alphabet choosenCharacter = ((Alphabet) nextLine.getChildren().get(k));
                                                    choosenCharacter.SetSelect(!choosenCharacter.IsSelected());
                                                }
                                            }
                                        }
                                    }
                                }
                                else if (i == GetTotalLines() - 1) {     
                                    // [SHIFT] + [DOWN]
                                    if (_isShiftKeyPressed) {
                                        Alphabet choosenCharacter = null;
                                        
                                        ((Alphabet) line.getChildren().get(initialCaretPosition)).ClearCaret();
                                        
                                        for (int k = initialCaretPosition; k <= line.GetTotalCharacters(); k++) {
                                            if (k < line.getChildren().size() - 1) {
                                                choosenCharacter = ((Alphabet) line.getChildren().get(k + 1));
                                                choosenCharacter.SetSelect(!choosenCharacter.IsSelected());
                                            }
                                        }
                                        
                                        if (choosenCharacter != null) {
                                            choosenCharacter.SetCaret();
                                        }
                                        else if (j == line.GetTotalCharacters()) {
                                            ((Alphabet) line.getChildren().get(j)).SetCaret();
                                        }
                                    }
                                }
                                
                                break;
                            }
                        }
                        
                        if (isCaretFound) {
                            break;
                        }
                    }
                }
                // [Home]
                else if (event.getCode() == KeyCode.HOME) {
                    DeselectAll();
                    
                    boolean isCaretFound = false;
                    
                    for (int i = 0; i < GetTotalLines(); i++) {
                        zunayedhassan.SimpleRichTextFX.Line line = (zunayedhassan.SimpleRichTextFX.Line) getChildren().get(i);
                        
                        for (int j = 0; j < line.getChildren().size(); j++) {
                            Alphabet character = (Alphabet) line.getChildren().get(j);
                            
                            if (character.IsCaretExists()) {
                                isCaretFound = true;

                                character.ClearCaret();
                                ((Alphabet) line.getChildren().get(0)).SetCaret();
                                
                                break;
                            }
                        }
                        
                        if (isCaretFound) {
                            break;
                        }
                    }
                }
                // [END]
                else if (event.getCode() == KeyCode.END) {
                    DeselectAll();
                    
                    boolean isCaretFound = false;
                    
                    for (int i = 0; i < GetTotalLines(); i++) {
                        zunayedhassan.SimpleRichTextFX.Line line = (zunayedhassan.SimpleRichTextFX.Line) getChildren().get(i);
                        
                        for (int j = 0; j < line.getChildren().size(); j++) {
                            Alphabet character = (Alphabet) line.getChildren().get(j);
                            
                            if (character.IsCaretExists()) {
                                isCaretFound = true;

                                character.ClearCaret();
                                ((Alphabet) line.getChildren().get(line.GetTotalCharacters())).SetCaret();
                                
                                break;
                            }
                        }
                        
                        if (isCaretFound) {
                            break;
                        }
                    }
                }
                // [DELETE]
                else if (event.getCode() == KeyCode.DELETE) {
                    if (IsAnythingSelected()) {
                        _getClear();
                    }
                    else {
                        boolean isCaretFound = false;
                    
                        for (int i = 0; i < GetTotalLines(); i++) {
                            zunayedhassan.SimpleRichTextFX.Line line = (zunayedhassan.SimpleRichTextFX.Line) getChildren().get(i);

                            for (int j = 0; j < line.getChildren().size(); j++) {
                                Alphabet character = (Alphabet) line.getChildren().get(j);

                                if (character.IsCaretExists()) {
                                    isCaretFound = true;

                                    int caretPosition = j;

                                    if ((caretPosition >= 0) && (caretPosition < line.GetTotalCharacters())) {
                                        character.ClearCaret();

                                        Alphabet nextCharacter = (Alphabet) line.getChildren().get(caretPosition + 1);                     
                                        line.getChildren().remove(nextCharacter);

                                        Alphabet previousCharacter = (Alphabet) line.getChildren().get(caretPosition);
                                        previousCharacter.SetCaret();
                                    }
                                    else if ((caretPosition == line.GetTotalCharacters()) && (i < GetTotalLines()) && (i != 0) && (line.GetTotalCharacters() > 0)) {
                                        zunayedhassan.SimpleRichTextFX.Line nextLine = (zunayedhassan.SimpleRichTextFX.Line) getChildren().get(i + 1);
                                        String text = nextLine.GetText(0, nextLine.GetTotalCharacters() - 1);

                                        getChildren().remove(nextLine);

                                        for (int k = 0; k < text.length(); k++) {
                                            String currentCharacter = Character.toString(text.charAt(k));
                                            _addText(currentCharacter, false);
                                        }

                                        ((Alphabet) line.getChildren().get(line.getChildren().size() - 1)).ClearCaret();
                                        ((Alphabet) line.getChildren().get(caretPosition)).SetCaret();
                                    }

                                    break;
                                }
                            }

                            if (isCaretFound) {
                                break;
                            }
                        }
                    }
                }
                // [Ctrl]
                else if (event.getCode() == KeyCode.CONTROL) {
                    _isCtrlKeyPressed = true;
                }
                // [SHIFT]
                else if (event.getCode() == KeyCode.SHIFT) {
                    _isShiftKeyPressed = true;
                }
                // [Ctrl] + [LEFT]
                else if (_isCtrlKeyPressed && (event.getCode() == KeyCode.LEFT)) {
                    boolean isCaretFound = false;
                    
                    for (int i = 0; i < GetTotalLines(); i++) {
                        zunayedhassan.SimpleRichTextFX.Line line = (zunayedhassan.SimpleRichTextFX.Line) getChildren().get(i);
                        
                        for (int j = 0; j < line.getChildren().size(); j++) {
                            Alphabet character = (Alphabet) line.getChildren().get(j);
                            
                            if (character.IsCaretExists()) {
                                isCaretFound = true;
                                
                                int caretPosition = j;
                                int initialCaretPosition = j;
                                
                                if (caretPosition > 0) {
                                    character.ClearCaret();
                                    
                                    String text = line.GetText(0, caretPosition - 1);
                                    
                                    if (text.length() > 0) {
                                        boolean isWhiteSpaceFound = ((text.charAt(text.length() - 1) == ' ') || (text.charAt(text.length() - 1) == '\t')) ? true : false;

                                        if (!isWhiteSpaceFound) {
                                            for (int k = text.length() - 1; k >= 0; k--) {
                                                if (!(text.charAt(k) == ' ') && !(text.charAt(k) == '\t')) {
                                                    caretPosition--;
                                                }
                                                else {
                                                    break;
                                                }
                                            }
                                        }
                                        else {
                                            for (int k = text.length() - 1; k >= 0; k--) {
                                                if ((text.charAt(k) == ' ') || (text.charAt(k) == '\t')) {
                                                    caretPosition--;
                                                }
                                                else {
                                                    break;
                                                }
                                            }
                                        }
                                        
                                        ((Alphabet) line.getChildren().get(caretPosition)).SetCaret();
                                        
                                        // [Ctrl] + [Shift] + [Left]
                                        if (_isShiftKeyPressed) {
                                            int k = 0;
                                            
                                            for (k = caretPosition; k <= initialCaretPosition; k++) {
                                                ((Alphabet) line.getChildren().get(k)).SetSelect(!((Alphabet) line.getChildren().get(k)).IsSelected());
                                            }
                                            
                                            ((Alphabet) line.getChildren().get(caretPosition)).SetSelect(!((Alphabet) line.getChildren().get(caretPosition)).IsSelected());
                                            
                                            if (caretPosition == 0) {
                                                if (i > 0) {
                                                    ((Alphabet) line.getChildren().get(caretPosition)).ClearCaret();
                                                    
                                                    zunayedhassan.SimpleRichTextFX.Line previousLine = (zunayedhassan.SimpleRichTextFX.Line) getChildren().get(i - 1);
                                                    ((Alphabet) previousLine.getChildren().get(previousLine.GetTotalCharacters())).SetCaret();
                                                }
                                            }
                                        }
                                    }
                                }
                                else if (i > 0) {
                                    ((Alphabet) line.getChildren().get(0)).ClearCaret();
                                    
                                    zunayedhassan.SimpleRichTextFX.Line previousLine = (zunayedhassan.SimpleRichTextFX.Line) getChildren().get(i - 1);
                                    ((Alphabet) previousLine.getChildren().get(previousLine.GetTotalCharacters())).SetCaret();
                                }
                                
                                break;
                            }
                        }
                        
                        if (isCaretFound) {
                            break;
                        }
                    }
                }
                // [Ctrl] + [RIGHT]
                else if (_isCtrlKeyPressed && (event.getCode() == KeyCode.RIGHT)) {
                    boolean isCaretFound = false;
                    
                    for (int i = 0; i < GetTotalLines(); i++) {
                        zunayedhassan.SimpleRichTextFX.Line line = (zunayedhassan.SimpleRichTextFX.Line) getChildren().get(i);
                        
                        for (int j = 0; j < line.getChildren().size(); j++) {
                            Alphabet character = (Alphabet) line.getChildren().get(j);
                            
                            if (character.IsCaretExists()) {
                                isCaretFound = true;
                                
                                int caretPosition = j;
                                int initialCaretPosition = j;

                                ((Alphabet) line.getChildren().get(caretPosition)).ClearCaret();
                                
                                if (caretPosition == line.GetTotalCharacters()) {
                                    if (i == GetTotalLines() - 1) {
                                        ((Alphabet) line.getChildren().get(caretPosition)).SetCaret();
                                        break;
                                    }
                                    else {
                                        ((Alphabet) ((zunayedhassan.SimpleRichTextFX.Line) getChildren().get(i + 1)).getChildren().get(0)).SetCaret();
                                    }
                                }
                                
                                if ((caretPosition >= 0) && (caretPosition < line.GetTotalCharacters())) {
                                    String text = line.GetText(caretPosition, line.GetTotalCharacters() - 1);
                                    
                                    boolean isWhiteSpaceFound = false;
                                    
                                    if ((text.charAt(0) == ' ') || (text.charAt(0) == '\t')) {
                                        isWhiteSpaceFound = true;
                                    }
                                    
                                    if (isWhiteSpaceFound) {
                                        for (int k = 0; k < text.length(); k++) {
                                            if ((text.charAt(k) == ' ') || (text.charAt(k) == '\t')) {
                                                caretPosition++;
                                            }
                                            else {
                                                break;
                                            }
                                        }
                                    }
                                    else {
                                        if (caretPosition == 0) {
                                            caretPosition++;
                                        }
                                        
                                        if (text.split(" ") != null) {
                                            caretPosition += text.split(" ")[0].length();
                                        }
                                        else {
                                            caretPosition += text.length();
                                        }
                                    }
                                    
                                    if (initialCaretPosition == 0) {
                                        caretPosition--;
                                    }
                                    
                                    Alphabet previousCharacterWithCaret = ((Alphabet) line.getChildren().get(caretPosition));
                                    previousCharacterWithCaret.SetCaret();
                                    
                                    // [Ctrl] + [Shift] + [Right]
                                    if (_isShiftKeyPressed) {
                                        if (caretPosition <= line.GetTotalCharacters()) {
                                            for (int k = initialCaretPosition; k < caretPosition; k++) {
                                                ((Alphabet) line.getChildren().get(k + 1)).SetSelect(!((Alphabet) line.getChildren().get(k + 1)).IsSelected());
                                            }
                                            
                                            if (caretPosition == line.GetTotalCharacters()) {
                                                if (i < GetTotalLines() - 1) {
                                                    previousCharacterWithCaret.ClearCaret();

                                                    zunayedhassan.SimpleRichTextFX.Line nextLine = (zunayedhassan.SimpleRichTextFX.Line) getChildren().get(i + 1);
                                                    ((Alphabet) nextLine.getChildren().get(0)).SetCaret();
                                                }
                                            }
                                        }
                                    }
                                }
                                
                                break;
                            }
                        }
                        
                        if (isCaretFound) {
                            break;
                        }
                    }
                }
                // [Ctrl] + [A]
                else if (_isCtrlKeyPressed && (event.getCode() == KeyCode.A)) {
                    ApplySelectAll();
                }
                // [Ctrl] + [C]
                else if (_isCtrlKeyPressed && (event.getCode() == KeyCode.C)) {
                    ApplyCopy();
                }
                // [Ctrl] + [V]
                else if (_isCtrlKeyPressed && (event.getCode() == KeyCode.V)) {
                    ApplyPaste();
                }
                // [Ctrl] + [X]
                else if (_isCtrlKeyPressed && (event.getCode() == KeyCode.X)) {
                    ApplyCut();
                }
                // For any other characters
                else {    
                    String character = event.getText();
                    
                    if (!event.getText().equals("\r") || !event.getText().equals(" ")) {
                        if (_isShiftKeyPressed) {
                            if (character.equals("`")) {
                                character = "~";
                            }
                            else if (character.equals("1")) {
                                character = "!";
                            }
                            else if (character.equals("2")) {
                                character = "@";
                            }
                            else if (character.equals("3")) {
                                character = "#";
                            }
                            else if (character.equals("4")) {
                                character = "$";
                            }
                            else if (character.equals("5")) {
                                character = "%";
                            }
                            else if (character.equals("6")) {
                                character = "^";
                            }
                            else if (character.equals("7")) {
                                character = "^";
                            }
                            else if (character.equals("8")) {
                                character = "*";
                            }
                            else if (character.equals("9")) {
                                character = "(";
                            }
                            else if (character.equals("0")) {
                                character = ")";
                            }
                            else if (character.equals("-")) {
                                character = "_";
                            }
                            else if (character.equals("=")) {
                                character = "+";
                            }
                            else if (character.equals("[")) {
                                character = "{";
                            }
                            else if (character.equals("]")) {
                                character = "}";
                            }
                            else if (character.equals(";")) {
                                character = ":";
                            }
                            else if (character.equals("'")) {
                                character = "\"";
                            }
                            else if (character.equals("\\")) {
                                character = "|";
                            }
                            else if (character.equals(",")) {
                                character = "<";
                            }
                            else if (character.equals(".")) {
                                character = ">";
                            }
                            else if (character.equals("/")) {
                                character = "?";
                            }
                            
                            _addText(character.toUpperCase(), false);
                        }
                        else {
                            _addText(character, false);
                        }
                    }
                }
                        
                event.consume();
            }
        });
        
        this.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.CONTROL) {
                    _isCtrlKeyPressed = false;
                }
                else if (event.getCode() == KeyCode.SHIFT) {
                    _isShiftKeyPressed = false;
                }
            }
        });
    }

    private void _addText(String text, boolean isAddToTheLastLine) {
        if (!isAddToTheLastLine) {
            boolean isCaretFound = false;

            for (int i = 0; i < GetTotalLines(); i++) {
                zunayedhassan.SimpleRichTextFX.Line line = (zunayedhassan.SimpleRichTextFX.Line) getChildren().get(i);

                for (int j = 0; j < line.getChildren().size(); j++) {
                    Alphabet character = (Alphabet) line.getChildren().get(j);

                    if (character.IsCaretExists()) {
                        isCaretFound = true;

                        ((Alphabet) line.getChildren().get(j)).ClearCaret();

                        int start = j + 1;
                        int end = line.getChildren().size() - 1;

                        ArrayList<Alphabet> tempText = new ArrayList<>();

                        if (start < end) {
                            for (int k = start; k <= end; k++) {
                                tempText.add((Alphabet) line.getChildren().get(k));
                            }

                            for (Alphabet onwardCharacter : tempText) {
                                line.getChildren().remove(onwardCharacter);
                            }
                        }

                        Alphabet newCharacter = new Alphabet(this, text);
                        newCharacter.SetCaret();
                        newCharacter.SetFont(CurrentFontFamily, CurrentFontSize);
                        newCharacter.SetBold(IsCurrentFontBold);
                        newCharacter.SetItalic(IsCurrentFontItalic);
                        newCharacter.SetUnderline(IsUnderline);
                        newCharacter.SetStrikethrough(IsStrikethrough);
                        line.getChildren().add(newCharacter);

                        for (Alphabet onwardCharacter : tempText) {
                            line.getChildren().add(onwardCharacter);
                        }
                        
                        this.CheckForSpellingMistake(i);

                        break;
                    }
                }

                if (isCaretFound) {
                    break;
                }
            }
        }
        else {
            zunayedhassan.SimpleRichTextFX.Line lastLine = (zunayedhassan.SimpleRichTextFX.Line) this.getChildren().get(this.getChildren().size() - 1);
            
            if (lastLine != null) {
                lastLine.getChildren().add(new Alphabet(this, text));
            }
            
            this.CheckForSpellingMistake(this.getChildren().size() - 1);
        }
    }
    
    public String GetSelectedText() {
        String selectedText = "";
        
        for (int i = 0; i < GetTotalLines(); i++) {
            zunayedhassan.SimpleRichTextFX.Line line = (zunayedhassan.SimpleRichTextFX.Line) getChildren().get(i);

            for (int j = 0; j < line.getChildren().size(); j++) {
                Alphabet character = (Alphabet) line.getChildren().get(j);
                
                if (character.IsSelected()) {
                    selectedText += character.GetText();
                }
            }
            
            selectedText += "\n";
        }
        
        return selectedText;
    }
    
    public void ApplyCut() {
        String cutText = _getClear();
                
        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(cutText);
        _clipboard.setContent(clipboardContent);
    }
    
    public void ApplyCopy() {
        if (IsAnythingSelected()) {
            String text = GetSelectedText();
            ClipboardContent content = new ClipboardContent();
            content.putString(text);
            _clipboard.setContent(content);
        }
    }
    
    public void ApplyPaste() {
        String text = _clipboard.getString();
                    
        if (text.length() > 0) {
            boolean isNewLineExists = false;

            for (int i = 0; i < text.length(); i++) {
                String currentCharacter = Character.toString(text.charAt(i));

                if (currentCharacter.equals("\n") || currentCharacter.equals("\r")) {
                    isNewLineExists = true;
                    break;
                }
            }

            if (!isNewLineExists) {
                for (int i = 0; i < text.length(); i++) {
                    String character = Character.toString(text.charAt(i));

                    if (!(character.equals("\n") || character.equals("\r"))) {
                        _addText(character, false);
                    }
                }
            }
            else {
                boolean isNewLineAdded = false;

                for (int i = 0; i < GetTotalLines(); i++) {
                    zunayedhassan.SimpleRichTextFX.Line line = (zunayedhassan.SimpleRichTextFX.Line) getChildren().get(i);

                    for (int j = 0; j < line.getChildren().size(); j++) {
                        Alphabet character = (Alphabet) line.getChildren().get(j);

                        if (character.GetCaret() != null) {
                            InsertLineAt(i + 1);

                            if (j < line.GetTotalCharacters()) {
                                InsertLineAt(i + 2);

                                ArrayList<Alphabet> restOfTheCharacters = new ArrayList<>();

                                for (int k = j + 1; k <= line.GetTotalCharacters(); k++) {
                                    restOfTheCharacters.add((Alphabet) line.getChildren().get(k));
                                }

                                for (Alphabet restOfTheCharacter : restOfTheCharacters) {
                                    line.getChildren().remove(restOfTheCharacter);
                                }

                                for (Alphabet restOfTheCharacter : restOfTheCharacters) {
                                    ((zunayedhassan.SimpleRichTextFX.Line) getChildren().get(i + 2)).getChildren().add(restOfTheCharacter);
                                }
                            }

                            int currentLineIndex = i;

                            for (int textIndex = 0; textIndex < text.length(); textIndex++) {
                                String characterFromText = Character.toString(text.charAt(textIndex));

                                if (!characterFromText.equals("\n") && !characterFromText.equals("\r")) {
                                    ((zunayedhassan.SimpleRichTextFX.Line) getChildren().get(currentLineIndex)).getChildren().add(_getNewCharacter(characterFromText));
                                }
                                else {
                                    InsertLineAt(++currentLineIndex);
                                }
                            }

                            ClearCaret();

                            zunayedhassan.SimpleRichTextFX.Line lastLineFromClipboardText = ((zunayedhassan.SimpleRichTextFX.Line) getChildren().get(currentLineIndex));
                            ((Alphabet) lastLineFromClipboardText.getChildren().get(lastLineFromClipboardText.GetTotalCharacters())).SetCaret();

                            isNewLineAdded = true;

                            break;
                        }
                    }

                    if (isNewLineAdded) {
                        break;
                    }
                }
            }
        }
    }
    
    public void ApplySelectAll() {
        for (int i = 0; i < GetTotalLines(); i++) {
            zunayedhassan.SimpleRichTextFX.Line line = (zunayedhassan.SimpleRichTextFX.Line) getChildren().get(i);

            for (int j = 0; j < line.getChildren().size(); j++) {
                Alphabet character = (Alphabet) line.getChildren().get(j);
                character.SetSelect(true);
            }
        }
    }
    
    public void AddText(String text, int fontSize) {
        int index = ((zunayedhassan.SimpleRichTextFX.Line) this.getChildren().get(this.getChildren().size() - 1)).getChildren().size() - 1;
        
        for (int i = 0; i < text.length(); i++) {
            this._addText(Character.toString(text.charAt(i)), true);
        }
        
        for (int i = index; i <= (index + text.length()); i++) {
            ((Alphabet) this.GetLines().get(this.GetLines().size() - 1).getChildren().get(i)).SetFontSize(fontSize);
        }
        
        this.ClearCaret(); 
        zunayedhassan.SimpleRichTextFX.Line lastLine = (zunayedhassan.SimpleRichTextFX.Line) this.getChildren().get(this.GetTotalLines() - 1);
        ((Alphabet) lastLine.getChildren().get(lastLine.GetTotalCharacters())).SetCaret();
    }

    public void RemoveCaret() {
        boolean isCaretCleared = false;
        
        for (Node lineNode : this.getChildren()) {
            for (Node alphabetNode : ((zunayedhassan.SimpleRichTextFX.Line) lineNode).getChildren()) {
                Alphabet character = (Alphabet) alphabetNode;

                if (character.GetCaret() instanceof zunayedhassan.SimpleRichTextFX.Caret) {
                    character.ClearCaret();
                    isCaretCleared = true;
                    break;
                }
            }

            if (isCaretCleared) {
                break;
            }
        }
    }
    
    public void AddLine() {
        Line line = new Line(this);
        this.getChildren().add(line);
    }
    
    public void InsertLineAt(int index) {
        if (index <= this.GetTotalLines()) {
            ArrayList<zunayedhassan.SimpleRichTextFX.Line> restOfTheLines = new ArrayList<>();
            
            for (int i = 0; i < this.GetTotalLines(); i++) {
                if (i >= index) {
                    zunayedhassan.SimpleRichTextFX.Line line = (zunayedhassan.SimpleRichTextFX.Line) this.getChildren().get(i);
                    restOfTheLines.add(line);
                }
            }
            
            if (restOfTheLines.size() > 0) {
                for (zunayedhassan.SimpleRichTextFX.Line line : restOfTheLines) {
                    this.getChildren().remove(line);
                }
            }
            
            AddLine();
            
            if (restOfTheLines.size() > 0) {
                for (zunayedhassan.SimpleRichTextFX.Line line : restOfTheLines) {
                    this.getChildren().add(line);
                }
            }
        } 
    }
    
    public ArrayList<zunayedhassan.SimpleRichTextFX.Line> GetLines() {
        ArrayList<zunayedhassan.SimpleRichTextFX.Line> lines = new ArrayList<>();
        
        for (Node lineNode : this.getChildren()) {
            lines.add((zunayedhassan.SimpleRichTextFX.Line) lineNode);
        }
        
        return lines;
    }
    
    public int GetTotalLines() {
        return this.getChildren().size();
    }
    
    public void DeselectAll() {
        for (Node lineNode : this.getChildren()) {
            for (Node alphabetNode : ((zunayedhassan.SimpleRichTextFX.Line) lineNode).getChildren()) {
                Alphabet character = (Alphabet) alphabetNode;
                character.SetSelect(false);
                character.isMouseOver = false;
            }
        }
    }
    
    public void ClearCaret() {
        for (int i = 0; i < GetTotalLines(); i++) {
            zunayedhassan.SimpleRichTextFX.Line line = (zunayedhassan.SimpleRichTextFX.Line) getChildren().get(i);

            for (int j = 0; j < line.getChildren().size(); j++) {
                Alphabet character = (Alphabet) line.getChildren().get(j);

                if (character.IsCaretExists()) {
                    character.ClearCaret();
                }
            }
        }
    }
    
    public boolean IsAnythingSelected() {
        for (int i = 0; i < GetTotalLines(); i++) {
            zunayedhassan.SimpleRichTextFX.Line line = (zunayedhassan.SimpleRichTextFX.Line) getChildren().get(i);

            for (int j = 0; j < line.getChildren().size(); j++) {
                Alphabet character = (Alphabet) line.getChildren().get(j);
                
                if (character.IsSelected()) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    public void SetFont(String fontFamily) {
        this.CurrentFontFamily = fontFamily;
        
        if (this.IsAnythingSelected()) {
            for (int i = 0; i < GetTotalLines(); i++) {
                zunayedhassan.SimpleRichTextFX.Line line = (zunayedhassan.SimpleRichTextFX.Line) getChildren().get(i);

                for (int j = 0; j < line.getChildren().size(); j++) {
                    Alphabet character = (Alphabet) line.getChildren().get(j);
                    
                    if (character.IsSelected()) {
                        character.SetFont(fontFamily, this.CurrentFontSize);
                    }
                }
            }
        }
    }
    
    public void SetFontInLine(int lineNumber, int startCharacter, int endCharacter, String fontFamily) {
        lineNumber--;
        
        if (lineNumber < this.GetTotalLines()) {
            zunayedhassan.SimpleRichTextFX.Line line = (zunayedhassan.SimpleRichTextFX.Line) this.getChildren().get(lineNumber);
            
            if ((startCharacter >= 0) && (endCharacter <= line.GetTotalCharacters())) {
                for (int i = startCharacter; i <= endCharacter; i++) {
                    Alphabet character = (Alphabet) line.getChildren().get(i);
                    character.SetFont(fontFamily, this.CurrentFontSize);
                }
            }
        }
    }
    
    public void SetFontSize(int size) {
        this.CurrentFontSize = size;
        
        if (this.IsAnythingSelected()) {
            for (int i = 0; i < GetTotalLines(); i++) {
                zunayedhassan.SimpleRichTextFX.Line line = (zunayedhassan.SimpleRichTextFX.Line) getChildren().get(i);

                for (int j = 0; j < line.getChildren().size(); j++) {
                    Alphabet character = (Alphabet) line.getChildren().get(j);
                    
                    if (character.IsSelected()) {
                        character.SetFontSize(size);
                    }
                }
            }
        }
    }
    
    public void SetFontSizeInLine(int lineNumber, int startCharacter, int endCharacter, int size) {
        lineNumber--;
        
        if (lineNumber < this.GetTotalLines()) {
            zunayedhassan.SimpleRichTextFX.Line line = (zunayedhassan.SimpleRichTextFX.Line) this.getChildren().get(lineNumber);
            
            if ((startCharacter >= 0) && (endCharacter <= line.GetTotalCharacters())) {
                for (int i = startCharacter; i <= endCharacter; i++) {
                    Alphabet character = (Alphabet) line.getChildren().get(i);
                    character.SetFontSize(size);
                }
            }
        }
    }
    
    public void SetBold(boolean isBold) {
        this.IsCurrentFontBold = isBold;
        
        if (this.IsAnythingSelected()) {
            for (int i = 0; i < GetTotalLines(); i++) {
                zunayedhassan.SimpleRichTextFX.Line line = (zunayedhassan.SimpleRichTextFX.Line) getChildren().get(i);

                for (int j = 0; j < line.getChildren().size(); j++) {
                    Alphabet character = (Alphabet) line.getChildren().get(j);
                    
                    if (character.IsSelected()) {
                        character.SetBold(this.IsCurrentFontBold);
                    }
                }
            }
        }
    }
    
    public void SetBoldInLine(int lineNumber, int startCharacter, int endCharacter, boolean isBold) {
        lineNumber--;
        
        if (lineNumber < this.GetTotalLines()) {
            zunayedhassan.SimpleRichTextFX.Line line = (zunayedhassan.SimpleRichTextFX.Line) this.getChildren().get(lineNumber);
            
            if ((startCharacter >= 0) && (endCharacter <= line.GetTotalCharacters())) {
                for (int i = startCharacter; i <= endCharacter; i++) {
                    Alphabet character = (Alphabet) line.getChildren().get(i);
                    this.IsCurrentFontBold = isBold;
                    character.SetBold(this.IsCurrentFontBold);
                }
            }
        }
    }
    
    public void SetItalic(boolean isItalic) {
        this.IsCurrentFontItalic = isItalic;
        
        if (this.IsAnythingSelected()) {
            for (int i = 0; i < GetTotalLines(); i++) {
                zunayedhassan.SimpleRichTextFX.Line line = (zunayedhassan.SimpleRichTextFX.Line) getChildren().get(i);

                for (int j = 0; j < line.getChildren().size(); j++) {
                    Alphabet character = (Alphabet) line.getChildren().get(j);
                    
                    if (character.IsSelected()) {
                        character.SetItalic(this.IsCurrentFontItalic);
                    }
                }
            }
        }
    }
    
    public void SetItalicInLine(int lineNumber, int startCharacter, int endCharacter, boolean isItalic) {
        lineNumber--;
        
        if (lineNumber < this.GetTotalLines()) {
            zunayedhassan.SimpleRichTextFX.Line line = (zunayedhassan.SimpleRichTextFX.Line) this.getChildren().get(lineNumber);
            
            if ((startCharacter >= 0) && (endCharacter <= line.GetTotalCharacters())) {
                for (int i = startCharacter; i <= endCharacter; i++) {
                    Alphabet character = (Alphabet) line.getChildren().get(i);
                    this.IsCurrentFontItalic = isItalic;
                    character.SetItalic(this.IsCurrentFontItalic);
                }
            }
        }
    }
    
    public void SetUnderline(boolean isUnderline) {
        this.IsUnderline = isUnderline;
        
        if (this.IsAnythingSelected()) {
            for (int i = 0; i < GetTotalLines(); i++) {
                zunayedhassan.SimpleRichTextFX.Line line = (zunayedhassan.SimpleRichTextFX.Line) getChildren().get(i);

                for (int j = 0; j < line.getChildren().size(); j++) {
                    Alphabet character = (Alphabet) line.getChildren().get(j);
                    
                    if (character.IsSelected()) {
                        character.SetUnderline(this.IsUnderline);
                    }
                }
            }
        }
    }
    
    public void SetUndelineInLine(int lineNumber, int startCharacter, int endCharacter, boolean isUnderline) {
        lineNumber--;
        
        if (lineNumber < this.GetTotalLines()) {
            zunayedhassan.SimpleRichTextFX.Line line = (zunayedhassan.SimpleRichTextFX.Line) this.getChildren().get(lineNumber);
            
            if ((startCharacter >= 0) && (endCharacter <= line.GetTotalCharacters())) {
                for (int i = startCharacter; i <= endCharacter; i++) {
                    Alphabet character = (Alphabet) line.getChildren().get(i);
                    this.IsUnderline = isUnderline;
                    character.SetUnderline(this.IsUnderline);
                }
            }
        }
    }
    
    public void SetStrikethrough(boolean isStrikethrough) {
        this.IsStrikethrough = isStrikethrough;
        
        if (this.IsAnythingSelected()) {
            for (int i = 0; i < GetTotalLines(); i++) {
                zunayedhassan.SimpleRichTextFX.Line line = (zunayedhassan.SimpleRichTextFX.Line) getChildren().get(i);

                for (int j = 0; j < line.getChildren().size(); j++) {
                    Alphabet character = (Alphabet) line.getChildren().get(j);
                    
                    if (character.IsSelected()) {
                        character.SetStrikethrough(this.IsStrikethrough);
                    }
                }
            }
        }
    }
    
    public void SetStrikethroughInLine(int lineNumber, int startCharacter, int endCharacter, boolean isStrikethrough) {
        lineNumber--;
        
        if (lineNumber < this.GetTotalLines()) {
            zunayedhassan.SimpleRichTextFX.Line line = (zunayedhassan.SimpleRichTextFX.Line) this.getChildren().get(lineNumber);
            
            if ((startCharacter >= 0) && (endCharacter <= line.GetTotalCharacters())) {
                for (int i = startCharacter; i <= endCharacter; i++) {
                    Alphabet character = (Alphabet) line.getChildren().get(i);
                    this.IsStrikethrough = isStrikethrough;
                    character.SetStrikethrough(this.IsStrikethrough);
                }
            }
        }
    }
    
    private void _justify(Pos position) {
        for (int i = 0; i < GetTotalLines(); i++) {
            zunayedhassan.SimpleRichTextFX.Line line = (zunayedhassan.SimpleRichTextFX.Line) getChildren().get(i);

            boolean isSelectionFound = false;
            
            for (int j = 0; j < line.getChildren().size(); j++) {
                Alphabet character = (Alphabet) line.getChildren().get(j);
                
                if (character.IsSelected() || (character.GetCaret() != null)) {
                    isSelectionFound = true;
                    break;
                }
            }
            
            if (isSelectionFound) {
                line.setAlignment(position);
            }
        }
    }
    
    public void SetLeftJustify() {
        this._justify(Pos.BASELINE_LEFT);
    }
    
    public void SetLeftJustifyInLine(int lineNumber) {
        lineNumber--;
        
        if (lineNumber < this.GetTotalLines()) {
            zunayedhassan.SimpleRichTextFX.Line line = (zunayedhassan.SimpleRichTextFX.Line) this.getChildren().get(lineNumber);
            line.SetLeftJustify();
        }
    }
    
    public void SetCenterJustify() {
        this._justify(Pos.BASELINE_CENTER);
    }
    
    public void SetCenterJustifyInLine(int lineNumber) {
        lineNumber--;
        
        if (lineNumber < this.GetTotalLines()) {
            zunayedhassan.SimpleRichTextFX.Line line = (zunayedhassan.SimpleRichTextFX.Line) this.getChildren().get(lineNumber);
            line.SetCenterJustify();
        }
    }
    
    public void SetRightJustify() {
        this._justify(Pos.BASELINE_RIGHT);
    }
    
    public void SetRightJustifyInLine(int lineNumber) {
        lineNumber--;
        
        if (lineNumber < this.GetTotalLines()) {
            zunayedhassan.SimpleRichTextFX.Line line = (zunayedhassan.SimpleRichTextFX.Line) this.getChildren().get(lineNumber);
            line.SetRightJustify();
        }
    }
    
    public void SetColor(Color color) {
        this.CurrentColor = color;
        
        if (this.IsAnythingSelected()) {
            for (int i = 0; i < GetTotalLines(); i++) {
                zunayedhassan.SimpleRichTextFX.Line line = (zunayedhassan.SimpleRichTextFX.Line) getChildren().get(i);

                for (int j = 0; j < line.getChildren().size(); j++) {
                    Alphabet character = (Alphabet) line.getChildren().get(j);
                    
                    if (character.IsSelected()) {
                        character.SetColor(this.CurrentColor);
                    }
                }
            }
        }
    }
    
    public void SetColorInLine(int lineNumber, int startCharacter, int endCharacter, Color color) {
        lineNumber--;
        
        if (lineNumber < this.GetTotalLines()) {
            zunayedhassan.SimpleRichTextFX.Line line = (zunayedhassan.SimpleRichTextFX.Line) this.getChildren().get(lineNumber);
            
            if ((startCharacter >= 0) && (endCharacter <= line.GetTotalCharacters())) {
                for (int i = startCharacter; i <= endCharacter; i++) {
                    Alphabet character = (Alphabet) line.getChildren().get(i);
                    this.CurrentColor = color;
                    character.SetColor(this.CurrentColor);
                }
            }
        }
    }
    
    private Alphabet _getNewCharacter() {
        return (new Alphabet(this, ""));
    }
    
    private Alphabet _getNewCharacter(String character) {
        return (new Alphabet(this, character));
    }
    
    private zunayedhassan.SimpleRichTextFX.Line _getNewLine() {
        return (new zunayedhassan.SimpleRichTextFX.Line(this));
    }
    
    private String _getClear() {
        int previousLine = 0;
        boolean isSelectedLineFound = false;
        String cutText = "";
        
        for (int i = 0; i < GetTotalLines(); i++) {
            zunayedhassan.SimpleRichTextFX.Line line = (zunayedhassan.SimpleRichTextFX.Line) getChildren().get(i);

            ArrayList<Alphabet> charactersToBeRemoved = new ArrayList<>();
            boolean isSelectedCharacterFoundAlready = false;
            int newCaretPosition = 0;

            for (int j = 0; j < line.getChildren().size(); j++) {
                Alphabet character = (Alphabet) line.getChildren().get(j);

                if (character.IsSelected()) {
                    if (!isSelectedLineFound) {
                        previousLine = i;
                        isSelectedLineFound = true;
                    }
                    
                    if (!isSelectedCharacterFoundAlready) {
                        newCaretPosition = j;
                        isSelectedCharacterFoundAlready = true;
                    }

                    charactersToBeRemoved.add(character);
                    cutText += character.GetText();
                    
                    if ((isSelectedLineFound) && (i != previousLine)) {
                        previousLine++;
                        cutText += "\n";
                    }
                }
            }

            if (charactersToBeRemoved.size() > 0) {
                for (Alphabet character : charactersToBeRemoved) {
                    line.getChildren().remove(character);
                }

                ClearCaret();

                if (newCaretPosition > line.GetTotalCharacters()) {
                    --newCaretPosition;
                }

                if ((newCaretPosition <= line.GetTotalCharacters()) && (newCaretPosition != -1)) {
                    ((Alphabet) line.getChildren().get(newCaretPosition)).SetCaret();
                }
                else if (i > 0) {
                    zunayedhassan.SimpleRichTextFX.Line lineToBeSetCaret = ((zunayedhassan.SimpleRichTextFX.Line) getChildren().get(i - 1));

                    if (lineToBeSetCaret.GetTotalCharacters() >= 0) {
                        ((Alphabet) lineToBeSetCaret.getChildren().get(lineToBeSetCaret.GetTotalCharacters())).SetCaret();
                    }
                }
            }
        }

        if (((zunayedhassan.SimpleRichTextFX.Line) getChildren().get(0)).getChildren().size() == 0) {
            Alphabet newCharacter = _getNewCharacter();
            ((zunayedhassan.SimpleRichTextFX.Line) getChildren().get(0)).getChildren().add(newCharacter);
            newCharacter.SetCaret();
        }
        
        return cutText;
    }
    
    private zunayedhassan.SimpleRichTextFX.Line _getLastLine() {
        zunayedhassan.SimpleRichTextFX.Line lastLine = null;
  
        ArrayList<zunayedhassan.SimpleRichTextFX.Line> lines = this.GetLines();

        if (lines.size() > 0) {
            lastLine = lines.get(lines.size() - 1);
        }
        
        return lastLine;
    }
    
    private ContextMenu _getContextMenu() {
        ContextMenu contextMenu = new ContextMenu();

        contextMenu.getItems().addAll(
                this.cutMenuItem,
                this.copyMenuItem,
                this.pasteMenuItem,
                new SeparatorMenuItem(),
                this.selectAllMenuItem
        );
        
        return contextMenu;
    }
    
    private void _showContextMenu(double x, double y) {
        contextMenu.show(this, x, y);
    }
    
    private ImageView _getIcon(String image) {
        return new ImageView(new Image(this.getClass().getResourceAsStream(image)));
    }
    
    public void SetSpellCheckingSupport(boolean isSpellCheck) {
        this._isSpellCheckOn = isSpellCheck;
        
        if (this._isSpellCheckOn) {
            this._dictionary.clear();
            
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader("Resources/words3.txt"));

                String line = null;

                while ((line = bufferedReader.readLine()) != null) {
                    this._dictionary.add(line);
                }
            }
            catch (FileNotFoundException exception) {
                exception.printStackTrace();
            }
            catch (IOException exception) {
                exception.printStackTrace();
            }
            
            this.CheckForSpellingMistake();
        }
        else {
            this._dictionary.clear();
            
            for (int i = 0; i < GetTotalLines(); i++) {
                zunayedhassan.SimpleRichTextFX.Line line = (zunayedhassan.SimpleRichTextFX.Line) getChildren().get(i);

                for (int j = 0; j < line.getChildren().size(); j++) {
                    Alphabet character = (Alphabet) line.getChildren().get(j);
                    
                    character.SetSpellCheckingOn(false);
                }
            }
        }
    }
    
    public boolean IsSpellCheckingSupport() {
        return this._isSpellCheckOn;
    }
    
    public void CheckForSpellingMistake(int lineIndex) {
        if (this.IsSpellCheckingSupport()) {
            zunayedhassan.SimpleRichTextFX.Line line = (zunayedhassan.SimpleRichTextFX.Line) getChildren().get(lineIndex);
            
            ArrayList<Alphabet> wordsInLine = new ArrayList<>();

            for (int i = 0; i <= line.GetTotalCharacters(); i++) {
                Alphabet character = (Alphabet) line.getChildren().get(i);

                if (!character.GetText().equals("~") &&
                    !character.GetText().equals("!") &&
                    !character.GetText().equals("@") &&
                    !character.GetText().equals("#") &&
                    !character.GetText().equals("$") &&
                    !character.GetText().equals("%") &&
                    !character.GetText().equals("^") &&
                    !character.GetText().equals("&") &&
                    !character.GetText().equals("*") &&
                    !character.GetText().equals("(") &&
                    !character.GetText().equals(")") &&
                    !character.GetText().equals("-") &&
                    !character.GetText().equals("+") &&
                    !character.GetText().equals("_") &&
                    !character.GetText().equals("=") &&
                    !character.GetText().equals("[") &&
                    !character.GetText().equals("]") &&
                    !character.GetText().equals("{") &&
                    !character.GetText().equals("}") &&
                    !character.GetText().equals(";") &&
                    !character.GetText().equals("'") &&
                    !character.GetText().equals(":") &&
                    !character.GetText().equals("\"") &&
                    !character.GetText().equals("\\") &&
                    !character.GetText().equals("|") &&
                    !character.GetText().equals(",") &&
                    !character.GetText().equals(".") &&
                    !character.GetText().equals("<") &&
                    !character.GetText().equals(">") &&
                    !character.GetText().equals("/") &&
                    !character.GetText().equals("?")) {
                    
                    wordsInLine.add(character);

                    if (character.GetText().equals(" ") ||
                       (i == line.GetTotalCharacters())) {

                        String word = "";

                        for (Alphabet currentCharacter : wordsInLine) {
                            word += currentCharacter.GetText();
                        }

                        word = word.trim().toLowerCase();
                        boolean isFound = false;

                        for (String currentWord : this._dictionary) {
                            if (currentWord.toLowerCase().trim().equals(word)) {
                                isFound = true;
                                break;
                            }
                        }

                        if (!isFound) {
                            for (Alphabet currentCharacter : wordsInLine) {
                                currentCharacter.SetSpellCheckingOn(true);
                            }
                        }
                        else {
                            for (Alphabet currentCharacter : wordsInLine) {
                                currentCharacter.SetSpellCheckingOn(false);
                            }
                        }

                        wordsInLine.clear();
                    }
                }
            }
        }
    }
    
    public void CheckForSpellingMistake() {
        for (int i = 0; i < this.GetTotalLines(); i++) {
            this.CheckForSpellingMistake(i);
        }
    }
}
