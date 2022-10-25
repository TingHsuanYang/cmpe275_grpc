import subprocess
import sys 
from multiprocessing import Process
import socket, errno
import os
import psutil
from psutil import process_iter
from signal import SIGTERM # or SIGKILL


def is_port_in_use(port: int) -> bool:
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        return s.connect_ex(('0.0.0.0',int(port))) == 0

def check_Add():
	for m in range(2345,65535):
		if is_port_in_use(m):
			pass
		else:
			return m
def check_Delete():
	for m in range(9,0,-1):
		if is_port_in_use(str(m)*4):
			return m
		else:
			pass	
	
def server(config_file):

	command = 'sh runServer.sh '+config_file
	process = subprocess.Popen(command.split(), stdout=subprocess.PIPE)
	output, error = process.communicate()
	print(output)

def add_server():
	#print(len(servers))
	sernum=check_Add()
	next = int(sernum)+1
	f = open("server"+str(sernum)+".txt", "w")
	f.write("server.id="+str(sernum)+"\n")
	f.write("server.name=server"+str(sernum)+"\n")
	f.write("server.port="+str(sernum)+"\n")
	f.write("server.role=worker|monitor|leader"+"\n")
	f.write("server.next.id="+str(next)+"\n")
	f.write("server.next.port="+str(next)+"\n")
	f.write("server.threshold=50"+"\n")
	f.close()
	server("server"+str(sernum)+".txt")
		
		

def delete_server():
    portnum = check_Delete()
    os.remove("server"+str(portnum)+".txt")
    portnum = str(portnum)*4
    connections = psutil.net_connections()
    port = int(portnum)
    print(port)
    for con in connections:
        if con.raddr != tuple():
            if con.raddr.port == port:
                return con.pid, con.status
        if con.laddr != tuple():
            if con.laddr.port == port:
                return con.pid, con.status
    print("done")
    
    
    
if __name__ == "__main__":
	print("here")
	action = str(sys.argv[1])
	#print(is_port_in_use(1111))
	if action == 'add':
		add_server()
	if action == 'delete':
		server = delete_server()
		s=psutil.Process(server[0])
		s.terminate()
	
