/** The editor itself **/
Textile.Editor = Textile.Utils.makeClass({

    model: null,
    ctx: null,
    el: null,
    cursor: null,

    lineHeight: 17,
    first_line: 1,
    gutterWidth: 30,
    paddingTop: 5,
    paddingLeft: 15,
    font: '9pt Monaco, Lucida Console, monospace',

    hasFocus: false,
    selection: null,

    highlighter: [], // maps site ids to the array of highlighting ranges: 1 -> [lineNr][colNr1, colNr2, ...]
    shift: false, // barbarically detect Shift, as the evt.shiftKey doesn't seem to work

    constructor: function(canvasEl) {
        this.el = (typeof(canvasEl) == 'string' ? document.getElementById(canvasEl) : canvasEl);
        if (!this.el.getContext) {
            // Too bad.
            return;
        }
        this.ctx = this.el.getContext('2d');
        if (!this.ctx.fillText) {
            // Too bad.
            return;
        }
        this.model = new Textile.Model(this.el.innerHTML, this);
        this.cursor = new Textile.Cursor(this);
        this.history = new Textile.History(this);
        this.clipboard = new Textile.Clipboard(this);

        // Gecko detection
        this.gecko = false;

        // Events
        this.el.addEventListener('dblclick', Textile.Utils.bind(this.onDblclick, this), true);
        document.addEventListener('mousedown', Textile.Utils.bind(this.onMousedown, this), true);
        document.addEventListener('mouseup', Textile.Utils.bind(this.onMouseup, this), true);
        document.addEventListener('mousemove', Textile.Utils.bind(this.onMousemove, this), true);
        document.addEventListener('keypress', Textile.Utils.bind(this.onKeypress, this), true);
        document.addEventListener('keydown', Textile.Utils.bind(this.onKeydown, this), true);
        document.addEventListener('keyup', Textile.Utils.bind(this.onKeyup, this), true);
        this.el.addEventListener('mousewheel', Textile.Utils.bind(this.onMousewheel, this), true);

        //resize the editor to fit it's parent container dimensions
        window.onresize = function(evt) {
            var parentElement = document.getElementById("xwikieditcontentinner"); //todo-bf: get the parent element programatically
            this.editor.resize(parentElement.clientWidth, parentElement.clientHeight);
        };

        // Gecko hacks
        this.el.addEventListener('DOMMouseScroll', Textile.Utils.bind(this.onMousewheelGecko, this), true);

        // First
        this.resize(this.el.width, this.el.height);
    },

    setContent: function(content) {
        this.model.content = content;
        this.model.update();
//        this.paint();
    },

    getContent: function() {
        return this.model.content;
    },

    getPosition: function() {
        var pos = jQuery(this.el).position();
        return {
            top: pos.top + parseInt(jQuery(this.el).css('borderTopWidth')) + parseInt(jQuery(this.el).css('paddingTop')) + parseInt(jQuery(this.el).css('marginTop')),
            left: pos.left + parseInt(jQuery(this.el).css('borderLeftWidth')) + + parseInt(jQuery(this.el).css('paddingLeft')) + + parseInt(jQuery(this.el).css('marginLeft'))
        }
    },

    resize: function(w, h) {
        this.width = w;
        this.height = h;
        this.el.width = w;
        this.el.height = h;
        this.ctx.font = this.font;
        var txt = ' ';
        for (var i = 0; i < 500; i++) {
            if (this.ctx.measureText(txt).width < this.width - 10 - this.gutterWidth - 2 * this.paddingLeft) {
                txt += ' ';
            } else {
                this.charWidth = this.ctx.measureText(txt).width / txt.length;
                break;
            }
        }
        this.lineWidth = Math.round((this.width - 10 - this.gutterWidth - 2 * this.paddingLeft ) / this.charWidth);
        this.lines = Math.round((this.height - this.paddingTop * 3) / this.lineHeight);
        this.model.update();
        this.paint();
    },

    onMousedown: function(e) {
        if (e.target == this.el) {
            this.hasFocus = true;
        } else {
            this.hasFocus = false;
        }
        // Scrollbar click ?
        if (e.pageX > this.getPosition().left + this.width - 20 && e.target == this.el) {
            var h = this.lines * this.lineHeight;
            var olh = h / this.model.lines.length;
            var bar = this.lines * olh;
            if (bar < 10) bar = 10;
            var o = (this.first_line - 1) * olh;
            var y = e.pageY - this.getPosition().top - this.paddingTop;
            // The bar itself
            if (y > o && y < o + bar) {
                this.scrollBase = e.pageY;
                this.scrollBaseLine = this.first_line;
            }
            // Up
            else if (y < o) {
                this.onMousewheel({wheelDelta: 1});
            }
            // Down
            else {
                this.onMousewheel({wheelDelta: -1});
            }
        }
        // Text click
        else {
            this.selection = null;
            this.bp = true;
            if (e.target == this.el) {
                this.cursor.fromPointer(this.translate(e));
                this.paint();
            } else {
                this.paint();
            }
        }
    },

    onDblclick: function(e) {
        var txt = this.model.lines[this.cursor.line - 1].content;
        var c = this.cursor.column;
        while (txt.charAt(c).match(/\w/) && c > -1) {
            c--;
        }
        c++;
        this.selection = {
            anchor: this.cursor.getPosition(),
            from: c + this.model.lines[this.cursor.line - 1].offset,
            to: null
        }
        c = this.cursor.column + 1;
        while (txt.charAt(c).match(/\w/) && c < txt.length) {
            c++;
        }
        this.selection.to = c + this.model.lines[this.cursor.line - 1].offset;
        this.paint();
    },

    onMouseup: function(e) {
        // Clear all stuff
        this.bp = false;
        this.scrollBase = null;
        clearTimeout(this.autoscroller);
        if (this.selection && (this.selection.from == null || this.selection.to == null)) {
            this.selection = null;
        }
    },

    onMousemove: function(e) {
        // Change cursor automatically
        if (e.pageX > this.getPosition().left + this.width - 20 && e.target == this.el) {
            this.el.style.cursor = 'default';
        } else {
            this.el.style.cursor = 'text';
        }
        if (!this.hasFocus) return;
        // A scroll ?
        if (this.scrollBase) {
            var h = this.lines * this.lineHeight;
            var olh = h / this.model.lines.length;
            var line = Math.round((e.pageY - this.scrollBase) / olh) + this.scrollBaseLine;
            this.onMousewheel({}, line);
            return;
        }
        // A selection ?
        if (this.bp) {
            if (!this.selection) {
                this.selection = {
                    anchor: this.cursor.getPosition(),
                    from: null,
                    to: null
                }
            } else {
                this.cursor.fromPointer(this.translate(e));
                var newBound = this.cursor.getPosition();
                if (newBound < this.selection.anchor && this.selection.from != newBound) {
                    this.selection.from = newBound;
                    this.selection.to = this.selection.anchor;
                    this.paint();
                }
                if (newBound > this.selection.anchor && this.selection.to != newBound) {
                    this.selection.from = this.selection.anchor;
                    this.selection.to = newBound;
                    this.paint();
                }
                if (newBound == this.selection.anchor && this.selection.from != null) {
                    this.selection.from = null;
                    this.selection.to = null;
                    this.paint();
                }
            }
        }
        // Auto-scroll while selecting
        var auto = false;
        if (this.bp) {
            if (e.pageY < this.getPosition().top) {
                this.onMousewheel({wheelDelta: 1});
                auto = true;
            }
            if (e.pageY > this.getPosition().top + this.height) {
                this.onMousewheel({wheelDelta: -1});
                auto = true;
            }
        }
        clearTimeout(this.autoscroller);
        if (auto) {
            this.autoscroller = setTimeout(Textile.Utils.bind(function() {
                this.onMousemove(e);
            }, this), 10);
        }
    },

    onMousewheel: function(e, o) {
        // Hack. Call it with e = null, for direct line access
        if (o != null) {
            this.first_line = o;
        } else {
            var delta = e.wheelDelta;
            if (delta > 0) {
                this.first_line--;
            } else {
                this.first_line++;
            }
        }
        if (e.preventDefault) e.preventDefault();
        this.cursor.bound();
        this.paint();
    },

    onMousewheelGecko: function(e) {
        if (e.axis == e.VERTICAL_AXIS) {
            this.onMousewheel({
                wheelDelta: -e.detail
            });
            e.preventDefault();
        }
    },

    onKeydown: function(e, force) {
        if (this.hasFocus && (!this.gecko || force)) {
            var keyCode = e.keyCode || e.which;
            if (keyCode == Keys.SHIFT) {
                this.shift = true;
                return;
            }
            if (this.shift) {
                var caretPos = this.cursor.getPosition();
                // implement Shift + left/right arrows, Shift + Home/End
                if (keyCode == Keys.HOME) {
                    if (this.selection) {
                        this.selection.to = this.model.lines[this.cursor.line - 1].offset;
                    } else {
                        this.selection = {
                            from: caretPos,
                            to: this.model.lines[this.cursor.line - 1].offset
                        }
                    }
                } else if (keyCode == Keys.END) {
                    var actualLine = this.model.lines[this.cursor.line - 1];
                    if (this.selection) {
                        this.selection.to = actualLine.content.length + actualLine.offset;
                    } else {
                        this.selection = {
                            from: caretPos,
                            to: actualLine.content.length + actualLine.offset
                        }
                    }
                } else if (keyCode == Keys.LEFT_ARROW) {
                    if (this.selection) {
                        this.selection.to = this.selection.to - 1;
                    } else {
                        this.selection = {
                            from: caretPos,
                            to: caretPos - 1  // in paintSelection cursor is automatically put at position to
                        }
                    }
                } else if (keyCode == Keys.RIGHT_ARROW) {
                    if (this.selection) {
                        this.selection.to = this.selection.to + 1;
                    } else {
                        this.selection =  {
                            from: caretPos,
                            to: caretPos + 1
                        }
                    }
                }
                this.cursor.focus();
                return;
            }

            if (e.metaKey || e.ctrlKey) { // meta key == MACINTOSH Command key
                if (keyCode == Keys.C) { //copy
                    this.clipboard.copy();
                } else if (keyCode == Keys.V) { //paste
                    this.clipboard.paste();
                } else if (keyCode == Keys.X) { // cut
                    this.clipboard.cut();
                }
                // Ctrl + Home/End goes to beginning/end of document
                if (keyCode == Keys.HOME) {
                    this.cursor.toPosition(0);
                    this.cursor.focus();
                } else if (keyCode == Keys.END) {
                    this.cursor.toPosition(Number.MAX_VALUE);
                    this.cursor.focus();
                }
                return;
            }
            this.cursor.show = true;
            //todo: pehaps a switch..
            if (keyCode == Keys.HOME) {
                e.preventDefault();
                this.cursor.home();
                this.cursor.focus();
                return;
            }
            if (keyCode == Keys.END) {
                e.preventDefault();
                this.cursor.end();
                this.cursor.focus();
                return;
            }
            // ~~~~ MOVE
            if (e.keyCode == Keys.DOWN_ARROW) {
                e.preventDefault();
                this.cursor.lineDown();
                this.cursor.focus();
                return;
            }
            if (e.keyCode == Keys.UP_ARROW) {
                e.preventDefault();
                this.cursor.lineUp();
                this.cursor.focus();
                return;
            }
            if (e.keyCode == Keys.LEFT_ARROW) {
                e.preventDefault();
                this.cursor.left();
                this.cursor.focus();
                return;
            }
            if (e.keyCode == Keys.RIGHT_ARROW) {
                e.preventDefault();
                this.cursor.right();
                this.cursor.focus();
                return;
            }
            // ~~~~ With pos
            var position = this.cursor.getPosition();
            // ENTER
            if (e.keyCode == Keys.ENTER) {
                e.preventDefault();
                if (this.selection) {
                    if (Textile.Utils.isFunction(window.enterHook) && Textile.Utils.isFunction(window.deleteStringHook)) {
                        window.deleteStringHook(this.selection.from, this.selection.to);
                        window.enterHook(this.selection.from);
                    }
                    this.model.replace(this.selection.from, this.selection.to, '\n');
                    this.cursor.toPosition(Math.min(this.selection.from, this.selection.to) + 1);
                    this.selection = null;
                } else {
                    if (Textile.Utils.isFunction(window.enterHook)) {
                        window.enterHook(position);
                    }
//                    this.onEnterHighlight(this.cursor.getCartesianPosition().x, this.cursor.getCartesianPosition().y -1);
                    this.model.lineBreak(position);
                    this.cursor.toPosition(position + 1);
                }
                this.cursor.focus();
                return;
            }
            // BACKSPACE
            if (e.keyCode == Keys.BACKSPACE) {
                e.preventDefault();
                if (this.selection) {
                    if (Textile.Utils.isFunction(window.deleteStringHook)) {
                        window.deleteStringHook(this.selection.from, this.selection.to);
                    }
                    this.model.replace(this.selection.from, this.selection.to, '');
                    this.cursor.toPosition(Math.min(this.selection.from, this.selection.to));
                    this.selection = null;
                } else {
                    if (position == 0) {return}

                    if (Textile.Utils.isFunction(window.deleteHook)) {
                        window.deleteHook(position - 1);
                    }
//                    this.shiftLocalIndexes(cartPos.x - 1, cartPos.y - 1, true);
                    this.model.deleteLeft(position);
                    this.cursor.toPosition(position - 1);
                }
                this.cursor.focus();
                return;
            }
            // TAB
            if (e.keyCode == Keys.TAB) {
                e.preventDefault();
                if (this.selection) {
                    if (Textile.Utils.isFunction(window.deleteStringHook) && Textile.Utils.isFunction(window.tabHook)) {
                        window.deleteStringHook(this.selection.from, this.selection.to);
                        window.tabHook(this.selection.from);
                    }
                    this.model.replace(this.selection.from, this.selection.to, '    ');
                    this.cursor.toPosition(Math.min(this.selection.from, this.selection.to) + 4);
                    this.selection = null;
                } else {
                    this.model.insert(position, '    ');
                    this.cursor.toPosition(position + 4);
                    if (Textile.Utils.isFunction(window.tabHook)) {
                        window.tabHook(position);
                    }
                }
                this.cursor.focus();
                return;
            }
            // SUPPR
            if (e.keyCode == Keys.DELETE) {
                e.preventDefault();
                if (this.selection) {
                    if (Textile.Utils.isFunction(window.deleteStringHook)) {
                        window.deleteStringHook(this.selection.from, this.selection.to);
                    }
                    this.model.replace(this.selection.from, this.selection.to, '');
                    this.cursor.toPosition(Math.min(this.selection.from, this.selection.to));
                    this.selection = null;
                } else {
                    if (position == this.model.content.length) {return}
                    if (Textile.Utils.isFunction(window.deleteHook)) {
                        window.deleteHook(position);
                    }
    //                this.shiftLocalIndexes(cartPos.x, cartPos.y -1, true);
                    this.model.deleteRight(position);
                    this.cursor.toPosition(position);
                }
                this.cursor.focus();
                return;
            }
        }
    },

    onKeypress: function(e) {
        if (!e.charCode || e.charCode == Keys.ENTER || e.keyCode == Keys.BACKSPACE) {
            if (this.gecko) this.onKeydown(e, true);
            return;
        }
        if (this.hasFocus) {
            this.cursor.show = true;
            var position = this.cursor.getPosition();
            if (e.metaKey || e.ctrlKey) {
                if (e.charCode == Keys.NUMPAD1) {
                    e.preventDefault();
                    this.selection = {
                        anchor: 0,
                        from: 0,
                        to: this.model.content.length
                    }
                    this.paint();
                }
                if (e.charCode == Keys.F11) {
                    this.history.undo();
                }
                if (e.charCode == Keys.F10) {
                    this.history.redo();
                }
                
                return;
            }
            // CHARS
            var c = String.fromCharCode(e.charCode);

            e.preventDefault();
            if (this.selection) {
                this.model.replace(this.selection.from, this.selection.to, c);
                this.cursor.toPosition(Math.min(this.selection.from, this.selection.to) + 1);
                if (Textile.Utils.isFunction(window.deleteStringHook) && Textile.Utils.isFunction(window.insertHook)) {
                    window.deleteStringHook(this.selection.from, this.selection.to);
                    window.insertHook(c, Math.min(this.selection.from, this.selection.to));
                }
                this.selection = null;
            } else {
                this.model.insert(position, c);
                this.cursor.toPosition(position + 1);

                if (Textile.Utils.isFunction(window.insertHook)) {
                    window.insertHook(c, position);
                }
//                this.shiftLocalIndexes(cartPos.x -1, cartPos.y -1, false);
            }
            this.cursor.focus();
        }
    },

    onKeyup: function(e) {
        if (this.hasFocus) {
            var kk = e.keyCode || e.which;
            if (kk == Keys.SHIFT) {
                this.shift = false;
                this.paintCursor();
            }
        }
    },

    translate: function(e) {
        var pos = this.getPosition();
        return {
            x: e.pageX - pos.left - this.gutterWidth - this.paddingLeft,
            y: e.pageY - pos.top - this.paddingTop
        }
    },

    updateCursor: function() {
        this.showCursor = this.hasFocus && !this.showCursor;
        this.paint();
    },

    paint: function() {
        try {
            this.paintBackground();
            this.paintLineNumbers();
            //this.paintHighlighting();
            this.paintSelection();
            this.paintContent();
            this.paintScrollbar();
            this.paintCursor();
        } catch(e) {
        // due to asynchronous receival of remote ops,
        // it might happen that inner model data is not synchronized with inner cursor data
        // (both of which are used in this rendering phase)
        }
    },

    paintBackground: function() {
        var style = Textile.Theme['PLAIN'];
        if (style && style.background) {
            this.ctx.fillStyle = style.background;
        } else {
            this.ctx.fillStyle = '#000';
        }
        this.ctx.fillRect(0, 0, this.width, this.height);
        //
        var parser = new Textile.Parser(this.model, this.first_line, this.first_line + this.lines - 1);
        var token = parser.nextToken();
        var x = 0, y = 1;
        while (token.type != 'EOF') {
            style = Textile.Theme[token.type];
            if (style && style.background) {
                this.ctx.fillStyle = style.background;
                for (var i = token.startLine - this.first_line; i <= token.endLine - this.first_line; i++) {
                    this.ctx.fillRect(this.gutterWidth + this.paddingLeft, (i) * this.lineHeight + this.paddingTop, this.charWidth * (this.lineWidth - 1), this.lineHeight);
                }
            }
            // Yop
            token = parser.nextToken();
        }
    },

    paintSelection: function() {
        if (this.hasFocus) {
            var style = Textile.Theme['SELECTION'];
            if (style && style.background) {
                this.ctx.fillStyle = style.background;
            } else {
                this.ctx.fillStyle = 'rgba(255, 248, 198, .75)';
            }
            if (!this.selection) {
                if (this.cursor.isVisible()) {
                    this.ctx.fillRect(this.gutterWidth + 1, (this.cursor.line - this.first_line) * this.lineHeight + this.paddingTop, this.width - this.gutterWidth, this.lineHeight);
                }
            } else {
                this.cursor.toPosition(this.selection.from);
                var fl = this.cursor.line, fc = this.cursor.column;
                this.cursor.toPosition(this.selection.to);
                var tl = this.cursor.line, tc = this.cursor.column;
                if (fl == tl) {
                    this.ctx.fillRect(this.gutterWidth + this.paddingLeft + fc * this.charWidth, (fl - this.first_line) * this.lineHeight + this.paddingTop, (tc - fc) * this.charWidth, this.lineHeight);
                } else {
                    for (var i = fl; i <= tl; i++) {
                        if (this.cursor.isLineVisible(i)) {
                            if (i == fl) {
                                this.ctx.fillRect(this.gutterWidth + this.paddingLeft + fc * this.charWidth, (i - this.first_line) * this.lineHeight + this.paddingTop, (this.lineWidth - fc) * this.charWidth, this.lineHeight);
                                continue;
                            }
                            if (i == tl) {
                                this.ctx.fillRect(this.gutterWidth + this.paddingLeft, (i - this.first_line) * this.lineHeight + this.paddingTop, tc * this.charWidth, this.lineHeight);
                                continue;
                            }
                            this.ctx.fillRect(this.gutterWidth + this.paddingLeft, (i - this.first_line) * this.lineHeight + this.paddingTop, this.lineWidth * this.charWidth, this.lineHeight);
                        }
                    }
                }
            }
        }
    },

    paintLineNumbers: function() {
        this.ctx.fillStyle = '#DEDEDE';
        this.ctx.fillRect(0, 0, this.gutterWidth, this.height);
        this.ctx.fillStyle = '#8E8E8E';
        this.ctx.fillRect(this.gutterWidth, 0, 1, this.height);
        this.ctx.font = this.font;
        var previousLine = null;
        var rl = 1;
        for (var i = this.first_line; i < this.first_line + this.lines; i++) {
            if (i > this.model.lines.length) {
                break;
            }
            if (this.hasFocus && !this.selection && this.model.lines[i - 1].line == this.model.lines[this.cursor.line - 1].line) {
                this.ctx.fillStyle = '#000000';
            } else {
                this.ctx.fillStyle = '#888888';
            }
            var ln = '';
            if (this.model.lines[i - 1].line == previousLine) {
                ln = '\u00B7';
            } else {
                previousLine = (this.model.lines[i - 1].line);
                ln = previousLine + '';
            }
            var w = ln.length * 8;
            this.ctx.fillText(ln, this.gutterWidth - this.paddingLeft - w, rl++ * this.lineHeight + this.paddingTop - 4);
        }
    },

    paintContent: function() {
        var parser = new Textile.Parser(this.model, this.first_line, this.first_line + this.lines - 1);
        var token = parser.nextToken();
        var x = 0, y = 1;
        while (token.type != 'EOF') {
            if (token.text) {
                var style = Textile.Theme[token.type];
                if (style && style.color) {
                    this.ctx.fillStyle = style.color;
                } else {
                    this.ctx.fillStyle = '#FFF';
                }
                if (style && style.fontStyle) {
                    this.ctx.font = style.fontStyle + ' ' + '12px Monaco, Lucida Console, monospace';
                } else {
                    this.ctx.font = '12px Monaco, Lucida Console, monospace';
                }
                if (token.text.indexOf('\n') > -1 || token.text.indexOf('\r') > -1) {
                    var lines = token.text.split(/[\n\r]/);
                    for (var i = 0; i < lines.length; i++) {
                        if (token.startLine + i >= y + this.first_line - 1 && token.startLine + i <= this.first_line + this.lines - 1) {
                            this.ctx.fillText(lines[i], this.gutterWidth + this.paddingLeft + x * this.charWidth, y * this.lineHeight + this.paddingTop - 4);
                            x += lines[i].length;
                            if (i < lines.length - 1) {
                                x = 0;
                                y++;
                            }
                        }
                    }
                } else {
                    if (token.startLine >= y + this.first_line - 1 && token.startLine <= this.first_line + this.lines - 1) {
                        this.ctx.fillText(token.text, this.gutterWidth + this.paddingLeft + x * this.charWidth, y * this.lineHeight + this.paddingTop - 4);
                        if (style && (style.underline || style.foo)) {
                            this.ctx.fillRect(this.gutterWidth + this.paddingLeft + x * this.charWidth, y * this.lineHeight + this.paddingTop - 4 + 1, token.text.length * this.charWidth + 1, 1);
                        }
                        x += token.text.length;
                    }
                }
            }
            // Yop
            token = parser.nextToken();
        }
    },

    paintCursor: function() {
        if (this.hasFocus && this.cursor.show && !this.selection && this.cursor.isVisible()) {
            this.ctx.fillStyle = Textile.Theme['CURSOR'].color;
            this.ctx.fillRect(this.gutterWidth + this.paddingLeft + this.cursor.column * this.charWidth, this.paddingTop + ((this.cursor.line - this.first_line) * this.lineHeight), 1, this.lineHeight);
        }
    },

    paintScrollbar: function() {
        if (this.model.lines.length > this.lines) {
            var h = this.lines * this.lineHeight;
            var olh = h / this.model.lines.length;
            var bar = this.lines * olh;
            var o = (this.first_line - 1) * olh;
            // Draw
            this.ctx.strokeStyle = Textile.Theme['SCROLLBAR'].strokeStyle;
            this.ctx.lineWidth = 10;
            this.ctx.beginPath();
            this.ctx.moveTo(this.width - 10, this.paddingTop + o);
            this.ctx.lineTo(this.width - 10, this.paddingTop + o + bar);
            //            this.ctx.closePath();
            this.ctx.stroke();
        }
    },

    paintHighlighting: function() {
        //apply the highlighting
        var style = Textile.Theme['REMOTE'];
        for (var siteId in this.highlighter) {
            if (style  && style.colors) {
                this.ctx.fillStyle = style.colors[siteId % 9]; // 9 colors
            } else {
                this.ctx.fillStyle = '#09D9F6'; //default
            }

            var lines = this.highlighter[siteId];
            for (var lineNr in lines) {
                var dy = parseInt(lineNr) - this.first_line + 1;
                if (dy >= 0 && dy < this.lines) { //highlight just the current view, not more
                    var y = dy * this.lineHeight + this.paddingTop;
                    var colsArray = lines[lineNr];
                    for (var colNr in colsArray) {
                        var x = this.gutterWidth + this.paddingLeft + parseInt(colsArray[colNr]) * this.charWidth;
                        this.ctx.fillRect(x, y, this.charWidth, this.lineHeight);
                    }
                }
            }
        }
    },

    /**
     * Shifts remote indexes and inserts data in the highlighting structure.
     * This is done when a remote operation arrives at 'this' client
     *
     * @param siteId the remote site id
     * @param colNr the column nr
     * @param lnNr the line nr
     * @param remove true for delete operation
     */
    insertHighlighting: function (siteId, colNr, lnNr, remove) {
        var hgh = this.highlighter;
        var lines = hgh[siteId];

        if (lines && lines != null) {
            var colsArray = lines[lnNr];

            if (colsArray && colsArray != null) {
                //shift right some indexes
                this.shiftIndexes(siteId, colNr, lnNr, false);

                colsArray.push(colNr);
                //sort asc
                colsArray.sort(function(a, b){return a - b});
            } else {
                lines[lnNr] = [colNr];
            }
        } else {
            hgh[siteId] = new Array();
            hgh[siteId][lnNr] = [colNr];
        }
    },

    removeHighlighting: function(siteId, colNr, lineNr) {

        var colsArray = this.highlighter[siteId][lineNr];
        if (colsArray && colsArray != null) {
            var idx = colsArray.indexOf(colNr);
            if (idx > -1) { //really found it
                colsArray.splice(idx, 1);
            }
        }

        this.shiftLocalIndexes(colNr, lineNr, true);
    },

    /**
     * Shifts left/right the highlighting indexes when a local insert/remove operation occurs
     * @param colNr the column at which local op occurred
     * @param lineNr the line at which local op occurred
     * @param remove true for remove op
     */
    shiftLocalIndexes: function(colNr, lineNr, remove) { //todo: remote
        for (var siteId in this.highlighter) {
            this.shiftIndexes(siteId, colNr, lineNr, remove);
        }
    },

    shiftIndexes: function (siteId, colNr, lineNr, remove) {
        var lines = this.highlighter[siteId];
        for (var line in lines) {
            if(line == lineNr) {
                var oldColsArray = this.highlighter[siteId][line];
                if (remove) {
                    //Remove: shift left
                    for (var oldColNr in oldColsArray) {
                        if (oldColsArray[oldColNr] > colNr) { //
                            oldColsArray[oldColNr]--;
                        }
                        oldColsArray.filter(function(elem){return elem >=0;})
                    }
                } else {
                     //Insert: shift right
                    for(oldColNr in oldColsArray) {
                        if (oldColsArray[oldColNr] >= colNr) {
                            oldColsArray[oldColNr]++;
                        }
                    }
                }
            }
        }
    },

    /**
     * Moves with one line down the highlighters when Enter hit
     * @param colNr
     * @param lineNr
     */
    onEnterHighlight: function (colNr, lineNr) {
        for (var siteId in this.highlighter) {
            var lines = this.highlighter[siteId];
            for (var line = lines.length - 1; line >= 0; line--) {
                var colsArray = this.highlighter[siteId][line];
                if (colsArray == undefined || colsArray == null) {continue;}

                    var ln = line;
                    if (line > lineNr) { // pull down all lines below
                        this.highlighter[siteId][ln] = null;
                        this.highlighter[siteId][++ln] = colsArray;
                    } else if (line == lineNr) {
                        var i = 0;
                        if (colNr <= colsArray[0]) {
                            i = 0;
                        } else if (colNr > colsArray[colsArray.length - 1]) { // do nothing
                            break;
                        } else {
                            var indx = colsArray.indexOf(colNr);
                            if (indx > 0) {
                                i = indx;    
                            } else {
                                for (var idx = 0; colsArray[idx] < colNr; idx++);
                                i = idx - 1;
                            }
                        }
                        // split the current line (where you hit enter) in 2 lines
                        i == 0 ? this.highlighter[siteId][line] = null : this.highlighter[siteId][line] = colsArray.slice(0, i);
                        this.highlighter[siteId][++ln] = colsArray.slice(i).map(
                                function(nr){
                                //and substract colNr
                                    return nr - colNr;
                                }
                        ).filter(function(elem) { //filter negative vals
                            return elem >=0;
                        });
                    }
                }
            }
        }
});
