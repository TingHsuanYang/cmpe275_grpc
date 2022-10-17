package gash.grpc.route.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.google.protobuf.ByteString;

import gash.grpc.route.client.RouteClient;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
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
	private final int MAX_SIZE = 2;

	private ConcurrentLinkedQueue<Work> queue = new ConcurrentLinkedQueue<Work>();

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
	public void request(Route request, StreamObserver<Route> responseObserver) {
		
		System.out.println(String.format("get request from %s, with %s", request.getOrigin(), new String(request.getPayload().toByteArray())));
		if (queue.size() < MAX_SIZE) {
			System.out.println(String.format("--Add work to queue! Queue Size: %d", queue.size()));
			Work work = new Work(request, responseObserver);
			queue.add(work);
		} else {
			// forward
			if (RouteServer.getInstance().getNextServerID() != 9999L) {
				System.out.println(String.format("--Forward to %s", RouteServer.getInstance().getNextServerID()));
				// 9999 means there's no next server
				ManagedChannel ch = ManagedChannelBuilder
						.forAddress("localhost", RouteServer.getInstance().getNextServerPort())
						.usePlaintext()
						.build();
				RouteServiceGrpc.RouteServiceBlockingStub stub = RouteServiceGrpc.newBlockingStub(ch);
				Route res = null;
				try {
					// update path
					Route newReq = Route.newBuilder(request)
			        		.setPath(String.format("%s->%s", request.getPath(),RouteServer.getInstance().getServerName()))
			        		.build();
					res = stub.request(newReq);
					responseObserver.onNext(res);
				} catch (StatusRuntimeException e) {
					if (e.getStatus().getCode() == Status.Code.UNAVAILABLE) {
						System.out.println("Return the error back: Reach the end of server pipline!");
						responseObserver.onError(e); // return the error back to previous client
					} else {
						System.out.println("Got an exception in request");
						e.printStackTrace();
					}
				}
				responseObserver.onCompleted();
				ch.shutdown();
			} else {
				// throw exception, since there's no next server
				System.out.println("--Reach the end of server pipline!");
				responseObserver.onError(Status.UNAVAILABLE.withDescription("Reach the end of server pipline!")
						.augmentDescription("sent from: " + RouteServer.getInstance().getServerName())
						.asRuntimeException());
			}
		}

		// Simulation: do something time-consuming. e.g. I/O, access database
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		while (!queue.isEmpty()) {
			Work w = queue.poll();
			w.run();
		}
		
	}

	// we can't use this...no need to open another thread for stream request
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
				responseObserver.onNext(Route.newBuilder()
						.setMessage("Return from serverID: " + RouteServer.getInstance().getServerID()).build());
			}

		};

	}

}
