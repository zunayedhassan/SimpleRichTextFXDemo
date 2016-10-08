/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zunayedhassan;

import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import zunayedhassan.SimpleRichTextFX.RichText;

/**
 *
 * @author Zunayed Hassan
 */
public class RootUI extends BaseUI {
    public ToolBar RichTextToolBar = new ToolBar();
    public RichText RichTextControl = new RichText();
    
    protected ScrollPane scrollPane = null;
    protected ComboBox<String> fontsCombobox = this._getFontsCombobox();
    protected ComboBox<String> fontSizeComboBox = this._getFontSizeComboBox();
    protected ToggleButton boldToggleButton = this._getIconToggleButton("SimpleRichTextFX/icons/format-text-bold.png");
    protected ToggleButton italicToggleButton = this._getIconToggleButton("SimpleRichTextFX/icons/format-text-italic.png");
    protected ToggleButton underlineToggleButton = this._getIconToggleButton("SimpleRichTextFX/icons/format-text-underline.png");
    protected ToggleButton strikethroughToggleButton = this._getIconToggleButton("SimpleRichTextFX/icons/format-text-strikethrough.png");
    protected Button leftJustfyToggleButton = this._getIconButton("SimpleRichTextFX/icons/format-justify-left.png");
    protected Button centerJustfyToggleButton = this._getIconButton("SimpleRichTextFX/icons/format-justify-center.png");
    protected Button rightJustfyToggleButton = this._getIconButton("SimpleRichTextFX/icons/format-justify-right.png");
    protected ColorPicker fontColorPicker = new ColorPicker(Color.BLACK);
    protected ToggleButton spellCheckToggleButton = this._getIconToggleButton("SimpleRichTextFX/icons/tools-check-spelling.png");
    
    public RootUI() {    
        this._initializeLayout();
        this._initializeEvents();
        
        // Example        
        this.RichTextControl.AddText("Hello World ", 12);
        this.RichTextControl.AddLine();
        this.RichTextControl.AddText("Rich Text Test", 28);
        this.RichTextControl.AddLine();
        this.RichTextControl.AddText("This is another line", 20);
        this.RichTextControl.SetFontInLine(1, 1, 5, "Comic Sans MS");
        this.RichTextControl.SetFontSizeInLine(1, 1, 5, 36);
        this.RichTextControl.SetBoldInLine(1, 1, 5, true);
        this.RichTextControl.SetItalicInLine(2, 1, 5, true);
        this.RichTextControl.SetUndelineInLine(1, 1, 5, true);
        this.RichTextControl.SetStrikethroughInLine(2, 1, 5, true);
        this.RichTextControl.SetLeftJustifyInLine(1);
        this.RichTextControl.SetCenterJustifyInLine(2);
        this.RichTextControl.SetRightJustifyInLine(3);
        this.RichTextControl.SetColorInLine(2, 1, 5, Color.web("#51dacd"));
        this.RichTextControl.AddLine();
        this.RichTextControl.AddText("Spellcheck is also available now!!!", 28);
        this.RichTextControl.SetFontSizeInLine(1, 1, 5, 36);
        this.RichTextControl.SetBoldInLine(3, 1, 5, false);
        this.RichTextControl.SetItalicInLine(3, 1, 5, false);
        this.RichTextControl.SetUndelineInLine(3, 1, 5, false);
        this.RichTextControl.SetStrikethroughInLine(3, 1, 5, false);
    }
    
    private ComboBox<String> _getFontsCombobox() {
        ComboBox<String> fontsCombobox = new ComboBox<>();
        List<String> fontsList = Font.getFamilies();
        fontsCombobox.getItems().addAll(fontsList);
        
        int defaultFontIndex = fontsList.indexOf("System");
        fontsCombobox.getSelectionModel().select(defaultFontIndex);
        
        return fontsCombobox;
    }
    
    private ComboBox<String> _getFontSizeComboBox() {
        ComboBox<String> fontSize = new ComboBox<>();
        
        fontSize.getItems().addAll(
                "8", "10", "12", "14", "16", "18", "20", "22", "24", "36", "48", "72"
        );
        
        fontSize.getSelectionModel().select(2);
        
        return fontSize;
    }
    
    private ToggleButton _getIconToggleButton(String icon) {
        ToggleButton toggleButton = new ToggleButton();
        toggleButton.setGraphic(new ImageView(new Image(this.getClass().getResourceAsStream(icon))));
        
        return toggleButton;
    }
    
    private Button _getIconButton(String icon) {
        Button button = new Button();
        button.setGraphic(new ImageView(new Image(this.getClass().getResourceAsStream(icon))));
        
        return button;
    }
    
    private void _initializeLayout() {
        this.RichTextToolBar.getItems().addAll(
                this.fontsCombobox,
                this.fontSizeComboBox,
                new Separator(),
                this.boldToggleButton,
                this.italicToggleButton,
                this.underlineToggleButton,
                this.strikethroughToggleButton,
                new Separator(),
                this.leftJustfyToggleButton,
                this.centerJustfyToggleButton,
                this.rightJustfyToggleButton,
                new Separator(),
                this.fontColorPicker,
                this.spellCheckToggleButton
        );
        
        this.setTop(this.RichTextToolBar);
        
        this.scrollPane = new ScrollPane(this.RichTextControl);
        this.setCenter(this.RichTextControl);
        
        scrollPane.setFitToWidth(true);
    }
    
    private void _initializeEvents() {
        this.scrollPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                RichTextControl.requestFocus();
            }
        });
        
        this.fontsCombobox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String choosenFont) {
                RichTextControl.SetFont(choosenFont);
            }
        });
        
        this.fontSizeComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String fontSizeAsText) {
                int fontSize = Integer.parseInt(fontSizeAsText);
                RichTextControl.SetFontSize(fontSize);
            }
        });
        
        this.boldToggleButton.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean isBold) {
                RichTextControl.SetBold(isBold);
            }
        });
        
        this.italicToggleButton.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean isItalic) {
                RichTextControl.SetItalic(isItalic);
            }
        });
        
        this.underlineToggleButton.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean isUnderline) {
                RichTextControl.SetUnderline(isUnderline);
            }
        });
        
        this.strikethroughToggleButton.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean isStrikethrough) {
                RichTextControl.SetStrikethrough(isStrikethrough);
            }
        });
        
        this.leftJustfyToggleButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                RichTextControl.SetLeftJustify();
            }
        });
        
        this.centerJustfyToggleButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                RichTextControl.SetCenterJustify();
            }
        });
        
        this.rightJustfyToggleButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                RichTextControl.SetRightJustify();
            }
        });
        
        this.fontColorPicker.valueProperty().addListener(new ChangeListener<Color>() {
            @Override
            public void changed(ObservableValue<? extends Color> observable, Color oldValue, Color choosenColor) {
                RichTextControl.SetColor(choosenColor);
            }
        });
        
        this.spellCheckToggleButton.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean isSelected) {
                RichTextControl.SetSpellCheckingSupport(isSelected);
            }
        });
    }
}