package com.cisco.dvbu.ps.deploytool.gui.util;

import java.util.List;

/**
 * <p>
 * Bean class used to hold column list index and sorting information
 * </p>
 * 
 * @author Calvin Goodrich
 * @version 1.0
 */
public class ListColumnInfo {
    private int index;
    private int sortType;
    
    /**
     * <p>
     * Constructor.
     * </p>
     */
    public ListColumnInfo() {}
    
    /**
     * <p>
     * Constructor.
     * </p>
     * 
     * @param index    The index in the column {@link List}
     * @param sortType Indicates the method used for sorting: {@link ListResultRowComparator#SORT_TYPE_STRING} or 
     *                 {@link ListResultRowComparator#SORT_TYPE_NUMBER}
     */
    public ListColumnInfo (
        int index,
        int sortType
    ) {
        this.index = index;
        this.sortType = sortType;
    }

    /**
     * <p>
     * Sets the <code>index</code> field.
     * </p>
     * 
     * @param  index  The index in the column {@link List}
     */
    public void setIndex (int index) {
        this.index = index;
    }

    /**
     * <p>
     * Returns the value of the <code>index</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public int getIndex () {
        return index;
    }

    /**
     * <p>
     * Sets the <code>sortType</code> field.
     * </p>
     * 
     * @param  sortType  Indicates the method used for sorting: {@link ListResultRowComparator#SORT_TYPE_STRING} or 
     *                   {@link ListResultRowComparator#SORT_TYPE_NUMBER}
     */
    public void setSortType (int sortType) {
        this.sortType = sortType;
    }

    /**
     * <p>
     * Returns the value of the <code>sortType</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public int getSortType () {
        return sortType;
    }
}
