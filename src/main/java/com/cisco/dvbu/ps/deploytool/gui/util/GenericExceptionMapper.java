package com.cisco.dvbu.ps.deploytool.gui.util;

/*
 * (c) 2014 Cisco and/or its affiliates. All rights reserved.
 */

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("rawtypes")
public class GenericExceptionMapper implements ExceptionMapper {

    private Logger logger = LoggerFactory.getLogger(JsonMappingExceptionHandler.class);

    public GenericExceptionMapper () {
        super ();
    }

    public Response toResponse (Throwable throwable) {
        logger.debug(throwable.getMessage());
        return Response.serverError().status(Response.Status.BAD_REQUEST).entity(throwable.getMessage()).build();
    }
}
