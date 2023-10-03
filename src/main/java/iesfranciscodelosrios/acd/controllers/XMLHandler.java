package iesfranciscodelosrios.acd.controllers;

import iesfranciscodelosrios.acd.models.User;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.List;


public class XMLHandler {
    public static void saveUsers(List<User> users, String filePath) {
        try {
            File file = new File(filePath);
            JAXBContext jaxbContext = JAXBContext.newInstance(User.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(users, file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<User> loadUsers(String filePath) {
        try {
            File file = new File(filePath);
            JAXBContext jaxbContext = JAXBContext.newInstance(User.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            User users = (User) unmarshaller.unmarshal(file);
            return users.getUsers();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

