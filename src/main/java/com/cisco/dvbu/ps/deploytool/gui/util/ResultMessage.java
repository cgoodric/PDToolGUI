package com.cisco.dvbu.ps.deploytool.gui.util;

/*
 * (c) 2014 Cisco and/or its affiliates. All rights reserved.
 */

import java.util.List;

/**
 * <p>
 * Bean object for server CRUD operation results. Intended to be serialized/deserialized by Jackson into/from JSON.
 * Can be used to serialize validation errors or the results on operations across multiple servers (e.g. copy and delete.)
 * </p>
 * 
 * @author Calvin Goodrich
 * @version 1.0
 */
public class ResultMessage {
    
    private String status;
    private String message;
    private List<MessageItem> messagelist;

    /**
     * <p>
     * Constructor.
     * </p>
     */
    public ResultMessage () {}

    /**
     * <p>
     * Constructor.
     * </p>
     * 
     * @param status      An indicator of success or failure.
     * @param message     A single message for the request.
     * @param messagelist A {@link List} of message results when a single message isn't sufficient.
     */
    public ResultMessage (
        String status,
        String message,
        List<MessageItem> messagelist
    ) {
        this.status = status;
        this.message = message;
        this.messagelist = messagelist;
    }

    /**
     * <p>
     * Sets the <code>status</code> field.
     * </p>
     * 
     * @param  status  The status of the request. Typically "success" or "error".
     */
    public void setStatus (String status) {
        this.status = status;
    }

    /**
     * <p>
     * Returns the value of the <code>status</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getStatus () {
        return status;
    }

    /**
     * <p>
     * Sets the <code>message</code> field.
     * </p>
     * 
     * @param  message  The message for the result of the request.
     */
    public void setMessage (String message) {
        this.message = message;
    }

    /**
     * <p>
     * Returns the value of the <code>message</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getMessage () {
        return message;
    }

    /**
     * <p>
     * Sets the <code>messagelist</code> field.
     * </p>
     * 
     * @param  messagelist  When a single message is not sufficient, this field is used to set a {@link List} of messages.
     */
    public void setMessagelist (List<MessageItem> messagelist) {
        this.messagelist = messagelist;
    }

    /**
     * <p>
     * Returns the value of the <code>messagelist</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public List<MessageItem> getMessagelist () {
        return messagelist;
    }
    
    /**
     * <p>
     * Bean object for a message row in a message list. Intended to be serialized/deserialized by Jackson into/from JSON.
     * </p>
     * <p>
     * This is used when validation fails and multiple errors need to be reported back to a user, or when a CRUD operation
     * occurs involving multiple objects and individual results need to be reported.
     * </p>
     * 
     * @author Calvin Goodrich
     * @version 1.0
     */
    public static class MessageItem {
        private String field;
        private String message;
        
        /**
         * <p>
         * Constructor.
         * </p>
         */
        public MessageItem() {}
        
        /**
         * <p>
         * Constructor.
         * </p>
         * 
         * @param field   The field name of the object.
         * @param message The message pertaining to the field.
         */
        public MessageItem (
            String field,
            String message
        ) {
            this.field = field;
            this.message = message;
        }

        /**
         * <p>
         * Sets the <code>field</code> field.
         * </p>
         * 
         * @param  field  The ID of the data constituting this row of the result list.
         */
        public void setField (String field) {
            this.field = field;
        }

        /**
         * <p>
         * Returns the value of the <code>field</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getField () {
            return field;
        }

        /**
         * <p>
         * Sets the <code>message</code> field.
         * </p>
         * 
         * @param  message  The ID of the data constituting this row of the result list.
         */
        public void setMessage (String message) {
            this.message = message;
        }

        /**
         * <p>
         * Returns the value of the <code>message</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getMessage () {
            return message;
        }
    }
}
