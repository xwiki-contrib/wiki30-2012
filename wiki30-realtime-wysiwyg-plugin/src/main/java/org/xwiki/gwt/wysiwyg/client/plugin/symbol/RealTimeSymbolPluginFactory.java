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
package org.xwiki.gwt.wysiwyg.client.plugin.symbol;

import org.xwiki.gwt.wysiwyg.client.plugin.Plugin;
import org.xwiki.gwt.wysiwyg.client.plugin.internal.AbstractPluginFactory;

/**
 * Factory for {@link RealTimeSymbolPlugin}.
 * 
 * @version $Id: 71e60f5f579c96ec66a6554e7ab734cc0b2d13c8 $
 */
public final class RealTimeSymbolPluginFactory extends AbstractPluginFactory
{
    /**
     * The singleton instance.
     */
    private static RealTimeSymbolPluginFactory instance;

    /**
     * Creates a new factory for {@link RealTimeSymbolPlugin}.
     */
    private RealTimeSymbolPluginFactory()
    {
        super("rt-symbol");
    }

    /**
     * @return the singleton instance.
     */
    public static synchronized RealTimeSymbolPluginFactory getInstance()
    {
        if (instance == null) {
            instance = new RealTimeSymbolPluginFactory();
        }
        return instance;
    }

    /**
     * {@inheritDoc}
     * 
     * @see AbstractPluginFactory#newInstance()
     */
    public Plugin newInstance()
    {
        return new RealTimeSymbolPlugin();
    }
}
