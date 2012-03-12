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
package org.xwiki.gwt.wysiwyg.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * This {@link ClientBundle} is used for all the button icons. Using a client bundle allows all of these images to be
 * packed into a single image, which saves a lot of HTTP requests, drastically improving startup time.
 * 
 * @version $Id: f371c1dcf2553b5232c42cf59c6445d4dfbcdf01 $
 */
public interface Images extends ClientBundle
{
    /**
     * An instance of this client bundle that can be used anywhere in the code to extract images.
     */
    Images INSTANCE = GWT.create(Images.class);

    @Source("bold.gif")
    ImageResource bold();

    @Source("italic.gif")
    ImageResource italic();

    @Source("strikethrough.gif")
    ImageResource strikeThrough();

    @Source("underline.gif")
    ImageResource underline();

    @Source("charmap.gif")
    ImageResource charmap();
    
    @Source("hr.gif")
    ImageResource hr();
}
