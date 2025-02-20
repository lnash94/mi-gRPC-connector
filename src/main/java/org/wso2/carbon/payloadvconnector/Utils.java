/**
 * Copyright (c) 2025, WSO2 LLC. (https://www.wso2.com).
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.payloadvconnector;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPBody;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.apache.synapse.commons.json.JsonUtil;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.transport.passthru.PassThroughConstants;

import com.google.gson.JsonObject;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

// Generated on $timestamp

/**
 * The Utils class contains all the utils methods related to the connector.
 */
public class Utils {

    private static final Log log = LogFactory.getLog(Utils.class);

    public static void populateOperationResult(MessageContext messageContext, OMElement payload, String targetVariable,
                        JsonObject attributes, boolean replaceBody) {

        Map < String, Object > results = new HashMap < > ();
        results.put("payload", payload);
        results.put("attributes", attributes);
        messageContext.setVariable(targetVariable, results);
        if (replaceBody) {
            SOAPBody soapBody = messageContext.getEnvelope().getBody();
            //Detaching first element (soapBody.getFirstElement().detach()) will be done by following method anyway.
            JsonUtil.removeJsonPayload(((Axis2MessageContext) messageContext).getAxis2MessageContext());
            ((Axis2MessageContext) messageContext).getAxis2MessageContext().
                    removeProperty(PassThroughConstants.NO_ENTITY_BODY);
            soapBody.addChild(payload);
        }
    }

    /**
     * Sets the error code and error message in message context.
     *
     * @param messageContext Message Context
     * @param errorCode      Error Code
     * @param errorMessage   Error Message
     */
    public static void setErrorPropertiesToMessage(MessageContext messageContext, String errorCode, String errorMessage) {

        messageContext.setProperty("ERROR_CODE", errorCode);
        messageContext.setProperty("ERROR_MESSAGE", errorMessage);
    }

    /**
     * Encode the client credentials using Base64.
     *
     * @param clientCredentials Client Credentials
     * @return Encoded Client Credentials
     */
    public static String encode(String clientCredentials) {
        try {
            // Encoding the client credentials using Base64
            return Base64.getEncoder().encodeToString(clientCredentials.getBytes("UTF-8"));
        } catch (Exception e) {
            // Handle the exception appropriately (logging, rethrowing, etc.)
            throw new RuntimeException("Error encoding client credentials.", e);
        }
    }
}
