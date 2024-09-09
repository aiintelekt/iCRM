/**
 * 
 */
package org.fio.homeapps.filter;

import java.io.IOException;

import org.fio.homeapps.constants.GlobalConstants;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

/**
 * @author Sharif
 *
 */
public class CORSFilter implements ContainerResponseFilter {

    @Override
    public ContainerResponse filter(ContainerRequest request, ContainerResponse response) {
        response.getHttpHeaders().add("Access-Control-Allow-Origin", GlobalConstants.ALLOW_ORIGIN_ALL);
        response.getHttpHeaders().add("Access-Control-Allow-Headers", "CSRF-Token, X-Requested-By, Authorization, Content-Type, Accept, Accept-Language, X-Authorization, apiUser, apiKey");
        response.getHttpHeaders().add("Access-Control-Allow-Credentials", "true");
        response.getHttpHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD, PATCH");
        return response;
    }
    
}
