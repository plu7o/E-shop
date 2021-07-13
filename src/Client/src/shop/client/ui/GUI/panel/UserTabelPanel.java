package shop.client.ui.GUI.panel;

import shop.client.ui.GUI.model.UserTableModel;
import shop.common.valueObject.User;

import javax.swing.*;
import java.util.Collections;
import java.util.List;

public class UserTabelPanel extends JTable {

    public UserTabelPanel(List<User> users) {
        super();

        UserTableModel userTableModel = new UserTableModel(users);
        setModel(userTableModel);
        updateUser(users);
    }

    public void updateUser(List<User> users) {
        Collections.sort(users, (b1, b2) -> b1.getUserNr() - b2.getUserNr());
        UserTableModel userTableModel = (UserTableModel) getModel();
        userTableModel.setUser(users);
    }
}
