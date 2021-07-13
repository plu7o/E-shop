package shop.client.ui.GUI.model;

import shop.common.valueObject.Article;

import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.Vector;

public class ArticleTableModel extends AbstractTableModel {
    private List<Article> articles;
    private String[] columnName = {"ID", "Name", "Price", "stock"};

    public ArticleTableModel(List<Article> currentArticles) {
        super();

        articles = new Vector<Article>();
        articles.addAll(currentArticles);
    }

    public void setArticles(List<Article> currentArticles) {
        articles.clear();
        articles.addAll(currentArticles);
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return articles.size();
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
                return selectedArticle.getStock();
            }
            default -> {
                return null;
            }
        }
    }
}
