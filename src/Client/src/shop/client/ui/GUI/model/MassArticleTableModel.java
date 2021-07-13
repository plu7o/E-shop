package shop.client.ui.GUI.model;

import shop.common.valueObject.MassArticle;

import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.Vector;

public class MassArticleTableModel extends AbstractTableModel {
    private List<MassArticle> articles;
    private String[] columnName = {"ID", "Name", "Price", "Stock", "Package"};

    public MassArticleTableModel(List<MassArticle> currentArticles) {
        super();

        articles = new Vector<MassArticle>();
        articles.addAll(currentArticles);
    }

    public void setArticles(List<MassArticle> currentArticles) {
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
        MassArticle selectedArticle = articles.get(row);
        switch (col) {
            case 0 -> {
                return selectedArticle.getArticleNr();
            }
            case 1 -> {
                return selectedArticle.getName();
            }
            case 2 -> {
                return selectedArticle.getPriceStr();
            }
            case 3 -> {
                return selectedArticle.getStock();
            }
            case 4 -> {
                return selectedArticle.getPackageSize();
            }
            default -> {
                return null;
            }
        }
    }
}
