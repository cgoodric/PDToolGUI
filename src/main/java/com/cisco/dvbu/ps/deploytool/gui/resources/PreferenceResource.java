package com.cisco.dvbu.ps.deploytool.gui.resources;

import com.cisco.dvbu.ps.deploytool.gui.PDToolGUIConfiguration;
import com.cisco.dvbu.ps.deploytool.gui.core.config.prefs.Preferences;
import com.cisco.dvbu.ps.deploytool.gui.core.config.prefs.PreferencesDAO;
import com.cisco.dvbu.ps.deploytool.gui.core.config.prefs.PreferencesManager;

import com.cisco.dvbu.ps.deploytool.gui.util.ResultMessage;

import com.cisco.dvbu.ps.deploytool.gui.util.SecurityManager;

import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Servlet for working on UI preferences.
 * </p>
 * 
 * @author Calvin Goodrich
 * @version 1.0
 */
@Path("/prefs")
public class PreferenceResource {

    private static final Logger log = LoggerFactory.getLogger (PreferenceResource.class);
    PreferencesDAO dao = new PreferencesDAO();

    /**
     * <p>
     * Constructor.
     * </p>
     */
    public PreferenceResource (PDToolGUIConfiguration conf) {
        super ();

        PreferencesManager.init (conf);
    }

    /**
     * <p>
     * Returns the static preferences object as generated from the "pdtoolgui.yml" file found in $PDTOOL_HOME/gui.
     * </p>
     * 
     * @param  req  Servlet request object containing client request parameters.
     * @return      The requested {@link Preferences} as an object.
     * @see         PreferenceResource
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    public Preferences get(
        @Context HttpServletRequest req
    ) {
        SecurityManager.testAccess (req);

        return dao.get();
    }

    /**
     * <p>
     * Updates a the user's preferences.
     * </p>
     * 
     * @param  prefs    The {@link Preferences} object used to update the user's preferences.
     * @param  req      Servlet request object containing client request parameters.
     * @return          A {@link ResultMessage} object containing the results of the edit request. Serialized by Jackson into JSON.
     */
    @PUT
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public ResultMessage update (
        Preferences prefs,
        @Context HttpServletRequest req
    ) {
        SecurityManager.testAccess (req);

        return dao.edit (prefs);
    }
}
