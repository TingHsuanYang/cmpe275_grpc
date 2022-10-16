import subprocess
import sys 
from multiprocessing import Process

def client():

	command = 'sh runClient.sh'
	process = subprocess.Popen(command.split(), stdout=subprocess.PIPE)
	output, error = process.communicate()
	print(output)

if __name__ == "__main__":
	print("here")
	number_of_clients = int(sys.argv[1])
	processes=[]
	print(number_of_clients)
	for m in range(1,number_of_clients):
		n = m + 1
		p = Process(target=client)
		p.start()
		processes.append(p)
		
	for p in processes:
		p.join()
