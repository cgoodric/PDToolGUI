package com.cisco.dvbu.ps.deploytool.gui.util;

/*
 * (c) 2014 Cisco and/or its affiliates. All rights reserved.
 */

import java.util.List;

/**
 * <p>
 * Bean class for jqGrid lists. Intended to be serialized/deserialized by Jackson into/from JSON. It
 * also is used for populating parameter value pick lists in the UI.
 * </p>
 * <p>
 * This is the POJO representation of the JSON format that the jqGrid widget on the UI side expects.
 * </p>
 * 
 * @author Calvin Goodrich
 * @version 1.0
 */
public class ListResult {
    private int total;
    private int page;
    private int records;
    private List<Row> rows;
    private String param;
    
    /**
     * <p>
     * Constructor.
     * </p>
     */
    public ListResult () {}
    
    /**
     * <p>
     * Constructor.
     * </p>
     * 
     * @param total   Total number of pages available.
     * @param page    Page number of this result.
     * @param records Total number of records.
     * @param rows    List of rows constituting this page of results.
     * @param param   The parameter field name to populate in the UI.
     */
    public ListResult (
        int total,
        int page,
        int records,
        List<Row> rows,
        String param
    ) {
        this.total = total;
        this.page = page;
        this.records = records;
        this.rows = rows;
        this.param = param;
    }
    
    /**
     * <p>
     * Sets the <code>total</code> field.
     * </p>
     * 
     * @param  total  The total number of pages available.
     */
    public void setTotal (int total) {
        this.total = total;
    }
    
    /**
     * <p>
     * Returns the value of the <code>total</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public int getTotal () {
        return this.total;
    }
    
    /**
     * <p>
     * Sets the <code>page</code> field.
     * </p>
     * 
     * @param  page  The page number of this result.
     */
    public void setPage (int page) {
        this.page = page;
    }
    
    /**
     * <p>
     * Returns the value of the <code>page</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public int getPage() {
        return this.page;
    }
    
    /**
     * <p>
     * Sets the <code>records</code> field.
     * </p>
     * 
     * @param  records  The total number of records in this search result.
     */
    public void setRecords (int records) {
        this.records = records;
    }

    /**
     * <p>
     * Returns the value of the <code>page</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public int getRecords () {
        return records;
    }

    /**
     * <p>
     * Sets the <code>rows</code> field.
     * </p>
     * 
     * @param  rows  The {@link List} of rows constituting this page of results.
     */
    public void setRows (List<Row> rows) {
        this.rows = rows;
    }
    
    /**
     * <p>
     * Returns the value of the <code>rows</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public List<Row> getRows () {
        return this.rows;
    }

    /**
     * <p>
     * Sets the <code>param</code> field.
     * </p>
     * 
     * @param  param  The parameter field to populate with the values in this object.
     */
    public void setParam (String param) {
        this.param = param;
    }

    /**
     * <p>
     * Returns the value of the <code>param</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getParam () {
        return param;
    }

    /**
     * <p>
     * Bean object for a row of data in a server list. Intended to be serialized/deserialized by Jackson into/from JSON.
     * </p>
     * 
     * @author Calvin Goodrich
     * @version 1.0
     */
    public static class Row {
        private String id;
        private List<String> cell;
        
        /**
         * <p>
         * Constructor.
         * </p>
         */
        public Row () {}
        
        /**
         * <p>
         * Constructor.
         * </p>
         * 
         * @param id   The ID of the row.
         * @param cell The {@link List} of data in the row.
         */
        public Row (
            String id,
            List<String> cell
        ) {
            this.id = id;
            this.cell = cell;
        }
        
        /**
         * <p>
         * Sets the <code>id</code> field.
         * </p>
         * 
         * @param  id  The ID of the data constituting this row of the result list.
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
            return this.id;
        }
        
        /**
         * <p>
         * Sets the <code>cell</code> field.
         * </p>
         * 
         * @param  cell  The {@link List} of data constituting this row of the result list.
         */
        public void setCell (List<String> cell) {
            this.cell = cell;
        }
        
        /**
         * <p>
         * Returns the value of the <code>cell</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public List<String> getCell () {
            return this.cell;
        }

        /**
         * <p>
         * Returns the {@link String} value of this object. Useful for debugging.
         * </p>
         * 
         * @return     The value.
         */
        public String toString() {
            StringBuffer sb = new StringBuffer();
            
            sb.append ("id = " + this.id + "\n");
            sb.append ("cell = " + cell + "\n");
            
            return sb.toString();
        }
    }
}
