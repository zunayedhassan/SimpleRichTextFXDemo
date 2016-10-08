# SimpleRichTextFX Demo
RichText Control for JavaFX

## Download (Demo): https://github.com/zunayedhassan/SimpleRichTextFXDemo/blob/master/Download/SimpleRichTextFXDemo.zip

![ScreenShot](https://github.com/zunayedhassan/SimpleRichTextFXDemo/blob/master/preview.png?raw=true)

## Useage

``` java
public RichText RichTextControl = new RichText();

// ...
this.RichTextControl.SetSpellCheckingSupport(true);

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

```
