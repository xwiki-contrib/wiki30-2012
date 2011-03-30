/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.gwt.dom.mutation.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xwiki.gwt.dom.mutation.client.MutationEvent.MutationEventType;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Node;

/**
 * Default {@link MutationSource} implementation.
 * 
 * @version $Id$
 */
public class DefaultMutationSource implements MutationListener, MutationSource
{
    /**
     * The root of the subtree whose mutation events are caught.
     */
    @SuppressWarnings("unused")
    private final Node source;

    /**
     * Registered mutation event listeners.
     */
    private final Map<MutationEventType, List<MutationListener>> listeners =
        new HashMap<MutationEventType, List<MutationListener>>();

    /**
     * The JavaScript object that catches all the mutation events.
     */
    @SuppressWarnings("unused")
    private final JavaScriptObject catcher;

    /**
     * Creates a new mutation source that catches all the mutation events triggered in the subtree with the specified
     * root node.
     * 
     * @param source the root of the subtree whose mutation events are caught
     */
    public DefaultMutationSource(Node source)
    {
        this.source = source;
        catcher = createMutationListenerFunction(this);
        for (MutationEventType type : MutationEventType.values()) {
            listeners.put(type, new ArrayList<MutationListener>());
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see MutationSource#addListener(MutationEventType, MutationListener)
     */
    public void addListener(MutationEventType type, MutationListener listener)
    {
        if (type == null || listener == null) {
            throw new NullPointerException();
        }
        List<MutationListener> listenersByType = listeners.get(type);
        if (listenersByType.isEmpty()) {
            catchMutationEvents(type);
        }
        listeners.get(type).add(listener);
    }

    /**
     * {@inheritDoc}
     * 
     * @see MutationSource#removeListener(MutationEventType, MutationListener)
     */
    public void removeListener(MutationEventType type, MutationListener listener)
    {
        if (type == null || listener == null) {
            throw new NullPointerException();
        }
        List<MutationListener> listenersByType = listeners.get(type);
        listenersByType.remove(listener);
        if (listenersByType.isEmpty()) {
            ignoreMutationEvents(type);
        }
    }

    /**
     * Creates a JavaScript function that forwards the call to the specified mutation event listener. This function can
     * be registered as a native event listener.
     * 
     * @param listener a mutation event listener
     * @return a JavaScript function to be used for catching mutation events
     */
    private static native JavaScriptObject createMutationListenerFunction(MutationListener listener)
    /*-{
        return function(event) {
            listener.@org.xwiki.gwt.dom.mutation.client.MutationListener::onMutation(Lorg/xwiki/gwt/dom/mutation/client/MutationEvent;)(event);
        }
    }-*/;

    /**
     * Starts listening to mutation events of the specified type.
     * 
     * @param type the type of mutation event to listen to
     */
    private native void catchMutationEvents(MutationEventType type)
    /*-{
        var source = this.@org.xwiki.gwt.dom.mutation.client.DefaultMutationSource::source;
        source.addEventListener(type.@org.xwiki.gwt.dom.mutation.client.MutationEvent.MutationEventType::toString()(),
            this.@org.xwiki.gwt.dom.mutation.client.DefaultMutationSource::catcher, false);
    }-*/;

    /**
     * Stops listening to mutation events of the specified type.
     * 
     * @param type the type of mutation event to stop listening to
     */
    private native void ignoreMutationEvents(MutationEventType type)
    /*-{
        var source = this.@org.xwiki.gwt.dom.mutation.client.DefaultMutationSource::source;
        source.removeEventListener(type.@org.xwiki.gwt.dom.mutation.client.MutationEvent.MutationEventType::toString()(),
            this.@org.xwiki.gwt.dom.mutation.client.DefaultMutationSource::catcher, false);
    }-*/;

    /**
     * {@inheritDoc}
     * 
     * @see MutationListener#onMutation(MutationEvent)
     */
    public void onMutation(MutationEvent event)
    {
        for (MutationListener listener : listeners.get(event.getMutationEventType())) {
            listener.onMutation(event);
        }
    }
}
