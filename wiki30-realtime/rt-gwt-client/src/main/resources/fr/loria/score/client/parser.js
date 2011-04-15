/** Textile parser **/
Textile.Parser = Textile.Utils.makeClass({

    constructor: function(model, from, to) {
        this.model = model;
        this.from = from;
        this.to = to;
        if (!/^$/.test(this.model.lines[this.from - 1].content)) {
            while (this.from > 1) {
                if (!/^$/.test(this.model.lines[this.from - 1].content)) {
                    this.from--;
                } else {
                    this.from++;
                    break;
                }
            }
        }
        this.text = '';
        for (var i = this.from; i <= this.to; i++) {
            if (i > this.model.lines.length) {
                continue;
            }
            this.text += this.model.lines[i - 1].content;
            if (this.model.lines[i] && this.model.lines[i].line > this.model.lines[i - 1].line) {
                this.text += '\n';
            } else {
                this.text += '\r';
            }
        }
        this.len = this.text.length;
        this.end = this.begin = 0;
        this.state = 'PLAIN';
    },

    found: function(newState, skip, beginOffset) {
        var begin2 = beginOffset ? beginOffset + this.begin : this.begin;
        var end2 = --this.end + skip;
        this.lastState = this.state;
        var text = this.text.substring(begin2, end2);
        var lines = text.match(/[\n\r]/g);
        var from = this.from;
        var to = this.from + (lines ? lines.length - 1 : 0);
        this.from = this.from + (lines ? lines.length : 0);
        this.begin = this.end += skip;
        this.state = newState;
        return {
            type: this.lastState,
            text: text,
            startLine: from,
            endLine: to
        };
    },

    checkHas: function(pattern, skip, noThis) {
        var nc = this.end + (skip ? skip : 0);
        while (nc < this.text.length && this.text.charAt(nc) != '\n') {
            var e = '';
            for (var i = 0; i < pattern.length; i++) {
                e += this.text.charAt(nc + i);
            }
            if (e == pattern) {
                return true;
            }
            if (noThis && e.charAt(0).match(noThis)) {
                return false;
            }
            nc++;
        }
        return false;
    },

    nextToken: function() {
        for (; ;) {
            var left = this.len - this.end;
            if (left < 1 || !left) {
                this.end++;
                return this.found('EOF', 0);
            }

            var c = this.text.charAt(this.end++);
            var c1 = left > 1 ? this.text.charAt(this.end) : 0;
            var c2 = left > 2 ? this.text.charAt(this.end + 1) : 0;
            var c3 = left > 3 ? this.text.charAt(this.end + 2) : 0;

            /** The STATE machine **/
            if (this.state == 'PLAIN') {
                if (c == '~' && (/[1-9]/.test(c1)) && c2 == ':') {
                    var skip = 3;
                    return this.found('REMOTE', 0);
                }

                if (c1 == "~" && (/[1-9]/).test(c2) && c3 == ':') {
                    return this.found('PLAIN', 1);
                }

//                if (c != '\n') {
//                    return this.found('PARAGRAPH', 0);
//                }
            }

            if (this.state == 'REMOTE') {
                if (c == '~' && (/[1-9]/.test(c1)) && c2 == ":") {
                        skip = 3;
//                    } else if (/[0-9]/.test(c2) && c3 == ':') {
//                        skip = 4;
//                    }
                    return this.found('PLAIN', skip + 1, 3);
                }
            }

            if (true /** MATCH ALL**/) {
                if (c1 == "~" && (/[1-9]/).test(c2) && c3 == ':') {
                    return this.found('PLAIN', 1);
                }
                
                if (c == '\n' && c1 == '\n') {
                    return this.found('PLAIN', 1);
                }
            }

        }
    }

});
