package si.pronic.beer.proxy.ui;

import javax.microedition.content.ContentHandler;
import javax.microedition.content.Invocation;
import javax.microedition.content.Registry;

import net.rim.blackberry.api.browser.Browser;
import net.rim.blackberry.api.browser.BrowserSession;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.UiEngine;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.decor.Background;
import net.rim.device.api.ui.decor.BackgroundFactory;

public class BBMUpgradeScreen extends MainScreen implements FieldChangeListener {

    private final ButtonField _downloadButton;
    private final ButtonField _cancelButton;
    
    public BBMUpgradeScreen(UiApplication app) 
    {
        this.setTitle((Field) null);
        
        Background background = BBMUpgradeScreen.getScreenBackground();
        this.getMainManager().setBackground(background);
        
        final String appName = ApplicationDescriptor.currentApplicationDescriptor().getName();
        final String message = "Welcome to Beer Buddy for BBM\n\n" +
                               "BlackBerry Messenger v6.0.0 or above is needed to run the application.";
        LabelField messageField = new ColorLabelField(message, Color.WHITE, Field.FIELD_HCENTER);
        final int padding = (int)((double) Display.getWidth() * 0.125);
        messageField.setPadding(padding, padding, padding, padding);
        this.add(messageField);
        
        final HorizontalFieldManager buttonMgr = new HorizontalFieldManager(Field.FIELD_HCENTER);
        this.add(buttonMgr);
        
        _downloadButton = new ButtonField("Download", ButtonField.CONSUME_CLICK | ButtonField.NEVER_DIRTY);
        _downloadButton.setChangeListener(this);
        buttonMgr.add(_downloadButton);
        
        _cancelButton =   new ButtonField("Cancel",   ButtonField.CONSUME_CLICK | ButtonField.NEVER_DIRTY);
        _cancelButton.setChangeListener(this);
        buttonMgr.add(_cancelButton);
        
        app.invokeLater(new Runnable() 
        {
            public void run() 
            {
//                Dialog.alert("ABC");
            }
        });
        
    }

    public void fieldChanged(Field field, int context) 
    {
        if(field == _downloadButton) 
        {
            downloadBBM();
        } 
        else if(field == _cancelButton) 
        {
            this.close();
        }
    }
    
    public static void downloadBBM() 
    {
        final int appWorldHandle = CodeModuleManager.getModuleHandle("net_rim_bb_appworld");
        if (appWorldHandle == 0) 
        {                                  
            downloadBBMFromBrowser();
        } 
        else 
        {                                                    
            try
            {
                downloadBBMFromAppWorld();
            } 
            catch(Exception e) 
            {
                downloadBBMFromBrowser();
            }
        }
    }
    
    private static void downloadBBMFromBrowser() 
    {
        BrowserSession browser = Browser.getDefaultSession();
        browser.displayPage("http://mobileapps.blackberry.com/devicesoftware/entry.do?code=bbm");
        browser.showBrowser();
    }
    
    private static void downloadBBMFromAppWorld() throws Exception 
    {
        Registry registry = Registry.getRegistry(BBMUpgradeScreen.class.getName());
        final String appWorldBBMId = "3729";
        Invocation invocation = new Invocation(null, null, "net.rim.bb.appworld.Content", true, ContentHandler.ACTION_OPEN);
        invocation.setArgs(new String[] {appWorldBBMId}); 
        registry.invoke(invocation);
    }
    
    public static Background getScreenBackground() 
    {
        final int width = Display.getWidth();
        final int height = Display.getHeight();
        final int imageSize = 800;
        Bitmap image = Bitmap.getBitmapResource("img/background_800x800.png");
        
        final int repeat;
        if (width > imageSize || height > imageSize) 
        {
            repeat = Background.REPEAT_SCALE_TO_FIT;
        } 
        else 
        {
            repeat = Background.REPEAT_NONE;
        }
        
        return BackgroundFactory.createBitmapBackground(image,
                                                        Background.POSITION_X_CENTER,
                                                        Background.POSITION_Y_CENTER,
                                                        repeat);
    }
    
    private static class ColorLabelField extends LabelField 
    {
        private final int _color;
        
        public ColorLabelField(String label, int color, long style) 
        {
            super(label, style);
            _color = color;
        }
        
        protected void paint(Graphics graphics) 
        {
            final int originalColor = graphics.getColor();
            graphics.setColor(_color);
            try 
            {
                super.paint(graphics);
            } 
            finally 
            {
                graphics.setColor(originalColor);
            }
        }
    }

}
