package fr.loria.score.client;

import com.google.gwt.core.client.JavaScriptObject;

public final class Editor extends JavaScriptObject {
    private static int oldCaretPos;

    protected Editor() {
    }

    public static native Editor getEditor() /*-{
        return $wnd.editor;
    }-*/;

    public native void addHooksToEventListeners(RtApi.EditorApi api)/*-{

        $wnd.insertHook = function(str, position) {
            api.@fr.loria.score.client.RtApi.EditorApi::clientInsert(Ljava/lang/String;I)(str, position);
        };

        $wnd.enterHook = function(position, cartPos) {
            api.@fr.loria.score.client.RtApi.EditorApi::clientInsert(Ljava/lang/String;I)( "\n", position);
        };

        $wnd.deleteHook = function(position) {
            api.@fr.loria.score.client.RtApi.EditorApi::clientDelete(I)(position);
        };

        $wnd.deleteStringHook = function(from, end) {
            if (from > end) { // user could select starting from line end backwards
                api.@fr.loria.score.client.RtApi.EditorApi::clientDelete(II)(end, from);
            } else {
                api.@fr.loria.score.client.RtApi.EditorApi::clientDelete(II)(from, end);
            }
        };

        $wnd.tabHook = function(position) {
            api.@fr.loria.score.client.RtApi.EditorApi::clientInsert(Ljava/lang/String;I)("    ", position);
        };
    }-*/;


    public native void setContent(String content) /*-{
        $wnd.editor.setContent(content);
    }-*/;

    public native void paint() /*-{
        $wnd.editor.paint();
    }-*/;


    public native void insertHighlighting(int siteId, int colNr, int lineNr) /*-{
        $wnd.editor.insertHighlighting(siteId, colNr, lineNr);

    }-*/;

    public native void removeHighlighting(int siteId, int colNr, int lineNr) /*-{
        $wnd.editor.removeHighlighting(siteId, colNr, lineNr);
    }-*/;

    public native void onRemoteEnterHighlighting(int colNr, int lineNr) /*-{
        $wnd.editor.onEnterHighlight(colNr + 1, lineNr);
    }-*/;

    /**
     * Local caret consistency with respect to remote operations
     */
    
    public native int getCaretPosition()/*-{
        return $wnd.editor.cursor.getPosition();
    }-*/;

    public int getOldCaretPos() {
        return this.oldCaretPos;
    }

    public void setOldCaretPos(int oldCaretPos) {
        this.oldCaretPos = oldCaretPos;
    }

    /**
     * Shifts left/right the UI caret.
     * N.B. the new content was setup before calling this method
     * @param position the position <strong> in the linear model</strong> at which the insert/remove op occurred
     */
    public native void shiftCaret(int position) /*-{
        var caret = $wnd.editor.cursor;
        caret.toPosition(position);
    }-*/;
}
