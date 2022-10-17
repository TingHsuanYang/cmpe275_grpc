package gash.grpc.route.server;

import com.google.protobuf.ByteString;

import io.grpc.stub.StreamObserver;
import route.Route;

public class Work implements Runnable {

	private Route request;
	private StreamObserver<route.Route> responseObserver;


	public Work(Route request, StreamObserver<route.Route> responseObserver) {
		this.request = request;
		this.responseObserver = responseObserver;
	}

	@Override
	public void run() {

		route.Route.Builder builder = route.Route.newBuilder();

		// routing/header information
		builder.setId(RouteServer.getInstance().getNextMessageID());
		builder.setOrigin(RouteServer.getInstance().getServerID());
		builder.setDestination(request.getOrigin());
		builder.setPath(request.getPath());

		// do the work and reply
		builder.setPayload(process());
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		route.Route rtn = builder.build();
		responseObserver.onNext(rtn);
		responseObserver.onCompleted();

	}

	private ByteString process() {

		String content = new String(this.request.getPayload().toByteArray());
		System.out.println("-- got: " + this.request.getOrigin() + ", path: " + this.request.getPath() + ", with: " + content);

		// TODO complete processing
		final String blank = "blank";
		byte[] raw = blank.getBytes();

		return ByteString.copyFrom(raw);
	}

}
