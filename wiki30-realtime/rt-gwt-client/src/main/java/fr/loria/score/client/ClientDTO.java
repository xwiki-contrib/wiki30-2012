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
    private Document data;

    public ClientDTO() {
    }

    public ClientDTO(Document data, int siteId, int esid) {
        this.siteId = siteId;
        this.esid = esid;
        this.data = data;
    }

    public ClientDTO(String data, int siteId, int esid) {
        this.siteId = siteId;
        this.esid = esid;
        this.data = new PlainDocument(data);
    }

    public ClientDTO(ClientJupiterAlg cja) {
        this.siteId = cja.getSiteId();
        this.esid = cja.getEditingSessionId();
        this.data = cja.getDocument();
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
        return data;
    }

    public ClientDTO setDocument(Document data) {
        this.data = data;
        return this;
    }
}
