import java.util.Random;

/**
 * Write a description of class Zombie here.
 * 
 * @author Steven Lee
 * @version 4-4-2016
 */
public class Zombie
{
    private Room location; // room the zombie is in
    /**
     * Create a zombie in given room, and add 1 to that rooms number of zombies.
     * @param Room zombie is initially in.
     */
    public Zombie(Room room) 
    {
        location = room;
        location.addZombies(1);
    }
    
     /**
     * Randomly lets either move to a neighboring room, or stay where it is,
     * and update the rooms' number of zombies accordingly.
     */
    public void move() 
    {
        String direction;
        int num;
        Random rand = new Random();
        boolean done = false;
        Room nextRoom;
        while(!done)
        {
            num = rand.nextInt(7);
            switch(num)
            {
                case 0: direction = "north";
                    break;
                case 1: direction = "south";
                    break;
                case 2: direction = "west";
                    break;
                case 3: direction = "east";
                    break;
                case 4: direction = "upstairs";
                    break;
                case 5: direction = "downstairs";
                    break;
                default: return;
            }
            nextRoom = location.getExit(direction);
            if(nextRoom != null)
            {
                location.addZombies(-1); // subtract 1 from old room's number of zombies
                location = nextRoom;
                location.addZombies(1); // add 1 to new room's number of zombies.
                done = true;
            }
        }
    }
    
     /**
     * @return The room the zombie is currently in.
     */
    public Room getRoom()
    {
        return location;
    }
    
     /**
     * Change the room the zombie is in. Only used for sending zombies to hell.
     * @param direction The room the zombie will move to.
     */
    public void setRoom(Room r)
    {
        location = r;
    }
}
