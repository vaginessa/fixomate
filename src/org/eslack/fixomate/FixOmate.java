package org.eslack.fixomate;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import android.view.MotionEvent;
import java.lang.reflect.Method;
import java.lang.Class;


public class FixOmate implements IXposedHookLoadPackage {

 void printSamples(MotionEvent ev) {
     final int historySize = ev.getHistorySize();
     final int pointerCount = ev.getPointerCount();
     for (int h = 0; h < historySize; h++) {
         XposedBridge.log("PAU: At time " + ev.getHistoricalEventTime(h));
         for (int p = 0; p < pointerCount; p++) {
             XposedBridge.log("PAU: ---->  pointer " + ev.getPointerId(p) + ": (" + ev.getHistoricalX(p, h) +"," + ev.getHistoricalY(p, h) + ")");
         }
     }
     XposedBridge.log("PAU: At time " + ev.getEventTime());
     for (int p = 0; p < pointerCount; p++) {
         XposedBridge.log("PAU: ---->  pointer " + ev.getPointerId(p) + ": (" + ev.getX(p) + "," + ev.getY(p) + ")");
     }
 }


    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {

/*
        findAndHookMethod("android.view.MotionEvent", lpparam.classLoader, "getPointerCount", new XC_MethodReplacement() {
            @Override
		protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
			//XposedBridge.log("PAU: getPointerCount() Hook in place");
			return 1;
		}
        });

*/


       final XC_MethodHook checkSource = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    MotionEvent event = (MotionEvent)param.args[0];

		XposedBridge.log("PAU: " + event.toString() + " getAction=" + event.getAction());

                    if(event.getDevice().getName().equals("mtk-tpd")) { 





			//printSamples(event);
			//XposedBridge.log("PAU: " + event.toString());
			if(event.getPointerCount()>1) {
				XposedBridge.log("PAU: borked event ^^^^^^^^");

                        	Class < ? >myclass = Class.forName("android.view.MotionEvent");
                        	Object[] params = new Object[] { new Integer (1) };
				Class[] cArg = new Class[1];
				cArg[0] = int.class;
				Method split = myclass.getMethod("split", cArg);
				event = (MotionEvent) (split.invoke (myclass.newInstance(),params));

				XposedBridge.log("PAU: fixed event vvvvvvvv");
				XposedBridge.log("PAU: " + event.toString() + " getAction=" + event.getAction());

			} 
/*
			if (event.getAction()==MotionEvent.ACTION_CANCEL && event.getPointerCount()==1) {
				XposedBridge.log("PAU: blocked CANCEL event > getPointerCount=" + event.getPointerCount() + " getAction=" + event.getAction());
                        	param.setResult(false);
			}
*/
/*
				XposedBridge.log("PAU: " + event.toString() + " getAction=" + event.getAction());
			//if (event.getPointerCount()>1) {
				if (event.getAction() == 261 || event.getAction() == 262 || event.getAction() == 517 || event.getAction() == MotionEvent.ACTION_POINTER_UP) {
					event.setAction(MotionEvent.ACTION_OUTSIDE);
					event.setLocation(0,0);
					//event.setLocation(event.getX(0), event.getY(0));
					//printSamples(event);
					XposedBridge.log("PAU: replaced BORKED > " + event.toString() + " getAction=" + event.getAction());
                        		param.setResult(false);
				}
*/
/*
				if (event.getAction()==MotionEvent.ACTION_CANCEL && event.getPointerCount()==1) {
					event.setAction(2);
					XposedBridge.log("PAU: replaced CANCEL > " + event.toString() + " getAction=" + event.getAction());
                        		//param.setResult(false);
				}
*/

			//}
		    }
            }
        };


        findAndHookMethod("android.view.View", lpparam.classLoader, "dispatchTouchEvent", MotionEvent.class, checkSource);
        findAndHookMethod("android.app.Activity", lpparam.classLoader, "dispatchTouchEvent", MotionEvent.class, checkSource);

        findAndHookMethod("android.view.View", lpparam.classLoader, "onTouchEvent", MotionEvent.class, checkSource);
        findAndHookMethod("android.app.Activity", lpparam.classLoader, "onTouchEvent", MotionEvent.class, checkSource);

        findAndHookMethod("android.view.View", lpparam.classLoader, "dispatchGenericMotionEvent", MotionEvent.class, checkSource);
        findAndHookMethod("android.app.Activity", lpparam.classLoader, "dispatchGenericMotionEvent", MotionEvent.class, checkSource);



    }
}
