package fr.loria.score.jupiter.model;


import java.io.Serializable;

/**
 * Represents a document on which OT algorithms work: a plain string, a tree etc.<br/>
 * On different algorithms, the document may contain more information than the visible text such as metadata
 * (tombstone invisible chars etc).
 *
 * @author sebastien.parisot
 * @author Bogdan.Flueras@inria.fr
 */
public interface Document extends Serializable {
    /**
     * @return the view of the document, what it is expected to be viewable by user according to the used algorithm
     */
    public String getContent();

    /**
     * @param content the content to be set for this document
     */
    public void setContent(String content);

    /**
     * Apply an operation to the document and modifies the content of the document according to the semantics of the operation
     *
     * @param op the operation to be applied on this document
     */
    public void apply(AbstractOperation op);

    /**
     * @return a deep clone of this Document
     */
    public Document deepCloneDocument();
}
