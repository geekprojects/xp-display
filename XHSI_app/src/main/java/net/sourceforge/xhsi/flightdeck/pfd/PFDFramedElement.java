/**
* PFDFramedElement.java
* 
* Manage framing and flashing elements for PFD 
* 
* Copyright (C) 2014,2023  Nicolas Carel
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
package net.sourceforge.xhsi.flightdeck.pfd;

import java.awt.Graphics2D;

import net.sourceforge.xhsi.util.FramedElement;

public class PFDFramedElement extends FramedElement {	
	
    private PFDGraphicsConfig pfd_gc;

	public final static int FMA_COL1  = 1;
	public final static int FMA_COL2  = 2;
	public final static int FMA_COL3  = 3;
	public final static int FMA_COL4  = 4;
	public final static int ALT_FLAG  = 5;
	public final static int ATT_FLAG  = 6;
	public final static int HDG_FLAG  = 7;
	public final static int SPD_FLAG  = 8;
	public final static int VS_FLAG   = 9;
	public final static int FD_FLAG   = 10;
	public final static int FPV_FLAG  = 11;
	public final static int RA_FLAG   = 12;
	public final static int LOC_FLAG  = 13;
	public final static int GS_FLAG   = 14;
	public final static int DME_FLAG  = 15;
	public final static int LDG_ALT_FLAG    = 16;
	public final static int NO_VSPEED_FLAG  = 17;
	public final static int SPEED_LIM_FLAG  = 18;
	public final static int SEL_SPEED_FLAG  = 19;
	public final static int PITCH_FLAG = 20;
	public final static int ROLL_FLAG  = 21;
	public final static int AOA_FLAG  = 22;
    
    
	public PFDFramedElement(int col, int raw, PFDGraphicsConfig pfd_gc, FE_Color default_pfe_color, FE_Align default_text_align ) {
		super(pfd_gc);
		this.col = col;
		this.raw = raw;		
		this.pfd_gc = pfd_gc;
		text_color = default_pfe_color;
		text_align = default_text_align;
	}
    
	public PFDFramedElement(int col, int raw, PFDGraphicsConfig pfd_gc, FE_Color default_pfe_color) {
		super(pfd_gc);
		this.col = col;
		this.raw = raw;		
		this.pfd_gc = pfd_gc;
		text_color = default_pfe_color;
	}

	public void paint(Graphics2D g2) {   
		super.paint(g2);

		if (display_text) {
			switch (text_style) {
			case ONE_LINE 		: draw1Mode(g2, 0, str_line1_left); break;
			case ONE_LINE_LR 	: draw2Mode(g2, 0, str_line1_left, str_line1_right); break;
			case TWO_COLUMNS 	: drawFinalMode(g2, 0, str_line1_left); break;
			case TWO_LINES 		: draw1Mode(g2, 0, str_line1_left); draw1Mode(g2, 1, str_line2_left); break;
			case THREE_LINES 	: draw1Mode(g2, 0, str_line1_left); 
			draw1Mode(g2, 1, str_line2_left); 
			draw1Mode(g2, 2, str_line3_left);
			break;
			case TWO_LINES_LR 	: draw1Mode(g2, 0, str_line1_left); draw2Mode(g2, 1, str_line2_left, str_line2_right); break;
			} 
		}
	}	
    
	private void drawVerticalString(Graphics2D g2, String text, int x, int y, int vertical_step) {
		int v_pos=y;
		for (int i = 0; i < text.length(); i++)	{
			g2.drawString(text.substring(i,i+1),x,v_pos);			
			v_pos+=vertical_step;
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
        int mode_w = textWidth(g2, mode);
        int mode_x = pfd_gc.fma_left + digit_width/2;
        setTextColor(g2);      
        g2.setFont(text_font);
        if ( text_align == FE_Align.CENTER ) {
        	mode_x = text_c  - mode_w/2;
        } else {           	
            switch (col) {
        		case 2: mode_x += pfd_gc.fma_col_2;
        			mode_x += pfd_gc.fma_col_2 + (pfd_gc.fma_col_3 - pfd_gc.fma_col_2)/2 - mode_w/2;
        			break;
        		default: 
        			mode_x = text_x;
        			break;
            }
        }
        
        if (text_orientation==FE_Orientation.HORIZONTAL) {
            g2.drawString(mode, mode_x, text_y[raw]);
        } else {
        	// Vertical orientation
        	drawVerticalString(g2, mode, mode_x, text_y[raw], this.line_height);
        }

    }
    
    private void draw2Mode(Graphics2D g2, int raw, String mode, String value) {
        int mode_w1 = pfd_gc.get_text_width(g2, text_font, mode);
        int mode_w2 = pfd_gc.get_text_width(g2, text_font, value);
        int mode_w = mode_w1 + mode_w2;
        int mode_x = text_c - mode_w/2;
        int mode_x2 = mode_x + mode_w1;        
        setTextColor(g2);          
        g2.setFont(text_font);
        g2.drawString(mode, mode_x, text_y[raw]);        
        setValueColor(g2);  
        g2.drawString(value, mode_x2, text_y[raw]);
    }
    
    private void drawFinalMode(Graphics2D g2, int raw, String mode) {
        int mode_w = pfd_gc.get_text_width(g2, text_font, mode);
        int mode_x = pfd_gc.fma_left + pfd_gc.fma_col_1 + (pfd_gc.fma_col_3 - pfd_gc.fma_col_1)/2 - mode_w/2;
        // Erase middle line
        g2.setColor(pfd_gc.background_color);
        g2.drawLine(pfd_gc.fma_left + pfd_gc.fma_col_2, pfd_gc.fma_top, pfd_gc.fma_left + pfd_gc.fma_col_2, pfd_gc.fma_top + pfd_gc.fma_height * 2/3);
        setTextColor(g2);  
        g2.setFont(text_font);
        g2.drawString(mode, mode_x, text_y[raw]);
    }
   
    protected void updateConfig (Graphics2D g2) {
    	super.updateConfig(g2);
        
    	frame_x = pfd_gc.fma_left + pfd_gc.digit_width_xl / 4;
    	frame_y = pfd_gc.fma_top + pfd_gc.fma_height*raw/3 + pfd_gc.line_height_xl - 2 - pfd_gc.line_height_xl*15/16; 

        text_c = pfd_gc.fma_left;
        text_x = pfd_gc.fma_left + pfd_gc.digit_width_xl/2;
        text_y[0] = pfd_gc.fma_top + pfd_gc.fma_height*raw/3 + pfd_gc.line_height_xl - 2;
        text_y[1] = pfd_gc.fma_top + pfd_gc.fma_height*(raw+1)/3 + pfd_gc.line_height_xl - 2;
        text_y[2] = pfd_gc.fma_top + pfd_gc.fma_height*(raw+2)/3 + pfd_gc.line_height_xl - 2;
                
        switch (col) {
    		case FMA_COL1:  
    			frame_x += pfd_gc.fma_col_1;
    			frame_w = (pfd_gc.fma_col_2 - pfd_gc.fma_col_1) - pfd_gc.digit_width_xl / 2;
    			text_c += pfd_gc.fma_col_1 + (pfd_gc.fma_col_2 - pfd_gc.fma_col_1)/2; 
    			text_x += pfd_gc.fma_col_1;
    			break;
    		case FMA_COL2: 
    			frame_x += pfd_gc.fma_col_2; 
    			frame_w = (pfd_gc.fma_col_3 - pfd_gc.fma_col_2) - pfd_gc.digit_width_xl / 2;
    			text_c += pfd_gc.fma_col_2 + (pfd_gc.fma_col_3 - pfd_gc.fma_col_2)/2;
    			text_x += pfd_gc.fma_col_2;    			
    			break;
    		case FMA_COL3: 
    			frame_x += pfd_gc.fma_col_3;
    			frame_w = (pfd_gc.fma_col_4 - pfd_gc.fma_col_3) - pfd_gc.digit_width_xl / 2;
    			text_c += pfd_gc.fma_col_3 + (pfd_gc.fma_col_4 - pfd_gc.fma_col_3)/2;
    			text_x += pfd_gc.fma_col_3;
    			break;
    		case FMA_COL4: 
    			frame_x += pfd_gc.fma_col_4;
    			frame_w = (pfd_gc.fma_width  - pfd_gc.fma_col_4) - pfd_gc.digit_width_xl / 2;
    			text_c += pfd_gc.fma_col_4 + (pfd_gc.fma_width - pfd_gc.fma_col_4)/2;
    			text_x += pfd_gc.fma_col_4; 
    			break;
    		case ALT_FLAG:
    			text_c = pfd_gc.alti_flag_cx;
    			text_x = pfd_gc.alti_flag_x; // Not used
    			text_y[0] = pfd_gc.alti_flag_y;
    			frame_y = pfd_gc.alti_flag_y + line_height*6/10 - frame_h/2;
    			frame_x = text_c - frame_w/2 - digit_width*2/12;
    			break;
    		case HDG_FLAG:
    			frame_x = pfd_gc.adi_cx - digit_width * 2;
    			frame_y = pfd_gc.hdg_top + line_height * 2/16;
    			text_c = pfd_gc.adi_cx;
    			text_x = frame_x; 
    			text_y[0] = pfd_gc.hdg_top + line_height;			
    			break; 		
    		case ATT_FLAG:
    			text_w = digit_width * 3;
    			frame_x = pfd_gc.adi_cx - digit_width * 2;
    	    	frame_y = pfd_gc.adi_att_flag_y - line_height*15/16; 
    	    	frame_w = digit_width * 4;   // 3 characters big font
    			text_c = pfd_gc.adi_cx;
    			text_x = text_c - digit_width; // not used for ATT flag
    			text_y[0] = pfd_gc.adi_att_flag_y;
    			break; 	    						
    		case SPD_FLAG:
    			text_c = pfd_gc.speed_flag_cx;
    			text_x = pfd_gc.speed_flag_cx;
    			text_y[0] = pfd_gc.speed_flag_y;
    			frame_x = text_c - frame_w/2 - digit_width*2/12;
    			frame_y = pfd_gc.alti_flag_y + line_height*6/10 - frame_h/2;
    			break; 	     			    			
    		case VS_FLAG:
    			text_c = pfd_gc.vsi_flag_cx;
    			text_x = pfd_gc.vsi_left+pfd_gc.vsi_width/5;
    			text_y[0] = pfd_gc.vsi_flag_y;
    			text_y[1] = pfd_gc.adi_cy + line_height/2 - 4;
    			text_y[2] = pfd_gc.adi_cy + line_height*3/2 - 4;
    			frame_x = text_c - frame_w/2 - digit_width*2/12;
    			frame_y = pfd_gc.vsi_flag_y - line_height*9/10;
    			break;
    		case FD_FLAG:
    	    	frame_y = pfd_gc.adi_fd_flag_y - line_height * 15/16; 
    			text_c = pfd_gc.adi_fd_flag_x;
    			text_x = text_c - digit_width * 2; // not used
    			text_y[0] = pfd_gc.adi_fd_flag_y;
    			frame_x = text_c - frame_w/2 - digit_width*2/12;
    			break;
    		case FPV_FLAG:
    	    	frame_y = pfd_gc.adi_fd_flag_y - line_height * 15/16; 
    			text_c = pfd_gc.adi_fpv_flag_x;
    			text_x = text_c - pfd_gc.digit_width_xl * 2; // not used
    			text_y[0] = pfd_gc.adi_fd_flag_y;
    			frame_x = text_c - frame_w/2 - digit_width*2/12;
    			break;
    		case RA_FLAG:
    	    	frame_y = pfd_gc.adi_ra_flag_y - line_height * 15/16; 
    			text_c = pfd_gc.adi_ra_flag_x;
    			text_x = text_c - pfd_gc.digit_width_xl * 2; // not used
    			text_y[0] = pfd_gc.adi_ra_flag_y;
    			frame_x = text_c - frame_w/2 - digit_width*2/12;
    			break;
    		case AOA_FLAG:
    	    	frame_y = pfd_gc.adi_aoa_flag_y - line_height * 15/16; 
    			text_c = pfd_gc.adi_aoa_flag_x;
    			text_x = text_c - pfd_gc.digit_width_xl * 2; // not used
    			text_y[0] = pfd_gc.adi_aoa_flag_y;
    			frame_x = text_c - frame_w/2 - digit_width*2/12;
    			break;
    		case LOC_FLAG:
    			text_c = pfd_gc.adi_cx;
    			text_x = frame_x; 
    			text_y[0] = pfd_gc.adi_cy + pfd_gc.adi_size_down + line_height*3/2;
    			frame_x = text_c - frame_w/2 - digit_width*2/12;
    			frame_y = text_y[0] - line_height * 9/10;
    			break;
    		case GS_FLAG:
    			text_c = pfd_gc.adi_cx + pfd_gc.adi_size_right*9/8;
    			text_x = frame_x; 
    			text_y[0] = pfd_gc.alti_flag_y;
    			frame_x = text_c - frame_w/2 - digit_width*2/12;
    			frame_y = pfd_gc.alti_flag_y + line_height*6/10 - frame_h/2;
    			break;
    		case DME_FLAG:
                text_c = pfd_gc.adi_cx - pfd_gc.adi_size_left*6/8;
                text_y[0] = pfd_gc.adi_cy - pfd_gc.adi_size_up - 2*pfd_gc.line_height_m - pfd_gc.line_height_l;
                frame_x = text_c - frame_w/2 - digit_width*2/12;
                frame_y = text_y[0] - line_height * 9/10;
                break;
    		case LDG_ALT_FLAG:
    		case NO_VSPEED_FLAG:
    			// Contains horizontal and vertical text
    		case SPEED_LIM_FLAG:
    		case SEL_SPEED_FLAG:

    		default: 
    			frame_w = pfd_gc.fma_col_1 - pfd_gc.digit_width_xl / 2;
    			text_c += pfd_gc.fma_col_1 / 2 ;
    			break;
        }    

        // TODO : WARNING : text_style is not part of Graphic Context
        if (text_style == FE_Style.TWO_COLUMNS) { frame_w += (pfd_gc.fma_col_3 - pfd_gc.fma_col_2); }
        if (text_style == FE_Style.TWO_LINES || text_style == FE_Style.TWO_LINES_LR ) frame_h += pfd_gc.line_height_xl*18/16; 

    }
    
}
