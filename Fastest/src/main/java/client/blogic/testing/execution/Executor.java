package client.blogic.testing.execution;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * This class controls the execution of the concrete test cases.
 * It captures the test case's standard output, standard error, exit code and parses the yaml file with the state of the
 * specification variables.
 * @author Cristian Rosa
 */
public abstract class Executor {

    /**
     * Executes the concrete test case command and captures standard output, error, exit code, and yaml file contents.
     * @param command       the command to run the concrete test case
     * @param workingDir    the directory to run the test case
     * @return              an object that contains the standard output, error, yaml and exit code
     */
    public static Execution execute(String command, String workingDir) {
        try {
            // execute the concrete test case and pipe the standard output and error.
            ProcessBuilder pb = new ProcessBuilder(command.split(" "));
            //pb.directory(new File(workingDir));
            Process proc = pb.start();


            BufferedReader stdInputBuffer = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            BufferedReader stdErrorBuffer = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

            int exitCode = proc.waitFor();

            // read the output from the command (is redirected through fastest input stream)
            String stdOutput = null;
            String line = null;
            while ((line = stdInputBuffer.readLine()) != null) {
                stdOutput += "\n" + line;
            }

            // read any errors from the attempted command
            String stdError = null;
            line = null;
            while ((line = stdErrorBuffer.readLine()) != null) {
                stdError += "\n" + line;
            }

            return new Execution(null, stdOutput, stdError, exitCode);

        } catch (IOException | InterruptedException e) {
            // in case of an exception return an execution with exit code 1 that represents an error
            return new Execution(null, null, null, 1);
        }
    }
}


