//class for Page Table Entry functions

public class PTEntry {

    private int PageTable_Index;
    private int frame;

    private boolean isValid;
    private boolean isReferenced;
    private boolean isDirty;

    public PTEntry()
    {
        isValid = false;
        isReferenced = false;
        isDirty = false;
        PageTable_Index = -1;
        frame = -1;
    }

    public boolean isValid()
    {
        return isValid;
    }
    public boolean isReferenced()
    {
        return isReferenced;
    }
    public boolean isDirty()
    {
        return isDirty;
    }
    public int getIndex()
    {
        return PageTable_Index;
    }
    public int getFrame()
    {
        return frame;
    }
    public void setValid(boolean value)
    {
        isValid = value;
    }
    public void setDirty(boolean value)
    {
        isDirty = value;
    }
    public void setReferenced(boolean value)
    {
        isReferenced = value;
    }
    public void setIndex(int i)
    {
        PageTable_Index = i;
    }
    public void setFrame(int value)
    {
        frame = value;
    }
}
