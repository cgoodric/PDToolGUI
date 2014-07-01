package com.cisco.dvbu.ps.deploytool.gui.util;

/*
 * (c) 2014 Cisco and/or its affiliates. All rights reserved.
 */

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import com.fasterxml.jackson.databind.JsonMappingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonMappingExceptionHandler implements ExceptionMapper<JsonMappingException> {
 
    private Logger logger = LoggerFactory.getLogger(JsonMappingExceptionHandler.class);
 
    public Response toResponse(JsonMappingException e) {
        String msg = e.getMessage();
        for (int i = 0; i < e.getStackTrace().length; i++) {
            msg += "\n" + e.getStackTrace()[i];
        }
        logger.error (msg);
        return Response.serverError().status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
    }
}