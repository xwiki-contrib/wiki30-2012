package fr.loria.score.client;

import fr.loria.score.jupiter.model.Document;
import fr.loria.score.jupiter.plain.PlainDocument;

import java.io.Serializable;

/**
 * DTO class for ClientJupiterAlg
 * @author Bogdan.Flueras@inria.fr
 */
public class ClientDTO implements Serializable {
    private int siteId;
    private int esid;
    private Document document;

    public ClientDTO() {
    }

    public ClientDTO(Document document, int siteId, int esid) {
        this.siteId = siteId;
        this.esid = esid;
        this.document = document;
    }

    public ClientDTO(String content, int siteId, int esid) {
        this(new PlainDocument(content), siteId, esid);
    }

    public ClientDTO(ClientJupiterAlg cja) {
        this.siteId = cja.getSiteId();
        this.esid = cja.getEditingSessionId();
        this.document = cja.getDocument();
    }

    public int getSiteId() {
        return siteId;
    }

    public ClientDTO setSiteId(int siteId) {
        this.siteId = siteId;
        return this;
    }

    public int getEditingSessionId() {
        return esid;
    }

    public ClientDTO setEditingSessionId(int esid) {
        this.esid = esid;
        return this;
    }

    public Document getDocument() {
        return document;
    }

    public ClientDTO setDocument(Document data) {
        this.document = data;
        return this;
    }

    @Override
    public String toString() {
        return "ClientDTO: siteId: " + siteId + ", esid: " + esid + ", document: " + document;
    }
}
