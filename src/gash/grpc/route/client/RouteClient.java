package gash.grpc.route.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.google.protobuf.ByteString;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import route.Route;
import route.RouteServiceGrpc;

/**
 * copyright 2021, gash
 *
 * Gash licenses this file to you under the Apache License, version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

public class RouteClient {
    private static long clientID = 501;
    private static int port = 2345;

    private static final Route constructMessage(int mID, String path, String payload) {
        Route.Builder bld = Route.newBuilder();
        bld.setId(mID);
        bld.setOrigin(RouteClient.clientID);
        bld.setPath(path);

        byte[] hello = payload.getBytes();
        bld.setPayload(ByteString.copyFrom(hello));

        return bld.build();
    }

    private static final void response(Route reply) {
        // TODO handle the reply/response from the server
        var payload = new String(reply.getPayload().toByteArray());
        System.out.println("reply: " + reply.getId() + ", from: " + reply.getOrigin() + ", payload: " + payload);
    }

    private static final void doRequest(ManagedChannel channel) {
        RouteServiceGrpc.RouteServiceBlockingStub stub = RouteServiceGrpc.newBlockingStub(channel);
        final int I = 1;
        for (int i = 0; i < I; i++) {
            var msg = RouteClient.constructMessage(i, "/to/somewhere", "hello");

            // blocking!
            var r = stub.request(msg);
            response(r);

        }

        channel.shutdown();
    }

    private static final void doStreamRequest(ManagedChannel channel) throws IOException, InterruptedException {
        System.out.println("Enter doStreamRequest");
        RouteServiceGrpc.RouteServiceStub stub = RouteServiceGrpc.newStub(channel);
        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<Route> stream = stub.streamRequest(new StreamObserver<Route>() {

            @Override
            public void onCompleted() {
                latch.countDown();
            }

            @Override
            public void onError(Throwable arg0) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void onNext(Route res) {
                System.out.println("messages from server = " + res.getMessage());
                
            }
            
        });

        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
        while(true){
            String text = console.readLine();
            if (text.equals("exit")) {
                break;
            }
            stream.onNext(Route.newBuilder()
                    .setId(clientID)
                    .setOrigin(RouteClient.clientID)
                    .setPath("to/somewhere")
                    .setMessage(text)
                    .build());
            
        }
        

        stream.onCompleted();
        latch.await(3, TimeUnit.SECONDS);
    }

    public static void main(String[] args) throws IOException, InterruptedException {

        if (args.length == 0) {
            System.out.println("Need one argument to work");
            return;
        }

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", RouteClient.port).usePlaintext().build();

        switch (args[0]) {
            case "request":
                doRequest(channel);
                break;
            case "streamRequest":
                doStreamRequest(channel);
                break;
            default:
                System.out.println("Keyword Invalid: " + args[0]);
                break;
        }

    }
}
