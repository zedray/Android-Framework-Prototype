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

package com.zedray.framework.utils;

/***
 * Defines all the message types used in the framework.
 */
public enum Type {

    /***
     * UI to Service messages.
     */
    DO_X, DO_Y,

    /***
     * Service to UI messages.
     */
    UPDATE_X, UPDATE_Y, UPDATE_QUEUE, SHOW_DIALOG,

    /***
     * UI Dialogs.
     */
    DIALOG_STATUS,

    /***
     * Do not handle this message.
     */
    UNKNOWN;

    /***
     * Get the Type from a given Integer value.
     *
     * @param input Integer.ordinal value of the UiEvent
     * @return Relevant Type or UNKNOWN if the Integer is not known.
     */
    public static Type getType(final int input) {
        if (input < 0 || input > UNKNOWN.ordinal()) {
            return UNKNOWN;
        } else {
            return Type.values()[input];
        }
    }
}