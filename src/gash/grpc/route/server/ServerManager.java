package gash.grpc.route.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerManager {

    private static Logger logger = LoggerFactory.getLogger(ServerManager.class);

    public static void main(String[] args) {

        Process process = null;
        BufferedReader reader =null;
        String line = "";

        try {
            if (args.length == 0) {
                logger.error("Need atleast one port number");
                throw new RuntimeException("No processes to kill");
            }


            String portnumber = args[0];

            if(portnumber.length()!=4 || !portnumber.matches("[0-9]+")){
                throw new RuntimeException("Invalid input");
            }

            logger.info("Received port number" + portnumber);

            String command = "lsof -Fp -i:" + portnumber;

         
            logger.info("Finding process running on portnumber " + portnumber);

            process = Runtime.getRuntime().exec(command);
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

          

            //Read first line to get the process id.
            if ((line = reader.readLine()) != null) {

                logger.info("process id: " + line);
                
                //Removed first character from the process id
                String pid = line.substring(1, line.length());

                logger.info("modified process id: " + pid);

                //Execute command to kill the process
                process = Runtime.getRuntime().exec("kill -9 " + pid);

                logger.info("Successfully stopped process running on port number " + portnumber + " with processId " + pid);

            }else{
                logger.info("unable to find any process running on the port "+portnumber);
            }
            //wait till the process represented by this obj is terminated
            process.waitFor();

        } catch (InterruptedException e) {
            logger.error("Exception occured while stopping the process"+e.getStackTrace());
        } catch (IOException e) {
            logger.error("Exception occured while stopping the process"+e.getStackTrace());
        }

    }

}
