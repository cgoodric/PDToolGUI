package com.cisco.dvbu.ps.deploytool.gui;

import com.bazaarvoice.dropwizard.assets.AssetsConfiguration;
import com.bazaarvoice.dropwizard.assets.AssetsBundleConfiguration;
import com.yammer.dropwizard.config.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;

public class PDToolGUIConfiguration extends Configuration 
implements AssetsBundleConfiguration // comment out when compiling for deployment
{
    @NotEmpty
    @JsonProperty
    private String pdtgHome = "C:/PDTool62/gui"; // not used. using apps.install.dir java property

    @NotNull
    @JsonProperty
    private int xmlIndentWidth = 4;

    @NotEmpty
    @JsonProperty
    private String defaultProfile = "C:/PDTool62/resources/conf/deploy.properties";

    @NotEmpty
    @JsonProperty
    private String defaultServer = "localhost";

    @NotEmpty
    @JsonProperty
    private String backupFiles = "true";
    
    @NotEmpty
    @JsonProperty
    private String restrictAccessToLocalhost = "true";

    @JsonProperty
    private final AssetsConfiguration assets = new AssetsConfiguration();

    public String getPdtgHome() {
        return pdtgHome;
    }

    public int getXmlIndentWidth() {
        return xmlIndentWidth;
    }

    public String getDefaultProfile() {
        return defaultProfile;
    }

    public String getDefaultServer() {
        return defaultServer;
    }

    public String getBackupFiles() {
        return backupFiles;
    }

    public String getRestrictAccessToLocalhost() {
        return restrictAccessToLocalhost;
    }

    public AssetsConfiguration getAssetsConfiguration() {
        return assets;
    }
}