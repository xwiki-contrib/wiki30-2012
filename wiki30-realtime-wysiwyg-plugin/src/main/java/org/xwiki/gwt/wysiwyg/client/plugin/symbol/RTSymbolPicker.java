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
package org.xwiki.gwt.wysiwyg.client.plugin.symbol;

import org.xwiki.gwt.user.client.ui.CompositeDialogBox;
import org.xwiki.gwt.wysiwyg.client.Images;
import org.xwiki.gwt.wysiwyg.client.Strings;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Image;

/**
 * A popup panel which allows you to pick a symbol from a symbol palette by clicking on that symbol.
 * 
 * @version $Id: 604d7dde909a34a4d409544fd17b8553954f78fe $
 */
public class RTSymbolPicker extends CompositeDialogBox implements SelectionHandler<String>
{
    /**
     * The default list of symbols.
     */
    private static final Object[][] RT_SYMBOLS = {
        {"&amp;",     "\u0026", "&#38;",   true, "ampersand"},
//      {"&quot;",    "\u0022", "&#34;",   true, "quotation mark"},
        {"&quot;",    "\"",     "&#34;",   true, "quotation mark"},
        // finance    
        {"&cent;",    "\u00A2", "&#162;",  true, "cent sign"},
        {"&euro;",    "\u20AC", "&#8364;", true, "euro sign"},
        {"&pound;",   "\u00A3", "&#163;",  true, "pound sign"},
        {"&yen;",     "\u00A5", "&#165;",  true, "yen sign"},
        // signs      
        {"&copy;",    "\u00A9", "&#169;",  true, "copyright sign"},
        {"&reg;",     "\u00AE", "&#174;",  true, "registered sign"},
        {"&trade;",   "\u2122", "&#8482;", true, "trade mark sign"},
        {"&permil;",  "\u2030", "&#8240;", true, "per mille sign"},
        {"&micro;",   "\u00B5", "&#181;",  true, "micro sign"},
        {"&middot;",  "\u00B7", "&#183;",  true, "middle dot"},
        {"&bull;",    "\u2022", "&#8226;", true, "bullet"},
        {"&hellip;",  "\u2026", "&#8230;", true, "three dot leader"},
        {"&prime;",   "\u2032", "&#8242;", true, "minutes / feet"},
        {"&Prime;",   "\u2033", "&#8243;", true, "seconds / inches"},
        {"&sect;",    "\u00A7", "&#167;",  true, "section sign"},
        {"&para;",    "\u00B6", "&#182;",  true, "paragraph sign"},
        {"&szlig;",   "\u00DF", "&#223;",  true, "sharp s / ess-zed"},
        // quotations 
        {"&lsaquo;",  "\u2039", "&#8249;", true, "single left-pointing angle quotation mark"},
        {"&rsaquo;",  "\u203A", "&#8250;", true, "single right-pointing angle quotation mark"},
        {"&laquo;",   "\u00AB", "&#171;",  true, "left pointing guillemet"},
        {"&raquo;",   "\u00BB", "&#187;",  true, "right pointing guillemet"},
        {"&lsquo;",   "\u2018", "&#8216;", true, "left single quotation mark"},
        {"&rsquo;",   "\u2019", "&#8217;", true, "right single quotation mark"},
        {"&ldquo;",   "\u201C", "&#8220;", true, "left double quotation mark"},
        {"&rdquo;",   "\u201D", "&#8221;", true, "right double quotation mark"},
        {"&sbquo;",   "\u201A", "&#8218;", true, "single low-9 quotation mark"},
        {"&bdquo;",   "\u201E", "&#8222;", true, "double low-9 quotation mark"},
        {"&lt;",      "\u003C", "&#60;",   true, "less-than sign"},
        {"&gt;",      "\u003E", "&#62;",   true, "greater-than sign"},
        {"&le;",      "\u2264", "&#8804;", true, "less-than or equal to"},
        {"&ge;",      "\u2265", "&#8805;", true, "greater-than or equal to"},
        {"&ndash;",   "\u2013", "&#8211;", true, "en dash"},
        {"&mdash;",   "\u2014", "&#8212;", true, "em dash"},
        {"&macr;",    "\u00AF", "&#175;",  true, "macron"},
        {"&oline;",   "\u203E", "&#8254;", true, "overline"},
        {"&curren;",  "\u00A4", "&#164;",  true, "currency sign"},
        {"&brvbar;",  "\u00A6", "&#166;",  true, "broken bar"},
        {"&uml;",     "\u00A8", "&#168;",  true, "diaeresis"},
        {"&iexcl;",   "\u00A1", "&#161;",  true, "inverted exclamation mark"},
        {"&iquest;",  "\u00BF", "&#191;",  true, "turned question mark"},
        {"&circ;",    "\u02C6", "&#710;",  true, "circumflex accent"},
        {"&tilde;",   "\u02DC", "&#732;",  true, "small tilde"},
        {"&deg;",     "\u00B0", "&#176;",  true, "degree sign"},
        {"&minus;",   "\u2212", "&#8722;", true, "minus sign"},
        {"&plusmn;",  "\u00B1", "&#177;",  true, "plus-minus sign"},
        {"&divide;",  "\u00F7", "&#247;",  true, "division sign"},
        {"&frasl;",   "\u2044", "&#8260;", true, "fraction slash"},
        {"&times;",   "\u00D7", "&#215;",  true, "multiplication sign"},
        {"&sup1;",    "\u00B9", "&#185;",  true, "superscript one"},
        {"&sup2;",    "\u00B2", "&#178;",  true, "superscript two"},
        {"&sup3;",    "\u00B3", "&#179;",  true, "superscript three"},
        {"&frac14;",  "\u00BC", "&#188;",  true, "fraction one quarter"},
        {"&frac12;",  "\u00BD", "&#189;",  true, "fraction one half"},
        {"&frac34;",  "\u00BE", "&#190;",  true, "fraction three quarters"},
        // math / logical
        {"&fnof;",    "\u0192", "&#402;",  true, "function / florin"},
        {"&int;",     "\u222B", "&#8747;", true, "integral"},
        {"&sum;",     "\u2211", "&#8721;", true, "n-ary sumation"},
        {"&infin;",   "\u221E", "&#8734;", true, "infinity"},
        {"&radic;",   "\u221A", "&#8730;", true, "square root"},
        {"&sim;",     "\u223C", "&#8764;", false, "similar to"},
        {"&cong;",    "\u2245", "&#8773;", false, "approximately equal to"},
        {"&asymp;",   "\u2248", "&#8776;", true, "almost equal to"},
        {"&ne;",      "\u2260", "&#8800;", true, "not equal to"},
        {"&equiv;",   "\u2261", "&#8801;", true, "identical to"},
        {"&isin;",    "\u2208", "&#8712;", false, "element of"},
        {"&notin;",   "\u2209", "&#8713;", false, "not an element of"},
        {"&ni;",      "\u220B", "&#8715;", false, "contains as member"},
        {"&prod;",    "\u220F", "&#8719;", true, "n-ary product"},
        {"&and;",     "\u2227", "&#8743;", false, "logical and"},
        {"&or;",      "\u2228", "&#8744;", false, "logical or"},
        {"&not;",     "\u00AC", "&#172;",  true, "not sign"},
        {"&cap;",     "\u2229", "&#8745;", true, "intersection"},
        {"&cup;",     "\u222A", "&#8746;", false, "union"},
        {"&part;",    "\u2202", "&#8706;", true, "partial differential"},
        {"&forall;",  "\u2200", "&#8704;", false, "for all"},
        {"&exist;",   "\u2203", "&#8707;", false, "there exists"},
        {"&empty;",   "\u2205", "&#8709;", false, "diameter"},
        {"&nabla;",   "\u2207", "&#8711;", false, "backward difference"},
        {"&lowast;",  "\u2217", "&#8727;", false, "asterisk operator"},
        {"&prop;",    "\u221D", "&#8733;", false, "proportional to"},
        {"&ang;",     "\u2220", "&#8736;", false, "angle"},
        // undefined  
        {"&acute;",   "\u00B4", "&#180;",  true, "acute accent"},
        {"&cedil;",   "\u00B8", "&#184;",  true, "cedilla"},
        {"&ordf;",    "\u00AA", "&#170;",  true, "feminine ordinal indicator"},
        {"&ordm;",    "\u00BA", "&#186;",  true, "masculine ordinal indicator"},
        {"&dagger;",  "\u2020", "&#8224;", true, "dagger"},
        {"&Dagger;",  "\u2021", "&#8225;", true, "double dagger"},
        // alphabetical special chars
        {"&Agrave;",  "\u00C0", "&#192;", true, "A - grave"},
        {"&Aacute;",  "\u00C1", "&#193;", true, "A - acute"},
        {"&Acirc;",   "\u00C2", "&#194;", true, "A - circumflex"},
        {"&Atilde;",  "\u00C3", "&#195;", true, "A - tilde"},
        {"&Auml;",    "\u00C4", "&#196;", true, "A - diaeresis"},
        {"&Aring;",   "\u00C5", "&#197;", true, "A - ring above"},
        {"&AElig;",   "\u00C6", "&#198;", true, "ligature AE"},
        {"&Ccedil;",  "\u00C7", "&#199;", true, "C - cedilla"},
        {"&Egrave;",  "\u00C8", "&#200;", true, "E - grave"},
        {"&Eacute;",  "\u00C9", "&#201;", true, "E - acute"},
        {"&Ecirc;",   "\u00CA", "&#202;", true, "E - circumflex"},
        {"&Euml;",    "\u00CB", "&#203;", true, "E - diaeresis"},
        {"&Igrave;",  "\u00CC", "&#204;", true, "I - grave"},
        {"&Iacute;",  "\u00CD", "&#205;", true, "I - acute"},
        {"&Icirc;",   "\u00CE", "&#206;", true, "I - circumflex"},
        {"&Iuml;",    "\u00CF", "&#207;", true, "I - diaeresis"},
        {"&ETH;",     "\u00D0", "&#208;", true, "ETH"},
        {"&Ntilde;",  "\u00D1", "&#209;", true, "N - tilde"},
        {"&Ograve;",  "\u00D2", "&#210;", true, "O - grave"},
        {"&Oacute;",  "\u00D3", "&#211;", true, "O - acute"},
        {"&Ocirc;",   "\u00D4", "&#212;", true, "O - circumflex"},
        {"&Otilde;",  "\u00D5", "&#213;", true, "O - tilde"},
        {"&Ouml;",    "\u00D6", "&#214;", true, "O - diaeresis"},
        {"&Oslash;",  "\u00D8", "&#216;", true, "O - slash"},
        {"&OElig;",   "\u0152", "&#338;", true, "ligature OE"},
        {"&Scaron;",  "\u0160", "&#352;", true, "S - caron"},
        {"&Ugrave;",  "\u00D9", "&#217;", true, "U - grave"},
        {"&Uacute;",  "\u00DA", "&#218;", true, "U - acute"},
        {"&Ucirc;",   "\u00DB", "&#219;", true, "U - circumflex"},
        {"&Uuml;",    "\u00DC", "&#220;", true, "U - diaeresis"},
        {"&Yacute;",  "\u00DD", "&#221;", true, "Y - acute"},
        {"&Yuml;",    "\u0178", "&#376;", true, "Y - diaeresis"},
        {"&THORN;",   "\u00DE", "&#222;", true, "THORN"},
        {"&agrave;",  "\u00E0", "&#224;", true, "a - grave"},
        {"&aacute;",  "\u00E1", "&#225;", true, "a - acute"},
        {"&acirc;",   "\u00E2", "&#226;", true, "a - circumflex"},
        {"&atilde;",  "\u00E3", "&#227;", true, "a - tilde"},
        {"&auml;",    "\u00E4", "&#228;", true, "a - diaeresis"},
        {"&aring;",   "\u00E5", "&#229;", true, "a - ring above"},
        {"&aelig;",   "\u00E6", "&#230;", true, "ligature ae"},
        {"&ccedil;",  "\u00E7", "&#231;", true, "c - cedilla"},
        {"&egrave;",  "\u00E8", "&#232;", true, "e - grave"},
        {"&eacute;",  "\u00E9", "&#233;", true, "e - acute"},
        {"&ecirc;",   "\u00EA", "&#234;", true, "e - circumflex"},
        {"&euml;",    "\u00EB", "&#235;", true, "e - diaeresis"},
        {"&igrave;",  "\u00EC", "&#236;", true, "i - grave"},
        {"&iacute;",  "\u00ED", "&#237;", true, "i - acute"},
        {"&icirc;",   "\u00EE", "&#238;", true, "i - circumflex"},
        {"&iuml;",    "\u00EF", "&#239;", true, "i - diaeresis"},
        {"&eth;",     "\u00F0", "&#240;", true, "eth"},
        {"&ntilde;",  "\u00F1", "&#241;", true, "n - tilde"},
        {"&ograve;",  "\u00F2", "&#242;", true, "o - grave"},
        {"&oacute;",  "\u00F3", "&#243;", true, "o - acute"},
        {"&ocirc;",   "\u00F4", "&#244;", true, "o - circumflex"},
        {"&otilde;",  "\u00F5", "&#245;", true, "o - tilde"},
        {"&ouml;",    "\u00F6", "&#246;", true, "o - diaeresis"},
        {"&oslash;",  "\u00F8", "&#248;", true, "o slash"},
        {"&oelig;",   "\u0153", "&#339;", true, "ligature oe"},
        {"&scaron;",  "\u0161", "&#353;", true, "s - caron"},
        {"&ugrave;",  "\u00F9", "&#249;", true, "u - grave"},
        {"&uacute;",  "\u00FA", "&#250;", true, "u - acute"},
        {"&ucirc;",   "\u00FB", "&#251;", true, "u - circumflex"},
        {"&uuml;",    "\u00FC", "&#252;", true, "u - diaeresis"},
        {"&yacute;",  "\u00FD", "&#253;", true, "y - acute"},
        {"&thorn;",   "\u00FE", "&#254;", true, "thorn"},
        {"&yuml;",    "\u00FF", "&#255;", true, "y - diaeresis"},
        {"&Alpha;",   "\u0391", "&#913;", true, "Alpha"},
        {"&Beta;",    "\u0392", "&#914;", true, "Beta"},
        {"&Gamma;",   "\u0393", "&#915;", true, "Gamma"},
        {"&Delta;",   "\u0394", "&#916;", true, "Delta"},
        {"&Epsilon;", "\u0395", "&#917;", true, "Epsilon"},
        {"&Zeta;",    "\u0396", "&#918;", true, "Zeta"},
        {"&Eta;",     "\u0397", "&#919;", true, "Eta"},
        {"&Theta;",   "\u0398", "&#920;", true, "Theta"},
        {"&Iota;",    "\u0399", "&#921;", true, "Iota"},
        {"&Kappa;",   "\u039A", "&#922;", true, "Kappa"},
        {"&Lambda;",  "\u039B", "&#923;", true, "Lambda"},
        {"&Mu;",      "\u039C", "&#924;", true, "Mu"},
        {"&Nu;",      "\u039D", "&#925;", true, "Nu"},
        {"&Xi;",      "\u039E", "&#926;", true, "Xi"},
        {"&Omicron;", "\u039F", "&#927;", true, "Omicron"},
        {"&Pi;",      "\u03A0", "&#928;", true, "Pi"},
        {"&Rho;",     "\u03A1", "&#929;", true, "Rho"},
        {"&Sigma;",   "\u03A3", "&#931;", true, "Sigma"},
        {"&Tau;",     "\u03A4", "&#932;", true, "Tau"},
        {"&Upsilon;", "\u03A5", "&#933;", true, "Upsilon"},
        {"&Phi;",     "\u03A6", "&#934;", true, "Phi"},
        {"&Chi;",     "\u03A7", "&#935;", true, "Chi"},
        {"&Psi;",     "\u03A8", "&#936;", true, "Psi"},
        {"&Omega;",   "\u03A9", "&#937;", true, "Omega"},
        {"&alpha;",   "\u03B1", "&#945;", true, "alpha"},
        {"&beta;",    "\u03B2", "&#946;", true, "beta"},
        {"&gamma;",   "\u03B3", "&#947;", true, "gamma"},
        {"&delta;",   "\u03B4", "&#948;", true, "delta"},
        {"&epsilon;", "\u03B5", "&#949;", true, "epsilon"},
        {"&zeta;",    "\u03B6", "&#950;", true, "zeta"},
        {"&eta;",     "\u03B7", "&#951;", true, "eta"},
        {"&theta;",   "\u03B8", "&#952;", true, "theta"},
        {"&iota;",    "\u03B9", "&#953;", true, "iota"},
        {"&kappa;",   "\u03BA", "&#954;", true, "kappa"},
        {"&lambda;",  "\u03BB", "&#955;", true, "lambda"},
        {"&mu;",      "\u03BC", "&#956;", true, "mu"},
        {"&nu;",      "\u03BD", "&#957;", true, "nu"},
        {"&xi;",      "\u03BE", "&#958;", true, "xi"},
        {"&omicron;", "\u03BF", "&#959;", true, "omicron"},
        {"&pi;",      "\u03C0", "&#960;", true, "pi"},
        {"&rho;",     "\u03C1", "&#961;", true, "rho"},
        {"&sigmaf;",  "\u03C2", "&#962;", true, "final sigma"},
        {"&sigma;",   "\u03C3", "&#963;", true, "sigma"},
        {"&tau;",     "\u03C4", "&#964;", true, "tau"},
        {"&upsilon;", "\u03C5", "&#965;", true, "upsilon"},
        {"&phi;",     "\u03C6", "&#966;", true, "phi"},
        {"&chi;",     "\u03C7", "&#967;", true, "chi"},
        {"&psi;",     "\u03C8", "&#968;", true, "psi"},
        {"&omega;",   "\u03C9", "&#969;", true, "omega"},
        // symbols
        {"&alefsym;", "\u2135", "&#8501;", false, "alef symbol"},
        {"&piv;",     "\u03D6", "&#982;",  false, "pi symbol"},
        {"&real;",    "\u211C", "&#8476;", false, "real part symbol"},
        {"&thetasym;", "\u03D1", "&#977;", false, "theta symbol"},
        {"&upsih;",   "\u03D2", "&#978;",  false, "upsilon - hook symbol"},
        {"&weierp;",  "\u2118", "&#8472;", false, "Weierstrass p"},
        {"&image;",   "\u2111", "&#8465;", false, "imaginary part"},
        // arrows
        {"&larr;",    "\u2190", "&#8592;", true, "leftwards arrow"},
        {"&uarr;",    "\u2191", "&#8593;", true, "upwards arrow"},
        {"&rarr;",    "\u2192", "&#8594;", true, "rightwards arrow"},
        {"&darr;",    "\u2193", "&#8595;", true, "downwards arrow"},
        {"&harr;",    "\u2194", "&#8596;", true, "left right arrow"},
        {"&crarr;",   "\u21B5", "&#8629;", false, "carriage return"},
        {"&lArr;",    "\u21D0", "&#8656;", false, "leftwards double arrow"},
        {"&uArr;",    "\u21D1", "&#8657;", false, "upwards double arrow"},
        {"&rArr;",    "\u21D2", "&#8658;", false, "rightwards double arrow"},
        {"&dArr;",    "\u21D3", "&#8659;", false, "downwards double arrow"},
        {"&hArr;",    "\u21D4", "&#8660;", false, "left right double arrow"},
        {"&there4;",  "\u2234", "&#8756;", false, "therefore"},
        {"&sub;",     "\u2282", "&#8834;", false, "subset of"},
        {"&sup;",     "\u2283", "&#8835;", false, "superset of"},
        {"&nsub;",    "\u2284", "&#8836;", false, "not a subset of"},
        {"&sube;",    "\u2286", "&#8838;", false, "subset of or equal to"},
        {"&supe;",    "\u2287", "&#8839;", false, "superset of or equal to"},
        {"&oplus;",   "\u2295", "&#8853;", false, "circled plus"},
        {"&otimes;",  "\u2297", "&#8855;", false, "circled times"},
        {"&perp;",    "\u22A5", "&#8869;", false, "perpendicular"},
        {"&sdot;",    "\u22C5", "&#8901;", false, "dot operator"},
        {"&lceil;",   "\u2308", "&#8968;", false, "left ceiling"},
        {"&rceil;",   "\u2309", "&#8969;", false, "right ceiling"},
        {"&lfloor;",  "\u230A", "&#8970;", false, "left floor"},
        {"&rfloor;",  "\u230B", "&#8971;", false, "right floor"},
        {"&lang;",    "\u2329", "&#9001;", false, "left-pointing angle bracket"},
        {"&rang;",    "\u232A", "&#9002;", false, "right-pointing angle bracket"},
        {"&loz;",     "\u25CA", "&#9674;", true, "lozenge"},
        {"&spades;",  "\u2660", "&#9824;", false, "black spade suit"},
        {"&clubs;",   "\u2663", "&#9827;", true, "black club suit"},
        {"&hearts;",  "\u2665", "&#9829;", true, "black heart suit"},
        {"&diams;",   "\u2666", "&#9830;", true, "black diamond suit"},
        {"&ensp;",    "\u2002", "&#8194;", false, "en space"},
        {"&emsp;",    "\u2003", "&#8195;", false, "em space"},
        {"&thinsp;",  "\u2009", "&#8201;", false, "thin space"},
        {"&zwnj;",    "\u200C", "&#8204;", false, "zero width non-joiner"},
        {"&zwj;",     "\u200D", "&#8205;", false, "zero width joiner"},
        {"&lrm;",     "\u200E", "&#8206;", false, "left-to-right mark"},
        {"&rlm;",     "\u200F", "&#8207;", false, "right-to-left mark"},
        {"&shy;",     "\u00AD", "&#173;",  false, "soft hyphen"}
};

    
    /**
     * Column index of the HTML ENTITY representing the character
     */
    static final int CHAR_HTML_ENTITY = 0;
 
    /**
     * Column index of the UNICODE of the character
     */
    static final int CHAR_UNICODE = 1;

    /**
     * Column index of the flag to enable/disable the character
     */
    static final int CHAR_HTML_CODE = 2;

    /**
     * Column index of the flag to enable/disable the character
     */
    static final int CHAR_ENABLED = 3;
    
    /**
     * Column index of the description of the character
     */    
    static final int CHAR_TITLE = 4;
    
    /**
     * The default number of rows in the grid that makes up the symbol palette.
     */
    private static final int SYMBOLS_PER_ROW = 20;

    /**
     * The default number of columns in the grid that makes up the symbol palette.
     */
    private static final int SYMBOLS_PER_COL = 10;

    /**
     * The symbol palette used for picking the symbol.
     */
    private final RTSymbolPalette symbolPalette;

    /**
     * Creates a new symbol picker using the default list of symbols.
     */
    public RTSymbolPicker()
    {
        super(false, true);

        getDialog().setIcon(new Image(Images.INSTANCE.charmap()));
        getDialog().setCaption(Strings.INSTANCE.charmap());
        getDialog().addStyleName("xSymbolPicker");

        symbolPalette = new RTSymbolPalette(RT_SYMBOLS, SYMBOLS_PER_ROW, SYMBOLS_PER_COL);
        symbolPalette.addSelectionHandler(this);

        initWidget(symbolPalette);
    }

    /**
     * {@inheritDoc}
     * 
     * @see SelectionHandler#onSelection(SelectionEvent)
     */
    public void onSelection(SelectionEvent<String> event)
    {
        if (event.getSource() == symbolPalette) {
            hide();
        }
    }

    /**
     * @return the selected symbol
     */
    public String getSymbol()
    {
        return symbolPalette.getSelectedSymbol();
    }
    
    /**
     * {@inheritDoc}
     * 
     * @see CompositeDialogBox#center()
     */
    public void center()
    {
        // Reset the selected symbol each time the symbol picker is shown.
        symbolPalette.setSelectedSymbol(null);

        super.center();
    }
}
