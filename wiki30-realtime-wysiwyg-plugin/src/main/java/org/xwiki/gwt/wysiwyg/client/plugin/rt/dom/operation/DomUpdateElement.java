package org.xwiki.gwt.wysiwyg.client.plugin.rt.dom.operation;

import org.xwiki.gwt.dom.client.Document;
import org.xwiki.gwt.dom.client.Range;

import com.google.gwt.dom.client.Node;

import fr.loria.score.jupiter.tree.operation.TreeOperation;
import fr.loria.score.jupiter.tree.operation.TreeUpdateElement;

/**
 * @author Bogdan.Flueras@inria.fr
 */
public class DomUpdateElement extends AbstractDomOperation
{
    public DomUpdateElement(TreeOperation operation)
    {
        super(operation);
    }

    @Override public Range execute(Document document)
    {
        TreeUpdateElement op = getOperation();
        String newNodeName = op.getValue();
        Node node = getTargetNode(document);

        Range caret = document.createRange();
        caret.setStart(node, op.getPosition());

        if (shouldUpdate(node.getNodeName(), newNodeName)) {
            Node newNode = document.createElement(newNodeName);
            for (int i = 0; i < node.getChildCount(); i++) {
                newNode.appendChild(node.getChild(i));
            }

            if (node.getParentElement() != null) {
                node.getParentElement().replaceChild(newNode, node);
            }

            // No change for caret position
            caret.setStart(newNode, op.getPosition());
        }

        caret.collapse(true);
        return caret;
    }

    private boolean shouldUpdate(String nodeName, String newNodeName)
    {
        if (nodeName != null && ("p".equalsIgnoreCase(nodeName) || "h".equalsIgnoreCase(nodeName.substring(0, 1)))) {
             return true;
        }
        return false;
    }
}
