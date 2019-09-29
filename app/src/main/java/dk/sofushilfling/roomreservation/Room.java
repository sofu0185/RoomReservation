package dk.sofushilfling.roomreservation;


import java.io.Serializable;

public class Room implements Serializable
{
    /**
     * id : 1
     * name : RO-D3.07
     * description : Class Room
     * capacity : 35
     * remarks : null
     */

    private int id;
    private String name;
    private String description;
    private int capacity;
    private Object remarks;

    public int getId() { return id;}

    public void setId(int id) { this.id = id;}

    public String getName() { return name;}

    public void setName(String name) { this.name = name;}

    public String getDescription() { return description;}

    public void setDescription(String description) { this.description = description;}

    public int getCapacity() { return capacity;}

    public void setCapacity(int capacity) { this.capacity = capacity;}

    public Object getRemarks() { return remarks;}

    public void setRemarks(Object remarks) { this.remarks = remarks;}

    @Override
    public String toString()
    {
        return getName();
    }
}
