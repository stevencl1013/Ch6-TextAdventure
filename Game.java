import java.util.Random;
import java.util.ArrayList;
import java.awt.*;
import javax.swing.*;
/**
 *  This is a text adventure game, where the player must try to kill as 
 *  many zombies as possible. To kill zombies, the user must either get
 *  the knife from the kitchen, or get the gun from the bedroom and ammo
 *  from the basement. The bathroom contains pills which can make the 
 *  user able to swim, omniscient, or dead. The gun, ammo, and neighbor's
 *  house can only be accessed if the master key is found. The neighbor's
 *  house contains a map. 
 * 
 * @author  Steven Lee
 * @version 4-4-2016
 */

public class Game 
{
    private Parser parser;
    private Room currentRoom;
    private int masterKeyRoom;
    private int totalZombies;
    private int ammo;
    private int kills;
    private int steps;
    private int timesReloaded;
    private boolean hasMasterKey;
    private boolean hasKnife;
    private boolean hasGun;
    private boolean hasMap;
    private boolean canSwim;
    private boolean finished;
    private boolean omniscient; // lets character see how many zombies are in each room.
    private Room[] rooms;
    private Room hell; // where all the dead zombies go.
    private ArrayList<Zombie> zombies;
        
    /**
     * Create the game and initialise its internal map.
     */
    public Game() 
    {
        createRooms();
        parser = new Parser();
    }
    
    private void resetValues() 
    {
        Random rand = new Random();
        masterKeyRoom = 11;
        totalZombies = 0;
        ammo = 0;
        kills = 0;
        steps = 0;
        timesReloaded = 0;
        hasMasterKey = false;
        canSwim = false;
        hasGun = false;
        hasKnife = false;
        hasMap = false;
        zombies = new ArrayList<Zombie>();
        for(int i = 0; i < rooms.length; i++)
        {
            rooms[i].setNumZombies(0);
        }
        while(masterKeyRoom == 11) // randomly set master key room to any room except the neighbor's house.
        {
            masterKeyRoom = rand.nextInt(15);
        }
    }
    
    private void resetRoom() // puts character back in front yard.
    {
        currentRoom = rooms[9];
    }

    /**
     * Create all the rooms and link their exits together.
     */
    private void createRooms()
    {
        rooms = new Room[15];
        // create the rooms
        hell = new Room("Hell");
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
        rooms[1].setExit("west", rooms[0]);
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
        rooms[12].setExit("upstairs", rooms[5]);
        rooms[13].setExit("downstairs", rooms[5]);
        rooms[13].setExit("east", rooms[14]);
        rooms[14].setExit("west", rooms[13]);
        
        resetValues();
        resetRoom();
    }

    /**
     *  Main play routine.  Loops until end of play.
     */
    public void play() 
    {            
        resetValues();
        resetRoom();
        printWelcome();
        // Enter the main command loop.  Here we repeatedly read commands and
        // execute them until the game is over.
                
        finished = false;
        while (! finished) {
            Command command = parser.getCommand();
            processCommand(command);
        }
        System.out.println("Kills: " + kills);
        System.out.println("Steps: " + steps);
        System.out.println("Thank you for playing.  Good bye.");
    }

    /**
     * Print out the opening message for the player.
     */
    private void printWelcome()
    {
        System.out.println();
        System.out.println("Welcome to the Zombie Apocalypse!");
        System.out.println("Kill as many zombies as you can, without getting killed yourself.");
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
                finished = true;
                break;
        }
        return wantToQuit;
    }

    // implementations of user commands:

    /**
     * Print out some help information.
     * Here we print a list of the 
     * command words.
     */
    private void printHelp() 
    {
        System.out.println("Your command words are:");
        parser.showCommands();
    }

    /** 
     * Try to go in one direction. If there is an exit, enter the new
     * room, otherwise print an error message. Also, generates new zombies
     * and moves existing zombies. Based on the room, different actions will
     * occur.
     */
    private void goRoom(Command command) 
    {
        if(!command.hasSecondWord()) {
            // if there is no second word, we don't know where to go...
            System.out.println("Go where?");
            return;
        }

        String direction = command.getSecondWord();
        Random rand = new Random();
        int num;
        // Try to leave current room.
        Room nextRoom = currentRoom.getExit(direction);

        if (nextRoom == null) 
        {
            System.out.println("There is no door!");
        }
        else 
        {
            steps++;
            currentRoom = nextRoom;
            System.out.println(currentRoom.getLongDescription());
            for(Zombie zombie : zombies) // move every zombie
            {
                if(!zombie.getRoom().equals(hell))
                    zombie.move();
            }
            for(int i = 0; i < rooms.length; i++) // On each move, each room has a 1/4 chance of adding a new zombie.
            {
                num = rand.nextInt(4);
                if(num == 0 && steps > 2)
                {
                    zombies.add(new Zombie(rooms[i]));
                }
            }
            if(currentRoom.getNumZombies() > 3) // decides what to do based on number of zombies in room.
            {
                if(currentRoom.getNumZombies() > ammo)
                {
                    finished = true;
                    System.out.println("There are more zombies in this room than you can kill.");
                    return;
                }
                else
                {
                    System.out.println("You have killed all "+currentRoom.getNumZombies()+" zombies in this room with your gun.");
                    for(Zombie zombie : zombies)
                    {
                        if(zombie.getRoom().equals(currentRoom))
                        {
                            ammo--;
                            zombie.setRoom(hell);
                            currentRoom.addZombies(-1);
                            kills++;
                        }
                    }
                    System.out.println("You now have " + ammo + " bullets.");
                }
            }
            else if(currentRoom.getNumZombies() == 0)
            {
                System.out.println("You're lucky, there are no zombies in this room.");
            }
            else
            {
                if(hasKnife)
                {
                    System.out.println("You have killed all "+currentRoom.getNumZombies()+" zombies in this room with your knife.");
                    for(Zombie zombie : zombies)
                    {
                        if(zombie.getRoom().equals(currentRoom))
                        {
                            zombie.setRoom(hell);
                            currentRoom.addZombies(-1);
                            kills++;
                        }
                    }
                }
                else if(ammo >= currentRoom.getNumZombies())
                {
                    System.out.println("You have killed all "+currentRoom.getNumZombies()+" zombies in this room with your gun.");
                    for(Zombie zombie : zombies)
                    {
                        if(zombie.getRoom().equals(currentRoom))
                        {
                            ammo--;
                            zombie.setRoom(hell);
                            currentRoom.addZombies(-1);
                            kills++;
                        }
                    }
                    System.out.println("You now have " + ammo + " bullets.");
                }
                else
                {
                    finished = true;
                    System.out.println("There are more zombies in this room than you can kill.");
                    return;
                }
                    
            }
            
            if(currentRoom.equals(rooms[masterKeyRoom]) && !hasMasterKey) // if user finds master key
            {
                hasMasterKey = true;
                System.out.println("You have acquired the master key.");
                System.out.println("You can now access the gun in the bedroom,");
                System.out.println("the ammo in the basement, and your neighbor's house.");
                rooms[9].setExit("south", rooms[11]);
                rooms[11].setExit("north", rooms[9]);
            }
            if(currentRoom.equals(rooms[1]) && (!canSwim)) // if user enters lake without being able to swim
            {
                System.out.println("You cannot swim, you have drowned to death.");
                finished = true;
            }
            else if(currentRoom.equals(rooms[8]) || currentRoom.equals(rooms[14])) // if user enters a bathroom, he must take a pill
            {
                System.out.println("Since you came to the bathroom, you must take a pill.");
                int pillNum = rand.nextInt(3);
                switch(pillNum) // randomly decides what pill will be taken
                {
                    case 0: System.out.println("You can now swim!");
                        canSwim = true;
                        break;
                    case 1: System.out.println("Whoops, you took the death pill.");
                        finished = true;
                        break;
                    case 2: System.out.println("You are now omniscient. You can see how many zombies are in each room.");
                        omniscient = true;
                        break;
                }
            }
            else if(currentRoom.equals(rooms[6]) && !hasKnife) //if user enters kitchen, he gets the knife.
            {
                System.out.println("You now have a knife.");
                hasKnife = true;
            }
            else if(currentRoom.equals(rooms[13]) && !hasGun && hasMasterKey) //if user enters bedroom, he gets the gun.
            {
                System.out.println("You now have a gun.");
                hasGun = true;
            }
            else if(currentRoom.equals(rooms[12]) && hasMasterKey) // if user enters basement, he reloads the gun, if he has one.
            {
                if(hasGun)
                {
                    if(timesReloaded < 5)
                    {
                        System.out.println("You now have 10 bullets.");
                        ammo = 10;
                        timesReloaded++;
                        System.out.println("You may reload " + (5-timesReloaded) + " more times.");
                    }
                    else
                        System.out.println("Since you have already reloaded 5 times, you may not reload again.");
                }
                else
                {
                    System.out.println("If you had a gun, you would be able to add ammo from this room.");
                }
            }
            else if(currentRoom.equals(rooms[11]) && !hasMap) // if user enters neighbor's house, he finds the map.
            {
                System.out.println("Here, you have found a map!");
                displayMap();
            }
            
            if(omniscient) // if omniscient, user can see where zombies are and where the master key is.
            {
                for(int i = 0; i < rooms.length; i++)
                    System.out.println("In the "+rooms[i].getShortDescription()+" there are "+rooms[i].getNumZombies()+" zombies.");
                if(!hasMasterKey)
                    System.out.println("The master key is in the "+rooms[masterKeyRoom].getShortDescription());
            }
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
    
    
    /** 
     * Displays map of the game. Note: I did not write this code myself. 
     */
    private void displayMap()
    {
        String fileName = "TextAdventureMap.jpg";
        ImageIcon icon = new ImageIcon(fileName);
        JLabel label = new JLabel(icon);
        JFrame f = new JFrame();
        f.getContentPane().add(new JScrollPane(label));
        f.setSize(1000,1000);
        f.setLocation(200,200);
        f.setVisible(true);
    }
    public static void main (String[] args)
    {
        Game game = new Game();
        game.play();
    }
}
