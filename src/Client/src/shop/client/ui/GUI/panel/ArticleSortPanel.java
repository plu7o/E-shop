package shop.client.ui.GUI.panel;

import shop.common.interfaces.ShopInterface;
import shop.common.valueObject.Article;
import shop.server.domain.Shop;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

public class ArticleSortPanel extends JPanel {

    private ShopInterface shop;
    private ArticleSortListener articleSortListener;

    String[] sort_options = {"ID", "Name", "Stock", "Price"};
    private JComboBox sortDropDown = new JComboBox(sort_options);

    public interface ArticleSortListener {
        public void onSortID(List<Article> sortedList);
        public void onSortName(List<Article> sortedList);
        public void onSortPrice(List<Article> sortedList);
    }

    public ArticleSortPanel(ShopInterface shopInterface, ArticleSortListener listener) {
        shop = shopInterface;
        articleSortListener = listener;


        setup();
        setupEvents();
    }

    private void setup() {
        setLayout(new GridLayout(2,1,5,5));
        add(sortDropDown);
    }

    private void setupEvents() {
        sortDropDown.addActionListener(new SortListener());
    }

    public class SortListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if(e.getSource().equals(sortDropDown)) {
                switch (sortDropDown.getSelectedIndex()) {
                    case 0 -> {
                        List<Article> articles = shop.getSorted("ID", true);
                        articleSortListener.onSortID(articles);
                        //ID
                    }
                    case 1 -> {
                        List<Article> articles = shop.getSorted("name", true);
                        articleSortListener.onSortName(articles);
                        //Name
                    }
                    case 2 -> {
                        List<Article> articles = shop.getSorted("price", true);
                        articleSortListener.onSortPrice(articles);
                        //Pirce
                    }
                    case 3 -> {
                        List<Article> articles = shop.getSorted("stock", true);
                        articleSortListener.onSortPrice(articles);
                        //stock
                    }
                }
            }
        }
    }
}
