package model.message;

/**
 * Created by cyprien on 08/10/15.
 */
public class Message implements MessageInterface {
    private int idExp;
    private int idDest;
    private Action action;

    public Message() {
        this(-1,-1,null);
    }

    public Message(int idExp,int idDest, Action actions){
        this.idExp=idExp;
        this.idDest=idDest;
        this.action =actions;
    }


    public int getIdDest() {
        return idDest;
    }

    public Action getAction() {
        return action;
    }

    public int getIdExp() {

        return idExp;
    }
}
