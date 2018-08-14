package canvastest;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class Main {
  static int ROWS = 3;

  Main() {
    Object[][] data = { { "A", "B", "Snowboarding", new Integer(5) },
        { "C", "D", "Pool", new Integer(10) } };
    Object[] columnNames = { "firstname", "lastname", "age" };
    final JTable table = new JTable(data, columnNames) {
      @Override
      public Dimension getPreferredScrollableViewportSize() {
        Dimension d = getPreferredSize();
        int n = getRowHeight();
        return new Dimension(d.width, (n * ROWS));
      }
    };
    JPanel jPanel = new JPanel();
    jPanel.setLayout(new GridLayout());
    JScrollPane sp = new JScrollPane(table);
    jPanel.add(sp);
    JDialog jdialog = new JDialog();
    jdialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    jdialog.setContentPane(jPanel);

    jdialog.pack();
    jdialog.setVisible(true);
  }

  public static void main(String[] args) {
    new Main();
  }
}

