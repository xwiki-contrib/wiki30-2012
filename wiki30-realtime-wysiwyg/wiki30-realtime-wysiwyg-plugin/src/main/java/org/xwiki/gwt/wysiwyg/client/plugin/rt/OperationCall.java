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
package org.xwiki.gwt.wysiwyg.client.plugin.rt;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Represents an operation call.
 * 
 * @version $Id: 238acf4c76e3553ee3ebb49d2bbea444ce9212ba $
 */
public class OperationCall implements IsSerializable
{
    /**
     * The operation target.
     */
    private OperationTarget target;

    /**
     * The operation identifier.
     */
    private String operationId;

    /**
     * The value the operation is called with.
     */
    private String value;

    /**
     * Default constructor.
     */
    public OperationCall()
    {
    }

    /**
     * Creates a new operation call.
     * 
     * @param operationId the operation identifier
     * @param value the operation value
     * @param target the operation target
     */
    public OperationCall(String operationId, String value, OperationTarget target)
    {
        this.operationId = operationId;
        this.value = value;
        this.target = target;
    }

    /**
     * @return the operation target
     */
    public OperationTarget getTarget()
    {
        return target;
    }

    /**
     * Sets the operation target.
     * 
     * @param target the new operation target
     */
    public void setTarget(OperationTarget target)
    {
        this.target = target;
    }

    /**
     * @return the operation identifier
     */
    public String getOperationId()
    {
        return operationId;
    }

    /**
     * Sets the operation identifier.
     * 
     * @param operationId the new operation identifier
     */
    public void setOperationId(String operationId)
    {
        this.operationId = operationId;
    }

    /**
     * @return the value the operation is called with
     */
    public String getValue()
    {
        return value;
    }

    /**
     * Sets the value the operation is called with.
     * 
     * @param value the new value
     */
    public void setValue(String value)
    {
        this.value = value;
    }

    @Override
    public String toString() {
        return "OperationCall: opId: " + operationId + ", value: " + value + ", target: " + target;
    }
}
