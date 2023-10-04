package iesfranciscodelosrios.acd.models;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.*;

@XmlRootElement(name = "room")
@XmlAccessorType(XmlAccessType.FIELD)
public class Room {
    private String name; // Nombre de la sala

    public Room(String name) {
        this.name = name;
    }
    public Room(){}
    // Getter para obtener el nombre de la sala
    public String getName() {
        return name;
    }

}
