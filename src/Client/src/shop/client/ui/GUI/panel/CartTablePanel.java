package shop.client.ui.GUI.panel;

import shop.client.ui.GUI.model.ArticleTableModel;
import shop.client.ui.GUI.model.CartTableModel;
import shop.common.valueObject.Article;

import javax.swing.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CartTablePanel extends JTable {
    public CartTablePanel(Map<Article, Integer> shoppingCart) {
        super();

        CartTableModel cartTableModel = new CartTableModel(shoppingCart);
        setModel(cartTableModel);
        updateCart(shoppingCart);
    }

    public void updateCart(Map<Article, Integer> shoppingCart) {
        CartTableModel cartTableModel = (CartTableModel) getModel();
        cartTableModel.setCart(shoppingCart);
    }
}
