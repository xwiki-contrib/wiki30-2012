package fr.loria.score;

import fr.loria.score.client.ClientDTO;
import fr.loria.score.client.CommunicationService;
import fr.loria.score.jupiter.model.Message;
import fr.loria.score.jupiter.tree.Tree;
import fr.loria.score.jupiter.tree.TreeDocument;
import fr.loria.score.jupiter.tree.operation.*;
import fr.loria.score.server.ClientServerCorrespondents;
import fr.loria.score.server.CommunicationServiceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;


public class TreeCommunicationServiceTest  {
    private CommunicationService communicationService;

    @Before
    public void beforeTest() {
        assertEquals("Invalid nr of server correspondents", 0, ClientServerCorrespondents.getInstance().getCorrespondents().size());
        assertEquals("Invalid nr of editing sessions", 0, ClientServerCorrespondents.getInstance().getEditingSessions().size());
        communicationService = new CommunicationServiceImpl();
    }

    @After
    public void afterTest() {
        ClientServerCorrespondents.getInstance().getEditingSessions().clear();
        ClientServerCorrespondents.getInstance().getCorrespondents().clear();
    }

    @Test
    public void test2Clients() throws Exception {
        int nrClients = 2;
        int nrSessions = 1;
        int esid = 45;

        TestUtils.createServerPairs1(nrClients, esid, communicationService, 1);
        assertEquals(1, ClientServerCorrespondents.getInstance().getEditingSessions().size());
        assertEquals(nrClients, ClientServerCorrespondents.getInstance().getEditingSessions().get(esid).size());
        assertEquals(nrClients, ClientServerCorrespondents.getInstance().getCorrespondents().size());

        int[] t1 = new int[] {0, 0};
        int[] t2 = new int[] {1, 0};
        int siteId1 = 0;
        int siteId2 = 1;

        TreeOperation op01 = new TreeNewParagraph(siteId1, 0);
        TreeOperation op02 = new TreeInsertText(siteId1, 0, t1, 'A');
        TreeOperation op03 = new TreeInsertText(siteId1,1, t1, 'B');
        TreeOperation op04 = new TreeInsertParagraph(siteId1, 2, t1, true);
        TreeOperation op05 = new TreeInsertText(siteId1, 0, t2, 'F');
        TreeOperation op06 = new TreeInsertText(siteId1, 1, t2, 'G');
        TreeOperation op07 = new TreeMergeParagraph(siteId1, 1, 1,1);

        TreeOperation op11 = new TreeNewParagraph(siteId2, 0);
        TreeOperation op12 = new TreeInsertText(siteId2, 0, t1, 'E');
	    TreeOperation op13 = new TreeInsertText(siteId2, 1, t1, 'C');
        TreeOperation op14 = new TreeInsertParagraph(siteId2, 1, t1, true);
        TreeOperation op15 = new TreeMergeParagraph(siteId2, 1, 1, 1);


        Map<Integer, List<Message>> messages = new HashMap<Integer, List<Message>>();
        List<Message> site1Messages = TestUtils.createMessagesFromOperations(esid, op01, op02, op03, op04, op05, op06, op07);
        messages.put(siteId1, site1Messages);

        List<Message> site2Messages = TestUtils.createMessagesFromOperations(esid, op11, op12, op13, op14, op15);
        messages.put(siteId2, site2Messages);

        TestUtils.sendMessagesToServer(nrClients, communicationService, messages);
        TestUtils.compareServersAtQuiescence();
    }

    @Test
    public void test3Clients() throws Exception {
        int nrClients = 3;
        int esid = 10;

        TestUtils.createServerPairs1(nrClients, esid, communicationService, 1);

        assertEquals(1, ClientServerCorrespondents.getInstance().getEditingSessions().size());
        assertEquals(nrClients, ClientServerCorrespondents.getInstance().getEditingSessions().get(esid).size());
        assertEquals(nrClients, ClientServerCorrespondents.getInstance().getCorrespondents().size());

        int[] t1 = new int[] {0, 0};
        int[] t2 = new int[] {1, 0};
        int siteId1 = 0;
        int siteId2 = 1;
        int siteId3 = 2;

        TreeOperation op01 = new TreeNewParagraph(siteId1, 0);
        TreeOperation op02 = new TreeInsertText(siteId1, 0, t1, 'a');
        TreeOperation op03 = new TreeInsertText(siteId1, 1, t1, 'b');
        TreeOperation op04 = new TreeInsertParagraph(siteId1, 2, t1, true);
        TreeOperation op05 = new TreeInsertText(siteId1, 0, t2, 'f');
        TreeOperation op06 = new TreeInsertText(siteId1, 1, t2, 'g');

        final Map<Integer, List<Message>> messages = new HashMap<Integer, List<Message>>();
        List<Message> site1Messages = TestUtils.createMessagesFromOperations(esid, op01, op02, op03, op04, op05, op06);
        messages.put(siteId1, site1Messages);

        TreeOperation op11 = new TreeNewParagraph(siteId2, 0);
        TreeOperation op12 = new TreeInsertText(siteId2, 0, t1, 'e');
        TreeOperation op13 = new TreeInsertText(siteId2, 1, t1, 'c');
        TreeOperation op15 = new TreeInsertParagraph(siteId2, 1, t1, true);
        TreeOperation op14 = new TreeMergeParagraph(siteId2, 1, 1, 1);

        List<Message> site2Messages = TestUtils.createMessagesFromOperations(esid, op11, op12, op13, op15, op14);
        messages.put(siteId2, site2Messages);

        TreeOperation op21 = new TreeNewParagraph(siteId3, 0);
        TreeOperation op22 = new TreeInsertText(siteId3, 0, t1, 'i');
        TreeOperation op23 = new TreeInsertText(siteId3, 1, t1, 'd');
        TreeOperation op24 = new TreeInsertText(siteId3, 2, t1, 'j');
        TreeOperation op25 = new TreeInsertParagraph(siteId3, 2, t1,  true);

        List<Message> site3Messages = TestUtils.createMessagesFromOperations(esid, op21, op22, op23, op24, op25);
        messages.put(siteId3, site3Messages);

        TestUtils.sendMessagesToServer(nrClients, communicationService, messages);
        TestUtils.compareServersAtQuiescence();

        //a new client joins and should receive the existing content
        ClientDTO client = new ClientDTO().setEditingSessionId(esid).setDocument(new TreeDocument(new Tree("root", null)));
        client = communicationService.initClient(client);
        Integer id = client.getSiteId();
        String clientContent = client.getDocument().getContent();
        String serverContent = ClientServerCorrespondents.getInstance().getCorrespondents().get(id).getDocument().getContent();
        assertEquals("Divergent content between client and server", clientContent, serverContent);
        assertEquals("Invalid client content joining an editing session",
                ClientServerCorrespondents.getInstance().getCorrespondents().get(0).getDocument().getContent(),
                clientContent);
    }

    @Test
    public void testStyle() throws Exception {
        int nrClients = 2;
        int esid = 5;
        TestUtils.createServerPairs1(nrClients, esid, communicationService, 1);
          assertEquals(1, ClientServerCorrespondents.getInstance().getEditingSessions().size());
        assertEquals(nrClients, ClientServerCorrespondents.getInstance().getEditingSessions().get(esid).size());
        assertEquals(nrClients, ClientServerCorrespondents.getInstance().getCorrespondents().size());

        int site1 = 0;
        int site2 = 1;

        TreeOperation paragraph1 = new TreeNewParagraph(site1, 0);
        final int[] path1 = {0, 0};
        TreeOperation insertText10 = new TreeInsertText(site1, 0, path1, '1');
        TreeOperation insertText11 = new TreeInsertText(site1, 1, path1, '2');
        TreeOperation insertText12 = new TreeInsertText(site1, 2, path1, '3');
        TreeOperation style1 = new TreeStyle(site1, path1, 1, 2, "foo", "bar", true, true, true);

        TreeOperation paragraph2 = new TreeNewParagraph(site2, 0);
        TreeOperation insertText2 = new TreeInsertText(site2, 0, path1, '5');
        TreeOperation paragraph21 = new TreeNewParagraph(site2, 1);
        TreeOperation insertText22 = new TreeInsertText(site2, 0, new int[] {1, 0}, '6');


        Map<Integer, List<Message>> messages = new HashMap<Integer, List<Message>>();
        List<Message> messages1 = TestUtils.createMessagesFromOperations(esid, paragraph1, insertText10, insertText11, insertText12, style1);
        messages.put(site1, messages1);

        List<Message> messages2 = TestUtils.createMessagesFromOperations(esid, paragraph2, insertText2, paragraph21, insertText22);
        messages.put(site2, messages2);

        TestUtils.sendMessagesToServer(nrClients, communicationService, messages);
//        //todo: assert what you expect
        TestUtils.compareServersAtQuiescence();
    }

    @Test
	public void testMove1() throws Exception {
        int esid = 15;
        int nrClients = 2;
        TestUtils.createServerPairs1(nrClients, esid, communicationService, 1);

         assertEquals(1, ClientServerCorrespondents.getInstance().getEditingSessions().size());
        assertEquals(nrClients, ClientServerCorrespondents.getInstance().getEditingSessions().get(esid).size());
        assertEquals(nrClients, ClientServerCorrespondents.getInstance().getCorrespondents().size());

        int site1 = 0;
        int site2 = 1;

		TreeOperation paragraph1=new TreeNewParagraph(site1, 0);
		TreeOperation insertText1=new TreeInsertText(site1, 0, new int[] {0,0},'x');
		TreeOperation paragraph11=new TreeNewParagraph(site1, 1);
		TreeOperation insertText11=new TreeInsertText(site1, 0, new int[] {1,0},'y');
//        TreeOperation move1=new TreeMoveParagraph(site1, 0, 1);

        TreeOperation paragraph2=new TreeNewParagraph(site2, 0);
        TreeOperation insertText2=new TreeInsertText(site2, 0, new int[] {0,0},'z');

        Map<Integer, List<Message>> messages = new HashMap<Integer, List<Message>>();
        List<Message> message1 = TestUtils.createMessagesFromOperations(esid, paragraph1, insertText1, paragraph11, insertText11);
        messages.put(site1, message1);

        List<Message> message2 = TestUtils.createMessagesFromOperations(esid, paragraph2, insertText2);
        messages.put(site2, message2);

        TestUtils.sendMessagesToServer(nrClients, communicationService, messages);
        TestUtils.compareServersAtQuiescence();
	}
}
