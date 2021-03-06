package net.geco.ui.basics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/*
 * Adapted from:
 * http://stackoverflow.com/questions/10368856/jcombobox-filter-in-java-look-and-feel-independent
 */
public class FilterComboBox extends JComboBox {

	public interface LazyLoader {
		public Object[] loadItems();
	}
	
	private Object[] allItems;

	private JTextField searchField;

	private FocusListener focusListener;

	public FilterComboBox() {
        setEditable(true);
        searchField = (JTextField) this.getEditor().getEditorComponent();
        setupSearchBehavior();
    }

    public void setItems(Object[] items) {
    	// disable lazy load
    	searchField.removeFocusListener(focusListener);
    	this.allItems = items;
    	setModel(new DefaultComboBoxModel(items));
    }

    public void lazyLoadItems(final LazyLoader loader) {
    	focusListener = new FocusListener() {
    		public void focusLost(FocusEvent e) {}
    		public void focusGained(FocusEvent e) {
    			setItems(loader.loadItems());
    			unsetDefaultSelection();
    		}
    	};
		searchField.addFocusListener(focusListener);
    }
    
    private void setupSearchBehavior() {
        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent ke) {
            	if( ke.getKeyCode() == KeyEvent.VK_ESCAPE || ke.getKeyCode() == KeyEvent.VK_ENTER ) {
            		hidePopup();
            	} else if( arrow_key_not_pressed(ke) ) {
	                SwingUtilities.invokeLater(new Runnable() {
	                    public void run() {
	                    	if( ! isPopupVisible() ) { showPopup(); }
	                        String enteredText = searchField.getText();
							filterComboItems(enteredText);
	                        searchField.setText(enteredText); // redisplay search text
	                    }
	                });
            	}
            }

            private boolean arrow_key_not_pressed(KeyEvent ke) {
				return 	ke.getKeyCode() != KeyEvent.VK_UP && ke.getKeyCode() != KeyEvent.VK_DOWN && 
						ke.getKeyCode() != KeyEvent.VK_LEFT && ke.getKeyCode() != KeyEvent.VK_RIGHT;
			}
        });
        
        addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if( getSelectedItem() != null ){
					searchField.setText(getSelectedItem().toString());
				}
			}
		});
	}
   
    private void filterComboItems(String enteredText) {
        String filterText = enteredText.toLowerCase();
        List<Object> filteredItems = new ArrayList<Object>();
        for (Object item : allItems) {
			if( item.toString().toLowerCase().contains(filterText) ) {
                filteredItems.add(item);
            }			
		}
        
        DefaultComboBoxModel model = (DefaultComboBoxModel) this.getModel();
        model.removeAllElements();
        for (Object item: filteredItems) {
        	model.addElement(item);
        }
        unsetDefaultSelection();
    }

	public void unsetDefaultSelection() {
		setSelectedIndex(-1); // no default selection
	}

}
