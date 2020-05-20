package Server;

import algorithms.mazeGenerators.Maze;
import algorithms.search.ASearchingAlgorithm;
import algorithms.search.ISearchable;
import algorithms.search.ISearchingAlgorithm;
import algorithms.search.Solution;

import java.io.*;
import java.util.Arrays;
import java.util.Hashtable;

public class ServerStrategySolveSearchProblem implements IServerStrategy {
    private ISearchingAlgorithm solving;
    private Hashtable<String, String> mazesSolved;
    private static int solutionCounter = 0; //static?


    //Lielle: Probably doesn't work:
    @Override
    public void serverStrategy(InputStream inputStream, OutputStream outputStream) {
        Maze maze;
        Solution solution;
        try {
            ObjectInputStream fromClient = new ObjectInputStream(inputStream);
            ObjectOutputStream toClient = new ObjectOutputStream(outputStream);
            maze = (Maze)fromClient.readObject();
            String tempDirectoryPath = System.getProperty("java.io.tmpdir");

            String mazeName = maze.arrayToString();
            if (!mazesSolved.containsKey(mazeName)){
                String fileName = "" + solutionCounter + ".txt";
                mazesSolved.put(mazeName, fileName);
                solutionCounter++;
                solution = solving.solve((ISearchable) maze);
                //Now we save the solution (no idea what's going on here with this I/O)
                File newFile = new File(tempDirectoryPath, fileName);
                FileOutputStream outFile = new FileOutputStream(newFile);
                toClient = new ObjectOutputStream(outFile);

                toClient.writeObject(solution);
                toClient.flush();
            }
            else{
                String fileName = mazesSolved.get(mazeName);
                //Now we get the solution from the file
                File newFile = new File(tempDirectoryPath, fileName);
                FileInputStream inputFile = new FileInputStream(newFile);
                ObjectInputStream returnFile = new ObjectInputStream(inputFile);

                solution = (Solution) returnFile.readObject();
                returnFile.close();
                toClient.writeObject(solution);
                toClient.flush();

            }

            toClient.close();
            fromClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

}