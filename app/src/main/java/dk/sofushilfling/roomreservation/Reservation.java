package dk.sofushilfling.roomreservation;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Reservation implements Parcelable
{
    /**
     * id : 0
     * fromTime : 0
     * toTime : 0
     * userId : string
     * purpose : string
     * roomId : 0
     */

    private int id;
    private long fromTime;
    private long toTime;
    private String userId;
    private String purpose;
    private int roomId;

    protected Reservation(Parcel in)
    {
        id = in.readInt();
        fromTime = in.readLong();
        toTime = in.readLong();
        userId = in.readString();
        purpose = in.readString();
        roomId = in.readInt();
    }

    public int getId() { return id;}
    public void setId(int id) { this.id = id;}

    public long getFromTime() { return fromTime;}
    public void setFromTime(int fromTime) { this.fromTime = fromTime;}

    public long getToTime() { return toTime;}
    public void setToTime(int toTime) { this.toTime = toTime;}

    public String getUserId() { return userId;}
    public void setUserId(String userId) { this.userId = userId;}

    public String getPurpose() { return purpose;}
    public void setPurpose(String purpose) { this.purpose = purpose;}

    public int getRoomId() { return roomId;}
    public void setRoomId(int roomId) { this.roomId = roomId;}

    @Override
    public String toString()
    {
        return "From: " + new Date(getFromTime() * 1000) +
                ", To: " + new Date(getToTime() * 1000) +
                ", Purpose: '" + purpose + "'";
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(id);
        dest.writeLong(fromTime);
        dest.writeLong(toTime);
        dest.writeString(userId);
        dest.writeString(purpose);
        dest.writeInt(roomId);
    }

    public static final Creator<Reservation> CREATOR = new Creator<Reservation>()
    {
        @Override
        public Reservation createFromParcel(Parcel in)
        {
            return new Reservation(in);
        }

        @Override
        public Reservation[] newArray(int size)
        {
            return new Reservation[size];
        }
    };
}
