package gash.grpc.route.server;

import com.google.protobuf.ByteString;

import io.grpc.stub.StreamObserver;
import route.Route;

public class Work implements Runnable {

	private Route request;
	private StreamObserver<Route> responseObserver;


	public Work(Route request, StreamObserver<route.Route> responseObserver) {
		this.request = request;
		this.responseObserver = responseObserver;
	}

	@Override
	public void run() {

		Route response = Route.newBuilder(this.request)
				.setId(RouteServer.getInstance().getNextMessageID())
				.setOrigin(RouteServer.getInstance().getServerID())
				.setDestination(request.getOrigin())
				.setPath(String.format("%s->%s", this.request.getPath(),RouteServer.getInstance().getServerName()))
				.setPayload(process())
				.build();
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		responseObserver.onNext(response);
		responseObserver.onCompleted();

	}

	private ByteString process() {

		String content = new String(this.request.getPayload().toByteArray());
		System.out.println("-- WORK got: " + this.request.getOrigin() + ", path: " + this.request.getPath() + ", with: " + content);

		// TODO complete processing
		final String blank = String.format("hello from %s", RouteServer.getInstance().getServerName());
		byte[] raw = blank.getBytes();

		return ByteString.copyFrom(raw);
	}

}
