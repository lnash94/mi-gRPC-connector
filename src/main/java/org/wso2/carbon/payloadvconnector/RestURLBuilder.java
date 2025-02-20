/*
 *  Copyright (c) 2024, WSO2 LLC. (https://www.wso2.com).
 *
 *  WSO2 LLC. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.wso2.carbon.payloadvconnector;

import org.apache.commons.lang3.StringUtils;
import org.apache.synapse.MessageContext;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.ConnectException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class RestURLBuilder extends AbstractConnector {

    private static final String encoding = "UTF-8";
    private static final String URL_PATH = "uri.var.urlPath";
    private static final String URL_QUERY = "uri.var.urlQuery";
    private static final String PROPERTY_ERROR_CODE = "ERROR_CODE";
    private static final String PROPERTY_ERROR_MESSAGE = "ERROR_MESSAGE";
    private static final String GENERAL_ERROR_MSG = "payloadv connector encountered an error: ";

    private String operationPath = "";
    private String pathParameters = "";
    private String queryParameters = "";

    public static final Map<String, String> parameterNameMap = new HashMap<String, String>() {{

            put("id", "id");
    }};

    static class ErrorCodes {

            public static final String INVALID_CONFIG = "701102";
            public static final String GENERAL_ERROR = "701101";
        }

    public String getOperationPath() {

        return operationPath;
    }

    public void setOperationPath(String operationPath) {

        this.operationPath = operationPath;
    }

    public String getPathParameters() {

        return pathParameters;
    }

    public void setPathParameters(String pathParameters) {

        this.pathParameters = pathParameters;
    }

    public String getQueryParameters() {

        return queryParameters;
    }

    public void setQueryParameters(String queryParameters) {

        this.queryParameters = queryParameters;
    }

    @Override
    public void connect(MessageContext messageContext) throws ConnectException {

        try {
            String urlPath = getOperationPath();
            if (StringUtils.isNotEmpty(this.pathParameters)) {
                String[] pathParameterList = getPathParameters().split(",");
                for (String pathParameter : pathParameterList) {
                    String name = parameterNameMap.get(pathParameter);
                    String paramValue = (String) getParameter(messageContext, pathParameter);
                    if (StringUtils.isNotEmpty(paramValue)) {
                        String encodedParamValue = URLEncoder.encode(paramValue, encoding);
                        urlPath = urlPath.replace("{" + name + "}", encodedParamValue);
                    } else {
                        String errorMessage = GENERAL_ERROR_MSG + "Mapping parameter '" + pathParameter + "' is not set.";
                        setErrorPropertiesToMessage(messageContext, ErrorCodes.INVALID_CONFIG, errorMessage);
                        handleException(errorMessage, messageContext);
                    }
                }
            }

            StringJoiner urlQuery = new StringJoiner("&");
            urlQuery.add("?");
            if (StringUtils.isNotEmpty(this.queryParameters)) {
                String[] queryParameterList = getQueryParameters().split(",");
                for (String queryParameter : queryParameterList) {
                    String paramValue = (String) getParameter(messageContext, queryParameter);
                    if (StringUtils.isNotEmpty(paramValue)) {
                        String name = parameterNameMap.get(queryParameter);
                        String encodedParamValue = URLEncoder.encode(paramValue, encoding);
                        urlQuery.add(name + "=" + encodedParamValue);
                    }
                }
            }

            messageContext.setProperty(URL_PATH, urlPath);
            messageContext.setProperty(URL_QUERY, urlQuery.toString());

        } catch (UnsupportedEncodingException e) {
            String errorMessage = GENERAL_ERROR_MSG + "Error occurred while constructing the URL query.";
            setErrorPropertiesToMessage(messageContext, ErrorCodes.GENERAL_ERROR, errorMessage);
            handleException(errorMessage, messageContext);
        }
    }

    private void setErrorPropertiesToMessage(MessageContext messageContext, String errorCode, String errorMessage) {

        messageContext.setProperty(PROPERTY_ERROR_CODE, errorCode);
        messageContext.setProperty(PROPERTY_ERROR_MESSAGE, errorMessage);
    }
}
