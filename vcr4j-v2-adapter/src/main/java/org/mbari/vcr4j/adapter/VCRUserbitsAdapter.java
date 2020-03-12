/*
 * @(#)VCRUserbitsAdapter.java   2009.02.24 at 09:44:50 PST
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



package org.mbari.vcr4j.adapter;


import mbarix4j.util.IObserver;
import mbarix4j.util.ObservableSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author brian
 */
public class VCRUserbitsAdapter implements IVCRUserbits {

    private static final Logger log = LoggerFactory.getLogger(VCRUserbitsAdapter.class);

    private final ObservableSupport os = new ObservableSupport();

    private volatile byte[] userbits = new byte[] { 0, 0, 0, 0 };

    /** Creates a new instance of VCRUserbitsAdapter */
    public VCRUserbitsAdapter() {}

    public void addObserver(IObserver observer) {
        os.add(observer);
    }

    /**
     * @return  the userbits
     */
    public byte[] getUserbits() {
        return userbits;
    }

    /** Notifies all the observers of a chage of state */
    protected void notifyObservers() {
        if (log.isDebugEnabled()) {
            StringBuffer b = new StringBuffer("Notification of new Userbits: [");

            for (int i = 0; i < userbits.length; i++) {
                b.append(userbits[i]);

                if (i < userbits.length - 1) {
                    b.append(", ");
                }
            }

            b.append("]");
            log.debug(b.toString());
        }

        os.notify(this, null);
    }

    public void removeAllObservers() {
        os.clear();
    }

    public void removeObserver(IObserver observer) {
        os.remove(observer);
    }

    /**
     * @param userbits  the userbits to set
     */
    public void setUserbits(byte[] userbits) {
        synchronized (this.userbits) {
            this.userbits = userbits;
        }
        notifyObservers();
    }
}
