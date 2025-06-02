import java.io.*;
import java.util.*;

public class NimReinforcementLearning {
    private static final int MAX_STICKS = 10;
    private static final int MAX_CHOICE = 3;
    private static final String FILE_NAME = "qvalues.txt";
    private static double[][] qValues = new double[MAX_CHOICE][MAX_STICKS];

    public static void main(String[] args) {
        loadQValues();
        Scanner scanner = new Scanner(System.in);
        boolean playAgain = true;

        while (playAgain) {
            play(scanner);
            System.out.print("Play again? (y/n): ");
            String response = scanner.next();
            playAgain = response.equalsIgnoreCase("y");
        }
        saveQValues();
        scanner.close();
    }

    private static void play(Scanner scanner) {
        int sticks = MAX_STICKS;
        List<Integer> computerMoves = new ArrayList<>();
        boolean computerTurn = true;

        System.out.println("\nNEW GAME");

        while (sticks > 0) {
            System.out.println("\nThere are " + sticks + " sticks left. ");

            if (computerTurn) {
                int move = getBestMove(sticks);
                System.out.println("The computer takes " + move + " stick(s).");
                computerMoves.add((sticks - 1) * MAX_CHOICE + (move - 1));
                sticks -= move;

                if (sticks == 0) {
                    System.out.println("Computer wins!");
                    updateQValues(computerMoves, true);
                    return;
                }
            } 
            else {
                int move = getUserMove(scanner, sticks);
                sticks -= move;

                if (sticks == 0) {
                    System.out.println("You win!");
                    updateQValues(computerMoves, false);
                    return;
                }
            }
            computerTurn = !computerTurn;
        }
    }

    private static int getUserMove(Scanner scanner, int sticks) {
        int move = 0;
        boolean validMove = false;

        while (!validMove) {
            System.out.print("Your turn. Please take 1, 2, or 3 sticks: ");
            if (scanner.hasNextInt()) {
                move = scanner.nextInt();
                if (move >= 1 && move <= 3 && move <= sticks) {
                    validMove = true;
                } 
                else {
                    System.out.println("Invalid move. Please enter a number 1-3: ");
                }
            } 

            else {
                System.out.println("Invalid move. Please enter a number 1-3: ");
                scanner.next();
            }
        }
        return move;
    }

    private static int getBestMove(int sticks) {
        int column = sticks - 1;
        double bestValue = Double.NEGATIVE_INFINITY;
        List<Integer> bestMoves = new ArrayList<>();

        for (int i = 0; i < MAX_CHOICE; i++) {
            if ((i + 1) <= sticks) {
                double value = qValues[i][column];
                if (value > bestValue) {
                    bestValue = value;
                    bestMoves.clear();
                    bestMoves.add(i + 1);
                } 
                else if (value == bestValue) {
                    bestMoves.add(i + 1);
                }
            }
            
        }
        return bestMoves.get(new Random().nextInt(bestMoves.size()));
    }

    private static void updateQValues(List<Integer> moves, boolean computerWin) {
        double qReward;
        if (computerWin) {
            qReward = 1.0;
        } 
        else {
            qReward = -1.0;
        }

        for (int index : moves) {
            int row = index % MAX_CHOICE;
            int col = index / MAX_CHOICE;
            qValues[row][col] += qReward;
        }
    }

    private static void loadQValues() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            System.out.println("(Q values file not found.)");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            for (int i = 0; i < MAX_CHOICE; i++) {
                String[] line = reader.readLine().split(",");
                for (int j = 0; j < MAX_STICKS; j++) {
                    qValues[i][j] = Double.parseDouble(line[j]);
                }
            }
        } 
        catch (IOException | NumberFormatException e) {
            System.out.println("Error loading q values file.");
        }
    }

    private static void saveQValues() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (int i = 0; i < MAX_CHOICE; i++) {
                for (int j = 0; j < MAX_STICKS; j++) {
                    writer.write(Double.toString(qValues[i][j]));
                    if (j < MAX_STICKS - 1) {
                        writer.write(",");
                    }
                }
                writer.newLine();
            }
        } 
        catch (IOException e) {
            System.out.println("Error saving q values file.");
        }
    }
}