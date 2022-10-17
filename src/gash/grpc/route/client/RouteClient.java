package gash.grpc.route.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
    	// client -> server1 -> server2 -> ...
        return Route.newBuilder()
        		.setId(mID)
        		.setOrigin(RouteClient.clientID)
        		.setPath(path)
        		.setPayload(ByteString.copyFrom(payload.getBytes()))
        		.build();
    }

    private static final void showResponse(Route reply) {
        var payload = new String(reply.getPayload().toByteArray());
        System.out.println(String.format("reply: %s, path: %s",payload, reply.getPath()));
    }

	private static final void doRequest(ManagedChannel channel) {
		RouteServiceGrpc.RouteServiceBlockingStub stub = RouteServiceGrpc.newBlockingStub(channel);
		final int I = 1;
		for (int i = 0; i < I; i++) {
			var msg = RouteClient.constructMessage(i, "client", "hello");
			var r = stub.request(msg);
			showResponse(r);
		}
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

		if (args.length < 2) {
			System.out.println("Need 2 argument to work");
			return;
		}

		clientID = Long.parseLong(args[1]);

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

		channel.shutdown();

	}
}
