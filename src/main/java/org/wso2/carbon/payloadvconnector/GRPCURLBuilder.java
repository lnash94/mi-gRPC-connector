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

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.apache.synapse.MessageContext;
import org.wso2.carbon.connector.core.AbstractConnector;

public class GRPCURLBuilder extends AbstractConnector {

    private static String server;
    private static int port;
    private static final String GRPC_CHANNEL = "grpc_channel";

    public static String getServer() {
        return server;
    }

    public static void setServer(String server) {
       GRPCURLBuilder.server = server;
    }

    public static int getPort() {
        return port;
    }

    public static void setPort(int port) {
        GRPCURLBuilder.port = port;
    }

    @Override
    public void connect(MessageContext messageContext) {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(getServer(), getPort())
                .usePlaintext() // For local testing or non-secure connections
                .build();
        messageContext.setProperty(GRPC_CHANNEL, channel);
    }
}
