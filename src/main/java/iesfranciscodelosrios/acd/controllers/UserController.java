package iesfranciscodelosrios.acd.controllers;

import iesfranciscodelosrios.acd.models.User;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class UserController {
    private User user = new User(null, null, null);

    private boolean isUserLogedIn(String nickname) throws IOException {
        nickname = user.getNickname();
        boolean result = false;
        try {
            File file = new File("../../../resources/xmls/Users.xml");
            JAXBContext jaxbContext = JAXBContext.newInstance(User.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            User users = (User) unmarshaller.unmarshal(file);

            List<User> userList = users.getUsers();
            for (User user : userList) {
                if (user.getNickname().equals(nickname)) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}