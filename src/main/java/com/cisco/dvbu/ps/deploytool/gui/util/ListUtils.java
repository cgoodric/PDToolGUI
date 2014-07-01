package com.cisco.dvbu.ps.deploytool.gui.util;

/*
 * (c) 2014 Cisco and/or its affiliates. All rights reserved.
 */

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

/**
 * <p>
 * Utility class used for working with the jQuery list object.
 * </p>
 * 
 * @author Calvin Goodrich
 * @version 1.0
 */
public class ListUtils {

//    private static final Logger log = LoggerFactory.getLogger (ListUtils.class);

    // prevent unintentional object instantiations
    //
    private ListUtils () {}

    /**
     * <p>
     * Applies search rules to a result list.
     * </p>
     * 
     * @param inList           The list of rows to search.
     * @param listColumnsInfo  A map of the columns in the table and their search capabilities.
     * @param searchField      Search field to search apply search rules to.
     * @param searchString     String to search for
     * @param searchOper       Search operation (equals or "eq", not equals or "ne", etc.)
     * @return                 The requested list of items.
     */
    public static List<ListResult.Row> applySearchRules (
        List<ListResult.Row> inList,
        Map<String, ListColumnInfo> listColumnsInfo,
        String searchField,
        String searchString,
        String searchOper
    ) {
        List<ListResult.Row> result = new ArrayList<ListResult.Row>();
        Iterator<ListResult.Row> itr = inList.iterator();
        
        // if the search field is null or isn't found in the known list of columns
        //
        if (searchField == null || listColumnsInfo.get (searchField) == null)
            return inList;
        
        // if the search string is null set it to the empty string
        //
        if (searchString == null)
            searchString = "";
        
        int searchIndex = listColumnsInfo.get (searchField).getIndex();
        searchString = searchString.trim();
        
        while (itr.hasNext()) {
            ListResult.Row r = itr.next();
            String value = r.getCell().get (searchIndex);
            
            value = (value == null) ? "" : value.trim();
            
            if (searchOper.equalsIgnoreCase ("eq")) { // equals
                if (value.equalsIgnoreCase (searchString))
                    result.add (r);
            } else if (searchOper.equalsIgnoreCase ("ne")) { // not equals
                if (! value.equalsIgnoreCase (searchString))
                    result.add (r);
            } else if (searchOper.equalsIgnoreCase ("bw")) { // begins with
                if (value.matches ("(?i)\\Q" + searchString + "\\E.*"))
                    result.add (r);
            } else if (searchOper.equalsIgnoreCase ("bn")) { // does not begin with
                if (! value.matches ("(?i)\\Q" + searchString + "\\E.*"))
                    result.add (r);
            } else if (searchOper.equalsIgnoreCase ("ew")) { // ends with
                if (value.matches ("(?i).*\\Q" + searchString + "\\E"))
                    result.add (r);
            } else if (searchOper.equalsIgnoreCase ("en")) { // does not end with
                if (! value.matches ("(?i).*\\Q" + searchString + "\\E"))
                    result.add (r);
            } else if (searchOper.equalsIgnoreCase ("cn")) { // contains
                if (value.matches ("(?i).*\\Q" + searchString + "\\E.*"))
                    result.add (r);
            } else if (searchOper.equalsIgnoreCase ("nc")) { // does not contain
                if (! value.matches ("(?i).*\\Q" + searchString + "\\E.*"))
                    result.add (r);
            } else if (searchOper.equalsIgnoreCase ("nu")) { // is null
                if (value == null)
                    result.add (r);
            } else if (searchOper.equalsIgnoreCase ("nn")) { // is not null
                if (value != null)
                    result.add (r);
            } else if (searchOper.equalsIgnoreCase ("in")) { // is in
                String[] searchKeys = searchString.split ("\\s*,\\s*");

                for (int i = 0; i < searchKeys.length; i++) {
                    if (value.equalsIgnoreCase (searchKeys[i])) {
                        result.add(r);
                        break;
                    }
                }
            } else if (searchOper.equalsIgnoreCase ("ni")) { // is not in
                String[] searchKeys = searchString.split ("\\s*,\\s*");
                boolean found = false;

                for (int i = 0; i < searchKeys.length; i++) {
                    if (value.equalsIgnoreCase (searchKeys[i])) {
                        found = true;
                        break;
                    }
                }
                
                if (! found)
                    result.add(r);
            } else {
                result.add(r);
            }
        }

        return result;
    }
    
    /**
     * <p>
     * Gets a subset of results based on an offset and page size
     * </p>
     * 
     * @param inList   The complete list of rows.
     * @param numRows  The number of rows in a page.
     * @param pageNum  The page number to return.
     * @return         The requested page of items.
     */
    public static List<ListResult.Row> getPage (
        List<ListResult.Row> inList,
        int numRows,
        int pageNum
    ) {
        int listLen = inList.size();
        int firstIncl = (pageNum - 1) * numRows;
        int lastExcl = (pageNum * numRows <= listLen) ? pageNum * numRows : listLen;;
        
        return inList.subList (firstIncl, lastExcl);
    }
    
    /**
     * <p>
     * Constructs a {@link ListResult} return object based on the filtered and paginated list
     * plus the number of rows per page and the page number.
     * </p>
     * 
     * @param resultList  The filtered and paginated rows.
     * @param numRows     The number of rows in a page.
     * @param pageNum     The page number to return.
     * @param param       The parameter field in the UI to populate with the values in the returned {@link ListResult} object.
     * @return            The requested page of items in the object format the jqGrid widget is expecting. This
     *                    object is automatically converted to JSON by Jackson as it is returned by the appropriate servlet.
     */
    public static ListResult getListResult (
        List<ListResult.Row> resultList,
        int numRows,
        int pageNum,
        String param
    ) {
        // get total number of records in sorted/filtered result
        //
        int totalRecords = resultList.size();
        
        // figure out the total number of pages
        //
        int totalPages = (int)(totalRecords / numRows);
        if (totalRecords % numRows > 0)
            totalPages++;

        // get page subset
        //
        if (numRows > 0 && pageNum > 0) {

            // make sure starting index is within the bounds of the list by resetting the requested page number to the last page.
            // this can happen when the user searches or filters data and the display is on the last page.
            //
            if ((pageNum - 1) * numRows > resultList.size() - 1)
                pageNum = (int)(resultList.size() / numRows) + 1;

            resultList = ListUtils.getPage (
                             resultList, 
                             numRows, 
                             pageNum
                         );
        }

        return new ListResult (
                       totalPages, 
                       pageNum, 
                       totalRecords, 
                       resultList,
                       param
                   );
    }
}
