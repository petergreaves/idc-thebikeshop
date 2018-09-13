package com.ibm.commerce.ue.rest;

import com.ibm.commerce.ue.rest.BaseResource;

import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;

/*
 * High-level Swagger Definitions used to display information in Swagger UI
 */

@SwaggerDefinition(
        info = @Info(
                description = "API Extensions for IBM Digital Commerce customizations.",
                version = "3.0",
                title = "IBM Digital Commerce API Extensions"
        )
)
public class SwaggerDefinitions extends BaseResource {}