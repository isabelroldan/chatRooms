package iesfranciscodelosrios.acd.models;

import jakarta.xml.bind.annotation.*;

import java.io.Serializable;
import java.util.ArrayList;

@XmlRootElement(name = "users")
@XmlAccessorType(XmlAccessType.FIELD)
public class Users implements Serializable {

    @XmlElementWrapper(name = "users")
    @XmlElement(name = "user")
    ArrayList<User> users = new ArrayList<>();

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }
}
