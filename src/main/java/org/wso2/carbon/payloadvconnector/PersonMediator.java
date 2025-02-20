package org.wso2.carbon.payloadvconnector;

import helloworld.Person;
import io.grpc.Channel;
import org.apache.synapse.MessageContext;

// Import your generated classes:

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import org.wso2.carbon.connector.core.AbstractConnector;

public class PersonMediator extends AbstractConnector {
    private String name;
    private String message = "";

    public void setName(String gname) {
        name = gname;
    }
    public String getName() {
        return name;
    }

    public void setMessage(String mString) {
        message = mString;
    }
    public String getMessage() {
        return message;
    }
    @Override
    public void connect(MessageContext context) {
        try {
            // 1. Extract data from the Synapse message context (MC).
            //    For example, read a name from a property or the JSON/XML payload:
            log.info("i'm in...");
            String name = getName();
            log.info(name);

            if (name == null) {
                name = "lnash_defaultName";
            }

            // 2. Create a protobuf HelloRequest:
             Person.HelloRequest request = Person.HelloRequest.newBuilder()
                                                .setName(name)
                                                .build();

             helloworld.GreeterGrpc.GreeterBlockingStub stub = helloworld.GreeterGrpc.newBlockingStub((ManagedChannel) context
                     .getProperty("grpc_channel"));
             Person.HelloReply response = stub.sayHello(request);


            //TODO: Alternatively, you might transform it to JSON or XML for further Synapse flow:
             String jsonPayload = "{\"message\": \"" + response.getMessage() + "\"}";

             context.getEnvelope().getBody().getFirstElement().setText(jsonPayload);

            // return true; // mediation succeeded
            log.info("i'm out... byeee...");
            log.info(response.getMessage());
        } catch (Exception e) {
            // Log and handle error
            log.error("Error in PersonMediator: ", e);
            // return false; // mediation failed
        }
    }
}
