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
import android.os.Process;
import android.view.Menu;
import android.view.MenuItem;
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
public class AllTasks extends BaseActivity {

    /** [Optional] ID of the Menu item for killing the current process. **/
    private static final int MENU_KILL_PROCESS = 1;
    /** UI TextViews. **/
    private TextView mTextViewX, mTextViewY, mTextViewQueue;

    @Override
    public final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setTitle(AllTasks.class.getSimpleName());
        final Context context = this;

        mTextViewX = (TextView) findViewById(
                R.id.main_TextView_StatusShortTask);
        mTextViewY = (TextView) findViewById(
                R.id.main_TextView_StatusLongTask);
        mTextViewQueue = (TextView) findViewById(
                R.id.main_TextView_StatusQueue);

        ((Button) findViewById(R.id.main_Button_DoShortTask))
            .setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View view) {
                Bundle outBundle = new Bundle();
                outBundle.putString("TEXT", AllTasks.class.getSimpleName());
                getServiceQueue().postToService(Type.DO_SHORT_TASK, outBundle);
            }
        });

        ((Button) findViewById(R.id.main_Button_DoLongTask)).setOnClickListener(
                new OnClickListener() {
            @Override
            public void onClick(final View view) {
                getServiceQueue().postToService(Type.DO_LONG_TASK, null);
            }
        });

        setButton(R.id.main_Button_Go1, ShortTasks.class, context);
        setButton(R.id.main_Button_Go2, LongTasks.class, context);

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
        button.setText(targetClass.getSimpleName());
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
        mTextViewX.setText(getCache().getStateShortTask());
    }

    /***
     * Update the given TextView with information from the Application Cache.
     */
    private void updateTextViewY() {
        mTextViewY.setText(getCache().getStateLongTask());
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
            case UPDATE_SHORT_TASK:
                updateTextViewX();
                break;

            case UPDATE_LONG_TASK:
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

    /***
     * [Optional] Create the menu items.
     *
     * @param menu Add new menu items on to this object.
     * @return TRUE for the menu to be displayed.
     */
    public final boolean onCreateOptionsMenu(final Menu menu) {
        menu.add(0, MENU_KILL_PROCESS, 0, "Kill Process");
        return true;
    }

    /**
     * [Optional] Handle the selection of a menu item.
     *
     * @param item MenuItem that was selected.
     * @return TRUE to consume the selection event.
     */
    public final boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
        case MENU_KILL_PROCESS:
            Process.killProcess(Process.myPid());
            return true;
        default:
            // Do nothing.
            break;
        }
        return false;
    }
}
