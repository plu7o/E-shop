package shop.client.ui.GUI.panel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import shop.common.interfaces.ShopInterface;
import shop.common.valueObject.Article;

public class SearchArticlePanel extends JPanel {

    private ShopInterface shop;
    private SearchResultListener searchListener;

    private JTextField searchTextField;
    private JButton searchButton;

    public interface SearchResultListener {
        public void onSearchResult(List<Article> articles);
    }

    public SearchArticlePanel(ShopInterface shopInterface, SearchResultListener listener) {
        shop = shopInterface;
        searchListener = listener;

        setup();
        setupEvents();
    }

    private void setup() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        setLayout(gridBagLayout);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy = 0;

        JLabel searchLabel = new JLabel("Search: ");

        c.gridx = 0;
        c.weightx = 0.2;
        gridBagLayout.setConstraints(searchLabel, c);
        add(searchLabel);

        searchTextField = new JTextField();
        searchTextField.setToolTipText("Enter Search");
        c.gridx = 1;
        c.weightx = 0.6;
        gridBagLayout.setConstraints(searchTextField, c);
        add(searchTextField);

        searchButton = new JButton("Search!");
        c.gridx = 2;
        c.weightx = 0.2;
        gridBagLayout.setConstraints(searchButton, c);
        add(searchButton);

        setBorder(BorderFactory.createTitledBorder("Search"));
    }

    private void setupEvents() {
        searchButton.addActionListener(new SearchActionListener());
    }

    class SearchActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (e.getSource().equals(searchButton)) {
                String search = searchTextField.getText();
                List<Article> result;
                if (search.isEmpty()) {
                    result = shop.getAllArticles();
                    searchTextField.setText("");
                } else {
                    result = shop.searchArticle(search);
                    searchTextField.setText("");
                }
                searchListener.onSearchResult(result);
            }
        }
    }
}
