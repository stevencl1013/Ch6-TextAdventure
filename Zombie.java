import java.util.Random;

/**
 * Write a description of class Zombie here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Zombie
{
    private Room location;
    /**
     * Constructor for objects of class Zombie
     */
    public Zombie(Room room)
    {
        location = room;
        location.addZombies(1);
    }
    
    public void move() // moves zombie to a random bordering room, or stays still.
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
                location.addZombies(-1);
                location = nextRoom;
                location.addZombies(1);
                done = true;
            }
        }
    }
    
    public Room getRoom()
    {
        return location;
    }
    
    public void setRoom(Room r)
    {
        location = r;
    }
}
