/*
 * An embeddable textile editor based on Canvas.
 * http://guillaume.bort.fr/textile-editor.html
 *
 * Copyright (c) 2009 Guillaume Bort (http://guillaume.bort.fr) & zenexity (http://www.zenexity.fr)
 * Licensed under the Apache2 license.
 * http://www.apache.org/licenses/LICENSE-2.0.html
 *
 * Date: 2009-08-06
 * Revision: 1
 */

var Textile = {};

/** Theme **/
Textile.Theme = {
    'REMOTE': {
        colors: [
            '#006600', '#0066FF', '#00CC00',
            '#660000', '#CC0000', '#CC00FF',
            '#CC6600', '#FF6600', '#FF66FF']
    },
    
    'PLAIN': {
        background: '#FFFFFF',
        color: '#000000'
    },

    'SELECTION': {
        background: 'rgba(255, 248, 198, .75)'
    },

    'CURSOR' : {
        color: '#000000'   
    },

    'SCROLLBAR': {
        strokeStyle: 'rgba(222, 222, 222, 1)'    
    },

    'HEADING': {
        background: '#622C10',
        color: '#FEDCC5'
    },

    'PARAGRAPH': {
        background: '#100F0B'
    },

    'STRONG': {
        color: '#E9C062'
    },

    'EM': {
        color: '#E15F9F',
        fontStyle: 'italic'
    },

    'CITATION': {
        color: '#8BBBE1',
        fontStyle: 'italic'
    },

    'TODO': {
        color: '#F00',
        underline: true
    },

    'BLOCKQUOTE': {
        color: '#DED4BA',
        fontStyle: 'italic',
        background: '#100F0B'
    },

    'DASH': {
        color: '#DE6112'
    },

    'COPYRIGHT': {
        color: '#DE6112',
        fontStyle: 'italic'
    },

    'REGISTRED': {
        color: '#DE6112',
        fontStyle: 'italic'
    },

    'TRADEMARK': {
        color: '#DE6112',
        fontStyle: 'italic'
    },

    'IMAGE': {
        color: '#E7E51A',
        underline: true
    },

    'BLOCK_START': {
        color: '#92BCFA'
    },

    'ITEM_START': {
        color: '#9C0180'
    },

    'BLOCK_STYLE': {
        color: '#92BCFA',
        underline: true
    },

    'NOTE_MARK': {
        color: '#E900C0',
        underline: true
    },

    'CODE': {
        color: '#7BDE09',
        fontStyle: 'italic',
        background: '#100F0B'
    },

    'LINK': {
        color: '#73AC45'
    },

    'LINK_URL': {
        color: '#D48C6A',
        underline: true
    }

};