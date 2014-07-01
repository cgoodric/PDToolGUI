package com.cisco.dvbu.ps.deploytool.gui;

/*
 * (c) 2014 Cisco and/or its affiliates. All rights reserved.
 */

import com.bazaarvoice.dropwizard.assets.ConfiguredAssetsBundle;

//import com.yammer.dropwizard.assets.AssetsBundle;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;

//import com.sun.jersey.api.core.ResourceConfig;

import com.cisco.dvbu.ps.deploytool.gui.resources.ArchiveModuleResource;
import com.cisco.dvbu.ps.deploytool.gui.resources.ArchiveResource;
import com.cisco.dvbu.ps.deploytool.gui.resources.DataSourceModuleResource;
import com.cisco.dvbu.ps.deploytool.gui.resources.DataSourceResource;
import com.cisco.dvbu.ps.deploytool.gui.resources.DeploymentPlanResource;
import com.cisco.dvbu.ps.deploytool.gui.resources.DeploymentProfileResource;
import com.cisco.dvbu.ps.deploytool.gui.resources.ExecuteResource;
import com.cisco.dvbu.ps.deploytool.gui.resources.FileListResource;
import com.cisco.dvbu.ps.deploytool.gui.resources.FileResource;
import com.cisco.dvbu.ps.deploytool.gui.resources.GroupModuleResource;
import com.cisco.dvbu.ps.deploytool.gui.resources.GroupResource;
import com.cisco.dvbu.ps.deploytool.gui.resources.LogResource;
import com.cisco.dvbu.ps.deploytool.gui.resources.PreferenceResource;
import com.cisco.dvbu.ps.deploytool.gui.resources.PrivilegeModuleResource;
import com.cisco.dvbu.ps.deploytool.gui.resources.PrivilegeResource;
import com.cisco.dvbu.ps.deploytool.gui.resources.RebindModuleResource;
import com.cisco.dvbu.ps.deploytool.gui.resources.RebindResource;
import com.cisco.dvbu.ps.deploytool.gui.resources.RegressionModuleResource;
import com.cisco.dvbu.ps.deploytool.gui.resources.RegressionResource;
import com.cisco.dvbu.ps.deploytool.gui.resources.ResourceCacheModuleResource;
import com.cisco.dvbu.ps.deploytool.gui.resources.ResourceCacheResource;
import com.cisco.dvbu.ps.deploytool.gui.resources.ResourceModuleResource;
import com.cisco.dvbu.ps.deploytool.gui.resources.ResourceResource;
import com.cisco.dvbu.ps.deploytool.gui.resources.SecurityResource;
import com.cisco.dvbu.ps.deploytool.gui.resources.ServerAttributeModuleResource;
import com.cisco.dvbu.ps.deploytool.gui.resources.ServerAttributeResource;
import com.cisco.dvbu.ps.deploytool.gui.resources.ServerListResource;
import com.cisco.dvbu.ps.deploytool.gui.resources.ServerResource;
import com.cisco.dvbu.ps.deploytool.gui.resources.TriggerModuleResource;
import com.cisco.dvbu.ps.deploytool.gui.resources.TriggerResource;
import com.cisco.dvbu.ps.deploytool.gui.resources.UserModuleResource;
import com.cisco.dvbu.ps.deploytool.gui.resources.UserResource;
import com.cisco.dvbu.ps.deploytool.gui.resources.VCSInitResource;
import com.cisco.dvbu.ps.deploytool.gui.resources.VCSModuleResource;
import com.cisco.dvbu.ps.deploytool.gui.resources.VCSResource;
import com.yammer.metrics.core.PDToolGUIHealthCheck;
//import com.cisco.dvbu.ps.deploytool.gui.util.GenericExceptionMapper;
import com.cisco.dvbu.ps.deploytool.gui.util.JsonMappingExceptionHandler;

//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;

//import javax.ws.rs.ext.ExceptionMapper;

/**
 * <p>
 * The main class of the PDToolGUI web server.
 * </p>
 * 
 * @author Calvin Goodrich
 * @version 1.0
 */
public class PDToolGUIService extends Service<PDToolGUIConfiguration> {
    public static void main(String[] args) throws Exception {
        new PDToolGUIService().run(args);
    }

    @Override
    public void initialize(Bootstrap<PDToolGUIConfiguration> bootstrap) {
        bootstrap.setName ("PDToolGUI");

        // enable access to the flat web assets 
        //
        // use top call when in development
        // use bottom call when compiling for deployment
        // 
        // TODO: figure out how to enable access to these from the root (/) context
        //
        bootstrap.addBundle (new ConfiguredAssetsBundle ("/Users/cgoodric/dev/JDeveloper Workspaces 11.1.1/Composite/PDToolGUI/src/main/resources/web_assets/", "/pdtoolgui/"));
//        bootstrap.addBundle (new AssetsBundle ("/web_assets/", "/pdtoolgui/"));
    }

    @Override
    public void run (PDToolGUIConfiguration configuration, Environment environment) {
        //final String pdtgHome = configuration.getPdtgHome(); // just to have something in the config. doesn't do anything for now.
/*
        // Remove all of Dropwizard's custom ExceptionMappers
        ResourceConfig jrConfig = environment.getJerseyResourceConfig();
        Set<Object> dwSingletons = jrConfig.getSingletons();
        List<Object> singletonsToRemove = new ArrayList<Object>();

        for (Object s : dwSingletons) {
            if (s instanceof ExceptionMapper && s.getClass().getName().startsWith ("com.yammer.dropwizard.jersey.")) {
                singletonsToRemove.add(s);
            }
        }

        for (Object s : singletonsToRemove) {
            jrConfig.getSingletons().remove(s);
        }
*/
        // Register the custom ExceptionMapper(s)
        //
        environment.addResource (new JsonMappingExceptionHandler());
//        environment.addProvider (new GenericExceptionMapper());

        // enable the REST servlets
        //
        environment.addResource (new ArchiveModuleResource());
        environment.addResource (new ArchiveResource());
        environment.addResource (new DataSourceModuleResource());
        environment.addResource (new DataSourceResource());
        environment.addResource (new DeploymentPlanResource());
        environment.addResource (new DeploymentProfileResource());
        environment.addResource (new ExecuteResource());
        environment.addResource (new FileListResource());
        environment.addResource (new FileResource());
        environment.addResource (new GroupModuleResource());
        environment.addResource (new GroupResource());
        environment.addResource (new LogResource());
        environment.addResource (new PreferenceResource (configuration)); // passing in the configuration object to pick up preferences set in the .yml file.
        environment.addResource (new PrivilegeModuleResource());
        environment.addResource (new PrivilegeResource());
        environment.addResource (new RebindModuleResource());
        environment.addResource (new RebindResource());
        environment.addResource (new RegressionModuleResource());
        environment.addResource (new RegressionResource());
        environment.addResource (new ResourceCacheModuleResource());
        environment.addResource (new ResourceCacheResource());
        environment.addResource (new ResourceModuleResource());
        environment.addResource (new ResourceResource());
        environment.addResource (new SecurityResource());
        environment.addResource (new ServerAttributeModuleResource());
        environment.addResource (new ServerAttributeResource());
        environment.addResource (new ServerListResource());
        environment.addResource (new ServerResource());
        environment.addResource (new TriggerModuleResource());
        environment.addResource (new TriggerResource());
        environment.addResource (new UserModuleResource());
        environment.addResource (new UserResource());
        environment.addResource (new VCSInitResource());
        environment.addResource (new VCSModuleResource());
        environment.addResource (new VCSResource());
        environment.addHealthCheck (new PDToolGUIHealthCheck ("Health Check"));
    }

}