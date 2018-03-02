package si.pronic.beer;

import javax.microedition.lcdui.Font;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.BorderFactory;

public class ChangeLocationScreen extends MainScreen 
{
	private BeerBuddy buddy;
	private BeerBuddyScreen buddyScreen;

	private VerticalFieldManager manager;
	private HorizontalFieldManager subManager;
	
	private EditField locField;
	private EditField timeField;
	
	private ButtonField changeButton;
	private ButtonField cancelButton;

    public ChangeLocationScreen(BeerBuddy buddy, BeerBuddyScreen buddyScreen)
    {
    	super();
        this.buddy = buddy;
        this.buddyScreen = buddyScreen;
    	setTitle("Beer Buddy for BBM™");
    	
        manager = new VerticalFieldManager(Manager.USE_ALL_WIDTH | Manager.FIELD_HCENTER);
        add(manager);
        
    	manager.add(new LabelField(" "));

    	LabelField label = new LabelField("Location:");
    	label.setFont(label.getFont().derive(Font.STYLE_BOLD));
    	manager.add(label);
    	
    	locField = new EditField(Field.USE_ALL_WIDTH);
    	locField.setEditable(true);
    	locField.setText(buddyScreen.addressValue);
//    	locField.setBorder(BorderFactory.createRoundedBorder(new XYEdges(1, 1, 1, 1)));
    	manager.add(locField);
    	
    	manager.add(new LabelField(" "));
    	
    	label = new LabelField("Time:");
    	label.setFont(label.getFont().derive(Font.STYLE_BOLD));
    	manager.add(label);
    	
    	
    	timeField = new EditField(Field.USE_ALL_WIDTH);
    	timeField.setEditable(true);
    	timeField.setText(buddyScreen.timeValue);
//    	timeField.setBorder(BorderFactory.createRoundedBorder(new XYEdges(1, 1, 1, 1)));
    	manager.add(timeField);
    	
    	manager.add(new LabelField(" "));
    	
    	subManager = new HorizontalFieldManager();
    	manager.add(subManager);
    	
        changeButton = new ButtonField("Change", Field.FIELD_HCENTER);
        changeButton.setChangeListener(changeListener);
        subManager.add(changeButton);
        
       	subManager.add(new LabelField(" "));

       	cancelButton = new ButtonField("Cancel", Field.FIELD_HCENTER);
        cancelButton.setChangeListener(cancelListener);
        subManager.add(cancelButton);
        
        int leftEmptySpace = (Display.getWidth() - subManager.getPreferredWidth()) / 2;
        subManager.setMargin(0, 0, 0, leftEmptySpace);
    }
    
    public boolean onSavePrompt() 
    {
        return true;
    }

    private FieldChangeListener changeListener = new FieldChangeListener()
    {
    	public void fieldChanged(Field field, int context)
    	{
    		final BeerBuddyScreen _screen = buddyScreen;
			buddy.invokeLater(new Runnable(){public void run(){
				buddyScreen.addressValue = locField.getText();
				buddyScreen.timeValue = timeField.getText();
				buddyScreen.addressLabel.setText("Location: " + buddyScreen.addressValue);
				buddyScreen.timeLabel.setText("Time: " + buddyScreen.timeValue);
				_screen.messageLabel.setText(_screen.getFormattedMessage());
				buddy.popScreen(ChangeLocationScreen.this);
				return;}});
    	}
    };
    
    private FieldChangeListener cancelListener = new FieldChangeListener()
    {
    	public void fieldChanged(Field field, int context)
    	{
			buddy.invokeLater(new Runnable(){public void run(){
				buddy.popScreen(ChangeLocationScreen.this);
				return;}});
     	}
    };
    
    
    
}
