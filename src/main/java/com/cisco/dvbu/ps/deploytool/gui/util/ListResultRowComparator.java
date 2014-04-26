package com.cisco.dvbu.ps.deploytool.gui.util;

import java.util.Comparator;
import java.util.List;


/**
 * <p>
 * Custom RowComparator class for sorting {@link ListResult.Row} objects in a {@link List}.
 * </p>
 *
 * @author Calvin Goodrich
 * @version 1.0
 */
public class ListResultRowComparator implements Comparator<ListResult.Row> { // specify a type so the compiler knows what's being compared and the compare() method can be properly overridden with a specific class.
    private int index;
    private int direction; // 1 = ascending, -1 = descending
    private int sortType;  // currently strings and numbers supported.

    /**
     * Indicates a {@link String} sort.
     */
    public static final int SORT_TYPE_STRING = 1;
    
    /**
     * Indicates a numeric sort.
     */
    public static final int SORT_TYPE_NUMBER = 2;
    
    /**
     * <p>
     * Constructor. Defaults to an ascending {@link String} sort of index 0. 
     * </p>
     * @see #ListResultRowComparator(int, String, int)
     */
    public ListResultRowComparator() {
        this.index = 0;
        this.direction = 1;
        this.sortType = SORT_TYPE_STRING;
    }
    
    /**
     * <p>
     * Constructor.
     * </p>
     * @param index     Indicates the index (0-based) of the cell {@link List} to sort on.
     * @param direction Indicates the direction of the sort: "asc" or "desc". If not one of these two values, then defaults to "asc".
     * @param sortType  Indicates the type of sort to perform: {@link #SORT_TYPE_STRING} or {@link #SORT_TYPE_NUMBER}.
     */
    public ListResultRowComparator(
        int index,
        String direction,
        int sortType
    ) {
        this.index = index;
        this.direction = (direction != null && direction.equals("desc")) ? -1 : 1;
        this.sortType = sortType;
    }

    /**
     * <p>
     * Performs the comparison of {@link ListResult.Row} objects.
     * </p>
     * @param  row1 First {@link ListResult.Row} object to compare.
     * @param  row2 Second {@link ListResult.Row} object to compare.
     * @return     -1 if row1 is "less than" row2, 1 if row1 is "greater than" row2, and 0 if they are "equal".
     */
    @Override
    public int compare (ListResult.Row row1, ListResult.Row row2) {

        String val1 = row1.getCell().get (this.index);
        String val2 = row2.getCell().get (this.index);
        
        // can't sort null values
        //
        val1 = (val1 == null) ? "" : val1;
        val2 = (val2 == null) ? "" : val2;

        if (this.sortType == SORT_TYPE_NUMBER) {
            try {
                int n1 = Integer.parseInt (val1);
                int n2 = Integer.parseInt (val2);

                return ((n1 < n2) ? -1 : (n1 > n2) ? 1 : 0) * direction;
            } catch (Exception e) { // if either parse fails, compare as strings.
                return val1.compareToIgnoreCase (val2) * direction;
            }
        } else {
            return val1.compareToIgnoreCase (val2) * direction; 
        }
    }

    /**
     * <p>
     * Sets the <code>index</code> field.
     * </p>
     * 
     * @param  index  The index (0-based) of the cell {@link List} to sort on.
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
    public int getIndex() {
        return index;
    }

    /**
     * <p>
     * Sets the <code>direction</code> field.
     * </p>
     * 
     * @param  direction  The direction of the sort: "asc" or "desc". If not one of these or is null then
     *                    the direction will be set to "asc".
     */
    public void setDirection (String direction) {
        if (direction == null) {
            this.direction = 1;
            return;
        }

        if (direction.equalsIgnoreCase ("desc"))
            this.direction = -1;
        else
            this.direction = 1;
    }

    /**
     * <p>
     * Returns the value of the <code>direction</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getDirection() {
        if (this.direction == 1)
            return "asc";
        else
            return "desc";
    }

    /**
     * <p>
     * Sets the <code>sortType</code> field.
     * </p>
     * 
     * @param  sortType  The type of sort to perform: {@link #SORT_TYPE_STRING} or {@link #SORT_TYPE_NUMBER}.
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
    public int getSortType() {
        return sortType;
    }
}
