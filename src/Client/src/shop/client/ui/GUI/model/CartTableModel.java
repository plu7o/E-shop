package shop.client.ui.GUI.model;

import shop.common.valueObject.Article;
import shop.common.valueObject.MassArticle;
import javax.swing.table.AbstractTableModel;
import java.util.*;

public class CartTableModel extends AbstractTableModel {
    private Map<Article, Integer> cart;
    private List<Article> articles;
    private List<Integer> amount;
    private String[] columnName = {"ID", "Name", "Price", "Amount"};

    public CartTableModel(Map<Article, Integer> shoppingCart) {
        super();
        cart = new HashMap<>(shoppingCart);
        articles = new ArrayList<>(cart.keySet());
        amount = new ArrayList<>(cart.values());

    }

    public void setCart(Map<Article, Integer> shoppingCart) {
        cart.clear();
        cart.putAll(shoppingCart);
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return cart.size();
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

        Article selectedArticle = articles.get(row);
        int amount_value = amount.get(row);
        switch (col) {
            case 0 -> {
                return selectedArticle.getArticleNr();
            }
            case 1 -> {
                return selectedArticle.getName();
            }
            case 2 -> {
                return selectedArticle.getPrice();
            }
            case 3 -> {
                return amount_value;
            }
            default -> {
                return null;
            }
        }
    }
}
