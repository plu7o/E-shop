package shop.client.ui.GUI.panel;

import shop.client.ui.GUI.model.ArticleTableModel;
import shop.common.valueObject.Article;

import javax.swing.*;
import java.util.Collections;
import java.util.List;

public class ArticleTablePanel extends JTable {
    public ArticleTablePanel(List<Article> articles) {
        super();

        ArticleTableModel articleTableModel = new ArticleTableModel(articles);
        setModel(articleTableModel);
        updateArticles(articles);
    }

    public void updateArticles(List<Article> articles) {
        ArticleTableModel articleTableModel = (ArticleTableModel) getModel();
        articleTableModel.setArticles(articles);
    }
}
