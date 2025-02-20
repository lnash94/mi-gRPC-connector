package org.wso2.carbon.payloadvconnector;

import org.apache.synapse.MessageContext;

// Import your generated classes:
import org.wso2.carbon.payloadvconnector.Person.HelloRequest;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.payloadvconnector.Person.HelloReply;

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
             HelloRequest request = HelloRequest.newBuilder()
                                                .setName(name)
                                                .build();

            // // 3. (Optional) If you have a gRPC client stub for your "Greeter" service:
            // //    Call the remote gRPC server to get a HelloReply.
            // //    Suppose you have a blocking stub:
            // //
            ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext() // For local testing or non-secure connections
                .build();

             GreeterGrpc.GreeterBlockingStub stub = GreeterGrpc.newBlockingStub(channel);
             HelloReply response = stub.sayHello(request);

            // // For demonstration, let’s just create a HelloReply ourselves:
            // HelloReply response = HelloReply.newBuilder()
            //                                 .setMessage("Hello, " + name + " from Synapse!")
            //                                 .build();

            // // 4. You can now serialize the HelloReply to a byte array, if needed:
            // String replyBytes = response.toString();

            // 5. Put the response back into the Synapse message context.
            //    For instance, set it as the message payload or as a property:
            context.setProperty("receivedName", response.getMessage());

            // Alternatively, you might transform it to JSON or XML for further Synapse flow:
            // String jsonPayload = "{\"message\": \"" + response.getMessage() + "\"}";
            // context.getEnvelope().getBody().getFirstElement().setText(jsonPayload);

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
