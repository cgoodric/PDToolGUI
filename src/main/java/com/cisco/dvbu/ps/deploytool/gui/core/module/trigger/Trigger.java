package com.cisco.dvbu.ps.deploytool.gui.core.module.trigger;

/*
 * (c) 2014 Cisco and/or its affiliates. All rights reserved.
 */

import com.cisco.dvbu.ps.deploytool.gui.core.shared.CalendarPeriod;
import com.cisco.dvbu.ps.deploytool.gui.util.StringUtils;

import org.jdom2.Element;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

/**
 * <p>
 * Bean object for trigger module schedule records. Intended to be serialized/deserialized by Jackson into/from JSON.
 * </p>
 * 
 * @author Calvin Goodrich
 * @version 1.0
 */
public class Trigger {

//    private static final Logger log = LoggerFactory.getLogger (Trigger.class);
    
    public static final int TYPE_TRIGGER = 0;
    public static final int TYPE_SCHEDULE = 1;
    
    public static final int SCHEDULE_MODE_NULL = 0;
    public static final int SCHEDULE_MODE_NONE = 1;
    public static final int SCHEDULE_MODE_PERIODIC = 2;
    public static final String[] SCHEDULE_MODE_LABELS = {
        "",
        "NONE",
        "PERIODIC"
    };

    // attributes used by the UI
    //
    String operation;
    String origId;
    
    // module specific attributes
    //
    String id;
    int type = 0;
    
    // trigger specific attributes
    //
    String resourcePath;
    boolean isEnabled = false;
    int maxEventsQueued = -1;
    String annotation;
    Condition condition;
    Action action;

    // schedule specific attributes
    //
    int mode = SCHEDULE_MODE_NULL;
    String startTime;
    CalendarPeriod schedulePeriod;
    int fromTimeInADay = -1;
    int endTimeInADay = -1;
    int recurringDay = 0;
    boolean isCluster = false;

    /**
     * <p>
     * Constructor.
     * </p>
     */
    public Trigger () {
        super ();
    }
    
    /**
     * <p>
     * Copy constructor. Used for making/serializing copies, where only the ID is updated.
     * </p>
     */
    public Trigger (Trigger t) {
        if (t != null) {
            this.operation = t.getOperation();
            this.origId = t.getOrigId();
            this.id = t.getId();
            this.type = t.getType();
            this.resourcePath = t.getResourcePath();
            this.isEnabled = t.isIsEnabled();
            this.maxEventsQueued = t.getMaxEventsQueued();
            this.annotation = t.getAnnotation();
            this.condition = t.getCondition(); // do not update !!
            this.action = t.getAction(); // do not update !!
            this.mode = t.getMode();
            this.schedulePeriod = t.getSchedulePeriod(); // do not update !!
            this.fromTimeInADay = t.getFromTimeInADay();
            this.endTimeInADay = t.getEndTimeInADay();
            this.recurringDay = t.getRecurringDay();
            this.isCluster = t.isIsCluster();
        }
    }

    /**
     * <p>
     * Constructor. Extracts attribute values from {@link Element} object.
     * </p>
     */
    public Trigger (Element tNode) {
        if (tNode.getName().equals ("trigger")) {
            this.type = TYPE_TRIGGER;
            
            for (Element c : tNode.getChildren()) {
                if (c.getName().equals ("id"))
                    this.id = c.getText();
                
                if (c.getName().equals ("resourcePath"))
                    this.resourcePath = c.getText();
                
                if (c.getName().equals ("isEnabled"))
                    this.isEnabled = (c.getText() == null) ? true : c.getText().matches ("(?i)^(yes|true|on|1)$");

                if (c.getName().equals ("maxEventsQueued"))
                    this.maxEventsQueued = Integer.parseInt (c.getText());
                
                if (c.getName().equals ("annotation"))
                    this.annotation = c.getText();
                
                if (c.getName().equals ("condition"))
                    this.condition = new Condition (c);
                
                if (c.getName().equals ("action"))
                    this.action = new Action (c);
                
            }
            
        } else if (tNode.getName().equals ("schedule")) {
            this.type = TYPE_SCHEDULE;
            
            for (Element c : tNode.getChildren()) {
                if (c.getName().equals ("scheduleId"))
                    this.id = c.getText();
                
                if (c.getName().equals ("mode")) {
                    for (int i = 0; i < SCHEDULE_MODE_LABELS.length; i++) {
                        if (c.getText().toUpperCase().equals (SCHEDULE_MODE_LABELS[i])) {
                            this.mode = i;
                            break;
                        }
                    }
                }
                
                if (c.getName().equals ("startTime"))
                    this.startTime = c.getText();
                
                // create the CalendarPeriod from the parent element since the period elements are in-line with
                // the rest of the parent element's children. the constructor method should ignore all the
                // other child nodes.
                //
                this.schedulePeriod = new CalendarPeriod (tNode);
                
                // zero out the schedule period if neither period child element were present.
                //
                if (this.schedulePeriod.getCount() == 0 && this.schedulePeriod.getPeriod() == 0)
                    this.schedulePeriod = null;
                
                if (c.getName().equals ("fromTimeInADay"))
                    this.fromTimeInADay = Integer.parseInt (c.getText());
                
                if (c.getName().equals ("endTimeInADay"))
                    this.endTimeInADay = Integer.parseInt (c.getText());
                
                if (c.getName().equals ("recurringDay"))
                    this.recurringDay = Integer.parseInt (c.getText());
                
                if (c.getName().equals ("isCluster"))
                    this.isCluster = (c.getText() == null) ? true : c.getText().matches ("(?i)^(yes|true|on|1)$");
            }
        }
    }

    /**
     * <p>
     * Returns the object as a JDom Element.
     * </p>
     * 
     * @param  ignored  Normally this is the name of the element to use. However, in this case, the name is derived from the type.
     * @param  indent   The number of tabs (spaces) to indent the child elements.
     * @return          The value.
     */
    public Element toElement (
        String ignored,
        int indent
    ) {
        Element result = null;
        String indentStr = StringUtils.getIndent (indent);
        
        if (this.type == TYPE_TRIGGER) {
            result = new Element ("trigger");
            
            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("id").setText (this.id));
            
            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("resourcePath").setText (this.resourcePath));
            
            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("isEnabled").setText ("" + this.isEnabled));
            
            if (this.maxEventsQueued >= 0) {
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("maxEventsQueued").setText ("" + this.maxEventsQueued));
            }

            if (this.annotation != null && this.annotation.length() > 0) {
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("annotation").setText (this.annotation));
            }
            
            if (this.condition != null) {
                result.addContent ("\n" + indentStr);
                result.addContent (this.condition.toElement ("condition", indent + 1).addContent ("\n" + indentStr));
            }
            
            if (this.action != null) {
                result.addContent ("\n" + indentStr);
                result.addContent (this.action.toElement ("action", indent + 1).addContent ("\n" + indentStr));
            }
            
        } else if (this.type == TYPE_SCHEDULE) {
            result = new Element ("schedule");
            
            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("scheduleId").setText (this.id));
            
            if (this.mode != SCHEDULE_MODE_NULL) {
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("mode").setText (SCHEDULE_MODE_LABELS[this.mode]));
            }
            
            if (this.startTime != null && this.startTime.length() > 0) {
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("startTime").setText (this.startTime));
            }
            
            if (this.schedulePeriod != null) {
                if (this.schedulePeriod.getPeriod() != CalendarPeriod.CALENDAR_PERIOD_TYPE_NULL) {
                    result.addContent ("\n" + indentStr);
                    result.addContent (new Element ("period").setText (CalendarPeriod.CALENDAR_PERIOD_TYPE_LABELS[this.schedulePeriod.getPeriod()]));
                }
                
                if (this.schedulePeriod.getCount() >= 0) {
                    result.addContent ("\n" + indentStr);
                    result.addContent (new Element ("count").setText ("" + this.schedulePeriod.getCount()));
                }
            }
            
            if (this.fromTimeInADay >= 0) {
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("fromTimeInADay").setText ("" + this.fromTimeInADay));
            }

            if (this.endTimeInADay >= 0) {
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("endTimeInADay").setText ("" + this.endTimeInADay));
            }

            if (this.recurringDay >= 0) {
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("recurringDay").setText ("" + this.recurringDay));
            }

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("isCluster").setText ("" + this.isCluster));
        }
        
        return result;
    }

    /**
     * <p>
     * Sets the <code>operation</code> field.
     * </p>
     * 
     * @param  operation  The operation to be performed on the archive.
     */
    public void setOperation (String operation) {
        this.operation = operation;
    }

    /**
     * <p>
     * Returns the value of the <code>operation</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getOperation () {
        return operation;
    }

    /**
     * <p>
     * Sets the <code>origId</code> field.
     * </p>
     * 
     * @param  origId  For edit/copy operations, this contains the original ID (for 
     *                 cases where the user wants to change the ID field.)
     */
    public void setOrigId (String origId) {
        this.origId = origId;
    }

    /**
     * <p>
     * Returns the value of the <code>origId</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getOrigId () {
        return origId;
    }

    /**
     * <p>
     * Sets the <code>id</code> field.
     * </p>
     * 
     * @param  id  The ID of the trigger record.
     */
    public void setId (String id) {
        this.id = id;
    }

    /**
     * <p>
     * Returns the value of the <code>id</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getId () {
        return id;
    }


    /**
     * <p>
     * Sets the <code>type</code> field.
     * </p>
     * 
     * @param  type  Indicates whether this object represents a trigger or trigger schedule.
     */
    public void setType (int type) {
        this.type = type;
    }

    /**
     * <p>
     * Returns the value of the <code>type</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public int getType () {
        return type;
    }

    /**
     * <p>
     * Sets the <code>resourcePath</code> field.
     * </p>
     * 
     * @param  resourcePath  The path to the trigger resource.
     */
    public void setResourcePath (String resourcePath) {
        this.resourcePath = resourcePath;
    }

    /**
     * <p>
     * Returns the value of the <code>resourcePath</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getResourcePath () {
        return resourcePath;
    }

    /**
     * <p>
     * Sets the <code>isEnabled</code> field.
     * </p>
     * 
     * @param  isEnabled  Indicates whether the trigger is enabled.
     */
    public void setIsEnabled (boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    /**
     * <p>
     * Returns the value of the <code>isEnabled</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public boolean isIsEnabled () {
        return isEnabled;
    }

    /**
     * <p>
     * Sets the <code>maxEventsQueued</code> field.
     * </p>
     * 
     * @param  maxEventsQueued  The maximum number of events to queue.
     */
    public void setMaxEventsQueued (int maxEventsQueued) {
        this.maxEventsQueued = maxEventsQueued;
    }

    /**
     * <p>
     * Returns the value of the <code>maxEventsQueued</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public int getMaxEventsQueued () {
        return maxEventsQueued;
    }

    /**
     * <p>
     * Sets the <code>annotation</code> field.
     * </p>
     * 
     * @param  annotation  The value of the trigger's annotation field.
     */
    public void setAnnotation (String annotation) {
        this.annotation = annotation;
    }

    /**
     * <p>
     * Returns the value of the <code>annotation</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getAnnotation () {
        return annotation;
    }

    /**
     * <p>
     * Sets the <code>condition</code> field.
     * </p>
     * 
     * @param  condition  The object representing the conditions under which the trigger will fire.
     */
    public void setCondition (Trigger.Condition condition) {
        this.condition = condition;
    }

    /**
     * <p>
     * Returns the value of the <code>condition</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public Trigger.Condition getCondition () {
        return condition;
    }

    /**
     * <p>
     * Sets the <code>action</code> field.
     * </p>
     * 
     * @param  action  The object representing the action to take when the trigger fires.
     */
    public void setAction (Trigger.Action action) {
        this.action = action;
    }

    /**
     * <p>
     * Returns the value of the <code>action</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public Trigger.Action getAction () {
        return action;
    }

    /**
     * <p>
     * Sets the <code>mode</code> field.
     * </p>
     * 
     * @param  mode  The trigger schedule's mode. See SCHEDULE_MODE_* constants.
     */
    public void setMode (int mode) {
        this.mode = mode;
    }

    /**
     * <p>
     * Returns the value of the <code>mode</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public int getMode () {
        return mode;
    }

    /**
     * <p>
     * Sets the <code>startTime</code> field.
     * </p>
     * 
     * @param  startTime  The start time of the trigger schedule in XML dateTime format.
     */
    public void setStartTime (String startTime) {
        this.startTime = startTime;
    }

    /**
     * <p>
     * Returns the value of the <code>startTime</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getStartTime () {
        return startTime;
    }

    /**
     * <p>
     * Sets the <code>schedulePeriod</code> field.
     * </p>
     * 
     * @param  schedulePeriod  The schedule period's unit of measure (minutes, days, etc.) and count.
     */
    public void setSchedulePeriod (CalendarPeriod schedulePeriod) {
        this.schedulePeriod = schedulePeriod;
    }

    /**
     * <p>
     * Returns the value of the <code>schedulePeriod</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public CalendarPeriod getSchedulePeriod () {
        return schedulePeriod;
    }

    /**
     * <p>
     * Sets the <code>fromTimeInADay</code> field.
     * </p>
     * 
     * @param  fromTimeInADay  The start of the recurring restriction window. In minutes from 
     *                         midnight, i.e.: 60 = 1AM, 720 = noon. Set to -1 when recurring
     *                         restriction is false.
     */
    public void setFromTimeInADay (int fromTimeInADay) {
        this.fromTimeInADay = fromTimeInADay;
    }

    /**
     * <p>
     * Returns the value of the <code>id</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public int getFromTimeInADay () {
        return fromTimeInADay;
    }

    /**
     * <p>
     * Sets the <code>endTimeInADay</code> field.
     * </p>
     * 
     * @param  endTimeInADay  The end of the recurring restriction window. In minutes from 
     *                        midnight, i.e.: 60 = 1AM, 720 = noon. Set to -1 when recurring
     *                        restriction is false.
     */
    public void setEndTimeInADay (int endTimeInADay) {
        this.endTimeInADay = endTimeInADay;
    }

    /**
     * <p>
     * Returns the value of the <code>endTimeInADay</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public int getEndTimeInADay () {
        return endTimeInADay;
    }

    /**
     * <p>
     * Sets the <code>recurringDay</code> field.
     * </p>
     * 
     * @param  recurringDay  Days in which the recurring window occurs. Set to -1 when recurring
     *                       restriction is false. Value is sum of bits representing days, i.e.
     *                       Sunday = 1, Monday = 2, Tuesday = 4, Wednesday = 8, etc. Sunday +
     *                       Tuesday + Wednesday = 13.
     */
    public void setRecurringDay (int recurringDay) {
        this.recurringDay = recurringDay;
    }

    /**
     * <p>
     * Returns the value of the <code>recurringDay</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public int getRecurringDay () {
        return recurringDay;
    }

    /**
     * <p>
     * Sets the <code>isCluster</code> field.
     * </p>
     * 
     * @param  isCluster  Indicates whether the trigger should fire only once per cluster (true) or that each
     *                    in the cluster should fire the trigger.
     */
    public void setIsCluster (boolean isCluster) {
        this.isCluster = isCluster;
    }

    /**
     * <p>
     * Returns the value of the <code>isCluster</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public boolean isIsCluster () {
        return isCluster;
    }

    /**
     * <p>
     * Bean object for cache storage targets. Intended to be serialized/deserialized by Jackson into/from JSON.
     * </p>
     *
     * @author Calvin Goodrich
     * @version 1.0
     */
    public static class Condition {
        public static final int TYPE_SYSTEM_EVENT = 0;
        public static final int TYPE_TIMER = 1;
        public static final int TYPE_JMS = 2;
        public static final int TYPE_USER_DEFINED = 3;
        public static final String[] TYPE_LABELS = {
            "System Event",
            "Timer",
            "JMS Event",
            "User Defined Event"
        };
        
        private static final String[] ELEMENT_NAMES = {
            "systemEvent",
            "timerEvent",
            "jmsEvent",
            "userDefinedEvent"
        };

        int type = 0;
        
        // system / user-defined event attributes
        //
        String eventName;
        
        // timer attributes
        //
        String scheduleId;
        
        // JMS attributes
        //
        String connector;
        String destination;
        String selector;
              
        /**
         * <p>
         * Constructor.
         * </p>
         */
        public Condition() {}

        /**
         * <p>
         * Constructor. Extracts attribute values from {@link Element} object.
         * </p>
         */
        public Condition (Element cNode) {
            for (Element ctNode : cNode.getChildren()) {
                for (int i = 0; i < ELEMENT_NAMES.length; i++) {
                    if (ctNode.getName().equals (ELEMENT_NAMES[i])) {
                        this.type = i;
                        break;
                    }
                }
    
                for (Element c : ctNode.getChildren()) {
                    if (c.getName().equals ("eventName"))
                        this.eventName = c.getText();
    
                    if (c.getName().equals ("scheduleId"))
                        this.scheduleId = c.getText();
                    
                    if (c.getName().equals ("connector"))
                        this.connector = c.getText();
                    
                    if (c.getName().equals ("destination"))
                        this.destination = c.getText();
                    
                    if (c.getName().equals ("selector"))
                        this.selector = c.getText();
                    
                }
            }
        }

        /**
         * <p>
         * Returns the object as a JDom Element.
         * </p>
         * 
         * @param  name    The name of the element to use.
         * @param  indent  The number of tabs (spaces) to indent the child elements.
         * @return         The value.
         */
        public Element toElement(
            String name,
            int indent
        ) {
            Element result = new Element (name);
            String indentStr = StringUtils.getIndent (indent);
            String indentStr2 = StringUtils.getIndent (indent + 1);

                Element tNode = new Element (ELEMENT_NAMES[this.type]);
                switch (this.type) {
                    case TYPE_SYSTEM_EVENT:
                    case TYPE_USER_DEFINED:
                        
                        tNode.addContent ("\n" + indentStr2);
                        tNode.addContent (new Element ("eventName").setText (this.eventName));
                        
                        break;
                    
                    case TYPE_TIMER:
                        
                        tNode.addContent ("\n" + indentStr2);
                        tNode.addContent (new Element ("scheduleId").setText (this.scheduleId));
                        
                        break;
        
                    case TYPE_JMS:
                        
                        tNode.addContent ("\n" + indentStr2);
                        tNode.addContent (new Element ("connector").setText (this.connector));
                        
                        tNode.addContent ("\n" + indentStr2);
                        tNode.addContent (new Element ("destination").setText (this.destination));
                        
                        tNode.addContent ("\n" + indentStr2);
                        tNode.addContent (new Element ("selector").setText (this.selector));
                        
                        break;
        
                }

                tNode.addContent ("\n" + indentStr);

            result.addContent ("\n" + indentStr);
            result.addContent (tNode);
    
            return result;
        }

        /**
         * <p>
         * Sets the <code>type</code> field.
         * </p>
         * 
         * @param  type  The condition type. See TYPE_* constants.
         */
        public void setType (int type) {
            this.type = type;
        }

        /**
         * <p>
         * Returns the value of the <code>type</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public int getType () {
            return type;
        }

        /**
         * <p>
         * Sets the <code>eventName</code> field.
         * </p>
         * 
         * @param  eventName  The name of the event for system or user-defined event conditions.
         */
        public void setEventName (String eventName) {
            this.eventName = eventName;
        }

        /**
         * <p>
         * Returns the value of the <code>eventName</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getEventName () {
            return eventName;
        }

        /**
         * <p>
         * Sets the <code>scheduleId</code> field.
         * </p>
         * 
         * @param  scheduleId  The schedule ID to use for timer conditions.
         */
        public void setScheduleId (String scheduleId) {
            this.scheduleId = scheduleId;
        }

        /**
         * <p>
         * Returns the value of the <code>scheduleId</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getScheduleId () {
            return scheduleId;
        }

        /**
         * <p>
         * Sets the <code>connector</code> field.
         * </p>
         * 
         * @param  connector  The JMS connector to monitor.
         */
        public void setConnector (String connector) {
            this.connector = connector;
        }

        /**
         * <p>
         * Returns the value of the <code>connector</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getConnector () {
            return connector;
        }

        /**
         * <p>
         * Sets the <code>destination</code> field.
         * </p>
         * 
         * @param  destination  The JMS destination to monitor.
         */
        public void setDestination (String destination) {
            this.destination = destination;
        }

        /**
         * <p>
         * Returns the value of the <code>destination</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getDestination () {
            return destination;
        }

        /**
         * <p>
         * Sets the <code>selector</code> field.
         * </p>
         * 
         * @param  selector  The JMS selector to monitor.
         */
        public void setSelector (String selector) {
            this.selector = selector;
        }

        /**
         * <p>
         * Returns the value of the <code>selector</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getSelector () {
            return selector;
        }
    }
    
    public static class Action {
        public static final int TYPE_PROCEDURE = 0;
        public static final int TYPE_STATISTICS = 1;
        public static final int TYPE_REINTROSPECT = 2;
        public static final int TYPE_EMAIL = 3;
        public static final String[] TYPE_LABELS = {
            "Execute Procedure",
            "Gather Statistics",
            "Reintrospect",
            "Send EMail"
        };

        private static final String[] ELEMENT_NAMES = {
            "executeProcedure",
            "gatherStatistics",
            "reintrospectDatasource",
            "sendEmail"
        };

        int type = 0;
        String resourcePath;
        String parameterValues;
        String emailTo;
        String emailCC;
        String emailBCC;
        String emailReplyTo;
        String emailSubject;
        String emailBody;
        boolean skipIfNoResults = true;
        boolean noCommit = true;
        boolean includeSummary = false;

        /**
         * <p>
         * Constructor.
         * </p>
         */
        public Action() {}

        /**
         * <p>
         * Constructor. Extracts attribute values from {@link Element} object.
         * </p>
         */
        public Action (Element aNode) {
            for (Element atNode : aNode.getChildren()) {
                for (int i = 0; i < ELEMENT_NAMES.length; i++) {
                    if (atNode.getName().equals (ELEMENT_NAMES[i])) {
                        this.type = i;
                        break;
                    }
                }
    
                for (Element c : atNode.getChildren()) {
                    if (c.getName().equals ("resourcePath"))
                        this.resourcePath = c.getText();
    
                    if (c.getName().equals ("parameterValues"))
                        this.parameterValues = c.getText();
                    
                    if (c.getName().equals ("emailTo"))
                        this.emailTo = c.getText();
                    
                    if (c.getName().equals ("emailCC"))
                        this.emailCC = c.getText();
                    
                    if (c.getName().equals ("emailBCC"))
                        this.emailBCC = c.getText();
                    
                    if (c.getName().equals ("emailReplyTo"))
                        this.emailReplyTo = c.getText();
                    
                    if (c.getName().equals ("emailSubject"))
                        this.emailSubject = c.getText();
                    
                    if (c.getName().equals ("emailBody"))
                        this.emailBody = c.getText();
                    
                    if (c.getName().equals ("skipIfNoResults"))
                        this.skipIfNoResults = (c.getText() == null) ? true : c.getText().matches ("(?i)^(yes|true|on|1)$");

                    if (c.getName().equals ("noCommit"))
                        this.noCommit = (c.getText() == null) ? true : c.getText().matches ("(?i)^(yes|true|on|1)$");

                    if (c.getName().equals ("includeSummary"))
                        this.includeSummary = (c.getText() == null) ? true : c.getText().matches ("(?i)^(yes|true|on|1)$");

                }
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
            String indentStr2 = StringUtils.getIndent (indent + 1);

                Element tNode = new Element (ELEMENT_NAMES[this.type]);

                tNode.addContent ("\n" + indentStr2);
                tNode.addContent (new Element ("resourcePath").setText (this.resourcePath));
                    
                switch (this.type) {
                    case TYPE_PROCEDURE:
                        
                        if (this.parameterValues != null && this.parameterValues.length() > 0) {
                            tNode.addContent ("\n" + indentStr2);
                            tNode.addContent (new Element ("parameterValues").setText (this.parameterValues));
                        }
                        
                        break;
                    
                    case TYPE_REINTROSPECT:
                        
                        tNode.addContent ("\n" + indentStr2);
                        tNode.addContent (new Element ("emailTo").setText (this.emailTo));
                        
                        if (this.emailCC != null && this.emailCC.length() > 0) {
                            tNode.addContent ("\n" + indentStr2);
                            tNode.addContent (new Element ("emailCC").setText (this.emailCC));
                        }
                        
                        if (this.emailBCC != null && this.emailBCC.length() > 0) {
                            tNode.addContent ("\n" + indentStr2);
                            tNode.addContent (new Element ("emailBCC").setText (this.emailBCC));
                        }
                        
                        if (this.emailReplyTo != null && this.emailReplyTo.length() > 0) {
                            tNode.addContent ("\n" + indentStr2);
                            tNode.addContent (new Element ("emailReplyTo").setText (this.emailReplyTo));
                        }
                        
                        if (this.emailSubject != null && this.emailSubject.length() > 0) {
                            tNode.addContent ("\n" + indentStr2);
                            tNode.addContent (new Element ("emailSubject").setText (this.emailSubject));
                        }
                        
                        if (this.emailBody != null && this.emailBody.length() > 0) {
                            tNode.addContent ("\n" + indentStr2);
                            tNode.addContent (new Element ("emailBody").setText (this.emailBody));
                        }
                        
                        tNode.addContent ("\n" + indentStr2);
                        tNode.addContent (new Element ("skipIfNoResults").setText ("" + this.skipIfNoResults));
                        
                        tNode.addContent ("\n" + indentStr2);
                        tNode.addContent (new Element ("noCommit").setText ("" + this.noCommit));
                        
                        break;
        
                    case TYPE_EMAIL:
                        
                        if (this.parameterValues != null && this.parameterValues.length() > 0) {
                            tNode.addContent ("\n" + indentStr2);
                            tNode.addContent (new Element ("parameterValues").setText (this.parameterValues));
                        }
                        
                        tNode.addContent ("\n" + indentStr2);
                        tNode.addContent (new Element ("emailTo").setText (this.emailTo));
                        
                        if (this.emailCC != null && this.emailCC.length() > 0) {
                            tNode.addContent ("\n" + indentStr2);
                            tNode.addContent (new Element ("emailCC").setText (this.emailCC));
                        }
                        
                        if (this.emailBCC != null && this.emailBCC.length() > 0) {
                            tNode.addContent ("\n" + indentStr2);
                            tNode.addContent (new Element ("emailBCC").setText (this.emailBCC));
                        }
                        
                        if (this.emailReplyTo != null && this.emailReplyTo.length() > 0) {
                            tNode.addContent ("\n" + indentStr2);
                            tNode.addContent (new Element ("emailReplyTo").setText (this.emailReplyTo));
                        }
                        
                        if (this.emailSubject != null && this.emailSubject.length() > 0) {
                            tNode.addContent ("\n" + indentStr2);
                            tNode.addContent (new Element ("emailSubject").setText (this.emailSubject));
                        }
                        
                        if (this.emailBody != null && this.emailBody.length() > 0) {
                            tNode.addContent ("\n" + indentStr2);
                            tNode.addContent (new Element ("emailBody").setText (this.emailBody));
                        }
                        
                        tNode.addContent ("\n" + indentStr2);
                        tNode.addContent (new Element ("skipIfNoResults").setText ("" + this.skipIfNoResults));
                        
                        tNode.addContent ("\n" + indentStr2);
                        tNode.addContent (new Element ("includeSummary").setText ("" + this.includeSummary));
                        
                        break;
        
                }

                tNode.addContent ("\n" + indentStr);

            result.addContent ("\n" + indentStr);
            result.addContent (tNode);
    
            return result;
        }

        /**
         * <p>
         * Sets the <code>type</code> field.
         * </p>
         * 
         * @param  type  The action type. See TYPE_* constants.
         */
        public void setType (int type) {
            this.type = type;
        }

        /**
         * <p>
         * Returns the value of the <code>type</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public int getType () {
            return type;
        }

        /**
         * <p>
         * Sets the <code>resourcePath</code> field.
         * </p>
         * 
         * @param  resourcePath  The resource path for procedure and reintrospection actions.
         */
        public void setResourcePath (String resourcePath) {
            this.resourcePath = resourcePath;
        }

        /**
         * <p>
         * Returns the value of the <code>resourcePath</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getResourcePath () {
            return resourcePath;
        }

        /**
         * <p>
         * Sets the <code>parameterValues</code> field.
         * </p>
         * 
         * @param  parameterValues  The comma separated list of arguments to the procedure of a procedure action. 
         *                          String values should be wrapped in single quotes. All others should be bare.
         */
        public void setParameterValues (String parameterValues) {
            this.parameterValues = parameterValues;
        }

        /**
         * <p>
         * Returns the value of the <code>parameterValues</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getParameterValues () {
            return parameterValues;
        }

        /**
         * <p>
         * Sets the <code>emailTo</code> field.
         * </p>
         * 
         * @param  emailTo  The TO: line for emails sent from reintrospection and send email actions.
         */
        public void setEmailTo (String emailTo) {
            this.emailTo = emailTo;
        }

        /**
         * <p>
         * Returns the value of the <code>emailTo</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getEmailTo () {
            return emailTo;
        }

        /**
         * <p>
         * Sets the <code>emailCC</code> field.
         * </p>
         * 
         * @param  emailCC  The CC: line for emails sent from reintrospection and send email actions.
         */
        public void setEmailCC (String emailCC) {
            this.emailCC = emailCC;
        }

        /**
         * <p>
         * Returns the value of the <code>id</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getEmailCC () {
            return emailCC;
        }

        /**
         * <p>
         * Sets the <code>emailBCC</code> field.
         * </p>
         * 
         * @param  emailBCC  The BCC: line for emails sent from reintrospection and send email actions.
         */
        public void setEmailBCC (String emailBCC) {
            this.emailBCC = emailBCC;
        }

        /**
         * <p>
         * Returns the value of the <code>emailBCC</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getEmailBCC () {
            return emailBCC;
        }

        /**
         * <p>
         * Sets the <code>emailReplyTo</code> field.
         * </p>
         * 
         * @param  emailReplyTo  The ReplyTo: line for emails sent from reintrospection and send email actions.
         */
        public void setEmailReplyTo (String emailReplyTo) {
            this.emailReplyTo = emailReplyTo;
        }

        /**
         * <p>
         * Returns the value of the <code>emailReplyTo</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getEmailReplyTo () {
            return emailReplyTo;
        }

        /**
         * <p>
         * Sets the <code>emailSubject</code> field.
         * </p>
         * 
         * @param  emailSubject  The subject for emails sent from reintrospection and send email actions.
         */
        public void setEmailSubject (String emailSubject) {
            this.emailSubject = emailSubject;
        }

        /**
         * <p>
         * Returns the value of the <code>emailSubject</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getEmailSubject () {
            return emailSubject;
        }

        /**
         * <p>
         * Sets the <code>emailBody</code> field.
         * </p>
         * 
         * @param  emailBody  The message content for emails sent from reintrospection and send email actions.
         */
        public void setEmailBody (String emailBody) {
            this.emailBody = emailBody;
        }

        /**
         * <p>
         * Returns the value of the <code>emailBody</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getEmailBody () {
            return emailBody;
        }

        /**
         * <p>
         * Sets the <code>skipIfNoResults</code> field.
         * </p>
         * 
         * @param  skipIfNoResults  Indicates that no email should be sent if the reintrospection does not result in any changes.
         */
        public void setSkipIfNoResults (boolean skipIfNoResults) {
            this.skipIfNoResults = skipIfNoResults;
        }

        /**
         * <p>
         * Returns the value of the <code>skipIfNoResults</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public boolean isSkipIfNoResults () {
            return skipIfNoResults;
        }

        /**
         * <p>
         * Sets the <code>noCommit</code> field.
         * </p>
         * 
         * @param  noCommit  Indicates that the reintrospection should not commit any discovered changes.
         */
        public void setNoCommit (boolean noCommit) {
            this.noCommit = noCommit;
        }

        /**
         * <p>
         * Returns the value of the <code>noCommit</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public boolean isNoCommit () {
            return noCommit;
        }

        /**
         * <p>
         * Sets the <code>includeSummary</code> field.
         * </p>
         * 
         * @param  includeSummary  Indicates that the send email action should include a summary.
         */
        public void setIncludeSummary (boolean includeSummary) {
            this.includeSummary = includeSummary;
        }

        /**
         * <p>
         * Returns the value of the <code>includeSummary</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public boolean isIncludeSummary () {
            return includeSummary;
        }
    }
}
