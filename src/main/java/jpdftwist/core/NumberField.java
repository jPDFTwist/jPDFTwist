/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package jpdftwist.core;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

/**
 * A JTextField that accepts only numbers .
 *
 * @author vasilis
 */
public class NumberField extends JTextField {

    public NumberField() {
        super();
    }

    public NumberField(int cols) {
        super(cols);
    }

    public NumberField(String text) {
        super(text);
    }


    protected Document createDefaultModel() {
        return new DoubleNumberDocument();
    }

    static class DoubleNumberDocument extends PlainDocument {


        public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {

            if (str == null) {
                return;
            }

            char[] chars = str.toCharArray();
            boolean ok = true;

            for (int i = 0; i < chars.length; i++) {

                if (chars[i] == '.' && !super.getText(0, super.getLength()).contains("."))
                    continue;

                try {
                    Double.parseDouble(String.valueOf(chars[i]));
                } catch (NumberFormatException ex) {
                    ok = false;
                    Logger.getLogger(NumberField.class.getName()).log(Level.SEVERE, "Ex058", ex);
                    break;
                }

            }

            if (ok) {
                super.insertString(offs, new String(chars), a);
            }

        }
    }
}