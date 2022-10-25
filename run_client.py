import subprocess
import sys 
from multiprocessing import Process
import os
def client(m):

	f = open("runClient"+str(m)+".sh", "w")
	f.write("#!/bin/bash\n")
	f.write('export SVR_HOME="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"\n')
	f.write('echo "** starting client from ${SVR_HOME} **"\n')
	f.write("JAVA_MAIN=' gash.grpc.route.client.RouteClient'\n")
	f.write('JAVA_ARGS="request"\n')
	f.write('JAVA_ARGs2="'+str(m)+'"\n')
	f.write("JAVA_TUNE='-client -Xms96m -Xmx512m'\n")
	f.write("java ${JAVA_TUNE} -cp .:${SVR_HOME}/lib/'*':${SVR_HOME}/bin ${JAVA_MAIN} ${JAVA_ARGS} ${JAVA_ARGs2}\n")
	f.close()
	command = 'sh runClient'+str(m)+'.sh'
	#command = 'java -client -Xms96m -Xmx512m -cp ./lib/*:./bin  gash.grpc.route.client.RouteClient request 1'
	process = subprocess.Popen(command.split(), stdout=subprocess.PIPE)
	output, error = process.communicate()
	print(output)
	os.remove("runClient"+str(m)+".sh")


if __name__ == "__main__":
	print("here")
	number_of_clients = int(sys.argv[1])
	processes=[]
	print(number_of_clients)
	for m in range(1,number_of_clients):

		p = Process(target=client, args=(m,))
		p.start()
		processes.append(p)
		
	for process in processes:
		process.join()
