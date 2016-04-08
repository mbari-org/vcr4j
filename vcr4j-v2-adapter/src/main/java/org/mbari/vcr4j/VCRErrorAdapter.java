/*
 * @(#)VCRErrorAdapter.java   2009.02.24 at 09:44:51 PST
 *
 * Copyright 2007 MBARI
 *
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 2.1
 * (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/copyleft/lesser.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package org.mbari.vcr4j;

import org.mbari.util.IObserver;
import org.mbari.util.ObservableSupport;
import org.mbari.vcr4j.IVCRError;

/**
 *
 * @author brian
 */
public class VCRErrorAdapter implements IVCRError {

    protected ObservableSupport oc = new ObservableSupport();

    /** Creates a new instance of VCRErrorAdapter */
    public VCRErrorAdapter() {}

    public void addObserver(IObserver observer) {
        oc.add(observer);
    }

    public int getError() {
        return OK;
    }

    public boolean isChecksumError() {
        return false;
    }

    public boolean isFramingError() {
        return false;
    }

    public boolean isOK() {
        return true;
    }

    public boolean isOverrunError() {
        return false;
    }

    public boolean isParityError() {
        return false;
    }

    public boolean isTimeout() {
        return false;
    }

    public boolean isUndefinedCommand() {
        return false;
    }

    /** Notifies all the observers of a chage of state */
    protected void notifyObservers() {
        oc.notify(this, null);
    }

    /** Removes all observers from the notfication list */
    public void removeAllObservers() {
        oc.clear();
    }

    /**
     * Remove an observer from the notification list
     *
     * @param observer
     */
    public void removeObserver(IObserver observer) {
        oc.remove(observer);
    }
}
