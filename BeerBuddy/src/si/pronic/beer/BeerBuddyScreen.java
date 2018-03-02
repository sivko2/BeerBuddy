package si.pronic.beer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.location.*;
import javax.microedition.pim.Contact;
import javax.microedition.pim.ContactList;
import javax.microedition.pim.PIM;
import javax.microedition.pim.PIMItem;
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.TextMessage;

import com.blackberry.facebook.ApplicationSettings;
import com.blackberry.facebook.Facebook;
import com.blackberry.facebook.FacebookException;
import com.blackberry.facebook.inf.User;
import com.twitterapime.rest.Credential;
import com.twitterapime.rest.GeoLocation;
import com.twitterapime.rest.TweetER;
import com.twitterapime.rest.UserAccountManager;
import com.twitterapime.search.Tweet;

import net.rim.blackberry.api.bbm.platform.*;
import net.rim.blackberry.api.bbm.platform.io.*;
import net.rim.blackberry.api.bbm.platform.profile.*;
import net.rim.blackberry.api.bbm.platform.service.MessagingService;
import net.rim.blackberry.api.invoke.Invoke;
import net.rim.blackberry.api.invoke.MessageArguments;
import net.rim.blackberry.api.mail.Address;
import net.rim.blackberry.api.mail.Folder;
import net.rim.blackberry.api.mail.Message;
import net.rim.blackberry.api.mail.Session;
import net.rim.blackberry.api.mail.Store;
import net.rim.blackberry.api.mail.Transport;
import net.rim.blackberry.api.pdap.BlackBerryContact;
import net.rim.blackberry.api.pdap.BlackBerryContactList;
import net.rim.blackberry.api.sms.SMS;
import net.rim.device.api.gps.*;
import net.rim.device.api.lbs.*;
import net.rim.device.api.lbs.maps.MapFactory;
import net.rim.device.api.lbs.maps.model.MapLocation;
import net.rim.device.api.lbs.maps.model.Mappable;
import net.rim.device.api.lbs.maps.utils.MappableVector;

import net.rim.device.api.system.*;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.ui.decor.BackgroundFactory;
import net.rim.device.api.ui.decor.Border;
import net.rim.device.api.ui.decor.BorderFactory;
import net.rim.device.api.util.*;

//import com.mobiroo.pronic.beerbuddyforbbm.*;

public final class BeerBuddyScreen extends MainScreen implements Runnable
{
	String FB_NEXT_URL = "http://www.facebook.com/connect/login_success.html";
	String FB_APPLICATION_ID = "240972715947829";
	String FB_APPLICATION_SECRET = "69a8a2b1fcff1af46228d3bc8bd92d4e";
	String[] FB_PERMISSIONS = Facebook.Permissions.PUBLISHING_PERMISSIONS;
	
	String TW_CONSUMER_KEY = "idqLaJKPrAcwgwF4NEGWJQ";
	String TW_CONSUMER_SECRET = "TZdmbGNjZy24uJHrWbXu1qdtXC0nVDOlrwyjt3TE7E";
	
	private BeerBuddy buddy;
	
	private VerticalFieldManager manager;
	private HorizontalFieldManager hManager;
	
	private BitmapField logoImage;
	private Bitmap logoBitmap;
	
	private ButtonField locateButton;
	private ButtonField changeButton;
	private ButtonField inviteButton;
	private ButtonField messageButton;
	private ButtonField writeButton;
	
	private LabelField satLabel;
	private LabelField latitudeLabel;
	private LabelField longitudeLabel;
	private LabelField altitudeLabel;
	public LabelField addressLabel;
	public LabelField timeLabel;
	public LabelField messageLabel;
	
	private RadioButtonGroup msgGroup = new RadioButtonGroup();
	private RadioButtonField bbmBox;
	private RadioButtonField facebookBox;
	private RadioButtonField twitterBox;
	private RadioButtonField smsBox;
	private RadioButtonField emailBox;
	
	public String addressValue = "";
	public String timeValue = "";
	public String messageValue = ListPopup.MESSAGES[0];
	public String twUsername;
	public String twPassword;
	
	private int sats = 0;
	private double latitude = 0;
	private double longitude = 0;
	private float altitude = 0;
	
	private String filename;
	
	private boolean gpsAvailable = false;
	private boolean gpsThreadRun = false;
	
	private BlackBerryLocation location;

	private long time;
	private AddressInfo addrInfo;
	
    public BeerBuddyScreen(BeerBuddy buddy)
    {
    	super();
		
        this.buddy = buddy;
    	setTitle("Beer Buddy for BBM™");
    	
    	gpsAvailable = GPSInfo.isGPSModeAvailable(GPSInfo.GPS_MODE_AUTONOMOUS);
        
//        add(new MobirooBanner());
//        add(new LabelField(" "));

        manager = new VerticalFieldManager(Manager.USE_ALL_WIDTH | Manager.FIELD_HCENTER);

        add(manager);
        
        logoBitmap = Bitmap.getBitmapResource("img/beer.png");
        logoImage = new BitmapField(logoBitmap, Field.FIELD_HCENTER);
        manager.add(logoImage);
        
        locateButton = new ButtonField("Where am I?", Field.FIELD_HCENTER);
        locateButton.setChangeListener(locateListener);
       	locateButton.setEditable(gpsAvailable);
        manager.add(locateButton);
        
        manager.add(new LabelField(" "));
        
        satLabel = new LabelField();
        satLabel.setBorder(BorderFactory.createSimpleBorder(new XYEdges(5, 10, 5, 5), Border.STYLE_TRANSPARENT));
        manager.add(satLabel);

        latitudeLabel = new LabelField();
        latitudeLabel.setBorder(BorderFactory.createSimpleBorder(new XYEdges(5, 10, 5, 5), Border.STYLE_TRANSPARENT));
        manager.add(latitudeLabel);
        
        longitudeLabel = new LabelField();
        longitudeLabel.setBorder(BorderFactory.createSimpleBorder(new XYEdges(5, 10, 5, 5), Border.STYLE_TRANSPARENT));
        manager.add(longitudeLabel);
        
        altitudeLabel = new LabelField();
        altitudeLabel.setBorder(BorderFactory.createSimpleBorder(new XYEdges(5, 10, 5, 5), Border.STYLE_TRANSPARENT));
        manager.add(altitudeLabel);
        
        addressLabel = new LabelField();
        addressLabel.setBorder(BorderFactory.createSimpleBorder(new XYEdges(5, 10, 5, 5), Border.STYLE_TRANSPARENT));
        manager.add(addressLabel);

        timeLabel = new LabelField();
        timeLabel.setBorder(BorderFactory.createSimpleBorder(new XYEdges(5, 10, 5, 5), Border.STYLE_TRANSPARENT));
        manager.add(timeLabel);

        manager.add(new LabelField(" "));
        
        changeButton = new ButtonField("Change location and time", Field.FIELD_HCENTER);
        changeButton.setChangeListener(changeListener);
        manager.add(changeButton);

        manager.add(new LabelField(" "));
        
        manager.add(new SeparatorField());

        manager.add(new LabelField(" "));
        
        LabelField f1 = new LabelField("Invitation message:");
        f1.setBorder(BorderFactory.createSimpleBorder(new XYEdges(5, 10, 5, 5), Border.STYLE_TRANSPARENT));
        manager.add(f1);
        

        messageLabel = new LabelField();
        messageLabel.setFont(messageLabel.getFont().derive(Font.ITALIC));
        messageLabel.setBorder(BorderFactory.createSimpleBorder(new XYEdges(5, 10, 5, 5), Border.STYLE_TRANSPARENT));
        manager.add(messageLabel);

        manager.add(new LabelField(" "));
        
        hManager = new HorizontalFieldManager();
        manager.add(hManager);
        
        hManager.add(new LabelField(" "));

        messageButton = new ButtonField("Change");
        messageButton.setChangeListener(messageListener);
        hManager.add(messageButton);
        
        writeButton = new ButtonField("Personalize");
        writeButton.setChangeListener(writeListener);
        hManager.add(writeButton);
        
        manager.add(new LabelField(" "));
        
        bbmBox = new RadioButtonField(" publish on BlackBerry® Messenger", msgGroup, true);
        bbmBox.setBorder(BorderFactory.createSimpleBorder(new XYEdges(5, 10, 5, 5), Border.STYLE_TRANSPARENT));
        manager.add(bbmBox);
        
        facebookBox = new RadioButtonField(" publish on Facebook® wall", msgGroup, false);
        facebookBox.setBorder(BorderFactory.createSimpleBorder(new XYEdges(5, 10, 5, 5), Border.STYLE_TRANSPARENT));
        manager.add(facebookBox);
        
        twitterBox = new RadioButtonField(" tweet on Twitter®", msgGroup, false);
        twitterBox.setBorder(BorderFactory.createSimpleBorder(new XYEdges(5, 10, 5, 5), Border.STYLE_TRANSPARENT));
        manager.add(twitterBox);
        
        smsBox = new RadioButtonField(" send text message (SMS)", msgGroup, false);
        smsBox.setBorder(BorderFactory.createSimpleBorder(new XYEdges(5, 10, 5, 5), Border.STYLE_TRANSPARENT));
        manager.add(smsBox);
        
        emailBox = new RadioButtonField(" send e-mail", msgGroup, false);
        emailBox.setBorder(BorderFactory.createSimpleBorder(new XYEdges(5, 10, 5, 5), Border.STYLE_TRANSPARENT));
        manager.add(emailBox);
        
        manager.add(new LabelField(" "));
        
        inviteButton = new ButtonField("Invite", Field.FIELD_HCENTER);
        inviteButton.setChangeListener(inviteListener);
        manager.add(inviteButton);
        
        manager.add(new LabelField(" "));
        
        manager.add(new SeparatorField());

        manager.add(new LabelField(" "));
        
        clear();
        
        LabelField f2 = new LabelField("Map of location:");
        f2.setBorder(BorderFactory.createSimpleBorder(new XYEdges(5, 10, 5, 5), Border.STYLE_TRANSPARENT));
        manager.add(f2);
        
        manager.add(mapField);
        
        locateButton.setFocus();
        
    }
    
    private void clear()
    {
    	sats = 0;
    	satLabel.setText("GPS: off");
    	latitude = 0;
    	latitudeLabel.setText("Latitude: -");
    	longitude = 0;
    	longitudeLabel.setText("Longitude: -");
    	altitude = 0;
    	altitudeLabel.setText("Altitude: -");
    	addressValue = "our favourite pub";
        addressLabel.setText("Location: " + addressValue);
        timeValue = "right now"; 
        timeLabel.setText("Time: " + timeValue);
        time = 0;
        messageLabel.setText(getFormattedMessage());
        mapField.setImage(null);
        filename = null;
    }
    
    public boolean onClose()
    {
//    	((BeerBuddy)UiApplication.getUiApplication()).getMobirooSplash().mobirooOnClose();
    	System.exit(0);
    	return true;
    }

    BitmapField mapField = new BitmapField()
    {
    	protected boolean navigationClick(int status, int time) 
    	{
    	    return true;
    	}

    	protected boolean navigationUnclick(int status, int time) 
    	{
    		fieldChangeNotify(FieldChangeListener.PROGRAMMATIC);
    	    return true;
    	}
    };

    protected void makeMenu(Menu menu, int instance)
    {
    	super.makeMenu(menu, instance);
    	if (instance != Menu.INSTANCE_CONTEXT)
    	{
    		menu.add(resetMenuItem);
    		menu.add(inviteDLMenuItem);
    		menu.add(helpMenuItem);
    		menu.add(aboutMenuItem);
    	}
    }
    
    private MenuItem inviteDLMenuItem = new MenuItem(new StringProvider("Invite to download"), 110, 10)
    {
    	public void run()
    	{
			buddy.invokeLater(new Runnable(){public void run(){
				
				MessagingService messagingService = buddy.platformContext.getMessagingService();
				messagingService.sendDownloadInvitation();
				return;}});
    	}
    }; 
    
    private MenuItem resetMenuItem = new MenuItem(new StringProvider("Reset"), 110, 10)
    {
    	public void run()
    	{
			buddy.invokeLater(new Runnable(){public void run(){
				clear();
				return;}});
    	}
    }; 
    
    private MenuItem aboutMenuItem = new MenuItem(new StringProvider("About"), 110, 10)
    {
    	public void run()
    	{
    		String text = "BEER BUDDY FOR BBM™\n\nDrinking invitation tool\n\n" +
    				"(c)2011, Pronic, Meselina Ponikvar Verhovsek s.p.\n";
    		Dialog.alert(text);
    	}
    }; 
    
    private MenuItem helpMenuItem = new MenuItem(new StringProvider("Help"), 110, 10)
    {
    	public void run()
    	{
        	EulaPopup popup = new EulaPopup("HELP",HELP_TEXT, "Close");
        	buddy.pushScreen(popup);
    	}
    }; 
    
    private FieldChangeListener clearListener = new FieldChangeListener()
    {
    	public void fieldChanged(Field field, int context)
    	{
			buddy.invokeLater(new Runnable(){public void run(){
				clear();
				return;}});
    	}
    };
    
    private FieldChangeListener mapListener = new FieldChangeListener()
    {
    	public void fieldChanged(Field field, int context)
    	{
    		if (filename != null)
    		{
				buddy.invokeLater(new Runnable(){public void run(){
					buddy.platformContext.getMessagingService().sendFile(filename, getFormattedMessage());
				}});
    		}
    	}
    };
    
    private FieldChangeListener changeListener = new FieldChangeListener()
    {
    	public void fieldChanged(Field field, int context)
    	{
    		final ChangeLocationScreen screen = new ChangeLocationScreen(buddy, BeerBuddyScreen.this);
			buddy.invokeLater(new Runnable(){public void run(){
				buddy.pushScreen(screen);
				return;}});
    	}
    };
    
    private FieldChangeListener messageListener = new FieldChangeListener()
    {
    	public void fieldChanged(Field field, int context)
    	{
			buddy.invokeLater(new Runnable(){public void run(){
				Vector messages = new Vector();
				for (int i = 0; i < ListPopup.MESSAGES.length; i++)
				{
					messages.addElement(ListPopup.MESSAGES[i]);
				}
	        	ListPopup popup = new ListPopup(BeerBuddyScreen.this, "List of messages", messages);
	        	buddy.pushScreen(popup);
				return;}});
    	}
    };
    
    private FieldChangeListener writeListener = new FieldChangeListener()
    {
    	public void fieldChanged(Field field, int context)
    	{
    		final ChangeMessageScreen screen = new ChangeMessageScreen(buddy, BeerBuddyScreen.this);
			buddy.invokeLater(new Runnable(){public void run(){
				buddy.pushScreen(screen);
				return;}});
    	}
    };
    
    private FieldChangeListener locateListener = new FieldChangeListener()
    {
    	public void fieldChanged(Field field, int context)
    	{
			buddy.invokeLater(new Runnable(){public void run(){
				satLabel.setText("Satellites: searching...");
				latitudeLabel.setText("Latitude: retrieving...");
				longitudeLabel.setText("Longitude: retrieving...");
				altitudeLabel.setText("Altitude: retrieving...");
				addressLabel.setText("Location: retrieving...");
				mapField.setImage(null);
	            enableUI(false);
			}});
			Thread thread = new Thread(BeerBuddyScreen.this);
    		thread.start();
    		GpsThread gpsThread = new GpsThread();
    		gpsThread.start();
    	}
    };
    
    private void enableUI(boolean ok)
    {
        messageButton.setEnabled(ok);
        locateButton.setEnabled(ok);
        inviteButton.setEnabled(ok);
        changeButton.setEnabled(ok);
        writeButton.setEnabled(ok);
        facebookBox.setEnabled(ok);
        facebookBox.setEditable(ok);
        twitterBox.setEnabled(ok);
        twitterBox.setEditable(ok);
        bbmBox.setEnabled(ok);
        bbmBox.setEditable(ok);
    }
    
    public String getFormattedMessage()
    {
		String msg = messageValue + " " +
 	   	addressValue + " (" + timeValue + ")" + (latitude != 0 && longitude != 0 ? " [latitude: " + latitude + 
 	   			", longitude: " + longitude + "]. " + 
 	   			"http://maps.BlackBerry.com?lat=" + latitude + "&lon=" + longitude + "&label=Meeting%20place&z=1" 
 	   			: ".");
		return msg;
    }
    
    public String getFormattedMessageForFacebook()
    {
		String msg = messageValue + " " +
 	   	addressValue + " (" + timeValue + ")" + (latitude != 0 && longitude != 0 ? " [latitude: " + latitude + 
 	   			", longitude: " + longitude + "]. " + 
 	   			"http://maps.google.com/maps?q=" + latitude + "," + longitude 
 	   			: ".");
		return msg;
    }
    
    public String getFormattedMessageForTwitter()
    {
		String msg = "Join me for a drink at " + addressValue + " (" + timeValue + ").";
		return (msg.length() > 140 ? msg.substring(0, 137) + "..." : msg);
    }
    
    public String getUrlForTwitter()
    {
		return (latitude != 0 && longitude != 0 ? "Invitation location: http://maps.google.com/maps?q=" + latitude + "," + longitude 
 	   			: null);
    }
    
    private FieldChangeListener inviteListener = new FieldChangeListener()
    {
    	public void fieldChanged(Field field, int context)
    	{
    		String msg = getFormattedMessage();
    		try 
    		{
    			// FACEBOOK
				if (facebookBox.isSelected())
				{
					ApplicationSettings appSettings = new ApplicationSettings(FB_NEXT_URL, FB_APPLICATION_ID, 
							FB_APPLICATION_SECRET, FB_PERMISSIONS);
					Facebook facebook = Facebook.getInstance(appSettings);
					try
					{
						User user = facebook.getCurrentUser();
						final String retVal = user.publishStatus(getFormattedMessageForFacebook());
/*						buddy.invokeLater(new Runnable(){public void run(){
							Dialog.alert("Facebook invitation is published on the wall.");
							return;
						}});*/
					}
					catch (Exception ex)
					{
    		    		Dialog.alert(ex.getMessage());
					}
				}
				
				// TWITTER
				if (twitterBox.isSelected())
				{
					LoginScreen loginScreen = new LoginScreen();
					loginScreen.doModal();
					if (loginScreen.getSelectedValue() == 0)
					{
						Credential cred = new Credential(loginScreen.getUsername(), loginScreen.getPassword(), 
								TW_CONSUMER_KEY, TW_CONSUMER_SECRET);
						final UserAccountManager uaManager = UserAccountManager.getInstance(cred);
						if (uaManager.verifyCredential())
						{
							if (longitude != 0 && latitude != 0)
							{
								GeoLocation location = new GeoLocation(String.valueOf(latitude), String.valueOf(longitude));
								Tweet tweet = new Tweet(getFormattedMessageForTwitter(), location);
								TweetER tweeter = TweetER.getInstance(uaManager);
								tweet = tweeter.post(tweet);
								tweet = new Tweet(getUrlForTwitter(), location);
								tweet = tweeter.post(tweet);
							}
							else
							{
								Tweet tweet = new Tweet(getFormattedMessageForTwitter());
								TweetER tweeter = TweetER.getInstance(uaManager);
								tweet = tweeter.post(tweet);
							}
							buddy.invokeLater(new Runnable(){public void run(){
								Dialog.alert("Invitation is tweeted.");
								return;
							}});
						}
						else
						{
							buddy.invokeLater(new Runnable(){public void run(){
								Dialog.alert("Wrong Twitter username or password.");
								return;
							}});
						}
					}
				}
				
				// BBM
				if (bbmBox.isSelected())
				{
					buddy.platformContext.getUIService().startBBMChat(msg);
				}
				
				// SMS
				if (smsBox.isSelected())
				{
					BlackBerryContactList blackBerryContactList = (BlackBerryContactList)PIM.getInstance().openPIMList(PIM.CONTACT_LIST, PIM.READ_ONLY);
					PIMItem contact = blackBerryContactList.choose();
					if (contact != null)
					{
						String msisdn = contact.getString(Contact.TEL, Contact.ATTR_NONE);

						MessageConnection mc = (MessageConnection)Connector.open("sms://");
						TextMessage m = (TextMessage)mc.newMessage(MessageConnection.TEXT_MESSAGE );
						m.setAddress("sms://" + msisdn);
						m.setPayloadText(getFormattedMessage());
						
						Invoke.invokeApplication(Invoke.APP_TYPE_MESSAGES, new MessageArguments(m));
					}
				}
				
				// EMAIL
				if (emailBox.isSelected())
				{
					BlackBerryContactList blackBerryContactList = (BlackBerryContactList)PIM.getInstance().openPIMList(PIM.CONTACT_LIST, PIM.READ_ONLY);
					PIMItem contact = blackBerryContactList.choose();
					if (contact != null)
					{
						String email = contact.getString(Contact.EMAIL, 0);

						Store store = Session.getDefaultInstance().getStore(); 
						
						Message m = new Message();
						Address a = new Address(email, "Beer Buddy");
						Address[] addresses = {a};
						m.addRecipients(net.rim.blackberry.api.mail.Message.RecipientType.TO, addresses);
						m.setContent(getFormattedMessage());
						m.setSubject("Beer Buddy Invitation");
					    m.setPriority(Message.Priority.HIGH); 
					    
					    Transport.send(m);
//						Invoke.invokeApplication(Invoke.APP_TYPE_MESSAGES, new MessageArguments(m));
						buddy.invokeLater(new Runnable(){public void run(){
							Dialog.alert("Email is sent.");
							return;
						}});
					}
				}
    		}
    		catch (Exception ex)
    		{
				final Exception _ex = ex;
//				if (ex != null && ex.getMessage() != null && ex.getMessage().length() > 0)
//				{
					buddy.invokeLater(new Runnable(){public void run(){
						Dialog.alert(_ex.toString());
						return;}});
//				}
    		}
    	}
    };

    public void run()
    {
    	int counter = 0;
    	gpsThreadRun = true;
   		while (gpsThreadRun)
   		{
   			if (counter == 5)
   			{
   				counter = 0;
   			}
   			switch (counter)
   			{
   				case 0:
    				buddy.invokeLater(new Runnable(){public void run(){
    					satLabel.setText("GPS: .");
    				}});
   			    	break;
   				case 1:
    				buddy.invokeLater(new Runnable(){public void run(){
    					satLabel.setText("GPS: ..");
    				}});
   			    	break;
   				case 2:
    				buddy.invokeLater(new Runnable(){public void run(){
    					satLabel.setText("GPS: ...");
    				}});
   			    	break;
   				case 3:
    				buddy.invokeLater(new Runnable(){public void run(){
    					satLabel.setText("GPS: ....");
    				}});
   			    	break;
   				case 4:
    				buddy.invokeLater(new Runnable(){public void run(){
    					satLabel.setText("GPS: .....");
    				}});
   			    	break;
  			}
   			counter++;
			try
			{
				Thread.sleep(1000);
			}
			catch (Exception ex)
			{
			}
		}
    }
    
    class GpsThread extends Thread
    {
		public void run() 
		{
			BlackBerryCriteria criteria = new BlackBerryCriteria();
			criteria.setMode(GPSInfo.GPS_MODE_AUTONOMOUS);
    		criteria.setCostAllowed(false);
    		try
    		{
    			BlackBerryLocationProvider provider = (BlackBerryLocationProvider)LocationProvider.
    				getInstance(criteria);
    			try
    			{
    			    location = (BlackBerryLocation)provider.getLocation(600);
    				latitude = location.getQualifiedCoordinates().getLatitude();
    				longitude = location.getQualifiedCoordinates().getLongitude();
    				altitude = location.getQualifiedCoordinates().getAltitude();
    				Coordinates coords = new Coordinates(latitude, longitude, altitude);
    				time = location.getTimestamp();
    				buddy.invokeLater(new Runnable(){public void run(){
	    				latitudeLabel.setText("Latitude: " + latitude);
	    				longitudeLabel.setText("Longitude: " + longitude);
	    				altitudeLabel.setText("Altitude: " + (int)altitude + " m");
	    				messageLabel.setText(getFormattedMessage());
    				}});
    				
    				try
    				{
    					Landmark[] results = Locator.reverseGeocode(coords, Locator.ADDRESS);
    					if (results != null && results.length > 0)
    					{
    						addrInfo = results[0].getAddressInfo();
    						String street = addrInfo.getField(AddressInfo.STREET);
    						String city = addrInfo.getField(AddressInfo.CITY);
    						String state = addrInfo.getField(AddressInfo.STATE);
    						String country = addrInfo.getField(AddressInfo.COUNTRY);
    						addressValue = "";
    						if (street != null && street.length() > 0)
    						{
    							addressValue += street + ", ";
    						}
    						if (city != null && city.length() > 0)
    						{
    							addressValue += city + ", ";
    						}
    						if (state != null && state.length() > 0)
    						{
    							addressValue += state + ", ";
    						}
    						if (country != null && country.length() > 0)
    						{
    							addressValue += country;
    						}
    						else
    						{
    							addressValue += "unknown country";
    						}
    						final String _addressValue = addressValue;
    	    				buddy.invokeLater(new Runnable(){public void run(){
    	    					addressLabel.setText("Location: " + _addressValue);
    		    				messageLabel.setText(getFormattedMessage());
    		    	            enableUI(true);
    	    		            locateButton.setFocus();
    	    				}});
    					}
    					
	    				buddy.invokeLater(new Runnable(){public void run(){
		    				MappableVector data = new MappableVector();
	    					data.addElement(new MapLocation(latitude, longitude, "Location", null));
	    					XYDimension imageSize = new XYDimension(Display.getWidth(), Display.getHeight());
	    					Bitmap bitmap = MapFactory.getInstance().generateStaticMapImage(imageSize, data);
	    					PNGEncodedImage img = PNGEncodedImage.encode(bitmap);
	    					mapField.setImage(img);
	    				}});
   	    				gpsThreadRun = false;
   	    				buddy.invokeLater(new Runnable(){public void run(){
   	    					satLabel.setText("GPS: on");
	    				}});
 	    			 
    				}
    				catch (LocatorException ex)
    				{
        				gpsThreadRun = false;
   	    				satLabel.setText("GPS: on");
    					final LocatorException _ex = ex;
    					buddy.invokeLater(new Runnable(){public void run(){
    		    		Dialog.alert("Locator error: " + _ex.getMessage());
	    	            enableUI(true);
    		            locateButton.setFocus();
    		            addressLabel.setText("Location: unknown");
    		            addressValue = "unknown";
    					return;}});
    				}
       				catch (Exception ex)
    				{
        				gpsThreadRun = false;
   	    				satLabel.setText("GPS: on");
    					final Exception _ex = ex;
    					buddy.invokeLater(new Runnable(){public void run(){
    		    		Dialog.alert("GPS error: " + _ex.getMessage());
	    	            enableUI(true);
    		            locateButton.setFocus();
    		            addressLabel.setText("Location: unknown");
    		            addressValue = "unknown";
    					return;}});
    				}
    			}
    			catch (InterruptedException ex)
    			{
    				gpsThreadRun = false;
    				satLabel.setText("GPS: off");
    				final InterruptedException _ex = ex;
    				buddy.invokeLater(new Runnable(){public void run(){
		    		Dialog.alert("Thread error: " + _ex.getMessage());
    	            enableUI(true);
		            locateButton.setFocus();
		            longitudeLabel.setText("Longitude: -");
		            altitudeLabel.setText("Altitude: -");
		            latitudeLabel.setText("Latitude: -");
		            addressLabel.setText("Location: unknown");
		            addressValue = "unknown";
		            return;}});
    			}
    			catch (LocationException ex)
    			{
    				gpsThreadRun = false;
	    			satLabel.setText("GPS: off");
	    			final LocationException _ex = ex;
    				buddy.invokeLater(new Runnable(){public void run(){
		    		Dialog.alert("Location error: " + _ex.getMessage());
    	            enableUI(true);
		            locateButton.setFocus();
		            altitudeLabel.setText("Altitude: -");
		            longitudeLabel.setText("Longitude: -");
		            latitudeLabel.setText("Latitude: -");
		            addressLabel.setText("Location: unknown");
		            addressValue = "unknown";
    				return;}});
    			}
    		}
    		catch (LocationException ex)
    		{
				gpsThreadRun = false;
    			satLabel.setText("GPS: off");
				final LocationException _ex = ex;
				buddy.invokeLater(new Runnable(){public void run(){
	    		Dialog.alert("Location error: " + _ex.getMessage());
	            enableUI(true);
	            locateButton.setFocus();
	            longitudeLabel.setText("Longitude: -");
	            altitudeLabel.setText("Altitude: -");
	            latitudeLabel.setText("Latitude: -");
	            addressLabel.setText("Location: unknown");
	            addressValue = "unknown";
	            latitude = 0;
	            longitude = 0;
	            altitude = 0;
				return;}});
    		}
		}
    }
 
    private static final String HELP_TEXT = "\nHow to invite your buddies to your current location?\n\n" +
		"1. Make sure you are outdoors or in an open location to be able to receive signals from the GPS satellites.\n\n" +
		"2. Tap on the 'Where am I?' button.\n\n" +
		"3. Wait till latitude, longitude, location address, and invitation time are properly filled.\n\n" +
		"4. Tap on the 'Change location and time' button to modify the values.\n\n" +
		"4a. Change the location (add the name of the pub, change the street number etc.).\n\n" +
		"4b. Change the meeting time (now, at 5pm, in 2 hours etc.).\n\n" +
		"5. Tap on the 'Change' button to select message type.\n\n" +
		"6. Optionally tap on the 'Personalize' button to create your own message.\n\n" +
		"7. Deselect the 'Publish on BlackBerry® Messenger' checkbox if you want to skip posting invitation on BBM™.\n\n" +
		"8. Select the 'Publish on Facebook® wall' checkbox if you want to post invitation on Facebook®.\n\n" +
		"9. Select the 'Tweet on Twitter®' checkbox if you want to post a tweet on Twitter® (only first 140 characters will be sent).\n\n" +
		"10. Select the 'Send text message' checkbox if you want to send a SMS invitation (only first 160 characters will be sent).\n\n" +
		"11. Select the 'Send e-mail' checkbox if you want to sent an E-mail invitation.\n\n" +
		"12. Tap on the 'Invite' button.\n\n" +
		"13. Allow access to Facebook® if asked.\n\n" +
		"14. Enter Twitter®'s username and password to login Twitter®.\n\n" +
		"15. Select all your buddies from the BlackBerry® Messenger's or PIM's contact list to send invitation on BBM™ or via E-mail.\n\n" +
		"16. Finally tap on the BlackBerry® Messenger's 'Send' button or press the ENTER key to complete the invitation process on BBM™ or via SMS.\n\n" +
		"* Skip steps 1 to 3 if you are not using GPS.";
}
