package com.cisco.dvbu.ps.deploytool.gui.core.shared;

import com.cisco.dvbu.ps.deploytool.gui.util.StringUtils;

import org.jdom2.Element;

/**
 * <p>
 * Bean object for refresh and expiration periods. Intended to be serialized/deserialized by Jackson into/from JSON.
 * </p>
 *
 * @author Calvin Goodrich
 * @version 1.0
 */
public class CalendarPeriod {
    public static final int CALENDAR_PERIOD_TYPE_NULL = 0;
    public static final int CALENDAR_PERIOD_TYPE_SECOND = 1;
    public static final int CALENDAR_PERIOD_TYPE_MINUTE = 2;
    public static final int CALENDAR_PERIOD_TYPE_HOUR = 3;
    public static final int CALENDAR_PERIOD_TYPE_DAY = 4;
    public static final int CALENDAR_PERIOD_TYPE_WEEK = 5;
    public static final int CALENDAR_PERIOD_TYPE_MONTH = 6;
    public static final int CALENDAR_PERIOD_TYPE_YEAR = 7;
    public static final String[] CALENDAR_PERIOD_TYPE_LABELS = {
        "",
        "SECOND",
        "MINUTE",
        "HOUR",
        "DAY",
        "WEEK",
        "MONTH",
        "YEAR"
    };

    int period = 0;
    int count = -1;
    
    /**
     * <p>
     * Constructor.
     * </p>
     */
    public CalendarPeriod() {}

    /**
     * <p>
     * Constructor. Extracts attribute values from {@link Element} object.
     * </p>
     */
    public CalendarPeriod (Element cpNode) {
        for (Element c : cpNode.getChildren()) {
            if (c.getName().equals ("period")) {
                for (int i = 0; i < CALENDAR_PERIOD_TYPE_LABELS.length; i++) {
                    if (c.getText().toUpperCase().equals(CALENDAR_PERIOD_TYPE_LABELS[i])) {
                        this.period = i;
                        break;
                    }
                }
            }

            if (c.getName().equals ("count"))
                this.count = Integer.parseInt (c.getText());
        }
    }

    /**
     * <p>
     * Returns the object as a JDom Element.
     * </p>
     * 
     * @param  name   The name of the element to use.
     * @param  indent The number of tabs (spaces) to indent the child elements.
     * @return        The value.
     */
    public Element toElement(
        String name,
        int indent
    ) {
        Element result = new Element (name);
        String indentStr = StringUtils.getIndent (indent);

        result.addContent ("\n" + indentStr);
        result.addContent (new Element ("period").setText (CALENDAR_PERIOD_TYPE_LABELS[this.period]));

        result.addContent ("\n" + indentStr);
        result.addContent (new Element ("count").setText ("" + this.count));

        return result;
    }

    /**
     * <p>
     * Attaches the period information to the input {@link Element}.
     * </p>
     * 
     * @param  e      The element to attach the period information to.
     * @param  indent The number of tabs (spaces) to indent the child elements.
     */
    public void attachToElement(
        Element e,
        int indent
    ) {
        String indentStr = StringUtils.getIndent (indent);

        e.addContent ("\n" + indentStr);
        e.addContent (new Element ("period").setText (CALENDAR_PERIOD_TYPE_LABELS[this.period]));

        e.addContent ("\n" + indentStr);
        e.addContent (new Element ("count").setText ("" + this.count));
    }

    /**
     * <p>
     * Sets the <code>period</code> field.
     * </p>
     * 
     * @param  period  The calendar period unit of measure. See CALENDAR_PERIOD_TYPE_* constants above.
     */
    public void setPeriod (int period) {
        this.period = period;
    }

    /**
     * <p>
     * Returns the value of the <code>period</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public int getPeriod () {
        return period;
    }

    /**
     * <p>
     * Sets the <code>count</code> field.
     * </p>
     * 
     * @param  count  The number of units (See period) in the calendar period.
     */
    public void setCount (int count) {
        this.count = count;
    }

    /**
     * <p>
     * Returns the value of the <code>count</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public int getCount () {
        return count;
    }
}
