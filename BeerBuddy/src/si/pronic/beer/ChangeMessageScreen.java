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

public class ChangeMessageScreen extends MainScreen 
{
	private BeerBuddy buddy;
	private BeerBuddyScreen buddyScreen;

	private VerticalFieldManager manager;
	private HorizontalFieldManager subManager;
	
	private EditField textField;
	
	private ButtonField changeButton;
	private ButtonField cancelButton;

    public ChangeMessageScreen(BeerBuddy buddy, BeerBuddyScreen buddyScreen)
    {
    	super();
        this.buddy = buddy;
        this.buddyScreen = buddyScreen;
    	setTitle("Beer Buddy for BBM™");
    	
        manager = new VerticalFieldManager(Manager.USE_ALL_WIDTH | Manager.FIELD_HCENTER);
        add(manager);
        
    	manager.add(new LabelField(" "));

    	LabelField label = new LabelField("Message:");
    	label.setFont(label.getFont().derive(Font.STYLE_BOLD));
    	manager.add(label);
    	
    	textField = new EditField(Field.USE_ALL_WIDTH);
    	textField.setEditable(true);
    	textField.setText(buddyScreen.messageValue);
//    	locField.setBorder(BorderFactory.createRoundedBorder(new XYEdges(1, 1, 1, 1)));
    	manager.add(textField);
    	
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
				buddyScreen.messageValue = textField.getText();
				_screen.messageLabel.setText(_screen.getFormattedMessage());
				buddy.popScreen(ChangeMessageScreen.this);
				return;}});
    	}
    };
    
    private FieldChangeListener cancelListener = new FieldChangeListener()
    {
    	public void fieldChanged(Field field, int context)
    	{
			buddy.invokeLater(new Runnable(){public void run(){
				buddy.popScreen(ChangeMessageScreen.this);
				return;}});
     	}
    };
    
    
    
}
