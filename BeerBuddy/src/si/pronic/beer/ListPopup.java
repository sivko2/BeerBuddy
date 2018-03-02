package si.pronic.beer;

import java.util.Vector;

import javax.microedition.lcdui.Font;

import net.rim.device.api.database.Row;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.component.table.SimpleList;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

public class ListPopup extends PopupScreen implements ListFieldCallback 
{
	public final static String MESSAGES[] = {
		"Hi! I badly need a drink. Please, meet me at",
		"I have great news to tell you. Join me at",
		"I want to party! Let's start having fun at",
		"Warning! Plenty of drink is waiting for you. If you have the guts to compete with me, challenge me at",
		"I challenge you to a beer fight at",
		"It's my birthday, drinks are on me. I'll be accepting gifts at",
		"Misery loves company. I'll be drowning my sorrow at",
		"Congratulations are in order. Meet me at",
		"It's time to celebrate. Join me at",
		"Beware! Lots of booze to go down. Come to",
		"Who is the ultimate drinking champion? If you think you can take me, come to",
		"Work was hell. I need to get it off my chest. Meet me for a drink at",
		"I just realised I've lost my wallet with all my drinking money. I will be accepting donations at",
		"My wife took all my money. Buy me a drink at",
		"You feeling lucky? I'll buy you a drink at",
		"Quench my thirst. I'll be at",
		"I'm bored. Let's be bored together. Meet me for a drink at",
		"Gossip Girl is in town. Don't miss the latest dish. Meet me at",
		"There's nothing a pint of lager can't fix. Come to",
		"Spirits for good spirits. Join me at",
		"Damn! My car is broken. I'll buy you a drink if you pick me up at"
//		"COFFEE TIME",
//		"FOOD TIME",
//		"ALCO BREAK TIME"
	};

	private Vector values;
	private ListField list;
	private BeerBuddyScreen screen;
	
	public ListPopup(BeerBuddyScreen screen, String titleValue, Vector values)
	{
		super(new VerticalFieldManager(NO_VERTICAL_SCROLL | NO_HORIZONTAL_SCROLL));
		this.values = values;
		this.screen = screen;
		
		
		LabelField title = new LabelField("List of invitations");
		title.setFont(title.getFont().derive(Font.STYLE_BOLD));
		add(title);
		add(new SeparatorField());

		HorizontalFieldManager subManager = new HorizontalFieldManager();
    	add(subManager);

    	final BeerBuddyScreen _screen = screen;
		final Vector _values = values;
		ButtonField okButton = new ButtonField("OK", ButtonField.CONSUME_CLICK | Field.FIELD_HCENTER);
		okButton.setChangeListener(new FieldChangeListener() 
		{
			public void fieldChanged(Field field, int context) 
			{
				int pos = list.getSelectedIndex();
				if (pos > -1)
				{
					_screen.messageValue = (String)_values.elementAt(pos);
					_screen.messageLabel.setText(_screen.getFormattedMessage());
				}
				close();
			}
		});
		subManager.add(okButton);
		
       	subManager.add(new LabelField(" "));

       	ButtonField cancelButton = new ButtonField("Cancel", ButtonField.CONSUME_CLICK | Field.FIELD_HCENTER);
		cancelButton.setChangeListener(new FieldChangeListener() 
		{
			public void fieldChanged(Field field, int context) 
			{
				
				close();
			}
		});
		subManager.add(cancelButton);

		VerticalFieldManager manager = new VerticalFieldManager(VERTICAL_SCROLL | VERTICAL_SCROLLBAR);
		add(manager);

		list = new ListField();
		list.setSize(values.size());
		list.setCallback(this);
		manager.add(list);
		
	}

    public void drawListRow(ListField list, Graphics g, int index, int y, int w) 
    { 
    	String row = (String)values.elementAt(index);
    	int drawColor = Color.WHITE;
        g.setColor(drawColor);
        g.drawText(row, 0, y, 0, w);
    }
    
    public Object get(ListField list, int index) 
    {
        return values.elementAt(index); 
    } 
    
    public int indexOfList(ListField list, String prefix, int string) 
    { 
        return values.indexOf(prefix, string); 
    } 
    
    public int getPreferredWidth(ListField list) 
    { 
        return Display.getWidth(); 
    } 

}
