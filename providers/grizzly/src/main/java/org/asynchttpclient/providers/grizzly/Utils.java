/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package org.asynchttpclient.providers.grizzly;

import org.glassfish.grizzly.Connection;
import org.glassfish.grizzly.Grizzly;
import org.glassfish.grizzly.attributes.Attribute;
import org.glassfish.grizzly.attributes.AttributeStorage;

import java.net.URI;
import java.util.concurrent.atomic.AtomicInteger;

public final class Utils {

    private static final Attribute<Boolean> IGNORE =
            Grizzly.DEFAULT_ATTRIBUTE_BUILDER.createAttribute(Utils.class.getName() + "-IGNORE");
    private static final Attribute<AtomicInteger> REQUEST_IN_FLIGHT =
                Grizzly.DEFAULT_ATTRIBUTE_BUILDER.createAttribute(Utils.class.getName() + "-IN-FLIGHT");


    // ------------------------------------------------------------ Constructors


    private Utils() {}


    // ---------------------------------------------------------- Public Methods


    public static boolean isSecure(final String uri) {
        return (uri.startsWith("https:") || uri.startsWith("wss:"));
    }

    public static boolean isSecure(final URI uri) {
        final String scheme = uri.getScheme();
        return ("https".equals(scheme) || "wss".equals(scheme));
    }

    public static void connectionIgnored(final Connection c, boolean ignored) {
        if (ignored) {
            IGNORE.set(c, true);
        } else {
            IGNORE.remove(c);
        }
    }

    public static boolean isIgnored(final Connection c) {
        Boolean result = IGNORE.get(c);
        return (result != null && result);
    }

    public static void addRequestInFlight(final AttributeStorage storage) {
        AtomicInteger counter = REQUEST_IN_FLIGHT.get(storage);
        if (counter == null) {
            counter = new AtomicInteger(1);
            REQUEST_IN_FLIGHT.set(storage, counter);
        } else {
            counter.incrementAndGet();
        }
    }

    public static void removeRequestInFlight(final AttributeStorage storage) {
        AtomicInteger counter = REQUEST_IN_FLIGHT.get(storage);
        if (counter != null) {
            counter.decrementAndGet();
        }
    }

    public static int getRequestInFlightCount(final AttributeStorage storage) {
        AtomicInteger counter = REQUEST_IN_FLIGHT.get(storage);
        return ((counter != null) ? counter.get() : 0);
    }
}
