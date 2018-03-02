package si.pronic.beer.proxy;

import si.pronic.beer.proxy.ui.BBMUpgradeScreen;
import si.pronic.beer.proxy.util.StringTokenizer;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.CodeModuleGroup;
import net.rim.device.api.system.CodeModuleGroupManager;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.UiEngine;
import net.rim.device.api.ui.component.Dialog;


/**
 * This proxy application should take the place of your application on the home screen. Its purpose
 * is to verify and upgrade the version of BBM that is installed on a user's device. If the desired
 * version of BBM is installed, your application starts seamlessly. If BBM is not installed or needs
 * to be upgraded, the user is prompted to download BBM.
 */
public class BeerBuddyProxy {
    
    private static final String MODULE_NAME = "si_pronic_BeerBuddy";  
    private static final int    APP_DESC_INDEX = 0;                           
    private static final String MIN_BBM_VERSION = "6.0.0";     
    
    public static void main(String[] args) 
    {
        try 
        {
            final int bbmHandle = CodeModuleManager.getModuleHandle("net_rim_bb_qm_peer_lib");
            final int bbmPlatformHandle = CodeModuleManager.getModuleHandle("net_rim_bb_qm_platform");
            if (bbmHandle == 0 || bbmPlatformHandle == 0) 
            {
                showBBMUpgradeScreen();
            } 
            else 
            {
                final String version = CodeModuleManager.getModuleVersion(bbmHandle);
                if (compareVersion(version, MIN_BBM_VERSION) >= 0) 
                { 
                	ApplicationDescriptor appDesc = getAppDesc(MODULE_NAME, APP_DESC_INDEX);
                    appDesc = new ApplicationDescriptor(appDesc, args); 
                    ApplicationManager.getApplicationManager().runApplication(appDesc);
                } 
                else 
                {                                           
                    showBBMUpgradeScreen();
                }
            }
        } 
        catch (Exception ex) 
        {
            String message = ex.getMessage();
            if (message == null || message.length() == 0) 
            {
                message = ex.toString();
                
                if (message == null || message.length() == 0) 
                {
                    message = ex.getClass().getName();
                }
            }
            showErrorScreen(message);
        }
    }
    
    private static void showBBMUpgradeScreen() 
    {
    	BBMUpgradeApplication app = new BBMUpgradeApplication();
    	app.enterEventDispatcher();
    }
    
    public static void showErrorScreen(String message) 
    {
        Application app = new ErrorApplication(message);
        app.enterEventDispatcher();
    }
    
    private static ApplicationDescriptor getAppDesc(String moduleName, int appDescIndex) 
    {
        final int handle = CodeModuleManager.getModuleHandle(moduleName);
        if (handle == 0) 
        {
            throw new IllegalArgumentException("Module " + moduleName + " not found");
        }
        ApplicationDescriptor[] appDescs = CodeModuleManager.getApplicationDescriptors(handle);
        if (appDescs != null && appDescs.length > 0) 
        {
            return appDescs[appDescIndex];
        } 
        else 
        {
            throw new IllegalArgumentException("Module " + moduleName + " has no application descriptors");
        }
    }
    
    private static int compareVersion(String v1, String v2) 
    {
        StringTokenizer v1Tokenizer = new StringTokenizer(v1, '.');
        StringTokenizer v2Tokenizer = new StringTokenizer(v2, '.');
        while(true) 
        {
            boolean v1HasMore = v1Tokenizer.hasMoreTokens();
            boolean v2HasMore = v2Tokenizer.hasMoreTokens();
            
            if (!v1HasMore && !v2HasMore) 
            {
                return 0;
            } 
            else if (v1HasMore && !v2HasMore) 
            {
                return 1;
            } 
            else if (!v1HasMore &&  v2HasMore) 
            {
                return -1;
            } 
            else 
            {
                int v1Component = Integer.parseInt(v1Tokenizer.nextToken());
                int v2Component = Integer.parseInt(v2Tokenizer.nextToken());
                int comparison = v1Component - v2Component;
                if (comparison != 0) 
                {
                    return comparison;
                }
            }
        }
    }
    
    private static class BBMUpgradeApplication extends UiApplication 
    {
        public BBMUpgradeApplication() 
        {
        	this.pushScreen(new BBMUpgradeScreen(this));
        }
    }
    
    private static class ErrorApplication extends Application 
    {
        public ErrorApplication(final String message) 
        {
            this.invokeLater(new Runnable() 
            {
                public void run() 
                {
                    final Dialog d = new Dialog(Dialog.D_OK, "@" + message, Dialog.OK, 
                    		Bitmap.getPredefinedBitmap(Bitmap.EXCLAMATION), 0);
                    Ui.getUiEngine().pushGlobalScreen(d, 0, UiEngine.GLOBAL_MODAL);
                    System.exit(0);
                }
            });
        }
    }
}
