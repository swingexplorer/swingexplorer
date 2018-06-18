/*
 *   Swing Explorer. Tool for developers exploring Java/Swing-based application internals. 
 * 	 Copyright (C) 2012, Maxim Zakharenkov
 *
 *   This library is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU Lesser General Public
 *   License as published by the Free Software Foundation; either
 *   version 2.1 of the License, or (at your option) any later version.
 *
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *   Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the Free Software
 *   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *   
 */
package org.swingexplorer.additiontrace;

import javax.swing.text.Element;
import javax.swing.text.ParagraphView;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTMLEditorKit.HTMLFactory;

/**
 * This Editor kit is necessary to avoid line wrapping
 * of the text in the JEditorPane which is done by default.
 * Unfortunately the JEditorPane has no any easier mechanism
 * of avoiding line wrapping. Thanks to Stanislav Lapitsky (http://java-sl.com/wrap.html)
 * for the idea how to do it
 * @author  Maxim Zakharenkov
 */
public class NoWrapEditorKit extends HTMLEditorKit {

    ViewFactory defaultFactory=new WrapColumnFactory();
    public ViewFactory getViewFactory() {
        return defaultFactory;
    }
}

class WrapColumnFactory extends HTMLFactory {
	public View create(Element elem) {
	    String kind = elem.getName();
	    if (kind.equals("p")) {
		    return new NoWrapParagraphView(elem);
	    }
	    return super.create(elem);
	}
}

class NoWrapParagraphView extends ParagraphView {
    public NoWrapParagraphView(Element elem) {
        super(elem);
    }

    public void layout(int width, int height) {
        super.layout(Short.MAX_VALUE, height);
    }

    public float getMinimumSpan(int axis) {
        return super.getPreferredSpan(axis);
    }
}
