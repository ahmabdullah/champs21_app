/**
 * 
 */
package com.champs21.schoolapp.callbacks;

import org.json.JSONObject;


/**
 * @author Amit
 *
 */
public interface SetOnGetResponseListener {
	public void onFailure(String errorMsg);
	public void onSuccess(String response);
}
