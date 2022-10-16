from __future__ import print_function

import logging

import grpc
import route_pb2
import route_pb2_grpc


def run():
    # NOTE(gRPC Python Team): .close() is possible on a channel and should be
    # used in circumstances in which the with statement does not fit the needs
    # of the code.
    with grpc.insecure_channel('localhost:5678') as channel:
        stub = route_pb2_grpc.RouteServiceStub(channel)
        response = stub.request(route_pb2.Route(id=1))
    print(response)


if __name__ == '__main__':
    logging.basicConfig()
    run()
