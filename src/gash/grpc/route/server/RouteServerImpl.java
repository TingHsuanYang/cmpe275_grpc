package gash.grpc.route.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

import gash.grpc.route.client.RouteClient;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import route.Route;
import route.RouteServiceGrpc;
import route.RouteServiceGrpc.RouteServiceImplBase;

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
public class RouteServerImpl extends RouteServiceImplBase {
    private Server svr;

//	private Queue<Work> reqQueue = new SynchronousQueue();
    private BlockingQueue<Work> reqQueue = new ArrayBlockingQueue(1024);

    /**
     * Configuration of the server's identity, port, and role
     */
    private static Properties getConfiguration(final File path) throws IOException {
        if (!path.exists())
            throw new IOException("missing file");

        Properties rtn = new Properties();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(path);
            rtn.load(fis);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }

        return rtn;
    }

    /**
     * TODO refactor this!
     * 
     * @param path
     * @param payload
     * @return
     */
//	protected ByteString process(route.Route msg) {
//		String content = new String(msg.getPayload().toByteArray());
//		System.out.println("-- got: " + msg.getOrigin() + ", path: " + msg.getPath() + ", with: " + content);
//
//		// TODO complete processing
//		final String blank = "blank";
//		byte[] raw = blank.getBytes();
//
//		return ByteString.copyFrom(raw);
//	}

    public static void main(String[] args) throws Exception {
        // check args!
        if (args.length == 0) {
            System.out.println("Need one argument to work");
        }

        String path = args[0];
        try {
            Properties conf = RouteServerImpl.getConfiguration(new File(path));
            RouteServer.configure(conf);

            /* Similar to the socket, waiting for a connection */
            final RouteServerImpl impl = new RouteServerImpl();
            impl.start();
            impl.blockUntilShutdown();

        } catch (IOException e) {
            // TODO better error message
            e.printStackTrace();
        }
    }

    private void start() throws Exception {
        svr = ServerBuilder.forPort(RouteServer.getInstance().getServerPort()).addService(new RouteServerImpl())
                .build();

        System.out.println("-- starting server");
        svr.start();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                RouteServerImpl.this.stop();
            }
        });
    }

    protected void stop() {
        svr.shutdown();
    }

    private void blockUntilShutdown() throws Exception {
        /* TODO what clean up is required? */
        svr.awaitTermination();
    }

    /**
     * server received a message!
     */
    @Override
    public void request(route.Route request, StreamObserver<route.Route> responseObserver) {

        // if the server is the last server, no forwarding, if queue is full, forward to
        // next server
        if (RouteServer.getInstance().getServerID() != 1236 && reqQueue.size() >= 2) {

            ManagedChannel ch = ManagedChannelBuilder
                    .forAddress("localhost", RouteServer.getInstance().getNextServerPort()).usePlaintext().build();
            RouteServiceGrpc.RouteServiceBlockingStub stub = RouteServiceGrpc.newBlockingStub(ch);

            // request
            var res = stub.request(request);

            responseObserver.onNext(res);
//            responseObserver.onCompleted();
            
//            route.Route.Builder builder = route.Route.newBuilder();

//            responseObserver.onNext(Route.newBuilder()
//                    .setMessage("return from " + RouteServer.getInstance().getNextServerID()).build());
            responseObserver.onCompleted();
            ch.shutdown();

        }

        Work work = new Work(request, responseObserver);
        reqQueue.add(work);

//		while (!reqQueue.isEmpty()) {
        while (reqQueue.size() > 2) {
            Work w = reqQueue.poll();
            w.run();
        }
    }

    // we can't use this...we don't need another process to handle the streamRequest
    @Override
    public StreamObserver<Route> streamRequest(StreamObserver<Route> responseObserver) {

        return new StreamObserver<Route>() {

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }

            @Override
            public void onError(Throwable t) {
                responseObserver.onError(t);
            }

            @Override
            public void onNext(Route req) {
                System.out
                        .println("ID: " + req.getId() + ", Path: " + req.getPath() + ", Messages: " + req.getMessage());
                responseObserver.onNext(Route.newBuilder().setMessage(
                        "Return from serverID: " + RouteServer.getInstance().getServerID()).build());
            }

        };

    }

}
