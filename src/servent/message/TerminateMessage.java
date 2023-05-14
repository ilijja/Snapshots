package servent.message;


public class TerminateMessage extends Message{

    public TerminateMessage() {
        super(Type.TERMINATE);
    }


    @Override
    public String toString() {
        return "MESSAGE: " +  getType();
    }

}
