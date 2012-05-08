package taygalove_shepherd.ui;

import java.awt.Font;

import javax.swing.*;
import javax.swing.text.Document;

public class CTextArea extends JTextArea {
    public CTextArea() {
        init();
    }

    public CTextArea(int rows, int columns) {
        super(rows, columns);
        init();
    }

    public CTextArea(String text) {
        super(text,2,0);
        init();
    }

    public CTextArea(String text, int rows, int columns) {
        super(text, rows, columns);
        init();
    }

    public CTextArea(Document doc) {
        super(doc);
        init();
    }

    public CTextArea(Document doc, String text, int rows, int columns) {
        super(doc, text, rows, columns);
        init();
    }

    private void init(){
      setBorder(BorderFactory.createLoweredBevelBorder());
      setFont(new Font("Dialog",Font.PLAIN,12));
    }
}
