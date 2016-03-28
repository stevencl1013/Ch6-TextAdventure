import java.util.Random;

/**
 *  This class is the main class of the "World of Zuul" application. 
 *  "World of Zuul" is a very simple, text based adventure game.  Users 
 *  can walk around some scenery. That's all. It should really be extended 
 *  to make it more interesting!
 * 
 *  To play this game, create an instance of this class and call the "play"
 *  method.
 * 
 *  This main class creates and initialises all the others: it creates all
 *  rooms, creates the parser and starts the game.  It also evaluates and
 *  executes the commands that the parser returns.
 * 
 * @author  Michael Kölling and David J. Barnes
 * @version 2011.08.10
 */

public class Game 
{
    private Parser parser;
    private Room currentRoom;
    private boolean hasMasterKey = false;
        
    /**
     * Create the game and initialise its internal map.
     */
    public Game() 
    {
        createRooms();
        parser = new Parser();
    }

    /**
     * Create all the rooms and link their exits together.
     */
    private void createRooms()
    {
        Room[] rooms = new Room[15];
        Random rand = new Random();
        int masterKeyRoom = 11;
        while(masterKeyRoom == 11) // randomly set master key room to any room except the neighbor's house.
        {
            masterKeyRoom = rand.nextInt(15);
        }
      
        // create the rooms
        rooms[0] = new Room("Woods");
        rooms[1] = new Room("Lake");
        rooms[2] = new Room("Backyard");
        rooms[3] = new Room("West Yard");
        rooms[4] = new Room("Living Room");
        rooms[5] = new Room("Hall");
        rooms[6] = new Room("Kitchen");
        rooms[7] = new Room("East Yard");
        rooms[8] = new Room("Main Bathroom");
        rooms[9] = new Room("Front Yard");
        rooms[10] = new Room("Closet");
        rooms[11] = new Room("Neighbor's House");
        rooms[12] = new Room("Basement");
        rooms[13] = new Room("Bedroom");
        rooms[14] = new Room("Upper Bathroom");
        
        // initialise room exits
        rooms[0].setExit("east", rooms[1]);
        rooms[0].setExit("south", rooms[2]);
        rooms[1].setExit("south", rooms[7]);
        rooms[2].setExit("north", rooms[0]);
        rooms[2].setExit("south", rooms[5]);
        rooms[3].setExit("east", rooms[4]);
        rooms[4].setExit("west", rooms[3]);
        rooms[4].setExit("south", rooms[8]);
        rooms[4].setExit("east", rooms[5]);
        rooms[5].setExit("north", rooms[2]);
        rooms[5].setExit("west", rooms[4]);
        rooms[5].setExit("south", rooms[9]);
        rooms[5].setExit("east", rooms[6]);
        rooms[5].setExit("upstairs", rooms[13]);
        rooms[5].setExit("downstairs", rooms[12]);
        rooms[6].setExit("west", rooms[5]);
        rooms[6].setExit("south", rooms[10]);
        rooms[6].setExit("east", rooms[7]);
        rooms[7].setExit("north", rooms[1]);
        rooms[7].setExit("west", rooms[6]);
        rooms[8].setExit("north", rooms[4]);
        rooms[9].setExit("north", rooms[5]);
        rooms[10].setExit("north", rooms[6]);
        // room 11 is only accessible with master key.
        rooms[12].setExit("upstairs", rooms[9]);
        rooms[13].setExit("downstairs", rooms[9]);
        rooms[13].setExit("east", rooms[14]);
        rooms[14].setExit("west", rooms[13]);

        currentRoom = rooms[9];  // start game in the front yard.
    }

    /**
     *  Main play routine.  Loops until end of play.
     */
    public void play() 
    {            
        printWelcome();

        // Enter the main command loop.  Here we repeatedly read commands and
        // execute them until the game is over.
                
        boolean finished = false;
        while (! finished) {
            Command command = parser.getCommand();
            finished = processCommand(command);
        }
        System.out.println("Thank you for playing.  Good bye.");
    }

    /**
     * Print out the opening message for the player.
     */
    private void printWelcome()
    {
        System.out.println();
        System.out.println("Welcome to the World of Zuul!");
        System.out.println("World of Zuul is a new, incredibly boring adventure game.");
        System.out.println("Type '" + CommandWord.HELP + "' if you need help.");
        System.out.println();
        System.out.println(currentRoom.getLongDescription());
    }

    /**
     * Given a command, process (that is: execute) the command.
     * @param command The command to be processed.
     * @return true If the command ends the game, false otherwise.
     */
    private boolean processCommand(Command command) 
    {
        boolean wantToQuit = false;

        CommandWord commandWord = command.getCommandWord();

        switch (commandWord) {
            case UNKNOWN:
                System.out.println("I don't know what you mean...");
                break;

            case HELP:
                printHelp();
                break;

            case GO:
                goRoom(command);
                break;

            case QUIT:
                wantToQuit = quit(command);
                break;
        }
        return wantToQuit;
    }

    // implementations of user commands:

    /**
     * Print out some help information.
     * Here we print some stupid, cryptic message and a list of the 
     * command words.
     */
    private void printHelp() 
    {
        System.out.println("You are lost. You are alone. You wander");
        System.out.println("around at the university.");
        System.out.println();
        System.out.println("Your command words are:");
        parser.showCommands();
    }

    /** 
     * Try to go in one direction. If there is an exit, enter the new
     * room, otherwise print an error message.
     */
    private void goRoom(Command command) 
    {
        if(!command.hasSecondWord()) {
            // if there is no second word, we don't know where to go...
            System.out.println("Go where?");
            return;
        }

        String direction = command.getSecondWord();

        // Try to leave current room.
        Room nextRoom = currentRoom.getExit(direction);

        if (nextRoom == null) {
            System.out.println("There is no door!");
        }
        else {
            currentRoom = nextRoom;
            System.out.println(currentRoom.getLongDescription());
        }
    }

    /** 
     * "Quit" was entered. Check the rest of the command to see
     * whether we really quit the game.
     * @return true, if this command quits the game, false otherwise.
     */
    private boolean quit(Command command) 
    {
        if(command.hasSecondWord()) {
            System.out.println("Quit what?");
            return false;
        }
        else {
            return true;  // signal that we want to quit
        }
    }
}
