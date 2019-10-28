package ninja.engineer.GroupMeProjetoFinal;

public class Members{
    private boolean status;
    private String name;
    private String objectId;
    private String proPic;

    public Members(){

    }
    public Members(String name, String objectId, String proPic){
        this.name = name;
        this.objectId = objectId;
        this.proPic = proPic;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getProPic() {
        return proPic;
    }

    public void setProPic(String proPic) {
        this.proPic = proPic;
    }
}
