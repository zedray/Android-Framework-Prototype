/*
 * Copyright 2010 Mark Brady
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zedray.framework.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.zedray.framework.R;
import com.zedray.framework.utils.Type;

/***
 * Example Activity to demonstrate the framework. Extends the Framework
 * BaseActivity class.
 */
public class Activity1 extends BaseActivity {

    /** UI TextViews. **/
    private TextView mTextViewX, mTextViewY, mTextViewQueue;

    @Override
    public final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setTitle(getResources().getString(R.string.app_name)
                + Activity1.class.getSimpleName());
        final Context context = this;

        mTextViewX = (TextView) findViewById(
                R.id.main_TextView_StatusX);
        mTextViewY = (TextView) findViewById(
                R.id.main_TextView_StatusY);
        mTextViewQueue = (TextView) findViewById(
                R.id.main_TextView_StatusQueue);

        ((Button) findViewById(R.id.main_Button_DoX)).setOnClickListener(
                new OnClickListener() {
            @Override
            public void onClick(final View view) {
                getServiceQueue().postToService(Type.DO_X, null);
            }
        });

        ((Button) findViewById(R.id.main_Button_DoY)).setOnClickListener(
                new OnClickListener() {
            @Override
            public void onClick(final View view) {
                Bundle outBundle = new Bundle();
                outBundle.putString("TEXT", "Message from Activity");
                getServiceQueue().postToService(Type.DO_Y, outBundle);
            }
        });

        setButton(R.id.main_Button_Go1, Activity2.class, context);
        setButton(R.id.main_Button_Go2, Activity3.class, context);

        updateAll();
    }

    @Override
    protected final void onResume() {
        updateAll();
        super.onResume();
    }

    /***
     * [Optional] Sets the UI buttons to point to the next Activity.
     *
     * @param buttonId Resource ID of the given Button
     * @param targetClass Activity to start on Button click.
     * @param context Application Context.
     */
    private void setButton(final int buttonId, final Class<?> targetClass,
            final Context context) {
        Button button = (Button) findViewById(buttonId);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View view) {
                startActivity(new Intent(context, targetClass));
            }
        });
        button.setText(targetClass.getName());
    }

    /***
     * Update all UI elements - called onCreate() and onResume().
     */
    private void updateAll() {
        updateTextViewX();
        updateTextViewY();
        updateTextViewQueue();
    }

    /***
     * Update the given TextView with information from the Application Cache.
     */
    private void updateTextViewX() {
        mTextViewX.setText(getCache().getX());
    }

    /***
     * Update the given TextView with information from the Application Cache.
     */
    private void updateTextViewY() {
        mTextViewY.setText(getCache().getY());
    }

    /***
     * Update the given TextView with information from the Application Cache.
     */
    private void updateTextViewQueue() {
        mTextViewQueue.setText(getCache().getQueue());
    }

    /***
     * Override the post method to receive incoming messages from the Service.
     *
     * @param type Message type.
     * @param bundle Optional Bundle of extra information, NULL otherwise.
     */
    @Override
    public final void post(final Type type, final Bundle bundle) {
        switch (type) {
            case UPDATE_X:
                updateTextViewX();
                break;

            case UPDATE_Y:
                updateTextViewY();
                break;

            case UPDATE_QUEUE:
                updateTextViewQueue();
                break;

            default:
                /** Let the BaseActivity handle other message types. */
                super.post(type, bundle);
                break;
        }
    }
}