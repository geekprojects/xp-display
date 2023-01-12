/**
* NDFramedElement.java
* 
* Manage framing and flashing elements for ND 
* 
* Copyright (C) 2018,2023 Nicolas Carel
* 
* 
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2 
* of the License, or (at your option) any later version.
*
* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU Lesser General Public
* License along with this library; if not, write to the Free Software
* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
*/

package net.sourceforge.xhsi.flightdeck.nd;

import java.awt.Graphics2D;

import net.sourceforge.xhsi.util.FramedElement;

public class NDFramedElement extends FramedElement {

	private NDGraphicsConfig nd_gc;

	public final static int MAP_FLAG = 10;
	public final static int LOC_FLAG = 11;
	public final static int HDG_FLAG = 12;
	public final static int DME1_FLAG = 13;
	public final static int DME2_FLAG = 14;
	public final static int GS_FLAG  = 15;

	public NDFramedElement(int col, int raw, NDGraphicsConfig nd_gc, FE_Color default_fe_color, FE_Align default_text_align ) {
		super(nd_gc);
		this.col = col;
		this.raw = raw;		
		this.nd_gc = nd_gc;
		text_color = default_fe_color;
		text_align = default_text_align;
	}
    
	public NDFramedElement(int col, int raw, NDGraphicsConfig nd_gc, FE_Color default_fe_color) {
		super(nd_gc);
		this.col = col;
		this.raw = raw;		
		this.nd_gc = nd_gc;
		text_color = default_fe_color;
	}

	public void paint(Graphics2D g2) {    
		super.paint(g2);		 

		if (display_text) {
			switch (text_style) {
			case ONE_LINE 		: draw1Mode(g2, 0, str_line1_left); break;
			case ONE_LINE_LR 	: draw2Mode(g2, 0, str_line1_left, str_line1_right); break;
			case TWO_COLUMNS 	: draw1Mode(g2, 0, str_line1_left); break; // UNUSED
			case TWO_LINES 		: draw1Mode(g2, 0, str_line1_left); draw1Mode(g2, 1, str_line2_left); break;
			case THREE_LINES 	: draw1Mode(g2, 0, str_line1_left); 
			draw1Mode(g2, 1, str_line2_left); 
			draw1Mode(g2, 2, str_line3_left);
			break;
			case TWO_LINES_LR 	: draw1Mode(g2, 0, str_line1_left); draw2Mode(g2, 1, str_line2_left, str_line2_right); break;
			} 
		}
	}
    
    /*
    private void drawFlag(Graphics2D g2,  String mode) {
        int mode_w = pfd_gc.get_text_width(g2, text_font, mode);
        setTextColor(g2);      
        g2.setFont(text_font);
        g2.drawString(mode, text_c  - mode_w/2, text_y1);
    }
    */
    
    private void draw1Mode(Graphics2D g2, int raw, String mode) {
        int mode_w = nd_gc.get_text_width(g2, text_font, mode);
        int mode_x = text_x;  
        
        if ( text_align == FE_Align.CENTER ) {
        	mode_x = text_c  - mode_w/2;
        }
        	
        setTextColor(g2);      
        g2.setFont(text_font);
        g2.drawString(mode, mode_x, text_y[raw]);
    }
    
    private void draw2Mode(Graphics2D g2, int raw, String mode, String value) {
        int mode_w1 = nd_gc.get_text_width(g2, text_font, mode);
        int mode_w2 = nd_gc.get_text_width(g2, text_font, value);
        int mode_w = mode_w1 + mode_w2;
        int mode_x = text_c - mode_w/2;
        int mode_x2 = mode_x + mode_w1;        
        setTextColor(g2);          
        g2.setFont(text_font);
        g2.drawString(mode, mode_x, text_y[raw]);        
        setValueColor(g2);  
        g2.drawString(value, mode_x2, text_y[raw]);
    }
    
    
    protected void updateConfig (Graphics2D g2) {
    	super.updateConfig(g2);
      
    	/*
    	 * Default Frame position
    	 */
    	int mode_w = nd_gc.get_text_width(g2, text_font, str_line1_left);
    	int hdg_message_y;
    	if (nd_gc.boeing_style)
    		hdg_message_y = (nd_gc.rose_y_offset + (nd_gc.frame_size.height  - nd_gc.border_bottom - NDGraphicsConfig.INITIAL_CENTER_BOTTOM)) / 2;
    	else
    		hdg_message_y = nd_gc.range_mode_message_y - nd_gc.line_height_xl * 4;
    	
    	frame_x = nd_gc.map_center_x - mode_w/2 - nd_gc.digit_width_xl / 4;
    	frame_y = hdg_message_y + nd_gc.line_height_xl/2 - 2 - nd_gc.line_height_xl*15/16; 
    	
    	/*
    	 * Default Text position
    	 */
        text_c = nd_gc.map_center_x;
        text_x = nd_gc.map_center_x - mode_w/2;
        text_y[0] = hdg_message_y + nd_gc.line_height_xl/2 + nd_gc.line_height_xl - 2;
        text_y[1] = hdg_message_y + nd_gc.line_height_xl/2 + nd_gc.line_height_xl*2 - 2;
        text_y[2] = hdg_message_y + nd_gc.line_height_xl/2 + nd_gc.line_height_xl*3 - 2;
        
        /*
        switch (col) {
    		case MAP_FLAG:
    			frame_x = pfd_gc.hdg_left;
    			frame_w = pfd_gc.hdg_width;
    			text_c = pfd_gc.adi_cx;
    			text_x = frame_x; 
    			text_y[0] = pfd_gc.hdg_top + pfd_gc.line_height_xxl*5/4;			
    			break; 		   			    			
    		default: 
    			frame_w = pfd_gc.fma_col_1 - pfd_gc.digit_width_xl / 2;
    			text_c += pfd_gc.fma_col_1 / 2 ;
    			break;
        } 
        */   

        // TODO : WARNING : text_style is not part of Graphic Context        
        if (text_style == FE_Style.TWO_LINES || text_style == FE_Style.TWO_LINES_LR ) frame_h += nd_gc.line_height_xl*18/16; 
    }
}
