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
package org.xwiki.gwt.wysiwyg.client.plugin.mutation;

import org.xwiki.gwt.dom.mutation.client.DefaultMutationSerializer;
import org.xwiki.gwt.dom.mutation.client.DefaultMutationSource;
import org.xwiki.gwt.dom.mutation.client.Mutation;
import org.xwiki.gwt.dom.mutation.client.MutationEvent;
import org.xwiki.gwt.dom.mutation.client.MutationListener;
import org.xwiki.gwt.dom.mutation.client.MutationSerializer;
import org.xwiki.gwt.dom.mutation.client.MutationSource;
import org.xwiki.gwt.dom.mutation.client.MutationEvent.MutationEventType;
import org.xwiki.gwt.user.client.Config;
import org.xwiki.gwt.user.client.Console;
import org.xwiki.gwt.user.client.ui.rta.RichTextArea;
import org.xwiki.gwt.user.client.ui.rta.cmd.Command;
import org.xwiki.gwt.user.client.ui.rta.cmd.CommandListener;
import org.xwiki.gwt.user.client.ui.rta.cmd.CommandManager;
import org.xwiki.gwt.wysiwyg.client.plugin.internal.AbstractPlugin;

/**
 * Logs DOM mutations generated inside the rich text area.
 * 
 * @version $Id$
 */
public class MutationPlugin extends AbstractPlugin implements CommandListener, MutationListener
{
    /**
     * The object used to serialize mutation events.
     */
    private final MutationSerializer mutationSerializer = new DefaultMutationSerializer();

    /**
     * The object that generates mutation events.
     */
    private MutationSource mutationSource;

    /**
     * The object used to catch command events before the rest of the command listeners.
     */
    private CommandListener commandListener;

    /**
     * {@inheritDoc}
     * 
     * @see AbstractPlugin#init(RichTextArea, Config)
     */
    public void init(RichTextArea textArea, Config config)
    {
        super.init(textArea, config);

        // Create the command listener that will be notified before the rest of the listeners.
        commandListener = new CommandListener()
        {
            /** Flag indicating that this listener has been called at least once. */
            private boolean called;

            public boolean onBeforeCommand(CommandManager sender, Command command, String param)
            {
                if (!called) {
                    called = true;
                    // Register the command listener that will be notified after the rest of the listeners.
                    getTextArea().getCommandManager().addCommandListener(MutationPlugin.this);
                }
                Console.getInstance().log("begin command " + command + '[' + param + ']');
                return false;
            }

            public void onCommand(CommandManager sender, Command command, String param)
            {
                // Ignore.
            }
        };
        getTextArea().getCommandManager().addCommandListener(commandListener);
        catchMutations();
    }

    /**
     * {@inheritDoc}
     * 
     * @see AbstractPlugin#destroy()
     */
    public void destroy()
    {
        getTextArea().getCommandManager().removeCommandListener(commandListener);
        getTextArea().getCommandManager().removeCommandListener(this);
        ignoreMutations();

        super.destroy();
    }

    /**
     * Start catching mutation events.
     */
    private void catchMutations()
    {
        if (mutationSource == null) {
            mutationSource = new DefaultMutationSource(getTextArea().getDocument().getBody());
            mutationSource.addListener(MutationEventType.DOM_ATTR_MODIFIED, this);
            mutationSource.addListener(MutationEventType.DOM_CHARACTER_DATA_MODIFIED, this);
            mutationSource.addListener(MutationEventType.DOM_NODE_INSERTED, this);
            mutationSource.addListener(MutationEventType.DOM_NODE_REMOVED, this);
        }
    }

    /**
     * Stop catching mutation events.
     */
    private void ignoreMutations()
    {
        if (mutationSource != null) {
            mutationSource.removeListener(MutationEventType.DOM_ATTR_MODIFIED, this);
            mutationSource.removeListener(MutationEventType.DOM_CHARACTER_DATA_MODIFIED, this);
            mutationSource.removeListener(MutationEventType.DOM_NODE_INSERTED, this);
            mutationSource.removeListener(MutationEventType.DOM_NODE_REMOVED, this);
            mutationSource = null;
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see CommandListener#onBeforeCommand(CommandManager, Command, String)
     */
    public boolean onBeforeCommand(CommandManager sender, Command command, String param)
    {
        // Ignore.
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see CommandListener#onCommand(CommandManager, Command, String)
     */
    public void onCommand(CommandManager sender, final Command command, final String param)
    {
        if ("enable".equalsIgnoreCase(command.toString())) {
            // We have to update the mutation source because the edited document has been reloaded.
            if (Boolean.valueOf(param)) {
                catchMutations();
            } else {
                ignoreMutations();
            }
        }
        Console.getInstance().log("end command " + command + '[' + param + ']');
    }

    /**
     * {@inheritDoc}
     * 
     * @see MutationListener#onMutation(MutationEvent)
     */
    public void onMutation(MutationEvent event)
    {
        Mutation mutation = mutationSerializer.serialize(event, getTextArea().getDocument().getBody());
        Console.getInstance().log(
            "mutation: type[" + mutation.getType() + "] locator[" + mutation.getLocator() + "] value["
                + mutation.getValue() + ']');
    }
}
