package com.cisco.dvbu.ps.deploytool.gui.core.config.deployment_plan;

/*
 * (c) 2014 Cisco and/or its affiliates. All rights reserved.
 */

import java.util.List;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

/**
 * <p>
 * Bean object for deployment plans. Intended to be serialized/deserialized by Jackson into/from JSON.
 * </p>
 * 
 * @author Calvin Goodrich
 * @version 1.0
 */
public class DeploymentPlan {
    //private static final Logger log = LoggerFactory.getLogger (DeploymentPlan.class);
    
    private String path;
    private List<Step> steps;
    
    /**
     * <p>
     * Constructor.
     * </p>
     */
    public DeploymentPlan() {}

    /**
     * <p>
     * Sets the <code>path</code> field.
     * </p>
     * 
     * @param  path  The path to the plan file.
     */
    public void setPath (String path) {
        this.path = path;
    }

    /**
     * <p>
     * Returns the value of the <code>path</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getPath () {
        return path;
    }

    /**
     * <p>
     * Sets the <code>steps</code> field.
     * </p>
     * 
     * @param  steps  The list of execution plan steps.
     */
    public void setSteps (List<DeploymentPlan.Step> steps) {
        this.steps = steps;
    }

    /**
     * <p>
     * Returns the value of the <code>steps</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public List<DeploymentPlan.Step> getSteps () {
        return steps;
    }

    /**
     * <p>
     * Bean object for deployment plan steps. Intended to be serialized/deserialized by Jackson into/from JSON.
     * </p>
     * 
     * @author Calvin Goodrich
     * @version 1.0
     */
    public static class Step {
        private String lineNum; // jqGrid is putting "jqgNN" as rowids when new rows are added. not used in the plan file itself so doesn't matter.
        private boolean enabled;
        private boolean expectedBehavior;
        private boolean exitOnError;
        private String batchName;
        private String method;
        private String param0;
        private String param1;
        private String param2;
        private String param3;
        private String param4;
        private String param5;
        private String param6;
        private String param7;
        private String param8;
        private String param9;
    
        /**
         * <p>
         * Constructor.
         * </p>
         */
        public Step () {}
    
        /**
         * <p>
         * Sets the <code>lineNum</code> field.
         * </p>
         * 
         * @param  lineNum  The line number of the plan step.
         */
        public void setLineNum (String lineNum) {
            this.lineNum = lineNum;
        }
    
        /**
         * <p>
         * Returns the value of the <code>expectedBehavior</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getLineNum () {
            return lineNum;
        }
    
        /**
         * <p>
         * Sets the <code>enabled</code> field.
         * </p>
         * 
         * @param  enabled  Indicates whether the step is enabled or not.
         */
        public void setEnabled (boolean enabled) {
            this.enabled = enabled;
        }
    
        /**
         * <p>
         * Returns the value of the <code>expectedBehavior</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public boolean isEnabled () {
            return enabled;
        }
    
        /**
         * <p>
         * Sets the <code>expectedBehavior</code> field.
         * </p>
         * 
         * @param  expectedBehavior  The expected behavior of the plan step. Maps to PASS or FAIL.
         */
        public void setExpectedBehavior (boolean expectedBehavior) {
            this.expectedBehavior = expectedBehavior;
        }
    
        /**
         * <p>
         * Returns the value of the <code>expectedBehavior</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public boolean isExpectedBehavior () {
            return expectedBehavior;
        }
    
        /**
         * <p>
         * Sets the <code>exitOnError</code> field.
         * </p>
         * 
         * @param  exitOnError  Indicates whether to exit the deployment or not on error
         */
        public void setExitOnError (boolean exitOnError) {
            this.exitOnError = exitOnError;
        }
    
        /**
         * <p>
         * Returns the value of the <code>exitOnError</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public boolean isExitOnError () {
            return exitOnError;
        }
    
        /**
         * <p>
         * Sets the <code>batchName</code> field.
         * </p>
         * 
         * @param  batchName  The name of the script/procedure to execute. Never seen it set to anything other than "ExecuteAction".
         */
        public void setBatchName (String batchName) {
            this.batchName = batchName;
        }
    
        /**
         * <p>
         * Returns the value of the <code>batchName</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getBatchName () {
            return batchName;
        }
    
        /**
         * <p>
         * Sets the <code>method</code> field.
         * </p>
         * 
         * @param  method  The module's method to call.
         */
        public void setMethod (String method) {
            this.method = method;
        }
    
        /**
         * <p>
         * Returns the value of the <code>method</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getMethod () {
            return method;
        }
    
        /**
         * <p>
         * Sets the <code>param0</code> field.
         * </p>
         * 
         * @param  param0  The zero-th parameter to the module's method.
         */
        public void setParam0 (String param0) {
            this.param0 = param0;
        }
    
        /**
         * <p>
         * Returns the value of the <code>param0</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getParam0 () {
            return param0;
        }
        /**
         * <p>
         * Sets the <code>param1</code> field.
         * </p>
         * 
         * @param  param1  The first parameter to the module's method.
         */
        public void setParam1 (String param1) {
            this.param1 = param1;
        }
    
        /**
         * <p>
         * Returns the value of the <code>param1</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getParam1 () {
            return param1;
        }
    
        /**
         * <p>
         * Sets the <code>param2</code> field.
         * </p>
         * 
         * @param  param2  The second parameter to the module's method.
         */
        public void setParam2 (String param2) {
            this.param2 = param2;
        }
    
        /**
         * <p>
         * Returns the value of the <code>param2</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getParam2 () {
            return param2;
        }
    
        /**
         * <p>
         * Sets the <code>param3</code> field.
         * </p>
         * 
         * @param  param3  The third parameter to the module's method.
         */
        public void setParam3 (String param3) {
            this.param3 = param3;
        }
    
        /**
         * <p>
         * Returns the value of the <code>param3</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getParam3 () {
            return param3;
        }
    
        /**
         * <p>
         * Sets the <code>param4</code> field.
         * </p>
         * 
         * @param  param4  The fourth parameter to the module's method.
         */
        public void setParam4 (String param4) {
            this.param4 = param4;
        }
    
        /**
         * <p>
         * Returns the value of the <code>param4</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getParam4 () {
            return param4;
        }
    
        /**
         * <p>
         * Sets the <code>param5</code> field.
         * </p>
         * 
         * @param  param5  The fifth parameter to the module's method.
         */
        public void setParam5 (String param5) {
            this.param5 = param5;
        }
    
        /**
         * <p>
         * Returns the value of the <code>param5</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getParam5 () {
            return param5;
        }
    
        /**
         * <p>
         * Sets the <code>param6</code> field.
         * </p>
         * 
         * @param  param6  The sixth parameter to the module's method.
         */
        public void setParam6 (String param6) {
            this.param6 = param6;
        }
    
        /**
         * <p>
         * Returns the value of the <code>param6</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getParam6 () {
            return param6;
        }

        /**
         * <p>
         * Sets the <code>param7</code> field.
         * </p>
         * 
         * @param  param7  The seventh parameter to the module's method.
         */
        public void setParam7 (String param7) {
            this.param7 = param7;
        }

        /**
         * <p>
         * Returns the value of the <code>param7</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getParam7 () {
            return param7;
        }

        /**
         * <p>
         * Sets the <code>param8</code> field.
         * </p>
         * 
         * @param  param8  The eighth parameter to the module's method.
         */
        public void setParam8 (String param8) {
            this.param8 = param8;
        }

        /**
         * <p>
         * Returns the value of the <code>param8</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getParam8 () {
            return param8;
        }

        /**
         * <p>
         * Sets the <code>param9</code> field.
         * </p>
         * 
         * @param  param9  The ninth parameter to the module's method.
         */
        public void setParam9 (String param9) {
            this.param9 = param9;
        }

        /**
         * <p>
         * Returns the value of the <code>param9</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getParam9 () {
            return param9;
        }
    }
}
