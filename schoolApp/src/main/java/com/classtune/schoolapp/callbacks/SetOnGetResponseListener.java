/**
 * 
 */
package com.classtune.schoolapp.callbacks;


/**
 * @author Amit
 *
 */
public interface SetOnGetResponseListener {
	public void onFailure(String errorMsg);
	public void onSuccess(String response);
}
