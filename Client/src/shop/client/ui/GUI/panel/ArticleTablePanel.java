package shop.client.ui.GUI.panel;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.Collections;
import java.util.List;

import shop.client.ui.GUI.model.ArticleTableModel;
import shop.common.valueObject.Article;

public class ArticleTablePanel extends JTable {
    public ArticleTablePanel(List<Article> articles) {
        super();

        ArticleTableModel articleTableModel = new ArticleTableModel(articles);
        setModel(articleTableModel);
        updateArticles(articles);
    }

    public void updateArticles(List<Article> articles) {
        Collections.sort(articles, (b1, b2) -> b1.getArticleNr() - b2.getArticleNr());
        ArticleTableModel articleTableModel = (ArticleTableModel) getModel();
        articleTableModel.setArticles(articles);
    }
}
