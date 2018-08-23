import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.j3d.BranchGroup;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.fem.object.Point;

import com.sun.j3d.utils.universe.SimpleUniverse;

public class LineModel extends JPanel implements MouseListener {

	@Override
	public void mouseClicked(MouseEvent e) {
		Object[] columnNames = { "X", "Y", "Z" };
	    TableModel model = new DefaultTableModel(columnNames,3) {
	    	public Class<?> getColumnClass(int columnIndex) {
	    		return Float.class;
	    	};
	    };
	    final JTable table = new JTable(model) {
	      @Override
	      public Dimension getPreferredScrollableViewportSize() {
	        Dimension d = getPreferredSize();
	        int n = getRowHeight();
	        return new Dimension(d.width, (n * 3));
	      }
	    };
	    
//	    table.getModel().addTableModelListener(new MyTableModelListener(table,line));
	    JPanel jPanel = new JPanel();
	    jPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
	    jPanel.setLayout(new BorderLayout(0, 0));

	    jPanel.setLayout(new GridLayout());
	    JScrollPane sp = new JScrollPane(table);
	    jPanel.add(sp);
	    JDialog jdialog = new JDialog();
	    jdialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    jdialog.setContentPane(jPanel);

	    jdialog.pack();
	    jdialog.setVisible(true);
	    jdialog.addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                System.out.println("触发windowClosing事件"); 
                TableModel model = table.getModel();
					
					for (int i = 0; i < model.getRowCount(); i++) {
						Point point = new Point();
						for (int j = 0; j < model.getColumnCount(); j++) {
							switch (j) {
							case 0:
								point.setX((Float)model.getValueAt(i, j));
								break;
							case 1:
								point.setY((Float)model.getValueAt(i, j));
								break;
							case 2:
								point.setZ((Float)model.getValueAt(i, j));
								break;
							default:
								break;
							}
						}
						
						
						line.getEndpoints().add(point);
					}
            }  
  
            public void windowClosed(WindowEvent e)  
            {  
            	BranchGroup scene = createSceneGraph(line);  
		        SimpleUniverse u = new SimpleUniverse(jp);  
		        u.getViewingPlatform().setNominalViewingTransform();  
		        u.addBranchGraph(scene); 
		        contentPane.remove(jPanel);
            }  
        });
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
