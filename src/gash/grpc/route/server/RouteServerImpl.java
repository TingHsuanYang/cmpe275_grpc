package gash.grpc.route.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private static Logger logger = LoggerFactory.getLogger(RouteServerImpl.class);

	private Server svr;
	private final int MAX_SIZE = 2;

	private LinkedBlockingQueue<Work> queue = new LinkedBlockingQueue<Work>(MAX_SIZE);

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
			logger.info("Need one argument to work");
	
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

		logger.info("-- starting server");		
	
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

		logger.info("get request from "+request.getOrigin()+" with "+ new String(request.getPayload().toByteArray()));

		try {
			Work work = new Work(request, responseObserver);
			queue.add(work);
			logger.info("--Add work to queue! Queue Size: "+queue.size());
		
		} catch (IllegalStateException e) {
			logger.info("Queue full");
			// forward
			if (RouteServer.getInstance().getNextServerID() != 9999L) {
				logger.info("----Forward to : "+RouteServer.getInstance().getNextServerID());
				// 9999 means there's no next server
				ManagedChannel ch = ManagedChannelBuilder
						.forAddress("localhost", RouteServer.getInstance().getNextServerPort())
						.usePlaintext()
						.build();
				RouteServiceGrpc.RouteServiceBlockingStub stub = RouteServiceGrpc.newBlockingStub(ch);
				Route res = null;
				try {
					// update path
					Route newReq = Route.newBuilder(request).setPath(
							String.format("%s->%s", request.getPath(), RouteServer.getInstance().getServerName()))
							.build();
					res = stub.request(newReq);
					responseObserver.onNext(res);
					responseObserver.onCompleted();
				} catch (StatusRuntimeException se) {
					if (se.getStatus().getCode() == Status.Code.UNAVAILABLE) {
						logger.info(se.getMessage());
						responseObserver.onError(se); // return the error back to previous client
					} else {
						System.out.println("Got an exception in request");
						se.printStackTrace();
					}
				}
				ch.shutdown();
			} else {
				// throw exception, since there's no next server
				logger.error("---"+request.getOrigin() +" Reach the end of server pipline! ");
				responseObserver.onError(Status.UNAVAILABLE.withDescription(String.format("%s Reach the end of server pipline!", request.getOrigin()))
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
		
			Work w;
			try {
				w = queue.take();
				w.run();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
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

				logger.info("ID: " + req.getId() + ", Path: " + req.getPath() + ", Messages: " + req.getMessage());
			
						responseObserver.onNext(Route.newBuilder()
						.setMessage("Return from serverID: " + RouteServer.getInstance().getServerID()).build());
			}

		};

	}

}
