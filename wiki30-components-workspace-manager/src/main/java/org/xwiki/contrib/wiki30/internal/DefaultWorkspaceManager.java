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
package org.xwiki.contrib.wiki30.internal;

import org.xwiki.component.annotation.Component;
import org.xwiki.component.annotation.Requirement;
import org.xwiki.component.logging.AbstractLogEnabled;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;
import org.xwiki.context.Execution;
import org.xwiki.contrib.wiki30.WorkspaceManager;
import org.xwiki.model.reference.DocumentReference;

import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;
import com.xpn.xwiki.plugin.wikimanager.WikiManager;
import com.xpn.xwiki.plugin.wikimanager.WikiManagerMessageTool;
import com.xpn.xwiki.plugin.wikimanager.WikiManagerPluginApi;
import com.xpn.xwiki.plugin.wikimanager.doc.XWikiServer;
import com.xpn.xwiki.user.api.XWikiRightService;

/**
 * Implementation of a <tt>WorkspaceManager</tt> component.
 * 
 * @version $Id:$
 */
@Component
public class DefaultWorkspaceManager extends AbstractLogEnabled implements WorkspaceManager, Initializable
{
    /** Admin right. */
    private static final String RIGHT_ADMIN = "admin";

    /** Wiki preferences page for local wiki (unprefixed and relative to the current wiki). */
    private static final String WIKI_PREFERENCES_LOCAL = "XWiki.XWikiPreferences";

    /** Format string for the wiki preferences page of a certain wiki (absolute reference). */
    private static final String WIKI_PREFERENCES_PREFIXED_FORMAT = "%s:" + WIKI_PREFERENCES_LOCAL;

    /** Execution context. */
    @Requirement
    private Execution execution;

    /** Wrapped wiki manager plugin. */
    private WikiManagerPluginApi wikiManager;

    /** Internal wiki manager tookit required to overcome the rights checking of the API. */
    private WikiManager wikiManagerInternal;

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.component.phase.Initializable#initialize()
     */
    public void initialize() throws InitializationException
    {
        XWikiContext deprecatedContext = getXWikiContext();

        this.wikiManager =
            (WikiManagerPluginApi) deprecatedContext.getWiki().getPluginApi("wikimanager", deprecatedContext);

        WikiManagerMessageTool wikiManagerMessageTool = WikiManagerMessageTool.getDefault(deprecatedContext);
        this.wikiManagerInternal = new WikiManager(wikiManagerMessageTool);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.contrib.wiki30.WorkspaceManager#canCreateWorkspace(java.lang.String, java.lang.String)
     */
    public boolean canCreateWorkspace(String userName, String workspaceName)
    {
        XWikiContext deprecatedContext = getXWikiContext();

        /* If XWiki is not in virtual mode, don`t bother. */
        if (!deprecatedContext.getWiki().isVirtualMode()) {
            return false;
        }

        /* Avoid "traps" by making sure the page from where this is executed has PR. */
        if (!deprecatedContext.getWiki().getRightService().hasProgrammingRights(deprecatedContext)) {
            return false;
        }

        /* User name input validation. */
        if (userName == null || userName.trim().length() == 0) {
            return false;
        }

        /* Do not allow the guest user. XXX: Shouldn't this be decided by the admin trough rights? */
        if (XWikiRightService.GUEST_USER_FULLNAME.equals(userName)) {
            return false;
        }

        return true;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.contrib.wiki30.WorkspaceManager#canEditWorkspace(java.lang.String, java.lang.String)
     */
    public boolean canEditWorkspace(String userName, String workspaceName)
    {
        XWikiContext deprecatedContext = getXWikiContext();

        /* Avoid "traps" by making sure the page from where this is executed has PR. */
        if (!deprecatedContext.getWiki().getRightService().hasProgrammingRights(deprecatedContext)) {
            return false;
        }

        try {
            XWikiServer wikiServer = wikiManager.getWikiDocument(workspaceName);
            String wikiOwner = wikiServer.getOwner();

            XWikiRightService rightService = deprecatedContext.getWiki().getRightService();
            String mainWikiPreferencesDocumentName =
                String.format(WIKI_PREFERENCES_PREFIXED_FORMAT, deprecatedContext.getMainXWiki());

            /* Owner or main wiki admin. */
            return wikiOwner.equals(userName)
                || rightService.hasAccessLevel(RIGHT_ADMIN, userName, mainWikiPreferencesDocumentName,
                    deprecatedContext);
        } catch (Exception e) {
            // TODO: Log me!
            e.printStackTrace();
            // if (getLogger().isErrorEnabled()) {
            // XWikiPluginMessageTool msg = getMessageTool(deprecatedContext);
            // getLogger().error(msg.get(WikiManagerMessageTool.LOG_MANAGERCANEDIT), e);
            // }
            return false;
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.contrib.wiki30.WorkspaceManager#canDeleteWorkspace(java.lang.String, java.lang.String)
     */
    public boolean canDeleteWorkspace(String userName, String workspaceName)
    {
        XWikiContext deprecatedContext = getXWikiContext();

        /* Avoid "traps" by making sure the page from where this is executed has PR. */
        if (!deprecatedContext.getWiki().getRightService().hasProgrammingRights(deprecatedContext)) {
            return false;
        }

        try {
            XWikiServer wikiServer = wikiManager.getWikiDocument(workspaceName);
            String wikiOwner = wikiServer.getOwner();

            XWikiRightService rightService = deprecatedContext.getWiki().getRightService();
            String mainWikiPreferencesDocumentName =
                String.format(WIKI_PREFERENCES_PREFIXED_FORMAT, deprecatedContext.getMainXWiki());

            /* Owner or main wiki admin. */
            return deprecatedContext.getWiki().isVirtualMode()
                && (wikiOwner.equals(userName) || rightService.hasAccessLevel(RIGHT_ADMIN, userName,
                    mainWikiPreferencesDocumentName, deprecatedContext));
        } catch (Exception e) {
            // TODO: Log me!
            e.printStackTrace();
            // if (LOG.isErrorEnabled()) {
            // XWikiPluginMessageTool msg = getMessageTool(context);
            // LOG.error(msg.get(WikiManagerMessageTool.LOG_MANAGERCANDELETE), e);
            // }
            return false;
        }
    }

    /**
     * @return the deprecated xwiki context used to manipulate xwiki objects
     */
    private XWikiContext getXWikiContext()
    {
        return (XWikiContext) execution.getContext().getProperty("xwikicontext");
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.contrib.wiki30.WorkspaceManager#createWorkspace(java.lang.String, java.util.Map)
     */
    public XWikiServer createWorkspace(String workspaceName, XWikiServer newWikiXObjectDocument) throws XWikiException
    {
        XWikiContext deprecatedContext = getXWikiContext();

        /* Create new wiki. */
        newWikiXObjectDocument.setWikiName(workspaceName);

        String comment = String.format("Created new workspace '%s'", workspaceName);
        XWikiServer result =
            wikiManagerInternal.createNewWikiFromTemplate(newWikiXObjectDocument, "workspacetemplate", true, comment,
                deprecatedContext);

        /* Create new group for the workspace and put owner in it. */
        String mainWikiName = deprecatedContext.getMainXWiki();

        String workspaceGroupName = String.format("WorkspaceGroup%s", workspaceName);
        DocumentReference workspaceGroupReference = new DocumentReference(mainWikiName, "XWiki", workspaceGroupName);
        XWikiDocument workspaceGroupDocument = new XWikiDocument(workspaceGroupReference);
        String workspaceOwner = newWikiXObjectDocument.getOwner();

        String currentWikiName = deprecatedContext.getDatabase();
        try {
            deprecatedContext.setDatabase(mainWikiName);

            XWiki wiki = deprecatedContext.getWiki();

            DocumentReference groupClassReference = wiki.getGroupClass(deprecatedContext).getDocumentReference();
            BaseObject workspaceGroupObject =
                workspaceGroupDocument.getXObject(groupClassReference, true, deprecatedContext);
            workspaceGroupObject.setStringValue("member", workspaceOwner);

            wiki.saveDocument(workspaceGroupDocument, comment, deprecatedContext);
        } catch (Exception e) {
            getLogger().error("Failed to create workspace global group.", e);
        } finally {
            deprecatedContext.setDatabase(currentWikiName);
        }

        /* TODO: Add workspace marker object with a reference to the workspace group. */

        /* TODO: Launch workspace created event. */

        return result;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.contrib.wiki30.WorkspaceManager#deleteWorkspace(java.lang.String)
     */
    public void deleteWorkspace(String workspaceName) throws XWikiException
    {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.contrib.wiki30.WorkspaceManager#editWorkspace(java.lang.String, java.util.Map)
     */
    public void editWorkspace(String workspaceName, XWikiServer modifiedWikiXObjectDocument) throws XWikiException
    {
        // TODO Auto-generated method stub

    }

}
