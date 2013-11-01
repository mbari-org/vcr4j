/*
 * @(#)TimeSelectPanel.java   2009.02.24 at 09:44:54 PST
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



package org.mbari.vcr.ui;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import org.mbari.movie.Timecode;
import org.mbari.vcr.VCRUtil;

/**
 * <p>
 * A panel with 4 sliders and editable text fields. This class is useful for
 * specifying a timecode for the seekTimecode() method of IVCR.
 * </p>
 *
 * @author : $Author: hohonuuli $
 * @version : $Revision: 332 $
 */
public class TimeSelectPanel extends JPanel {

    /**
     *
     */
    private static final long serialVersionUID = -8507196646203262606L;

    /**
     *     @uml.property  name="frameWidget"
     *     @uml.associationEnd
     */
    private TimeSlider frameWidget;

    /**
     *     @uml.property  name="hourWidget"
     *     @uml.associationEnd
     */
    private TimeSlider hourWidget;

    /**
     *     @uml.property  name="minuteWidget"
     *     @uml.associationEnd
     */
    private TimeSlider minuteWidget;

    /**
     *     @uml.property  name="numberFormat"
     */
    private NumberFormat numberFormat;

    /**
     *     @uml.property  name="secondWidget"
     *     @uml.associationEnd
     */
    private TimeSlider secondWidget;

    /** Constructor. */
    public TimeSelectPanel() {
        initialize();
    }

    /** @return The frame value set in this panel */
    public int getFrame() {
        return getFrameWidget().getTime();
    }

    /*
     * TODO 20040517 brian: VCRPanel needs to grab this and register a document
     * listener to transfer focus to the OK button.
     */

    /**
     *     <p><!-- Method description --></p>
     *     @return
     *     @uml.property  name="frameWidget"
     */
    public TimeSlider getFrameWidget() {
        if (frameWidget == null) {
            frameWidget = new TimeSlider(30);
        }

        return frameWidget;
    }

    /** @return The hour value set in this panel */
    public int getHour() {
        return getHourWidget().getTime();
    }

    /**
     *     <p><!-- Method description --></p>
     *     @return
     *     @uml.property  name="hourWidget"
     */
    public TimeSlider getHourWidget() {
        if (hourWidget == null) {
            hourWidget = new TimeSlider(24);

            final JTextField tf = hourWidget.getTextField();
            final Document d = tf.getDocument();

            d.addDocumentListener(makeDocumentListener(getMinuteWidget().getTextField()));
        }

        return hourWidget;
    }

    /** @return The minute value set in this panel */
    public int getMinute() {
        return getMinuteWidget().getTime();
    }

    /**
     *     <p><!-- Method description --></p>
     *     @return
     *     @uml.property  name="minuteWidget"
     */
    public TimeSlider getMinuteWidget() {
        if (minuteWidget == null) {
            minuteWidget = new TimeSlider(59);

            final JTextField tf = minuteWidget.getTextField();
            final Document d = tf.getDocument();

            d.addDocumentListener(makeDocumentListener(getSecondWidget().getTextField()));
        }

        return minuteWidget;
    }

    /**
     *     <p><!-- Method description --></p>
     *     @return
     *     @uml.property  name="numberFormat"
     */
    private NumberFormat getNumberFormat() {
        if (numberFormat == null) {
            numberFormat = new DecimalFormat();
            numberFormat.setMaximumFractionDigits(0);
            numberFormat.setMinimumIntegerDigits(2);
        }

        return numberFormat;
    }

    /** @return The second value set in this panel */
    public int getSecond() {
        return getSecondWidget().getTime();
    }

    /**
     *     <p><!-- Method description --></p>
     *     @return
     *     @uml.property  name="secondWidget"
     */
    public TimeSlider getSecondWidget() {
        if (secondWidget == null) {
            secondWidget = new TimeSlider(59);

            final JTextField tf = secondWidget.getTextField();
            final Document d = tf.getDocument();

            d.addDocumentListener(makeDocumentListener(getFrameWidget().getTextField()));
        }

        return secondWidget;
    }

    /**
     * Most classes that use an instance of this panel will call this method to
     * get the time. The format returned by this method is the one used directly
     * by the VCR for timecodes.
     *
     * @return A timecode that is formatted for use by a Sony VCR.
     */
    public byte[] getTime() {
        return new byte[] { VCRUtil.intToTime(getFrameWidget().getTime()),
                            VCRUtil.intToTime(getSecondWidget().getTime()),
                            VCRUtil.intToTime(getMinuteWidget().getTime()),
                            VCRUtil.intToTime(getHourWidget().getTime()) };
    }

    /**
     * Formats the timecode into a string that a user would expect to see. For
     * example 23:11:03:16
     *
     * @return A timecode that is formatted as a String.
     */
    public String getTimeAsString() {
        final StringBuffer sb = new StringBuffer();
        final NumberFormat nf = getNumberFormat();

        sb.append(nf.format((long) getHour())).append(":");
        sb.append(nf.format((long) getMinute())).append(":");
        sb.append(nf.format((long) getSecond())).append(":");
        sb.append(nf.format((long) getFrame()));

        return sb.toString();
    }

    public Timecode getTimecode() {
        final Timecode timecode = new Timecode();

        timecode.setTime(getHour(), getMinute(), getSecond(), getFrame());

        return timecode;
    }

    /**
     * Setup method generated by JBuilder
     *
     */
    void initialize() {
        add(getHourWidget());
        add(getMinuteWidget());
        add(getSecondWidget());
        add(getFrameWidget());
    }

    /**
     * Generates a document listener that transfers focus to another target
     * when the document reaches 2 characters in length
     * @param target
     * @return
     */
    public DocumentListener makeDocumentListener(final JComponent target) {
        return new DocumentListener() {

            public void changedUpdate(DocumentEvent e) {
                update(e);
            }
            public void insertUpdate(DocumentEvent e) {
                update(e);
            }
            public void removeUpdate(DocumentEvent e) {

                // DO nothing on removes
            }
            void update(DocumentEvent e) {
                if (e.getDocument().getLength() >= 2) {
                    target.requestFocus();
                }
            }
        };
    }
}
