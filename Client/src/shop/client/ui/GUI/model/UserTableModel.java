package shop.client.ui.GUI.model;

import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.Vector;

import shop.common.valueObject.Article;
import shop.common.valueObject.User;

public class UserTableModel extends AbstractTableModel {

    private List<User> users;
    private String[] columnName = { "ID", "Name", "Username", "Address" };

    public UserTableModel(List<User> currentUser) {
        super();

        users =  new Vector<User>();
        users.addAll(currentUser);
    }

    public void setUser(List<User> currentUser) {
        users.clear();
        users.addAll(currentUser);
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return users.size();
    }

    @Override
    public int getColumnCount() {
        return columnName.length;
    }

    @Override
    public String getColumnName(int col) {
        return columnName[col];
    }

    @Override
    public Object getValueAt(int row, int col) {
        User selectedUser = users.get(row);
        switch (col) {
            case 0 -> {
                return selectedUser.getUserNr();
            }
            case 1 -> {
                return selectedUser.getName();
            }
            case 2 -> {
                return selectedUser.getUsername();
            }
            case 3 -> {
                return selectedUser.getAddress();
            }
            default -> {
                return null;
            }
        }
    }
}
