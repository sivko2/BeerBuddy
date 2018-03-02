package si.pronic.beer;

import java.util.Vector;

import javax.microedition.lcdui.Font;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.PasswordEditField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

public class LoginScreen extends Dialog 
{
	private static String LOGIN_CHOICES[] = {"Login", "Cancel"};
	private static int LOGIN_VALUES[] = {Dialog.OK, Dialog.CANCEL};

	private EditField usernameEditField;
	private PasswordEditField passwordEditField;
	
	public LoginScreen()
	{
		super("Login", LOGIN_CHOICES, LOGIN_VALUES, Dialog.OK, 
				Bitmap.getPredefinedBitmap(Bitmap.INFORMATION), Dialog.GLOBAL_STATUS);

		VerticalFieldManager manager = new VerticalFieldManager(Manager.USE_ALL_WIDTH | Manager.FIELD_HCENTER);
        add(manager);
		
		LabelField usernameField = new LabelField("Twitter username:");
		usernameField.setFont(usernameField.getFont().derive(Font.STYLE_BOLD));
		manager.add(usernameField);
		
    	usernameEditField = new EditField(Field.USE_ALL_WIDTH);
    	usernameEditField.setEditable(true);
    	manager.add(usernameEditField);

    	manager.add(new LabelField(" "));
		
		LabelField passwordField = new LabelField("Twitter password*:");
		passwordField.setFont(passwordField.getFont().derive(Font.STYLE_BOLD));
		manager.add(passwordField);

    	passwordEditField = new PasswordEditField("", "", 20, Field.USE_ALL_WIDTH);
    	passwordEditField.setEditable(true);
    	manager.add(passwordEditField);

		LabelField noteField = new LabelField("* Password will not be stored.");
		noteField.setFont(passwordField.getFont().derive(Font.STYLE_ITALIC));
		manager.add(noteField);

		manager.add(new LabelField(" "));
		
	}
	
	public String getUsername()
	{
		return usernameEditField.getText();
	}
	
	public String getPassword()
	{
		return passwordEditField.getText();
	}
}
