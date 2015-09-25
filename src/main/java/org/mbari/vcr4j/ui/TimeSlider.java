/*
 * @(#)TimeSlider.java   2009.02.24 at 09:44:54 PST
 *
 * Copyright 2007 MBARI
 *
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 2.1
 * (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/copyleft/lesser.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package org.mbari.vcr4j.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.mbari.awt.layout.VerticalFlowLayout;

/**
 * <p>
 * This class is used for sliders in the TimeSelectPanel. It provides a vertical
 * slider with an editable text field. Changes in the slider or the textfield
 * will be reflected by the other component.
 * </p>
 *
 * @author : $Author: hohonuuli $
 * @version : $Revision: 332 $
 */
public class TimeSlider extends JPanel {

    /**
     *
     */
    private static final long serialVersionUID = 5054214597783715431L;

    /**
         * @uml.property  name="layout"
         * @uml.associationEnd  multiplicity="(1 1)"
         */
    final VerticalFlowLayout layout = new VerticalFlowLayout();

    /**
         * @uml.property  name="maxValue"
         */
    int maxValue;

    /**
         * @uml.property  name="slider"
         * @uml.associationEnd
         */
    JSlider slider;

    /**
         * @uml.property  name="textField"
         * @uml.associationEnd
         */
    JTextField textField;

    /**
     * Constructs ...
     *
     *
     * @param maxValue
     */
    public TimeSlider(int maxValue) {
        this.maxValue = maxValue;
        initialize();
    }

    /**
         * <p><!-- Method description --></p>
         * @return
         * @uml.property  name="slider"
         */
    public JSlider getSlider() {
        if (slider == null) {
            slider = new JSlider();
            slider.setOrientation(JSlider.VERTICAL);
            slider.setInverted(true);
            slider.setMajorTickSpacing(10);
            slider.setMaximum(maxValue);
            slider.setMinorTickSpacing(1);
            slider.setPaintTicks(true);
            slider.setPreferredSize(new Dimension(43, 230));

            /*
             * Adjustments to the slider should be reflected in the textField.
             */
            slider.addChangeListener(new ChangeListener() {

                public void stateChanged(ChangeEvent e) {
                    String v = slider.getValue() + "";
                    String text = getTextField().getText();

                    if (!v.equals(text)) {
                        getTextField().setText(slider.getValue() + "");
                        getTextField().requestFocus();
                    }
                }

            });
            slider.setValue(0);
            slider.setPreferredSize(new Dimension(38, maxValue * 3));
            slider.setRequestFocusEnabled(false);
            slider.setFocusable(false);
        }

        return slider;
    }

    /**
         * <p><!-- Method description --></p>
         * @return
         * @uml.property  name="textField"
         */
    public JTextField getTextField() {
        if (textField == null) {
            textField = new JTextField();
            textField.setBackground(Color.black);
            textField.setFont(new java.awt.Font("Dialog", 1, 16));
            textField.setForeground(Color.red);
            textField.setPreferredSize(new Dimension(38, 25));
            textField.setSelectedTextColor(Color.red);
            textField.setText("");
            textField.setHorizontalAlignment(SwingConstants.RIGHT);
            textField.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    slider.setValue(Integer.parseInt(textField.getText()));
                }
            });

            /*
             * We need to consume non-digit values.
             */
            textField.addKeyListener(new KeyAdapter() {

                public void keyTyped(KeyEvent e) {
                    char c = e.getKeyChar();

                    if (c == KeyEvent.VK_ENTER) {

                        // do nothing. Enter is handled by the action Listener
                    }
                    else if (!((Character.isDigit(c)) || (c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE))) {
                        getToolkit().beep();
                        e.consume();
                    }
                }

            });

            /*
             * When focus is gainged in a textfield, highlight all characters so
             * that typing overwrites the existing values
             *
             * When focus is lost the slider should be updated with the value
             * from the textField.
             */
            textField.addFocusListener(new FocusListener() {

                public void focusGained(FocusEvent e) {
                    textField.setSelectionStart(0);
                    textField.setSelectionEnd(textField.getText().length());
                }
                public void focusLost(FocusEvent e) {
                    int value = 0;

                    try {
                        value = Integer.parseInt(textField.getText());
                    }
                    catch (NumberFormatException ex) {

                        /*
                         * Ignore. This is for "". Values are set to minimum
                         * allowed value
                         */
                    }

                    setTime(value);
                }

            });
        }

        return textField;
    }

    /**
     * Returns the value that this component is set to.
     *
     * @return A number that the slider is set to.
     */
    public int getTime() {
        return slider.getValue();
    }

    /**
     * <p><!-- Method description --></p>
     *
     */
    void initialize() {
        this.setLayout(layout);
        this.setPreferredSize(new Dimension(45, 230));
        this.add(getTextField(), null);
        this.add(getSlider(), null);
    }

    /**
     * <p><!-- Method description --></p>
     *
     *
     * @param value
     */
    public void setTime(int value) {
        final JSlider s = getSlider();
        final int min = s.getMinimum();
        final int max = s.getMaximum();

        if (value > max) {
            value = max;
        }
        else if (value < min) {
            value = min;
        }

        if (s.getValue() != value) {
            s.setValue(value);
        }
    }
}
